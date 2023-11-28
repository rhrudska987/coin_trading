package com.example.coin_trade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CoinTradeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoinTradeApplication.class, args);
    }

}
