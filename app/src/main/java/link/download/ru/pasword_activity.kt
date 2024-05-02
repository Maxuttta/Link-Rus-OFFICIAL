package link.download.ru

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import link.download.ru.databinding.ActivityPaswordBinding

class pasword_activity : AppCompatActivity() {

    private lateinit var binding: ActivityPaswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        reWrite()
        setNum()
        checkPassword()

    }

    private fun checkPassword() {
        binding.apply {
            val sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE)
            val pas = sharedPref.getString("password","")
            next.setOnClickListener {
                var password = pasEditText.text.toString()
                if (password.equals(pas.toString())){
                    val intent = Intent(this@pasword_activity, successedLogin::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.from_left, R.anim.to_left)
                    finish()
                }
                else {
                    Toast.makeText(this@pasword_activity, "Неверный пароль", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setNum() {
        binding.apply {
            val sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE)
            val num = sharedPref.getString("phone","-")
            textView2.setText("Номер телефона: +$num")
        }
    }

    private fun reWrite() {
        binding.apply {
            textView3.setOnClickListener {
                val intent = Intent(this@pasword_activity, Phone::class.java)
                overridePendingTransition(R.anim.from_right, R.anim.to_right)
                startActivity(intent)
                finish()
            }
        }
    }
}