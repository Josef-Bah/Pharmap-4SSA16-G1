# notification-service

**MS6 · Alertes et notifications (microservice commun, technologie avancée)** : responsable : **Groupe (commun)**

| | |
|---|---|
| Port | `8086` |
| Base de données | MongoDB (`pharmap_notification`) : conteneur `mongodb`, port 27017 |
| Swagger UI | http://localhost:8086/swagger-ui.html |

## Responsabilités
- Gérer les abonnements « prévenez-moi quand ce médicament revient en stock ».
- Consommer les événements de tous les services et produire des notifications.
- Conserver l'historique des notifications et des événements reçus.

## Entités
- **alerts (document)** : { patientId, drugId, city, radiusKm, active, createdAt }
- **notifications (document)** : { userId, type, title, message, payload: {...}, read, createdAt }
- **events_log (document)** : { source, eventType, payload, receivedAt }

**Choix de la base :** MongoDB (technologie avancée imposée) : le contenu (payload) varie selon le type d'événement, fort volume d'écritures, schéma flexible orienté documents.

## API REST prévue

| Méthode | URI | Statuts | Description | Accès |
|---|---|---|---|---|
| POST | `/api/alerts` | 201, 400 | S'abonner à une alerte de retour en stock | PATIENT |
| GET | `/api/alerts/me` | 200 | Mes alertes | PATIENT |
| DELETE | `/api/alerts/{id}` | 204, 404 | Supprimer une alerte | PATIENT |
| GET | `/api/notifications/me?unread=true` | 200 | Mes notifications | PATIENT, PHARMACIST |
| PATCH | `/api/notifications/{id}/read` | 200, 404 | Marquer comme lue | PATIENT, PHARMACIST |

## Interactions
- **Appelle (OpenFeign)** : aucun service
- **Appelé par** : uniquement via l'API Gateway
- **Publie** : aucun événement
- **Consomme** : `stock.replenished / stock.out`, `reservation.*`, `prescription.validated / rejected`, `pharmacy.duty.changed`
- **En cas de panne** : N'appelle personne en synchrone : il ne bloque jamais les autres. S'il est en panne, les messages restent dans la file RabbitMQ et sont traités à son redémarrage ; aucun événement n'est perdu.

## Lancer le service

```bash
# 1. Démarrer la base et le broker (depuis la racine du dépôt)
docker compose up -d mongodb rabbitmq
# 2. Démarrer le service (ou depuis l'IDE)
cd Backend/notification-service
./mvnw spring-boot:run
```

Le service démarre même sans Eureka ni Config Server (ils sont optionnels en local).
