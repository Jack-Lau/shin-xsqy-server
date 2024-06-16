/*
 * Created 2018-6-30 17:23:21
 */
package cn.com.yting.kxy.web.apimodel;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.message.TypeDefinitionsHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Azige
 */
@Controller
@ConditionalOnProperty("kxy.web.debug")
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private List<ModuleApiProvider> moduleApiProviders;
    @Autowired
    private TypeDefinitionsHolder typeDefinitionsHolder;

    private Module rootModule;

    @RequestMapping("")
    public String indexRedirect() {
        return "redirect:/api/";
    }

    @RequestMapping("/")
    public String index() {
        return "api/index";
    }

    @RequestMapping("/view")
    @ResponseBody
    public Object view() {
        return getOrCreateRootModule();
    }

    @RequestMapping(path = "/view/shell", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public void viewShell(HttpServletResponse response) throws IOException {
        ShellCommandsGenerator generator = new ShellCommandsGenerator();
        generator.generate(getOrCreateRootModule(), response.getWriter());
    }

    @RequestMapping(path = "/types")
    @ResponseBody
    public String typeDefinitions() {
        return typeDefinitionsHolder.getCode();
    }

    private synchronized Module getOrCreateRootModule() {
        if (rootModule == null) {
            ModuleBuilder<?> builder = Module.builder()
                .name("root")
                .baseUri("");
            moduleApiProviders.forEach(it -> it.buildModuleApi(builder.submodule()));
            ApiScanner scanner = new ApiScanner();
            scanner.scan().forEach(it -> builder.submodule(it));
            rootModule = builder.build();
        }
        return rootModule;
    }
}
