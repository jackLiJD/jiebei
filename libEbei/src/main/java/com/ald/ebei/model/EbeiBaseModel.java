package com.ald.ebei.model;

import java.io.Serializable;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/1/14 17:40
 * 描述：基础Model
 * 修订历史：
 */
public class EbeiBaseModel implements Serializable {
    private long modelId;

    public long getModelId() {
        return modelId;
    }

    public void setModelId(long modelId) {
        this.modelId = modelId;
    }

}
