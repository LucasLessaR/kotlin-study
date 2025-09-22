package com.mercadolivro.security

import com.mercadolivro.enums.CustomerStatus
import com.mercadolivro.model.CustomerModel
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserCustomDetails(val customerModel: CustomerModel): UserDetails {
    val id: Int = customerModel.id!!

    override fun getAuthorities(): Collection<GrantedAuthority?> {
        return customerModel.roles.map {
            SimpleGrantedAuthority(it.description)
        }.toMutableList()
    }

    override fun getPassword(): String = customerModel.password

    override fun getUsername(): String = customerModel.id.toString()

    override fun isEnabled(): Boolean = customerModel.status == CustomerStatus.ATIVO
}