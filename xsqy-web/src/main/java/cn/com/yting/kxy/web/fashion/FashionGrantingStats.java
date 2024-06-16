/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.web.fashion;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Darkholme
 */
@Entity
@Table(name = "fashion_granting_stats")
@Data
public class FashionGrantingStats implements Serializable {

    @Id
    @Column(name = "definition_id")
    private long definitionId;
    @Column(name = "granted_count")
    private int grantedCount;

    public void increaseGrantedCount() {
        grantedCount++;
    }

}
