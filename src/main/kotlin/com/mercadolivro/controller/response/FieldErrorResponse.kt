package com.mercadolivro.controller.response

import kotlinx.serialization.Serializable

@Serializable
data class FieldErrorResponse (
    var message: String,
    var field: String
)
