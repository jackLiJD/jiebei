package com.ald.ebei.auth.model;

import com.ald.ebei.model.EbeiBaseModel;

import java.math.BigDecimal;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/1/19 19:31
 * 描述：
 * 修订历史：
 */
public class EbeiZMXYModel extends EbeiBaseModel {
    private String tooLow;
    private BigDecimal creditAmount;
    private long zmScore;//	Number	芝麻信用分
    private long ivsScore;//	Number	反欺诈分
    private String allowConsume;//	String	是否允许分期,Y或者N
    private String creditLevel;
    private long creditAssessTime;


    public EbeiZMXYModel() {
    }

    public long getCreditAssessTime() {
        return creditAssessTime;
    }

    public void setCreditAssessTime(long creditAssessTime) {
        this.creditAssessTime = creditAssessTime;
    }

    public String getCreditLevel() {
        return creditLevel;
    }

    public void setCreditLevel(String creditLevel) {
        this.creditLevel = creditLevel;
    }

    public String getAllowConsume() {
        return allowConsume;
    }

    public void setAllowConsume(String allowConsume) {
        this.allowConsume = allowConsume;
    }

    public String getTooLow() {
        return tooLow;
    }

    public void setTooLow(String tooLow) {
        this.tooLow = tooLow;
    }

    public BigDecimal getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(BigDecimal creditAmount) {
        this.creditAmount = creditAmount;
    }

    public long getZmScore() {
        return zmScore;
    }

    public void setZmScore(long zmScore) {
        this.zmScore = zmScore;
    }

    public long getIvsScore() {
        return ivsScore;
    }

    public void setIvsScore(long ivsScore) {
        this.ivsScore = ivsScore;
    }
}
