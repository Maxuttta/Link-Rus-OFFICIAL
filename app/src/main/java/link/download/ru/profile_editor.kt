package link.download.ru

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import link.download.ru.databinding.ActivityProfileEditorBinding
import java.io.ByteArrayOutputStream
import java.io.File

class profile_editor : AppCompatActivity() {
    lateinit var binding: ActivityProfileEditorBinding
    private lateinit var dbRef: DatabaseReference

    var phone = ""
    var id = ""
    var name = ""
    var avaUrl = ""
    var password = ""
    var i1 = ""
    var i2 = ""

    var isAvaChanged = false

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImageUri = result.data?.data
                val image: ImageView = findViewById(R.id.avaPic)
                Picasso.get().load(selectedImageUri).into(image)
                isAvaChanged = true
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityProfileEditorBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE)
        phone = sharedPref.getString("phone", "noPhone").toString()
        id = sharedPref.getString("id", "id").toString()
        avaUrl = sharedPref.getString("avaUrl", "avaUrl").toString()
        name = sharedPref.getString("name", "name").toString()
        password = sharedPref.getString("password", "name").toString()

        designAdapter()
        binding.apply {

            cardView14.setOnClickListener {
                openGallery()
            }

            backk.setOnClickListener{
                val intent = Intent(this@profile_editor, Settings::class.java)
                val options = ActivityOptions.makeSceneTransitionAnimation(
                    this@profile_editor,
                    cardView14,
                    "userAva"
                )
                startActivity(intent, options.toBundle())
            }
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference.child("Avatars/$phone")
            val file = File(this@profile_editor.filesDir, "$phone")
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.path)
                avaPic.setImageBitmap(bitmap)
            }
            else{
                storageRef.getFile(file).addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(file.path)
                    avaPic.setImageBitmap(bitmap)
                }
            }
            nameText.setText(name)
            idText.setText(id)
            passwordText.setText(password)

            done.setOnClickListener{

                val newName = nameText.text.toString()
                val newPass = passwordText.text.toString()
                val newId = idText.text.toString()

                if (newId == id){
                    id = id
                }else{
                    id = newId
                }

                if (newPass == password){
                    password = password
                }else{
                    password = newPass
                }

                if (newName == name){
                    name = name
                }else{
                    name = newName
                }

                if (isAvaChanged){
                    val storageRef = FirebaseStorage.getInstance().reference.child("Avatars/$phone")
                    val bitmap = (avaPic.drawable as BitmapDrawable).bitmap
                    val baos = ByteArrayOutputStream()
                    val scaledBitmap2 = Bitmap.createScaledBitmap(bitmap, 400, 400, true)
                    scaledBitmap2.compress(Bitmap.CompressFormat.PNG, 50, baos)
                    val data = baos.toByteArray()
                    val uploadFile = storageRef.putBytes(data)
                    uploadFile.addOnSuccessListener {
                        Log.d("123123123123", "Upload is done")
                    }.addOnFailureListener {
                        Log.d("123123123123", "Upload is notdone")
                    }
                }

                if (isAvaChanged){
                    i1 = generateRandomString()
                }
                val map = hashMapOf<String, Any>(
                    "Id" to id,
                    "name" to name,
                    "password" to password,
                    "icon" to phone,
                    "icon1" to i1,
                    "icon2" to i2
                )
                dbRef = FirebaseDatabase.getInstance().getReference("Users")
                dbRef.child(phone).updateChildren(map)
                    .addOnSuccessListener {
                        Toast.makeText(this@profile_editor,"Данные обновлены",Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(this@profile_editor,"Ошибка",Toast.LENGTH_SHORT).show()
                    }
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
        val intent = Intent(this@profile_editor, Settings::class.java)
        val options = ActivityOptions.makeSceneTransitionAnimation(
            this@profile_editor,
            binding.cardView14,
            "userAva"
        )
        startActivity(intent, options.toBundle())
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getContent.launch(intent)
    }
    private fun generateRandomString(): String {
        return "${System.currentTimeMillis()}"
    }
}