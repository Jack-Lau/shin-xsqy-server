/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.currency.kuaibi;

/**
 *
 * @author Darkholme
 */
public final class KuaibiConstants {

    public static final double DEPOSIT_MAINTENANCE_RATE = 0.05;
    public static final double DEPOSIT_DESTROY_RATE = 0.45;
    public static final double DEPOSIT_AIRDROP_RATE = 0.45;

    public static final double DEPOSIT_PLAYER_INTERACTIVE_MAINTENANCE_RATE = 0.02;
    public static final double DEPOSIT_PLAYER_INTERACTIVE_RATE = 0.95;

    public static enum DepositType {
        ACTURAL_COST,
        PLAYER_INTERACTIVE
    }

}
