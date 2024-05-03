package link.download.ru

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import link.download.ru.databinding.ActivityListDrawerBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mxn.soul.flowingdrawer_core.ElasticDrawer


@Suppress("DEPRECATION", "NAME_SHADOWING")
class list_drawer : AppCompatActivity(), chatListAdapter.Listener {
    private lateinit var binding: ActivityListDrawerBinding
    private val adapter = chatListAdapter(this)

//    var newList:MutableList<chat> = mutableListOf()
//    var old:MutableList<chat> = mutableListOf<chat>()

    private val db = Firebase.firestore
    private lateinit var dbRef: DatabaseReference
    private lateinit var childEventListener: ChildEventListener

    private var isLog = ""
    var name = ""
    var id = ""
    var phone = ""

    private var c = "false"
    var chatIs = 0

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

        toLeft2 = AnimationUtils.loadAnimation(this, R.anim.to_left2)
        toLeft1 = AnimationUtils.loadAnimation(this, R.anim.to_left3)
        fadeRotate1 = AnimationUtils.loadAnimation(this, R.anim.fade_rotate1)
        fadeRotate2 = AnimationUtils.loadAnimation(this, R.anim.fade_rotate2)
        toTop1 = AnimationUtils.loadAnimation(this, R.anim.to_top1)
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        toBottomFadeOut = AnimationUtils.loadAnimation(this, R.anim.to_bottom_fade_out)

        val sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE)

        isLog = sharedPref.getString("isLog", "false").toString()
        id = sharedPref.getString("id", "noId").toString()
        phone = sharedPref.getString("phone", "noPhone").toString()
        name = sharedPref.getString("name", "noName").toString()

        binding.flowButton1.setImageResource(R.drawable.ic_menu_white)

        if (isLog == "false") {
            val intent = Intent(this@list_drawer, hello::class.java)
            startActivity(intent)
            finish()
        } else {
            floatingDrawer()
            loadChats()
            searchView()
            designAdapter()
            menuButtonsAdapter()
            val intent = Intent(this@list_drawer, MyService::class.java)
            startForegroundService(intent)



        }
//        Toast.makeText(this@list_drawer,"$isLog",Toast.LENGTH_SHORT).show()
    }

    private fun menuButtonsAdapter() {
        binding.apply {
            addGroupeButton.setOnClickListener{

            }
            contactButton.setOnClickListener {

            }
            settingsButton.setOnClickListener {

            }
            savedButton.setOnClickListener {
                val key = id
                val cophone = phone
                val coname = name
                chatIs = 1
                val sharedPref = getSharedPreferences("chatInfo", Context.MODE_PRIVATE).edit()
                sharedPref.putString("chatName", coname).apply()
                sharedPref.putString("chatPhone", cophone).apply()
                sharedPref.putString("chatKey", key).apply()
                val intent = Intent(this@list_drawer, ChatWindow::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.from_left, R.anim.to_left)
            }
        }
    }

    private fun designAdapter() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
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
                                var data = snapshot.getValue(UserData::class.java)
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
                                            Intent(this@list_drawer, ChatWindow::class.java)
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
//                        val docRef1 = db.collection("Users").document(searchForChat)
//                        docRef1.get()
//                            .addOnSuccessListener { document ->
//                                if (document != null) {
//                                    Log.d(ContentValues.TAG, "counter data: ${document.data}")
//                                    val name = document.get("name").toString()
//                                    val key = document.get("Id").toString()
//                                    val phone = document.get("phone").toString()
//
//                                    val sharedPref = getSharedPreferences("chatInfo", Context.MODE_PRIVATE).edit()
//                                    sharedPref.putString("chatName", name).apply()
//                                    sharedPref.putString("chatKey", phone).apply()
//
//                                    sharedPref.putString("key", key).apply()
//
//                                    val intent = Intent(this@list_drawer, ChatWindow::class.java)
//                                    startActivity(intent)
//                                    overridePendingTransition(R.anim.from_left, R.anim.to_left)
//                                } else {
//                                    Log.d(ContentValues.TAG, "No such document")
//                                }
//
//                            }
//                            .addOnFailureListener {
//                                Toast.makeText(this@list_drawer, "Ошибка", Toast.LENGTH_SHORT).show()
//                            }
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
                    drawerlayout.toggleMenu()
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
        binding.chatRecyclerList.layoutManager = LinearLayoutManager(this@list_drawer)
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

    private fun floatingDrawer() {
        binding.drawerlayout.setTouchMode(ElasticDrawer.TOUCH_MODE_FULLSCREEN)
        binding.drawerlayout.setOnDrawerStateChangeListener(object :
            ElasticDrawer.OnDrawerStateChangeListener {
            override fun onDrawerStateChange(oldState: Int, newState: Int) {
                if (newState == ElasticDrawer.STATE_CLOSED) {
                    binding.darker.visibility = View.INVISIBLE
                }
                else{
                    binding.darker.visibility = View.VISIBLE
                }
            }

            override fun onDrawerSlide(openRatio: Float, offsetPixels: Int) {
                Log.i("MainActivity", "openRatio=$openRatio ,offsetPixels=$offsetPixels")
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (binding.drawerlayout.isMenuVisible) {
            binding.drawerlayout.closeMenu()
        }
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

    override fun onClick(chat: chat) {
        val key = chat.id
        val cophone = chat.phone
        val coname = chat.name
        chatIs = 1
//        Toast.makeText(this, "$cophone  $key  $coname", Toast.LENGTH_SHORT).show()
        val sharedPref = getSharedPreferences("chatInfo", Context.MODE_PRIVATE).edit()
        sharedPref.putString("chatName", coname).apply()
        sharedPref.putString("chatPhone", cophone).apply()
        sharedPref.putString("chatKey", key).apply()
        val intent = Intent(this@list_drawer, ChatWindow::class.java)
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
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStop() {
        val sharedPre = getSharedPreferences("system", Context.MODE_PRIVATE)
        c = sharedPre.getString("isNotification", "-").toString()
        if (c == "true"){
        }
        if (c == "false"){
//            val intent = Intent(this@list_drawer, MyService::class.java)
//            startForegroundService(intent)
//            val sharedPref = getSharedPreferences("system", Context.MODE_PRIVATE).edit()
//            sharedPref.putString("isNotification", "true").apply()
        }
        super.onStop()

        if (chatIs == 1) {
            val sharedPref = getSharedPreferences("system", Context.MODE_PRIVATE).edit()
            sharedPref.putString("chatListIs", "false").apply()
            val dbRef = FirebaseDatabase.getInstance().getReference("Users")
            val chatData = hashMapOf(
                "status" to "online",
                "time" to ""
            )
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
}