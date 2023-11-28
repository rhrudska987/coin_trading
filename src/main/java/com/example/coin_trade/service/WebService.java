package com.example.coin_trade.service;

import com.example.coin_trade.domain.coin.Coin;
import com.example.coin_trade.domain.coin.CoinRepository;
import com.example.coin_trade.domain.price.Price;
import com.example.coin_trade.domain.price.PriceRepository;
import com.example.coin_trade.domain.wallet.Wallet;
import com.example.coin_trade.domain.wallet.WalletRepository;
import com.example.coin_trade.util.Api_Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WebService {

    private final CoinRepository coinRepository;
    private final PriceRepository priceRepository;
    private final WalletRepository walletRepository;

    @Value("${BITHUMB.REST.CONNECT-KEY}")
    private String connectKey;
    @Value("${BITHUMB.REST.SECRET-KEY}")
    private String secretKey;

    public List<Coin> findAllCoins() {
        return coinRepository.findAll();
    }

    public List<Price> findPriceList(String coinCode) {
        Coin coin = coinRepository.findByCoin_code(coinCode);
        return priceRepository.findByCoinOrderByCoinDesc(coin);
    }

    public Optional<Wallet> findRecentWallet() {
        List<Wallet> walletList = walletRepository.findAll();
        Long recentId = 0L;
        for (int i = 0; i < walletList.size(); i++) {
            if (walletList.get(i).getId() > recentId) {
                recentId = walletList.get(i).getId();
            }
        }
        return walletRepository.findById(recentId);
    }

    // 10분 동안 거래량 계산을 위한, 10분 전 가격 정보를 담을 Map
    static Map<String, Double> preVolumeMap = new HashMap<>();

    // 프로그램 실행 시 이전에 저장했던 가격 정보는 삭제
    @PostConstruct
    private void initDelAllPrices() throws Exception {
        log.info("[initDelAllPrices] 프로그램 최초 실행 시, 기존 Price 데이터 삭제 (지운 데이터: " + priceRepository.count() + ")");
        priceRepository.deleteAll();
        walletRepository.deleteAll();
        viewMyWallet("BTC");
    }

//    @Transactional(readOnly = true)
    public void viewMyWallet(String coinCode) {
        Coin coin = coinRepository.findByCoin_code(coinCode);
        Api_Client apiClient = new Api_Client(connectKey, secretKey);
        HashMap<String, String> rgParams = new HashMap<>();
        rgParams.put("currency", coinCode);
        coinCode = coinCode.toLowerCase();
        try {
            String result = apiClient.callApi("/info/balance", rgParams);
            System.out.println(result);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> m = (Map<String, String>) mapper.readValue(result, Map.class).get("data");
            while (Double.parseDouble(m.get("in_use_krw")) != 0 && Double.parseDouble(m.get("in_use_"+coinCode)) != 0) {
                m.clear();
                result = apiClient.callApi("/info/balance", rgParams);
                m = (Map<String, String>) mapper.readValue(result, Map.class).get("data");
            }
            Wallet myWallet = Wallet.builder()
                    .total_krw(Double.parseDouble(m.get("total_krw")))
                    .total_coin(Double.parseDouble(m.get("total_" + coinCode)))
                    .in_use_krw(Double.parseDouble(m.get("in_use_krw")))
                    .in_use_coin(Double.parseDouble(m.get("in_use_" + coinCode)))
                    .available_krw(Double.parseDouble(m.get("available_krw")))
                    .available_coin(Double.parseDouble(m.get("available_" + coinCode)))
                    .xcoin_last_coin(Double.parseDouble(m.get("xcoin_last_" + coinCode)))
                    .coin(coin)
                    .build();
            walletRepository.save(myWallet);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //    // 1분마다 코인의 가격과 거래량 정보를 저장
//    @Scheduled(cron = "30 0,3,6,9,12,15,18,21,24,27,30,33,36,39,42,45,48,51,54,57,58,59 * * * *")
    @Scheduled(fixedDelay = 50000)
    public void savePriceEvery3min() throws Exception {
        Price currentPrice;
        List<Coin> coinList = coinRepository.findAll();
        log.info("[savePriceEvery3min] 3분마다 가격 정보를 저장 (현재 시간: " + LocalDateTime.now() + ", 저장할 코인 수: " + coinList.size()
                + ")");

        double curPreGap = 0.0;

        for (Coin c : coinList) {
            currentPrice = getCoinPrice(c.getCoin_code());
            if (preVolumeMap.get(c.getCoin_code()) == null
                    || priceRepository.countByCoin(c) == 0) {// 최초 실행의 경우, 거래량은 0으로 현재가격을 저장
                preVolumeMap.put(c.getCoin_code(), currentPrice.getVolume());
                currentPrice.setVolume(0.0);
            } else { // 직전 데이터가 있는 경우
                log.info(currentPrice.toString() + " | " + preVolumeMap.toString());
                curPreGap =
                        currentPrice.getVolume() - preVolumeMap.get(c.getCoin_code()); // 현재 거래량에서 직전거래량을 빼면 10분 동안 거래량
                preVolumeMap.put(c.getCoin_code(), currentPrice.getVolume());
                if (curPreGap >= 0) {
                    currentPrice.setVolume(curPreGap);
                } else { // 매일 00:09:30의 경우, 빗썸 API가 00시 기준으로 리셋이 되기 때문에 그 전날 23:59:30~24:00:00의 30초동안 거래량은 버리고 00:00:00~00:09:30의 거래량만 저장
                    currentPrice.setVolume(currentPrice.getVolume());
                }
            }
            priceRepository.save(currentPrice); // DB에 저장
        }
    }

    public void buyBTC(BigDecimal units, String coinCode, String currency) throws Exception {
        String url = "/trade/market_buy";
        HashMap<String, String> params = new HashMap<>();
        params.put("units", "" + units);
        params.put("order_currency", coinCode);
        params.put("payment_currency", currency);
        System.out.println("coinCode: " + coinCode + ", currency: " + currency + ", units: " + units);
        Api_Client apiClient = new Api_Client(connectKey, secretKey);
        String result = apiClient.callApi(url, params);
        System.out.println(result);
//        viewMyWallet(coinCode);
    }

    public void sellBTC(BigDecimal units, String coinCode, String currency) throws Exception {
        String url = "/trade/market_sell";
        HashMap<String, String> params = new HashMap<>();
        params.put("units", "" + units);
        params.put("order_currency", coinCode);
        params.put("payment_currency", currency);
        System.out.println("coinCode: " + coinCode + ", currency: " + currency + ", units: " + units);
        Api_Client apiClient = new Api_Client(connectKey, secretKey);
        String result = apiClient.callApi(url, params);
        System.out.println(result);
//        viewMyWallet(coinCode);
    }

    // 빗썸 API를 통해 코인의 현재 가격 정보를 가져 옴
    private Price getCoinPrice(String coinCode) throws Exception {
        Coin coin = coinRepository.findByCoin_code(coinCode);
        Api_Client apiClient = new Api_Client(connectKey, secretKey);
        HashMap<String, String> rgParams = new HashMap<>();
        rgParams.put("payment_currency", "KRW");
        String result = apiClient.callApi("/public/ticker/" + coinCode + "_KRW", rgParams);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> m = (Map<String, String>) mapper.readValue(result, Map.class).get("data");
        Price price = Price.builder()
                .price(Double.parseDouble(m.get("closing_price")))
                .volume(Double.parseDouble(m.get("units_traded")))
                .date(LocalDateTime.now())
                .coin(coin)
                .build();
        return price;
    }
}
