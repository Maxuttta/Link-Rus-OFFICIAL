package link.download.ru

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import link.download.ru.databinding.ActivityWorkspaceBinding

class workspace : AppCompatActivity() {
    lateinit var binding: ActivityWorkspaceBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var dbRef2: DatabaseReference
    var text = ""
    var chatId = ""
    var a = ""
    var id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityWorkspaceBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            val sharedPref = getSharedPreferences("workspace", Context.MODE_PRIVATE)
            text = sharedPref.getString("text", "no text").toString()
            chatId = sharedPref.getString("chat", "no chat").toString()
            id = sharedPref.getString("id", "no id").toString()
            workspace.setText(text)

            workspace.addTextChangedListener {
                a = workspace.text.toString()
                val map = hashMapOf<String, Any>(
                    "title" to a
                )
                dbRef = FirebaseDatabase.getInstance().getReference("Chats")
                dbRef.child(chatId).child(id).updateChildren(map)
                    .addOnSuccessListener {
                        //отправлено
                    }.addOnFailureListener {
                        //не отправлено
                    }
            }
            val dbRef2 = FirebaseDatabase.getInstance().getReference("Chats").child(chatId)
            dbRef2.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val data = snapshot.getValue(Message::class.java)
                    val idd = data?.messageId.toString()
                    if (idd == id){
                        val textt = data?.title
                        workspace.setText(textt)
                        workspace.setSelection(workspace.text.length)
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@workspace, chatWindow::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.from_right, R.anim.to_right)
        finish()
    }
}