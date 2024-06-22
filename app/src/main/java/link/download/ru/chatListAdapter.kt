package link.download.ru

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import link.download.ru.databinding.UserChatItemBinding
import java.io.File

@Suppress("NAME_SHADOWING")
class chatListAdapter(val listener:Listener
, val context: Context, ): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    var chatList = mutableListOf<Chat>()

    lateinit var mDiffResult: DiffUtil.DiffResult
    private lateinit var dbRef2: DatabaseReference

    class ChatHolder(item: View): RecyclerView.ViewHolder(item){
        private val binding = UserChatItemBinding.bind(item)
        val userNameText = itemView.findViewById<TextView>(R.id.userNameText)
        val lastMessageText = itemView.findViewById<TextView>(R.id.lastMessage)
        val lastMessageTime = itemView.findViewById<TextView>(R.id.lastMessageTime)
        val extraCard = itemView.findViewById<CardView>(R.id.extraCard)
        val avaCard = itemView.findViewById<CardView>(R.id.cardView4)
        val extraText = itemView.findViewById<TextView>(R.id.extraText)
        val context = itemView.findViewById<TextView>(R.id.context)
        val extraImage = itemView.findViewById<ImageView>(R.id.extraImage)
        val ava = item.findViewById<ImageView>(R.id.userAva)
        fun bind(chat:Chat, listener: Listener) = with(binding) {
            itemView.setOnClickListener{
                listener.onClick(chat)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_chat_item,parent,false)
        return ChatHolder(view)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val holder = holder as ChatHolder
        holder.bind(chatList[position], listener)
        val currentChat = chatList[position]
        Log.d("qwerqwer","$currentChat")
        holder.userNameText.text = currentChat.nameOfChat
        holder.lastMessageTime.text = currentChat.lastMessageTime
        holder.lastMessageText.text = currentChat.lastMessage
        holder.lastMessageText.ellipsize = TextUtils.TruncateAt.END
        holder.lastMessageText.maxLines = 1
        if (currentChat.nameOfChat == "Избранное"){
            holder.ava.setBackgroundResource(R.drawable.saves_ava)
        }
        if ((currentChat.pinnedMessage != "-") && (currentChat.isCalling != "active")){
            holder.extraText.text = "Прикрепленное сообщение:"
            holder.extraCard.visibility = View.VISIBLE
            holder.extraText.visibility = View.VISIBLE
            holder.context.visibility = View.VISIBLE
            holder.context.text = currentChat.pinnedMessage
        }
        if (currentChat.chatStatus.toString() == "active"){
            holder.extraCard.visibility = View.VISIBLE
            holder.extraText.visibility = View.VISIBLE
            holder.extraImage.setBackgroundResource(R.drawable.baseline_phone_24_2)
            holder.extraText.text = "Идет звонок"
//            holder.context.visibility = View.VISIBLE
//            holder.context.text = "Х человек в звонке"
        }

        val dbRef1 = FirebaseDatabase.getInstance().getReference("Users")
        dbRef1.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(
                snapshot: DataSnapshot,
                previousChildName: String?
            ) {
                val data = snapshot.getValue(UserData::class.java)
                if (data != null) {
                    val i = data.phone
                    if (currentChat.id == i) {
                        val name = data.name
                        holder.userNameText.text = currentChat.nameOfChat
                        val id = data.Id
                        val phone = data.phone.toString()
                        val status = data.status
                        val iconUrl = data.icon
                        var i1 = data.icon1.toString()
                        var i2 = data.icon2.toString()
                        if (i1 != i2){
                            val storage = FirebaseStorage.getInstance()
                            val storageRef = storage.reference.child("Avatars/$iconUrl")
                            val file = File(context.filesDir, "$phone")
                            if (file.exists()) {
                                val bitmap = BitmapFactory.decodeFile(file.path)
                                if (currentChat.name != "Избранное") {
                                    holder.ava.setImageBitmap(bitmap)
                                }
                            }
                            else{
                                storageRef.getFile(file).addOnSuccessListener {
                                    val bitmap = BitmapFactory.decodeFile(file.path)
                                    if (currentChat.name != "Избранное") {
                                        holder.ava.setImageBitmap(bitmap)
                                    }
                                }
                            }
                            i2 = i1
                            val map = hashMapOf<String, Any>(
                                "icon1" to i1,
                                "icon2" to i2
                            )
                            dbRef2 = FirebaseDatabase.getInstance().getReference("Users")
                            dbRef2.child(phone).updateChildren(map)
                                .addOnSuccessListener {

                                }.addOnFailureListener {

                                }
                        }
                        else{
                            val storage = FirebaseStorage.getInstance()
                            val storageRef = storage.reference.child("Avatars/$iconUrl")
                            val file = File(context.filesDir, "$phone")
                            if (file.exists()) {
                                val bitmap = BitmapFactory.decodeFile(file.path)
                                if (currentChat.name != "Избранное") {
                                    holder.ava.setImageBitmap(bitmap)
                                }
                            }
                            else{
                                storageRef.getFile(file).addOnSuccessListener {
                                    val bitmap = BitmapFactory.decodeFile(file.path)
                                    if (currentChat.name != "Избранное") {
                                        holder.ava.setImageBitmap(bitmap)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

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
    }

//    @SuppressLint("NotifyDataSetChanged")
//    fun  addChat(chat: chat){
//        chatList.add(chat)
//        notifyDataSetChanged()
//    }
    fun addChat(item: Chat){
        val newList = mutableListOf<Chat>()
        newList.addAll(chatList)
        newList.add(item)
        mDiffResult = DiffUtil.calculateDiff(ChatDiffUtil(chatList, newList))
        mDiffResult.dispatchUpdatesTo(this)
        chatList = newList
}

    fun updateItem(item: Chat) {
        val index = chatList.indexOfFirst { it.id == item.id }
        if (index != -1) {
            val newList = mutableListOf<Chat>()
            newList.addAll(chatList)
            newList[index] = item
            mDiffResult = DiffUtil.calculateDiff(ChatDiffUtil(chatList, newList))
            mDiffResult.dispatchUpdatesTo(this)
            chatList = newList
        } else {
            addChat(item)
        }
    }

    fun removeItem(item: Chat) {
        val index = chatList.indexOfFirst { it.id == item.id }
        chatList.removeAt(index)
        notifyItemRemoved(index)
        notifyItemRangeChanged(index,chatList.size)
    }

    interface Listener{
        fun onClick(chat: Chat)
    }
    private fun generateRandomString(): String {
        return "${System.currentTimeMillis()}"
    }

}