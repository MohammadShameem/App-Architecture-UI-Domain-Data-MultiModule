package co.example.entity.home

data class ReportPrintApiEntity(
    val message: String,
    val report_date: String,
    val synced_today: SyncedTodayEntity,
    val amount_wise_distributions: List<AmountWiseDistributionEntity>
)

data class SyncedTodayEntity(
    val paid_amount: Int,
    val paid_count: Int
)

data class AmountWiseDistributionEntity(
    val amount: Int,
    val quantity: Int,
    val total_amount: Int
)