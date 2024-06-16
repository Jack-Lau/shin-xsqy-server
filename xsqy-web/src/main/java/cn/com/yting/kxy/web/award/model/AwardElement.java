/*
 * Created 2016-11-21 18:29:07
 */
package cn.com.yting.kxy.web.award.model;

/**
 *
 * @author Azige
 */
public interface AwardElement {

    void apply(AwardBuilder builder, int playerLevel, long playerFc);
}
