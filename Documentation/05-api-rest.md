# 5. API REST : principaux endpoints

Tous les appels passent par l'**API Gateway** (`http://localhost:8080`). Chaque service documente aussi son API avec **Swagger UI** (`/swagger-ui.html` sur son port).

Conventions : JSON, verbes HTTP standards, codes de statut explicites (`201` + en-tête `Location` à la création, `400` validation, `404` introuvable, `409` conflit d'état, `503` service dépendant indisponible), erreurs au format `{ "timestamp", "status", "error", "message", "path" }`.

## `pharmacy-service` (:8081)

| Méthode | URI | Statuts | Description | Accès |
|---|---|---|---|---|
| GET | `/api/pharmacies?city=` | 200 | Lister / filtrer les pharmacies | public |
| GET | `/api/pharmacies/{id}` | 200, 404 | Détail d'une pharmacie | public |
| GET | `/api/pharmacies/nearby?lat=&lng=&radius=` | 200, 400 | Pharmacies proches d'une position | public |
| GET | `/api/pharmacies/on-duty?date=` | 200 | Pharmacies de garde à une date | public |
| POST | `/api/pharmacies` | 201, 400, 409 | Créer une pharmacie | ADMIN |
| PUT | `/api/pharmacies/{id}` | 200, 400, 404 | Modifier une pharmacie | ADMIN, PHARMACIST |
| DELETE | `/api/pharmacies/{id}` | 204, 404 | Supprimer une pharmacie | ADMIN |
| POST | `/api/pharmacies/{id}/duties` | 201, 400, 404 | Ajouter une période de garde | ADMIN, PHARMACIST |

## `drug-catalog-service` (:8082)

| Méthode | URI | Statuts | Description | Accès |
|---|---|---|---|---|
| GET | `/api/drugs?search=&category=` | 200 | Rechercher des médicaments | public |
| GET | `/api/drugs/{id}` | 200, 404 | Détail d'un médicament | public |
| GET | `/api/drugs/{id}/equivalents` | 200, 404 | Génériques équivalents | public |
| GET | `/api/categories` | 200 | Lister les catégories | public |
| POST | `/api/drugs` | 201, 400, 409 | Ajouter un médicament | ADMIN |
| PUT | `/api/drugs/{id}` | 200, 400, 404 | Modifier un médicament | ADMIN |
| DELETE | `/api/drugs/{id}` | 204, 404 | Supprimer un médicament | ADMIN |
| POST | `/api/drugs/{id}/equivalents` | 201, 404, 409 | Déclarer une équivalence | ADMIN |

## `stock-service` (:8083)

| Méthode | URI | Statuts | Description | Accès |
|---|---|---|---|---|
| GET | `/api/stocks?drugId=` | 200 | Pharmacies ayant un médicament en stock | public |
| GET | `/api/stocks/pharmacy/{pharmacyId}` | 200, 404 | Stock d'une pharmacie | PHARMACIST |
| PUT | `/api/stocks/{pharmacyId}/{drugId}` | 200, 400, 404 | Mettre à jour la quantité | PHARMACIST |
| POST | `/api/stocks/hold` | 200, 404, 409 | Bloquer une quantité (409 si insuffisante) | interne (Reservation) |
| POST | `/api/stocks/release` | 200, 404 | Libérer une quantité bloquée | interne (Reservation) |
| GET | `/api/stocks/{id}/movements` | 200, 404 | Historique des mouvements | PHARMACIST |

## `prescription-service` (:8084)

| Méthode | URI | Statuts | Description | Accès |
|---|---|---|---|---|
| POST | `/api/prescriptions` | 201, 400, 404 | Déposer une ordonnance | PATIENT |
| GET | `/api/prescriptions/me` | 200 | Mes ordonnances | PATIENT |
| GET | `/api/prescriptions/pharmacy/{pharmacyId}?status=` | 200 | Ordonnances reçues par une pharmacie | PHARMACIST |
| GET | `/api/prescriptions/{id}` | 200, 403, 404 | Détail d'une ordonnance | PATIENT, PHARMACIST |
| PATCH | `/api/prescriptions/{id}/validate` | 200, 404, 409 | Valider (409 si pas en attente) | PHARMACIST |
| PATCH | `/api/prescriptions/{id}/reject` | 200, 404, 409 | Rejeter avec un motif | PHARMACIST |
| GET | `/api/prescriptions/{id}/covers?drugId=` | 200, 404 | L'ordonnance couvre-t-elle ce médicament ? | interne (Reservation) |

## `reservation-service` (:8085)

| Méthode | URI | Statuts | Description | Accès |
|---|---|---|---|---|
| POST | `/api/reservations` | 201, 400, 403, 409, 503 | Créer une réservation | PATIENT |
| GET | `/api/reservations/me` | 200 | Mes réservations | PATIENT |
| GET | `/api/reservations/pharmacy/{pharmacyId}?status=` | 200 | Réservations d'une pharmacie | PHARMACIST |
| PATCH | `/api/reservations/{id}/accept` | 200, 404, 409 | Accepter une réservation | PHARMACIST |
| PATCH | `/api/reservations/{id}/pickup` | 200, 404, 409 | Confirmer le retrait | PHARMACIST |
| PATCH | `/api/reservations/{id}/cancel` | 200, 404, 409 | Annuler | PATIENT, PHARMACIST |

## `notification-service` (:8086)

| Méthode | URI | Statuts | Description | Accès |
|---|---|---|---|---|
| POST | `/api/alerts` | 201, 400 | S'abonner à une alerte de retour en stock | PATIENT |
| GET | `/api/alerts/me` | 200 | Mes alertes | PATIENT |
| DELETE | `/api/alerts/{id}` | 204, 404 | Supprimer une alerte | PATIENT |
| GET | `/api/notifications/me?unread=true` | 200 | Mes notifications | PATIENT, PHARMACIST |
| PATCH | `/api/notifications/{id}/read` | 200, 404 | Marquer comme lue | PATIENT, PHARMACIST |

