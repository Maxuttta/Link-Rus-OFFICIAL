package link.download.ru

import com.google.firebase.database.DataSnapshot

fun DataSnapshot.getMessageModel(): Message =
    this.getValue(Message::class.java)?:Message()

fun DataSnapshot.getChatModel(): Chat =
    this.getValue(Chat::class.java)?:Chat()