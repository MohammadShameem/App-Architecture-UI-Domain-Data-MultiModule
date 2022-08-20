package co.example.data.mapper.home

import co.example.apiresponse.reportprint.ReportPrintApiResponse
import co.example.data.mapper.Mapper
import co.example.entity.home.AmountWiseDistributionEntity
import co.example.entity.home.ReportPrintApiEntity
import co.example.entity.home.SyncedTodayEntity
import javax.inject.Inject

class ReportPrintApiMapper @Inject constructor() :
    Mapper<ReportPrintApiResponse, ReportPrintApiEntity> {
    override fun mapFromApiResponse(type: ReportPrintApiResponse): ReportPrintApiEntity {
        return ReportPrintApiEntity(
            message = type.message,
            synced_today = SyncedTodayEntity(
                paid_amount = type.synced_today?.amount?:0,
                paid_count = type.synced_today?.count?:0
            ),
            amount_wise_distributions = type.amount_wise_distributions?.map { amountWiseDistribution->
                AmountWiseDistributionEntity(
                    amount = amountWiseDistribution.amount,
                    quantity = amountWiseDistribution.quantity,
                    total_amount = amountWiseDistribution.total_amount
                )
            }?: emptyList(),
            report_date = type.report_date
        )
    }
}