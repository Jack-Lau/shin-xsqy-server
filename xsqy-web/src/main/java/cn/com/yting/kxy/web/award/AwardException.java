/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.award;

import cn.com.yting.kxy.web.KxyWebException;

/**
 *
 * @author Administrator
 */
public class AwardException extends KxyWebException {
    
    public static final int EC_INSUFFICIENT_XS = 4100;
    public static final int EC_INSUFFICIENT_YB = 4101;
    
    public AwardException(int errorCode, String message) {
        super(errorCode, message);
    }
    
    public static AwardException insufficientXS() {
        return new AwardException(EC_INSUFFICIENT_XS, "拥有的仙石不足");
    }
    
    public static AwardException insufficientYB() {
        return new AwardException(EC_INSUFFICIENT_YB, "拥有的元宝不足");
    }
    
}
