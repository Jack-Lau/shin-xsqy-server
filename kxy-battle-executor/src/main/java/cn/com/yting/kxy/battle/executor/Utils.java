/*
 * Created 2017-2-22 15:55:47
 */
package cn.com.yting.kxy.battle.executor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import cn.com.yting.kxy.battle.BattleResult;

/**
 *
 * @author Azige
 */
public final class Utils {

    private static JFileChooser textFileChooser = new JFileChooser();

    static {
        textFileChooser.setCurrentDirectory(new File("."));
        textFileChooser.setFileFilter(new FileNameExtensionFilter("文本文件 (*.txt)", "txt"));
    }

    private Utils() {
    }

    public static void exportClientBattleRecord(BattleResult battleResult) {
        if (JOptionPane.showConfirmDialog(null, "要导出战斗记录吗？", "导出战斗记录", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (textFileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                File saveFile = textFileChooser.getSelectedFile();
                if (!saveFile.getName().endsWith(".txt")) {
                    saveFile = new File(saveFile.getParent(), saveFile.getName() + ".txt");
                }
                if (saveFile.exists()) {
                    if (JOptionPane.showConfirmDialog(null, "文件[" + saveFile.getName() + "]已存在，要覆盖吗？", "确认文件覆盖", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                byte[] outputData = battleResult.toString().getBytes();
                outputData = Base64.getEncoder().encode(outputData);
                try (OutputStream output = new BufferedOutputStream(new FileOutputStream(saveFile))) {
                    output.write(outputData);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        }
    }

}
