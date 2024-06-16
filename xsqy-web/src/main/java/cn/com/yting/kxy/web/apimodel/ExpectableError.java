/*
 * Created 2018-7-3 16:51:54
 */
package cn.com.yting.kxy.web.apimodel;

import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
public class ExpectableError {

    private int errorCode;
    private String description;
}
