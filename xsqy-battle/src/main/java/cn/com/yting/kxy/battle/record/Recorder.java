/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.battle.record;

/**
 *
 * @author Darkholme
 */
public interface Recorder {
    
    public void addActionRecord(ActionRecord actionRecord);
    
    public void createAffectRecordPack();
    
    public void addAffectRecord(AffectRecord affectRecord);
    
}
