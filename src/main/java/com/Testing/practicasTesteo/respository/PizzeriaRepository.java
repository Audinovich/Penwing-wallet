package com.Testing.practicasTesteo.respository;

import com.Testing.practicasTesteo.entity.Pizzeria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PizzeriaRepository extends JpaRepository<Pizzeria,Long> {
}
