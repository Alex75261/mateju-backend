# Mateju backend

API local de la primera entrega de Mateju. Usa Ktor, `kotlinx.serialization` y
cuentas de demostración definidas en `src/main/resources/users.json`.

## Requisitos

- JDK 21
- Gradle 9.5.1 o superior compatible

## Ejecutar

```powershell
cd backend
.\gradlew.bat run
```

El servidor queda disponible en `http://localhost:8080` (o en el puerto de la
variable de entorno `PORT`).

```powershell
Invoke-RestMethod http://localhost:8080/health

Invoke-RestMethod http://localhost:8080/api/v1/auth/login `
  -Method Post -ContentType 'application/json' `
  -Body '{"username":"ana.estudiante","password":"Mateju2026!"}'
```

## Endpoints

| Método | Ruta | Respuesta |
| --- | --- | --- |
| `GET` | `/health` | `{ "status": "ok" }` |
| `POST` | `/api/v1/auth/login` | `id`, `displayName` y `role` del usuario autenticado |

Las cuentas son exclusivamente ficticias para la demostración local. No se
deben reutilizar las contraseñas de este archivo en un entorno real.

## Pruebas

```powershell
cd backend
.\gradlew.bat test
```
