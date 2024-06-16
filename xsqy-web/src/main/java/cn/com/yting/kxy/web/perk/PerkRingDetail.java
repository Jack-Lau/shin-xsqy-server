/*
 * Created 2019-1-7 17:23:17
 */
package cn.com.yting.kxy.web.perk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import cn.com.yting.kxy.core.parameter.AggregateParameterSpace;
import cn.com.yting.kxy.core.parameter.ParameterSpace;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.message.WebMessageType;
import cn.com.yting.kxy.web.perk.resource.Talent;
import cn.com.yting.kxy.web.perk.resource.TalentTrainModel;
import cn.com.yting.kxy.web.perk.resource.TalentTrainModelLoader;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
@WebMessageType
public class PerkRingDetail {

    private PerkRing perkRing;
    private List<Perk> perks;

    public ParameterSpace toParameterSpace() {
        return new AggregateParameterSpace(perks.stream().map(Perk::getParameterSpace).collect(Collectors.toList()));
    }

    public static PerkRingDetail fromPerkRing(PerkRing perkRing, long schoolId, ResourceContext resourceContext) {
        List<Perk> perks;
        TalentTrainModel model = resourceContext.getByLoaderType(TalentTrainModelLoader.class).findModel(perkRing.getProgress());
        if (model == null) {
            perks = perkRing.exportPerkSelections().stream()
                .map(it -> new Perk(0, it, ParameterSpace.EMPTY))
                .collect(Collectors.toList());
        } else {
            Collection<Talent> talents = resourceContext.getLoader(Talent.class).getAll().values();
            perks = new ArrayList<>();
            List<PerkSelection> selections = perkRing.exportPerkSelections();
            for (int i = 0; i < selections.size(); i++) {
                int position = i + 1;
                Talent talent = talents.stream()
                    .filter(it -> it.getSchoolId() == schoolId && it.getPosition() == position)
                    .findAny().get();
                Perk perk = talent.createPerk(model.getPositionStar(i), selections.get(i));
                perks.add(perk);
            }
        }
        return new PerkRingDetail(perkRing, perks);
    }
}
