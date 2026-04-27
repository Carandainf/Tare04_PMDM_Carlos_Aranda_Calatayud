package dam.pmdm.spyrothedragon.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dam.pmdm.spyrothedragon.R
import dam.pmdm.spyrothedragon.VideoEasterEggActivity
import dam.pmdm.spyrothedragon.models.World

class WorldsAdapter(
    private val list: List<World>
) : RecyclerView.Adapter<WorldsAdapter.WorldsViewHolder>() {

    // Variables para el Easter Egg
    private var clickCount = 0
    private var lastClickTime: Long = 0

    private val worldImages = mapOf(
        "sunny_beach" to R.drawable.sunny_beach,
        "midday_gardens" to R.drawable.midday_gardens,
        "autumn_plains" to R.drawable.autumn_plains,
        "glimmer" to R.drawable.glimmer,
        "cloud_spires" to R.drawable.cloud_spires,
        "hurricane_halls" to R.drawable.hurricane_halls,
        "frozen_altars" to R.drawable.frozen_altars,
        "lost_fleet" to R.drawable.lost_fleet,
        "sunset_beach" to R.drawable.sunset_beach
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorldsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview, parent, false)
        return WorldsViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorldsViewHolder, position: Int) {
        val world = list[position]
        holder.nameTextView.text = world.name

        val drawableRes = worldImages[world.image] ?: R.drawable.placeholder
        holder.imageImageView.setImageResource(drawableRes)

        /**
         * Lógica del Easter Egg: Detectar 3 clics rápidos
         */
        holder.itemView.setOnClickListener {
            val currentTime = System.currentTimeMillis()

            // Si pasa más de 500ms entre clics, reinicio el contador
            if (currentTime - lastClickTime > 500) {
                clickCount = 0
            }

            clickCount++
            lastClickTime = currentTime

            when (clickCount) {
                1 -> { /* Primer clic, no hacemos nada */ }
                2 -> { /* Segundo clic, seguimos sin hacer nada */ }
                3 -> {
                    clickCount = 0 // Reset
                    val context = it.context
                    val intent = Intent(context, VideoEasterEggActivity::class.java)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int = list.size

    class WorldsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.name)
        val imageImageView: ImageView = itemView.findViewById(R.id.image)
    }
}
