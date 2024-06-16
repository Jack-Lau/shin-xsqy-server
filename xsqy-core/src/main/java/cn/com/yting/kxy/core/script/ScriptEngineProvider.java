/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.yting.kxy.core.script;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 *
 * @author Darkholme
 */
public class ScriptEngineProvider {

    private static final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    private static final ThreadLocal<ScriptEngine> engineLocal = ThreadLocal.withInitial(() -> scriptEngineManager.getEngineByName("javascript"));

    public static ScriptEngine getScriptEngine() {
        return engineLocal.get();
    }

}
