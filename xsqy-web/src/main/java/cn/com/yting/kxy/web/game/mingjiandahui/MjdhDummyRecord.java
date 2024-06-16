/*
 * Created 2018-12-19 12:53:59
 */
package cn.com.yting.kxy.web.game.mingjiandahui;

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
@Table(name = "mjdh_dummy_record")
@Data
public class MjdhDummyRecord implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
}
