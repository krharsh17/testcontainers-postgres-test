package dev.draft.testcontainerspostgrestest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query(value = "select * from customer where email = ?1", nativeQuery = true)
    List<Customer> findByEmailAddress(String emailAddress);

    @Query(value = "select * from customer where age < 18", nativeQuery = true)
    List<Customer> findMinorCustomers();

    @Query(value = "select * from customer where country = 'US'", nativeQuery = true)
    List<Customer> findLocalCustomers();

    @Modifying
    @Transactional
    @Query(value = "insert into customer (name, email, country, age) values (?1, ?2, 'US', ?3)", nativeQuery = true)
    void createNewLocalCustomer(String name, String email, int age);

}
