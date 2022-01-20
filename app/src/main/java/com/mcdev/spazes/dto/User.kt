package com.mcdev.spazes.dto

data class User(
    var created_at: String?,
    var description: String?,
    var entities: Entities?,
    var id: String?,
    var location: String?,
    var name: String?,
    var pinned_tweet_id: String?,
    var profile_image_url: String?,
    var `protected`: Boolean?,
    var public_metrics: PublicMetrics?,
    var url: String?,
    var username: String?,
    var verified: Boolean?
)