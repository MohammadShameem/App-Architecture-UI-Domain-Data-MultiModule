package co.example.data.apiservice

import co.example.apiresponse.credential.LoginApiResponse
import co.example.apiresponse.credential.LogoutApiResponse
import co.example.apiresponse.dashboard.TodaySyncedTicketApiResponse
import co.example.apiresponse.offlinedatasync.OfflineDataSyncApiResponse
import co.example.apiresponse.profile.ProfileApiResponse
import co.example.apiresponse.reportprint.ReportPrintApiResponse
import co.example.apiresponse.syncsoldticket.SyncSoldTicketApiResponse
import co.example.entity.home.SyncSoldTicketEntity
import retrofit2.Response
import retrofit2.http.*

interface ApiServices {


    @POST("/v1/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("phone") phone: String,
        @Field("password") password: String,
    ): Response<LoginApiResponse>

    @GET("/v1/logout")
    suspend fun logout(): Response<LogoutApiResponse>

    @GET("/v1/offline-data-sync")
    suspend fun fetchOfflineDataSync(): Response<OfflineDataSyncApiResponse>

    @POST("/v1/sync-booking-data")
    suspend  fun syncSoldTicketsToServer(@Body syncSoldTicketEntity: SyncSoldTicketEntity): Response<SyncSoldTicketApiResponse>

    /**
     * Fetching user profile data to user user data in profile activity
     * @return: userProfileApiResponse
     * */
    @GET("/v1/profile")
    suspend fun fetchUserProfileData(): Response<ProfileApiResponse>


    @GET("/v1/get-today-synced")
    suspend fun getTodaySyncedTicketData(): Response<TodaySyncedTicketApiResponse>


    /**
     * Fetching ticket details to print report
     * @return: reportPrintApiResponse
     * */
    @GET("/v1/print-report")
    suspend fun fetchPrintReport(@Query("report_date") currentDate: String): Response<ReportPrintApiResponse>
}