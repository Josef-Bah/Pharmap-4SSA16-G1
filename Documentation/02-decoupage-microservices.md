# 2. Découpage en microservices

Le découpage suit les **capacités métier** (contextes bornés) et non les tables : chaque service a une responsabilité claire, sans recouvrement, et possède **ses propres données**.

| # | Microservice | Responsable | Contexte borné | Base | Port |
|---|---|---|---|---|---|
| MS1 | `pharmacy-service` | Manel Ouni | Pharmacies, localisation et gardes | MySQL | 8081 |
| MS2 | `drug-catalog-service` | Youssef Bahdoudi | Référentiel des médicaments | MySQL | 8082 |
| MS3 | `stock-service` | Mohamed Rayen Souayah | Stock par pharmacie | MySQL | 8083 |
| MS4 | `prescription-service` | Mohamed Dhia Rached | Ordonnances | MySQL | 8084 |
| MS5 | `reservation-service` | Mehdi Ben Moussa | Réservations | MySQL | 8085 |
| MS6 | `notification-service` | Groupe (commun) | Alertes et notifications (microservice commun, technologie avancée) | MongoDB | 8086 |

## Responsabilités

### MS1 · `pharmacy-service` (Manel Ouni)
- Gérer les pharmacies (identité, adresse, coordonnées GPS, téléphone, licence).
- Gérer les horaires d'ouverture et le planning des pharmacies de garde.
- Retrouver les pharmacies proches d'une position ou de garde à une date donnée.

### MS2 · `drug-catalog-service` (Youssef Bahdoudi)
- Tenir le référentiel des médicaments (nom commercial, DCI, forme, dosage, laboratoire, prix).
- Indiquer si un médicament nécessite une ordonnance.
- Gérer les catégories et les équivalences génériques.

### MS3 · `stock-service` (Mohamed Rayen Souayah)
- Connaître la quantité disponible de chaque médicament dans chaque pharmacie.
- Bloquer (hold) et libérer (release) des quantités pour les réservations, sans sur-réservation.
- Signaler les ruptures et les retours en stock.

### MS4 · `prescription-service` (Mohamed Dhia Rached)
- Permettre au patient de déposer une ordonnance (médicaments, quantités, posologie).
- Permettre au pharmacien de valider ou rejeter l'ordonnance reçue.
- Indiquer si une ordonnance validée couvre un médicament donné.

### MS5 · `reservation-service` (Mehdi Ben Moussa)
- Créer une réservation après avoir vérifié médicament, ordonnance et stock.
- Gérer son cycle de vie : PENDING → ACCEPTED → PICKED_UP, ou CANCELLED / EXPIRED.
- Faire expirer automatiquement une réservation non retirée (2 h) et en informer Stock.

### MS6 · `notification-service` (Groupe (commun))
- Gérer les abonnements « prévenez-moi quand ce médicament revient en stock ».
- Consommer les événements de tous les services et produire des notifications.
- Conserver l'historique des notifications et des événements reçus.


## Pourquoi ce découpage ?

- **Stock est séparé de Pharmacy** : une pharmacie change rarement (adresse, gardes), alors que son stock change en permanence et exige de la concurrence maîtrisée (transactions, verrou optimiste). Ce sont deux rythmes et deux contraintes différents.
- **Drug-Catalog est séparé de Stock** : le catalogue est un référentiel national commun à toutes les pharmacies ; le stock est propre à chaque officine.
- **Prescription est séparé de Reservation** : une ordonnance a son propre cycle (dépôt, validation, expiration) et peut couvrir plusieurs réservations.
- **Notification est commun et isolé** : il ne fait que *réagir* à des événements. Il peut tomber sans bloquer personne, et c'est là que MongoDB a une vraie justification.

## Répartition

- **MS1 à MS5** : un microservice par étudiant, avec sa propre base MySQL.
- **MS6 `notification-service`** : microservice commun du groupe (technologie avancée : MongoDB + messagerie asynchrone).
- **Travail commun du groupe** : API Gateway, Eureka, Config Server, sécurité Keycloak, communication entre services, dockerisation, front-end.
