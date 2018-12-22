package com.ald.ebei.model;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/2/9 15:36
 * 描述：
 * 修订历史：
 */
public class EbeiUrlModel extends EbeiBaseModel {
    private String url;
    private String srcFileName;

    public EbeiUrlModel() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSrcFileName() {
        return srcFileName;
    }

    public void setSrcFileName(String srcFileName) {
        this.srcFileName = srcFileName;
    }
}
