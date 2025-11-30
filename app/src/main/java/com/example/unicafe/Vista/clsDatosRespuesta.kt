package com.example.unicafe.Vista
import com.google.gson.annotations.SerializedName

data class clsDatosRespuesta(
    val Estado: String,
    val Salida: String,

    @SerializedName("idUsuario")
    val user_id: Int? = null,

    @SerializedName("intIdRol")
    val rol_id: Int? = null
)