package com.example.coin_trade.domain.price;

import com.example.coin_trade.domain.coin.Coin;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PriceRepository extends JpaRepository<Price, Long> {

    List<Price> findByCoinOrderByCoinDesc(Coin coin);

    Long countByCoin(Coin coin);
}
