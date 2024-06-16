/*
 * Created 2018-10-13 11:37:41
 */
package cn.com.yting.kxy.web.pet.resource;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class PetAddStarLoader extends XmlMapContainerResourceLoader<PetAddStar> {

    private Map<Integer, Map<Integer, PetAddStar>> levelToStageToPetAddStarMap;

    @Override
    protected void afterReload(ResourceContext context) {
        levelToStageToPetAddStarMap = getMap().values().stream()
            .collect(Collectors.groupingBy(
                it -> it.getStarLevel(),
                Collectors.toMap(it -> it.getStarStage(), Function.identity())
            ));
    }

    public PetAddStar getByLevelAndStage(int level, int stage) {
        return Optional.ofNullable(levelToStageToPetAddStarMap.get(level))
            .map(it -> it.get(stage))
            .orElseThrow(() -> new NoSuchElementException("不存在的宠物星阶级，level=" + level + ", stage=" + stage));
    }

    @Override
    public String getDefaultResourceName() {
        return "sPetAddStar.xml";
    }

    @Override
    public Class<PetAddStar> getSupportedClass() {
        return PetAddStar.class;
    }

}
