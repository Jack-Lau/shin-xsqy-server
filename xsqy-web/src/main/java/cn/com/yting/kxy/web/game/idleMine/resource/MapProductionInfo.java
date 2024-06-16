/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.game.idleMine.resource;

import cn.com.yting.kxy.core.resource.Resource;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import lombok.Getter;

/**
 *
 * @author Darkholme
 */
@Getter
public class MapProductionInfo implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private long produceCurrencyId;
    @XmlElements(
            @XmlElement(name = "expeditionTeam", type = ExpeditionTeam.class)
    )
    private List<ExpeditionTeam> expeditionTeam;
    @XmlElement
    private long kcUnitTime;
    @XmlElement
    private long goldUnitTime;
    @XmlElement
    private long kcFloatingPrice;
    @XmlElement
    private long goldFloatingPrice;
    @XmlElement
    private long expeditionRequireLevel;

    public ExpeditionTeam getExpeditionTeamById(long id) {
        if (expeditionTeam != null) {
            for (ExpeditionTeam et : expeditionTeam) {
                if (et.getId() == id) {
                    return et;
                }
            }
        }
        return null;
    }

    @Getter
    public static class ExpeditionTeam {

        @XmlElement
        private long id;
        @XmlElement
        private long efficiency;
        @XmlElement
        private long totalTime;

    }

}
