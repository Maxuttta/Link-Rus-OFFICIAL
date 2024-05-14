package link.download.ru

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import link.download.ru.databinding.ActivityContactBinding

class contact : AppCompatActivity() {
    private lateinit var binding: ActivityContactBinding
    private lateinit var adapter1: ContactAdapter
    private lateinit var adapter2: ContactAdapter

    var id = ""
    var name = ""
    var phone = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter1 = ContactAdapter()
        adapter2 = ContactAdapter()
        binding.recyclerView1.layoutManager = LinearLayoutManager(this@contact)
        binding.recyclerView2.layoutManager = LinearLayoutManager(this@contact)
        binding.recyclerView1.adapter = adapter1
        binding.recyclerView2.adapter = adapter2

        getContacts()
        viewAdapter()

    }

    private fun viewAdapter() {
        binding.apply {
            back.setOnClickListener {
                val intent = Intent(this@contact, Listdrawer::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.from_right, R.anim.to_right)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@contact, Listdrawer::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.from_left, R.anim.to_left)
    }

    @SuppressLint("Range")
    private fun getContacts() {

        val contactsList = mutableListOf<ContactData>()
        val contentResolver = this.contentResolver
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        )

        cursor?.use { c ->
            while (c.moveToNext()) {
                var a = 0
                val id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID))
                var name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                var phone = ""
                val hasPhone =
                    c.getInt(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0
                if (hasPhone) {
                    val phoneCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    phoneCursor?.use { pc ->
                        if (pc.moveToFirst()) {
                            phone =
                                pc.getString(pc.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            phone = formatPhoneNumber(phone)
                        }
                    }
                }
                val contact = ContactData(id, name, phone)
                adapter1.addItem(contact)
//                val dbRef = FirebaseDatabase.getInstance().getReference("Users").child(phone)
//                dbRef.get().addOnSuccessListener {snapshot ->
//                    if (snapshot.exists()) {
//                        val data = snapshot.getValue(UserData::class.java)
//                        name = data?.name.toString()
//                        phone = data?.phone.toString()
//                        val contact = ContactData(id, name, phone)
//                        adapter1.addItem(contact)
//                    } else {
//                        val contact = ContactData(id, name, phone)
//                        adapter1.addItem(contact)
//                    }
//                }
            }
        }
    }
    fun formatPhoneNumber(phoneNumber: String): String {
        return phoneNumber.replace("(", "")
            .replace(")", "")
            .replace("-", "")
            .replace(" ", "")
    }
}