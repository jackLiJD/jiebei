package com.ald.ebei.network.interceptor;

import java.util.Map;


/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/1/6
 * 描述：添加动态参数接口
 * 修订历史：
 */
public interface EbeiIBasicDynamic {
    void dynamicParams(Map<String, String> paramsMap);
}
