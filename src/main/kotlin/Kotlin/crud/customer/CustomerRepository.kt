package Kotlin.crud.customer

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository: JpaRepository<Customer,Long>  {
    fun existsByEmail(email:String):Boolean;

    @Query("SELECT * FROM CUSTOMER WHERE email = :email", nativeQuery = true)
    fun findCustomerByEmail(@Param("email") email: String):Customer?
}