package integration

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
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
            withReuse(true)
            start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgresqlContainer.jdbcUrl }
            registry.add("spring.datasource.username") { postgresqlContainer.username }
            registry.add("spring.datasource.password") { postgresqlContainer.password }
            registry.add("spring.jpa.hibernate.ddl-auto") { "update" }
        }
    }


}