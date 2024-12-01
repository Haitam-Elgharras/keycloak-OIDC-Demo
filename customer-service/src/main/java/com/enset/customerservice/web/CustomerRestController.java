package com.enset.customerservice.web;

import com.enset.customerservice.entities.Customer;
import com.enset.customerservice.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerRestController {
    private final CustomerRepository customerRepository;

    @GetMapping("/")
    @PreAuthorize("hasAuthority('USER')")
    public List<Customer> customers() {
        return customerRepository.findAll();
    }

    @GetMapping("/{id}")
    public Customer getCustomer(@PathVariable Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    // get auth session
    @GetMapping("/mySession")
    public Authentication getAuth(Authentication authentication) {
        return authentication;
    }

}
