package com.example.coin_trade.domain.coin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CoinRepository extends JpaRepository<Coin, Long> {
    @Query("select c from Coin c where c.coin_code = :coin_code")
    Coin findByCoin_code(@Param("coin_code") String coin_code);
}
