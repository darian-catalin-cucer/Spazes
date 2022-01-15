package com.mcdev.spazes.dto

data class Spaces(
    var created_at: String?,
    var creator_id: String?,
    var host_ids: List<String>?,
    var id: String?,
    var invited_user_ids: List<String>?,
    var is_ticketed: Boolean?,
    var lang: String?,
    var participant_count: Int?,
    var scheduled_start: String?,
    var speaker_ids: List<String>?,
    var started_at: String?,
    var state: String?,
    var title: String?,
    var topic_ids: List<String>?,
    var updated_at: String?
)