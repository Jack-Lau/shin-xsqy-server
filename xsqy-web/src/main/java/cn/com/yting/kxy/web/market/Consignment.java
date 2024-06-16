/*
 * Created 2018-12-19 16:54:27
 */
package cn.com.yting.kxy.web.market;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.message.WebMessageType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "consignment", indexes = {
    @Index(columnList = "seller_account_id"),
    @Index(columnList = "buyer_account_id"),
    @Index(columnList = "sold, deadline, goods_type")
})
@Data
@WebMessageType
public class Consignment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "seller_account_id", nullable = false)
    private long sellerAccountId;
    @Column(name = "goods_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private GoodsType goodsType;
    @Column(name = "goods_object_id", nullable = false)
    private long goodsObjectId;
    @Column(name = "goods_definition_id", nullable = false)
    private long goodsDefinitionId;
    @Column(name = "price", nullable = false)
    private long price;
    @Column(name = "previous_price", nullable = false)
    private long previousPrice;
    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    @Column(name = "deadline")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deadline;
    @Column(name = "sold", nullable = false)
    private boolean sold;
    @Column(name = "deal_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dealTime;
    @Column(name = "buyer_account_id")
    private Long buyerAccountId;
    @Column(name = "goods_delivered", nullable = false)
    private boolean goodsDelivered;
    @Column(name = "payment_delivered", nullable = false)
    private boolean paymentDelivered;

    @OneToMany(mappedBy = "consignment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ConsignmentMarker> markers;

    @OneToOne(mappedBy = "consignment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private ConsignmentEquipmentInfo consignmentEquipmentInfo;

    @OneToMany(mappedBy = "consignment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ConsignmentEquipmentEffect> consignmentEquipmentEffects;

    @OneToOne(mappedBy = "consignment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private ConsignmentPetInfo consignmentPetInfo;

    @OneToMany(mappedBy = "consignment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ConsignmentPetAbility> consignmentPetAbilities;

    public boolean isOnSale(Date currentTime) {
        return !isSold() && deadline != null && deadline.compareTo(currentTime) > 0;
    }

    public void verifySeller(long accountId) {
        if (this.sellerAccountId != accountId) {
            throw KxyWebException.unknown("不是货品的售出者");
        }
    }

    public ConsignmentDetail toDetail() {
        return new ConsignmentDetail(this, markers.size());
    }
}
