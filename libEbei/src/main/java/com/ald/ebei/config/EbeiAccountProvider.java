package com.ald.ebei.config;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/2/9 14:23
 * 描述：
 * 修订历史：
 */
public interface EbeiAccountProvider {
    //登录账号
    public String getUserName();
    //登录账号后的token
    public String getUserToken();
}
