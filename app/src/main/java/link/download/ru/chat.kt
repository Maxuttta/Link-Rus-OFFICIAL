package link.download.ru

public class chat{
    var id:String? = null
    var nameOfChat: String? = null
    var avaUrl:String? = null
    var isCalling:String? = null
    var chatStatus:String? = null
    var lastMessage: String? = null
    var lastMessageTime: String? = null
    var pinnedMessage: String? = null
    var phone: String? = null
    var name: String? = null

    constructor(id: String, nameOfChat: String, avaUrl: String, chatStatus: String, isCalling: String, lastMessage: String,lastMessageTime: String,pinnedMessage:String, phone: String, name: String)
    {
        this.id = id
        this.nameOfChat = nameOfChat
        this.avaUrl = avaUrl
        this.isCalling = isCalling
        this.chatStatus = chatStatus
        this.lastMessage = lastMessage
        this.lastMessageTime = lastMessageTime
        this.pinnedMessage = pinnedMessage
        this.phone = phone
        this.name = name

    }
constructor()
}

