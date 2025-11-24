# Sistema de Cálculo de Notas Finales

Sistema para calcular notas finales de estudiantes considerando evaluaciones, asistencia y puntos extra.

## Requerimientos

- Java 17
- Maven 3.6+

## Configuración

### SonarQube

El proyecto está configurado para usar SonarQube. Las credenciales están en `sonar-project.properties`.

**Token de SonarQube:**
- Backend: `sqp_d1dc53340da080145b4e71e98fddac3bca97e43a`

### Ejecutar Tests Locales

```bash
cd backend
./mvnw clean test
```

### Ejecutar Análisis de SonarQube

```bash
cd backend
./mvnw clean test jacoco:report sonar:sonar
```

### Ejecutar la Aplicación

```bash
cd backend
./mvnw spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080/api`

## Endpoints

### POST /api/grades/calculate

Calcula la nota final de un estudiante.

**Ejemplo de Request:**
```json
{
  "examsStudents": [
    {"grade": 4.0, "weight": 50.0},
    {"grade": 3.5, "weight": 50.0}
  ],
  "hasReachedMinimumClasses": true,
  "allYearsTeachers": false
}
```

## Estructura del Proyecto

```
backend/
├── src/
│   ├── main/
│   │   └── java/com/backend/backend/
│   │       ├── Grade/          # Módulo principal
│   │       ├── common/         # Utilidades comunes
│   │       └── BackendApplication.java
│   └── test/
│       └── java/com/backend/backend/
│           └── Grade/          # Tests del módulo
```

## Tests

Todos los tests se ejecutan en local usando H2 en memoria. No se requiere base de datos externa.

```bash
./mvnw test
```

