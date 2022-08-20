package co.example.apiresponse.dashboard

data class TodaySyncedTicketApiResponse(
    val code: Int,
    val message: String,
    val status: String,
    var synced_today_amount: Int,
    val synced_today_count: Int
)
