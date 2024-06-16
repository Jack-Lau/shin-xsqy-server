/*
 * Created 2018-7-2 18:30:31
 */
package cn.com.yting.kxy.web.apimodel;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriTemplate;

/**
 *
 * @author Azige
 */
public class ShellCommandsGenerator {

    private String shellFileHeader = "#!/bin/bash";
    private String rootUrl = "http://localhost:8080/kxy-web";
    private String rootUrlPlaceHolder = "$ROOT";
    private String curlTemplate = "curl -L -i --cookie @cookie --cookie-jar cookie $* 2> /dev/null";
    private String curlTemplateFuncName = "c";

    public void generate(Module module, Writer writer) throws IOException {
        writer
            .append(shellFileHeader).append("\n")
            .append("ROOT=").append(rootUrl).append("\n")
            .append(curlTemplateFuncName).append("() {").append("\n")
            .append(curlTemplate).append("\n")
            .append("}").append("\n");
        generateModule(module, writer, "");
    }

    private void generateModule(Module module, Writer writer, String parentBaseUri) throws IOException {
        String baseUri = parentBaseUri + module.getBaseUri();
        String baseUrl = rootUrlPlaceHolder + baseUri;
        String functionNamePrefix = (parentBaseUri + module.getBaseUri()).replaceFirst("^/", "").replaceAll("/", "_");
        if (!functionNamePrefix.isEmpty()) {
            functionNamePrefix += "_";
        }
        for (WebInterface wi : module.getWebInterfaces()) {
            int paramNumber = 1;
            String functionName;
            if (wi.getShortName() != null) {
                functionName = wi.getShortName();
            } else {
                functionName = functionNamePrefix + wi.getName();
            }
            UriTemplate uriTemplate = new UriTemplate(baseUrl + wi.getUri());
            Map<String, String> paramMap = new LinkedHashMap<>();
            for (RequestParameter param : wi.getRequestParameters()) {
                if (paramNumber < 10) {
                    paramMap.put(param.getName(), "$" + paramNumber);
                } else {
                    paramMap.put(param.getName(), "${" + paramNumber + "}");
                }
                paramNumber++;
            }
            String url;
            try {
                url = uriTemplate.expand(paramMap).toString();
            } catch (IllegalArgumentException ex) {
                throw new IllegalStateException("缺少参数：" + uriTemplate, ex);
            }
            uriTemplate.getVariableNames().forEach(name -> {
                paramMap.remove(name);
            });

            String queryString = "";
            String postString = "";
            String parameterString = paramMap.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.reducing((a, b) -> a + "&" + b))
                .orElse("");
            if (Objects.equals(wi.getHttpMethod(), HttpMethod.POST)) {
                postString = "-X POST ";
                if (!parameterString.equals("")) {
                    postString += "-d \"" + parameterString + "\" ";
                }
            } else {
                if (!parameterString.equals("")) {
                    queryString = "?" + parameterString;
                }
            }

            writer
                .append(functionName).append("() {").append("\n")
                .append(curlTemplateFuncName).append(" ")
                .append(postString).append("\"").append(url).append(queryString).append("\"").append("\n")
                .append("}").append("\n");
        }

        for (Module m : module.getSubmodules()) {
            generateModule(m, writer, baseUri);
        }
    }
}
