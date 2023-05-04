package dev.draft.testcontainerspostgrestest;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.IOException;
import java.util.List;


@SpringBootTest
public class CustomerTest {

    @Autowired
    CustomerRepository customerRepository;


    /**
     * Basic test case to check if the customer table exists
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
        List<Customer> customerList = customerRepository.findByEmailAddress("janet@wheeler.com");

        Assertions.assertEquals(1, customerList.size());
        Assertions.assertEquals("Janet Wheeler", customerList.get(0).getName());
    }

    /**
     * Checks if the function {@link CustomerRepository#findMinorCustomers() } works as expected
     */
    @Test
    public void checkIfFindMinorCustomerWorks() {
        List<Customer> customerList = customerRepository.findMinorCustomers();

        Assertions.assertEquals(5, customerList.size());
        Assertions.assertEquals("Angela Perry", customerList.get(0).getName());
    }

    /**
     * Checks if the function {@link CustomerRepository#findLocalCustomers() } works as expected
     */
    @Test
    public void checkIfFindLocalCustomersWorks() {
        List<Customer> customerList = customerRepository.findLocalCustomers();

        Assertions.assertEquals(9, customerList.size());

        String[] expectedEmails = {"lauren@richards.com", "angela@perry.com", "vera@anderson.io"};
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

        Assertions.assertEquals(10, customerList.size());

    }
}
