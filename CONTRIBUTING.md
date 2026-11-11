# Guide de contribution

## Principe

Un seul dépôt, **un dossier par membre** dans `Backend/`. Chacun ne modifie que son service ; les fichiers communs (`docker-compose.yml`, `README.md`, `Documentation/`, `Backend/api-gateway`…) passent par une Pull Request annoncée au groupe.

## Branches

| Branche | Rôle |
|---|---|
| `main` | Version stable, **protégée** (jamais de push direct) |
| `develop` | Intégration : tout ce qui est validé par PR |
| `feature/<service>-<sujet>` | Une tâche = une branche (ex. `feature/stock-hold-endpoint`) |

## Au quotidien

```bash
git checkout develop && git pull
git checkout -b feature/<service>-<sujet>
# ... coder, tester ...
git add <fichiers> && git commit -m "feat(<service>): <message>"
git push -u origin feature/<service>-<sujet>
# ouvrir une Pull Request vers develop, la faire relire par un autre membre
```

## Messages de commit

`type(service): message court à l'impératif`

Types : `feat`, `fix`, `docs`, `test`, `refactor`, `chore`. Exemples : `feat(stock): add hold endpoint`, `docs(reservation): describe state machine`.

Commits **petits et fréquents** : l'historique Git sert à évaluer la contribution de chacun.

## Travailler quand les autres services ne sont pas prêts

1. S'appuyer sur le **contrat d'API** figé dans [Documentation/05-api-rest.md](Documentation/05-api-rest.md).
2. Utiliser des **stubs** (WireMock ou Feign client de test) pour simuler les services appelés.
3. Les microservices démarrent sans Eureka ni Config Server (optionnels en local).

## Ports et bases

Voir la table des ports dans [Documentation/06-architecture.md](Documentation/06-architecture.md). **Une base par service** : on n'accède jamais à la base d'un autre service.

## Règles

- Ne jamais committer de mot de passe ou de fichier `.env`.
- `./mvnw test` doit passer avant de pousser.
- Si `develop` ne démarre plus à cause de votre code, vous le corrigez en priorité.
