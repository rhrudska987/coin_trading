package com.example.coin_trade.controller;

import com.example.coin_trade.domain.coin.Coin;
import com.example.coin_trade.domain.coin.CoinRepository;
import com.example.coin_trade.domain.price.Price;
import com.example.coin_trade.domain.wallet.Wallet;
import com.example.coin_trade.domain.wallet.WalletRepository;
import com.example.coin_trade.service.WebService;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
//@RestController
@Controller
//@RequestMapping("/api")
public class WebController {

    private final WebService webService;
    private final WalletRepository walletRepository;
    private final CoinRepository coinRepository;

    @GetMapping("/")
    public String mainPage(Model model) throws Exception {
        List<Coin> coinList = webService.findAllCoins();
        model.addAttribute("coinList", coinList); // 전체 코인 리스트 전달

        List<Price> priceList = webService.findPriceList(coinList.get(0).getCoin_code());
        model.addAttribute("priceList", priceList); // 코인 리스트의 첫번째 코인의 가격 정보 전달

        Optional<Wallet> wallet = webService.findRecentWallet();
        if(wallet.isPresent()){
            Wallet myWallet = wallet.get();
            model.addAttribute("myWallet", myWallet);
        }
        return "main";
    }

    @GetMapping("/view-my-wallet")
    public String viewMyWallet(@RequestParam("coinCode") String coinCode, Model model) {
        webService.viewMyWallet(coinCode);
        List<Coin> coinList = webService.findAllCoins();
        model.addAttribute("coinList", coinList); // 전체 코인 리스트 전달

        List<Price> priceList = webService.findPriceList(coinList.get(0).getCoin_code());
        model.addAttribute("priceList", priceList); // 코인 리스트의 첫번째 코인의 가격 정보 전달

        Coin coin = coinRepository.findByCoin_code(coinCode);
        List<Wallet> walletList = walletRepository.findAllByCoin(coin);
        Wallet myWallet = walletList.get(0);
        model.addAttribute("myWallet", myWallet);

        return "main";
    }

    @RequestMapping(value = "/buy-sell")
    public String BuyCoin(HttpServletRequest request, Model model) throws Exception {
        Optional<String> units_buy_opt = Optional.ofNullable(request.getParameter("buy_units"));
        if (units_buy_opt.isPresent()) {
            Optional<String> coinCode = Optional.ofNullable(request.getParameter("coin_code"));
            String unit_buy_str = units_buy_opt.get();
            BigDecimal units = new BigDecimal(unit_buy_str).abs();
            webService.buyBTC(units, coinCode.get(), "KRW");
            Coin coin = coinRepository.findByCoin_code(coinCode.get());
            List<Wallet> walletList = walletRepository.findAllByCoin(coin);
            Wallet myWallet = walletList.get(0);
            model.addAttribute("myWallet", myWallet);
        } else {
            Optional<String> units_sell_opt = Optional.ofNullable(request.getParameter("sell_units"));
            Optional<String> coinCode = Optional.ofNullable(request.getParameter("coin_code"));
            if (units_sell_opt.isPresent()) {
                String unit_sell_str = units_sell_opt.get();
                BigDecimal units = new BigDecimal(unit_sell_str).abs();
                webService.sellBTC(units, coinCode.get(), "KRW");
                Coin coin = coinRepository.findByCoin_code(coinCode.get());
                List<Wallet> walletList = walletRepository.findAllByCoin(coin);
                Wallet myWallet = walletList.get(0);
                model.addAttribute("myWallet", myWallet);
            }
        }
//        return "main";
        return "redirect:";
    }

    @GetMapping("/coin/prices") // AJAX 구현을 위한 Price 데이터 전달 메소드
    public String getCoinPrices(Model model, @RequestParam String coinCode) throws Exception {

        List<Price> priceList = webService.findPriceList(coinCode); // 코인코드를 파라미터로 받아, DB 조회 후 가격 정보를 전달
        model.addAttribute("priceList", priceList);
        return "main :: priceTable"; // thymeleaf AJAX 구현을 위해, 데이터가 변경 될 ":: ID" 추가
    }
}
