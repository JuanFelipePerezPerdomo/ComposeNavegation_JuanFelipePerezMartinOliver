# 📚 BiblioApp - Gestión de Biblioteca Personal

## 1. Datos del Proyecto

| Campo | Valor |
|-------|-------|
| **Nombre** | BiblioApp (ComposeNavegationJuanFelipePerezMartinOliver) |
| **Autores** | Juan Felipe Pérez Perdomo, Martín Oliver Pellares|
| **Fecha** | Diciembre 2025 |
| **Target SDK** | 36 |
| **Min SDK** | 26 (Android 8.0 Oreo) |
| **Versión de la App** | 1.0.0 |

---

## 2. Tecnología Elegida y Justificación

### Jetpack Compose

Se ha elegido **Jetpack Compose** como framework de UI por las siguientes razones:

- **Desarrollo declarativo**: Permite describir la UI como funciones, lo que resulta en código más legible y mantenible comparado con el sistema tradicional de Views y XML.

- **Estado reactivo**: La integración nativa con `StateFlow` y `collectAsState()` facilita la gestión del estado de la aplicación de forma reactiva y predecible.

- **Menos código boilerplate**: Elimina la necesidad de `findViewById`, adapters complejos y archivos XML separados.

- **Material Design 3**: Soporte nativo para Material You con temas dinámicos y componentes modernos como `ModalBottomSheet`, `FilterChip` y `SegmentedButton`.

- **Navegación tipada**: Integración con Navigation Compose usando rutas serializables (`@Serializable`), proporcionando seguridad de tipos en tiempo de compilación.

### Stack Tecnológico Completo

| Tecnología | Uso |
|------------|-----|
| Kotlin | Lenguaje principal |
| Jetpack Compose | Framework de UI |
| Room | Base de datos local (SQLite) |
| DataStore | Preferencias de usuario |
| Navigation Compose | Navegación entre pantallas |
| Kotlin Coroutines + Flow | Programación asíncrona y reactiva |
| Material 3 | Sistema de diseño |

---

## 3. Configuración del Entorno y Ejecución

### Requisitos Previos

- **Android Studio**: Hedgehog (2023.1.1) o superior
- **JDK**: 17 o superior
- **Gradle**: 8.0+
- **Kotlin**: 1.9.0+

### Pasos de Instalación

1. **Clonar el repositorio**
   ```bash
   git clone <url-del-repositorio>
   cd ComposeNavegationJuanFelipePerezMartinOliver
   ```

2. **Abrir en Android Studio**
   - File → Open → Seleccionar la carpeta del proyecto
   - Esperar a que Gradle sincronice las dependencias

3. **Configurar un dispositivo**
   
   **Opción A - Emulador:**
   - Tools → Device Manager → Create Device
   - Seleccionar Pixel 6 o similar
   - Descargar imagen del sistema API 34
   
   **Opción B - Dispositivo físico:**
   - Habilitar "Opciones de desarrollador" en el dispositivo
   - Activar "Depuración USB"
   - Conectar via USB y aceptar la huella digital

4. **Ejecutar la aplicación**
   ```bash
   ./gradlew installDebug
   ```
   O pulsar el botón ▶️ (Run) en Android Studio

### Dependencias Principales (build.gradle.kts)

```kotlin
dependencies {
    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")
    
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    
    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
}
```

---

## 4. Estructura del Proyecto

```
app/src/main/java/com/edu/dam/
│
├── 📂 data/                          # Capa de datos
│   ├── 📂 local/                     # Base de datos Room
│   │   ├── 📂 dao/
│   │   │   └── BooksDao.kt           # Data Access Object - queries SQL
│   │   ├── 📂 entity/
│   │   │   └── BookEntity.kt         # Entidad de Room (tabla books)
│   │   └── BooksDatabase.kt          # Configuración de la BD
│   │
│   ├── 📂 mappers/
│   │   └── BookMappers.kt            # Conversión Entity ↔ Domain
│   │
│   ├── 📂 model/
│   │   └── Book.kt                   # Modelo de dominio
│   │
│   ├── 📂 prefs/
│   │   └── UserPrefsRepository.kt    # DataStore - preferencias usuario
│   │
│   ├── 📂 repository/
│   │   └── BooksRepository.kt        # Repositorio (patrón Repository)
│   │
│   └── AppState.kt                   # Estado global de la app
│
├── 📂 di/
│   └── ServiceLocator.kt             # Inyección de dependencias manual
│
├── 📂 navigation/
│   ├── NavGraph.kt                   # Configuración de rutas
│   └── Routes.kt                     # Definición de destinos (@Serializable)
│
├── 📂 theme/
│   ├── Animations.kt                 # Animaciones reutilizables
│   ├── Color.kt                      # Paleta de colores (naranja/azul)
│   ├── Theme.kt                      # Tema Material 3
│   └── Type.kt                       # Tipografía
│
├── 📂 ui/
│   ├── 📂 books/
│   │   └── BooksViewModel.kt         # ViewModel principal
│   │
│   ├── 📂 common/
│   │   ├── BookFormats.kt            # Formateo de fechas
│   │   └── UserNameValidation.kt     # Validación de nombres
│   │
│   ├── 📂 components/
│   │   └── AppBottomBar.kt           # Barra de navegación inferior
│   │
│   ├── 📂 detail/
│   │   ├── 📂 components/
│   │   │   └── EditNoteSheet.kt      # Bottom sheet de edición
│   │   └── DetailScreen.kt           # Pantalla de detalle
│   │
│   ├── 📂 home/
│   │   ├── 📂 components/
│   │   │   ├── AddBookSheet.kt       # Bottom sheet para añadir
│   │   │   └── BookCard.kt           # Tarjeta de libro
│   │   └── HomeScreen.kt             # Pantalla principal
│   │
│   ├── 📂 login/
│   │   └── LoginScreen.kt            # Pantalla de login con captcha
│   │
│   └── 📂 settings/
│       └── SettingsScreen.kt         # Pantalla de ajustes
│
└── MainActivity.kt                   # Entry point de la app
```

### Descripción de Carpetas Principales

| Carpeta | Responsabilidad |
|---------|-----------------|
| `data/` | Acceso a datos: Room, DataStore, repositorios |
| `di/` | Inyección de dependencias (ServiceLocator) |
| `navigation/` | Configuración de Navigation Compose |
| `theme/` | Colores, tipografía y tema Material 3 |
| `ui/` | Pantallas y componentes de interfaz |

---

## 5. Perfil de Despliegue

### Configuración SDK

| Parámetro | Valor |
|-----------|-------|
| **compileSdk** | 36 |
| **targetSdk** | 36 |
| **minSdk** | 24 |
| **buildToolsVersion** | 36.0.0 |

### Dispositivo de Pruebas

| Característica | Valor |
|----------------|-------|
| **Dispositivo** | Emulador Pixel 6 / Dispositivo físico |
| **Versión Android** | Android 14 (API 34) |
| **Resolución** | 1080 x 2400 pixels |
| **Densidad** | 411 dpi (xxhdpi) |
| **RAM asignada** | 2048 MB (emulador) |

### Modos de Tema

La aplicación soporta tres modos de tema:

- **Claro** 🌞: Paleta naranja (`Orange40`, `Amber40`)
- **Oscuro** 🌙: Paleta azul (`Blue80`, `Cyan80`)
- **Sistema** 📱: Sigue la configuración del dispositivo

---

## 6. Funcionamiento de la App

### Descripción General

**BiblioApp** es una aplicación de gestión de biblioteca personal que permite a los usuarios catalogar sus libros con información detallada.

### Funcionalidades Principales

#### 🔐 Autenticación
- Login con nickname validado (3-30 caracteres)
- Captcha matemático para verificación humana
- Persistencia del nombre de usuario

#### 📚 Gestión de Libros
- **Crear**: Añadir libros con título, autor, páginas y sinopsis
- **Leer**: Visualizar lista en grid de 3 columnas
- **Actualizar**: Editar información mediante bottom sheet
- **Eliminar**: Long-press en tarjeta → diálogo de confirmación

#### ⭐ Favoritos
- Marcar/desmarcar libros como favoritos
- Vista dedicada solo para favoritos
- Icono de estrella con animación de color

#### 🔍 Filtros y Ordenación
- Ordenar por fecha (más recientes primero)
- Ordenar por título (A-Z)
- Chips interactivos con animación

#### ⚙️ Configuración
- Cambio de nombre de usuario
- Selector de tema (Claro/Oscuro/Sistema)
- Reinicio del mensaje de bienvenida

### Flujo de Navegación

```
Login → Home ←→ Favoritos
          ↓         ↓
       Detalle   Ajustes
          ↓
    EditSheet
```

### Capturas de Pantalla

| Pantalla | Descripción |
|----------|-------------|
| **Home** | Grid de libros con filtros superiores |
| **Detalle** | Información completa del libro |
| **Ajustes** | Configuración de tema y perfil |

<img width="472" height="890" alt="image" src="https://github.com/user-attachments/assets/774d1133-b453-4408-b3f1-974102137255" />

<img width="407" height="307" alt="image" src="https://github.com/user-attachments/assets/6c07a02c-8681-448a-8872-765de847d24f" />


<img width="429" height="882" alt="image" src="https://github.com/user-attachments/assets/0674beaa-4ff0-40b6-98c1-b58273598733" />

<img width="371" height="720" alt="image" src="https://github.com/user-attachments/assets/7ec95892-3b94-4f06-a6ee-fac037b982e5" />

<img width="401" height="624" alt="image" src="https://github.com/user-attachments/assets/4be84e84-700f-4d7e-bc36-dac1ee75c879" />

<img width="384" height="840" alt="image" src="https://github.com/user-attachments/assets/ae375158-eb73-4488-a7da-06a75b015c11" />

<img width="412" height="866" alt="image" src="https://github.com/user-attachments/assets/745222cd-337c-49a7-8315-c1116817898c" />

<img width="392" height="859" alt="image" src="https://github.com/user-attachments/assets/472a07a8-bc7b-4b6d-8595-b3f171323b95" />


---

## 7. Conclusión y Limitaciones

### Aprendizajes

Durante el desarrollo de esta aplicación se adquirieron conocimientos en:

1. **Arquitectura MVVM**: Separación clara entre UI, lógica de negocio y datos usando ViewModel y Repository.

2. **Jetpack Compose**: Desarrollo de interfaces declarativas, manejo de estado con `remember`, `mutableStateOf` y recomposición eficiente.

3. **Room Database**: Implementación de persistencia local con DAOs, entidades e índices para optimización.

4. **DataStore Preferences**: Almacenamiento de preferencias de usuario de forma asíncrona y type-safe.

5. **Navigation Compose**: Navegación tipada con rutas serializables y paso de argumentos.

6. **Material Design 3**: Implementación de temas dinámicos, componentes modernos y animaciones.

7. **Kotlin Flows**: Programación reactiva para observar cambios en la base de datos y preferencias.

### Retos Encontrados

| Reto | Solución |
|------|----------|
| **Swipe en grid** | El gesto de swipe-to-delete no funciona bien en grids de 3 columnas; se implementó long-press como alternativa |
| **Recomposición excesiva** | Uso de `remember` y `derivedStateOf` para evitar recálculos innecesarios |
| **Tema dinámico** | Implementación de `ThemeMode` enum para manejar los 3 estados (Light/Dark/System) |
| **Validación de formularios** | Creación de `UserNameValidation` data class para centralizar la lógica |

### Limitaciones Conocidas

1. **Sin sincronización en la nube**: Los datos solo se almacenan localmente; no hay backup ni sincronización entre dispositivos.

2. **Sin imágenes de portada**: No se implementó la funcionalidad de añadir imágenes a los libros.

3. **Sin búsqueda de texto**: No existe una barra de búsqueda para filtrar libros por título o autor.

4. **Sin categorías/etiquetas**: Los libros no pueden organizarse por género o categorías personalizadas.

5. **Sin exportación de datos**: No hay opción para exportar la biblioteca a CSV o JSON.

6. **Rendimiento en listas grandes**: Con miles de libros, el `LazyVerticalGrid` podría presentar lag; se recomienda implementar paginación.

### Posibles Mejoras Futuras

- [ ] Implementar búsqueda con `SearchBar`
- [ ] Añadir categorías y etiquetas
- [ ] Integrar API de libros (Google Books, Open Library)
- [ ] Sincronización con Firebase/Supabase
- [ ] Modo offline-first con WorkManager
- [ ] Widget para el launcher
- [ ] Notificaciones de recordatorio de lectura
