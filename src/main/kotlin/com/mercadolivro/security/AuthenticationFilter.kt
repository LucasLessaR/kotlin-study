package com.mercadolivro.security

import com.mercadolivro.controller.request.LoginRequest
import com.mercadolivro.exception.AuthenticationException
import com.mercadolivro.repository.CustomerRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException

class AuthenticationFilter(
    authenticationManager: AuthenticationManager,
    private val customerRepository: CustomerRepository,
    private val jwtUtil: JwtUtil
): UsernamePasswordAuthenticationFilter(authenticationManager) {

    init {
        setFilterProcessesUrl("/login")
    }

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication? {
        try {
            val requestBody = request.inputStream.bufferedReader().use { it.readText() }
            println("Request body: $requestBody")

            val loginRequest = Json.decodeFromString<LoginRequest>(requestBody)
            println("Parsed login request: $loginRequest")

            val customer = customerRepository.findByEmail(loginRequest.email)
                ?: throw BadCredentialsException("Customer not found with email: ${loginRequest.email}")

            println("Found customer with ID: ${customer.id}")
            println("Customer Info: ${customer.email}, ${customer.name}")

            val authToken = UsernamePasswordAuthenticationToken(customer.id, loginRequest.password)
            return authenticationManager.authenticate(authToken)
        } catch (e: SerializationException) {
            throw AuthenticationException("JSON input cannot be deserialized to the value of type", "999")
        } catch (e: IllegalArgumentException) {
            throw AuthenticationException("decoded input cannot be represented as a valid instance of type", "999")
        } catch (e: IOException) {
            println("IOException during deserialization: ${e.message}")
            throw AuthenticationException("I/O error occurs and stream cannot be read from: ", "999")
        } catch (e: Exception) {
            throw AuthenticationException("Falha ao autenticar", "999")
        }
    }

    override fun successfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain, authResult: Authentication) {
        val id = (authResult.principal as UserCustomDetails).id
        println("Authentication successful for customer ID: $id")
        val token = jwtUtil.generateToken(id)

        response.addHeader("Authorization", "Bearer $token")
    }

    override fun unsuccessfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        failed: org.springframework.security.core.AuthenticationException
    ) {
        println("Authentication failed: ${failed.message}")
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.writer.write("{\"error\": \"Authentication failed: ${failed.message}\"}")
        response.contentType = "application/json"
    }
}