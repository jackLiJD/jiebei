package com.ald.ebei.config;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/2/9 14:23
 * 描述：
 * 修订历史：
 */
public interface EbeiServerProvider {
    //获取app接口服务器
    public String getAppServer();

    //获取app图片服务器
    public String getImageServer();

    // 获取app的H5服务器地址
    String getH5Server();

}
