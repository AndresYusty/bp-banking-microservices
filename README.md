# Sistema de Microservicios Bancarios

Este proyecto implementa un sistema de microservicios bancarios utilizando Java Spring Boot, siguiendo principios de Clean Architecture y buenas prácticas de programación.

## Arquitectura

El sistema está compuesto por dos microservicios:

1. **cliente-service**: Gestiona las entidades Persona y Cliente
2. **cuenta-service**: Gestiona las entidades Cuenta y Movimientos

La comunicación entre microservicios se realiza de forma asíncrona mediante RabbitMQ.

## Tecnologías Utilizadas

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- MySQL 8.0
- RabbitMQ
- Maven
- Docker & Docker Compose
- Lombok
- JUnit 5

## Estructura del Proyecto

```
bp-banking-microservices/
├── cliente-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/banking/clienteservice/
│   │   │   │       ├── domain/          # Capa de dominio
│   │   │   │       ├── application/     # Capa de aplicación
│   │   │   │       └── infrastructure/  # Capa de infraestructura
│   │   │   └── resources/
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
├── cuenta-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/banking/cuentaservice/
│   │   │   │       ├── domain/          # Capa de dominio
│   │   │   │       ├── application/    # Capa de aplicación
│   │   │   │       └── infrastructure/ # Capa de infraestructura
│   │   │   └── resources/
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
├── docker-compose.yml
└── README.md
```

## Requisitos Previos

- Java 17 o superior
- Maven 3.6+
- Docker y Docker Compose
- Postman (para pruebas de API)

## Instalación y Ejecución

### Opción 1: Ejecutar con Docker Compose (Recomendado)

1. Clonar o descargar el proyecto
2. Abrir una terminal en la raíz del proyecto
3. Ejecutar:

```bash
docker-compose up --build
```

Esto iniciará:
- MySQL compartida para ambos microservicios (puerto 3306)
- RabbitMQ (puerto 5672, Management UI en 15672)
- cliente-service (puerto 8081)
- cuenta-service (puerto 8082)

**Nota:** Si prefieres usar tu MySQL Workbench local en lugar del contenedor Docker, puedes:
1. Comentar el servicio `banking-db` en el `docker-compose.yml`
2. Crear la base de datos `banking_db` en tu MySQL local
3. Configurar las variables de entorno o editar `application.yml` para apuntar a `localhost:3306`

### Opción 2: Ejecutar Localmente

#### Configurar Base de Datos

1. Crear una base de datos MySQL compartida en tu MySQL Workbench:
   - `banking_db`
   
   Puedes ejecutar este comando SQL:
   ```sql
   CREATE DATABASE banking_db;
   ```

2. Configurar la conexión en `application.yml` de cada servicio:
   - Ambos servicios (`cliente-service` y `cuenta-service`) apuntan a la misma base de datos `banking_db`
   - Asegúrate de que `DB_HOST`, `DB_PORT`, `DB_USER`, `DB_PASSWORD` apunten a tu MySQL local
   
   Por defecto, los servicios esperan:
   - Host: `localhost`
   - Puerto: `3306`
   - Usuario: `root`
   - Contraseña: (vacía por defecto, ajusta según tu configuración)
   
   **Ventaja:** Al usar una sola base de datos, ambos microservicios pueden compartir las mismas tablas, lo cual es más práctico para este caso de uso donde los servicios están relacionados.

3. Iniciar RabbitMQ (puede usar Docker):
```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management-alpine
```

#### Ejecutar Microservicios

**Cliente Service:**
```bash
cd cliente-service
mvn spring-boot:run
```

**Cuenta Service:**
```bash
cd cuenta-service
mvn spring-boot:run
```

## Despliegue en Producción

La aplicación está desplegada y disponible en:

- **URL Base:** `http://3.151.86.111`
- **Cliente Service:** `http://3.151.86.111:8081`
- **Cuenta Service:** `http://3.151.86.111:8082`

### Colección de Postman

Se incluye una colección de Postman (`Banking_Microservices.postman_collection.json`) configurada para usar los endpoints de producción. Puedes importarla directamente en Postman para probar la API.

## Endpoints de la API

### Cliente Service (Puerto 8081)

**Producción:** `http://3.151.86.111:8081`  
**Local:** `http://localhost:8081`

#### CRUD de Clientes

- `POST /clientes` - Crear cliente
- `GET /clientes` - Obtener todos los clientes
- `GET /clientes/{id}` - Obtener cliente por ID
- `GET /clientes/clienteId/{clienteId}` - Obtener cliente por clienteId
- `PUT /clientes/{id}` - Actualizar cliente completo
- `PATCH /clientes/{id}` - Actualizar cliente parcialmente
- `DELETE /clientes/{id}` - Eliminar cliente

### Cuenta Service (Puerto 8082)

**Producción:** `http://3.151.86.111:8082`  
**Local:** `http://localhost:8082`

#### CRUD de Cuentas

- `POST /cuentas` - Crear cuenta
- `GET /cuentas` - Obtener todas las cuentas
- `GET /cuentas/{id}` - Obtener cuenta por ID
- `GET /cuentas/numero/{numeroCuenta}` - Obtener cuenta por número
- `GET /cuentas/cliente/{clienteId}` - Obtener cuentas por cliente
- `PUT /cuentas/{id}` - Actualizar cuenta completa
- `PATCH /cuentas/{id}` - Actualizar cuenta parcialmente

#### CRUD de Movimientos

- `POST /movimientos` - Crear movimiento (depósito o retiro)
- `GET /movimientos` - Obtener todos los movimientos
- `GET /movimientos/{id}` - Obtener movimiento por ID
- `GET /movimientos/cuenta/{cuentaId}` - Obtener movimientos por cuenta
- `PUT /movimientos/{id}` - Actualizar movimiento completo
- `PATCH /movimientos/{id}` - Actualizar movimiento parcialmente

#### Reportes

- `GET /reportes?cliente={clienteId}&fechaInicio={fechaInicio}&fechaFin={fechaFin}` - Generar estado de cuenta

**Formato de fechas:** ISO 8601 (ejemplo: `2022-02-01T00:00:00`)

## Ejemplos de Uso

> **Nota:** Los ejemplos muestran URLs de producción. Para desarrollo local, reemplaza `3.151.86.111` por `localhost`.

### 1. Crear Cliente

**Producción:**
```bash
POST http://3.151.86.111:8081/clientes
Content-Type: application/json

{
  "clienteId": "CLI001",
  "contrasena": "1234",
  "estado": true,
  "nombre": "Jose Lema",
  "genero": "Masculino",
  "edad": 35,
  "identificacion": "1234567890",
  "direccion": "Otavalo sn y principal",
  "telefono": "098254785"
}
```

**Local:**
```bash
POST http://localhost:8081/clientes
Content-Type: application/json

{
  "clienteId": "CLI001",
  "contrasena": "1234",
  "estado": true,
  "nombre": "Jose Lema",
  "genero": "Masculino",
  "edad": 35,
  "identificacion": "1234567890",
  "direccion": "Otavalo sn y principal",
  "telefono": "098254785"
}
```

### 2. Crear Cuenta

**Producción:**
```bash
POST http://3.151.86.111:8082/cuentas
Content-Type: application/json

{
  "numeroCuenta": "478758",
  "tipoCuenta": "Ahorros",
  "saldoInicial": 2000.00,
  "estado": true,
  "clienteId": "CLI001"
}
```

**Local:**
```bash
POST http://localhost:8082/cuentas
Content-Type: application/json

{
  "numeroCuenta": "478758",
  "tipoCuenta": "Ahorros",
  "saldoInicial": 2000.00,
  "estado": true,
  "clienteId": "CLI001"
}
```

### 3. Realizar Depósito

**Producción:**
```bash
POST http://3.151.86.111:8082/movimientos
Content-Type: application/json

{
  "numeroCuenta": "478758",
  "tipoMovimiento": "DEPOSITO",
  "valor": 600.00
}
```

**Local:**
```bash
POST http://localhost:8082/movimientos
Content-Type: application/json

{
  "numeroCuenta": "478758",
  "tipoMovimiento": "DEPOSITO",
  "valor": 600.00
}
```

### 4. Realizar Retiro

**Producción:**
```bash
POST http://3.151.86.111:8082/movimientos
Content-Type: application/json

{
  "numeroCuenta": "478758",
  "tipoMovimiento": "RETIRO",
  "valor": 575.00
}
```

**Local:**
```bash
POST http://localhost:8082/movimientos
Content-Type: application/json

{
  "numeroCuenta": "478758",
  "tipoMovimiento": "RETIRO",
  "valor": 575.00
}
```

### 5. Generar Estado de Cuenta

**Producción:**
```bash
GET http://3.151.86.111:8082/reportes?cliente=CLI001&fechaInicio=2022-02-01T00:00:00&fechaFin=2022-02-28T23:59:59
```

**Local:**
```bash
GET http://localhost:8082/reportes?cliente=CLI001&fechaInicio=2022-02-01T00:00:00&fechaFin=2022-02-28T23:59:59
```

## Funcionalidades Implementadas

### F1: CRUD Completo
- ✅ CRUD completo para Cliente
- ✅ CRU para Cuenta y Movimiento

### F2: Registro de Movimientos
- ✅ Actualización automática del saldo disponible
- ✅ Registro de todas las transacciones

### F3: Validación de Saldo
- ✅ Validación de saldo disponible antes de retiros
- ✅ Mensaje de error: "Saldo no disponible"

### F4: Reportes
- ✅ Generación de estado de cuenta por rango de fechas y cliente
- ✅ Incluye cuentas asociadas con saldos
- ✅ Detalle de movimientos de las cuentas

### F5: Pruebas Unitarias
- ✅ Prueba unitaria para la entidad Cliente

### F6: Pruebas de Integración
- ✅ Prueba de integración para MovimientoService

### F7: Docker
- ✅ Configuración completa de Docker y Docker Compose

## Pruebas

### Ejecutar Pruebas Unitarias

**Cliente Service:**
```bash
cd cliente-service
mvn test
```

**Cuenta Service:**
```bash
cd cuenta-service
mvn test
```

## Buenas Prácticas Implementadas

1. **Clean Architecture**: Separación en capas (Domain, Application, Infrastructure)
2. **Repository Pattern**: Abstracción del acceso a datos
3. **DTO Pattern**: Separación entre entidades de dominio y objetos de transferencia
4. **Exception Handling**: Manejo centralizado de excepciones
5. **Validación**: Validación de datos de entrada
6. **Comunicación Asíncrona**: RabbitMQ para comunicación entre microservicios
7. **Pruebas**: Unitarias e integración
8. **Docker**: Containerización completa

## Consideraciones de Rendimiento, Escalabilidad y Resiliencia

### Rendimiento
- Uso de índices en bases de datos (claves únicas)
- Lazy loading en relaciones JPA
- Connection pooling configurado por Spring Boot

### Escalabilidad
- Arquitectura de microservicios permite escalado independiente
- Bases de datos separadas por microservicio
- Comunicación asíncrona reduce acoplamiento

### Resiliencia
- Manejo de excepciones en todos los servicios
- Health checks en Docker Compose
- Validaciones de negocio antes de operaciones críticas

## Acceso a RabbitMQ Management

Una vez iniciado el sistema localmente, puede acceder a la interfaz de gestión de RabbitMQ en:
- **Local:** http://localhost:15672
- Usuario: guest
- Contraseña: guest

> **Nota:** El acceso a RabbitMQ Management solo está disponible en el entorno local, no en producción por razones de seguridad.

## Notas Adicionales

- Los servicios se reinician automáticamente en caso de fallo (restart: unless-stopped)
- Las bases de datos persisten datos en volúmenes Docker
- Los logs se pueden ver con: `docker-compose logs -f [nombre-servicio]`

## Autor

Implementado como parte de una prueba técnica, aplicando buenas prácticas de desarrollo de software.

