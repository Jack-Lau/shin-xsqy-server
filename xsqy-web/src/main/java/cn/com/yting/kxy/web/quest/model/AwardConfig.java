/*
 * Created 2016-6-14 17:05:46
 */
package cn.com.yting.kxy.web.quest.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Azige
 */
public class AwardConfig{

    private static final Pattern PATTERN = Pattern.compile("([^_]+)_(\\d+)");

    private final String results;
    private final long awardId;

    public AwardConfig(String results, long awardId){
        this.results = results;
        this.awardId = awardId;
    }

    public String getResults(){
        return results;
    }

    public long getAwardId(){
        return awardId;
    }

    public static List<AwardConfig> fromText(String text){
        List<AwardConfig> list = new ArrayList<>();
        Matcher matcher = PATTERN.matcher(text);
        while (matcher.find()){
            String results = matcher.group(1);
            long awardId = Long.parseLong(matcher.group(2));
            list.add(new AwardConfig(results, awardId));
        }
        return list;
    }
}
