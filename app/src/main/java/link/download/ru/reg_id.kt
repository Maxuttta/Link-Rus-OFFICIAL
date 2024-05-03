package link.download.ru

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import link.download.ru.databinding.ActivityRegIdBinding

@Suppress("DEPRECATION")
class Regid : AppCompatActivity() {

    private lateinit var binding: ActivityRegIdBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegIdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        next()

    }

    private fun next() {
        binding.apply {
            next.setOnClickListener {
                if ((IdEditText.text.toString() == "") || (IdEditText.text.length < 3)) {
                    if (IdEditText.text.toString() == "") {
                        Toast.makeText(this@Regid, "Заполните поле ввода", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(this@Regid, "Слишком короткий ID \n Минимальная длина - 3", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val id = IdEditText.text.toString()
                    var count = 0

                    val sharedPref2 = getSharedPreferences("login", Context.MODE_PRIVATE)
                    val num = sharedPref2.getString("phone", "noPhone").toString()

                    val dbRef = FirebaseDatabase.getInstance().getReference("Users").child("7$num")
                    dbRef.addValueEventListener(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val data = snapshot.getValue(UserData::class.java)
                            if (data != null){
                                val checkId= data.password
                                if (checkId == id){
                                    count = 1
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                    if (count != 1){
                        val sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE).edit()
                        sharedPref.putString("id", id).apply()
                        val intent = Intent(this@Regid, Successed_reg::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.from_left, R.anim.to_left)
                        finish()
                    }
                    else {
                        Toast.makeText(this@Regid, "ID занят", Toast.LENGTH_SHORT).show()
                    }
//                    docRef.get()
//                        .addOnSuccessListener { documents ->
//                            for (document in documents) {
//                                Log.d(TAG, "${document.id} => ${document.data}")
//                                val oId = document["Id"].toString()
//                                if (id.equals(oId)){
//                                    count = 1
//                                }
//                            }
//                            if (count != 1){
//                                val sharedPref = getSharedPreferences("userReg", Context.MODE_PRIVATE).edit()
//                                sharedPref.putString("userRegId", id).apply()
//                                val intent = Intent(this@reg_id, successed_reg::class.java)
//                                startActivity(intent)
//                                overridePendingTransition(R.anim.from_left, R.anim.to_left)
//                                finish()
//                            }else{
//                                Toast.makeText(this@reg_id, "ID занят", Toast.LENGTH_SHORT).show()
//                            }
//                        }.addOnFailureListener{
//                            Toast.makeText(this@reg_id, "Ошибка, попробуйте позже", Toast.LENGTH_SHORT).show()
//                        }
//                }
                }
            }
        }
    }

}