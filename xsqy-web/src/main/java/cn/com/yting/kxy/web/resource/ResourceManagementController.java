/*
 * Created 2017-7-15 18:09:12
 */
package cn.com.yting.kxy.web.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.Part;

import cn.com.yting.kxy.core.resource.LazyLoadResourceContext;
import cn.com.yting.kxy.core.resource.Resource;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.resource.ResourceLoader;
import cn.com.yting.kxy.core.resource.XmlResourceLoader;
import cn.com.yting.kxy.core.util.JsonUtils;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Azige
 */
@Controller
@RequestMapping("/management/resources")
public class ResourceManagementController {

    @Autowired
    private ResourceContext resourceContext;

    @RequestMapping("")
    public String viewAllStatus(Model model) {
        if (resourceContext instanceof LazyLoadResourceContext) {
            LazyLoadResourceContext lazyLoadResourceContext = (LazyLoadResourceContext) resourceContext;
            model.addAttribute("loaders", lazyLoadResourceContext.getAllLoaderContainers().stream()
                .map(loaderContainer -> {
                    ResourceLoader<?> loader = loaderContainer.getLoader();
                    if (loader instanceof XmlResourceLoader) {
                        return new LoaderModel(
                            loaderContainer.getLoader().getClass().getName(),
                            loaderContainer.isInitilized(),
                            true,
                            ((XmlResourceLoader) loader).getDefaultResourceName()
                        );
                    } else {
                        return new LoaderModel(
                            loaderContainer.getLoader().getClass().getName(),
                            loaderContainer.isInitilized(),
                            false,
                            null
                        );
                    }
                })
                .sorted(Comparator.comparing(LoaderModel::getName))
                .collect(Collectors.toList())
            );
        }
        return "resources-management/index";
    }

    @RequestMapping("/{loaderName:[0-9a-zA-z\\.]*}")
    public String viewLoader(
        @PathVariable("loaderName") String loaderName,
        Model model
    ) {
        try {
            @SuppressWarnings("unchecked")
            ResourceLoader<? extends Resource> loader = resourceContext.getByLoaderType((Class<ResourceLoader<?>>) Class.forName(loaderName));
            List<Long> ids = new ArrayList<>(loader.getAll().keySet());
            Collections.sort(ids);
            model.addAttribute("ids", ids);
        } catch (ClassNotFoundException ex) {
            model.addAttribute("message", "资源加载器不存在, loaderName=" + loaderName);
        }
        return "resources-management/loader";
    }

    @RequestMapping("/{loaderName:[0-9a-zA-z\\.]*}/{resourceId}")
    public String viewResource(
        @PathVariable("loaderName") String loaderName,
        @PathVariable("resourceId") long resourceId,
        Model model
    ) {
        try {
            @SuppressWarnings("unchecked")
            ResourceLoader<? extends Resource> loader = resourceContext.getByLoaderType((Class<ResourceLoader<?>>) Class.forName(loaderName));
            Resource resource = loader.get(resourceId);
            model.addAttribute("jsonText", JsonUtils.toJson(resource));
        } catch (ClassNotFoundException ex) {
            model.addAttribute("message", "资源加载器不存在, loaderName=" + loaderName);
        }
        return "resources-management/resource";
    }

    @RequestMapping(path = "update", method = RequestMethod.POST)
    public String updateAllResource(
        @RequestPart("file") Part part,
        RedirectAttributes redirectAttributes
    ) throws IOException {
        if (resourceContext instanceof LazyLoadResourceContext) {
            File tempFile = File.createTempFile("kxy-resources", ".tmp");
            try (InputStream input = part.getInputStream(); OutputStream output = new FileOutputStream(tempFile)) {
                StreamUtils.copy(input, output);
            }
            LazyLoadResourceContext lazyLoadResourceContext = (LazyLoadResourceContext) resourceContext;
            lazyLoadResourceContext.reloadAll(new URLClassLoader(new URL[]{tempFile.toURI().toURL()}, null));
            redirectAttributes.addFlashAttribute("message", "资源更新成功");
        } else {
            redirectAttributes.addFlashAttribute("message", "当前资源环境不能更新资源");
        }
        return "redirect:/management/resources";
    }

    @RequestMapping(path = "/{loaderName:[0-9a-zA-z\\.]*}/update", method = RequestMethod.POST)
    public String updateResource(
        @PathVariable("loaderName") String loaderName,
        @RequestPart("file") Part part,
        RedirectAttributes redirectAttributes
    ) throws IOException {
        try {
            @SuppressWarnings("unchecked")
            Class<? extends ResourceLoader<?>> loaderType = (Class<? extends ResourceLoader<?>>) Class.forName(loaderName);
            ResourceLoader<?> loader = resourceContext.getByLoaderType(loaderType);
            if (loader != null) {
                if (loader instanceof XmlResourceLoader) {
                    XmlResourceLoader<?> xmlResourceLoader = (XmlResourceLoader<?>) loader;
                    try (InputStream input = part.getInputStream()) {
                        xmlResourceLoader.reload(resourceContext, input);
                    }
                    redirectAttributes.addFlashAttribute("message", "资源更新成功，loaderName=" + loaderName);
                } else {
                    redirectAttributes.addFlashAttribute("message", "资源加载器不是 XmlResourceLoader 类型，loaderName=" + loaderName);
                }
            } else {
                redirectAttributes.addFlashAttribute("message", "资源加载器不存在，loaderName=" + loaderName);
            }
        } catch (ClassNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", "资源加载器不存在，loaderName=" + loaderName);
        }
        return "redirect:/management/resources";
    }

    @Value
    public static class LoaderModel {

        private String name;
        private boolean initilized;
        private boolean updatable;
        private String defaultResourceName;
    }
}
