/*
 * Created 2019-1-21 15:53:07
 */
package cn.com.yting.kxy.web.game.fuxingjianglin;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.com.yting.kxy.core.random.RandomProvider;
import cn.com.yting.kxy.web.message.WebMessageType;
import cn.com.yting.kxy.web.repository.LongId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "fxjl_shared_record")
@Data
@WebMessageType
public class FxjlSharedRecord implements Serializable, LongId {

    @Id
    private Long id;
    @Column(name = "quest_id_1", nullable = false)
    @JsonIgnore
    private long questId_1;
    @Column(name = "quest_id_2", nullable = false)
    @JsonIgnore
    private long questId_2;
    @Column(name = "quest_id_3", nullable = false)
    @JsonIgnore
    private long questId_3;
    @Column(name = "quest_id_4", nullable = false)
    @JsonIgnore
    private long questId_4;
    @Column(name = "today_lucky_info_id", nullable = false)
    private long todayLuckyInfoId;

    public List<Long> getQuestIds() {
        return Arrays.asList(questId_1, questId_2, questId_3, questId_4);
    }

    public void randomizeQuestIds() {
        Random random = RandomProvider.getRandom();
        questId_1 = random.nextInt(10) + 730111;
        questId_2 = random.nextInt(10) + 730121;
        questId_3 = random.nextInt(10) + 730131;
        questId_4 = random.nextInt(10) + 730141;
    }
}
