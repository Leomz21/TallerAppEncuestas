# App de Encuestas Offline

Aplicación Android para realizar encuestas de satisfacción sin conexión a internet. Las preguntas no están quemadas en la interfaz: se leen desde una base de datos local SQLite y las respuestas se guardan en el dispositivo.

## Cómo funciona

1. Al abrir la app aparece una pantalla de bienvenida con un botón para ingresar a la encuesta.
2. La encuesta muestra las preguntas leídas desde la base de datos y genera los campos de respuesta dinámicamente.
3. Al guardar, se muestra un mensaje de que la encuesta se completó con éxito, con la opción de realizar otra encuesta o ver el historial.
4. El historial muestra todas las encuestas guardadas con su fecha y respuestas.

## Base de datos

Se usan dos tablas: `preguntas` y `respuestas`. Las preguntas se insertan automáticamente en el `onCreate()` del `SQLiteOpenHelper`, no desde la interfaz.

## Tecnologías

- Java
- SQLite
- RecyclerView
- SDK mínimo: API 24 (Android 7.0)

## Cómo ejecutar

Clonar el repositorio, abrirlo en Android Studio y ejecutarlo en un emulador o dispositivo con Android 7.0 o superior.
