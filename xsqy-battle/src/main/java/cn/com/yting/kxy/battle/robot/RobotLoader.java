/*
 * Created 2018-8-18 21:04:44
 */
package cn.com.yting.kxy.battle.robot;

import cn.com.yting.kxy.core.resource.ClasspathScanResourceLoader;

/**
 *
 * @author Azige
 */
public class RobotLoader extends ClasspathScanResourceLoader<Robot>{

    @Override
    public Class<Robot> getSupportedClass() {
        return Robot.class;
    }

}
