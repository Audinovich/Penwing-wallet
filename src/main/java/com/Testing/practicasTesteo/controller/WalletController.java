package com.Testing.practicasTesteo.controller;


import com.Testing.practicasTesteo.entity.Wallet;
import com.Testing.practicasTesteo.exceptions.WalletNotFoundException;
import com.Testing.practicasTesteo.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    WalletService walletService;

    @GetMapping("/getAll")
    public ResponseEntity<List<Wallet>> findAllWallets() {
        try {
            List<Wallet> walletFound = walletService.getAllWallets();
            return new ResponseEntity<>(walletFound, HttpStatus.OK);

        } catch (WalletNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    
    @GetMapping("/getWallet/{walletId}")
    public Wallet getWalletById(@PathVariable("walletId") long walletId) {
        return walletService.getWalletById(walletId);
    }


    @PutMapping("/updateWallet/{walletId}")
    public Wallet updateWalletById(@RequestBody Wallet p, @PathVariable("walletId") long walletId) {
        return walletService.updateWalletById(p, walletId);
    }

    @PostMapping("/saveWallet")
    public Wallet saveWallet(@RequestBody Wallet p) {
        return walletService.saveWallet(p);
    }

    @DeleteMapping("/deleteAll")
    public String deleteAllWallet() {
        boolean getWallet = walletService.deleteAllWallet();
        if (getWallet) {
            return "Se han eliminado todas las Wallets";
        } else {
            return "No se  han podido eliminar todas las Wallets";
        }
    }

    @DeleteMapping("/delete/{id}")
    public String deleteWalletById(@PathVariable("id") Long id) {
        if (walletService.deleteWalletById(id)) {
            return "Se han eliminado  la Wallet";
        } else {
            return "No se  ha eliminado  la Wallet";
        }
    }


}
