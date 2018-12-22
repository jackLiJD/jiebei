package com.ald.ebei.network.entity;

import java.util.List;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/1/7
 * 描述：分页返回值
 * 修订历史：
 */
public class EbeiApiPageData<T> {

    private int nextTimestamp;
    private int totalPage;
    private int pageNo;
    private List<T> dataList;

    public EbeiApiPageData() {
    }

    public int getNextTimestamp() {
        return nextTimestamp;
    }

    public void setNextTimestamp(int nextTimestamp) {
        this.nextTimestamp = nextTimestamp;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }
}
