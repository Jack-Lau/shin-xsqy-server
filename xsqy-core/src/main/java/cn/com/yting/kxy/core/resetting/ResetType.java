/*
 * Created 2016-11-29 15:47:42
 */
package cn.com.yting.kxy.core.resetting;

import static cn.com.yting.kxy.core.resetting.ResetConstants.*;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Stream;

import org.springframework.scheduling.support.CronSequenceGenerator;

/**
 *
 * @author Azige
 */
public enum ResetType{

    NEVER(null),
    DAILY(CRON_DAILY),
    WEEKLY(CRON_WEEKLY),
    HOURLY(CRON_HOURLY),
    MONTHLY(CRON_MONTHLY);

    private final CronSequenceGenerator cronSequenceGenerator;

    private ResetType(String cronExpress){
        if (cronExpress != null){
            cronSequenceGenerator = new CronSequenceGenerator(cronExpress);
        }else{
            cronSequenceGenerator = null;
        }
    }

    public long getTimeToReset(long currentTime){
        if (cronSequenceGenerator == null){
            return 0;
        }else{
            return cronSequenceGenerator.next(Date.from(Instant.ofEpochSecond(currentTime))).toInstant().getEpochSecond() - currentTime;
        }
    }

    public static ResetType ofIndex(int index){
        return values()[index];
    }

    /**
     * 从集合中过滤出重置类型为此对象的元素
     *
     * @param <T>
     * @param collection
     * @return
     */
    public <T extends Resetable> Stream<T> filterStream(Collection<T> collection){
        return collection.stream()
            .filter(it -> this.equals(it.getResetType()));
    }
}
