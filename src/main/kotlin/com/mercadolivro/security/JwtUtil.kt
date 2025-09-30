package com.mercadolivro.security

import com.mercadolivro.exception.AuthenticationException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

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

    fun isValidToken(token: String): Boolean {
        val claims = getClaims(token)
        return !(claims.subject.isNullOrEmpty() ||
                claims.expiration == null ||
                Date().after(claims.expiration)
                )
    }

    private fun getClaims(token: String): Claims {
        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: Exception) {
            throw AuthenticationException("Token Invalido: Get Claims", "999")
        }
    }

    fun getSubject(token: String): String {
        return getClaims(token).subject
    }
}