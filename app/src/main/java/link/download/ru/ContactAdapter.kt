package link.download.ru

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(): RecyclerView.Adapter<ContactAdapter.ContactHolder>() {
    private var contactList = mutableListOf<ContactData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ContactHolder(view)
    }
    class ContactHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.contactName)
        val firstName = itemView.findViewById<TextView>(R.id.firstLetter)
    }
    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        holder as ContactHolder
        val contact = contactList[position]
        val firstChar = contact.name?.get(0).toString()
        holder.name.text = contact.name
        holder.firstName.text = firstChar
    }

    override fun getItemCount() = contactList.size

    fun addItem(item: ContactData) {
        contactList.add(item)
    }
}