/*
 * Created 2018-9-29 23:42:05
 */
package cn.com.yting.kxy.web.party;

import java.util.List;

import org.springframework.context.ApplicationEvent;

/**
 *
 * @author Azige
 */
public class SupportExpiredEvent extends ApplicationEvent {

    private List<SupportRelation> expiredSupportRelations;

    public SupportExpiredEvent(Object source, List<SupportRelation> expiredSupportRelations) {
        super(source);
        this.expiredSupportRelations = expiredSupportRelations;
    }

    public List<SupportRelation> getExpiredSupportRelations() {
        return expiredSupportRelations;
    }
}
