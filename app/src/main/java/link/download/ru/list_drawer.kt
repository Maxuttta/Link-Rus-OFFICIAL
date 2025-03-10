package link.download.ru

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsetsController
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet.Layout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import link.download.ru.databinding.ActivityListDrawerBinding
import java.io.File


@Suppress("DEPRECATION")
class Listdrawer : AppCompatActivity(), chatListAdapter.Listener {
    private lateinit var binding: ActivityListDrawerBinding
    private val adapter = chatListAdapter(this,this)


    private lateinit var dbRef: DatabaseReference
    private lateinit var childEventListener: ChildEventListener

    private var isLog = ""
    var name = ""
    var id = ""
    var phone = ""

    private var c = "false"
    private var chatIs = 0

    private lateinit var toLeft1: Animation
    private lateinit var toLeft2: Animation
    private lateinit var fadeRotate1: Animation
    private lateinit var fadeRotate2: Animation
    private lateinit var toTop1: Animation
    private lateinit var fadeOut: Animation
    private lateinit var fadeIn: Animation
    private lateinit var toBottomFadeOut: Animation

    private var isFlow = true

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPre = getSharedPreferences("system", Context.MODE_PRIVATE)
        c = sharedPre.getString("chatListIs", "false").toString()

//
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_CONTACTS), 1)
        } else {
            // Permission already granted
        }


        toLeft2 = AnimationUtils.loadAnimation(this, R.anim.to_left2)
        toLeft1 = AnimationUtils.loadAnimation(this, R.anim.to_left3)
        fadeRotate1 = AnimationUtils.loadAnimation(this, R.anim.fade_rotate1)
        fadeRotate2 = AnimationUtils.loadAnimation(this, R.anim.fade_rotate2)
        toTop1 = AnimationUtils.loadAnimation(this, R.anim.to_top1)
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        toBottomFadeOut = AnimationUtils.loadAnimation(this, R.anim.to_bottom_fade_out)
        val intent = Intent(this@Listdrawer, MyService::class.java)
        startService(intent)
        val sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE)

        isLog = sharedPref.getString("isLog", "false").toString()
        id = sharedPref.getString("id", "noId").toString()
        phone = sharedPref.getString("phone", "noPhone").toString()
        name = sharedPref.getString("name", "noName").toString()
        binding.flowButton1.setImageResource(R.drawable.ic_menu_white)

        if (isLog == "false") {
            val intent = Intent(this@Listdrawer, Hello::class.java)
            startActivity(intent)
            finish()
        } else {
            loadChats()
            searchView()
            designAdapter()
            menuAdapter()




        }
//        Toast.makeText(this@list_drawer,"$isLog",Toast.LENGTH_SHORT).show()
    }

    private fun menuAdapter() {
        binding.apply {
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference.child("Avatars/$phone")
            val file = File(this@Listdrawer.filesDir, "$phone")
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.path)
                userAvatar.setImageBitmap(bitmap)
            }
            else{
                storageRef.getFile(file).addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(file.path)
                    userAvatar.setImageBitmap(bitmap)
                }
            }

            userName.text = name

            navView.setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.contacts -> {
                        if (ContextCompat.checkSelfPermission(
                                this@Listdrawer,
                                android.Manifest.permission.READ_CONTACTS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                this@Listdrawer,
                                arrayOf(android.Manifest.permission.READ_CONTACTS),
                                1
                            )
                        } else {
                            val intent = Intent(this@Listdrawer, contact::class.java)
                            startActivity(intent)
                            overridePendingTransition(R.anim.from_right, R.anim.to_right)
                        }
                    }

                    R.id.settings -> {
                        val intent = Intent(this@Listdrawer, Settings::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.from_right, R.anim.to_right)
                    }

                    R.id.saved -> {
                        val key = id
                        val cophone = phone
                        val coname = name
                        chatIs = 1
                        val sharedPref =
                            getSharedPreferences("chatInfo", Context.MODE_PRIVATE).edit()
                        sharedPref.putString("chatName", coname).apply()
                        sharedPref.putString("chatPhone", cophone).apply()
                        sharedPref.putString("chatKey", key).apply()
                        val intent = Intent(this@Listdrawer, chatWindow::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.from_left, R.anim.to_left)
                    }
                }
                true
            }
        }
    }
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (и ваше приложение) могут показывать уведомления.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                // Показать пользователю информационный интерфейс, объясняющий, какие функции будут включены при предоставлении разрешения.
            } else {
                // Запросить разрешение
                ActivityCompat.requestPermissions(this@Listdrawer, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
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
    private fun searchView() {
        binding.apply {
            loader()

            searchButton.setOnClickListener {
                if (isFlow) {
                    fadeRotate2.fillAfter = true
                    fadeOut.fillAfter = true
                    toTop1.fillAfter = true
                    fadeIn.fillAfter = true
                    toBottomFadeOut.fillAfter = false
                    searchEditText.visibility = View.VISIBLE
                    constraintSearchList.visibility = View.VISIBLE
                    chatRecyclerList.visibility = View.GONE
                    flowButton1.startAnimation(fadeRotate1)
                    flowButton1.setImageResource(R.drawable.baseline_close_24)
                    flowButton2.setImageResource(R.drawable.ic_menu_white)
                    flowButton2.startAnimation(fadeRotate2)
                    searchEditText.startAnimation(toTop1)
                    title1.startAnimation(fadeOut)
                    constraintSearchList.startAnimation(fadeIn)
                    chatRecyclerList.startAnimation(fadeOut)
                    isFlow = false
                    searchEditText.isEnabled = true
                } else {
                    if (searchEditText.text.isNotEmpty()) {
                        val searchForChat = searchEditText.text.toString()
                        val view: View? = currentFocus
                        if (view != null) {
                            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(view.windowToken, 0)
                        }
                        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
                        dbRef.addChildEventListener(object : ChildEventListener {
                            override fun onChildAdded(
                                snapshot: DataSnapshot,
                                previousChildName: String?
                            ) {
                                val data = snapshot.getValue(UserData::class.java)
                                Log.d("MyApp", "User Data: ${data.toString()}")
                                Log.d("MyApp", "User ID: ${data!!.Id}")
                                Log.d("MyApp", "User Name: ${data.name}")
                                Log.d("MyApp", "User Phone: ${data.phone}")
                                if (data != null) {
                                    val i = data.Id
                                    if (searchForChat == i) {
                                        val name = data.name
                                        val key = data.Id
                                        val phone = data.phone
                                        val sharedPref = getSharedPreferences(
                                            "chatInfo",
                                            Context.MODE_PRIVATE
                                        ).edit()
                                        sharedPref.putString("chatName", name).apply()
                                        sharedPref.putString("chatKey", key).apply()
                                        sharedPref.putString("chatPhone", phone).apply()
                                        sharedPref.putString("key", key).apply()

                                        val intent =
                                            Intent(this@Listdrawer, chatWindow::class.java)
                                        startActivity(intent)
                                        overridePendingTransition(R.anim.from_left, R.anim.to_left)
                                    }
                                }
                            }

                            override fun onChildChanged(
                                snapshot: DataSnapshot,
                                previousChildName: String?
                            ) {

                            }

                            override fun onChildRemoved(snapshot: DataSnapshot) {
                                TODO("Not yet implemented")
                            }

                            override fun onChildMoved(
                                snapshot: DataSnapshot, previousChildName: String?
                            ) {
                                TODO("Not yet implemented")
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }


                        })
                    }
                }
            }
            flowButton2.setOnClickListener {
                if (!isFlow) {
                    val view: View? = currentFocus
                    if (view != null) {
                        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                    fadeRotate2.fillAfter = true
                    fadeOut.fillAfter = true
                    toLeft1.fillAfter = true
                    toBottomFadeOut.fillAfter = false
                    flowButton1.startAnimation(fadeRotate1)
                    flowButton1.setImageResource(R.drawable.ic_menu_white)
                    flowButton2.setImageResource(R.drawable.baseline_close_24)
                    flowButton2.startAnimation(fadeRotate2)
                    searchEditText.startAnimation(fadeOut)
                    searchEditText.visibility = View.INVISIBLE
                    constraintSearchList.visibility = View.GONE
                    chatRecyclerList.visibility = View.VISIBLE
                    constraintSearchList.startAnimation(fadeOut)
                    chatRecyclerList.startAnimation(fadeIn)
                    title1.startAnimation(toLeft1)
                    searchEditText.isEnabled = false
                    isFlow = true
                } else {

                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loader() {
        binding.apply {
            if (c == "false") {
                title1.text = "Загрузка."
                Handler().postDelayed({
                    title1.text = "Загрузка.."
                    Handler().postDelayed({
                        title1.text = "Загрузка..."
                        Handler().postDelayed({
                            loader()
                            c = "true"

                        }, 500)
                    }, 500)
                }, 500)
            } else {
                val sharedPref = getSharedPreferences("system", Context.MODE_PRIVATE).edit()
                sharedPref.putString("chatListIs", "true").apply()
                title1.text = "Link"
                c = "true"
            }
        }
    }

    private fun loadChats() {
        binding.chatRecyclerList.layoutManager = LinearLayoutManager(this@Listdrawer)
        binding.chatRecyclerList.adapter = adapter

        dbRef = FirebaseDatabase.getInstance().getReference("UserChats").child(phone)
        childEventListener = AppChildEventListener { snapshot, eventType ->
                when (eventType){
                    1 -> {
                        adapter.updateItem(snapshot.getChatModel())
                    }

                    2 -> {
                        adapter.updateItem(snapshot.getChatModel())
                    }

                    3 -> {
                        adapter.removeItem(snapshot.getChatModel())
                    }
                }
        }
        dbRef.addChildEventListener(childEventListener)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
//        if (binding.drawerlayout.isMenuVisible) {
//            binding.drawerlayout.closeMenu()
//        }
        if (!isFlow) {
            binding.apply {
                val view: View? = currentFocus
                if (view != null) {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
                fadeRotate2.fillAfter = true
                fadeOut.fillAfter = true
                toLeft1.fillAfter = true
                flowButton1.startAnimation(fadeRotate1)
                flowButton1.setImageResource(R.drawable.ic_menu_white)
                flowButton2.setImageResource(R.drawable.baseline_close_24)
                flowButton2.startAnimation(fadeRotate2)
                searchEditText.startAnimation(fadeOut)
                searchEditText.visibility = View.INVISIBLE
                isFlow = true
                title1.startAnimation(toLeft1)
                searchEditText.isEnabled = false
            }
        }
    }

    override fun onClick(chat: Chat) {
        val key = chat.id
        val cophone = chat.phone
        val coname = chat.name
        chatIs = 1
//        Toast.makeText(this, "$cophone  $key  $coname", Toast.LENGTH_SHORT).show()
        val sharedPref = getSharedPreferences("chatInfo", Context.MODE_PRIVATE).edit()
        sharedPref.putString("chatName", coname).apply()
        sharedPref.putString("chatPhone", cophone).apply()
        sharedPref.putString("chatKey", key).apply()
        val intent = Intent(this@Listdrawer, chatWindow::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.from_left, R.anim.to_left)
    }

    override fun onStart() {
        super.onStart()
        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
        val chatData = hashMapOf(
            "status" to "inChatList",
            "time" to ""
        )
        Handler().postDelayed({
            dbRef.child(phone).updateChildren(chatData as Map<String, Any>)
        }, 500)
        val sharedPref = getSharedPreferences("system", Context.MODE_PRIVATE).edit()
        sharedPref.putString("chatIs", "false").apply()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStop() {
        val sharedPre = getSharedPreferences("system", Context.MODE_PRIVATE)
        c = sharedPre.getString("isNotification", "-").toString()

        val sharedPref = getSharedPreferences("system", Context.MODE_PRIVATE)
//        isChat = sharedPref.getString("chatListIs", "true").toString()
//        if (c == "true"){
//        }
//        if (c == "false"){
//            val intent = Intent(this@Listdrawer, MyService::class.java)
//            startForegroundService(intent)
//            val sharedPref = getSharedPreferences("system", Context.MODE_PRIVATE).edit()
//            sharedPref.putString("isNotification", "true").apply()
//        }
        super.onStop()

        if (chatIs == 1) {
            val sharedPref = getSharedPreferences("system", Context.MODE_PRIVATE).edit()
            sharedPref.putString("chatListIs", "false").apply()
            val dbRef = FirebaseDatabase.getInstance().getReference("Users")
            val chatData = hashMapOf(
                "status" to "online",
                "time" to ""
            )
            chatIs = 0
            dbRef.child(phone).updateChildren(chatData as Map<String, Any>)
        }else{
            val dbRef = FirebaseDatabase.getInstance().getReference("Users")
            val chatData = hashMapOf(
                "status" to "offline",
                "time" to ""
            )
            dbRef.child(phone).updateChildren(chatData as Map<String, Any>)
        }
    }

    override fun onDestroy() {

        super.onDestroy()
    }
}