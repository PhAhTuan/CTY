package com.example.deadlinecty.model

data class ConfigResponse(
    val status: Int,
    val message: String,
    val data: ConfigData?
)

data class ConfigData(
    val type: String?,
    val version: String?,
    val domain: String?,
    val ckc: String?,
    val api_key: String?,
    val api_domain: String?,
    val report_domain: String?,
    val report_domain_v2: String?,
    val realtime_domain: String?,
    val system_server: String?,
    val current_domain: String?,
    val install_app_url: String?,
    val aloline_new_feed_content_url_path: String?,
    val is_allow_forgot_password: Int?,
    val date_time: String?,
    val api_chat_tms: String?,
    val api_chat_aloline: String?,
    val api_upload: String?,
    val api_upload_v2: String?,
    val api_upload_short: String?,
    val api_connection: String?,
    val api_log: String?,
    val ads_domain: String?,
    val chat_domain: String?,
    val api_oauth_node: String?,
    val api_saler: String?,
    val socket_conect_login: String?,
    val api_seemt_connection: String?,
    val api_join_group: String?,
    val is_enable_topup_card: Int?,
    val realtime_ip: String?,
    val rpk_public_key: String?
)
