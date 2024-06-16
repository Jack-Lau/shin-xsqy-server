/*
 * Created 2018-11-19 15:49:35
 */
package cn.com.yting.kxy.web.apimodel;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.WebInterface.WebInterfaceBuilder;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.MethodIntrospector.MetadataLookup;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.web.bind.annotation.RequestMapping;
import cn.com.yting.kxy.web.apimodel.annotation.ModuleDoc;
import cn.com.yting.kxy.web.apimodel.annotation.ParamDoc;
import cn.com.yting.kxy.web.apimodel.annotation.QueueNotification;
import cn.com.yting.kxy.web.apimodel.annotation.TopicNotification;
import cn.com.yting.kxy.web.apimodel.annotation.WebInterfaceDoc;
import com.google.common.primitives.Primitives;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * 用于扫描被 {@link ModuleDoc} 标注的类型以生成 API 模型
 *
 * @author Azige
 */
public class ApiScanner {

    public List<Module> scan() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ModuleDoc.class));
        return scanner.findCandidateComponents("cn.com.yting.kxy.web").stream()
            .map(beanDefinition -> {
                try {
                    ModuleBuilder<?> builder = Module.builder();
                    Class<?> controllerClass = Class.forName(beanDefinition.getBeanClassName());
                    RequestMapping classMapping = AnnotatedElementUtils.findMergedAnnotation(controllerClass, RequestMapping.class);
                    if (classMapping != null) {
                        builder.baseUri(classMapping.path()[0]);
                    }
                    ModuleDoc classDoc = AnnotatedElementUtils.findMergedAnnotation(controllerClass, ModuleDoc.class);
                    assert classDoc != null;
                    builder.name(classDoc.moduleName());
                    MethodIntrospector.selectMethods(
                        controllerClass,
                        (MetadataLookup<RequestMapping>) (method -> AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class))
                    ).forEach((method, anno) -> {
                        WebInterfaceBuilder<?> interfaceBuilder = builder.webInterface();
                        interfaceBuilder.uri(anno.path()[0]);
                        if (Arrays.asList(anno.method()).contains(RequestMethod.POST)) {
                            interfaceBuilder.post();
                        }
                        WebInterfaceDoc methodDoc = AnnotatedElementUtils.findMergedAnnotation(method, WebInterfaceDoc.class);
                        if (methodDoc != null) {
                            if (!methodDoc.name().isEmpty()) {
                                interfaceBuilder.name(methodDoc.name());
                            } else {
                                interfaceBuilder.name(method.getName());
                            }
                            interfaceBuilder.description(methodDoc.description());
                            Class<?> returnType = method.getReturnType();
                            if (Collection.class.isAssignableFrom(returnType)) {
                                Type collectionElementType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
                                if (collectionElementType instanceof Class) {
                                    interfaceBuilder.responseArray((Class<?>) collectionElementType, methodDoc.response());
                                } else {
                                    System.out.println(collectionElementType);
                                }
                            } else {
                                interfaceBuilder.response(returnType, methodDoc.response());
                            }
                        }
                        Stream.of(method.getParameters()).forEach(parameter -> {
                            ParamDoc paramDoc = AnnotatedElementUtils.findMergedAnnotation(parameter, ParamDoc.class);
                            if (paramDoc != null) {
                                String type;
                                String name;
                                String description = paramDoc.value();
                                Class<?> parameterType = parameter.getType();
                                if (parameterType.isPrimitive()) {
                                    if (Number.class.isAssignableFrom(Primitives.wrap(parameterType))) {
                                        type = "number";
                                    } else {
                                        type = parameterType.getSimpleName();
                                    }
                                } else if (Number.class.isAssignableFrom(parameterType)) {
                                    type = "number";
                                } else if (parameterType.equals(String.class)) {
                                    type = "string";
                                } else {
                                    type = parameterType.getSimpleName();
                                }
                                RequestParam requestParam;
                                PathVariable pathVariable;
                                if ((requestParam = AnnotatedElementUtils.findMergedAnnotation(parameter, RequestParam.class)) != null) {
                                    name = requestParam.name();
                                } else if ((pathVariable = AnnotatedElementUtils.findMergedAnnotation(parameter, PathVariable.class)) != null) {
                                    name = pathVariable.name();
                                } else {
                                    name = parameter.getName();
                                }
                                interfaceBuilder.requestParameter(type, name, description);
                            }

                            RequestBody requestBody = AnnotatedElementUtils.findMergedAnnotation(parameter, RequestBody.class);
                            if (requestBody != null) {
                                interfaceBuilder.requestBody(parameter.getType());
                            }

                            if (Pageable.class.isAssignableFrom(parameter.getType())) {
                                interfaceBuilder.requestPagenationParameters();
                            }
                        });
                    });
                    MethodIntrospector.selectMethods(
                        controllerClass,
                        (MetadataLookup<QueueNotification>) (method -> AnnotatedElementUtils.findMergedAnnotation(method, QueueNotification.class))
                    ).forEach((method, anno) -> {
                        builder.webNotification()
                            .queue(anno.destination())
                            .description(anno.description())
                            .messageType(anno.messageType());
                    });
                    MethodIntrospector.selectMethods(
                        controllerClass,
                        (MetadataLookup<TopicNotification>) (method -> AnnotatedElementUtils.findMergedAnnotation(method, TopicNotification.class))
                    ).forEach((method, anno) -> {
                        builder.webNotification()
                            .topic(anno.destination())
                            .description(anno.description())
                            .messageType(anno.messageType());
                    });
                    return builder.build();
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            })
            .collect(Collectors.toList());
    }
}
