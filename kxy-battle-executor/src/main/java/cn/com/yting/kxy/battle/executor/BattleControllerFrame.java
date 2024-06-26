/*
 * Created 2017-2-19 16:11:59
 */
package cn.com.yting.kxy.battle.executor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.JButton;

import cn.com.yting.kxy.battle.BattleDirector;
import cn.com.yting.kxy.battle.BattleDirectorBuilder;
import cn.com.yting.kxy.battle.BattleResult.TurnInfo;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.Unit.Stance;
import cn.com.yting.kxy.battle.UnitInitInfo;
import cn.com.yting.kxy.battle.action.Action;
import cn.com.yting.kxy.battle.action.UseSkillAction;
import cn.com.yting.kxy.battle.robot.Robot;
import cn.com.yting.kxy.battle.skill.Skill;
import cn.com.yting.kxy.core.resource.Resource;

/**
 *
 * @author Azige
 */
public class BattleControllerFrame extends javax.swing.JFrame {

    private Map<Long, UnitInfoPanel> idPanelMap;
    private Map<Long, JButton> idActionButtonMap;
    private Map<Long, Unit> idUnitMap;
    private Map<Long, ActionReference> turnActionMap;
    private BattleRecordDisplayFrame recordFrame;
    private BattleDirector battleDirector;
    private ListSelectDialog selectDialog = new ListSelectDialog(this);

    /**
     * Creates new form UnitInfoMonitorFrame
     */
    public BattleControllerFrame() {
        initComponents();
        setLocationRelativeTo(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        playerUnitInfoPanel1 = new cn.com.yting.kxy.battle.executor.UnitInfoPanel();
        playerUnitInfoPanel2 = new cn.com.yting.kxy.battle.executor.UnitInfoPanel();
        playerUnitInfoPanel3 = new cn.com.yting.kxy.battle.executor.UnitInfoPanel();
        playerUnitInfoPanel4 = new cn.com.yting.kxy.battle.executor.UnitInfoPanel();
        playerUnitInfoPanel5 = new cn.com.yting.kxy.battle.executor.UnitInfoPanel();
        playerUnitInfoPanel6 = new cn.com.yting.kxy.battle.executor.UnitInfoPanel();
        enemyUnitInfoPanel1 = new cn.com.yting.kxy.battle.executor.UnitInfoPanel();
        enemyUnitInfoPanel2 = new cn.com.yting.kxy.battle.executor.UnitInfoPanel();
        enemyUnitInfoPanel3 = new cn.com.yting.kxy.battle.executor.UnitInfoPanel();
        enemyUnitInfoPanel4 = new cn.com.yting.kxy.battle.executor.UnitInfoPanel();
        enemyUnitInfoPanel5 = new cn.com.yting.kxy.battle.executor.UnitInfoPanel();
        enemyUnitInfoPanel6 = new cn.com.yting.kxy.battle.executor.UnitInfoPanel();
        nextTurnButton = new javax.swing.JButton();
        exportBattleRecordButton = new javax.swing.JButton();
        actionButtonP1 = new javax.swing.JButton();
        actionButtonP2 = new javax.swing.JButton();
        actionButtonP3 = new javax.swing.JButton();
        actionButtonP4 = new javax.swing.JButton();
        actionButtonP5 = new javax.swing.JButton();
        actionButtonP6 = new javax.swing.JButton();
        actionButtonE3 = new javax.swing.JButton();
        actionButtonE4 = new javax.swing.JButton();
        actionButtonE5 = new javax.swing.JButton();
        actionButtonE6 = new javax.swing.JButton();
        actionButtonE1 = new javax.swing.JButton();
        actionButtonE2 = new javax.swing.JButton();

        setTitle("战斗控制器");

        nextTurnButton.setText("下一回合");
        nextTurnButton.setEnabled(false);
        nextTurnButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextTurnButtonActionPerformed(evt);
            }
        });

        exportBattleRecordButton.setText("导出战斗记录");
        exportBattleRecordButton.setEnabled(false);
        exportBattleRecordButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportBattleRecordButtonActionPerformed(evt);
            }
        });

        actionButtonP1.setText("无指令");
        actionButtonP1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputAction(evt);
            }
        });

        actionButtonP2.setText("无指令");
        actionButtonP2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputAction(evt);
            }
        });

        actionButtonP3.setText("无指令");
        actionButtonP3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputAction(evt);
            }
        });

        actionButtonP4.setText("无指令");
        actionButtonP4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputAction(evt);
            }
        });

        actionButtonP5.setText("无指令");
        actionButtonP5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputAction(evt);
            }
        });

        actionButtonP6.setText("无指令");
        actionButtonP6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputAction(evt);
            }
        });

        actionButtonE3.setText("无指令");
        actionButtonE3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputAction(evt);
            }
        });

        actionButtonE4.setText("无指令");
        actionButtonE4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputAction(evt);
            }
        });

        actionButtonE5.setText("无指令");
        actionButtonE5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputAction(evt);
            }
        });

        actionButtonE6.setText("无指令");
        actionButtonE6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputAction(evt);
            }
        });

        actionButtonE1.setText("无指令");
        actionButtonE1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputAction(evt);
            }
        });

        actionButtonE2.setText("无指令");
        actionButtonE2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputAction(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(nextTurnButton)
                        .addGap(18, 18, 18)
                        .addComponent(exportBattleRecordButton))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(playerUnitInfoPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(actionButtonP1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(enemyUnitInfoPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(actionButtonE1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(actionButtonP2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(playerUnitInfoPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(enemyUnitInfoPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(actionButtonE2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(actionButtonP3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(playerUnitInfoPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(actionButtonE3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(enemyUnitInfoPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(actionButtonP4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(playerUnitInfoPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(actionButtonE4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(enemyUnitInfoPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(enemyUnitInfoPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(actionButtonP5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(playerUnitInfoPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(actionButtonE5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(enemyUnitInfoPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(playerUnitInfoPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(actionButtonP6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(actionButtonE6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(playerUnitInfoPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(playerUnitInfoPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(playerUnitInfoPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(playerUnitInfoPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(playerUnitInfoPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(playerUnitInfoPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(actionButtonP1)
                    .addComponent(actionButtonP2)
                    .addComponent(actionButtonP3)
                    .addComponent(actionButtonP4)
                    .addComponent(actionButtonP5)
                    .addComponent(actionButtonP6))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(enemyUnitInfoPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(enemyUnitInfoPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(enemyUnitInfoPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(enemyUnitInfoPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(enemyUnitInfoPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(enemyUnitInfoPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(actionButtonE1)
                    .addComponent(actionButtonE2)
                    .addComponent(actionButtonE3)
                    .addComponent(actionButtonE4)
                    .addComponent(actionButtonE6)
                    .addComponent(actionButtonE5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nextTurnButton)
                    .addComponent(exportBattleRecordButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nextTurnButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextTurnButtonActionPerformed
        boolean battleEnd = battleDirector.nextTurn();
        if (battleEnd) {
            nextTurnButton.setEnabled(false);
            exportBattleRecordButton.setEnabled(true);
            recordFrame.flush();
        }
        List<TurnInfo> turnInfoList = battleDirector.getBattleResult().getTurnInfo();
        TurnInfo turnInfo = turnInfoList.get(turnInfoList.size() - 1);
        recordFrame.addText("==========开始第" + turnInfo.getTurnCount() + "回合==========");
        turnInfo.getEndOfTurnUnitStatus().forEach(us -> idPanelMap.get(us.id).setUnitStatus(us));
        recordFrame.setUnitList(battleDirector.getAllUnits().collect(Collectors.toList()));
        if (turnInfo.getTurnCount() == 1) {
            recordFrame.initBattleRate();
        }
        recordFrame.addRecords(turnInfo.getActionRecord());
        recordFrame.flush();
        cleanActions();
        if (battleEnd) {
            recordFrame.showBattleRate();
        }
    }//GEN-LAST:event_nextTurnButtonActionPerformed

    private void exportBattleRecordButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportBattleRecordButtonActionPerformed
        Utils.exportClientBattleRecord(battleDirector.getBattleResult());
    }//GEN-LAST:event_exportBattleRecordButtonActionPerformed

    private void inputAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputAction
        long id = idActionButtonMap.entrySet().stream()
                .filter(entry -> entry.getValue() == evt.getSource())
                .findAny().get().getKey();
        JButton button = (JButton) evt.getSource();

        Map<String, Long> skillOptionMap = idUnitMap.get(id).getSkills().stream()
                .collect(Collectors.toMap(Skill::getName, Skill::getId, (a, b) -> a, LinkedHashMap::new));
        skillOptionMap.put("无指令", -1000L);
        Long skillId = selectDialog.makeSelect("选择要使用的技能", skillOptionMap);
        if (skillId == null) {
            return;
        } else if (skillId == -1000) {
            turnActionMap.remove(id);
            button.setText("无指令");
            return;
        }
        Map<String, Long> targetOptionMap = battleDirector.getAllUnits()
                .collect(Collectors.toMap(Unit::getName, Unit::getId, (a, b) -> a, LinkedHashMap::new));
        Long targetId = selectDialog.makeSelect("选择目标", targetOptionMap);
        if (targetId == null) {
            return;
        }

        button.setText(idUnitMap.get(id).getSkill(skillId).getName());

        turnActionMap.put(id, new ActionReference(skillId, targetId));
    }//GEN-LAST:event_inputAction

    public BattleRecordDisplayFrame getRecordFrame() {
        return recordFrame;
    }

    public void setRecordFrame(BattleRecordDisplayFrame recordFrame) {
        this.recordFrame = recordFrame;
    }

    public void startBattle(BattleDirectorBuilder battleDirectorBuilder) {
        battleDirector = battleDirectorBuilder.build();
        battleDirector.battleStart();
        setupUnitInitInfo(battleDirector.getBattleResult().getUnitInitInfo());
        nextTurnButton.setEnabled(true);
        exportBattleRecordButton.setEnabled(false);
        turnActionMap = new HashMap<>();
        recordFrame.clearBattleRate();
        setVisible(true);
    }

    private void setupUnitInitInfo(List<UnitInitInfo> unitInitInfoList) {
        idPanelMap = new HashMap<>();
        idActionButtonMap = new HashMap<>();
        idUnitMap = battleDirector.getAllUnits()
                .collect(Collectors.toMap(Unit::getId, Function.identity()));
        List<UnitInitInfo> playerInitInfoList = unitInitInfoList.stream()
                .filter(info -> info.stance.equals(Stance.STANCE_RED))
                .collect(Collectors.toList());
        setupPartyUnitInitInfoTo(playerPanels(), playerActionButtons(), playerInitInfoList);

        List<UnitInitInfo> enemyInitInfoList = unitInitInfoList.stream()
                .filter(info -> info.stance.equals(Stance.STANCE_BLUE))
                .collect(Collectors.toList());
        setupPartyUnitInitInfoTo(enemyPanels(), enemyActionButtons(), enemyInitInfoList);
    }

    private void setupPartyUnitInitInfoTo(UnitInfoPanel[] infoPanels, JButton[] actionbuttons, List<UnitInitInfo> unitInitInfoList) {
        for (int i = 0; i < infoPanels.length; i++) {
            UnitInfoPanel panel = infoPanels[i];
            JButton button = actionbuttons[i];
            if (unitInitInfoList.size() <= i) {
                panel.setVisible(false);
                button.setVisible(false);
            } else {
                UnitInitInfo initInfo = unitInitInfoList.get(i);
                panel.setUnitInitInfo(initInfo);
                Unit unit = idUnitMap.get(initInfo.id);
                if (unit.getRobot() != null) {
                    button.setText("Robot控制");
                    button.setEnabled(false);
                } else {
                    unit.setRobot(new ControllableRobot());
                    button.setText("无指令");
                    button.setEnabled(true);
                }
                idPanelMap.put(initInfo.id, panel);
                idActionButtonMap.put(initInfo.id, button);
                panel.setVisible(true);
                button.setVisible(true);
            }
        }
    }

    private void cleanActions() {
        turnActionMap = new HashMap<>();
        resetActionButtons(playerActionButtons());
        resetActionButtons(enemyActionButtons());
    }

    private void resetActionButtons(JButton[] buttons) {
        for (JButton button : buttons) {
            if (button.isVisible() && button.isEnabled()) {
                button.setText("无指令");
            }
        }
    }

    private UnitInfoPanel[] playerPanels() {
        return new UnitInfoPanel[]{
            playerUnitInfoPanel1,
            playerUnitInfoPanel2,
            playerUnitInfoPanel3,
            playerUnitInfoPanel4,
            playerUnitInfoPanel5,
            playerUnitInfoPanel6
        };
    }

    private UnitInfoPanel[] enemyPanels() {
        return new UnitInfoPanel[]{
            enemyUnitInfoPanel1,
            enemyUnitInfoPanel2,
            enemyUnitInfoPanel3,
            enemyUnitInfoPanel4,
            enemyUnitInfoPanel5,
            enemyUnitInfoPanel6
        };
    }

    private JButton[] playerActionButtons() {
        return new JButton[]{
            actionButtonP1,
            actionButtonP2,
            actionButtonP3,
            actionButtonP4,
            actionButtonP5,
            actionButtonP6
        };
    }

    private JButton[] enemyActionButtons() {
        return new JButton[]{
            actionButtonE1,
            actionButtonE2,
            actionButtonE3,
            actionButtonE4,
            actionButtonE5,
            actionButtonE6
        };
    }

    private class ControllableRobot implements Robot {

        @Override
        public long getId() {
            return Resource.ID_UNSUPPORTED;
        }

        @Override
        public Action generateActionAtActionStart(Unit source, List<Unit> allUnits) {
            return null;
        }

        @Override
        public Action generateActionAtTurnStart(Unit source, List<Unit> allUnits) {
            ActionReference actionRef = turnActionMap.get(source.getId());
            if (actionRef == null) {
                return null;
            } else {
                Skill skill = source.getSkill(actionRef.getSkillId());
                Unit target = allUnits.stream()
                        .filter(unit -> unit.getId() == actionRef.getTargetId())
                        .findAny().get();
                return new UseSkillAction(skill, target);
            }
        }

    }

    private static class ActionReference {

        private final long skillId;
        private final long targetId;

        public ActionReference(long skillId, long targetId) {
            this.skillId = skillId;
            this.targetId = targetId;
        }

        public long getSkillId() {
            return skillId;
        }

        public long getTargetId() {
            return targetId;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton actionButtonE1;
    private javax.swing.JButton actionButtonE2;
    private javax.swing.JButton actionButtonE3;
    private javax.swing.JButton actionButtonE4;
    private javax.swing.JButton actionButtonE5;
    private javax.swing.JButton actionButtonE6;
    private javax.swing.JButton actionButtonP1;
    private javax.swing.JButton actionButtonP2;
    private javax.swing.JButton actionButtonP3;
    private javax.swing.JButton actionButtonP4;
    private javax.swing.JButton actionButtonP5;
    private javax.swing.JButton actionButtonP6;
    private cn.com.yting.kxy.battle.executor.UnitInfoPanel enemyUnitInfoPanel1;
    private cn.com.yting.kxy.battle.executor.UnitInfoPanel enemyUnitInfoPanel2;
    private cn.com.yting.kxy.battle.executor.UnitInfoPanel enemyUnitInfoPanel3;
    private cn.com.yting.kxy.battle.executor.UnitInfoPanel enemyUnitInfoPanel4;
    private cn.com.yting.kxy.battle.executor.UnitInfoPanel enemyUnitInfoPanel5;
    private cn.com.yting.kxy.battle.executor.UnitInfoPanel enemyUnitInfoPanel6;
    private javax.swing.JButton exportBattleRecordButton;
    private javax.swing.JButton nextTurnButton;
    private cn.com.yting.kxy.battle.executor.UnitInfoPanel playerUnitInfoPanel1;
    private cn.com.yting.kxy.battle.executor.UnitInfoPanel playerUnitInfoPanel2;
    private cn.com.yting.kxy.battle.executor.UnitInfoPanel playerUnitInfoPanel3;
    private cn.com.yting.kxy.battle.executor.UnitInfoPanel playerUnitInfoPanel4;
    private cn.com.yting.kxy.battle.executor.UnitInfoPanel playerUnitInfoPanel5;
    private cn.com.yting.kxy.battle.executor.UnitInfoPanel playerUnitInfoPanel6;
    // End of variables declaration//GEN-END:variables
}
