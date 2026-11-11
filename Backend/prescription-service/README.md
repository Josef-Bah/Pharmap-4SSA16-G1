# prescription-service

**MS4 · Ordonnances** : responsable : **Mohamed Dhia Rached**

| | |
|---|---|
| Port | `8084` |
| Base de données | MySQL (`pharmap_prescription`) : port hôte 3310, conteneur `mysql-prescription` |
| Swagger UI | http://localhost:8084/swagger-ui.html |

## Responsabilités
- Permettre au patient de déposer une ordonnance (médicaments, quantités, posologie).
- Permettre au pharmacien de valider ou rejeter l'ordonnance reçue.
- Indiquer si une ordonnance validée couvre un médicament donné.

## Entités
- **Prescription** : id, patientKeycloakId, pharmacyId, doctorName, issueDate, expiryDate, status (PENDING | VALIDATED | REJECTED | USED), rejectionReason
- **PrescriptionLine** : id, prescriptionId, drugId, quantity, posology

**Choix de la base :** Structure 1-N (ordonnance → lignes) avec intégrité référentielle et historique d'états auditable.

## API REST prévue

| Méthode | URI | Statuts | Description | Accès |
|---|---|---|---|---|
| POST | `/api/prescriptions` | 201, 400, 404 | Déposer une ordonnance | PATIENT |
| GET | `/api/prescriptions/me` | 200 | Mes ordonnances | PATIENT |
| GET | `/api/prescriptions/pharmacy/{pharmacyId}?status=` | 200 | Ordonnances reçues par une pharmacie | PHARMACIST |
| GET | `/api/prescriptions/{id}` | 200, 403, 404 | Détail d'une ordonnance | PATIENT, PHARMACIST |
| PATCH | `/api/prescriptions/{id}/validate` | 200, 404, 409 | Valider (409 si pas en attente) | PHARMACIST |
| PATCH | `/api/prescriptions/{id}/reject` | 200, 404, 409 | Rejeter avec un motif | PHARMACIST |
| GET | `/api/prescriptions/{id}/covers?drugId=` | 200, 404 | L'ordonnance couvre-t-elle ce médicament ? | interne (Reservation) |

## Interactions
- **Appelle (OpenFeign)** : `drug-catalog-service` (vérifier les médicaments d'une ligne d'ordonnance) ; `pharmacy-service` (vérifier la pharmacie destinataire)
- **Appelé par** : `reservation-service`
- **Publie** : `prescription.validated`, `prescription.rejected`
- **Consomme** : aucun événement
- **En cas de panne** : Si Drug-Catalog ou Pharmacy est indisponible : dépôt refusé (503), circuit breaker. La validation par le pharmacien reste possible (aucun appel externe).

## Lancer le service

```bash
# 1. Démarrer la base et le broker (depuis la racine du dépôt)
docker compose up -d mysql-prescription rabbitmq
# 2. Démarrer le service (ou depuis l'IDE)
cd Backend/prescription-service
./mvnw spring-boot:run
```

Le service démarre même sans Eureka ni Config Server (ils sont optionnels en local).
