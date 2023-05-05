package dev.draft.testcontainerspostgrestest;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.util.List;


@Testcontainers
@SpringBootTest
public class CustomerTest {

    @Container
    public static PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer("postgres:11.1")
                    .withDatabaseName("test")
                    .withUsername("root")
                    .withPassword("password");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @Autowired
    CustomerRepository customerRepository;


    /**
     * Initialize the table and mount data here
     */
    @BeforeAll
    public static void setUp() throws IOException, InterruptedException {
        /**
         * Empty because Flyway is being used to create table and insert test data
         * To test other methods, remove Flyway configuration
         * and run one of initializeViaMountingFilesInContainer or initializeViaExecInContainer
         */
    }

    /**
     * Creates a new table and inserts data using SQL instructions stored in a file
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public static void initializeViaMountingFilesInContainer() throws IOException, InterruptedException {
        String fileName = "customer-dataset.sql";

        postgreSQLContainer.copyFileToContainer(MountableFile.forClasspathResource(fileName),
                "/" + fileName);

        String[] insertCommand = {
                "bash",
                "-c",
                "PGPASSWORD=" + postgreSQLContainer.getPassword() +
                        " psql -U " + postgreSQLContainer.getUsername() +
                        " -d " + postgreSQLContainer.getDatabaseName() +
                        " -f " + fileName
        };

        postgreSQLContainer.execInContainer(insertCommand);
    }

    /**
     * Creates a new table and inserts data by running execInContainer one by one
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public static void initializeViaExecInContainer() throws IOException, InterruptedException {
        postgreSQLContainer.execInContainer(
                wrapCommand("create table customer\n" +
                        "(\n" +
                        "    id      serial primary key,\n" +
                        "    email   varchar(50)  not null,\n" +
                        "    name    varchar(100) not null,\n" +
                        "    country varchar(20)  not null,\n" +
                        "    age     int          not null\n" +
                        ");")
        );

        postgreSQLContainer.execInContainer(
                wrapCommand("insert into customer (name, email, country, age)\n" +
                        "values ('John Doe', 'john@doe.com', 'US', 25);")
        );

        postgreSQLContainer.execInContainer(
                wrapCommand("insert into customer (name, email, country, age)\n" +
                        "values ('Jane Doe', 'jane@doe.com', 'US', 27);")
        );

        postgreSQLContainer.execInContainer(
                wrapCommand("insert into customer (name, email, country, age)\n" +
                        "values ('Seamus Murphy', 's.murphy@abc.co', 'UK', 35);")
        );

        postgreSQLContainer.execInContainer(
                wrapCommand("insert into customer (name, email, country, age)\n" +
                        "values ('Toby Flenderson', 'toby.flenderson@dundermifflin.com', 'CR', 45);")
        );

        postgreSQLContainer.execInContainer(
                wrapCommand("insert into customer (name, email, country, age)\n" +
                        "values ('James Doe', 'james@email.com', 'US', 16);")
        );
    }

    /**
     * Helper method to wrap execInContainer commands with database name, user name, and password
     *
     * @param command
     * @return
     */
    public static String[] wrapCommand(String command) {
        return new String[]{
                "bash",
                "-c",
                "PGPASSWORD=" + postgreSQLContainer.getPassword() +
                        " psql -U " + postgreSQLContainer.getUsername() +
                        " -d " + postgreSQLContainer.getDatabaseName() +
                        " -c \"" + command + "\""
        };
    }

    /**
     * Basic test case to check if the data initialization method works and loads data correctly into the table
     */
    @Test
    public void checkIfTableIsCreated() {
        List<Customer> customerList = customerRepository.findAll();
        System.out.println("Customer table exists with size: " + customerList.size());
    }

    /**
     * Checks if the function {@link CustomerRepository#findByEmailAddress(String email) } works as expected
     */
    @Test
    public void checkIfFindByEmailWorks() {
        List<Customer> customerList = customerRepository.findByEmailAddress("jane@doe.com");

        Assertions.assertEquals(1, customerList.size());
        Assertions.assertEquals("Jane Doe", customerList.get(0).getName());
    }

    /**
     * Checks if the function {@link CustomerRepository#findMinorCustomers() } works as expected
     */
    @Test
    public void checkIfFindMinorCustomerWorks() {
        List<Customer> customerList = customerRepository.findMinorCustomers();

        Assertions.assertEquals(1, customerList.size());
        Assertions.assertEquals("James Doe", customerList.get(0).getName());
    }

    /**
     * Checks if the function {@link CustomerRepository#findLocalCustomers() } works as expected
     */
    @Test
    public void checkIfFindLocalCustomersWorks() {
        List<Customer> customerList = customerRepository.findLocalCustomers();

        Assertions.assertEquals(3, customerList.size());

        String[] expectedEmails = {"john@doe.com", "jane@doe.com", "james@email.com"};
        String[] actualEmails = {customerList.get(0).getEmail(), customerList.get(1).getEmail(), customerList.get(2).getEmail()};

        Assertions.assertArrayEquals(expectedEmails, actualEmails);
    }

    /**
     * Checks if the function {@link CustomerRepository#createNewLocalCustomer(String name, String email, int age) } works as expected
     */
    @Test
    public void checkIfCreateNewLocalCustomerWorks() {

        customerRepository.createNewLocalCustomer("Michael Scott", "michael@scott.com", 49);

        List<Customer> customerList = customerRepository.findByEmailAddress("michael@scott.com");

        Assertions.assertEquals(1, customerList.size());

    }
}
