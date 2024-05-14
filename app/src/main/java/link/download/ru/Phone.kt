package link.download.ru

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.database.FirebaseDatabase
import link.download.ru.databinding.ActivityPhoneBinding


@Suppress("DEPRECATION", "NAME_SHADOWING")
class Phone : AppCompatActivity() {

    private lateinit var binding: ActivityPhoneBinding

    var text = ""
    private var number = ""

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        phoneAdapter()
        num()
    }
    @SuppressLint("CommitPrefEdits", "SetTextI18n")
    private fun num() {
        binding.apply {
            next.setOnClickListener {
                number = ""
                if (phoneEditText.text.length < 13) {
                    Toast.makeText(this@Phone, "Введите корректный \n номер телефона", Toast.LENGTH_SHORT).show()
                } else {
                    val num = phoneEditText.text.toString()
                    num.trimMargin()
                    for (char in num) {
                        if (char.isDigit()) {
                            number += char
                        }
                    }
                    val dialog = Dialog(this@Phone)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setCancelable(false)
                    dialog.setContentView(R.layout.phone_dialog)
                    val body = dialog.findViewById(R.id.body) as TextView
                    body.text = "Проверка номера телефона"
                    val textView = dialog.findViewById(R.id.num) as TextView
                    textView.text = "+7 $num ?"
                    val positive = dialog.findViewById(R.id.positive) as CardView
                    positive.setOnClickListener {
                        val dbRef2 = FirebaseDatabase.getInstance().getReference("Users").child("7$number")

                        dbRef2.get().addOnSuccessListener {snapshot ->
                            val data = snapshot.getValue(UserData::class.java)
                            val id = data?.Id
                            val pas = data?.password
                            val name = data?.name
                            val phone = data?.phone

                            val sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE).edit()
                            sharedPref.putString("password", pas).apply()
                            sharedPref.putString("phone", phone).apply()
                            sharedPref.putString("id", id).apply()
                            sharedPref.putString("name", name).apply()
                            if (pas != null) {
                                if (pas.isNotEmpty()) {
                                    val intent = Intent(this@Phone, Pasword_activity::class.java)
                                    startActivity(intent)
                                    overridePendingTransition(
                                        R.anim.from_left,
                                        R.anim.to_left
                                    )
                                    finish()
                                }
                            }
                            else{
                                val sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE).edit()
                                sharedPref.putString("phone", number).apply()
                                val intent = Intent(this@Phone, Reg_username::class.java)
                                startActivity(intent)
                                overridePendingTransition(R.anim.from_left, R.anim.to_left)
                                finish()
                            }
                        }
                    }
                    val negative = dialog.findViewById(R.id.negative) as CardView
                    negative.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()
                }
            }
        }
    }
    private fun phoneAdapter() {
        binding.apply {
            phoneEditText.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        }
    }
}