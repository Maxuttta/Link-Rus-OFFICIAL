package link.download.ru

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Link_Splash)
        setContentView(R.layout.activity_splash)
        val intent = Intent(this@SplashActivity, Listdrawer::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.to_top1, R.anim.to_top2)
        finish()
    }
}