package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.entity.Wallet;
import com.Testing.practicasTesteo.exceptions.NotSavedException;
import com.Testing.practicasTesteo.exceptions.WalletNotFoundException;
import com.Testing.practicasTesteo.respository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WalletServiceImpl implements WalletService {

   @Autowired
   private final WalletRepository walletRepository;

   public WalletServiceImpl(WalletRepository walletRepository){
       this.walletRepository = walletRepository;
   }


    @Override
    public List<Wallet> getAllWallets() {
        try {
            List<Wallet> walletList = walletRepository.findAll();
            if (walletList.isEmpty()) {
                throw new WalletNotFoundException("Wallet Not Found");
            }
            return walletList;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Wallet getWalletById(Long id) throws WalletNotFoundException {
        return walletRepository.findById(id).orElseThrow(() -> new WalletNotFoundException("Wallet ID" + id + "Not Found"));
    }

    @Override
    public Wallet saveWallet(Wallet Wallet) {
        try {
            return walletRepository.save(Wallet);
        } catch (Exception e) {
            throw new NotSavedException("Error saving Wallet" + e.getMessage());
        }

    }

    @Override
    public Wallet updateWalletById(Wallet w, Long id) {
        Optional<Wallet> walletFound = walletRepository.findById(id);
        if (walletFound.isPresent()) {
            Wallet updatedWallet = walletFound.get();
            updatedWallet.setName(w.getName());

            return walletRepository.save(updatedWallet);
        } else {
            throw new WalletNotFoundException("Wallet con ID " + id + "no encontrada.");
        }

    }

    @Override
    public boolean deleteAllWallet() {
        try {
            walletRepository.deleteAll();
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Wallet Not deleted" + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteWalletById(Long id) {

        try {
            Wallet WalletFound = walletRepository.findById(id)
                    .orElseThrow(() -> new WalletNotFoundException("Wallet ID " + id + "Not Found."));
            walletRepository.delete(WalletFound);
            return true;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }


}
