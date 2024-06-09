package link.download.ru

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import link.download.ru.databinding.ActivitySuccessedLoginBinding

@Suppress("DEPRECATION")
class SuccessedLogin : AppCompatActivity() {

    private lateinit var binding: ActivitySuccessedLoginBinding

    var name = "errorLogin"
    var id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuccessedLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            next.setOnClickListener {
                val sharedPref2 = getSharedPreferences("login", Context.MODE_PRIVATE).edit()
                sharedPref2.putString("isLog", "true").apply()
                val intent = Intent(this@SuccessedLogin, Listdrawer::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.from_left, R.anim.to_left)
                finish()
            }
        }
    }
}