package dam.pmdm.spyrothedragon.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dam.pmdm.spyrothedragon.MagicCetroView
import dam.pmdm.spyrothedragon.R
import dam.pmdm.spyrothedragon.models.Character

class CharactersAdapter(
    private val list: List<Character>
) : RecyclerView.Adapter<CharactersAdapter.CharactersViewHolder>() {

    private val characterImages = mapOf(
        "spyro" to R.drawable.spyro,
        "hunter" to R.drawable.hunter,
        "elora" to R.drawable.elora,
        "ripto" to R.drawable.ripto
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharactersViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview, parent, false)
        return CharactersViewHolder(view)
    }

    override fun onBindViewHolder(holder: CharactersViewHolder, position: Int) {
        val character = list[position]
        holder.nameTextView.text = character.name

        val drawableRes = characterImages[character.image] ?: R.drawable.placeholder
        holder.imageImageView.setImageResource(drawableRes)

        /**
         * NUEVA LÓGICA EASTER EGG
         */
        holder.itemView.setOnLongClickListener {
            // Comprobamos si el personaje es Ripto (ignoro mayúsculas por seguridad)
            if (character.name.equals("Ripto", ignoreCase = true)) {
                val context = it.context

                // Creamos nuestra vista de Canvas
                val magicView = MagicCetroView(context)

                // La mostramos en un diálogo transparente para que parezca que flota
                val dialog = androidx.appcompat.app.AlertDialog.Builder(context)
                    .setTitle("¡Poder del Cetro!")
                    .setView(magicView)
                    .setPositiveButton("¡Cuidado!", null)
                    .show()

                // Ajustamos el tamaño del diálogo para que se vea la animación
                dialog.window?.setLayout(800, 800)

                // Iniciamos la magia
                magicView.startMagic()

                true // Indica que consumimos el evento
            } else {
                false // Si no es Ripto, no hace nada
            }
        }
    }

    override fun getItemCount(): Int = list.size

    class CharactersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.name)
        val imageImageView: ImageView = itemView.findViewById(R.id.image)
    }
}
