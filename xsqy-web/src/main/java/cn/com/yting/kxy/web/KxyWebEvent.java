/*
 * Created 2018-6-26 18:08:50
 */
package cn.com.yting.kxy.web;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.context.ApplicationEvent;

/**
 * 事件的基础类，方便用类型来查找
 *
 * @author Azige
 */
public class KxyWebEvent extends ApplicationEvent {

    public KxyWebEvent(Object source) {
        super(source);
    }

    @Override
    @JsonIgnore
    public Object getSource() {
        return super.getSource();
    }
}
