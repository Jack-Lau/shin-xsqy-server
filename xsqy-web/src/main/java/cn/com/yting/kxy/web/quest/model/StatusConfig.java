/*
 * Created 2016-6-14 16:55:40
 */
package cn.com.yting.kxy.web.quest.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Azige
 */
public class StatusConfig{

    private static final Pattern PATTERN = Pattern.compile("([^,_]+)_(\\d+)_([^,]+)");
    public static final String TRANSFER_COMPLETED = "已完成";
    public static final String TRANSFER_RESET = "未领取";

    private static final Set<String> VALID_TRANSFER = new HashSet<>(
        Arrays.asList(TRANSFER_COMPLETED, TRANSFER_RESET)
    );

    private final String results;
    private final long questId;
    private final String transfer;

    public StatusConfig(String results, long questId, String transfer){
        if (!VALID_TRANSFER.contains(transfer)){
            throw new IllegalArgumentException("无效的目标状态：" + transfer);
        }
        this.results = results;
        this.questId = questId;
        this.transfer = transfer;
    }

    public String getResults(){
        return results;
    }

    public long getQuestId(){
        return questId;
    }

    public String getTransfer(){
        return transfer;
    }

    public static List<StatusConfig> fromText(String text){
        List<StatusConfig> list = new ArrayList<>();
        Matcher matcher = PATTERN.matcher(text);
        while (matcher.find()){
            String results = matcher.group(1);
            long questId = Long.parseLong(matcher.group(2));
            String transfer = matcher.group(3);
            list.add(new StatusConfig(results, questId, transfer));
        }
        return list;
    }

    public String toText(){
        return results + "_" + questId + "_" + transfer;
    }
}
