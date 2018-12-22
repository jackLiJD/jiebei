package com.ald.ebei.dushi.model;

import com.ald.ebei.model.EbeiBaseModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页实体类
 * Created by ywd on 2018/11/23.
 */

public class EbeiHomeModel extends EbeiBaseModel {
    private int allPeriods;//借款期数
    private BigDecimal allRepayAmount;//提前结清金额
    private String allRepaySwitch;//提前结清开关 = ['Y(开)', 'N(关)']
    private String bankName;//银行名称
    private String cardNo;//银行卡号
    private int countdownDay;//本期还款倒计时
    private BigDecimal currWaitRepayAmount;//本期待还
    private BigDecimal downAmount;//借款额度下限
    private BigDecimal loanAmount;//借款总本金
    private long loanId;//借款id
    private String loanReviewStatus;//借款审核状态 = ['NO_LOAN(无借款)', 'REVIEW(审核中)', 'WAIT_LOAN(待放款)', 'AGREE(审核通过)']
    private String loanStatus;//借款状态 = ['WAIT_REPAY(待还款)', 'WAIT_PROCE(还款处理中)', 'OVERDUE(已逾期)']
    private BigDecimal monthAmount;//月供
    private int overDueCountdownDay;//逾期还款倒计时
    private String overDueRepayDay;//逾期还款日
    private BigDecimal overdueWaitAmount;//逾期待还总金额
    private String partRepaySwitch;//部分还款开关 = ['Y(开)', 'N(关)']
    private int periods;//借款期限
    //private BigDecimal periodsWaitAmount;// 本期待还
    private String realNameStatus;//实名认证状态 = ['N(未认证)', 'Y(已认证)', 'W(认证中)']
    private List<RemitListModel> remitList;//未出账分期减免手续费
    private String repayDay;//本期还款日
    private BigDecimal repayingAmount;//还款中金额
    private String riskStatus;//认证状态 = ['N(未认证)', 'Y(已认证)']
    private BigDecimal upAmount;//借款额度上限
    private BigDecimal allWaitRepayAmount;//所有代还
    private String repayFailMsg;
    private List<String> pics;

    public class RemitListModel extends EbeiBaseModel {
        private int nper;//期数
        private BigDecimal remitFee;//减免手续费

        public int getNper() {
            return nper;
        }

        public void setNper(int nper) {
            this.nper = nper;
        }

        public BigDecimal getRemitFee() {
            return remitFee;
        }

        public void setRemitFee(BigDecimal remitFee) {
            this.remitFee = remitFee;
        }
    }

    public List<String> getPics() {
        if (pics == null) {
            return new ArrayList<>();
        }
        return pics;
    }

    public int getCountdownDay() {
        return countdownDay;
    }

    public void setCountdownDay(int countdownDay) {
        this.countdownDay = countdownDay;
    }

    public BigDecimal getCurrWaitRepayAmount() {
        return currWaitRepayAmount;
    }

    public void setCurrWaitRepayAmount(BigDecimal currWaitRepayAmount) {
        this.currWaitRepayAmount = currWaitRepayAmount;
    }

    public long getLoanId() {
        return loanId;
    }

    public void setLoanId(long loanId) {
        this.loanId = loanId;
    }

    public int getOverDueCountdownDay() {
        return overDueCountdownDay;
    }

    public void setOverDueCountdownDay(int overDueCountdownDay) {
        this.overDueCountdownDay = overDueCountdownDay;
    }

    public List<RemitListModel> getRemitList() {
        return remitList;
    }

    public void setRemitList(List<RemitListModel> remitList) {
        this.remitList = remitList;
    }

    public int getAllPeriods() {
        return allPeriods;
    }

    public void setAllPeriods(int allPeriods) {
        this.allPeriods = allPeriods;
    }

    public BigDecimal getAllRepayAmount() {
        return allRepayAmount;
    }

    public void setAllRepayAmount(BigDecimal allRepayAmount) {
        this.allRepayAmount = allRepayAmount;
    }

    public String getAllRepaySwitch() {
        return allRepaySwitch;
    }

    public void setAllRepaySwitch(String allRepaySwitch) {
        this.allRepaySwitch = allRepaySwitch;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public BigDecimal getDownAmount() {
        return downAmount;
    }

    public void setDownAmount(BigDecimal downAmount) {
        this.downAmount = downAmount;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getLoanReviewStatus() {
        return loanReviewStatus;
    }

    public void setLoanReviewStatus(String loanReviewStatus) {
        this.loanReviewStatus = loanReviewStatus;
    }

    public String getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(String loanStatus) {
        this.loanStatus = loanStatus;
    }

    public BigDecimal getMonthAmount() {
        return monthAmount;
    }

    public void setMonthAmount(BigDecimal monthAmount) {
        this.monthAmount = monthAmount;
    }

    public String getOverDueRepayDay() {
        return overDueRepayDay;
    }

    public void setOverDueRepayDay(String overDueRepayDay) {
        this.overDueRepayDay = overDueRepayDay;
    }

    public BigDecimal getOverdueWaitAmount() {
        return overdueWaitAmount;
    }

    public void setOverdueWaitAmount(BigDecimal overdueWaitAmount) {
        this.overdueWaitAmount = overdueWaitAmount;
    }

    public String getPartRepaySwitch() {
        return partRepaySwitch;
    }

    public void setPartRepaySwitch(String partRepaySwitch) {
        this.partRepaySwitch = partRepaySwitch;
    }

    public int getPeriods() {
        return periods;
    }

    public void setPeriods(int periods) {
        this.periods = periods;
    }

    public String getRealNameStatus() {
        return realNameStatus;
    }

    public void setRealNameStatus(String realNameStatus) {
        this.realNameStatus = realNameStatus;
    }

    public String getRepayDay() {
        return repayDay;
    }

    public void setRepayDay(String repayDay) {
        this.repayDay = repayDay;
    }

    public BigDecimal getRepayingAmount() {
        return repayingAmount;
    }

    public void setRepayingAmount(BigDecimal repayingAmount) {
        this.repayingAmount = repayingAmount;
    }

    public String getRiskStatus() {
        return riskStatus;
    }

    public void setRiskStatus(String riskStatus) {
        this.riskStatus = riskStatus;
    }

    public BigDecimal getUpAmount() {
        return upAmount;
    }

    public void setUpAmount(BigDecimal upAmount) {
        this.upAmount = upAmount;
    }

    public BigDecimal getAllWaitRepayAmount() {
        return allWaitRepayAmount;
    }

    public void setAllWaitRepayAmount(BigDecimal allWaitRepayAmount) {
        this.allWaitRepayAmount = allWaitRepayAmount;
    }

    public String getRepayFailMsg() {
        return repayFailMsg;
    }

    public void setRepayFailMsg(String repayFailMsg) {
        this.repayFailMsg = repayFailMsg;
    }
}
