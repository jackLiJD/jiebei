package com.ald.ebei.protocol;


import com.ald.ebei.protocol.data.EbeiProtocolData;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/2/15 11:34
 * 描述：AlaWebView处理事件接口
 * 修订历史：
 */
public interface ProtocolHandler {
    String handleProtocol(String group, EbeiProtocolData data);
}
