package dam.pmdm.spyrothedragon

import android.animation.ObjectAnimator
import android.content.Context
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

        // --- Compruebo si hay que mostrar la guía al arrancar la app ---
        checkFirstLaunch()
    }

    /**
     * Este método comprueba si es la primera vez que se ejecuta la app.
     */
    private fun checkFirstLaunch() {
        // Abrimos el archivo de preferencias llamado "SpyroPrefs"
        // MODE_PRIVATE para que solo lo pueda leer nuestra App
        val sharedPref = getSharedPreferences("SpyroPrefs_V2", Context.MODE_PRIVATE)

        // Leemos el booleano "isFirstLaunch". Si no existe, por defecto devolvemos true.
        val isFirstLaunch = sharedPref.getBoolean("isFirstLaunch", true)

        if (isFirstLaunch) {
            // SI ES LA PRIMERA VEZ:
            startGuide()

            // Ahora marcamos en el archivo de preferencias que ya no es la primera vez
            val editor = sharedPref.edit()
            // Recordar que la versión final tengo que descomentar esta opción
            // editor.putBoolean("isFirstLaunch", false)
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

    private fun nextStep() {
        if (currentStep < 5) {
            currentStep++
            updateGuideText()
        } else {
            hideGuide()
        }
    }

    private fun updateGuideText() {
        val bubble = binding.includeGuide.guideBubble
        val arrow = binding.includeGuide.guideArrow
        val btnSkip = binding.includeGuide.btnSkip
        val bubbleParams = bubble.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
        val arrowParams = arrow.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
        val density = resources.displayMetrics.density

        // Configuración base
        btnSkip.visibility = View.VISIBLE
        arrow.visibility = View.VISIBLE
        bubbleParams.bottomToBottom = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
        bubbleParams.topToTop = -1
        bubbleParams.topMargin = 0
        bubbleParams.bottomMargin = (16 * density).toInt() // Distancia al menú inferior

        when (currentStep) {
            1 -> {
                binding.includeGuide.guideText.text = "Aquí podrás explorar a todos los personajes."
                bubbleParams.horizontalBias = 0.02f
                arrowParams.horizontalBias = 0.1f
                arrow.rotation = 0f
                // Flecha debajo del bocadillo
                arrowParams.topToBottom = binding.includeGuide.bubbleContent.id
                arrowParams.bottomToTop = -1
            }
            2 -> {
                binding.includeGuide.guideText.text = "Explora los diferentes mundos de la saga."
                bubbleParams.horizontalBias = 0.5f
                arrowParams.horizontalBias = 0.5f
                arrow.rotation = 0f
                arrowParams.topToBottom = binding.includeGuide.bubbleContent.id
                arrowParams.bottomToTop = -1
            }
            3 -> {
                binding.includeGuide.guideText.text = "Gestiona tus objetos coleccionables."
                bubbleParams.horizontalBias = 0.98f
                arrowParams.horizontalBias = 0.9f
                arrow.rotation = 0f
                arrowParams.topToBottom = binding.includeGuide.bubbleContent.id
                arrowParams.bottomToTop = -1
            }
            4 -> {
                binding.includeGuide.guideText.text = "Información sobre el autor y la app."
                bubbleParams.bottomToBottom = -1
                bubbleParams.topToTop = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
                bubbleParams.topMargin = (50 * density).toInt() // Cerca del icono superior
                bubbleParams.horizontalBias = 0.98f

                // FLECHA ARRIBA: Invertimos anclajes
                arrow.rotation = 180f
                arrowParams.horizontalBias = 0.9f
                arrowParams.bottomToTop = binding.includeGuide.bubbleContent.id
                arrowParams.topToBottom = -1 // Quitamos el anclaje de abajo
            }
            5 -> {
                binding.includeGuide.guideText.text = "¡Guía completada! Ya puedes comenzar."
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
    }

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
