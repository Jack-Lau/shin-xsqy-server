/*
 * Created 2018-8-14 16:40:20
 */
package cn.com.yting.kxy.web.battle;

import java.util.Date;

import cn.com.yting.kxy.battle.BattleDirector;
import cn.com.yting.kxy.web.repository.LongId;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Data
public class BattleSession implements LongId {

    private Long id;
    private long accountId;
    private Date createTime;
    private Long battleDescriptorId;
    private BattleDirector battleDirector;

    public BattleResponse toBattleResponse() {
        return new BattleResponse(id, battleDirector.getBattleResult());
    }
}
