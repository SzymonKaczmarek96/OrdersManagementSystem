import Kotlin.crud.CrudApplication
import Kotlin.crud.customer.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration


@ContextConfiguration
@SpringBootTest(classes = [CrudApplication::class])
class CustomerIntegrationTest : TestContainer() {

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var customerController: CustomerController

    @BeforeEach
    fun setUp() {
        customerRepository.deleteAll()
    }

    @Test
    fun `should get customer list when customer has been added`() {
        customerRepository.saveAll(saveCustomersWithDifferentDescriptionForTest());
        val response = customerController.getAllCustomers();
        assertThat(response.body).isNotNull()
        assertThat(response.body).hasSize(4)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `should get empty list`() {
        val customerDtoList = customerController.getAllCustomers().body;
        assertThat(customerDtoList).isEmpty()
    }

    @Test
    fun `should create new customer`() {
        val customer = saveCustomerWithCorrectData();
        val response = customerController.createNewCustomer(customer.toCustomerDto())
        assertThat(response.body).isNotNull
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).hasOnlyFields("address", "email", "firstName", "id", "lastName")
    }

    @Test
    fun `should throw EmailExistsException when email exists in database`() {
        customerRepository.save(saveCustomerWithCorrectData());
        val exception = assertThrows<EmailExistsException> {
            customerController.createNewCustomer(saveCustomerWithCorrectData().toCustomerDto())
        }
        assertThat(exception.message).isEqualTo("Email already exists")
    }

    @Test
    fun `should throw IllegalArgumentException when any data is blank`() {
        val exception = assertThrows<DataBlankException> {
            customerController.createNewCustomer(saveCustomerWithBlankData().toCustomerDto())
        }
        assertThat(exception.message).isEqualTo("Customer data cannot contain empty fields")
    }


    @Test
    fun `should find customer by email`() {
        customerRepository.saveAll(saveCustomersWithDifferentDescriptionForTest())
        val response = customerController.findCustomerByCustomerEmail("annanowak@o2.pl")

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).hasOnlyFields("address", "email", "firstName", "id", "lastName")
        assertThat(response.body?.firstName).isEqualTo("Anna")
    }



    @Test
    fun `should throw CustomerNotFoundException when customer has not been found`() {
        val exception = assertThrows<CustomerNotFoundException> {
            customerController.findCustomerByCustomerEmail("")
        }
        assertThat(exception.message).isEqualTo("Customer not found")
    }

    @Test
    fun `should not update customer information when new information are blank`(){
        customerRepository.saveAll(saveCustomersWithDifferentDescriptionForTest())
        val customer = customerRepository.findAll().get(0)
        val customerDto = CustomerDto(customer.toCustomerDto().id,"","","","")
        val response = customerController.updateCustomerInformation(customerDto)
        val customerAfterUpdate = customerRepository.findAll()
            .filter{ customer:Customer -> customer.toCustomerDto().id == customer.toCustomerDto().id}.first()
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(customerAfterUpdate.toCustomerDto().firstName).isEqualTo(customer.toCustomerDto().firstName)
        assertThat(customerAfterUpdate.toCustomerDto().lastName).isEqualTo(customer.toCustomerDto().lastName)
        assertThat(customerAfterUpdate.toCustomerDto().address).isEqualTo(customer.toCustomerDto().address)
    }

    @Test
    fun `should delete customer`(){
        val customers = saveCustomersWithDifferentDescriptionForTest()
        customerRepository.saveAll(customers)
        val customer = customerRepository.findAll().get(0).toCustomerDto()
        val response = customerController.deleteCustomer(customer.id!!);
        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
    }

    @Test
    fun `should throw CustomerNotFoundException when customer not exists during customer delete`(){
        customerRepository.save(saveCustomerWithCorrectData());
        val exception = assertThrows<CustomerNotFoundException> { customerController.deleteCustomer(2L) }
        assertThat(exception.message).isEqualTo("Customer not found")
    }


    private fun saveCustomersWithDifferentDescriptionForTest(): List<Customer> {
        return mutableListOf(
            Customer(1L, "Szymon", "Pawlak", "szymonpawlak@o2.pl", "123 Main St, Warsaw, Poland"),
            Customer(2L, "Anna", "Nowak", "annanowak@o2.pl", "456 Green Ave, Krakow, Poland"),
            Customer(3L, "Jan", "Kowalski", "jankowalski@o2.pl", "789 Elm St, Gdansk, Poland"),
            Customer(4L, "Ewa", "Wiśniewska", "ewawisniewska@o2.pl", "101 Oak Blvd, Poznan, Poland")
        )
    }

    private fun saveCustomerWithCorrectData(): Customer {
        return Customer(5L, "Szymon", "Pawlak", "szymonpawlak@o2.pl", "123 Main St, Warsaw, Poland")
    }

    private fun saveCustomerWithBlankData(): Customer {
        return Customer(6L, " ", "Pawlak", "szymonpawlak96@o2.pl", "123 Main St, Warsaw, Poland")
    }

    private fun createCustomerDtoToUpdateCustomerInformation(): Customer{
        return Customer(1L, "Kamila", "Robaczewska", "ewawisniewska@o2.pl", "10 Oak Blvd, Poznan, Poland")
    }


}