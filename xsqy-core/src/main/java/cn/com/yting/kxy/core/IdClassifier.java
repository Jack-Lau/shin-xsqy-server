/*
 * Created 2016-1-6 16:22:12
 */
package cn.com.yting.kxy.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.xml.bind.JAXB;

import io.github.azige.mgxy.ism.Section;
import io.github.azige.mgxy.ism.SectionXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Azige
 */
public class IdClassifier{

    private static final Logger LOG = LoggerFactory.getLogger(IdClassifier.class);
    private static final IdClassifier INSTANCE = new IdClassifier();
    private List<Section> sections;
    private Map<Section, Class<?>> typeMap = new HashMap<>();

    private IdClassifier(){
        try (InputStream input = getClass().getResourceAsStream("/id-section.xml")){
            SectionXml sectionXml = JAXB.unmarshal(input, SectionXml.class);
            this.sections = sectionXml.getSections();
            assert sections != null;
        }catch (IOException ex){
            LOG.error("", ex);
        }
        init();
    }

    private IdClassifier(List<Section> sections){
        this.sections = sections;
        init();
    }

    private void init(){
        sections.sort(Comparator.comparingLong(section -> section.getRange().getStart()));
        for (Section section : sections){
            String className = section.getClassName();
            if (className != null && !className.equals("")){
                try{
                    Class<?> type = Class.forName(className);
                    typeMap.put(section, type);
                }catch (ClassNotFoundException ex){
                    LOG.warn("找不到id段[{},{}]对应的类{}", section.getRange().getStart(), section.getRange().getEnd(), className);
                }
            }
        }
    }

    public static IdClassifier getInstance(){
        return INSTANCE;
    }

    /**
     *
     * @param id
     * @return
     * @deprecated 推荐用 {@link #classifyType(long) } 直接获得类型
     */
    @Deprecated
    public String classify(long id){
        Section section = findSection(id);
        if (section != null){
            return section.getClassName();
        }else{
            throw new IllegalStateException("无法识别类型的 id：" + id);
        }
    }

    public Class<?> classifyType(long id){
        Section section = findSection(id);
        return typeMap.get(section);
    }

    /**
     * 按指定的类型切分出能够识别部分类型的类型识别器。
     *
     * @param types
     * @return
     */
    @SuppressWarnings("unchecked")
    public IdClassifier copyPart(Class<?>... types){
        HashSet<Class<?>> typeSet = new HashSet<>(Arrays.asList(types));
        return copyPart(it -> typeSet.contains(it));
    }

    /**
     * 按给定的过滤器切分出能够识别部分类型的类型识别器。
     * 用于在只识别特定的部分类型的场合下提高识别效率。
     *
     * @param filters
     * @return
     */
    @SuppressWarnings("unchecked")
    public IdClassifier copyPart(Predicate<Class<?>>... filters){
        Predicate<Class<?>> filter = Stream.of(filters).reduce(a -> false, Predicate::or);
        List<Section> pickedSections = typeMap.entrySet().stream()
            .filter(entry -> filter.test(entry.getValue()))
            .map(entry -> entry.getKey())
            .collect(Collectors.toList());
        return new IdClassifier(pickedSections);
    }

    private Section findSection(long id){
        return sections.stream()
            .filter(section -> id >= section.getRange().getStart() && id <= section.getRange().getEnd())
            .findFirst().orElse(null);
    }
}
