# Módulo de Evaluación de Notas

## Resumen de Implementación

Este módulo implementa un sistema completo de cálculo de notas finales según los requerimientos funcionales (RF) y no funcionales (RNF) especificados.

## Requerimientos Implementados

### Requerimientos Funcionales (RF)

- ✅ **RF01**: Registrar evaluaciones (nota + peso %). Variable: `examsStudents`
- ✅ **RF02**: Registrar si cumplió asistencia mínima. Variable: `hasReachedMinimumClasses`
- ✅ **RF03**: Registrar acuerdo de docentes para puntos extra. Variable: `allYearsTeachers` (True/False)
- ✅ **RF04**: Solicitar cálculo de nota final (considerando evaluaciones, asistencia, puntos extra)
- ✅ **RF05**: Visualizar detalle: promedio ponderado, penalización por inasistencias, puntos extra

### Requerimientos No Funcionales (RNF)

- ✅ **RNF01**: Máximo 10 evaluaciones por estudiante
- ✅ **RNF02**: Soportar 50 usuarios concurrentes (Simulado en tests)
- ✅ **RNF03**: Cálculo determinista (mismos datos = misma nota)
- ✅ **RNF04**: Tiempo de cálculo < 300 ms

## Arquitectura

### Clases de Dominio

1. **Evaluation**: Representa una evaluación con nota y peso
2. **AttendancePolicy**: Política de asistencia (aplica penalización del 10% si no cumple)
3. **ExtraPointsPolicy**: Política de puntos extra (aplica 0.5 puntos si hay acuerdo de docentes)
4. **GradeCalculator**: Calculadora determinista de notas finales
5. **GradeCalculationResult**: Resultado del cálculo con todos los detalles

### Servicios

- **GradeService**: Servicio principal que orquesta el cálculo y valida los RNF

### Controladores

- **GradeController**: Endpoint REST para calcular notas finales

### DTOs

- **EvaluationDTO**: DTO para evaluaciones
- **CalculateGradeRequestDTO**: DTO de solicitud con todas las variables (RF01-RF03)
- **GradeResultDTO**: DTO de respuesta con el detalle completo (RF05)

## Endpoint API

### POST /api/grades/calculate

Calcula la nota final de un estudiante.

**Request Body:**
```json
{
  "examsStudents": [
    {
      "grade": 4.0,
      "weight": 50.0
    },
    {
      "grade": 3.5,
      "weight": 50.0
    }
  ],
  "hasReachedMinimumClasses": true,
  "allYearsTeachers": false
}
```

**Response:**
```json
{
  "success": true,
  "message": "Cálculo realizado exitosamente",
  "data": {
    "weightedAverage": 3.75,
    "attendancePenalty": 0.0,
    "gradeAfterAttendance": 3.75,
    "extraPoints": 0.0,
    "finalGrade": 3.75,
    "calculationTimeMs": 5,
    "message": "Promedio ponderado: 3.75. Asistencia mínima cumplida: sin penalización. Nota final: 3.75"
  }
}
```

## Pruebas

### Ejecutar Tests Unitarios

```bash
cd backend
./mvnw test -Dtest=Grade*Test
```

### Tests Implementados

1. **EvaluationTest**: Valida la clase Evaluation
2. **AttendancePolicyTest**: Prueba la política de asistencia
3. **ExtraPointsPolicyTest**: Prueba la política de puntos extra
4. **GradeCalculatorTest**: Prueba el cálculo determinista
5. **GradeServiceTest**: Prueba el servicio completo
6. **ConcurrentUsersTest**: Simula 50 usuarios concurrentes (RNF02)
7. **GradeControllerTest**: Prueba el endpoint REST

### Casos de Prueba Cubiertos

- ✅ Cálculo normal con todas las evaluaciones
- ✅ Sin asistencia mínima (penalización aplicada)
- ✅ Con puntos extra
- ✅ Sin puntos extra
- ✅ Casos borde: 0 evaluaciones, pesos inválidos
- ✅ Máximo de evaluaciones (10)
- ✅ Determinismo (mismos datos = misma nota)
- ✅ Tiempo de cálculo < 300ms

## Ejemplos de Uso

### Ejemplo 1: Cálculo Normal

```bash
curl -X POST http://localhost:8080/api/grades/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "examsStudents": [
      {"grade": 4.0, "weight": 30.0},
      {"grade": 3.5, "weight": 40.0},
      {"grade": 4.5, "weight": 30.0}
    ],
    "hasReachedMinimumClasses": true,
    "allYearsTeachers": false
  }'
```

### Ejemplo 2: Con Penalización por Asistencia

```bash
curl -X POST http://localhost:8080/api/grades/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "examsStudents": [
      {"grade": 4.0, "weight": 100.0}
    ],
    "hasReachedMinimumClasses": false,
    "allYearsTeachers": false
  }'
```

Resultado esperado: Nota final = 3.6 (4.0 - 10% = 3.6)

### Ejemplo 3: Con Puntos Extra

```bash
curl -X POST http://localhost:8080/api/grades/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "examsStudents": [
      {"grade": 4.0, "weight": 100.0}
    ],
    "hasReachedMinimumClasses": true,
    "allYearsTeachers": true
  }'
```

Resultado esperado: Nota final = 4.5 (4.0 + 0.5)

### Ejemplo 4: Con Penalización y Puntos Extra

```bash
curl -X POST http://localhost:8080/api/grades/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "examsStudents": [
      {"grade": 4.0, "weight": 100.0}
    ],
    "hasReachedMinimumClasses": false,
    "allYearsTeachers": true
  }'
```

Resultado esperado: Nota final = 4.1 (4.0 - 0.4 + 0.5)

## Validaciones

- Las notas deben estar entre 0.0 y 5.0
- Los pesos deben estar entre 0.01 y 100.0
- La suma de pesos debe ser exactamente 100%
- Máximo 10 evaluaciones por estudiante
- Mínimo 1 evaluación requerida

## Notas Técnicas

- El cálculo es determinista: mismos datos = misma nota
- El tiempo de cálculo está validado para ser < 300ms
- Se soportan 50 usuarios concurrentes (verificado en tests)
- Todas las clases siguen principios SOLID
- Separación de responsabilidades: cada clase tiene una única responsabilidad
- Bajo acoplamiento y alta cohesión

## Cobertura de Tests

Los tests cubren:
- Casos normales
- Casos borde
- Validaciones
- Concurrencia
- Determinismo
- Rendimiento

La cobertura supera el 50% requerido.

