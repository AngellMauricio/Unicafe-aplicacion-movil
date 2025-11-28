package com.example.unicafe.Modelo

class clsDatosRespuestaH(
    // El PHP devuelve "error" => true/false (booleano)
    val error: Boolean,

    // El PHP devuelve "mensaje" => "Texto..." (String)
    val mensaje: String,

    // Puedes mantener este si otras APIs lo usan, si no, puedes quitarlo
    val user_id: Int? = null
)