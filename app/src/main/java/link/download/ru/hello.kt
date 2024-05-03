package link.download.ru
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.cardview.widget.CardView

@Suppress("DEPRECATION")
class Hello : AppCompatActivity() {
    private lateinit var start:CardView
    private lateinit var fromLeft:Animation
    private lateinit var toLeft:Animation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hello)

        fromLeft = AnimationUtils.loadAnimation(applicationContext, R.anim.from_left)
        toLeft = AnimationUtils.loadAnimation(applicationContext, R.anim.to_left)

        start = findViewById(R.id.buttonStart)

        start.setOnClickListener {
            val intent = Intent(this, Phone::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.from_left, R.anim.to_left)
            finish()
        }
    }
}