package com.Testing.practicasTesteo.controller;


import com.Testing.practicasTesteo.entity.Pizzeria;
import com.Testing.practicasTesteo.service.PizzeriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("Pizzerias")
public class PizzeriaController {

    @Autowired
    PizzeriaService pizzeriaService;

    @GetMapping("/getAll")
    public List<Pizzeria> findAllPizzerias(){
        return pizzeriaService.getAllPizzerias();
    }

    @PostMapping("/save")
    public Pizzeria savePizzeria(@RequestBody Pizzeria p){
        return pizzeriaService.savePizzeria(p);
    }
    @DeleteMapping("/deleteAll")
    public String deleteAllPizzeria(){
        boolean getPizzeria = pizzeriaService.deleteAllPizzeria();
        if(getPizzeria){
            return "Se han eliminado todas las pizzerias";
        }else{
            return "No se  han podido eliminar todas las Pizzerias";
        }
    }

    @DeleteMapping("/delete/{id}")
    public String deletePizzeriaById(@PathVariable("id") Long id){
        if (pizzeriaService.deletePizzeriaById(id)){
            return "Se han eliminado  la pizzeria";
        }else{
            return "No se  ha eliminado  la pizzeria";
        }
    }

    @PutMapping("/update/{id}")
    public Optional<Pizzeria> updatePizzeriaById(@RequestBody Pizzeria p , @PathVariable("id") long id){
    return pizzeriaService.UpdatePizzeriaById(p,id);
    }


}
