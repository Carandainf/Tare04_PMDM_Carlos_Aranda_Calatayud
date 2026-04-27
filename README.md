# 🐉 Spyro the Dragon - App Informativa & Guía Interactiva

¡Bienvenido al repositorio de la aplicación de Spyro the Dragon! Este proyecto ha sido desarrollado como parte del módulo **PMDM**, enfocándose en la experiencia de usuario (UX), animaciones avanzadas y el uso de elementos multimedia en Android.

---

## 📖 Introducción
Esta aplicación es una enciclopedia visual sobre el universo de Spyro. Su propósito es permitir a los fans explorar personajes, mundos y coleccionables de la saga original. Además, incluye una **Guía de Inicio Interactiva** diseñada para ayudar a los nuevos usuarios a navegar por la interfaz de forma sencilla y atractiva.

## ✨ Características Principales
* **Guía de Inicio Interactiva**: Un tutorial paso a paso con sonidos y animaciones que bloquea la interacción hasta completar el aprendizaje.
* **Sección de Personajes**: Listado detallado de los protagonistas y enemigos.
* **Exploración de Mundos**: Información sobre los niveles icónicos del juego.
* **Gestión de Coleccionables**: Visualización de los objetos que Spyro debe recolectar.
* **Easter Eggs Ocultos**: 
    * **Vídeo Secreto**: Se activa con 3 clics rápidos en la sección de Mundos.
    * **Magia de Ripto**: Animación avanzada con **Canvas** al mantener pulsado al personaje Ripto.

## 🛠️ Tecnologías Utilizadas
* **Lenguaje**: Kotlin 🚀
* **Interfaz**: XML con ConstraintLayout y ViewBinding.
* **Navegación**: Jetpack Navigation Component.
* **Multimedia**: 
    * `SoundPool` para efectos de audio rápidos.
    * `VideoView` para la reproducción de contenido multimedia.
* **Gráficos Avanzados**: `Canvas API` para la creación de animaciones personalizadas mediante dibujo por código.
* **Persistencia**: `SharedPreferences` para controlar la visualización única de la guía.

## 🚀 Instrucciones de Uso
1.  Clona este repositorio:
    ```bash
    git clone [https://github.com/TU_USUARIO/TU_REPOSITORIO.git](https://github.com/TU_USUARIO/TU_REPOSITORIO.git)
    ```
2.  Abre el proyecto en **Android Studio** (Ladybug o superior).
3.  Sincroniza el proyecto con los archivos de **Gradle**.
4.  Ejecuta la aplicación en un emulador o dispositivo físico con Android 11+ (API 30+).

## 💡 Conclusiones del Desarrollador

El desarrollo de esta aplicación ha sido un reto técnico significativo que ha ido más allá de una simple interfaz de usuario. El proceso de aprendizaje ha sido intenso, enfrentando y superando los siguientes desafíos:

1.  **Lógica de Posicionamiento Dinámico (El reto de las flechas)**: Uno de los mayores desafíos fue calcular las coordenadas exactas para que las flechas de la guía apuntaran correctamente a los elementos de la interfaz (como los iconos de la Toolbar o las pestañas del Bottom Navigation). Adaptar estos elementos visuales para que se posicionen de forma coherente en diferentes tamaños de pantalla requirió una comprensión profunda del sistema de coordenadas de Android.
2.  **Gestión de Capas y Bloqueo de Interacción**: Lograr que la guía se superponga de manera semitransparente (`#80000000`) y que, al mismo tiempo, bloquee cualquier clic accidental en los botones inferiores de la aplicación, garantizando que el usuario mantenga el foco en el tutorial durante todo el proceso.
3.  **Animaciones Avanzadas con Canvas**: La implementación del Easter Egg de Ripto no fue solo "poner una imagen", sino aprender a dibujar en tiempo real sobre el `Canvas`. El uso de `RadialGradient` y `ValueAnimator` permitió crear un efecto de energía mágica profesional, optimizando el rendimiento mediante la pre-asignación de objetos para evitar tirones (*lag*) y avisos de memoria del sistema.
4.  **Feedback Sensorial (Audio y Movimiento)**: La integración de `SoundPool` para efectos de sonido inmediatos, junto con interpoladores de rebote (`OvershootInterpolator`) para los bocadillos de texto, fue clave para transformar una aplicación estática en una experiencia dinámica, fluida y "viva".

**Aprendizaje Personal**: Este proyecto me ha enseñado que los pequeños detalles —como el feedback sonoro tras una acción o que una flecha apunte exactamente al centro de un icono— son los que marcan la diferencia entre una aplicación funcional y una de alta calidad. Ha sido una excelente oportunidad para dominar la arquitectura de adaptadores, el ciclo de vida de los fragmentos y la comunicación avanzada entre componentes de Android.

---
Creado por: **Carlos Aranda Calatayud** - 2026
