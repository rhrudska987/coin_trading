package com.example.coin_trade.domain.coin;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinRepository extends JpaRepository<Coin, Long> {
}
