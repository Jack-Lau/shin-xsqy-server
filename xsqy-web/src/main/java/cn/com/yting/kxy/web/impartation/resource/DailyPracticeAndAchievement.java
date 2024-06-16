/*
 * Created 2018-11-21 17:02:30
 */
package cn.com.yting.kxy.web.impartation.resource;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;

import lombok.Getter;

/**
 *
 * @author Azige
 */
@Getter
public class DailyPracticeAndAchievement extends DailyPracticeAndAchievementCollectionSupplier {

    @XmlAttribute
    private long id;
    @XmlElement
    private String name;
    @XmlElement
    private int type;
    @XmlElement
    private long award;
    @XmlElement
    private String description;
    @XmlElement
    private String feedback;
    @XmlElements(@XmlElement(name = "showAward", type = ShowAward.class))
    private List<ShowAward> showAwards;

    @Override
    public Collection<DailyPracticeAndAchievement> get() {
        return Collections.singleton(this);
    }

    @Getter
    public static class ShowAward {

        @XmlElement
        private long id;
        @XmlElement
        private long amount;
    }
}
