/*
 * Created 2018-6-29 10:42:22
 */
package cn.com.yting.kxy.web.message;

import cn.com.yting.kxy.battle.Unit;
import cn.com.yting.kxy.web.KxyWebException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.habarta.typescript.generator.Input;
import cz.habarta.typescript.generator.JsonLibrary;
import cz.habarta.typescript.generator.Settings;
import cz.habarta.typescript.generator.TypeScriptGenerator;
import cz.habarta.typescript.generator.TypeScriptOutputKind;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

/**
 *
 * @author Azige
 */
@Component
public class TypeDefinitionsHolder {

    private String code;
    private boolean scanned = false;

    public String getCode() {
        scan();
        return code;
    }

    private synchronized void scan() {
        if (!scanned) {
            scanned = true;

            Settings settings = new Settings();
            settings.outputKind = TypeScriptOutputKind.module;
            settings.jsonLibrary = JsonLibrary.jackson2;
            settings.customTypeNaming.put(Unit.FashionDye.class.getName(), "Unit_FashionDye");
            settings.customTypeNaming.put(java.util.Date.class.getName(), "number");
            settings.customTypeNaming.put("cn.com.yting.kxy.web.game.baccarat.BaccaratConstants$Status", "BaccaratConstants_Status");
            settings.customTypeNaming.put("cn.com.yting.kxy.web.player.PlayerOnlineStatus$Status", "PlayerOnlineStatus_Status");
            TypeScriptGenerator generator = new TypeScriptGenerator(settings);
            ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
            scanner.addIncludeFilter(new AnnotationTypeFilter(WebMessageType.class));
            Class[] messageTypes = scanner.findCandidateComponents("cn.com.yting.kxy").stream()
                .map(definition -> {
                    try {
                        Class<?> clz = Class.forName(definition.getBeanClassName());
                        return clz;
                    } catch (ClassNotFoundException ex) {
                        throw KxyWebException.unknown("解析 web 消息的 schema 时出错", ex);
                    }
                })
                .toArray(Class[]::new);
            code = generator.generateTypeScript(Input.from(messageTypes));
        }
    }

}
