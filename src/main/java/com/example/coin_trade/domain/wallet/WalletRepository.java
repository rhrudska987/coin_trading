package com.example.coin_trade.domain.wallet;

import com.example.coin_trade.domain.coin.Coin;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    @Query("select w from Wallet w where w.coin = :coin order by w.id desc")
    List<Wallet> findAllByCoin(@Param("coin") Coin coin);
}
