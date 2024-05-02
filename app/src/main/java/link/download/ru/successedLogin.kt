package link.download.ru

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import link.download.ru.databinding.ActivitySuccessedLoginBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Suppress("DEPRECATION")
class successedLogin : AppCompatActivity() {

    private lateinit var binding: ActivitySuccessedLoginBinding

    private val db = Firebase.firestore

    var name = "errorSL"
    var id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuccessedLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            next.setOnClickListener {
                val sharedPref2 = getSharedPreferences("login", Context.MODE_PRIVATE).edit()
                sharedPref2.putString("isLog", "true").apply()
                val intent = Intent(this@successedLogin, list_drawer::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.from_left, R.anim.to_left)
                finish()
            }
        }
    }
}