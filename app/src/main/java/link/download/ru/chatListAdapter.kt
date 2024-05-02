package link.download.ru

import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import link.download.ru.databinding.UserChatItemBinding

@Suppress("NAME_SHADOWING")
class chatListAdapter(val listener:Listener): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    var chatList = mutableListOf<chat>()

    lateinit var mDiffResult: DiffUtil.DiffResult

    class ChatHolder(item: View): RecyclerView.ViewHolder(item){
        private val binding = UserChatItemBinding.bind(item)
        val userNameText = itemView.findViewById<TextView>(R.id.userNameText)
        val lastMessageText = itemView.findViewById<TextView>(R.id.lastMessage)
        val lastMessageTime = itemView.findViewById<TextView>(R.id.lastMessageTime)
        val extraCard = itemView.findViewById<CardView>(R.id.extraCard)
        val extraText = itemView.findViewById<TextView>(R.id.extraText)
        val context = itemView.findViewById<TextView>(R.id.context)
        val extraImage = itemView.findViewById<ImageView>(R.id.extraImage)

        fun bind(chat:chat, listener: Listener) = with(binding) {
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
    }

//    @SuppressLint("NotifyDataSetChanged")
//    fun  addChat(chat: chat){
//        chatList.add(chat)
//        notifyDataSetChanged()
//    }
    fun addChat(item: chat){
        val newList = mutableListOf<chat>()
        newList.addAll(chatList)
        newList.add(item)
        mDiffResult = DiffUtil.calculateDiff(chatDiffUtil(chatList, newList))
        mDiffResult.dispatchUpdatesTo(this)
        chatList = newList
}

    fun updateItem(item: chat) {
        val index = chatList.indexOfFirst { it.id == item.id }
        if (index != -1) {
            val newList = mutableListOf<chat>()
            newList.addAll(chatList)
            newList[index] = item
            mDiffResult = DiffUtil.calculateDiff(chatDiffUtil(chatList, newList))
            mDiffResult.dispatchUpdatesTo(this)
            chatList = newList
        } else {
            addChat(item)
        }
    }

    fun removeItem(item: chat) {
        val index = chatList.indexOfFirst { it.id == item.id }
        chatList.removeAt(index)
        notifyItemRemoved(index)
        notifyItemRangeChanged(index,chatList.size)
    }

    interface Listener{
        fun onClick(chat: chat)
    }

}