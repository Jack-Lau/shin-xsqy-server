/*
 * Created 2018-9-22 17:02:07
 */
package cn.com.yting.kxy.web.chat.model;

import java.util.Map;

/**
 * 定义模版消息中使用的一些特殊类型的参数的后缀。
 *
 * 例如存在这样的模版消息
 * <pre> {playerName} 获得了 {eName:EquipmentName} </pre>
 * 则实际上应该发送的消息参数是这样
 * <pre>
 * {
 *  "playerName": "Bob",
 *  "eName_definitionId": 1234,
 *  "eName_playerPrefabId": 1234
 * }
 * </pre>
 *
 * @author Azige
 */
public final class TemplateTypes {

    /**
     * 包含装备定义id和角色造型id，客户端可以根据这两个值来决定如何显示一件装备的名字
     */
    public final static class EquipmentName {

        private EquipmentName() {
        }

        public static String definitionId(String argName) {
            return argName + "_definitionId";
        }

        public static String playerPrefabId(String argName) {
            return argName + "_playerPrefabId";
        }

        public static void addTo(Map<String, Object> map, String argName, long definitionId, long playerPrefabId) {
            map.put(definitionId(argName), definitionId);
            map.put(playerPrefabId(argName), playerPrefabId);
        }
    }

    public final static class Currency {

        private Currency() {
        }

        public static String id(String argName) {
            return argName + "_id";
        }

        public static String amount(String argName) {
            return argName + "_amount";
        }

        public static void addTo(Map<String, Object> map, String argName, long id, long amount) {
            map.put(id(argName), id);
            map.put(amount(argName), amount);
        }
    }

    private TemplateTypes() {
    }
}
