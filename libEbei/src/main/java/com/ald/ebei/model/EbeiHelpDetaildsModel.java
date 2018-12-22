package com.ald.ebei.model;

public class EbeiHelpDetaildsModel extends EbeiBaseModel {


    /**
     * id : 2
     * gmtCreate : 2018-07-23T14:37:32.000+0800
     * gmtModified : 2018-07-23T14:37:32.000+0800
     * categoryId : 1
     * title : 最多能借多少款？
     * content : 想要多少就多少
     * sort : 0
     */

    private int id;
    private String gmtCreate;
    private String gmtModified;
    private int categoryId;
    private String title;
    private String content;
    private int sort;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(String gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(String gmtModified) {
        this.gmtModified = gmtModified;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
