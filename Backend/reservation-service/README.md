# reservation-service

**MS5 · Réservations** : responsable : **Mehdi Ben Moussa**

| | |
|---|---|
| Port | `8085` |
| Base de données | MySQL (`pharmap_reservation`) : port hôte 3311, conteneur `mysql-reservation` |
| Swagger UI | http://localhost:8085/swagger-ui.html |

## Responsabilités
- Créer une réservation après avoir vérifié médicament, ordonnance et stock.
- Gérer son cycle de vie : PENDING → ACCEPTED → PICKED_UP, ou CANCELLED / EXPIRED.
- Faire expirer automatiquement une réservation non retirée (2 h) et en informer Stock.

## Entités
- **Reservation** : id, patientKeycloakId, pharmacyId, drugId, quantity, prescriptionId (nullable), status (PENDING | ACCEPTED | PICKED_UP | CANCELLED | EXPIRED), createdAt, expiresAt

**Choix de la base :** Machine à états avec transactions et contraintes ; requêtes par patient, pharmacie et date d'expiration (job d'expiration).

## API REST prévue

| Méthode | URI | Statuts | Description | Accès |
|---|---|---|---|---|
| POST | `/api/reservations` | 201, 400, 403, 409, 503 | Créer une réservation | PATIENT |
| GET | `/api/reservations/me` | 200 | Mes réservations | PATIENT |
| GET | `/api/reservations/pharmacy/{pharmacyId}?status=` | 200 | Réservations d'une pharmacie | PHARMACIST |
| PATCH | `/api/reservations/{id}/accept` | 200, 404, 409 | Accepter une réservation | PHARMACIST |
| PATCH | `/api/reservations/{id}/pickup` | 200, 404, 409 | Confirmer le retrait | PHARMACIST |
| PATCH | `/api/reservations/{id}/cancel` | 200, 404, 409 | Annuler | PATIENT, PHARMACIST |

## Interactions
- **Appelle (OpenFeign)** : `drug-catalog-service` (savoir si le médicament exige une ordonnance) ; `prescription-service` (vérifier qu'une ordonnance validée couvre le médicament) ; `stock-service` (bloquer la quantité (hold)) ; `pharmacy-service` (vérifier la pharmacie)
- **Appelé par** : uniquement via l'API Gateway
- **Publie** : `reservation.created`, `reservation.accepted`, `reservation.cancelled`, `reservation.expired`
- **Consomme** : aucun événement
- **En cas de panne** : C'est le service qui orchestre le plus : chaque appel synchrone est protégé par un circuit breaker + fallback. En cas de panne, on refuse proprement (503) plutôt que de créer une réservation incohérente.

## Lancer le service

```bash
# 1. Démarrer la base et le broker (depuis la racine du dépôt)
docker compose up -d mysql-reservation rabbitmq
# 2. Démarrer le service (ou depuis l'IDE)
cd Backend/reservation-service
./mvnw spring-boot:run
```

Le service démarre même sans Eureka ni Config Server (ils sont optionnels en local).
