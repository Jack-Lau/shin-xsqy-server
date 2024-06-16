/*
 * Created 2018-10-16 17:41:27
 */
package cn.com.yting.kxy.web.activity.resource;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import cn.com.yting.kxy.core.resetting.ResetType;
import cn.com.yting.kxy.core.resetting.Resetable;
import cn.com.yting.kxy.core.resource.Resource;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class ActivityOtherInfo implements Resource, Resetable {

    @XmlAttribute
    private long id;
    @XmlElement
    private String name;
    @XmlElement
    private int responseType;
    @XmlElement
    private String responseParameter;
    @XmlElement
    private String livenessRequirement;
    @XmlElement
    private int livenessAward;
    @XmlElement
    private int livenessReset;

    @Override
    public ResetType getResetType() {
        switch (livenessReset) {
            case 1:
                return ResetType.DAILY;
            case 2:
                return ResetType.WEEKLY;
            default:
                return ResetType.NEVER;
        }
    }
}
