package com.mcdev.spazes.dto

data class SpacesResponse(
    var `data`: List<Spaces>?,
    var includes: Includes?,
    var meta: Meta?
)