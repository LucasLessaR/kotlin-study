package com.mercadolivro.controller.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty

data class PutCustomerRequest(

    @field:NotEmpty(message = "Name must be informed")
    var name: String,

    @field:Email(message = "Email must be valid")
    var email: String
)