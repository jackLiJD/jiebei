package com.ald.ebei.auth.model;

import com.ald.ebei.model.EbeiBaseModel;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/2/22 15:22
 * 描述：
 * 修订历史：
 */
public class EbeiContactsSycModel extends EbeiBaseModel {
    private String allowConsume;

    public EbeiContactsSycModel() {
    }

    public String getAllowConsume() {
        return allowConsume;
    }

    public void setAllowConsume(String allowConsume) {
        this.allowConsume = allowConsume;
    }
}
