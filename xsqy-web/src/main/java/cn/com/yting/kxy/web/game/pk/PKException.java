/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.pk;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Administrator
 */
public class PKException extends KxyWebException {

    public static final int EC_REQUEST_EXIST = 3700;
    public static final int EC_REQUEST_NOT_EXIST = 3701;
    public static final int EC_SENDER_BUSY = 3702;
    public static final int EC_RECEIVER_BUSY = 3703;

    public PKException(int errorCode, String message) {
        super(errorCode, message);
    }

    public static PKException requestExist() {
        return new PKException(EC_REQUEST_EXIST, "发起者存在未过期的切磋请求");
    }

    public static PKException requestNotExist() {
        return new PKException(EC_REQUEST_NOT_EXIST, "不存在该切磋请求");
    }

    public static PKException senderBusy() {
        return new PKException(EC_SENDER_BUSY, "发起者离线或正忙");
    }

    public static PKException receiverBusy() {
        return new PKException(EC_RECEIVER_BUSY, "接收者离线或正忙");
    }

}
