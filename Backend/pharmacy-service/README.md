# pharmacy-service

**MS1 · Pharmacies, localisation et gardes** : responsable : **Manel Ouni**

| | |
|---|---|
| Port | `8081` |
| Base de données | MySQL (`pharmap_pharmacy`) : port hôte 3307, conteneur `mysql-pharmacy` |
| Swagger UI | http://localhost:8081/swagger-ui.html |

## Responsabilités
- Gérer les pharmacies (identité, adresse, coordonnées GPS, téléphone, licence).
- Gérer les horaires d'ouverture et le planning des pharmacies de garde.
- Retrouver les pharmacies proches d'une position ou de garde à une date donnée.

## Entités
- **Pharmacy** : id, name, address, city, latitude, longitude, phone, licenseNumber, pharmacistKeycloakId
- **OpeningHours** : id, pharmacyId, dayOfWeek, openTime, closeTime
- **DutySchedule** : id, pharmacyId, startDate, endDate

**Choix de la base :** Données relationnelles (pharmacie → horaires → gardes) avec intégrité référentielle ; requêtes par ville, date et zone géographique.

## API REST prévue

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

## Interactions
- **Appelle (OpenFeign)** : aucun service
- **Appelé par** : `stock-service`, `prescription-service`, `reservation-service`
- **Publie** : `pharmacy.duty.changed`
- **Consomme** : aucun événement
- **En cas de panne** : Service source : il n'appelle personne, donc aucune dépendance en cas de panne. S'il est indisponible, les appelants utilisent un fallback (voir leurs sections).

## Lancer le service

```bash
# 1. Démarrer la base et le broker (depuis la racine du dépôt)
docker compose up -d mysql-pharmacy rabbitmq
# 2. Démarrer le service (ou depuis l'IDE)
cd Backend/pharmacy-service
./mvnw spring-boot:run
```

Le service démarre même sans Eureka ni Config Server (ils sont optionnels en local).
