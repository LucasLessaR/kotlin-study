package com.mercadolivro.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey
import kotlin.io.encoding.Base64

@Component
class JwtUtil {
    @Value($$"${jwt.expiration}")
    private val expiration: Long? = null

//    Code used to generate random Key that is "untraceable"
//    private val secretKey: SecretKey = Jwts.SIG.HS512.key().build()

    @Value($$"${jwt.secret}")
     private val jwtSecret: String? = null

     private val secretKey: SecretKey by lazy {
         if (!jwtSecret.isNullOrEmpty()) {
             println("secret: $jwtSecret")
             Keys.hmacShaKeyFor(jwtSecret.toByteArray())
         } else {
             Jwts.SIG.HS512.key().build()
         }
     }

    fun generateToken(id: Int): String{
        val now = Date()
        return Jwts.builder()
            .subject(id.toString())
            .expiration(Date(now.time + expiration!!))
            .signWith(secretKey)
            .compact()
    }
}