package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.entity.Pizzeria;

import java.util.List;
import java.util.Optional;

public interface PizzeriaService {

    List<Pizzeria> getAllPizzerias();
    Pizzeria getPizzeriaByid(Long id);
    Pizzeria savePizzeria(Pizzeria pizzeria);
    Pizzeria updatePizzeriaById( Pizzeria p ,Long id);
    boolean deleteAllPizzeria();
    boolean deletePizzeriaById(Long id);


}
