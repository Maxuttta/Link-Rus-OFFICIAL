package link.download.ru
class Message{
    var title: String? = null
    var time: String? = null
    var userId: String? = null
    var pictureUrl: String? = null
    var messageId: String? = null
    var messageType: String? = null
    var reText: String? = null
    var reId:String? = null
    var id:String? = null
    constructor(title: String, time: String, userId: String
                , messageId: String, messageType: String, pictureUrl: String
                ,reText: String, reId: String, id: String)
    {
        this.title = title
        this.time = time
        this.userId = userId
        this.messageId = messageId
        this.messageType = messageType
        this.pictureUrl = pictureUrl
        this.reId = reId
        this.reText = reText
        this.id = id
    }
    constructor()
    }

