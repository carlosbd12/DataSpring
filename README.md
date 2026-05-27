# DataSpring

DataSpring es una aplicación web para visualizar y gestionar datos de consumo energético industrial. El proyecto combina un backend REST con Spring Boot, persistencia en MySQL mediante JPA/Flyway y un frontend React con Vite.

## Funcionalidades

- Inicio de sesión y registro de usuarios.
- Panel principal con KPIs de consumo, CO2 y medidas energéticas.
- Gráficas por hora, tipo de carga, día de la semana, CO2 y estado semanal.
- Carga de datasets CSV desde el panel.
- Gestión de informes diarios, semanales, mensuales y de eficiencia.
- Exportación de informes a PDF.
- Vista de dispositivos registrados.

## Stack

**Backend**

- Java 17
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Spring Security
- Flyway
- MySQL
- OpenPDF
- Gradle

**Frontend**

- React 19
- Vite
- React Router
- Axios
- Recharts

## Estructura

```text
.
├── backend/
│   ├── main/java/org/example/dataspring/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── repository/
│   │   └── service/
│   ├── main/resources/
│   │   ├── application.properties
│   │   ├── data/
│   │   └── db/migration/
│   └── test/
├── frontend/
│   ├── src/
│   │   ├── api/
│   │   ├── components/
│   │   ├── context/
│   │   ├── pages/
│   │   ├── router/
│   │   └── styles/
│   ├── package.json
│   └── vite.config.js
├── build.gradle
├── settings.gradle
└── gradlew
```

## Requisitos

- Java 17 o superior.
- Node.js y npm.
- MySQL en local.

## Configuración

El backend usa la configuración de `backend/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/datasenseweb?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=asdasd123
server.port=8081
app.cors.allowed-origin=http://localhost:5173
```

Antes de arrancar el backend, crea la base de datos:

```sql
CREATE DATABASE datasenseweb;
```

Si tu usuario, contraseña o puerto de MySQL son distintos, actualiza `application.properties`.

Flyway ejecuta automáticamente las migraciones de `backend/main/resources/db/migration/` al arrancar la aplicación. La primera migración crea un usuario inicial:

```text
usuario: admin
password: admin
```

## Ejecución Local

### Backend

Desde la raíz del proyecto:

```bash
./gradlew bootRun
```

El backend queda disponible en:

```text
http://localhost:8081
```

### Frontend

En otra terminal:

```bash
cd frontend
npm install
npm run dev
```

El frontend queda disponible en:

```text
http://localhost:5173
```

Por defecto, el frontend llama a la API en `http://localhost:8081/api`. Para cambiarlo, define `VITE_API_URL`:

```bash
VITE_API_URL=http://localhost:8081/api npm run dev
```

## Scripts y Comandos

Backend:

```bash
./gradlew bootRun      # arranca la API
./gradlew test         # ejecuta tests
./gradlew build        # compila y genera artefactos
```

Frontend:

```bash
npm run dev            # servidor de desarrollo
npm run build          # build de producción
npm run preview        # vista previa del build
```

## Endpoints Principales

La API expone sus rutas bajo `/api`.

### Autenticación

```text
POST /api/auth/login
POST /api/auth/register
PUT  /api/auth/change-password/{userId}
```

### Dashboard

```text
GET  /api/dashboard/summary
GET  /api/dashboard/dataset
GET  /api/dashboard/hourly?date=YYYY-MM-DD
GET  /api/dashboard/load-type?mode=total
GET  /api/dashboard/day-of-week?mode=total
GET  /api/dashboard/co2?mode=total
GET  /api/dashboard/week-status
POST /api/dashboard/upload
```

### Informes

```text
GET  /api/reports
GET  /api/reports/{id}
POST /api/reports/generate/daily?date=YYYY-MM-DD
POST /api/reports/generate/weekly
POST /api/reports/generate/monthly
POST /api/reports/generate/efficiency
GET  /api/reports/{id}/export/pdf
```

### Usuarios

```text
GET    /api/users
GET    /api/users/{id}
POST   /api/users
PUT    /api/users/{id}
PATCH  /api/users/{id}/activate
PATCH  /api/users/{id}/deactivate
DELETE /api/users/{id}
```

## Datos

El proyecto incluye un dataset CSV en:

```text
backend/main/resources/data/steel_industry_data.csv
```

También existen migraciones que crean las tablas de usuarios, dispositivos, tipos de medición y mediciones, e insertan datos iniciales.

## Notas de Desarrollo

- El backend se ejecuta desde la raíz con Gradle; el `build.gradle` ya apunta a las carpetas `backend/main/java`, `backend/main/resources` y `backend/test/java`.
- El frontend protege las rutas principales usando el usuario guardado en `localStorage`.
- La configuración actual de Spring Security permite las rutas `/api/**`, por lo que la autenticación funciona a nivel de aplicación pero no como autorización estricta de API.
- Hay llamadas frontend a `/config` y `/devices`, pero en el backend actual solo aparecen controladores para autenticación, dashboard, informes y usuarios.
