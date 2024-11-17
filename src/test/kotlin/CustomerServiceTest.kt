import Kotlin.crud.customer.Customer
import Kotlin.crud.customer.CustomerRepository
import Kotlin.crud.customer.CustomerService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CustomerServiceTest {

    @MockK
    lateinit var customerRepository: CustomerRepository

    @InjectMockKs
    lateinit var customerService: CustomerService

    @Test
    fun shouldGetAllCustomers(){
        //given
        every { customerRepository.findAll()} returns (ArrayList<Customer>(createCustomersWithDifferentDescriptionForTest()))
        //when
        val customerList = customerService.getAllCustomers();
        //then
        assertThat(customerList).hasSize(4)
        assertEquals(listOf("Szymon","Anna","Jan","Ewa"),createCustomersWithDifferentDescriptionForTest()
            .map{it.toCustomerDto().firstName})
    }

    @Test
    fun shouldGetEmptyList(){
        //given
        every { customerRepository.findAll() }returns(listOf())
        //when
        val customerList = customerService.getAllCustomers();
        //
        assertThat(customerList).isEmpty();
    }




    private fun createCustomersWithDifferentDescriptionForTest():List<Customer>{
        return mutableListOf(
            Customer(1L, "Szymon", "Pawlak", "szymonpawlak@o2.pl", "123 Main St, Warsaw, Poland"),
            Customer(2L, "Anna", "Nowak", "annanowak@o2.pl", "456 Green Ave, Krakow, Poland"),
            Customer(3L, "Jan", "Kowalski", "jankowalski@o2.pl", "789 Elm St, Gdansk, Poland"),
            Customer(4L, "Ewa", "Wiśniewska", "ewawisniewska@o2.pl", "101 Oak Blvd, Poznan, Poland")
        )
    }

}