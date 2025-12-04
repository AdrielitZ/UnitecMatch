# UnitecMatch

## Datos de la universidad y del equipo

- **Universidad:** Universidad Tecnológica de México (UNITEC)
- **Campus:** [Sur]
- **Carrera:** Ingeniería en Sistemas Computacionales
- **Materia:** Aplicaciones Móviles
- **Profesor(a):** [Juan Luis Carrillo Garcia]
- **Alumno(s):** [Joel Adriel Pérez Campos/Jesús Daniel Macias Matus]
- **Cuatrimestre / Grupo:** [10.º cuatrimestre]
- **Fecha:** [04/12/2004]

---

## Descripción general del proyecto

**UnitecMatch** es una aplicación móvil desarrollada en **Kotlin** utilizando **Jetpack Compose** que simula una experiencia de exploración de perfiles tipo “match” entre estudiantes.  

Cada perfil muestra:

- Nombre y edad
- Biografía corta
- Intereses
- Distancia aproximada en kilómetros
- Fotografía de perfil

El objetivo del proyecto es practicar el desarrollo de interfaces modernas con Jetpack Compose, el manejo de estado y la navegación entre pantallas en el contexto de una aplicación sencilla pero coherente.

---

## Tecnologías utilizadas

- **Lenguaje:** Kotlin
- **Framework UI:** Jetpack Compose
- **Patrones / Estilo:** Componentes Material, diseño declarativo
- **IDE:** Android Studio
- **Control de versiones:** Git + GitHub
- **Plataforma objetivo:** Android

Las dependencias específicas (Compose, Material, AndroidX, etc.) se encuentran declaradas en el archivo de configuración del módulo `app` (`build.gradle`/`build.gradle.kts`).

---

## Instrucciones de instalación y ejecución

### 1. Prerrequisitos

- Android Studio instalado (versión reciente recomendada)
- JDK configurado en Android Studio
- Dispositivo físico o emulador con una versión de Android compatible con el proyecto

### 2. Clonar el repositorio

```bash
git clone https://github.com/AdrielitZ/UnitecMatch.git
cd UnitecMatch

UnitecMatch/
 ├─ app/
 │   ├─ src/
 │   │   ├─ main/
 │   │   │   ├─ java/com/example/unitec_match/
 │   │   │   │   ├─ data/         # Modelos de datos y lista de perfiles de ejemplo
 │   │   │   │   ├─ ui/           # Pantallas y componentes de interfaz con Compose
 │   │   │   │   ├─ navigation/   # (Si aplica) manejo de rutas y navegación
 │   │   │   │   └─ ...           
 │   │   │   └─ res/              # Recursos gráficos y de texto
 │   │   └─ ...
 │   └─ build.gradle(.kts)
 ├─ build.gradle(.kts)
 ├─ settings.gradle(.kts)
 └─ ...

