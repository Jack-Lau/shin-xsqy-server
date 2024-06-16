/*
 * Created 2018-12-21 16:16:42
 */
package cn.com.yting.kxy.web.market;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cn.com.yting.kxy.web.message.WebMessageType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "consignment_marker", indexes = @Index(columnList = "consignment_id"))
@Data
@IdClass(ConsignmentMarker.PK.class)
@WebMessageType
public class ConsignmentMarker implements Serializable {

    @Id
    @Column(name = "account_id")
    private long accountId;

    @Id
    @ManyToOne
    @JoinColumn(name = "consignment_id")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Consignment consignment;

    public long getConsignmentId() {
        return consignment.getId();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PK implements Serializable {

        private long accountId;
        private long consignment;
    }
}
