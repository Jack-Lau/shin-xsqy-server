/*
 * Created 2018-7-25 16:04:43
 */
package cn.com.yting.kxy.web.mail.resource;

import cn.com.yting.kxy.core.resource.XmlMapContainerResourceLoader;

/**
 *
 * @author Azige
 */
public class MailInformationLoader extends XmlMapContainerResourceLoader<MailInformation> {

    @Override
    public String getDefaultResourceName() {
        return "sMailInformation.xml";
    }

    @Override
    public Class<MailInformation> getSupportedClass() {
        return MailInformation.class;
    }
}
