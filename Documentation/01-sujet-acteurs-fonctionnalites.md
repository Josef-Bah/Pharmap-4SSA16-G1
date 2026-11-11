# 1. Sujet, acteurs et fonctionnalités

## Problématique

En Tunisie, les **pénuries de médicaments** sont fréquentes. Un patient qui cherche un traitement doit appeler ou visiter plusieurs pharmacies, sans savoir laquelle l'a en stock, ni laquelle est de garde. Les pharmacies, de leur côté, n'ont aucun moyen simple de faire savoir qu'elles disposent d'un produit recherché.

**Pharmap** met en relation patients et pharmacies : on voit en temps réel *où* trouver un médicament, on le **réserve** pour le retirer en pharmacie, et on est **alerté** quand un produit en rupture revient en stock.

## Acteurs

| Acteur | Rôle Keycloak | Besoin principal |
|---|---|---|
| Patient | `PATIENT` | Trouver et réserver rapidement un médicament près de lui |
| Pharmacien | `PHARMACIST` | Publier son stock, traiter réservations et ordonnances |
| Administrateur | `ADMIN` | Tenir le catalogue des médicaments et la liste des pharmacies |

## Fonctionnalités (user stories priorisées)

**Must : cœur du MVP**

| Id | En tant que… | je veux… | afin de… |
|---|---|---|---|
| P1 | Patient | rechercher un médicament et voir les pharmacies proches qui l'ont en stock | ne plus faire le tour des pharmacies |
| P2 | Patient | voir les pharmacies de garde à une date | me soigner la nuit ou le week-end |
| P3 | Patient | réserver un médicament pour 2 h | être sûr de le trouver à mon arrivée |
| P4 | Patient | déposer une ordonnance et suivre sa validation | obtenir un médicament soumis à prescription |
| Ph1 | Pharmacien | mettre à jour mon stock | afficher une disponibilité fiable |
| Ph2 | Pharmacien | accepter ou refuser une réservation et confirmer le retrait | gérer mon officine |
| Ph3 | Pharmacien | valider ou rejeter une ordonnance | respecter la réglementation |
| A1 | Admin | gérer le catalogue de médicaments et les pharmacies | garder des données fiables |

**Should : si le temps le permet**

| Id | En tant que… | je veux… | afin de… |
|---|---|---|---|
| P5 | Patient | créer une alerte « retour en stock » | être prévenu automatiquement |
| P6 | Patient | voir les génériques équivalents | trouver une alternative en cas de rupture |
| P7 | Patient / Pharmacien | consulter mes notifications | suivre mes réservations et ordonnances |
| Ph4 | Pharmacien | déclarer mes horaires et mes gardes | informer les patients |

**Could / hors périmètre** : paiement en ligne, livraison à domicile, intégration à la CNAM, lecture automatique (OCR) des ordonnances, avis sur les pharmacies. Ces points sont volontairement exclus pour garder un périmètre réaliste pour 5 étudiants en 6 séances.

## Périmètre réaliste

- 6 microservices, chacun avec 5 à 8 endpoints, soit environ 40 endpoints au total.
- Un front web unique pour les trois rôles.
- Scénario de démonstration de bout en bout : le patient crée une alerte → le pharmacien ajoute du stock → le patient reçoit la notification → il réserve (avec ordonnance si nécessaire) → le pharmacien accepte puis confirme le retrait.

Diagramme : [`Diagrams/cas-utilisation.md`](Diagrams/cas-utilisation.md)
