/*
 * Created 2018-10-16 18:24:33
 */
package cn.com.yting.kxy.web.game.kuaibidazhuanpan;

import cn.com.yting.kxy.web.KxyWebEvent;

/**
 *
 * @author Azige
 */
public class KbdzpMadeTurnEvent extends KxyWebEvent {

    private final KbdzpRecord kbdzpRecord;

    public KbdzpMadeTurnEvent(Object source, KbdzpRecord kbdzpRecord) {
        super(source);
        this.kbdzpRecord = kbdzpRecord;
    }

    public KbdzpRecord getKbdzpRecord() {
        return kbdzpRecord;
    }
}
