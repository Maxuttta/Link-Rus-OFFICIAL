package link.download.ru

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiTextView


@Suppress("NAME_SHADOWING")
class MessageAdapter(val context: Context, private val a: String, private val listener: ItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var messageList = mutableListOf<Message>()
    private val ITEM_CENTER = 3
    private val ITEM_FROM = 2
    private val ITEM_TO = 1
    private var reaction = ""
    var isReCardVisible = false
    var isEmojiPanelVisible = false
    var firstEmojiPanel = 0
    var isReactionVisible1 = false
    var isReactionVisible2 = false
    var isReactionCardVisible = false


    private lateinit var mDiffResult: DiffUtil.DiffResult
        class MessageCenterHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val text = itemView.findViewById<EmojiTextView>(R.id.text_center)
            val time = itemView.findViewById<EmojiTextView>(R.id.time_center)
        }

        class MessageToHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val text = itemView.findViewById<EmojiTextView>(R.id.messageTo)
            val timeText = itemView.findViewById<TextView>(R.id.timeTo)
            val card = itemView.findViewById<ConstraintLayout>(R.id.cardTo)
            val picTo = itemView.findViewById<ImageView>(R.id.picTo)
            val imageTo = itemView.findViewById<CardView>(R.id.imageTo)

            val reCard = itemView.findViewById<ConstraintLayout>(R.id.recardto)
            val reText = itemView.findViewById<TextView>(R.id.retextto)
            val reId = itemView.findViewById<TextView>(R.id.reidto)

            val emojiConstraint1 = itemView.findViewById<ConstraintLayout>(R.id.emojiConstraint1)
            val emojiConstraint2 = itemView.findViewById<LinearLayout>(R.id.emojiConstraint2)
            val e = itemView.findViewById<CardView>(R.id.e)
            val e1 = itemView.findViewById<CardView>(R.id.e1)
            val e2 = itemView.findViewById<CardView>(R.id.e2)
            val e3 = itemView.findViewById<CardView>(R.id.e3)
            val e4 = itemView.findViewById<CardView>(R.id.e4)
            val e5 = itemView.findViewById<CardView>(R.id.e5)

            val rview = itemView.findViewById<ConstraintLayout>(R.id.rviewto)
            val rc1to = itemView.findViewById<CardView>(R.id.rc1to)
            val rc2to = itemView.findViewById<CardView>(R.id.rc2to)
            val r1to = itemView.findViewById<EmojiTextView>(R.id.r1to)
            val r2to = itemView.findViewById<EmojiTextView>(R.id.r2to)
    }

    class MessageFromHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text = itemView.findViewById<EmojiTextView>(R.id.messageFrom)
        val timeText = itemView.findViewById<TextView>(R.id.timeFrom)
        val card = itemView.findViewById<ConstraintLayout>(R.id.cardFrom)
        val picFrom = itemView.findViewById<ImageView>(R.id.picFrom)
        val imageFrom = itemView.findViewById<CardView>(R.id.imageFrom)

        val reCard = itemView.findViewById<ConstraintLayout>(R.id.recardfrom)
        val reText = itemView.findViewById<TextView>(R.id.retextfrom)
        val reId = itemView.findViewById<TextView>(R.id.reidfrom)

        val emojiConstraint11 = itemView.findViewById<ConstraintLayout>(R.id.emojiConstraint11)
        val emojiConstraint22 = itemView.findViewById<LinearLayout>(R.id.emojiConstraint22)
        val ee = itemView.findViewById<CardView>(R.id.ee)
        val ee1 = itemView.findViewById<CardView>(R.id.ee1)
        val ee2 = itemView.findViewById<CardView>(R.id.ee2)
        val ee3 = itemView.findViewById<CardView>(R.id.ee3)
        val ee4 = itemView.findViewById<CardView>(R.id.ee4)
        val ee5 = itemView.findViewById<CardView>(R.id.ee5)

        val rviewfrom = itemView.findViewById<ConstraintLayout>(R.id.rviewfrom)
        val rc1from = itemView.findViewById<CardView>(R.id.rc1from)
        val rc2from = itemView.findViewById<CardView>(R.id.rc2from)
        val r1from = itemView.findViewById<EmojiTextView>(R.id.r1from)
        val r2from = itemView.findViewById<EmojiTextView>(R.id.r2from)
    }


    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        return if (currentMessage.userId == a) {
            ITEM_TO
        }
        else if(currentMessage.userId == "center"){
            return ITEM_CENTER
        }
        else {
            return ITEM_FROM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            3 -> {
                val view: View = LayoutInflater.from(context)
                    .inflate(R.layout.message_center_item, parent, false)
                MessageCenterHolder(view)
            }
            2 -> {
                val view: View = LayoutInflater.from(context)
                    .inflate(R.layout.activity_message_from_item, parent, false)
                MessageFromHolder(view)
            }

            1 -> {
                val view: View = LayoutInflater.from(context)
                    .inflate(R.layout.activity_message_to_item, parent, false)
                MessageToHolder(view)
            }

            else -> {
                val view: View = LayoutInflater.from(context)
                    .inflate(R.layout.activity_message_to_item, parent, false)
                MessageToHolder(view)
            }
        }

    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val ioScope = CoroutineScope(Dispatchers.IO)

        val currentMessage = messageList[position]

        if (holder.javaClass == MessageToHolder::class.java) {
            val holder = holder as MessageToHolder
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference.child("Images/${currentMessage.pictureUrl}")
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Picasso.get().load(uri).into(holder.picTo)
            }.addOnFailureListener { exception ->
                Log.e("FirestoreImageLoadError", "Failed to load image: ${exception.message}")
            }
//            }

            @SuppressLint("ClickableViewAccessibility")
            fun swipe(){

            }
            holder.text.text = currentMessage.title
            holder.timeText.text = currentMessage.time
            if (currentMessage.messageType == "text") {
                holder.text.textSize = 16F
                holder.card.setBackgroundResource(R.drawable.message_to)
                if (currentMessage.pictureUrl != "") {
                    holder.imageTo.visibility = View.VISIBLE
                    holder.picTo.maxWidth = 100
                }
            }
//            if (currentMessage.messageType == "emoji") {
//                holder.text.textSize = 90F
//                if (currentMessage.title == "😀"){
//
//                }
//                holder.card.setBackgroundResource(R.color.invisible)
//            }
            val message = messageList[position]
            holder.e.setOnClickListener {
                isEmojiPanelVisible = false
                holder.emojiConstraint1.visibility = View.GONE
                holder.emojiConstraint2.visibility = View.GONE
            }
            holder.e1.setOnClickListener {
                listener.e1(position, message)
            }
            holder.e2.setOnClickListener {
                listener.e2(position, message)
            }
            holder.e3.setOnClickListener {
                listener.e3(position, message)
            }
            holder.e4.setOnClickListener {
                listener.e4(position, message)
            }
            holder.e5.setOnClickListener {
                listener.e5(position, message)
            }
                ///////////////////////////////////////////////////////////////////////////////////
            if ((currentMessage.reaction1 != "") && (currentMessage.reaction1 != null) && (currentMessage.reaction1!!.isNotEmpty())) {
                isReactionVisible1 = true
                isReactionCardVisible = true
            }
            if (isReactionVisible1) {
                holder.rview.visibility = View.VISIBLE
                holder.rc1to.visibility = View.VISIBLE
                reaction = currentMessage.reaction1.toString()
                holder.r1to.text = reaction
                reaction = ""
                isReactionVisible1 = false
                isReactionCardVisible = false
            }else {
                if ((currentMessage.reaction2 != "") && (currentMessage.reaction2 != null) && (currentMessage.reaction2!!.isNotEmpty())){
                    holder.rc1to.visibility = View.GONE
                    isReactionVisible1 = false
                    isReactionCardVisible = false
                }
                else{
                    holder.rview.visibility = View.GONE
                    holder.rc1to.visibility = View.GONE
                    holder.rc2to.visibility = View.GONE
                    isReactionVisible1 = false
                    isReactionVisible2= false
                    isReactionCardVisible = false
                }
            }

            if ((currentMessage.reaction2 != "") && (currentMessage.reaction2 != null) && (currentMessage.reaction2!!.isNotEmpty())) {
                isReactionVisible2 = true
                isReactionCardVisible = true
            }
            if (isReactionVisible2) {
                holder.rview.visibility = View.VISIBLE
                holder.rc2to.visibility = View.VISIBLE
                reaction = currentMessage.reaction2.toString()
                holder.r2to.text = reaction
                reaction = ""
                isReactionVisible2 = false
                isReactionCardVisible = false
            }
            else {
                if ((currentMessage.reaction1 != "") && (currentMessage.reaction1 != null) && (currentMessage.reaction1!!.isNotEmpty())){
                    holder.rc2to.visibility = View.GONE
                    isReactionVisible2 = false
                    isReactionCardVisible = false
                }
                else{
                    holder.rview.visibility = View.GONE
                    holder.rc1to.visibility = View.GONE
                    holder.rc2to.visibility = View.GONE
                    isReactionVisible1 = false
                    isReactionVisible2 = false
                    isReactionCardVisible = false
                }
            }

            ///////////////////////////////////////////////////////////////////////////////////////
            if ((currentMessage.reText != null) && (currentMessage.reText != "")){
                isReCardVisible = true
            }

            if (isReCardVisible) {
                holder.reCard.visibility = View.VISIBLE
                holder.reText.text = currentMessage.reText
                holder.reText.ellipsize = TextUtils.TruncateAt.END
                holder.reText.maxLines = 1
                holder.reId.ellipsize = TextUtils.TruncateAt.END
                holder.reId.maxLines = 1
                holder.reId.text = currentMessage.reId
                isReCardVisible = false
            } else {
                holder.reCard.visibility = View.GONE
            }
            ///////////////////////////////////////////////////////////////////////////////////////
            holder.itemView.setOnLongClickListener {
                if (!isEmojiPanelVisible){
                    holder.emojiConstraint1.visibility = View.VISIBLE
                    holder.emojiConstraint2.visibility = View.VISIBLE
                    isEmojiPanelVisible = true
                }
                true
            }
            holder.itemView.setOnClickListener {
                if (isEmojiPanelVisible){
                    isEmojiPanelVisible = false
                    holder.emojiConstraint1.visibility = View.GONE
                    holder.emojiConstraint2.visibility = View.GONE
                }else{
                    showPopupMenu(it, position)
                }
            }
        }

        else if (holder.javaClass == MessageCenterHolder::class.java){
            val holder = holder as MessageCenterHolder
            holder.text.text = currentMessage.reText
            holder.time.text = currentMessage.time
            val message = messageList[position]
            holder.itemView.setOnClickListener{
                listener.editCenter(position, message)
            }
        }

      ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        else {
            val holder = holder as MessageFromHolder
            val currentMessage = messageList[position]
            holder.itemView.setOnLongClickListener {
                if (!isEmojiPanelVisible){
//                    firstEmojiPanel = position
                    firstEmojiPanel = 1
                    isEmojiPanelVisible = true
                }
                if (isEmojiPanelVisible){
                    holder.emojiConstraint11.visibility = View.VISIBLE
                    holder.emojiConstraint22.visibility = View.VISIBLE
                }
                true

            }
                val storage = FirebaseStorage.getInstance()
                val storageRef = storage.reference.child("Images/${currentMessage.pictureUrl}")
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    Picasso.get().load(uri).into(holder.picFrom)
                }.addOnFailureListener { exception ->
                    Log.e("FirestoreImageLoadError", "Failed to load image: ${exception.message}")
                }

            val message = messageList[position]
            holder.ee.setOnClickListener {
                isEmojiPanelVisible = false
                holder.emojiConstraint11.visibility = View.GONE
                holder.emojiConstraint22.visibility = View.GONE
            }
            holder.ee1.setOnClickListener {
                listener.e1(position,message)
            }
            holder.ee2.setOnClickListener {
                listener.e2(position,message)
            }
            holder.ee3.setOnClickListener {
                listener.e3(position,message)
            }
            holder.ee4.setOnClickListener {
                listener.e4(position,message)
            }
            holder.ee5.setOnClickListener {
                listener.e5(position,message)
            }
            holder.text.text = currentMessage.title
            holder.timeText.text = currentMessage.time
            if (currentMessage.messageType == "text") {
                holder.text.textSize = 16F
                holder.card.setBackgroundResource(R.drawable.message_from)
                if (currentMessage.pictureUrl != ""){
                    holder.imageFrom.visibility = View.VISIBLE
                    holder.picFrom.maxWidth = 100
                }
            }
            ///////////////////////////////////////////////////////////////////////////////////////////////////
            if ((currentMessage.reaction1 != "") && (currentMessage.reaction1 != null)
                && (currentMessage.reaction1!!.isNotEmpty())){
                isReactionVisible1 = true
                isReactionCardVisible = true
//                holder.text.text = "1"
            }
            if (isReactionVisible1 && isReactionCardVisible) {
                holder.rviewfrom.visibility = View.VISIBLE
                holder.rc1from.visibility = View.VISIBLE
                reaction = currentMessage.reaction1.toString()
                holder.r1from.text = reaction
                reaction = ""
                isReactionVisible1 = false
                isReactionCardVisible = false
//                holder.text.text = "2"

            }else {
                if ((currentMessage.reaction2 != "") && (currentMessage.reaction2 != null)){
                    holder.rc1from.visibility = View.GONE
                    isReactionVisible1 = false
                    isReactionCardVisible = false
                }
                else{
                    holder.rviewfrom.visibility = View.GONE
                    holder.rc1from.visibility = View.GONE
                    holder.rc2from.visibility = View.GONE
                    isReactionVisible1 = false
                    isReactionVisible2 = false
                    isReactionCardVisible = false
                }
            }

            if ((currentMessage.reaction2 != "") && (currentMessage.reaction2 != null)
                && (currentMessage.reaction2!!.isNotEmpty())) {
                isReactionVisible2 = true
                isReactionCardVisible = true
            }
            if (isReactionVisible2 && isReactionCardVisible) {
                holder.rviewfrom.visibility = View.VISIBLE
                holder.rc2from.visibility = View.VISIBLE
                reaction = currentMessage.reaction2.toString()
                holder.r2from.text = reaction
                reaction = ""
                isReactionVisible1 = false
                isReactionCardVisible = false
            }else {
                if ((currentMessage.reaction1 != "") && (currentMessage.reaction1 != null)){
                    holder.rc2from.visibility = View.GONE
                    isReactionVisible2 = false
                    isReactionCardVisible = false
                }
                else{
                    holder.rviewfrom.visibility = View.GONE
                    holder.rc2from.visibility = View.GONE
                    isReactionVisible1 = false
                    isReactionVisible2 = false
                    isReactionCardVisible = false
                }
            }
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            if ((currentMessage.reText != null) && (currentMessage.reText != "")){
                isReCardVisible = true
            }

            if (isReCardVisible) {
                holder.reCard.visibility = View.VISIBLE
                holder.reText.text = currentMessage.reText
                holder.reText.ellipsize = TextUtils.TruncateAt.END
                holder.reText.maxLines = 1
                holder.reId.ellipsize = TextUtils.TruncateAt.END
                holder.reId.maxLines = 1
                holder.reId.text = currentMessage.reId
                isReCardVisible = false
            } else {
                holder.reCard.visibility = View.GONE
            }
            holder.itemView.setOnClickListener {
                if (isEmojiPanelVisible){
                    isEmojiPanelVisible = false
                    holder.emojiConstraint11.visibility = View.GONE
                    holder.emojiConstraint22.visibility = View.GONE
                }
                else{
                    showPopupMenu(it, position)
                }
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    override fun getItemCount(): Int {
        return messageList.size
    }

    fun addItem(item: Message) {
        val newList = mutableListOf<Message>()
        newList.addAll(messageList)
        newList.add(item)
        mDiffResult = DiffUtil.calculateDiff(DiffUtilCallback(messageList, newList))
        mDiffResult.dispatchUpdatesTo(this)
        messageList = newList
    }

    fun updateItem(item: Message) {
        val index = messageList.indexOfFirst { it.messageId == item.messageId }
        if (index != -1) {
            val newList = mutableListOf<Message>()
            newList.addAll(messageList)
            newList[index] = item
            mDiffResult = DiffUtil.calculateDiff(DiffUtilCallback(messageList, newList))
            mDiffResult.dispatchUpdatesTo(this)
            messageList = newList
        } else {
            addItem(item)
        }
    }

    fun removeItem(item: Message) {
        val index = messageList.indexOfFirst { it.messageId == item.messageId }
        messageList.removeAt(index)
        notifyItemRemoved(index)
        notifyItemRangeChanged(index,messageList.size)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showPopupMenu(view: View, position: Int) {
        val message = messageList[position]
        if (message.userId == a) {
            val popupMenu = PopupMenu(context, view)
            popupMenu.setForceShowIcon(true)
            popupMenu.menuInflater.inflate(R.menu.message_menu, popupMenu.menu)
            // Установка иконок для пунктов меню
            popupMenu.menu.findItem(R.id.recieve).setIcon(R.drawable.baseline_answer_24)
            popupMenu.menu.findItem(R.id.copy).setIcon(R.drawable.copy_icon)
            popupMenu.menu.findItem(R.id.edit).setIcon(R.drawable.baseline_edit_24)
            popupMenu.menu.findItem(R.id.delete).setIcon(R.drawable.delete)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.copy -> {
                        listener.onCopyClicked(position, message)
                        true
                    }
                    R.id.edit -> {
                        listener.onEditClicked(position, message)
                        true
                    }
                    R.id.center -> {
                        listener.onCenterClicked(position, message)
                        true
                    }
                    R.id.delete -> {
                        listener.onDeleteClicked(position, message)
                        true
                    }
                    R.id.recieve -> {
                        listener.onRecieveClicked(position,message)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
        if (message.userId != a){
            val popupMenu = PopupMenu(context, view)
            popupMenu.setForceShowIcon(true)
            popupMenu.menuInflater.inflate(R.menu.message_menuu, popupMenu.menu)
            popupMenu.menu.findItem(R.id.recieve).setIcon(R.drawable.baseline_answer_24)
            popupMenu.menu.findItem(R.id.copy).setIcon(R.drawable.copy_icon)
            popupMenu.menu.findItem(R.id.delete).setIcon(R.drawable.delete)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.copy -> {
                        listener.onCopyClicked(position, message)
                        true
                    }
                    R.id.recieve -> {
                        listener.onRecieveClicked(position,message)
                        true
                    }
                    R.id.delete -> {
                        listener.onDeleteClicked(position, message)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }
    interface ItemClickListener {
        fun onDeleteClicked(position: Int, message: Message)
        fun onEditClicked(position: Int, message: Message)
        fun onCopyClicked(position: Int, message: Message)
        fun onRecieveClicked(position: Int,message: Message)
        fun onCenterClicked(position: Int,message: Message)
        fun e1(position: Int,message: Message)
        fun e2(position: Int,message: Message)
        fun e3(position: Int,message: Message)
        fun e4(position: Int,message: Message)
        fun e5(position: Int,message: Message)
        fun editCenter(position: Int,message: Message)
        fun exit()
    }
    interface Listener{
        fun onClick(message: Message)
    }
}