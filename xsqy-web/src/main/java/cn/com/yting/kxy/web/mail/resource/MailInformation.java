/*
 * Created 2018-7-25 15:59:28
 */
package cn.com.yting.kxy.web.mail.resource;

import java.io.IOException;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

import cn.com.yting.kxy.core.resource.Resource;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.web.KxyWebException;
import groovy.text.Template;
import groovy.text.TemplateEngine;
import lombok.Getter;
import org.codehaus.groovy.control.CompilationFailedException;

/**
 *
 * @author Azige
 */
@Getter
public class MailInformation implements Resource {

    @XmlAttribute
    private long id;
    @XmlElement
    private String title;
    @XmlElement
    private String content;

    private Template titleTemplate;
    private Template contentTemplate;

    public void createTemplateIfNull(TemplateEngine templateEngine) {
        if (titleTemplate == null || contentTemplate == null) {
            try {
                titleTemplate = templateEngine.createTemplate(title);
                contentTemplate = templateEngine.createTemplate(content);
            } catch (CompilationFailedException | ClassNotFoundException | IOException ex) {
                throw KxyWebException.unknown("无法创建邮件模版", ex);
            }
        }
    }

    public static MailInformation getFrom(ResourceContext resourceContext, long id) {
        return resourceContext.getLoader(MailInformation.class).get(id);
    }
}
