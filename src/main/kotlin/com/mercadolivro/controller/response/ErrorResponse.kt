package com.mercadolivro.controller.response

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse (
    var httpCode: Int,
    var message: String,
    var internalCode: String,
    var errors: List<FieldErrorResponse>?
)