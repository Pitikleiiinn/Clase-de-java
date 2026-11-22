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

## Docker local

```bash
cp .env.example .env
docker compose -f docker-compose.yml -f docker-compose.local.yml up --build -d
docker compose -f docker-compose.yml -f docker-compose.local.yml ps
```

| Servicio          | URL local              |
|-------------------|------------------------|
| auth-service      | http://localhost:8081  |
| eventos-service   | http://localhost:8082  |
| ordenes-service   | http://localhost:8083  |
| tickets-service   | http://localhost:8084  |
| ElasticMQ (SQS)   | http://localhost:9324  |

Cada servicio expone `GET /actuator/health`. Usuarios de prueba: `comprador@eventpass.cl`
(`Comprador123!`) y `staff@eventpass.cl` (`Staff123!`).

## Docker Swarm (IE7 / IE8)

`docker-compose.yml` es el archivo de definición del stack: 4 microservicios con 2 réplicas cada
uno, PostgreSQL, red overlay, actualización progresiva (`start-first`) con rollback automático y
límites de recursos.

### 1. Crear el clúster (nodo manager)

```bash
./scripts/swarm-init.sh <IP-DEL-MANAGER>
```

El script ejecuta `docker swarm init --advertise-addr <IP>` e imprime los comandos de unión.

### 2. Agregar un nodo worker

En la otra máquina (debe alcanzar el puerto 2377/tcp del manager):

```bash
docker swarm join --token <TOKEN-WORKER> <IP-DEL-MANAGER>:2377
```

El token se vuelve a consultar en el manager con `docker swarm join-token worker`.

### 3. Agregar un nodo manager adicional

```bash
# en el manager actual: obtener el token
docker swarm join-token manager
# en la nueva máquina:
docker swarm join --token <TOKEN-MANAGER> <IP-DEL-MANAGER>:2377
```

Para alta disponibilidad del plano de control usa un número impar de managers (3 o 5).
Puertos que deben estar abiertos entre nodos: `2377/tcp`, `7946/tcp+udp` y `4789/udp`.

### 4. Desplegar los servicios distribuidos

```bash
./scripts/swarm-deploy.sh            # = docker stack deploy -c docker-compose.yml eventpass
./scripts/swarm-status.sh            # nodos, servicios y en qué nodo corre cada réplica
```

### 5. Escalar según la carga

```bash
./scripts/swarm-scale.sh ordenes-service 5   # hacia arriba (preventa abierta)
./scripts/swarm-scale.sh ordenes-service 1   # hacia abajo
```

Equivale a `docker service scale eventpass_ordenes-service=5`. Los servicios son *stateless* (el
estado vive en PostgreSQL y en la cola), por lo que escalan sin cambios de código.
