/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.mineExploration;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Darkholme
 */
@Entity
@Table(name = "mine_exploration_record")
@Data
public class MineExplorationRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "in_game")
    private boolean inGame = false;
    @Column(name = "map", length = 1000)
    private String map;
    @Column(name = "mask", length = 1000)
    private String mask;
    @Column(name = "available_dig")
    private int availableDig;
    @Column(name = "coupon_take")
    private int couponTake;

    public static String[][] toMask(String mask) {
        String[][] toMask = new String[5][5];
        if (mask != null && !"".equals(mask)) {
            String[] rows = mask.split("#");
            for (int i = 0; i < rows.length; i++) {
                String[] columns = rows[i].split(",");
                for (int j = 0; j < columns.length; j++) {
                    toMask[i][j] = columns[j];
                }
            }
        }
        return toMask;
    }

    public static MineExplorationGrid[][] toMap(String map) {
        MineExplorationGrid[][] toMap = new MineExplorationGrid[5][5];
        for (int i = 0; i < toMap.length; i++) {
            for (int j = 0; j < toMap[i].length; j++) {
                toMap[i][j] = new MineExplorationGrid();
            }
        }
        if (map != null && !"".equals(map)) {
            String[] rows = map.split("#");
            for (int i = 0; i < rows.length; i++) {
                String[] columns = rows[i].split(",");
                for (int j = 0; j < columns.length; j++) {
                    MineExplorationGrid grid = new MineExplorationGrid();
                    String[] elements = columns[j].split("-");
                    if (elements.length >= 3) {
                        grid.type = Integer.parseInt(elements[0]);
                        grid.currencyId = Long.parseLong(elements[1]);
                        grid.amount = Long.parseLong(elements[2]);
                    }
                    toMap[i][j] = grid;
                }
            }
        }
        return toMap;
    }

    public static String fromMask(String[][] fromMask) {
        String mask = "";
        for (int i = 0; i < fromMask.length; i++) {
            for (int j = 0; j < fromMask[i].length; j++) {
                mask += fromMask[i][j];
                if (j < fromMask[i].length - 1) {
                    mask += ",";
                }
            }
            if (i < fromMask.length - 1) {
                mask += "#";
            }
        }
        return mask;
    }

    public static String fromMap(MineExplorationGrid[][] fromMap) {
        String map = "";
        for (int i = 0; i < fromMap.length; i++) {
            for (int j = 0; j < fromMap[i].length; j++) {
                map += fromMap[i][j].toString();
                if (j < fromMap[i].length - 1) {
                    map += ",";
                }
            }
            if (i < fromMap.length - 1) {
                map += "#";
            }
        }
        return map;
    }

    public int getCurrDigCount() {
        int canDigCount = 0;
        if (mask != null) {
            for (int i = 0; i < mask.length(); i++) {
                if (mask.charAt(i) == 'X') {
                    canDigCount++;
                }
            }
        }
        return MineExplorationConstants.MAX_DIG_COUNT - canDigCount;
    }

}
