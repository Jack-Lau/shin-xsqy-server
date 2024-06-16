/*
 * Created 2017-2-20 15:40:41
 */
package cn.com.yting.kxy.battle.executor.model;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author Azige
 */
public final class PartyIO{

    private PartyIO(){
    }

    public static void writeToFile(File file, Map<Integer, UnitModel> party) throws IOException{
        try (XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(file)))){
            encoder.writeObject(party);
        }
    }

    public static Map<Integer, UnitModel> readFromFile(File file) throws FileNotFoundException{
        try (XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(file)))){
            return (Map<Integer, UnitModel>)decoder.readObject();
        }
    }
}
