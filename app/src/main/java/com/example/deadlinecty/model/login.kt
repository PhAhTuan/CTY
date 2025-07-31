package com.example.deadlinecty.model

data class LoginResponse(
    val status: Int,
    val message: String,
    val data: LoginData?
)

data class LoginBody(
    val password: String,
    val app_type: Int,
    val device_uid: String,
    val push_token: String,
    val username: String,
    val device_name: String,
)

data class LoginData(
    val id: Int,
    val username: String?,
    val email: String?,
    val phone: String?,
    val name: String?,
    val avatar: String?,
    val address: String?,
    val gender: Int?,
    val birthday: String?,
    val permissions: List<String>?,
    val access_token: String?,
    val jwt_token: String?,
    val refresh_token: String?,
    val token_type: String?,
    val expires_in: Long?,
    val employee_role_id: Int?,
    val employee_role_name: String?,
    val employee_role_description: String?,
    val restaurant_id: Int?,
    val restaurant_name: String?,
    val restaurant_fullname: String?,
    val restaurant_brand_id: Int?,
    val restaurant_brand_name: String?,
    val branch_id: Int?,
    val branch_name: String?,
    val branch_address: String?,
    val area_id: Int?,
    val area_name: String?,
    val employee_rank_name: String?,
    val is_enable_change_password: Int?,
    val is_branch_office: Int?,
    val number_inferior: Int?,
    val is_working: Int?
)
