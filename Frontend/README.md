# Front-end Pharmap (Angular)

Application web des trois rôles (patient, pharmacien, administrateur). Elle consomme **uniquement** l'API Gateway (`http://localhost:8080`) et authentifie les utilisateurs via **Keycloak** (`http://localhost:8180`).

Projet créé en séance 4 (squelette vide) ; l'interface est développée à partir de la séance 5.

## Lancer

```bash
cd pharmap-web
npm install
npm start          # http://localhost:4200
```

## Écrans prévus

| Rôle | Écrans |
|---|---|
| Public | Recherche de médicaments, carte des pharmacies, pharmacies de garde |
| Patient | Réservations, ordonnances, alertes, notifications |
| Pharmacien | Gestion du stock, réservations à traiter, ordonnances à valider |
| Admin | Catalogue de médicaments, pharmacies |
