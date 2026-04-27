package dam.pmdm.spyrothedragon

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

/**
 * Actividad encargada de reproducir el vídeo secreto (Easter Egg).
 */
class VideoEasterEggActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_easter_egg)

        val videoView = findViewById<VideoView>(R.id.videoViewEasterEgg)

        // Ruta al vídeo en res/raw
        val videoPath = "android.resource://" + packageName + "/" + R.raw.spyro_video
        val uri = Uri.parse(videoPath)

        videoView.setVideoURI(uri)

        // Añadimos controles (play, pause, etc.)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        videoView.start()

        // Al terminar el vídeo, cerramos la actividad
        videoView.setOnCompletionListener {
            finish()
        }
    }
}