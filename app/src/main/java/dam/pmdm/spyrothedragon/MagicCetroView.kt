package dam.pmdm.spyrothedragon

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.graphics.toColorInt

class MagicCetroView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var radius = 0f
    private var alphaValue = 0

    // Colores para el diamante
    private val magicColors = intArrayOf(
        "#FFD700".toColorInt(), // Dorado
        "#FF00FF".toColorInt(), // Magenta
        Color.TRANSPARENT
    )

    /**
     * Por temas de optimización creamos el gradiente en onDraw,
     * lo declaramos aquí y lo actualizamos solo cuando cambie el radio.
     */
    fun startMagic() {
        val animator = ValueAnimator.ofFloat(0f, 500f)
        animator.duration = 2000
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.RESTART
        animator.interpolator = AccelerateDecelerateInterpolator()

        animator.addUpdateListener { animation ->
            radius = animation.animatedValue as Float
            alphaValue = (255 * (1 - animation.animatedFraction)).toInt()

            // Actualizamos el shader solo cuando el radio cambia
            if (radius > 0) {
                paint.shader = RadialGradient(
                    width / 2f, height / 2f, radius,
                    magicColors, null, Shader.TileMode.CLAMP
                )
            }

            invalidate() // Redibuja
        }
        animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f

        paint.alpha = alphaValue

        // Ahora onDraw solo se encarga de DIBUJAR, no de CREAR objetos y de esa manera no se queja
        // el Garbage Collector
        canvas.drawCircle(centerX, centerY, radius, paint)
    }
}