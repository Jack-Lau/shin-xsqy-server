/*
 * Created 2018-6-30 15:54:21
 */
package cn.com.yting.kxy.web.apimodel;

import cn.com.yting.kxy.core.SelfTyped;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 *
 * @author Azige
 */
@Data
@Setter(AccessLevel.PROTECTED)
public class WebInterface implements Cloneable {

    private String uri;
    private String name;
    private String shortName;
    private String description;
    private HttpMethod httpMethod = HttpMethod.GET;
    private String requestMediaType;
    private String requestBodyJsonType;
    private List<RequestParameter> requestParameters = new ArrayList<>();
    private String responseMediaType = MediaType.APPLICATION_JSON_VALUE;
    private String responseJsonType;
    private String responseElementType;
    private String responseDescription;
    private List<ExpectableError> expectableErrors = new ArrayList<>();

    protected WebInterface() {
    }

    protected void verify() {
        if (uri == null) {
            throw new IllegalStateException("接口缺少 URI");
        }
        if (name == null) {
            name = uri.replaceFirst("^/", "");
        }
    }

    public WebInterface copy() {
        try {
            return (WebInterface) clone();
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static WebInterfaceBuilder builder() {
        return new WebInterfaceBuilder();
    }

    public static class WebInterfaceBuilder<SELF extends WebInterfaceBuilder<SELF>> extends SelfTyped<SELF> {

        private final WebInterface valueHolder = new WebInterface();

        protected WebInterfaceBuilder() {
        }

        public SELF name(String name) {
            valueHolder.name = name;
            return self();
        }

        public SELF shortName(String shortName) {
            valueHolder.shortName = shortName;
            return self();
        }

        public SELF uri(String uri) {
            valueHolder.uri = uri;
            return self();
        }

        public SELF description(String description) {
            valueHolder.description = description;
            return self();
        }

        public SELF get() {
            valueHolder.httpMethod = HttpMethod.GET;
            return self();
        }

        public SELF post() {
            valueHolder.requestMediaType = MediaType.APPLICATION_FORM_URLENCODED_VALUE;
            valueHolder.httpMethod = HttpMethod.POST;
            return self();
        }

        public SELF requestParameter(String type, String name, String description) {
            valueHolder.requestParameters.add(new RequestParameter(type, name, description));
            return self();
        }

        public SELF requestPagenationParameters() {
            return requestParameter("number", "page", "分页编号，从 0 开始（可选）")
                .requestParameter("number", "size", "分页大小（可选）");
        }

        public SELF requestCaptchaParameters() {
            return requestParameter("string", "ticket", "验证码 ticket")
                .requestParameter("string", "randStr", "验证码 randStr");
        }

        public SELF requestBody(Class<?> mappedJavaType) {
            valueHolder.requestMediaType = MediaType.APPLICATION_JSON_VALUE;
            valueHolder.requestBodyJsonType = mappedJavaType.getSimpleName();
            return self();
        }

        public SELF response(Class<?> mappedJavaType, String description) {
            this.response(mappedJavaType.getSimpleName(), description);
            return self();
        }

        public SELF response(String responseJsonType, String description) {
            valueHolder.responseJsonType = responseJsonType;
            valueHolder.responseDescription = description;
            return self();
        }

        public SELF responseArray(Class<?> elementMappedJavaType, String description) {
            this.responseArray(elementMappedJavaType.getSimpleName(), description);
            return self();
        }

        public SELF responseArray(String responseElementType, String description) {
            valueHolder.responseJsonType = "array";
            valueHolder.responseElementType = responseElementType;
            valueHolder.responseDescription = description;
            return self();
        }

        public SELF expectableError(int errorCode, String description) {
            valueHolder.expectableErrors.add(new ExpectableError(errorCode, description));
            return self();
        }

        public WebInterface build() {
            WebInterface webInterface = valueHolder.copy();
            webInterface.verify();
            return webInterface;
        }
    }

    public static class NestedWebInterfaceBuilder<P> extends WebInterfaceBuilder<NestedWebInterfaceBuilder<P>> {

        private final P parent;

        protected NestedWebInterfaceBuilder(P parent) {
            this.parent = parent;
        }

        public P and() {
            return parent;
        }
    }
}
