/*
 * Created 2018-11-8 10:56:00
 */
package cn.com.yting.kxy.web.title;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "title_granting_stats")
@Data
public class TitleGrantingStats implements Serializable {

    @Id
    @Column(name = "title_id")
    private long titleId;
    @Column(name = "granted_count")
    private int grantedCount;

    public void increaseGrantedCount() {
        grantedCount++;
    }
}
