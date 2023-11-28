package com.example.coin_trade.domain.wallet;

import com.example.coin_trade.domain.coin.Coin;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double in_use_krw;
    private double in_use_coin;
    private double available_krw;
    private double available_coin;
    private double total_krw;
    private double total_coin;
    private double xcoin_last_coin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coinId")
    private Coin coin;


    @Builder
    public Wallet(double in_use_krw, double in_use_coin, double available_krw, double available_coin,
                  double total_krw, double total_coin, double xcoin_last_coin,
                  Coin coin) {
        this.in_use_krw = in_use_krw;
        this.in_use_coin = in_use_coin;
        this.available_krw = available_krw;
        this.available_coin = available_coin;
        this.total_krw = total_krw;
        this.total_coin = total_coin;
        this.xcoin_last_coin = xcoin_last_coin;
        this.coin = coin;
    }
}
