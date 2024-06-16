/*
 * Created 2018-6-30 16:04:02
 */
package cn.com.yting.kxy.web.apimodel;

import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
public class RequestParameter {

    private String type;
    private String name;
    private String description;
}
