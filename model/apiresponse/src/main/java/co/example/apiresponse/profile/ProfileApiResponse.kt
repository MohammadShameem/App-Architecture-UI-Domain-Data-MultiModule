package co.example.apiresponse.profile

data class ProfileApiResponse(
    val code: Int?,
    val data: UserDetails?,
    val message: String?,
    val status: String?
)

data class UserDetails(
    val company_id: Int?,
    val contact: String?,
    val direction: Int?,
    val id: Int?,
    val name: String?,
    val phone: String?,
    val report_print_limit: Int?,
    val stoppage_id: Int?,
)