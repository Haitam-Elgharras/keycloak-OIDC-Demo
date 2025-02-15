package com.enset.customerservice;

import com.enset.customerservice.entities.Customer;
import com.enset.customerservice.repositories.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }


    @Bean
    public CommandLineRunner commandLineRunner(CustomerRepository customerRepository) {
        return args -> {
            customerRepository.save(Customer.builder()
                    .name("Mohamed")
                    .email("mohamed@gmail.com")
                    .build());

            customerRepository.save(Customer.builder()
                    .name("Ali")
                    .email("ali@gmail.com")
                    .build());

            customerRepository.save(Customer.builder()
                    .name("Hassan")
                    .email("hassan@gmailcom")
                    .build());

            customerRepository.findAll().forEach(System.out::println);
        };
    }
}
