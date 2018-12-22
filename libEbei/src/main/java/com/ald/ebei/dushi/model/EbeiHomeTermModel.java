package com.ald.ebei.dushi.model;

import com.ald.ebei.model.EbeiBaseModel;

/**
 * 首页借款期限实体类
 * Created by ywd on 2018/11/18.
 */

public class EbeiHomeTermModel extends EbeiBaseModel {
    private String term;
    private boolean checked;

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
