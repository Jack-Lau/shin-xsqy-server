/*
 * Created 2017-2-16 17:05:24
 */
package cn.com.yting.kxy.battle.executor;

import java.awt.Frame;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import cn.com.yting.kxy.battle.BattleDirector;
import cn.com.yting.kxy.battle.BattleDirectorBuilder;
import cn.com.yting.kxy.battle.BattleDirectorBuilder.BattlePartyBuilder.BattleUnitBuilder;
import cn.com.yting.kxy.battle.BattleResult;
import cn.com.yting.kxy.battle.PartyBuilder;
import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.battle.Unit.Stance;
import cn.com.yting.kxy.battle.executor.model.PartyIO;
import cn.com.yting.kxy.battle.executor.model.UnitModel;
import cn.com.yting.kxy.battle.generate.PartyDescriptor;
import cn.com.yting.kxy.battle.generate.PartyStastics;
import cn.com.yting.kxy.battle.skill.Skill;
import cn.com.yting.kxy.core.parameter.Parameter;
import cn.com.yting.kxy.core.parameter.ParameterNameConstants;
import cn.com.yting.kxy.core.parameter.resource.Attributes;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.ResourceContextHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Azige
 */
public class MainFrame extends javax.swing.JFrame {

    private static final Logger LOG = LoggerFactory.getLogger(MainFrame.class);
    private BattleRecordDisplayFrame recordFrame = new BattleRecordDisplayFrame();
    private BattleControllerFrame controllerFrame = new BattleControllerFrame();
    private JFileChooser xmlFileChooser = new JFileChooser();
    private UnitModel clipboardUnitModel;

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        setLocationRelativeTo(null);

        controllerFrame.setRecordFrame(recordFrame);

        xmlFileChooser.setCurrentDirectory(new File("."));
        xmlFileChooser.setFileFilter(new FileNameExtensionFilter("XML文件 (*.xml)", "xml"));

        playerUnitPanel1.lockEnableCheckBox();
    }

    private void laterInit() {
        ResourceContext context = ResourceContextHolder.getResourceContext();
        Map<String, Double> parameterMap = context.getLoader(Attributes.class).getAll().values().stream()
                .collect(Collectors.toMap(Attributes::getName, Attributes::getBasicValueOfCharacter));
        int count = 0;
        UnitConfigurationPanel[] unitPanels;
        UnitConfigClipboardHandler clipboardHandler = new UnitConfigClipboardHandler() {
            @Override
            public void copy(UnitModel unitModel) {
                clipboardUnitModel = unitModel;
            }

            @Override
            public UnitModel paste() {
                return clipboardUnitModel;
            }
        };

        unitPanels = playerPanels();
        for (int i = 0; i < unitPanels.length; i++) {
            UnitConfigurationPanel unitPanel = unitPanels[i];
            unitPanel.setUnitName("路人" + (char) ('A' + count++));
            unitPanel.setParameterMap(parameterMap);
            unitPanel.setClipboardHandler(clipboardHandler);
        }

        unitPanels = enemyPanels();
        for (int i = 0; i < unitPanels.length; i++) {
            UnitConfigurationPanel unitPanel = unitPanels[i];
            unitPanel.setUnitName("路人" + (char) ('A' + count++));
            unitPanel.setParameterMap(parameterMap);
            unitPanel.setClipboardHandler(clipboardHandler);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        playerUnitPanel1 = new cn.com.yting.kxy.battle.executor.UnitConfigurationPanel();
        enemyUnitPanel1 = new cn.com.yting.kxy.battle.executor.UnitConfigurationPanel();
        playerUnitPanel2 = new cn.com.yting.kxy.battle.executor.UnitConfigurationPanel();
        playerUnitPanel3 = new cn.com.yting.kxy.battle.executor.UnitConfigurationPanel();
        playerUnitPanel4 = new cn.com.yting.kxy.battle.executor.UnitConfigurationPanel();
        playerUnitPanel5 = new cn.com.yting.kxy.battle.executor.UnitConfigurationPanel();
        playerUnitPanel6 = new cn.com.yting.kxy.battle.executor.UnitConfigurationPanel();
        enemyUnitPanel2 = new cn.com.yting.kxy.battle.executor.UnitConfigurationPanel();
        enemyUnitPanel3 = new cn.com.yting.kxy.battle.executor.UnitConfigurationPanel();
        enemyUnitPanel4 = new cn.com.yting.kxy.battle.executor.UnitConfigurationPanel();
        enemyUnitPanel5 = new cn.com.yting.kxy.battle.executor.UnitConfigurationPanel();
        enemyUnitPanel6 = new cn.com.yting.kxy.battle.executor.UnitConfigurationPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        oneshotMenuItem = new javax.swing.JMenuItem();
        turnByTurnMenuItem = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        viewRecordMenuItem = new javax.swing.JMenuItem();
        viewControllerMenuItem = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        savePlayerPartyMenuItem = new javax.swing.JMenuItem();
        loadPlayerPartyMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        loadMonsterPartyMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Mgxy Battle Executor");

        jMenu1.setText("战斗");

        oneshotMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        oneshotMenuItem.setText("一发");
        oneshotMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oneshotMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(oneshotMenuItem);

        turnByTurnMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, 0));
        turnByTurnMenuItem.setText("按回合进行");
        turnByTurnMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                turnByTurnMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(turnByTurnMenuItem);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("视图");

        viewRecordMenuItem.setText("战斗记录");
        viewRecordMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewRecordMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(viewRecordMenuItem);

        viewControllerMenuItem.setText("战斗控制");
        viewControllerMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewControllerMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(viewControllerMenuItem);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("数据");

        savePlayerPartyMenuItem.setText("保存玩家队伍");
        savePlayerPartyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savePlayerPartyMenuItemActionPerformed(evt);
            }
        });
        jMenu3.add(savePlayerPartyMenuItem);

        loadPlayerPartyMenuItem.setText("读取玩家队伍");
        loadPlayerPartyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadPlayerPartyMenuItemActionPerformed(evt);
            }
        });
        jMenu3.add(loadPlayerPartyMenuItem);
        jMenu3.add(jSeparator1);

        loadMonsterPartyMenuItem.setText("读取怪物队伍");
        loadMonsterPartyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadMonsterPartyMenuItemActionPerformed(evt);
            }
        });
        jMenu3.add(loadMonsterPartyMenuItem);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(playerUnitPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(playerUnitPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(playerUnitPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(playerUnitPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(playerUnitPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(playerUnitPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(enemyUnitPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(enemyUnitPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(enemyUnitPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(enemyUnitPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(enemyUnitPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(enemyUnitPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(playerUnitPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(playerUnitPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(playerUnitPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(playerUnitPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(playerUnitPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(playerUnitPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(enemyUnitPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(enemyUnitPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(enemyUnitPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(enemyUnitPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(enemyUnitPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(enemyUnitPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void oneshotMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oneshotMenuItemActionPerformed
        BattleDirector bd = createBattleBuilder().build();

        bd.oneshot();
        recordFrame.clearBattleRate();
        recordFrame.setUnitList(bd.getAllUnits().collect(Collectors.toList()));
        recordFrame.initBattleRate();
        BattleResult battleResult = bd.getBattleResult();
        Utils.exportClientBattleRecord(battleResult);
        battleResult.getTurnInfo().forEach(tr -> {
            recordFrame.addText("==========开始第" + tr.getTurnCount() + "回合==========");
//            tr.getUnitStatus().forEach(us -> recordFrame.addText(JsonUtil.toJson(us)));
            recordFrame.addRecords(tr.getActionRecord());
        });
        recordFrame.showBattleRate();
        recordFrame.flush();
        recordFrame.setVisible(true);
    }//GEN-LAST:event_oneshotMenuItemActionPerformed

    private void turnByTurnMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_turnByTurnMenuItemActionPerformed
        controllerFrame.startBattle(createBattleBuilder());
        recordFrame.setVisible(true);
    }//GEN-LAST:event_turnByTurnMenuItemActionPerformed

    private void viewRecordMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewRecordMenuItemActionPerformed
        recordFrame.setVisible(true);
    }//GEN-LAST:event_viewRecordMenuItemActionPerformed

    private void viewControllerMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewControllerMenuItemActionPerformed
        controllerFrame.setVisible(true);
    }//GEN-LAST:event_viewControllerMenuItemActionPerformed

    private void savePlayerPartyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePlayerPartyMenuItemActionPerformed
        xmlFileChooser.setSelectedFile(new File("玩家队伍配置数据.xml"));
        if (xmlFileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File saveFile = xmlFileChooser.getSelectedFile();
            if (!saveFile.getName().endsWith(".xml")) {
                saveFile = new File(saveFile.getParent(), saveFile.getName() + ".xml");
            }
            if (saveFile.exists()) {
                if (JOptionPane.showConfirmDialog(this, "文件[" + saveFile.getName() + "]已存在，要覆盖吗？", "确认文件覆盖", JOptionPane.YES_NO_OPTION) != JOptionPane.OK_OPTION) {
                    return;
                }
            }

            Map<Integer, UnitModel> playerParty = new HashMap<>();
            UnitConfigurationPanel[] unitPanels = playerPanels();

            for (int i = 0; i < unitPanels.length; i++) {
                UnitConfigurationPanel unitPanel = unitPanels[i];
                if (unitPanel.isInUse()) {
                    playerParty.put(i, unitPanel.toUnitModel());
                }
            }

            try {
                PartyIO.writeToFile(saveFile, playerParty);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_savePlayerPartyMenuItemActionPerformed

    private void loadPlayerPartyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadPlayerPartyMenuItemActionPerformed
        if (xmlFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Map<Integer, UnitModel> playerParty = PartyIO.readFromFile(xmlFileChooser.getSelectedFile());
                UnitConfigurationPanel[] unitPanels = playerPanels();

                for (int i = 0; i < unitPanels.length; i++) {
                    UnitConfigurationPanel unitPanel = unitPanels[i];
                    if (playerParty.containsKey(i)) {
                        unitPanel.configByUnitModel(playerParty.get(i));
                        unitPanel.setInUse(true);
                    } else {
                        unitPanel.setInUse(false);
                    }
                }
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_loadPlayerPartyMenuItemActionPerformed

    private void loadMonsterPartyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMonsterPartyMenuItemActionPerformed
        String input = JOptionPane.showInputDialog("输入队伍配置的id");
        if (input != null) {
            try {
                long id = Long.parseLong(input);
                ResourceContext resourceContext = ResourceContextHolder.getResourceContext();
                PartyDescriptor partyDescriptor = resourceContext.getLoader(PartyDescriptor.class).get(id);
                if (partyDescriptor == null) {
                    JOptionPane.showMessageDialog(this, "队伍配置不存在，id=" + id);
                    return;
                }
                PartyBuilder<?> pb = PartyBuilder.create();
                PartyStastics ps = new PartyStastics();

                List<Map<String, Double>> parameterMaps = Stream.of(playerPanels())
                        .filter(UnitConfigurationPanel::isInUse)
                        .map(UnitConfigurationPanel::toUnitModel)
                        .map(UnitModel::getParameterMap)
                        .collect(Collectors.toList());

                ps.LV = parameterMaps.get(0).get(ParameterNameConstants.等级).intValue();
                ps.ALV = (int) parameterMaps.stream()
                        .mapToDouble(m -> m.get(ParameterNameConstants.等级))
                        .average()
                        .getAsDouble();

                partyDescriptor.export(pb, ps, resourceContext);
                Map<Integer, Unit> unitMap = pb.build().getUnitMap();

                UnitConfigurationPanel[] enemyPanels = enemyPanels();
                for (int i = 0; i < enemyPanels.length; i++) {
                    UnitConfigurationPanel enemyPanel = enemyPanels[i];
                    if (unitMap.containsKey(i + 1)) {
                        Unit unit = unitMap.get(i + 1);
                        enemyPanel.configByUnitModel(new UnitModel(
                                unit.getName(),
                                (int) unit.getParameter(ParameterNameConstants.等级).getValue(),
                                unit.getRootParameterSpace().toParameters().stream()
                                        .collect(Collectors.toMap(Parameter::getName, Parameter::getValue)),
                                unit.getSkills().stream()
                                        .map(Skill::getId)
                                        .collect(Collectors.toList()),
                                unit.getRobot().getId()
                        ));
                        enemyPanel.setInUse(true);
                    } else {
                        enemyPanel.setInUse(false);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "数字格式不对");
            }
        }
    }//GEN-LAST:event_loadMonsterPartyMenuItemActionPerformed

    private BattleDirectorBuilder createBattleBuilder() {
        BattleDirectorBuilder builder = new BattleDirectorBuilder();
        BattleUnitBuilder unit;

        UnitConfigurationPanel[] unitPanels;
        unitPanels = playerPanels();
        for (int i = 0; i < unitPanels.length; i++) {
            if (!unitPanels[i].isInUse()) {
                continue;
            }
            unit = builder.redParty().unit(i + 1);
            unitPanels[i].exportUnit(unit);
            unit
                    .stance(Stance.STANCE_RED)
                    .attackSkill()
                    .robot(unitPanels[i].getRobot())
                    .type(Unit.UnitType.TYPE_PLAYER);
        }

        unitPanels = enemyPanels();
        for (int i = 0; i < unitPanels.length; i++) {
            if (!unitPanels[i].isInUse()) {
                continue;
            }
            unit = builder.blueParty().unit(i + 1);
            unitPanels[i].exportUnit(unit);
            unit
                    .stance(Stance.STANCE_BLUE)
                    .attackSkill()
                    .robot(unitPanels[i].getRobot())
                    .type(Unit.UnitType.TYPE_MONSTER);
        }
        return builder;
    }

    private UnitConfigurationPanel[] playerPanels() {
        return new UnitConfigurationPanel[]{
            playerUnitPanel1,
            playerUnitPanel2,
            playerUnitPanel3,
            playerUnitPanel4,
            playerUnitPanel5,
            playerUnitPanel6
        };
    }

    private UnitConfigurationPanel[] enemyPanels() {
        return new UnitConfigurationPanel[]{
            enemyUnitPanel1,
            enemyUnitPanel2,
            enemyUnitPanel3,
            enemyUnitPanel4,
            enemyUnitPanel5,
            enemyUnitPanel6
        };
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        Thread.setDefaultUncaughtExceptionHandler((Thread t, Throwable e) -> {
            LOG.error("未捕获的异常", e);
        });

        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        JDialog dialog = new JDialog((Frame) null, "Kxy Battle Executor", false);
        JLabel label = new JLabel("初始化中");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        dialog.add(label);
        dialog.setSize(200, 100);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        dialog.dispose();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MainFrame f = new MainFrame();
                f.laterInit();
                f.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private cn.com.yting.kxy.battle.executor.UnitConfigurationPanel enemyUnitPanel1;
    private cn.com.yting.kxy.battle.executor.UnitConfigurationPanel enemyUnitPanel2;
    private cn.com.yting.kxy.battle.executor.UnitConfigurationPanel enemyUnitPanel3;
    private cn.com.yting.kxy.battle.executor.UnitConfigurationPanel enemyUnitPanel4;
    private cn.com.yting.kxy.battle.executor.UnitConfigurationPanel enemyUnitPanel5;
    private cn.com.yting.kxy.battle.executor.UnitConfigurationPanel enemyUnitPanel6;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JMenuItem loadMonsterPartyMenuItem;
    private javax.swing.JMenuItem loadPlayerPartyMenuItem;
    private javax.swing.JMenuItem oneshotMenuItem;
    private cn.com.yting.kxy.battle.executor.UnitConfigurationPanel playerUnitPanel1;
    private cn.com.yting.kxy.battle.executor.UnitConfigurationPanel playerUnitPanel2;
    private cn.com.yting.kxy.battle.executor.UnitConfigurationPanel playerUnitPanel3;
    private cn.com.yting.kxy.battle.executor.UnitConfigurationPanel playerUnitPanel4;
    private cn.com.yting.kxy.battle.executor.UnitConfigurationPanel playerUnitPanel5;
    private cn.com.yting.kxy.battle.executor.UnitConfigurationPanel playerUnitPanel6;
    private javax.swing.JMenuItem savePlayerPartyMenuItem;
    private javax.swing.JMenuItem turnByTurnMenuItem;
    private javax.swing.JMenuItem viewControllerMenuItem;
    private javax.swing.JMenuItem viewRecordMenuItem;
    // End of variables declaration//GEN-END:variables
}