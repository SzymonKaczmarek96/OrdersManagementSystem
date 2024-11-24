import Kotlin.crud.customer.Customer
import Kotlin.crud.customer.CustomerController
import Kotlin.crud.customer.CustomerRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers


@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
open class TestContainer {

    companion object {
        private val postgresqlContainer = PostgreSQLContainer<Nothing>("postgres:13").apply {
            withDatabaseName("orders_manager")
            withUsername("user")
            withPassword("user")
            withExposedPorts(5432)
            start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgresqlContainer.jdbcUrl }
            registry.add("spring.datasource.username") { postgresqlContainer.username }
            registry.add("spring.datasource.password") { postgresqlContainer.password }
        }
    }


}