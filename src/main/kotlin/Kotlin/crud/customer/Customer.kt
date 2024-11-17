package Kotlin.crud.customer

import jakarta.persistence.*

@Entity
class Customer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
   private val id: Long,
    @Column(name = "first_name")
   private val firstName: String,
    @Column(name = "last_name")
   private val lastName: String,
    @Column(name = "email")
   private val email: String,
    @Column(name = "address")
   private val address: String
){
  fun toCustomerDto():CustomerDto{
      return CustomerDto(id,firstName,lastName,email,address);
  }
}
data class CustomerDto(val id: Long, val firstName:String,val lastName:String
                       , val email:String,val address:String)
