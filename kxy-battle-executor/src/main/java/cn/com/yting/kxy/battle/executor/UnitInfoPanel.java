/*
 * Created 2017-2-19 16:13:18
 */
package cn.com.yting.kxy.battle.executor;

import cn.com.yting.kxy.battle.UnitInitInfo;
import cn.com.yting.kxy.battle.UnitStatus;

/**
 *
 * @author Azige
 */
public class UnitInfoPanel extends javax.swing.JPanel{

    private UnitInitInfo initInfo;
    /**
     * Creates new form UnitInfoPanel
     */
    public UnitInfoPanel(){
        initComponents();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        hpLabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        spLabel = new javax.swing.JLabel();

        jLabel1.setText("名字");

        nameLabel.setText("路人");

        jLabel3.setText("HP");

        hpLabel.setText("100/100");

        jLabel7.setText("SP");

        spLabel.setText("100/100");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hpLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 10, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(nameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(hpLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(spLabel)))
        );
    }// </editor-fold>//GEN-END:initComponents

    public void setUnitInitInfo(UnitInitInfo initInfo){
        nameLabel.setText(initInfo.name);
        hpLabel.setText("" + initInfo.hp + "/" + initInfo.maxHp);
        spLabel.setText("" + initInfo.sp + "/" + initInfo.maxSp);
        this.initInfo = initInfo;
    }

    public void setUnitStatus(UnitStatus unitStatus){
        hpLabel.setText("" + unitStatus.currHp + "/" + initInfo.maxHp);
        spLabel.setText("" + unitStatus.currSp + "/" + initInfo.maxSp);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel hpLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel spLabel;
    // End of variables declaration//GEN-END:variables
}