package com.mcdev.spazes.dto

data class Description(
    var cashtags: List<Cashtag>?,
    var hashtags: List<Hashtag>?,
    var mentions: List<Mention>?,
    var urls: List<Url>?
)