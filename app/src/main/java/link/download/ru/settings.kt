package link.download.ru

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import link.download.ru.databinding.ActivityListDrawerBinding
import link.download.ru.databinding.ActivitySettingsBinding

class Settings : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewAdapter()
    }

    private fun viewAdapter() {
        binding.apply {
            back.setOnClickListener {
                val intent = Intent(this@Settings, Listdrawer::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.from_left, R.anim.to_left)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@Settings, Listdrawer::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.from_left, R.anim.to_left)
    }
}