/*
 * Created 2018-11-13 18:32:41
 */
package cn.com.yting.kxy.web.pet.resource;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import cn.com.yting.kxy.core.resource.Resource;
import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class GodPet implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private String petName;
    @XmlElement
    private long prototypeId;
    @XmlElement
    private int 生命资质;
    @XmlElement
    private int 攻击资质;
    @XmlElement
    private int 物防资质;
    @XmlElement
    private int 速度资质;
    @XmlElement
    private int 法防资质;
    @XmlElement
    private int nowStarLevel;
    @XmlElement
    private int nowStarStage;
    @XmlElement
    private int maxStarLevel;
    @XmlElement
    private long initialSkill;
    @XmlElement
    private String description;
    @XmlElement
    private int limitedQuantity;
    @XmlElement
    private Integer nowNumber;
}
