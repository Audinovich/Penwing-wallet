package com.Testing.practicasTesteo.service;

import com.Testing.practicasTesteo.entity.Wallet;

import java.util.List;

public interface WalletService {

    List<Wallet> getAllWallets();

    Wallet getWalletById(Long id);

    Wallet saveWallet(Wallet Wallet);

    Wallet updateWalletById(Wallet p, Long id);

    boolean deleteAllWallet();

    boolean deleteWalletById(Long id);


}
