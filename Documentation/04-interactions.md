# 4. Interactions entre services

## Règle de choix du mode de communication

| Mode | Quand ? | Exemple |
|---|---|---|
| **Synchrone** (OpenFeign) | L'appelant a **besoin de la réponse immédiatement** pour continuer | Reservation doit savoir si le stock est suffisant avant de répondre au patient |
| **Asynchrone** (RabbitMQ) | Le résultat n'est pas nécessaire tout de suite ; on veut **découpler** et ne bloquer personne | Prévenir les abonnés d'un retour en stock |

## Appels synchrones (OpenFeign)

| Appelant | Appelé | Pourquoi | Si l'appelé est indisponible |
|---|---|---|---|
| `stock-service` | `pharmacy-service` | Vérifier qu'une pharmacie existe | La mise à jour est refusée (503) : on n'écrit pas de stock pour une pharmacie non vérifiée. |
| `stock-service` | `drug-catalog-service` | Vérifier qu'un médicament existe | Même comportement : 503 + message clair (circuit breaker Resilience4j). |
| `prescription-service` | `drug-catalog-service` | Vérifier les médicaments d'une ligne d'ordonnance | Dépôt refusé (503) : on n'enregistre pas une ordonnance non vérifiable. |
| `prescription-service` | `pharmacy-service` | Vérifier la pharmacie destinataire | Dépôt refusé (503) avec message clair. |
| `reservation-service` | `drug-catalog-service` | Savoir si le médicament exige une ordonnance | Réservation refusée (503) : on ne devine pas la règle. |
| `reservation-service` | `prescription-service` | Vérifier qu'une ordonnance validée couvre le médicament | Réservation refusée (503) pour les médicaments sous ordonnance ; libre sinon. |
| `reservation-service` | `stock-service` | Bloquer la quantité (hold) | Circuit breaker : 503 « Service indisponible, réessayez ». Aucune réservation créée sans stock confirmé. |
| `reservation-service` | `pharmacy-service` | Vérifier la pharmacie | Réservation refusée (503). |

Chaque appel est protégé par un **circuit breaker Resilience4j** avec un **fallback** (timeout court, quelques essais, puis réponse d'erreur propre 503).

Diagramme : [`Diagrams/communication-synchrone.md`](Diagrams/communication-synchrone.md)

## Événements asynchrones (RabbitMQ)

Exchange `pharmap.events` de type **topic** ; chaque consommateur a sa propre file.

| Service | Sens | Routing key | Signification |
|---|---|---|---|
| `pharmacy-service` | publie | `pharmacy.duty.changed` | Le planning de garde d'une pharmacie a changé |
| `stock-service` | publie | `stock.replenished` | Un médicament est de nouveau disponible dans une pharmacie |
| `stock-service` | publie | `stock.out` | Un médicament est en rupture dans une pharmacie |
| `stock-service` | consomme | `reservation.cancelled` | Libérer la quantité bloquée |
| `stock-service` | consomme | `reservation.expired` | Libérer la quantité bloquée (réservation non retirée) |
| `prescription-service` | publie | `prescription.validated` | Une ordonnance a été validée |
| `prescription-service` | publie | `prescription.rejected` | Une ordonnance a été rejetée |
| `reservation-service` | publie | `reservation.created` | Réservation créée |
| `reservation-service` | publie | `reservation.accepted` | Réservation acceptée par le pharmacien |
| `reservation-service` | publie | `reservation.cancelled` | Réservation annulée |
| `reservation-service` | publie | `reservation.expired` | Réservation expirée (non retirée à temps) |
| `notification-service` | consomme | `stock.replenished / stock.out` | Retour en stock → alerter les abonnés |
| `notification-service` | consomme | `reservation.*` | Informer patient et pharmacien de l'état d'une réservation |
| `notification-service` | consomme | `prescription.validated / rejected` | Informer le patient du résultat |
| `notification-service` | consomme | `pharmacy.duty.changed` | Informer les abonnés d'un changement de garde |

Diagramme : [`Diagrams/communication-asynchrone.md`](Diagrams/communication-asynchrone.md)

## Interactions détaillées par service

### `pharmacy-service`
- **Appelé par** : `stock-service`, `prescription-service`, `reservation-service`
- **Appelle** : aucun service
- **En cas de panne** : Service source : il n'appelle personne, donc aucune dépendance en cas de panne. S'il est indisponible, les appelants utilisent un fallback (voir leurs sections).

### `drug-catalog-service`
- **Appelé par** : `stock-service`, `prescription-service`, `reservation-service`
- **Appelle** : aucun service
- **En cas de panne** : Service source : il n'appelle personne. Les appelants mettent en cache le strict minimum (nom, ordonnance requise) et appliquent un fallback si le catalogue est indisponible.

### `stock-service`
- **Appelé par** : `reservation-service`
- **Appelle** : `pharmacy-service`, `drug-catalog-service`
- **En cas de panne** : Si Pharmacy ou Drug-Catalog est indisponible : 503 + circuit breaker, aucune écriture. Si RabbitMQ est indisponible : l'événement est conservé (outbox simple) et republié au retour.

### `prescription-service`
- **Appelé par** : `reservation-service`
- **Appelle** : `drug-catalog-service`, `pharmacy-service`
- **En cas de panne** : Si Drug-Catalog ou Pharmacy est indisponible : dépôt refusé (503), circuit breaker. La validation par le pharmacien reste possible (aucun appel externe).

### `reservation-service`
- **Appelé par** : — (point d'entrée via la Gateway uniquement)
- **Appelle** : `drug-catalog-service`, `prescription-service`, `stock-service`, `pharmacy-service`
- **En cas de panne** : C'est le service qui orchestre le plus : chaque appel synchrone est protégé par un circuit breaker + fallback. En cas de panne, on refuse proprement (503) plutôt que de créer une réservation incohérente.

### `notification-service`
- **Appelé par** : — (point d'entrée via la Gateway uniquement)
- **Appelle** : aucun service
- **En cas de panne** : N'appelle personne en synchrone : il ne bloque jamais les autres. S'il est en panne, les messages restent dans la file RabbitMQ et sont traités à son redémarrage ; aucun événement n'est perdu.


## Scénario de bout en bout

Voir [`Diagrams/sequence-reservation.md`](Diagrams/sequence-reservation.md).
