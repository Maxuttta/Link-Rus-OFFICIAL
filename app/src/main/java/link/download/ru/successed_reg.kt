package link.download.ru

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import link.download.ru.databinding.ActivitySuccessedRegBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Suppress("UNCHECKED_CAST", "DEPRECATION")
class Successed_reg : AppCompatActivity() {

    private lateinit var binding: ActivitySuccessedRegBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuccessedRegBinding.inflate(layoutInflater)
        setContentView(binding.root)

        finishReg()

    }

    private fun finishReg() {
        binding.apply {
            next.setOnClickListener {
                val sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE)
                val sharedPref2 = getSharedPreferences("login", Context.MODE_PRIVATE).edit()


                val name = sharedPref.getString("name","sr")
                val pas = sharedPref.getString("password","sr")
                val phone = sharedPref.getString("phone", "sr")
                val id = sharedPref.getString("id", "sr")

                sharedPref2.putString("id", "$id").apply()
                sharedPref2.putString("name", "$name").apply()
                sharedPref2.putString("phone", "7$phone").apply()
                sharedPref2.putString("isLog", "true").apply()


                val userData = hashMapOf(
                    "name" to name,
                    "password" to pas,
                    "Id" to id,
                    "phone" to "7$phone",
                    "icon" to "url",
                    "status" to "???",
                    "time" to "???",
                    "icon1" to "",
                    "icon2" to ""
                )

                val dbRef = FirebaseDatabase.getInstance().getReference("Users")
                dbRef.child("7$phone").updateChildren(userData as Map<String, Any>)

                val intent = Intent(this@Successed_reg, Listdrawer::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.from_left, R.anim.to_left)
                finish()
            }
        }
    }
}