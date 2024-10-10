package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.entity.Pizzeria;
import com.Testing.practicasTesteo.exceptions.NotSavedException;
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
        try {
            List<Pizzeria> pizzeriaList = pizzeriaRepository.findAll();
            if (pizzeriaList.isEmpty()) {
                throw new PizzeriaNotFoundException("Pizzeria Not Found");
            }
            return pizzeriaList;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Pizzeria getPizzeriaByid(Long id) throws PizzeriaNotFoundException {
        return pizzeriaRepository.findById(id).orElseThrow(() -> new PizzeriaNotFoundException("Pizzeria ID" + id + " Not Found"));
    }

    @Override
    public Pizzeria savePizzeria(Pizzeria pizzeria) {
        try {
            return pizzeriaRepository.save(pizzeria);
        } catch (Exception e) {
            throw new NotSavedException("Error saving Pizzeria" + e.getMessage());
        }

    }

    @Override
    public Pizzeria updatePizzeriaById(Pizzeria p, Long id) {
        Optional<Pizzeria> pizzeriaEncontrada = pizzeriaRepository.findById(id);
        if (pizzeriaEncontrada.isPresent()) {
            Pizzeria pizzeriaActualizada = pizzeriaEncontrada.get();
            pizzeriaActualizada.setName(p.getName());
            pizzeriaActualizada.setAddress((p.getAddress()));
            return pizzeriaRepository.save(pizzeriaActualizada);
        } else {
            throw new PizzeriaNotFoundException("Pizzeria con ID " + id + "no encontrada.");
        }

    }

    @Override
    public boolean deleteAllPizzeria() {
        try {
            pizzeriaRepository.deleteAll();
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Pizzeria Not deleted" + e.getMessage(), e);
        }
    }

    @Override
    public boolean deletePizzeriaById(Long id) {

        try {
            Pizzeria pizzeriaFound = pizzeriaRepository.findById(id)
                    .orElseThrow(() -> new PizzeriaNotFoundException("Pizzeria ID " + id + "Not Found."));
            pizzeriaRepository.delete(pizzeriaFound);
            return true;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }


}
