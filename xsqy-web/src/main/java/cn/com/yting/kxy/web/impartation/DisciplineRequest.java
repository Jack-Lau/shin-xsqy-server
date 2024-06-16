/*
 * Created 2018-11-20 18:09:05
 */
package cn.com.yting.kxy.web.impartation;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Table;

import cn.com.yting.kxy.web.message.WebMessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "discipline_request", indexes = @Index(columnList = "master_account_id"))
@Data
@IdClass(DisciplineRequest.PK.class)
@WebMessageType
public class DisciplineRequest implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Id
    @Column(name = "master_account_id")
    private long masterAccountId;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class PK implements Serializable {

        private long accountId;
        private long masterAccountId;
    }
}
