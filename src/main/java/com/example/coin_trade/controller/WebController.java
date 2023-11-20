package com.example.coin_trade.controller;

import com.example.coin_trade.domain.coin.Coin;
import com.example.coin_trade.domain.price.Price;
import com.example.coin_trade.domain.wallet.Wallet;
import com.example.coin_trade.service.WebService;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class WebController {

    private final WebService webService;

    @GetMapping("/")
    public String mainPage(Model model) throws Exception {
        List<Coin> coinList = webService.findAllCoins();
        model.addAttribute("coinList", coinList); // 전체 코인 리스트 전달

        List<Price> priceList = new ArrayList<>();
        priceList = webService.findPriceList(coinList.get(0).getCoincode());
        model.addAttribute("priceList", priceList); // 코인 리스트의 첫번째 코인의 가격 정보 전달

        Optional<Wallet> wallet = webService.findRecentWallet();
        if (wallet.isPresent()) {
            Wallet myWallet = wallet.get();
            model.addAttribute("myWallet", myWallet);
        }
        return "main";
    }

    @RequestMapping(value = "/")
    public String BuyCoin(HttpServletRequest request) throws Exception {
        Optional<String> units_buy_opt = Optional.ofNullable(request.getParameter("buy_units"));
        if (units_buy_opt.isPresent()) {
            String unit_buy_str = units_buy_opt.get();
            BigDecimal units = new BigDecimal(unit_buy_str).abs();
            savePriceService.buyBTC(units, "BTC", "KRW");
        } else {
            Optional<String> units_sell_opt = Optional.ofNullable(request.getParameter("sell_units"));
            if (units_sell_opt.isPresent()) {
                String unit_sell_str = units_sell_opt.get();
                BigDecimal units = new BigDecimal(unit_sell_str).abs();
                savePriceService.sellBTC(units, "BTC", "KRW");
            }
        }
        return "redirect:";
    }

    @GetMapping("/coin/prices") // AJAX 구현을 위한 Price 데이터 전달 메소드
    public String getCoinPrices(Model model, @RequestParam String coinCode) throws Exception {

        List<Price> priceList = new ArrayList<>();
        priceList = webPageService.findPriceList(coinCode); // 코인코드를 파라미터로 받아, DB 조회 후 가격 정보를 전달
        model.addAttribute("priceList", priceList);
        return "main :: priceTable"; // thymeleaf AJAX 구현을 위해, 데이터가 변경 될 ":: ID" 추가
    }
}

}
