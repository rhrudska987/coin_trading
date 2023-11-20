package com.example.coin_trade.service;

import com.example.coin_trade.domain.coin.Coin;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebService {

    @Value("")
    private String apiKey;


    public List<Coin> findAllCoins() {


    }
}
