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
    var reaction1: String? = null
    var reaction2:String? = null
    constructor(title: String, time: String, userId: String
                , messageId: String, messageType: String, pictureUrl: String
                ,reText: String, reId: String, id: String
                ,reaction1: String, reaction2: String)
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
        this.reaction1 = reaction1
        this.reaction2 = reaction2
    }
    constructor()
    }

