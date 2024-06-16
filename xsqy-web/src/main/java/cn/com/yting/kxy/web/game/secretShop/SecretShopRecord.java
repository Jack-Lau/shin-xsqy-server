/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.secretShop;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 *
 * @author Darkholme
 */
@Entity
@Table(name = "secret_shop_record")
@Data
public class SecretShopRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "kc_pack_exchange_count")
    private int kcPackExchangeCount;
    @Column(name = "not_take_prizes")
    private String notTakePrizes;

    @Setter(AccessLevel.NONE)
    private transient List<Prize> notTakePrizeList;

    public List<Prize> getNotTakePrizes() {
        if (notTakePrizeList == null) {
            notTakePrizeList = new ArrayList<>();
            if (this.notTakePrizes != null && !"".equals(this.notTakePrizes)) {
                List<String> prizeStrList = Arrays.stream(this.notTakePrizes.split(","))
                        .collect(Collectors.toList());
                for (String str : prizeStrList) {
                    notTakePrizeList.add(Prize.fromString(str));
                }
            }
        }
        return notTakePrizeList;
    }

    public void setNotTakePrizes(List<Prize> prizes) {
        this.notTakePrizeList = prizes;
        this.notTakePrizes = "";
        for (int i = 0; i < notTakePrizeList.size(); i++) {
            if (i == 0) {
                this.notTakePrizes = notTakePrizeList.get(i).toString();
            } else {
                this.notTakePrizes += "," + notTakePrizeList.get(i).toString();
            }
        }
    }

    @Data
    public static class Prize {

        private long jackpotId;
        private long currencyId;
        private long currencyAmount;

        public Prize() {

        }

        public Prize(long jackpotId, long currencyId, long currencyAmount) {
            this.jackpotId = jackpotId;
            this.currencyId = currencyId;
            this.currencyAmount = currencyAmount;
        }

        public static Prize fromString(String str) {
            Prize prize = new Prize();
            List<String> elements = Arrays.stream(str.split("-"))
                    .collect(Collectors.toList());
            prize.setJackpotId(Long.parseLong(elements.get(0)));
            prize.setCurrencyId(Long.parseLong(elements.get(1)));
            prize.setCurrencyAmount(Long.parseLong(elements.get(2)));
            return prize;
        }

        @Override
        public String toString() {
            return jackpotId + "-" + currencyId + "-" + currencyAmount;
        }

    }

}
