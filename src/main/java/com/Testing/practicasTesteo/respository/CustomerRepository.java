package com.Testing.practicasTesteo.respository;

import com.Testing.practicasTesteo.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository <Customer , Long> {
}
