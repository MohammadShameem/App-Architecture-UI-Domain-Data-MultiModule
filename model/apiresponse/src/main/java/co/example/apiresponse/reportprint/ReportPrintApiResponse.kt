package co.example.apiresponse.reportprint

data class ReportPrintApiResponse(
    val code: Int,
    val status: String,
    val message: String,
    val report_date: String,
    val synced_today: SyncedToday?,
    val amount_wise_distributions: List<AmountWiseDistribution>?
)

data class SyncedToday(
    val amount: Int,
    val count: Int
)

data class AmountWiseDistribution(
    val amount:Int,
    val quantity:Int,
    val total_amount:Int
)

