package link.download.ru

import com.google.firebase.database.DataSnapshot

fun DataSnapshot.getMessageModel(): message =
    this.getValue(message::class.java)?:message()

fun DataSnapshot.getChatModel(): chat =
    this.getValue(chat::class.java)?:chat()