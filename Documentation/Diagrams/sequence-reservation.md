# Séquence : réserver un médicament

```mermaid
sequenceDiagram
  autonumber
  actor Patient
  participant GW as API Gateway
  participant RS as reservation-service
  participant DR as drug-catalog-service
  participant PR as prescription-service
  participant ST as stock-service
  participant MQ as RabbitMQ
  participant NT as notification-service
  Patient->>GW: POST /api/reservations (JWT)
  GW->>RS: route + token validé
  RS->>DR: GET /api/drugs/{id} (OpenFeign)
  DR-->>RS: requiresPrescription = true
  RS->>PR: GET /api/prescriptions/{id}/covers?drugId= (OpenFeign)
  PR-->>RS: true
  RS->>ST: POST /api/stocks/hold (OpenFeign)
  alt stock suffisant
    ST-->>RS: 200 OK
    RS-->>Patient: 201 Created (PENDING)
    RS--)MQ: reservation.created
    MQ--)NT: notifier le pharmacien
  else stock insuffisant
    ST-->>RS: 409 Conflict
    RS-->>Patient: 409 Conflict
  else stock-service en panne (circuit breaker ouvert)
    RS-->>Patient: 503 Service indisponible, réessayez
  end
  Note over RS,ST: Après 2 h sans retrait : reservation.expired
  RS--)MQ: reservation.expired
  MQ--)ST: libérer la quantité bloquée
  MQ--)NT: informer le patient
```

> Diagramme Mermaid : affiché automatiquement par GitHub. Pour l'exporter en image : https://mermaid.live
