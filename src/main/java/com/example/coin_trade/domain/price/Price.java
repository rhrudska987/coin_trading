package com.example.coin_trade.domain.price;

import com.example.coin_trade.domain.coin.Coin;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String price;

    private String volume;

    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coinId")
    private Coin coin;

    public void setCoin(Coin coin) {
        this.coin = coin;
        coin.getPrices().add(this);
    }

    @Builder
    public Price(String price, String volume, LocalDateTime date, Coin coin) {
        this.price = price;
        this.volume = volume;
        this.date = date;
        this.setCoin(coin);
    }
}
