package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.entity.Pizzeria;

import java.util.List;
import java.util.Optional;

public interface PizzeriaService {

    List<Pizzeria> getAllPizzerias();

    Pizzeria savePizzeria(Pizzeria p);

    Optional<Pizzeria> getPizzeriaByid(Long id);

    boolean deleteAllPizzeria();

    boolean deletePizzeriaById(Long id);

    Optional <Pizzeria> UpdatePizzeriaById( Pizzeria p ,Long id);
}
