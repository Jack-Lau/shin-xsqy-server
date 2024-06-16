/*
 * Created 2018-6-30 17:22:01
 */
package cn.com.yting.kxy.web.apimodel;

import cn.com.yting.kxy.web.apimodel.Module.ModuleBuilder;
import cn.com.yting.kxy.web.apimodel.annotation.ModuleDoc;

/**
 *
 * @author Azige
 * @deprecated 使用 {@link ModuleDoc} 标注来描述 API 模型
 */
@Deprecated
public interface ModuleApiProvider {

    void buildModuleApi(ModuleBuilder<?> builder);
}
