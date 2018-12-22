package com.ald.ebei.config;


/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/1/6
 * 描述： 统计的组件的名称提供器，供后台统计使用;每个界面，比如Activity或者Fragment都必须有一个显式的中文名称
 * 修订历史：
 */
public interface EbeiPermissionsResult {
    void onUIRequestPermissionsGrantedResult(int requestCode);
}
