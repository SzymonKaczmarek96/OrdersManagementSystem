package Kotlin.crud.customer

import jakarta.persistence.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.io.Serializable


@Entity
@Table(name = "customer")
class Customer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    var id: Long? = null,
    @Column(name = "first_name")
    val firstName: String,
    @Column(name = "last_name")
   val lastName: String,
    @Column(name = "email")
   val email: String,
    @Column(name = "address")
    val address: String
): Serializable{


  fun toCustomerDto():CustomerDto{
      return CustomerDto(firstName,lastName,email,address)
  }
}
data class CustomerDto(val firstName:String,val lastName:String
                       , val email:String,val address:String)

@ResponseStatus(HttpStatus.CONFLICT)
class EmailExistsException(message: String = "Email already exists") : RuntimeException(message)

@ResponseStatus(HttpStatus.NOT_FOUND)
class CustomerNotFoundException(message: String = "Customer not found") :RuntimeException(message)
