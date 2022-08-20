package co.example.entity.profile

data class ProfileApiEntity(
    val code: Int,
    val data: UserDetailsApiEntity,
    val message: String,
    val status: String
)

data class UserDetailsApiEntity(
    val company_id: Int,
    val contact: String,
    val direction: Int,
    val id: Int,
    val name: String,
    val phone: String,
    val report_print_limit: Int,
    val stoppage_id: Int,
)