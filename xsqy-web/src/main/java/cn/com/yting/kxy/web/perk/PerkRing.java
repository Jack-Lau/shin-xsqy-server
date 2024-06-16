/*
 * Created 2019-1-7 11:41:50
 */
package cn.com.yting.kxy.web.perk;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 *
 * @author Azige
 */
@Entity
@Table(name = "perk_ring")
@Data
public class PerkRing implements Serializable {

    private static final List<Function<PerkRing, PerkSelection>> PERK_SELECTION_GETTERS = Arrays.asList(
        PerkRing::getPerkSelection_1,
        PerkRing::getPerkSelection_2,
        PerkRing::getPerkSelection_3,
        PerkRing::getPerkSelection_4,
        PerkRing::getPerkSelection_5,
        PerkRing::getPerkSelection_6,
        PerkRing::getPerkSelection_7,
        PerkRing::getPerkSelection_8,
        PerkRing::getPerkSelection_9
    );

    private static final List<BiConsumer<PerkRing, PerkSelection>> PERK_SELECTION_SETTERS = Arrays.asList(
        PerkRing::setPerkSelection_1,
        PerkRing::setPerkSelection_2,
        PerkRing::setPerkSelection_3,
        PerkRing::setPerkSelection_4,
        PerkRing::setPerkSelection_5,
        PerkRing::setPerkSelection_6,
        PerkRing::setPerkSelection_7,
        PerkRing::setPerkSelection_8,
        PerkRing::setPerkSelection_9
    );

    @Id
    @Column(name = "account_id")
    private long accountId;
    @Column(name = "progress", nullable = false)
    private int progress;
    @Column(name = "perk_selection_1", nullable = false)
    @Enumerated(EnumType.STRING)
    private PerkSelection perkSelection_1;
    @Column(name = "perk_selection_2", nullable = false)
    @Enumerated(EnumType.STRING)
    private PerkSelection perkSelection_2;
    @Column(name = "perk_selection_3", nullable = false)
    @Enumerated(EnumType.STRING)
    private PerkSelection perkSelection_3;
    @Column(name = "perk_selection_4", nullable = false)
    @Enumerated(EnumType.STRING)
    private PerkSelection perkSelection_4;
    @Column(name = "perk_selection_5", nullable = false)
    @Enumerated(EnumType.STRING)
    private PerkSelection perkSelection_5;
    @Column(name = "perk_selection_6", nullable = false)
    @Enumerated(EnumType.STRING)
    private PerkSelection perkSelection_6;
    @Column(name = "perk_selection_7", nullable = false)
    @Enumerated(EnumType.STRING)
    private PerkSelection perkSelection_7;
    @Column(name = "perk_selection_8", nullable = false)
    @Enumerated(EnumType.STRING)
    private PerkSelection perkSelection_8;
    @Column(name = "perk_selection_9", nullable = false)
    @Enumerated(EnumType.STRING)
    private PerkSelection perkSelection_9;

    public List<PerkSelection> exportPerkSelections() {
        return PERK_SELECTION_GETTERS.stream()
            .map(it -> it.apply(this))
            .collect(Collectors.toList());
    }

    public void importPerkSelections(List<PerkSelection> list) {
        if (list.size() != PERK_SELECTION_SETTERS.size()) {
            throw new IllegalArgumentException("列表长度不对");
        }
        for (int i = 0; i < PERK_SELECTION_SETTERS.size(); i++) {
            PERK_SELECTION_SETTERS.get(i).accept(this, list.get(i));
        }
    }

    public PerkSelection getPerkSelection(int index) {
        return PERK_SELECTION_GETTERS.get(index).apply(this);
    }

    public void setPerkSelection(int index, PerkSelection selection) {
        PERK_SELECTION_SETTERS.get(index).accept(this, selection);
    }
}
