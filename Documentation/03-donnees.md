# 3. Données de chaque service

**Règle :** une base de données **par microservice**, aucune base partagée. Un service n'accède **jamais** à la base d'un autre : il passe par son API (synchrone) ou par un événement (asynchrone). Les références entre services se font par **identifiant** (`pharmacyId`, `drugId`, …), sans clé étrangère inter-bases.

| Service | Base | Port hôte | Nom de la base |
|---|---|---|---|
| `pharmacy-service` | MySQL | 3307 | `pharmap_pharmacy` |
| `drug-catalog-service` | MySQL | 3308 | `pharmap_drugcatalog` |
| `stock-service` | MySQL | 3309 | `pharmap_stock` |
| `prescription-service` | MySQL | 3310 | `pharmap_prescription` |
| `reservation-service` | MySQL | 3311 | `pharmap_reservation` |
| `notification-service` | MongoDB | 27017 | `pharmap_notification` |

## `pharmacy-service` · MySQL

**Entités**

| Entité | Champs |
|---|---|
| `Pharmacy` | id, name, address, city, latitude, longitude, phone, licenseNumber, pharmacistKeycloakId |
| `OpeningHours` | id, pharmacyId, dayOfWeek, openTime, closeTime |
| `DutySchedule` | id, pharmacyId, startDate, endDate |

**Choix de la base :** Données relationnelles (pharmacie → horaires → gardes) avec intégrité référentielle ; requêtes par ville, date et zone géographique.

## `drug-catalog-service` · MySQL

**Entités**

| Entité | Champs |
|---|---|
| `Drug` | id, brandName, dci, form, dosage, laboratory, requiresPrescription, price, categoryId |
| `Category` | id, name |
| `Equivalence` | id, drugId, equivalentDrugId |

**Choix de la base :** Référentiel structuré avec relations (catégorie, équivalences auto-référencées), recherche et contraintes d'unicité ; données stables.

## `stock-service` · MySQL

**Entités**

| Entité | Champs |
|---|---|
| `StockItem` | id, pharmacyId, drugId, quantity, reservedQuantity, version, lastUpdated |
| `StockMovement` | id, stockItemId, type (IN | OUT | HOLD | RELEASE), quantity, date |

**Choix de la base :** Mises à jour fréquentes et concurrentes : transactions ACID et verrouillage optimiste (@Version) pour ne jamais réserver deux fois la même boîte.

## `prescription-service` · MySQL

**Entités**

| Entité | Champs |
|---|---|
| `Prescription` | id, patientKeycloakId, pharmacyId, doctorName, issueDate, expiryDate, status (PENDING | VALIDATED | REJECTED | USED), rejectionReason |
| `PrescriptionLine` | id, prescriptionId, drugId, quantity, posology |

**Choix de la base :** Structure 1-N (ordonnance → lignes) avec intégrité référentielle et historique d'états auditable.

## `reservation-service` · MySQL

**Entités**

| Entité | Champs |
|---|---|
| `Reservation` | id, patientKeycloakId, pharmacyId, drugId, quantity, prescriptionId (nullable), status (PENDING | ACCEPTED | PICKED_UP | CANCELLED | EXPIRED), createdAt, expiresAt |

**Choix de la base :** Machine à états avec transactions et contraintes ; requêtes par patient, pharmacie et date d'expiration (job d'expiration).

## `notification-service` · MongoDB

**Entités**

| Entité | Champs |
|---|---|
| `alerts (document)` | { patientId, drugId, city, radiusKm, active, createdAt } |
| `notifications (document)` | { userId, type, title, message, payload: {...}, read, createdAt } |
| `events_log (document)` | { source, eventType, payload, receivedAt } |

**Choix de la base :** MongoDB (technologie avancée imposée) : le contenu (payload) varie selon le type d'événement, fort volume d'écritures, schéma flexible orienté documents.


Diagramme : [`Diagrams/modele-donnees.md`](Diagrams/modele-donnees.md)

## Cohérence des données entre services

| Besoin | Solution |
|---|---|
| Savoir si une pharmacie ou un médicament existe | Appel synchrone (OpenFeign) vers le service propriétaire |
| Libérer le stock quand une réservation expire | Événement asynchrone `reservation.expired` consommé par Stock |
| Informer un patient | Événement asynchrone consommé par Notification |
| Éviter la double réservation | Verrou optimiste (`@Version`) dans Stock + appel `hold` atomique |
