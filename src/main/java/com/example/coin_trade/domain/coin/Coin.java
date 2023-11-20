package com.example.coin_trade.domain.coin;

import com.example.coin_trade.domain.price.Price;
import com.example.coin_trade.domain.wallet.Wallet;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Coin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String coin_code;

    private String coin_name;

    @OneToMany(mappedBy = "coin", cascade = CascadeType.ALL)
    private List<Price> prices = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "walletId")
    private Wallet wallet;

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
        wallet.getCoins().add(this);
    }

    public Coin(String coin_code, String coin_name, Wallet wallet) {
        this.coin_code = coin_code;
        this.coin_name = coin_name;
        this.setWallet(wallet);
    }
}
