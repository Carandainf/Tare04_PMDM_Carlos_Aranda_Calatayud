package dam.pmdm.spyrothedragon

import android.animation.ObjectAnimator
import android.content.Context
import android.media.SoundPool
import android.os.Bundle

import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.BounceInterpolator
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import dam.pmdm.spyrothedragon.databinding.ActivityMainBinding
import androidx.core.graphics.toColorInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var navController: NavController? = null

    // Variable para saber en qué paso de la guía estamos (0, 1, 2...)
    private var currentStep = 0


     // Variable de tipo SoundPool, que se utiliza para la reproducción de efectos de sonido cortos (clips de audio).
    private lateinit var soundPool: SoundPool


    // Variable de tipo Int, que almacena el Identificador del recurso de sonido cargado en memoria para ser reproducido por SoundPool.
    private var soundId: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.navHostFragment)

        navHostFragment?.let {
            navController = NavHostFragment.findNavController(it)
            NavigationUI.setupWithNavController(binding.navView, navController!!)
            NavigationUI.setupActionBarWithNavController(this, navController!!)
        }

        binding.navView.setOnItemSelectedListener { menuItem ->
            selectedBottomMenu(menuItem)
        }

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_characters,
                R.id.navigation_worlds,
                R.id.navigation_collectibles -> {
                    // En las pantallas de los tabs no mostramos la flecha atrás
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                }
                else -> {
                    // En el resto de pantallas sí
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
            }
        }

        // --- Configuro los diferentes botones de la guía ---

        // Botón "Comenzar" de la pantalla 1
        binding.includeGuide.btnStart.setOnClickListener {
            startInteractiveSteps()
        }

        // Botón "Siguiente" de los bocadillos
        binding.includeGuide.btnNext.setOnClickListener {
            nextStep()
        }

        // Botón "Omitir" (para cualquier pantalla)
        binding.includeGuide.btnSkip.setOnClickListener {
            hideGuide()
        }

        // --- Fin botones ---

        // --- Configuración Audio ---

        // Configuración de SoundPool para la versión de Android Lollipop y superiores
        val audioAttributes = android.media.AudioAttributes.Builder()
            .setUsage(android.media.AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = android.media.SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()

        // Cargamos el sonido de la carpeta raw
        soundId = soundPool.load(this, R.raw.guia_sound, 1)

        // --- Fin Configuración Audio ---

        // --- Compruebo si hay que mostrar la guía al arrancar la app ---
        checkFirstLaunch()
    }

    /**
     * Este método comprueba si es la primera vez que se ejecuta la app.
     */
    private fun checkFirstLaunch() {
        // Abrimos el archivo de preferencias llamado "SpyroPrefs"
        // MODE_PRIVATE para que solo lo pueda leer nuestra App
        val sharedPref = getSharedPreferences("SpyroPrefs", Context.MODE_PRIVATE)

        // Leemos el booleano "isFirstLaunch". Si no existe, por defecto devolvemos true.
        val isFirstLaunch = sharedPref.getBoolean("isFirstLaunch", true)

        if (isFirstLaunch) {
            // SI ES LA PRIMERA VEZ:
            startGuide()

            // Ahora marcamos en el archivo de preferencias que ya no es la primera vez
            val editor = sharedPref.edit()
            // Recordar que la versión final tengo que descomentar esta opción
            editor.putBoolean("isFirstLaunch", false)
            editor.apply() // Esto guarda los cambios de forma asíncrona
        }
    }

    /**
     * Aquí es donde más adelante inflaremos los XML de la guía.
     * De momento pongo un mensaje de prueba para ver que funciona.
     */
    private fun startGuide() {
        // Hacemos visible el contenedor principal de la guía
        binding.includeGuide.root.visibility = View.VISIBLE

        // PANTALLA 1: Mostramos el grupo de bienvenida y ocultamos el bocadillo de pasos
        binding.includeGuide.welcomeLayout.visibility = View.VISIBLE
        binding.includeGuide.guideBubble.visibility = View.GONE

        // El fondo de la Pantalla 1 debe ser mi Drawable
        binding.includeGuide.root.setBackgroundResource(R.drawable.guide_background)
    }

    /**
     * Inicia la fase interactiva de la guía tras la pantalla de bienvenida.
     * Oculta el layout de bienvenida, cambia el fondo del contenedor a un tono
     * traslúcido y activa la visibilidad del primer bocadillo informativo,
     * además escucharemos un audio corto
     */
    private fun startInteractiveSteps() {
        // Al dar a "Comenzar", ocultamos TODO el bloque de bienvenida de golpe
        binding.includeGuide.welcomeLayout.visibility = View.GONE

        // El fondo del contenedor raíz se vuelve transparente para ver la app real debajo, como indica la profesora
        binding.includeGuide.root.setBackgroundColor("#80000000".toColorInt())

        // Mostramos el bocadillo con la flecha
        binding.includeGuide.guideBubble.visibility = View.VISIBLE

        currentStep = 1
        updateGuideText()
    }

    /**
     * Controla el flujo de navegación de la guía.
     * Incrementa el paso actual y actualiza el contenido visual, o finaliza
     * la guía si se han completado todos los pasos explicativos.
     */
    private fun nextStep() {
        if (currentStep < 5) {
            currentStep++
            updateGuideText()
        } else {
            hideGuide()
        }
    }

    /**
     * Actualiza dinámicamente el contenido del bocadillo, la posición de la burbuja
     * y la orientación de la flecha indicadora según el paso actual de la guía.
     * Ajusta los LayoutParams para señalar elementos específicos de la interfaz (BottomNav, Toolbar).
     */
    private fun updateGuideText() {
        val bubble = binding.includeGuide.guideBubble
        val arrow = binding.includeGuide.guideArrow
        val btnSkip = binding.includeGuide.btnSkip
        val bubbleContent = binding.includeGuide.bubbleContent

        val bubbleParams = bubble.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
        val arrowParams = arrow.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
        val density = resources.displayMetrics.density

        // RESET GENERAL DE ESTADO
        btnSkip.visibility = View.VISIBLE
        arrow.visibility = View.VISIBLE

        // Reset flecha: por defecto siempre debajo del contenido
        arrowParams.topToBottom = bubbleContent.id
        arrowParams.bottomToTop = -1
        arrow.rotation = 0f

        // Reset burbuja: por defecto en la parte inferior (para el menú)
        bubbleParams.bottomToBottom = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
        bubbleParams.topToTop = -1
        bubbleParams.topMargin = 0

        // Ultimo cálculo de distancia = 45dp espacio para que la flecha se vea sobre el menú
        bubbleParams.bottomMargin = (45 * density).toInt()

        when (currentStep) {
            1 -> {
                binding.includeGuide.guideText.text = "Aquí podrás explorar a todos los personajes."
                bubbleParams.horizontalBias = 0.02f
                arrowParams.horizontalBias = 0.1f
            }
            2 -> {
                binding.includeGuide.guideText.text = "Explora los diferentes mundos de la saga."
                bubbleParams.horizontalBias = 0.5f
                arrowParams.horizontalBias = 0.5f
            }
            3 -> {
                binding.includeGuide.guideText.text = "Gestiona tus objetos coleccionables."
                bubbleParams.horizontalBias = 0.98f
                arrowParams.horizontalBias = 0.9f
            }
            4 -> {
                binding.includeGuide.guideText.text = "Información sobre el autor y la app."
                bubbleParams.bottomToBottom = -1
                bubbleParams.topToTop = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID

                // Reducimos a 10dp para que esté bien pegado al icono superior
                bubbleParams.topMargin = (10 * density).toInt()
                bubbleParams.horizontalBias = 0.98f

                // CAMBIO FLECHA A PARTE SUPERIOR
                arrow.rotation = 180f
                arrowParams.horizontalBias = 0.95f // Ajustado un pelo más a la derecha
                arrowParams.topToBottom = -1
                arrowParams.bottomToTop = bubbleContent.id
            }
            5 -> {
                binding.includeGuide.guideText.text = "¡Resumen final!\n1. Personajes explorados\n2. Mundos visitados\n3. Coleccionables vistos\n4. Ayuda localizada"
                bubbleParams.topToTop = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
                bubbleParams.bottomToBottom = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
                bubbleParams.horizontalBias = 0.5f
                bubbleParams.topMargin = 0
                bubbleParams.bottomMargin = 0

                arrow.visibility = View.GONE
                btnSkip.visibility = View.GONE
                binding.includeGuide.btnNext.text = "Finalizar"
            }
        }

        bubble.layoutParams = bubbleParams
        arrow.layoutParams = arrowParams
        animateBubble(bubble)
        playGuideSound()
    }

    /**
     * Ejecuta el sonido de la guía cargado previamente en SoundPool.
     * Se llama en cada cambio de paso para ofrecer feedback auditivo al usuario.
     */
    private fun playGuideSound() {
        if (soundId != 0) {
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
        }
    }

    /**
     * Realiza una animación de escala (zoom-in) con efecto de rebote sobre la vista proporcionada.
     * @param view La vista que recibirá la animación (en este caso, el bocadillo de texto).
     */
private fun animateBubble(view: View) {
    // Animación de escala (hace que el bocadillo "aparezca" creciendo)
    view.scaleX = 0f
    view.scaleY = 0f

    view.animate()
        .scaleX(1f)
        .scaleY(1f)
        .setDuration(500)
        .setInterpolator(android.view.animation.OvershootInterpolator()) // Efecto rebote
        .start()
    }

    /**
     * Finaliza la experiencia de la guía ocultando el contenedor raíz de la misma,
     * permitiendo al usuario interactuar libremente con la aplicación.
     */
    private fun hideGuide() {
        // Ocultamos la guía para que el usuario pueda usar la app normal
        binding.includeGuide.root.visibility = View.GONE
    }

    private fun selectedBottomMenu(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_characters ->
                navController?.navigate(R.id.navigation_characters)
            R.id.nav_worlds ->
                navController?.navigate(R.id.navigation_worlds)
            else ->
                navController?.navigate(R.id.navigation_collectibles)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.about_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_info) {
            showInfoDialog()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun showInfoDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.title_about)
            .setMessage(R.string.text_about)
            .setPositiveButton(R.string.accept, null)
            .show()
    }
}
