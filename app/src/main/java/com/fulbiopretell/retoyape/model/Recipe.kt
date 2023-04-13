package com.fulbiopretell.retoyape.model

import com.google.gson.annotations.SerializedName

data class Recipe(
    @SerializedName("image")
    var image: String? = null,

    @SerializedName("description")
    var description: String? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("longitud")
    var longitud: Double? = null,

    @SerializedName("latitud")
    var latitud: Double? = null
)