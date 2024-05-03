package link.download.ru

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import link.download.ru.databinding.ActivityRegPasswordBinding

class Regpassword : AppCompatActivity() {

    lateinit var binding: ActivityRegPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        next()

    }

    private fun next() {
        binding.apply {
            next.setOnClickListener {
                if ((pasEditText.text.toString()=="") || (pasEditText.text.length < 6)){
                    if (pasEditText.text.toString()==""){
                        Toast.makeText(this@Regpassword,"Заполните поле ввода", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@Regpassword,"Слишком короткий пароль \n Минимальная длина - 6", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    val pas = pasEditText.text.toString()
                    val sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE).edit()
                    sharedPref.putString("password", pas).apply()
                    val intent = Intent(this@Regpassword, Regid::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.from_left, R.anim.to_left)
                    finish()
                }
            }
        }
    }
}