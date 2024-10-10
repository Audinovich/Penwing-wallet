package com.Testing.practicasTesteo.respository;

import com.Testing.practicasTesteo.entity.Wallet;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet,Long> {
}
