# stock-service

**MS3 · Stock par pharmacie** : responsable : **Mohamed Rayen Souayah**

| | |
|---|---|
| Port | `8083` |
| Base de données | MySQL (`pharmap_stock`) : port hôte 3309, conteneur `mysql-stock` |
| Swagger UI | http://localhost:8083/swagger-ui.html |

## Responsabilités
- Connaître la quantité disponible de chaque médicament dans chaque pharmacie.
- Bloquer (hold) et libérer (release) des quantités pour les réservations, sans sur-réservation.
- Signaler les ruptures et les retours en stock.

## Entités
- **StockItem** : id, pharmacyId, drugId, quantity, reservedQuantity, version, lastUpdated
- **StockMovement** : id, stockItemId, type (IN | OUT | HOLD | RELEASE), quantity, date

**Choix de la base :** Mises à jour fréquentes et concurrentes : transactions ACID et verrouillage optimiste (@Version) pour ne jamais réserver deux fois la même boîte.

## API REST prévue

| Méthode | URI | Statuts | Description | Accès |
|---|---|---|---|---|
| GET | `/api/stocks?drugId=` | 200 | Pharmacies ayant un médicament en stock | public |
| GET | `/api/stocks/pharmacy/{pharmacyId}` | 200, 404 | Stock d'une pharmacie | PHARMACIST |
| PUT | `/api/stocks/{pharmacyId}/{drugId}` | 200, 400, 404 | Mettre à jour la quantité | PHARMACIST |
| POST | `/api/stocks/hold` | 200, 404, 409 | Bloquer une quantité (409 si insuffisante) | interne (Reservation) |
| POST | `/api/stocks/release` | 200, 404 | Libérer une quantité bloquée | interne (Reservation) |
| GET | `/api/stocks/{id}/movements` | 200, 404 | Historique des mouvements | PHARMACIST |

## Interactions
- **Appelle (OpenFeign)** : `pharmacy-service` (vérifier qu'une pharmacie existe) ; `drug-catalog-service` (vérifier qu'un médicament existe)
- **Appelé par** : `reservation-service`
- **Publie** : `stock.replenished`, `stock.out`
- **Consomme** : `reservation.cancelled`, `reservation.expired`
- **En cas de panne** : Si Pharmacy ou Drug-Catalog est indisponible : 503 + circuit breaker, aucune écriture. Si RabbitMQ est indisponible : l'événement est conservé (outbox simple) et republié au retour.

## Lancer le service

```bash
# 1. Démarrer la base et le broker (depuis la racine du dépôt)
docker compose up -d mysql-stock rabbitmq
# 2. Démarrer le service (ou depuis l'IDE)
cd Backend/stock-service
./mvnw spring-boot:run
```

Le service démarre même sans Eureka ni Config Server (ils sont optionnels en local).
