package link.download.ru

import android.annotation.SuppressLint
import android.app.usage.UsageEvents.Event
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.vanniktech.emoji.google.GoogleEmojiProvider
import com.vanniktech.emoji.EmojiManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import link.download.ru.databinding.ActivityChatWindowBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiPopup
import java.io.ByteArrayOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class chatWindow : AppCompatActivity(), MessageAdapter.ItemClickListener {
    private lateinit var toLeft1: Animation
    private lateinit var toLeft2: Animation
    private lateinit var toRight1: Animation
    private lateinit var toRight2: Animation
    private lateinit var fadeRotate1: Animation
    private lateinit var fadeRotate2: Animation
    private lateinit var toTop: Animation
    private lateinit var fadeOut: Animation
    private lateinit var fadeIn: Animation

    private var a = 1

    private lateinit var binding: ActivityChatWindowBinding
    private lateinit var adapter: MessageAdapter
    private var isEditing = 0

    private var name = ""
    private var key = ""
    private var coname = ""
    private var cophone = ""
    private var phone = ""

    private var textCount = 0
    private var reaction = ""

    private var counterOfMessages = ""
    private var messageId = 1

    private var you = ""

    private var text = ""
    private var textCopy = ""
    private var time = ""
    private var messageType = ""
    private var uri = ""
    private var reText = ""
    private var reId = ""
    private var userId = ""

    private var editingText = ""
    private var edId = ""
    private var delId = ""
    private var newEdText = ""

    private lateinit var childEventListener: ChildEventListener

    private var chatId = ""
    private var isChat = ""
    private var listIs = 0
    private var imagechoosen = false

    private lateinit var dbRef: DatabaseReference
    private lateinit var dbRef2: DatabaseReference
    private lateinit var dbRef3: DatabaseReference

    private val handler = android.os.Handler()

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImageUri = result.data?.data
                val image: ImageView = findViewById(R.id.subImage)
                Picasso.get().load(selectedImageUri).into(image)
                imagechoosen = true
            }
        }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatWindowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toLeft2 = AnimationUtils.loadAnimation(this, R.anim.to_left2)
        toLeft1 = AnimationUtils.loadAnimation(this, R.anim.to_left3)
        fadeRotate1 = AnimationUtils.loadAnimation(this, R.anim.fade_rotate1)
        fadeRotate2 = AnimationUtils.loadAnimation(this, R.anim.fade_rotate2)
        toTop = AnimationUtils.loadAnimation(this, R.anim.to_top1)
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        toRight1 = AnimationUtils.loadAnimation(this, R.anim.to_right)
        toRight2 = AnimationUtils.loadAnimation(this, R.anim.to_right2)


        val view: View? = currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        val sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE)
        name = sharedPref.getString("name", "noNameError").toString()

        chaterId()
        chatInfo()
        chatStatus()
        newMessageLoader()
        back()
        sendMessage()
        chatBar()
        cancelEditingMessage()
        keysListener()
        leftswipe()

        var TextGenerator = TextGenerator()



        binding.messageChatList.layoutManager = LinearLayoutManager(this@chatWindow)
        binding.messageChatList.adapter = adapter
    }

    private fun keysListener() {
        binding.messageChatList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val isScrollingLeft = dx < 0 && firstVisibleItemPosition > 0
                if (isScrollingLeft) {
                    finish()
                }
            }
        })
    }

    private fun chatStatus() {
        val dbRef2 = FirebaseDatabase.getInstance().getReference("Users").child(cophone)
        dbRef2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(OnlineData::class.java)

                if (data != null) {
                    val status = data.status
//                    val time = data.time

                    if (status == "online") {
                        binding.chatStatusText.text = "В сети"
                        binding.statusChat.setBackgroundResource(R.color.color_online)
                    }
                    if (status == "inChatList") {
                        binding.chatStatusText.text = "Просматривает чаты"
                        binding.statusChat.setBackgroundResource(R.color.color_online)
                    }
                    if (status == "offline") {
                        binding.statusChat.setBackgroundResource(R.color.color_offline)
                        binding.chatStatusText.text = "Не в сети"
                    }
//                    Toast.makeText(this@ChatWindow, "$cophone $status $time", Toast.LENGTH_SHORT).show()
                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun chatBar() {
        EmojiManager.install(GoogleEmojiProvider())
        val emojiPopup = EmojiPopup(binding.root, binding.editText)
        emojiPopup.dismiss()
        binding.emojiButton.setOnClickListener {
            emojiPopup.toggle()
        }

        binding.editText.addTextChangedListener {
            val text = binding.editText.text.toString()
            if (text.isNotEmpty()) {
                if (textCount == 0) {
                    binding.fileButton.visibility = View.GONE
                    binding.fileButton.startAnimation(fadeOut)
                    textCount = 1
                }
            } else {
                binding.fileButton.visibility = View.VISIBLE
                binding.fileButton.startAnimation(fadeIn)
                textCount = 0
            }
        }
        binding.fileButton.setOnClickListener {
            openGallery()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun leftswipe(){
        binding.apply{
            var startX: Float = 0f
            val swipeDistance = 300
            editText.setOnTouchListener{ v, event ->
                when(event.action){
                    MotionEvent.ACTION_DOWN -> {
                        startX = event.x
                    }
                    MotionEvent.ACTION_MOVE -> {
                        // Отслеживаем движение пальца
                        if ((Math.abs(event.x - startX) > swipeDistance) && (event.x > startX)) {
                            listIs = 1
                            val intent = Intent(this@chatWindow, Listdrawer::class.java)
                            startActivity(intent)
                            overridePendingTransition(R.anim.from_right, R.anim.to_right)
                            finish()
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        startX = 0f
                    }
                }
                true
            }
        }
    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getContent.launch(intent)
    }

    private fun back() {
        binding.apply {
            backButton.setOnClickListener {
                val intent = Intent(this@chatWindow, Listdrawer::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.from_right, R.anim.to_right)
                finish()
            }
        }
    }


    private fun chaterId() {
        val sharedPref = getSharedPreferences("chatInfo", Context.MODE_PRIVATE)
        key = sharedPref.getString("chatPhone", "ERROR").toString()
        val sharedPref2 = getSharedPreferences("login", Context.MODE_PRIVATE)
        you = sharedPref2.getString("phone", "ERROR").toString()
        userId = sharedPref2.getString("id", "noId").toString()

        val n = key
        val nn = you

        val result = n.compareTo(nn)
        if (result > 0) {
            chatId = key + you
        }
        if (result < 0) {
            chatId = you + key
        }
        if (result == 0){
            chatId = you + you
        }
        adapter = MessageAdapter(
            this@chatWindow,
            you,
            this
        )

    }

    private fun chatInfo() {
        binding.apply {
            val sharedPref = getSharedPreferences("chatInfo", Context.MODE_PRIVATE)
            coname = sharedPref.getString("chatName", "").toString()
            cophone = sharedPref.getString("chatPhone", "").toString()

            val sharedPref2 = getSharedPreferences("login", Context.MODE_PRIVATE)
            phone = sharedPref2.getString("phone", "noPhone").toString()
            if (coname!=name){
                if (coname == "Избранное"){
                    avaChat.setBackgroundResource(R.drawable.saves_ava)
                    chatName.text = "Избранное"
                    coname = "Избранное"
                    chatStatusText.visibility = View.GONE
                }
                else{
                    chatName.text = coname
                }
            }
            else{
                avaChat.setBackgroundResource(R.drawable.saves_ava)
                chatName.text = "Избранное"
                coname = "Избранное"
                chatStatusText.visibility = View.GONE

            }
        }
    }

    private fun containsEmojis(text: String): Boolean {
        val regex = "[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+".toRegex()
        return regex.containsMatchIn(text)
    }

    private fun generateRandomString(): String {
        return "image${System.currentTimeMillis()}.jpg"
    }

    private fun sendMessage() {
        adapter = MessageAdapter(this, you, this)
        binding.apply {
            editText.setOnClickListener {
                binding.messageChatList.scrollToPosition(
                    binding.messageChatList.adapter!!.itemCount - 1
                )
            }
            send.setOnClickListener {
                toolConstraint.visibility = View.GONE
                if (isEditing == 1) {
                    if (editText.text.toString() == editingText) {
                        editText2.visibility = View.GONE
                        editText.visibility = View.VISIBLE
                        toolConstraint.visibility = View.GONE
                        isEditing = 0
                        editText2.setText("")
                    } else {
                        newEdText = editText2.text.toString()
                        val map = hashMapOf<String, Any>(
                            "title" to newEdText,
                        )
                        dbRef = FirebaseDatabase.getInstance().getReference("Chats")
                        dbRef.child(chatId).child(edId).updateChildren(map)
                            .addOnSuccessListener {
                                //отправлено
                            }.addOnFailureListener {
                                //не отправлено
                            }
                        editText2.visibility = View.GONE
                        editText.visibility = View.VISIBLE
                        toolConstraint.visibility = View.GONE
                        isEditing = 0
                        editText2.setText("")
                    }
                } else {
                    binding.messageChatList.scrollToPosition(
                        binding.messageChatList.adapter!!.itemCount - 1
                    )
                    dbRef = FirebaseDatabase.getInstance().getReference("Chats")
                    dbRef2 = FirebaseDatabase.getInstance().getReference("UserChats")
                    dbRef3 = FirebaseDatabase.getInstance().getReference("UserNotifications")

                    if ((editText.text.toString() != "")) {
                        val timeFormat: DateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                        time = timeFormat.format(Calendar.getInstance().time).toString()
                        text = editText.text.toString()
                        editText.setText("")
                        counterOfMessages = System.currentTimeMillis().toString()
                        val containsEmojis = containsEmojis(text)
                        messageType = if ((text.length == 2) && containsEmojis) {
                            "emoji"
                        } else {
                            "text"
                        }

                        val mapa = hashMapOf<String, Any>(
                            "messageId" to counterOfMessages,
                            "title" to text,
                            "time" to time,
                            "userId" to you,
                            "messageType" to messageType,
                            "pictureUrl" to uri,
                            "reText" to reText,
                            "reId" to reId,
                            "id" to userId,
                            "reaction1" to reaction,
                            "reaction2" to reaction
                        )
                        reText = ""
                        reId = ""
                        val chatData = hashMapOf(
                            "id" to key,
                            "avaUrl" to "?1?",
                            "chatStatus" to "-",
                            "lastMessage" to text,
                            "lastMessageTime" to time,
                            "nameOfChat" to coname,
                            "pinnedMessage" to "-",
                            "name" to coname,
                            "phone" to cophone
                        )
                        reText = ""
                        reId = ""
                        dbRef2.child(you).child(key).updateChildren(chatData as Map<String, Any>)
                        if (cophone!=phone){
                            val chatData2 = hashMapOf(
                                "id" to you,
                                "avaUrl" to "2",
                                "chatStatus" to "-",
                                "lastMessage" to text,
                                "lastMessageTime" to time,
                                "nameOfChat" to name,
                                "pinnedMessage" to "-",
                                "name" to name,
                                "phone" to phone
                            )
                            dbRef2.child(key).child(you).updateChildren(chatData2 as Map<String, Any>)
                        }
                        reText = ""
                        reId = ""
                        if (coname != "Избранное")
                        {
                            val messageData = hashMapOf(
                                "nottitle" to text,
                                "notname" to "---",
                                "phone" to cophone,
                                "cophone" to key
                            )
                            dbRef3.child(cophone).updateChildren(messageData as Map<String, Any>)
                        }
                        else{

                        }


                        text = ""
                        time = ""
                        reText = ""
                        reId = ""

                        dbRef.child(chatId).child(counterOfMessages).updateChildren(mapa)
                            .addOnSuccessListener {
                                reText = ""
                                reId = ""
                            }.addOnFailureListener {
                                //не отправлено
                            }
                    } else {
                        counterOfMessages = System.currentTimeMillis().toString()

                        val bitmap = (subImage.drawable as BitmapDrawable).bitmap
                        val nameofimg = generateRandomString()
                        val storageRef =
                            FirebaseStorage.getInstance().reference.child("Images/$nameofimg")
                        val baos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val data = baos.toByteArray()
                        val uploadFile = storageRef.putBytes(data)
                        uploadFile.addOnProgressListener { taskSnapshot ->
                            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                            Log.d("Upload progress", "Upload is $progress% done")
                        }

                        text = editText.text.toString()

                        val timeFormat: DateFormat =
                            SimpleDateFormat("HH:mm", Locale.getDefault())
                        time = timeFormat.format(Calendar.getInstance().time).toString()
                        messageType = "text"
                        val mapa = hashMapOf<String, Any>(
                            "messageId" to counterOfMessages,
                            "title" to text,
                            "time" to time,
                            "userId" to you,
                            "messageType" to messageType,
                            "pictureUrl" to nameofimg
                        )

                        dbRef = FirebaseDatabase.getInstance().getReference("Chats")
                        dbRef.child(chatId).child(counterOfMessages)
                            .updateChildren(mapa)
                            .addOnSuccessListener {
                                uri = ""
                            }.addOnFailureListener {
                                //не отправлено
                            }
                        editText.requestFocus()
                    }
                }

            }
        }
    }

    private fun newMessageLoader() {
        dbRef = FirebaseDatabase.getInstance().getReference("Chats").child(chatId)
        childEventListener = AppChildEventListener { snapshot, eventType ->
            when (eventType) {
                1 -> {
                    adapter.updateItem(snapshot.getMessageModel())
                    binding.messageChatList.scrollToPosition(
                        binding.messageChatList.adapter!!.itemCount - 1
                    )
                    messageId++
                }

                2 -> {
                    adapter.updateItem(snapshot.getMessageModel())
                    binding.messageChatList.scrollToPosition(
                        binding.messageChatList.adapter!!.itemCount - 1
                    )
                    messageId++
                }

                3 -> {
                    adapter.removeItem(snapshot.getMessageModel())
                    a = 0
                    messageId++
                }
            }

        }
        dbRef.addChildEventListener(childEventListener)
    }

    override fun onDeleteClicked(position: Int, message: Message) {
        delId = message.messageId.toString()
        dbRef = FirebaseDatabase.getInstance().getReference("Chats")
        dbRef.child(chatId).child(delId).removeValue()
            .addOnSuccessListener {
            }
    }
    private fun cancelEditingMessage() {
        binding.apply {
            cancelTool.setOnClickListener {
                if (isEditing == 1) {
                    editText2.visibility = View.GONE
                    editText.visibility = View.VISIBLE
                    toolConstraint.visibility = View.GONE
                    isEditing = 0
                    editText2.setText("")
                }
                else{
                    toolConstraint.visibility = View.GONE
                    reText = ""
                    reId = ""
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        listIs = 1
        val intent = Intent(this@chatWindow, Listdrawer::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.from_right, R.anim.to_right)
        finish()
    }


    @SuppressLint("SuspiciousIndentation")
    override fun onStart() {
        super.onStart()
        val sharedPref = getSharedPreferences("system", Context.MODE_PRIVATE)
        isChat = sharedPref.getString("chatListIs", "true").toString()
        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
        val chatData = hashMapOf(
            "status" to "online",
            "time" to ""
        )
        dbRef.child(phone).updateChildren(chatData as Map<String, Any>)
    }


    override fun onStop() {
        super.onStop()
        if (listIs == 0) {
            val dbRef = FirebaseDatabase.getInstance().getReference("Users")
            val chatData = hashMapOf(
                "status" to "offline",
                "time" to ""
            )
            dbRef.child(phone).updateChildren(chatData as Map<String, Any>)
        } else {
            val dbRef = FirebaseDatabase.getInstance().getReference("Users")
            val chatData = hashMapOf(
                "status" to "inChatList",
                "time" to ""
            )
            dbRef.child(phone).updateChildren(chatData as Map<String, Any>)
        }
    }


    override fun onEditClicked(position: Int, message: Message) {
        binding.apply {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            editText2.requestFocus()
            imm.showSoftInput(editText2, InputMethodManager.SHOW_IMPLICIT)
            editText2.requestFocus()
            handler.postDelayed({
                editText2.visibility = View.VISIBLE
                editText.visibility = View.GONE
                toolConstraint.visibility = View.VISIBLE
                isEditing = 1

                edId = message.messageId.toString()
                text = message.title.toString()
                editingText = text

                toolText2.text = text
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                editText2.requestFocus()
                imm.showSoftInput(editText2, InputMethodManager.SHOW_IMPLICIT)

                binding.editText2.setText(text)
                time = message.time.toString()
            }, 500)
            editText2.requestFocus()
            imm.showSoftInput(editText2, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun onCopyClicked(position: Int, message: Message) {
        textCopy = message.title.toString()
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("text", textCopy)
        clipboard.setPrimaryClip(clip)
    }

    override fun onRecieveClicked(position: Int, message: Message) {
        binding.apply {
            toolConstraint.visibility = View.VISIBLE
            reText = message.title.toString()
            reId = message.id.toString()
            toolText1.text = reId
            toolText2.text = reText
        }
    }

    override fun e1(position: Int, message: Message) {
        reaction = "👍"
        edId = message.messageId.toString()
        sendReaction()
    }

    override fun e2(position: Int, message: Message) {
        reaction = "❤️"
        edId = message.messageId.toString()
        sendReaction()}

    override fun e3(position: Int, message: Message) {
        reaction = "🤣"
        edId = message.messageId.toString()
        sendReaction()}

    override fun e4(position: Int, message: Message) {
        reaction = "☹️"
        edId = message.messageId.toString()
        sendReaction()}

    override fun e5(position: Int, message: Message) {
        reaction = "😡"
        edId = message.messageId.toString()
        sendReaction()
    }

    fun sendReaction(){
        val n = key
        val nn = you
        val result = n.compareTo(nn)
        if (result > 0) {
            val map = hashMapOf<String, Any>(
                "reaction1" to reaction,
            )
            dbRef = FirebaseDatabase.getInstance().getReference("Chats")
            dbRef.child(chatId).child(edId).updateChildren(map)
                .addOnSuccessListener {
                    //отправлено
                }.addOnFailureListener {
                    //не отправлено
                }
        }
        if (result < 0) {
            val map = hashMapOf<String, Any>(
                "reaction2" to reaction,
            )
            dbRef = FirebaseDatabase.getInstance().getReference("Chats")
            dbRef.child(chatId).child(edId).updateChildren(map)
                .addOnSuccessListener {
                    //отправлено
                }.addOnFailureListener {
                    //не отправлено
                }
        }
        if (result == 0) {
            val map = hashMapOf<String, Any>(
                "reaction1" to reaction,
            )
            dbRef = FirebaseDatabase.getInstance().getReference("Chats")
            dbRef.child(chatId).child(edId).updateChildren(map)
                .addOnSuccessListener {
                    //отправлено
                }.addOnFailureListener {
                    //не отправлено
                }
        }
        reaction = ""
    }
}