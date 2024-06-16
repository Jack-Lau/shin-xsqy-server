/*
 * Created 2018-6-29 15:11:23
 */
package cn.com.yting.kxy.web.controller;

import cn.com.yting.kxy.web.KxyWebException;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author Azige
 */
public final class ControllerUtils {

    public static KxyWebException notFoundException() {
        return KxyWebException.notFound("查找的资源不存在");
    }

    public static Object noContent() {
        return ResponseEntity.noContent().build();
    }

    private ControllerUtils() {
    }

}
