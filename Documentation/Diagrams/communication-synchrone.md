# Appels synchrones (OpenFeign)

```mermaid
flowchart LR
  RS[reservation-service] -->|OpenFeign| DR[drug-catalog-service]
  RS -->|OpenFeign| PR[prescription-service]
  RS -->|OpenFeign| ST[stock-service]
  RS -->|OpenFeign| PH[pharmacy-service]
  ST -->|OpenFeign| PH
  ST -->|OpenFeign| DR
  PR -->|OpenFeign| DR
  PR -->|OpenFeign| PH
```

> Diagramme Mermaid : affiché automatiquement par GitHub. Pour l'exporter en image : https://mermaid.live
