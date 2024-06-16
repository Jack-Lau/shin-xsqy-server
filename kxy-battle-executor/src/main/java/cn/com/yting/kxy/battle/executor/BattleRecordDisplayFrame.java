/*
 * Created 1517-2-19 12:38:14
 */
package cn.com.yting.kxy.battle.executor;

import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.buff.Buff;
import java.util.List;

import cn.com.yting.kxy.battle.record.ActionRecord;
import cn.com.yting.kxy.battle.record.AffectRecord;
import cn.com.yting.kxy.battle.skill.resource.SkillParam;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.ResourceContextHolder;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 *
 * @author Azige
 */
public class BattleRecordDisplayFrame extends javax.swing.JFrame {

    private StringBuilder recordTextBuffer = new StringBuilder();
    private List<Unit> unitList;
    private Map<Long, Double> unitDamage = new HashMap<>();
    private ResourceContext context = ResourceContextHolder.getResourceContext();

    /**
     * Creates new form BattleRecordDisplayFrame
     */
    public BattleRecordDisplayFrame() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        recordTextPane = new javax.swing.JTextPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        cleanMenuItem = new javax.swing.JMenuItem();

        setTitle("战斗记录");

        jScrollPane2.setPreferredSize(new java.awt.Dimension(144, 104));
        jScrollPane2.setViewportView(recordTextPane);

        jMenu1.setText("操作");

        cleanMenuItem.setText("清空");
        cleanMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(cleanMenuItem);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cleanMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanMenuItemActionPerformed
        recordTextBuffer.setLength(0);
        flush();
        recordTextPane.setText("");
    }//GEN-LAST:event_cleanMenuItemActionPerformed

    public void addText(String text) {
        setDocs(text, Color.BLACK, true, 18);
    }

    public void setUnitList(List<Unit> unitList) {
        this.unitList = unitList;
    }

    public void addRecords(List<ActionRecord> records) {
        records.forEach(ar -> {
            translateActionRecord(ar);
        });
    }

    private void translateActionRecord(ActionRecord ar) {
        String str;
        switch (ar.type) {
            case USE_SKILL:
                SkillParam skillParam = context.getLoader(SkillParam.class).get(ar.actionId);
                str = " --使用技能：" + getUnitNameById(ar.actorId) + " 使用了 " + skillParam.getName() + ",次数：" + ar.processCount;
                setDocs(str, Color.GREEN, false, 15);
                str = "  --消耗：";
                if (ar.cost.sp != 0) {
                    str += ar.cost.sp + "怒气 ";
                }
                setDocs(str, Color.BLACK, false, 15);
                break;
//            case SUMMON:
//                setDocs(" --召唤：" + getUnitNameById(ar.actorId) + " 进行了召唤", Color.BLACK, false, 15);
//                break;
            case BUFF_AFFECT:
                String buffName = "NULL";
                switch ((int) ar.actorId) {
                    case 22:
                        buffName = "回春等级";
                        break;
                    case 23:
                        buffName = "再生率";
                        break;
                    case 32:
                        buffName = "流血值";
                        break;
                }
                setDocs(" --Buff生效：" + buffName + " 生效了", Color.BLACK, false, 15);
                break;
            case BUFF_DECAY:
                setDocs(" --Buff衰减：" + ar.buffActor.getName() + " 衰减了", Color.BLACK, false, 15);
                break;
        }
        for (int i = 0; i < ar.affectRecordPack.size(); i++) {
            setDocs("  --结果 " + (i + 1) + "：", Color.BLACK, false, 15);
            List<AffectRecord> afl = ar.affectRecordPack.get(i);
            for (int j = 0; j < afl.size(); j++) {
                translateAffectRecord(afl.get(j));
            }
        }
    }

    private void translateAffectRecord(AffectRecord ar) {
        switch (ar.type) {
            case DAMAGE:
                if (ar.isHit) {
                    String str;
                    str = "   --伤害：" + ar.target.getName() + " 减少了 ";
                    if (ar.value.hp != 0) {
                        Double damage = unitDamage.get(ar.target.getId());
                        if (damage == null) {
                            damage = 0d;
                        }
                        damage += ar.value.hp;
                        unitDamage.put(ar.target.getId(), damage);
                        str += ar.value.hp + "生命 ";
                    }
                    if (ar.value.sp != 0) {
                        str += ar.value.sp + "怒气 ";
                    }
                    setDocs(str, Color.BLACK, false, 15);
                    str = "   --";
                    str += ar.damageType + " ";
                    if (ar.isCritical) {
                        str += "暴击 ";
                    }
                    if (ar.isBlock) {
                        str += "格挡 ";
                    }
                    if (ar.isOverKill) {
                        str += "过量伤害 ";
                    }
                    setDocs(str, Color.BLACK, false, 15);
                } else {
                    setDocs("   --闪避：" + ar.target.getName() + " 闪避了这次伤害", Color.BLACK, false, 15);
                }
                break;
            case RECOVER:
                if (ar.isHit) {
                    String str;
                    str = "   --回复：" + ar.target.getName() + " 回复了 ";
                    if (ar.value.hp != 0) {
                        str += ar.value.hp + "生命 ";
                    }
                    if (ar.value.sp != 0) {
                        str += ar.value.sp + "怒气 ";
                    }
                    setDocs(str, Color.BLACK, false, 15);
                    str = "   --";
                    str += ar.damageType + " ";
                    if (ar.isCritical) {
                        str += "暴击 ";
                    }
                    if (ar.isBless) {
                        str += "神佑 ";
                    }
                    setDocs(str, Color.BLACK, false, 15);
                } else {
                    setDocs("   --闪避：" + ar.target.getName() + " 闪避了这次回复", Color.BLACK, false, 15);
                }
                break;
            case BUFF_ATTACH:
                if (ar.isHit) {
                    setDocs("   --上Buff：" + ar.target.getName() + " 中了 " + ar.buffs.get(0).getName(), Color.BLACK, false, 15);
                } else {
                    setDocs("   --闪避：" + ar.target.getName() + " 闪避了这次上BUFF", Color.BLACK, false, 15);
                }
                break;
            case BUFF_DETACH:
                if (ar.isHit) {
                    String str;
                    str = "   --解Buff：" + ar.target.getName() + " 解除了 ";
                    if (ar.buffs != null) {
                        for (Buff b : ar.buffs) {
                            str += b.getName() + " ";
                        }
                    }
                    setDocs(str, Color.BLACK, false, 15);
                } else {
                    setDocs("   --闪避：" + ar.target.getName() + " 闪避了这次解BUFF", Color.BLACK, false, 15);
                }
                break;
            case DIE:
                setDocs("   --倒地：" + ar.target.getName() + " 落命了", Color.RED, false, 15);
                break;
            case FLY_OUT:
                setDocs("   --击飞：" + ar.target.getName() + " 被击飞了", Color.RED, false, 15);
                break;
            case REVIVE:
                if (ar.isHit) {
                    String str;
                    str = "   --复活：" + ar.target.getName() + " 被复活了 ";
                    if (ar.value.hp != 0) {
                        str += ar.value.hp + "生命 ";
                    }
                    if (ar.value.sp != 0) {
                        str += ar.value.sp + "怒气 ";
                    }
                    setDocs(str, Color.BLACK, false, 15);
                    str = "   --";
                    str += ar.damageType + " ";
                    if (ar.isCritical) {
                        str += "暴击 ";
                    }
                    if (ar.isBless) {
                        str += "神佑 ";
                    }
                    setDocs(str, Color.BLACK, false, 15);
                } else {
                    setDocs("   --闪避：" + ar.target.getName() + " 闪避了这次复活", Color.BLACK, false, 15);
                }
                break;
//            case SUMMONEE:
//                setDocs("   --召唤：" + ar.unit.getName() + " 召唤了 " + ar.summoneeInitInfo.name, Color.BLACK, false, 15);
//                break;
        }
    }

//    private void translateActionRecord(StringBuilder sb, ActionRecord ar) {
//        switch (ar.type) {
//            case USE_SKILL:
//                sb.append(" --使用技能：").append(getUnitNameById(ar.actorId)).append(" 使用了 ").append(ar.actionId).append(",次数：").append(ar.processCount).append("\n");
//                sb.append("  --消耗：");
//                if (ar.cost.hp != 0) {
//                    sb.append(ar.cost.hp).append("生命 ");
//                }
//                if (ar.cost.mp != 0) {
//                    sb.append(ar.cost.mp).append("真气 ");
//                }
//                if (ar.cost.sp != 0) {
//                    sb.append(ar.cost.sp).append("怒气 ");
//                }
//                sb.append("\n");
//                break;
//            case SUMMON:
//                sb.append(" --召唤：").append(getUnitNameById(ar.actorId)).append(" 进行了召唤").append("\n");
//                break;
//            case BUFF_AFFECT:
//                sb.append(" --Buff生效：").append(ar.buff.getName()).append(" 生效了").append("\n");
//                break;
//            case BUFF_DECAY:
//                sb.append(" --Buff衰减：").append(ar.buff.getName()).append(" 衰减了").append("\n");
//                break;
//        }
//        for (int i = 0; i < ar.affectRecordPack.size(); i++) {
//            sb.append("  --结果 ").append(i + 1).append("：").append("\n");
//            List<AffectRecord> afl = ar.affectRecordPack.get(i);
//            for (int j = 0; j < afl.size(); j++) {
//                translateAffectRecord(sb, afl.get(j));
//            }
//        }
//    }
//
//    private void translateAffectRecord(StringBuilder sb, AffectRecord ar) {
//        switch (ar.type) {
//            case DAMAGE:
//                if (ar.isHit) {
//                    sb.append("   --伤害：").append(ar.unit.getName()).append(" 减少了 ");
//                    if (ar.value.hp != 0) {
//                        sb.append(ar.value.hp).append("生命 ");
//                    }
//                    if (ar.value.mp != 0) {
//                        sb.append(ar.value.mp).append("真气 ");
//                    }
//                    if (ar.value.sp != 0) {
//                        sb.append(ar.value.sp).append("怒气 ");
//                    }
//                    sb.append("\n").append("   --");
//                    sb.append(ar.damageType).append(" ");
//                    if (ar.isCritcal) {
//                        sb.append("暴击 ");
//                    }
//                    if (ar.isBlock) {
//                        sb.append("格挡 ");
//                    }
//                    if (ar.isOverKill) {
//                        sb.append("过量伤害 ");
//                    }
//                    sb.append("\n");
//                } else {
//                    sb.append("   --闪避：").append(ar.unit.getName()).append(" 闪避了这次伤害").append("\n");
//                }
//                break;
//            case RECOVER:
//                if (ar.isHit) {
//                    sb.append("   --回复：").append(ar.unit.getName()).append(" 回复了 ");
//                    if (ar.value.hp != 0) {
//                        sb.append(ar.value.hp).append("生命 ");
//                    }
//                    if (ar.value.mp != 0) {
//                        sb.append(ar.value.mp).append("真气 ");
//                    }
//                    if (ar.value.sp != 0) {
//                        sb.append(ar.value.sp).append("怒气 ");
//                    }
//                    sb.append("\n").append("   --");
//                    sb.append(ar.damageType).append(" ");
//                    if (ar.isCritcal) {
//                        sb.append("暴击 ");
//                    }
//                    if (ar.isBless) {
//                        sb.append("神佑 ");
//                    }
//                    sb.append("\n");
//                } else {
//                    sb.append("   --闪避：").append(ar.unit.getName()).append(" 闪避了这次回复").append("\n");
//                }
//                break;
//            case BUFF_ATTACH:
//                if (ar.isSuccess) {
//                    sb.append("   --上Buff：").append(ar.unit.getName()).append(" 中了 ");
//                    sb.append(ar.buff.getName());
//                    sb.append("\n");
//                } else {
//                    sb.append("   --闪避：").append(ar.unit.getName()).append(" 闪避了这次上Buff").append("\n");
//                }
//                break;
//            case BUFF_DETACH:
//                if (ar.isSuccess) {
//                    sb.append("   --解Buff：").append(ar.unit.getName()).append(" 解除了 ");
//                    if (ar.buff != null) {
//                        sb.append(ar.buff.getName()).append(" ");
//                    }
//                    if (ar.buffs != null) {
//                        for (Buff b : ar.buffs) {
//                            sb.append(b.getName()).append(" ");
//                        }
//                    }
//                    sb.append("\n");
//                } else {
//                    sb.append("   --闪避：").append(ar.unit.getName()).append(" 闪避了这次解Buff").append("\n");
//                }
//                break;
//            case DIE:
//                sb.append("   --倒地：").append(ar.unit.getName()).append(" 落命了 ").append("\n");
//                break;
//            case FLY_OUT:
//                sb.append("   --击飞：").append(ar.unit.getName()).append(" 被击飞了 ").append("\n");
//                break;
//            case REVIVE:
//                if (ar.isHit) {
//                    sb.append("   --复活：").append(ar.unit.getName()).append(" 被复活了 ");
//                    if (ar.value.hp != 0) {
//                        sb.append(ar.value.hp).append("生命 ");
//                    }
//                    if (ar.value.mp != 0) {
//                        sb.append(ar.value.mp).append("真气 ");
//                    }
//                    if (ar.value.sp != 0) {
//                        sb.append(ar.value.sp).append("怒气 ");
//                    }
//                    sb.append("\n").append("   --");
//                    sb.append(ar.damageType).append(" ");
//                    if (ar.isCritcal) {
//                        sb.append("暴击 ");
//                    }
//                    if (ar.isBless) {
//                        sb.append("神佑 ");
//                    }
//                    sb.append("\n");
//                } else {
//                    sb.append("   --闪避：").append(ar.unit.getName()).append(" 闪避了这次复活").append("\n");
//                }
//                break;
//            case SUMMONEE:
//                sb.append("   --召唤：").append(ar.unit.getName()).append(" 召唤了 ");
//                sb.append(ar.summoneeInitInfo.name).append("\n");
//                break;
//        }
//    }
    private String getUnitNameById(long id) {
        for (Unit u : this.unitList) {
            if (u.getId() == id) {
                return u.getName();
            }
        }
        return Long.toString(id);
    }

    public void insert(String str, AttributeSet attrSet) {
        Document doc = recordTextPane.getDocument();
        str = "\n" + str;
        try {
            doc.insertString(doc.getLength(), str, attrSet);
        } catch (BadLocationException e) {
            System.out.println("BadLocationException: " + e);
        }
    }

    public void setDocs(String str, Color col, boolean bold, int fontSize) {
        SimpleAttributeSet attrSet = new SimpleAttributeSet();
        StyleConstants.setForeground(attrSet, col);
        if (bold == true) {
            StyleConstants.setBold(attrSet, true);
        }
        StyleConstants.setFontSize(attrSet, fontSize);
        insert(str, attrSet);
    }

    public void flush() {
        //recordTextPane.setText(recordTextBuffer.toString());
    }

    public void initBattleRate() {
        clearBattleRate();
        for (Unit u : unitList) {
            unitDamage.put(u.getId(), 0d);
        }
    }

    public void clearBattleRate() {
        unitDamage.clear();
    }

    public void showBattleRate() {
        double totalRate = 0;
        double avgRate = 0;
        double totalHp = 0;
        double totalDamage = 0;
        int playerCount = 0;
        for (Unit u : unitList) {
            if (u.getStance() == Unit.Stance.STANCE_RED) {
                playerCount++;
                totalHp += u.getHp().getUpperLimit().getValue();
                totalDamage += unitDamage.get(u.getId());
                avgRate += unitDamage.get(u.getId()) / u.getHp().getUpperLimit().getValue();
                //setDocs("=== 单位战损比 " + unitDamage.get(u.getId()) / u.getHp().getUpperLimit().getValue() * 100 + "% ===", Color.BLUE, true, 20);
            }
        }
        avgRate = avgRate / playerCount * 100;
        avgRate = Math.round(avgRate);
        totalRate = totalDamage / totalHp * 100;
        totalRate = Math.round(totalRate);
        setDocs("=== 总战损比 " + totalRate + "% ===", Color.BLUE, true, 20);
        setDocs("=== 平均战损比 " + avgRate + "% ===", Color.BLUE, true, 20);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem cleanMenuItem;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextPane recordTextPane;
    // End of variables declaration//GEN-END:variables
}