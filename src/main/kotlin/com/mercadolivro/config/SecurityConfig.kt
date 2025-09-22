package com.mercadolivro.config

import com.mercadolivro.repository.CustomerRepository
import com.mercadolivro.security.AuthenticationFilter
import com.mercadolivro.security.JwtUtil
import com.mercadolivro.security.UserCustomDetails
import com.mercadolivro.service.UserDetailsCustomService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customerRepository: CustomerRepository,
    private val userDetailsCustomService: UserDetailsCustomService,
    private val jwtUtil: JwtUtil
) {

    private val PUBLIC_MATCHERS = arrayOf<String>(
        "/customer",
        "/book/*",
    )
    private val PUBLIC_POST_MATCHERS = arrayOf(
        "/customer",
        "/login"
    )

    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userDetailsCustomService)
        authProvider.setPasswordEncoder(bCryptPasswordEncoder())
        return authProvider
    }

    @Bean
    fun config(http: HttpSecurity, authenticationManager: AuthenticationManager): SecurityFilterChain {
        return http
            .cors { cors -> cors.disable() }
            .csrf { csrf -> csrf.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(*PUBLIC_MATCHERS).permitAll()
                    .requestMatchers(HttpMethod.POST, *PUBLIC_POST_MATCHERS).permitAll()
                    .anyRequest().authenticated()
            }
            .addFilter(AuthenticationFilter(
                authenticationManager,
                customerRepository,
                jwtUtil)
            )
            .sessionManagement { sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .build()
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }
}