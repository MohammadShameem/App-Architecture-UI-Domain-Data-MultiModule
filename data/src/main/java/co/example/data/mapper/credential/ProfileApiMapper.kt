package co.example.data.mapper.credential



import co.example.apiresponse.profile.ProfileApiResponse
import co.example.data.mapper.Mapper
import co.example.di.qualifier.AppImageBaseUrl
import co.example.entity.profile.UserDetailsApiEntity
import co.example.sharedpref.SharedPrefHelper
import co.example.sharedpref.SpKey
import javax.inject.Inject


class ProfileApiMapper @Inject constructor() : Mapper<ProfileApiResponse, UserDetailsApiEntity> {
    override fun mapFromApiResponse(type: ProfileApiResponse): UserDetailsApiEntity {
        return UserDetailsApiEntity(
            id = type.data?.id?:0,
            name = type.data?.name?:"",
            direction = type.data?.direction?:0,
            company_id = type.data?.company_id?:0,
            contact = type.data?.contact?:"",
            phone = type.data?.phone?:"",
            report_print_limit = type.data?.report_print_limit?:0,
            stoppage_id = type.data?.stoppage_id?:0
        )
    }
}


class CacheProfile @Inject constructor(
    private val sharedPrefHelper: SharedPrefHelper,
    @AppImageBaseUrl private val imageBaseUrl:String
){
    fun cacheProfileData(profileApiEntity: UserDetailsApiEntity) {
        sharedPrefHelper.putString(SpKey.contact, profileApiEntity.contact?:"")
        sharedPrefHelper.putString(SpKey.userName,profileApiEntity.name)
        sharedPrefHelper.putString(SpKey.phone,profileApiEntity.phone)
        sharedPrefHelper.putInt(SpKey.companyId,profileApiEntity.company_id)
        sharedPrefHelper.putInt(SpKey.selectedBusStoppageId,profileApiEntity.stoppage_id)
        //sharedPrefHelper.putString(SpKey.selectedBusStoppageName,profileApiEntity.stoppage_name)
        sharedPrefHelper.putInt(SpKey.selectedDirection,profileApiEntity.direction)

    }
}
