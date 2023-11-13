package com.example.coin_trade.domain.wallet;

import com.example.coin_trade.domain.coin.Coin;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
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

    private String in_use_krw;
    private String in_use_btc;
    private String available_krw;
    private String available_btc;
    private String total_krw;
    private String total_btc;
    private String xcoin_last_btc;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL)
    private List<Coin> coins = new ArrayList<>();

    @Builder
    public Wallet(String in_use_krw, String in_use_btc, String available_krw, String available_btc,
                  String total_krw, String total_btc, String xcoin_last_btc,
                  List<Coin> coins) {
        this.in_use_krw = in_use_krw;
        this.in_use_btc = in_use_btc;
        this.available_krw = available_krw;
        this.available_btc = available_btc;
        this.total_krw = total_krw;
        this.total_btc = total_btc;
        this.xcoin_last_btc = xcoin_last_btc;
        this.coins = coins;
    }
}
