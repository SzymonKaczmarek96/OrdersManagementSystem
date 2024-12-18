package Kotlin.crud.customer

import jakarta.persistence.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import updateIf
import java.io.Serializable


@Entity
@Table(name = "customer")
class Customer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private var id: Long? = null,
    @Column(name = "first_name")
    private var firstName: String,
    @Column(name = "last_name")
    private var lastName: String,
    @Column(name = "email")
    private val email: String,
    @Column(name = "address")
    private var address: String
) : Serializable {


    fun toCustomerDto(): CustomerDto {
        return CustomerDto(id, firstName, lastName, email, address)
    }

    fun updateWith(customerDto: CustomerDto) {
        updateIf(customerDto.firstName.isNotBlank()) { this.firstName = customerDto.firstName }
        updateIf(customerDto.lastName.isNotBlank()) { this.lastName = customerDto.lastName }
        updateIf(customerDto.address.isNotBlank()) { this.address = customerDto.address }
    }


}

data class CustomerDto(
    val id: Long?, val firstName: String, val lastName: String, val email: String, val address: String
)


@ResponseStatus(HttpStatus.CONFLICT)
class EmailExistsException(message: String = "Email already exists") : RuntimeException(message)

@ResponseStatus(HttpStatus.NOT_FOUND)
class CustomerNotFoundException(message: String = "Customer not found") : RuntimeException(message)

@ResponseStatus(HttpStatus.CONFLICT)
class DataBlankException(message: String = "Customer data cannot contain empty fields") : RuntimeException(message)