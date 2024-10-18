package com.Testing.practicasTesteo.respository;

import com.Testing.practicasTesteo.entity.Wallet;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet,Long> {


}
