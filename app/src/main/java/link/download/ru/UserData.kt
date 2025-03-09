package link.download.ru

data class UserData (
    var Id: String? = null,
    var password: String? = null,
    var phone: String? = null,
    var name: String? = null,
    var icon: String? = null,
    var priceFrom2: String? = null,
    var rate: String? = null,
    var meal: String? = null,
    var currency: String? = null,
    var cancellationPolicy: String? = null,
    var roomType: String? = null,
    var amenities: List<String>? = null
)