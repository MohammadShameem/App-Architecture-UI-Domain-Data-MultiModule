package co.example.domain.usecase.remote.home

import co.example.dateutil.DateTimeFormat
import co.example.dateutil.DateTimeParser
import co.example.domain.base.ApiResult
import co.example.domain.base.ApiUseCaseNonParams
import co.example.domain.repository.remote.RemoteRepository
import co.example.entity.home.ReportPrintApiEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReportPrintApiUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository
): ApiUseCaseNonParams<ReportPrintApiEntity> {
    override suspend fun execute(): Flow<ApiResult<ReportPrintApiEntity>> {
        return remoteRepository.fetchReportPrintData(DateTimeParser.getCurrentDeviceDateTime(DateTimeFormat.sqlYMD))
    }

}