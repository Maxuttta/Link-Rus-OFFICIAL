package link.download.ru

public class message{
    var title: String? = null
    var time:String? = null
    var userId:String? = null
    var pictureUrl:String? = null
    var messageId: String? = null
    var messageType: String? = null
    constructor(title: String, time: String, userId: String, messageId: String, messageType: String, pictureUrl: String)
    {
        this.title = title
        this.time = time
        this.userId = userId
        this.messageId = messageId
        this.messageType = messageType
        this.pictureUrl = pictureUrl
    }
    constructor()
    }

