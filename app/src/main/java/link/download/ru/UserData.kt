package link.download.ru

class UserData {
    var id: String? = null
    var icon: String? = null
    var name: String? = null
    var password: String? = null
    var phone: String? = null
    var status: String? = null
    var time: String? = null
    var notname: String? = null
    var nottitle: String? = null
    var cophone: String? = null

    constructor(id:String, icon: String, name: String
                , password: String, phone: String, status: String
                , time: String, notname: String, nottitle: String, cophone: String){
        this.id = id
        this.icon = icon
        this.name = name
        this.password = password
        this.phone = phone
        this.status = status
        this.time = time
        this.nottitle = nottitle
        this.notname = notname
        this.cophone = cophone
    }
    constructor()
}