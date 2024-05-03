package link.download.ru

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import link.download.ru.databinding.ActivityRegUsernameBinding

@Suppress("DEPRECATION")
class Reg_username : AppCompatActivity() {

    private lateinit var binding: ActivityRegUsernameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegUsernameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        next()

    }

    private fun next() {
        binding.apply {
            next.setOnClickListener {
                if (nameEditText.text.toString()!=""){
                    val name = nameEditText.text.toString()
                    val sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE).edit()
                    sharedPref.putString("name", name).apply()
                    val intent = Intent(this@Reg_username, Regpassword::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.from_left, R.anim.to_left)
                    finish()
                }
                else{
                    Toast.makeText(this@Reg_username,"Заполните поле ввода", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}