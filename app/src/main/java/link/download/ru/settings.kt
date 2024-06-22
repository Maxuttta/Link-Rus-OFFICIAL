package link.download.ru

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import link.download.ru.databinding.ActivityListDrawerBinding
import link.download.ru.databinding.ActivitySettingsBinding
import java.io.File

class Settings : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    var phone = ""
    var id = ""
    var name = ""
    var avaUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE)
        phone = sharedPref.getString("phone", "noPhone").toString()

        viewAdapter()
        designAdapter()

    }

    private fun viewAdapter() {
        binding.apply {

            edit.setOnClickListener {
                val intent = Intent(this@Settings, profile_editor::class.java)
                val options = ActivityOptions.makeSceneTransitionAnimation(
                    this@Settings,
                    cardView9,
                    "userAva"
                )
                startActivity(intent, options.toBundle())
            }

            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference.child("Avatars/$phone")
            val file = File(this@Settings.filesDir, "$phone")
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.path)
                avaImage.setImageBitmap(bitmap)
            }
            else{
                storageRef.getFile(file).addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(file.path)
                    avaImage.setImageBitmap(bitmap)
                }
            }

            val dbRef = FirebaseDatabase.getInstance().getReference("Users")
            dbRef.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val data = snapshot.getValue(UserData::class.java)
                    if (data != null) {
                        val i = data.phone
                        if (phone == i) {
                            name = data.name.toString()
                            id = data.Id.toString()
                            avaUrl = data.phone.toString()
                            nameText.text = name
                            idText.text = id

                            val storage = FirebaseStorage.getInstance()
                            val storageRef = storage.reference.child("Avatars/$avaUrl")
                            val file = File(this@Settings.filesDir, avaUrl)
                            if (file.exists()) {
                                val bitmap = BitmapFactory.decodeFile(file.path)
                                if (bitmap != null) {
                                    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true)
                                    avaImage.setImageBitmap(scaledBitmap)
                                }
                            }
                            else {
                                storageRef.getFile(file).addOnSuccessListener {
                                    val bitmap = BitmapFactory.decodeFile(file.path)
                                    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true)
                                    avaImage.setImageBitmap(scaledBitmap)
                                }
                            }


                        }
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val data = snapshot.getValue(UserData::class.java)
                    if (data != null) {
                        val i = data.phone
                        if (phone == i) {
                            name = data.name.toString()
                            id = data.Id.toString()
                            avaUrl = data.phone.toString()
                            nameText.text = name
                            idText.text = id

                            val storage = FirebaseStorage.getInstance()
                            val storageRef = storage.reference.child("Avatars/$avaUrl")
                            val file = File(this@Settings.filesDir, avaUrl)
                            if (file.exists()) {
                                val bitmap = BitmapFactory.decodeFile(file.path)
                                if (bitmap != null) {
                                    val scaledBitmap =
                                        Bitmap.createScaledBitmap(bitmap, 400, 400, true)
                                    avaImage.setImageBitmap(scaledBitmap)
                                }
                            } else {
                                storageRef.getFile(file).addOnSuccessListener {
                                    val bitmap = BitmapFactory.decodeFile(file.path)
                                    val scaledBitmap =
                                        Bitmap.createScaledBitmap(bitmap, 400, 400, true)
                                    avaImage.setImageBitmap(scaledBitmap)
                                }
                            }


                        }
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

            back.setOnClickListener {
                super.onBackPressed()
                val intent = Intent(this@Settings, Listdrawer::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.from_left, R.anim.to_left)
            }
        }
    }
    private fun designAdapter() {
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            controller?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@Settings, Listdrawer::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.from_left, R.anim.to_left)
    }
}