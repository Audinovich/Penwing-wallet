package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.entity.Pizzeria;
import com.Testing.practicasTesteo.exceptions.PizzeriaNotFoundException;
import com.Testing.practicasTesteo.respository.PizzeriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PizzeriaServiceImpl implements PizzeriaService {

    @Autowired
    PizzeriaRepository pizzeriaRepository;

    @Override
    public List<Pizzeria> getAllPizzerias() {
        return pizzeriaRepository.findAll();
    }

    @Override
    public Optional<Pizzeria> getPizzeriaByid(Long id) {
        return pizzeriaRepository.findById(id);
    }

    @Override
    public Pizzeria savePizzeria(Pizzeria p) {
        return pizzeriaRepository.save(p);
    }

    @Override
    public boolean deleteAllPizzeria() {
        try {
            pizzeriaRepository.deleteAll();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deletePizzeriaById(Long id) {

        try {
            Optional<Pizzeria> p = getPizzeriaByid(id);
            pizzeriaRepository.delete(p.get());
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Optional<Pizzeria> UpdatePizzeriaById(Pizzeria p, Long id) {
        Optional<Pizzeria> pizzeriaEncontrada = pizzeriaRepository.findById(id);
        if (pizzeriaEncontrada.isPresent()) {
            Pizzeria pizzeriaActualizada = pizzeriaEncontrada.get();
            pizzeriaActualizada.setName(p.getName());
            pizzeriaActualizada.setAddress((p.getAddress()));
            return Optional.of(pizzeriaRepository.save(pizzeriaActualizada));
        } else {
            throw new PizzeriaNotFoundException("Pizzeria con ID " + id + "no encontrada.");
        }

    }
}
