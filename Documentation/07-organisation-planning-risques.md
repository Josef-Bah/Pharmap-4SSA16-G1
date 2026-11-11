# 7. Organisation, planning et risques

## Répartition nominative

| Membre | Microservice | Base | Tâches communes |
|---|---|---|---|
| Manel Ouni | `pharmacy-service` | MySQL | API Gateway |
| Youssef Bahdoudi | `drug-catalog-service` | MySQL | Eureka + Config Server |
| Mohamed Rayen Souayah | `stock-service` | MySQL | Docker / docker-compose |
| Mohamed Dhia Rached | `prescription-service` | MySQL | Keycloak |
| Mehdi Ben Moussa | `reservation-service` | MySQL | Front-end (coordination) |
| **Groupe** | `notification-service` | MongoDB | RabbitMQ, intégration, soutenance |

## Outil de gestion de projet

**GitHub Projects** (tableau Kanban dans le dépôt) : colonnes *Backlog → À faire → En cours → En revue → Terminé*. Une *issue* par user story (étiquettes `ms-pharmacy`, `ms-stock`, `front`, `infra`…), assignée à un membre, liée à une Pull Request.

## Organisation Git

- `main` : version stable, protégée. `develop` : intégration.
- Une branche par tâche : `feature/<service>-<sujet>` (ex. `feature/stock-hold-endpoint`).
- Messages de commit : `type(service): message` (ex. `feat(stock): add hold endpoint`).
- Pull Request relue par un autre membre avant fusion vers `develop`.
- Détails : [CONTRIBUTING.md](../CONTRIBUTING.md).

## Planning

| Séance | Objectif | Livrable |
|---|---|---|
| S1-S3 | Cadrage, sujet, découpage, contrats d'API | Documentation de conception |
| **S4** | **Validation de la conception** | Dépôt, projets créés, présentation |
| S5 | Entités + CRUD de chaque service, API Gateway | Services démarrent, routés par la Gateway |
| S6 | Communication synchrone (OpenFeign + Resilience4j) | Réservation de bout en bout |
| S7 | Communication asynchrone (RabbitMQ + MongoDB) | Alertes et notifications |
| S8 | Sécurité Keycloak | Connexion et rôles |
| S9 | Dockerisation, front-end finalisé | `docker compose up` démarre tout |
| **S10** | **Démo et soutenance** | Application complète |

## Risques et parades

| Risque | Impact | Parade |
|---|---|---|
| Un service dépend d'un autre pas encore prêt | Blocage | Contrats d'API figés dès S4, stubs WireMock, profil `standalone` |
| Conflits Git | Perte de temps | Un dossier par membre, branches courtes, PR relues |
| Sur-réservation (concurrence sur le stock) | Incohérence | Verrou optimiste `@Version` + opération `hold` atomique |
| Complexité de Keycloak / Docker en fin de projet | Retard | Commencer la config Keycloak dès S5 sur un realm d'essai |
| Données de démonstration insuffisantes | Démo pauvre | Script `data.sql` / jeu de données par service dès S5 |
| Membre indisponible | Service non livré | Binôme de relecture par service, documentation du contrat |

## Premiers éléments produits (pour S4)

- ✅ Dépôt avec la structure imposée, `docker-compose.yml`, README, CONTRIBUTING
- ✅ 9 projets Spring Boot créés dans `Backend/` (3 d'infrastructure + 6 microservices), qui compilent
- ✅ Documentation de conception complète et diagrammes
- ✅ Support de présentation dans `Documentation/Presentation/`
- ⬜ Backlog GitHub Projects à créer, un commit par membre dans son service
