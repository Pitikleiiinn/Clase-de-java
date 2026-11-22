# EventPass - Evaluación Parcial 3 (JVY0101)

Plataforma de venta de entradas para conciertos y eventos universitarios, construida con
microservicios Java (Spring Boot), contenedores Docker, Docker Swarm, pipeline CI/CD en
GitHub Actions y una arquitectura serverless en AWS (API Gateway + SQS + Lambda).

> Este README se completa a lo largo del desarrollo. Ver `docs/` para arquitectura, decisiones
> técnicas, guion del video y pendientes.

## Estructura

```
auth-service/      Autenticación y usuarios (JWT, roles COMPRADOR / STAFF)
eventos-service/   Eventos y aforo disponible
ordenes-service/   Recepción de órdenes y publicación en SQS
tickets-service/   Emisión de códigos de acceso y validación en puerta
lambda-emision/    Función AWS Lambda (FaaS) que procesa la cola SQS
infra/             Infraestructura como código (CloudFormation)
scripts/           Scripts de Swarm, escalado y pruebas end-to-end
docs/              Documentación
```
