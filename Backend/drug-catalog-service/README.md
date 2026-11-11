# drug-catalog-service

**MS2 · Référentiel des médicaments** : responsable : **Youssef Bahdoudi**

| | |
|---|---|
| Port | `8082` |
| Base de données | MySQL (`pharmap_drugcatalog`) : port hôte 3308, conteneur `mysql-drugcatalog` |
| Swagger UI | http://localhost:8082/swagger-ui.html |

## Responsabilités
- Tenir le référentiel des médicaments (nom commercial, DCI, forme, dosage, laboratoire, prix).
- Indiquer si un médicament nécessite une ordonnance.
- Gérer les catégories et les équivalences génériques.

## Entités
- **Drug** : id, brandName, dci, form, dosage, laboratory, requiresPrescription, price, categoryId
- **Category** : id, name
- **Equivalence** : id, drugId, equivalentDrugId

**Choix de la base :** Référentiel structuré avec relations (catégorie, équivalences auto-référencées), recherche et contraintes d'unicité ; données stables.

## API REST prévue

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

## Interactions
- **Appelle (OpenFeign)** : aucun service
- **Appelé par** : `stock-service`, `prescription-service`, `reservation-service`
- **Publie** : aucun événement
- **Consomme** : aucun événement
- **En cas de panne** : Service source : il n'appelle personne. Les appelants mettent en cache le strict minimum (nom, ordonnance requise) et appliquent un fallback si le catalogue est indisponible.

## Lancer le service

```bash
# 1. Démarrer la base et le broker (depuis la racine du dépôt)
docker compose up -d mysql-drugcatalog rabbitmq
# 2. Démarrer le service (ou depuis l'IDE)
cd Backend/drug-catalog-service
./mvnw spring-boot:run
```

Le service démarre même sans Eureka ni Config Server (ils sont optionnels en local).
