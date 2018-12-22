package com.ald.ebei.util;

import android.app.Activity;
import android.app.Application;


import com.ald.ebei.R;
import com.ald.ebei.auth.EbeiMxAuthCallBack;
import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.util.log.EbeiLogger;
import com.moxie.client.exception.MoxieException;
import com.moxie.client.manager.MoxieCallBack;
import com.moxie.client.manager.MoxieCallBackData;
import com.moxie.client.manager.MoxieContext;
import com.moxie.client.manager.MoxieSDK;
import com.moxie.client.model.MxLoginCustom;
import com.moxie.client.model.MxParam;
import com.moxie.client.model.TitleParams;

import java.util.HashMap;
import java.util.Map;

/**
 * 魔蝎认证工具类
 * Created by sean yu on 2017/7/10.
 */

public class EbeiMXAuthUtils {
    private static final String PARAM_TASK_FUND = MxParam.PARAM_TASK_FUND;
    private static final String PARAM_TASK_ONLINEBANK = MxParam.PARAM_TASK_ONLINEBANK;
    private static final String PARAM_TASK_SECURITY = MxParam.PARAM_TASK_SECURITY;
    private static final String PARAM_TASK_EMAIL = MxParam.PARAM_TASK_EMAIL;
    private static final String PARAM_TASK_ALIPAY = MxParam.PARAM_TASK_ALIPAY;
    private static final String PARAM_TASK_ZHENGXIN = MxParam.PARAM_TASK_ZHENGXIN;
    private static final String PARAM_TASK_CHSI = MxParam.PARAM_TASK_CHSI;
    private static final String PARAM_TASK_CARRIER = MxParam.PARAM_TASK_CARRIER;

    private static final String DEBUG_MX_API_KEY = "2accfe61d2d34214a7f92627def213ab";
    private static final String RELEASE_MX_API_KEY = "5b43e12df22d4c9396e91a19c2c047b0";

    private MxParam mxParam;
    private Activity activity;
    private EbeiMxAuthCallBack callBack;

    public EbeiMXAuthUtils(Activity activity) {
        this.activity = activity;
        mxParam = new MxParam();
    }


    /*
      初始化摩蝎SDK
    * */
    public static void initSDK(Application application) {
        MoxieSDK.init(application);
    }

    /**
     * 谁知认证回调监听
     *
     * @param callBack callBack
     * @return EbeiMXAuthUtils
     */
    public EbeiMXAuthUtils setCallBack(EbeiMxAuthCallBack callBack) {
        this.callBack = callBack;
        return this;
    }

    /**
     * 配置魔蝎api_key
     */
    private String getMXApiKey() {
        if (EbeiConfig.isDebug()) {
            return DEBUG_MX_API_KEY;
        }
        return RELEASE_MX_API_KEY;
    }


    /**
     * 配置魔蝎参数
     */
    public void configMxParams(String userId) {
        mxParam.setUserId(userId);
        mxParam.setApiKey(getMXApiKey());
        mxParam.setCacheDisable(MxParam.PARAM_COMMON_YES);//不使用缓存（非必传）
        mxParam.setQuitDisable(true); //设置导入过程中，触发返回键或者点击actionbar的返回按钮的时候，不执行魔蝎的默认行为
        mxParam.setThemeColor("#5088E5");
        configTitleParams();
    }


    /**
     * 配置魔蝎界面样式
     */
    private void configTitleParams() {
        TitleParams titleParams = new TitleParams.Builder()
                //不设置此方法会默认使用魔蝎的icon
                .leftNormalImgResId(R.drawable.back)
                //用于设置selector，表示按下的效果，不设置默认使用leftNormalImgResId()设置的图片
                .leftPressedImgResId(R.drawable.moxie_client_banner_back_black)
//                .titleColor(c.getResources().getColor(R.color.colorWhite))
                .backgroundColor(activity.getResources().getColor(R.color.colorAccent_new))
//                .backgroundDrawable(R.drawable.bg_actionbar)
//                .rightNormalImgResId(R.drawable.refresh)
                .immersedEnable(true)
                .build();
        mxParam.setTitleParams(titleParams);

    }


    /**
     * 网银信用卡认证
     */
    public void handleOnlineBack() {
        if (mxParam == null) {
            return;
        }
        //1.打开信用卡列表页面
        // MxParam.PARAM_ITEM_TYPE_CREDITCARD（信用卡列表）
        // MxParam.PARAM_ITEM_TYPE_DEBITCARD（储蓄卡列表）
        mxParam.setTaskType(MxParam.PARAM_TASK_EMAIL);
        handleAuth();
        showCardAuthToast();
    }


    /**
     * 网银认证(借记卡)
     */
    public void handleOnlineBank() {
        if (mxParam == null) {
            return;
        }
        mxParam.setTaskType(MxParam.PARAM_TASK_ONLINEBANK);
        MxLoginCustom loginCustom = new MxLoginCustom();
        loginCustom.setLoginType(MxLoginCustom.LOGIN_TYPE_V_DEBITCARD);
        mxParam.setLoginCustom(loginCustom);
        handleAuth();
    }


    /**
     * 公积金认证
     */
    public void handleFund() {
        if (mxParam == null) {
            return;
        }
        mxParam.setTaskType(MxParam.PARAM_TASK_FUND);
        handleAuth();
    }

    /**
     * 社保 认证
     */
    public void handleSecurity() {
        if (mxParam == null) {
            return;
        }
        mxParam.setTaskType(MxParam.PARAM_TASK_SECURITY);
        handleAuth();
    }

    /**
     * 支付宝 认证
     */
    public void handleAlipay() {
        if (mxParam == null) {
            return;
        }
        mxParam.setTaskType(MxParam.PARAM_TASK_ALIPAY);
        handleAuth();
    }

    /**
     * 学信网认证
     */
    public void handleChsi() {
        if (mxParam == null) {
            return;
        }
        mxParam.setTaskType(MxParam.PARAM_TASK_CHSI);
        handleAuth();
    }

    /**
     * 人行征信认证
     */
    public void handleZhengxin() {
        if (mxParam == null) {
            return;
        }
        mxParam.setTaskType(MxParam.PARAM_TASK_ZHENGXIN);
        handleAuth();
    }

    /**
     * 运营商认证
     *
     * @param phoneNum 用户手机号
     * @param realName 用户姓名
     * @param idCardNo 用户身份证号
     */
    public void handleOperator(String phoneNum, String realName, String idCardNo) {
        if (mxParam == null) {
            return;
        }
        mxParam.setTaskType(MxParam.PARAM_TASK_CARRIER);

        MxLoginCustom loginCustom = new MxLoginCustom();
        Map<String, Object> loginParam = new HashMap<>();
        loginParam.put("phone", phoneNum);
        loginParam.put("name", realName);
        loginParam.put("idcard", idCardNo);
        // 是否允许用户修改以上信息（目前仅支持运营商）
        // MxParam.PARAM_COMMON_NO:不允许用户在页面上修改身份证/手机号/姓名/密码
        // MxParam.PARAM_COMMON_YES:允许用户在页面上修改身份证/手机号/姓名/密码
        loginCustom.setEditable(MxParam.PARAM_COMMON_NO);
        loginCustom.setLoginParams(loginParam);
        mxParam.setLoginCustom(loginCustom);
        handleAuth();
    }


    /**
     * 处理认证信息
     */
    private void handleAuth() {
        if (mxParam == null || activity == null) {
            return;
        }
        MoxieSDK.getInstance().start(activity, mxParam, new MoxieCallBack() {
            /**
             *  物理返回键和左上角返回按钮的back事件以及任务的状态都通过这个函数来回调
             *
             * @param moxieContext       可以用这个来实现在魔蝎的页面弹框或者关闭魔蝎的界面
             * @param moxieCallBackData  我们可以根据 MoxieCallBackData 的code来判断目前处于哪个状态，以此来实现自定义的行为
             * @return 返回true表示这个事件由自己全权处理，返回false会接着执行魔蝎的默认行为
             */
            @Override
            public boolean callback(MoxieContext moxieContext, MoxieCallBackData moxieCallBackData) {
                if (moxieCallBackData != null) {
                    switch (moxieCallBackData.getCode()) {
                        /*
                         * 如果用户正在导入魔蝎SDK会出现这个情况，如需获取最终状态请轮询贵方后台接口
                         * 魔蝎后台会向贵方后台推送Task通知和Bill通知
                         * Task通知：登录成功/登录失败
                         * Bill通知：账单通知
                         */
                        case MxParam.ResultCode.IMPORTING:
                            //导入中先回调h5
                            if (moxieCallBackData.isLoginDone()) {
                                successSwitch(moxieContext, moxieCallBackData.getTaskType());
                            }
                            return true;
                        case MxParam.ResultCode.IMPORT_UNSTART:
//                            showTipDialog(moxieContext);
                            moxieContext.finish();
                            return true;
                        case MxParam.ResultCode.THIRD_PARTY_SERVER_ERROR:
                        case MxParam.ResultCode.MOXIE_SERVER_ERROR:
                        case MxParam.ResultCode.USER_INPUT_ERROR:
                        case MxParam.ResultCode.IMPORT_FAIL:
                            //根据taskType进行对应的处理
                            errorSwitch(moxieContext, moxieCallBackData);
                            return true;

                        case MxParam.ResultCode.IMPORT_SUCCESS:
                            //根据taskType进行对应的处理
                            successSwitch(moxieContext, moxieCallBackData.getTaskType());
                            return true;
                    }
                }
                return false;
            }

            @Override
            public void onError(MoxieContext moxieContext, MoxieException e) {
                super.onError(moxieContext, e);
                EbeiLogger.d("BigdataFragment", "MoxieSDK onError : " + (e != null ? e.toString() : ""));
            }
        });
    }

    /**
     *
     */
    private void showCardAuthToast() {
        EbeiConfig.postDelayOnUiThread(new Runnable() {
            @Override
            public void run() {
                EbeiUIUtils.showToast("请您选择信用卡绑定的邮箱");
            }
        }, 1000);
    }


    /**
     * 显示认证提示框
     */
//    private void showTipDialog(final MoxieContext context) {
//        CreditPromoteDialog dialog = new CreditPromoteDialog(context.getContext());
//        dialog.setContent("退出后需重新进行认证，确定返回？");
//        dialog.setCancelText("取消");
//        dialog.setSureText("确定");
//        dialog.setSureBtnVisible(View.VISIBLE);
//        dialog.setListener(new CreditPromoteDialog.MakeSureListener() {
//            @Override
//            public void onSureClick(Dialog dialog, View view) {
//                dialog.dismiss();
//                context.finish();
//            }
//        });
//        dialog.show();
//    }

    /**
     * 设置回调信息
     *
     * @param authCode 回调码
     * @param authMsg  回调信息
     */
    private void setCallBakInfo(String authCode, String authMsg) {
        if (EbeiMXAuthUtils.this.callBack != null) {
            callBack.authSuccess(authCode, authMsg, mxParam.getUserId());
        }
    }

    /**
     * 设置回调信息
     *
     * @param authCode 回调码
     * @param authMsg  回调信息
     */
    private void setErrorInfo(String authCode, String authMsg) {
        if (EbeiMXAuthUtils.this.callBack != null) {
            callBack.authError(authCode, authMsg);
        }
    }

    private void successSwitch(MoxieContext moxieContext, String taskType) {
        switch (taskType) {
            case MxParam.PARAM_TASK_FUND:
                setCallBakInfo(PARAM_TASK_FUND, "公积金导入成功");
                break;

            case MxParam.PARAM_TASK_ONLINEBANK:
                setCallBakInfo(PARAM_TASK_ONLINEBANK, "网银导入成功");
                break;

            case MxParam.PARAM_TASK_EMAIL:
                setCallBakInfo(PARAM_TASK_EMAIL, "邮箱导入成功");
                break;

            case MxParam.PARAM_TASK_SECURITY:
                setCallBakInfo(PARAM_TASK_SECURITY, "社保导入成功");
                break;

            case MxParam.PARAM_TASK_ALIPAY:
                setCallBakInfo(PARAM_TASK_ALIPAY, "支付宝导入成功");
                break;
            case MxParam.PARAM_TASK_CHSI:
                setCallBakInfo(PARAM_TASK_CHSI, "学信网导入成功");
                break;
            case MxParam.PARAM_TASK_ZHENGXIN:
                setCallBakInfo(PARAM_TASK_ZHENGXIN, "人行征信导入成功");
                break;
            case MxParam.PARAM_TASK_CARRIER:
                setCallBakInfo(PARAM_TASK_CARRIER, "运营商导入成功");
                break;

        }
        moxieContext.finish();
    }

    private void errorSwitch(MoxieContext moxieContext, MoxieCallBackData moxieCallBackData) {
        switch (moxieCallBackData.getTaskType()) {
            case MxParam.PARAM_TASK_FUND:
                setErrorInfo(PARAM_TASK_FUND, moxieCallBackData.getMessage());
                break;

            case MxParam.PARAM_TASK_ONLINEBANK:
                setErrorInfo(PARAM_TASK_ONLINEBANK, moxieCallBackData.getMessage());
                break;

            case MxParam.PARAM_TASK_EMAIL:
                setErrorInfo(PARAM_TASK_EMAIL, moxieCallBackData.getMessage());
                break;

            case MxParam.PARAM_TASK_SECURITY:
                setErrorInfo(PARAM_TASK_SECURITY, moxieCallBackData.getMessage());
                break;

            case MxParam.PARAM_TASK_ALIPAY:
                setErrorInfo(PARAM_TASK_ALIPAY, moxieCallBackData.getMessage());
                break;

            case MxParam.PARAM_TASK_CHSI:
                setErrorInfo(PARAM_TASK_CHSI, moxieCallBackData.getMessage());
                break;
            case MxParam.PARAM_TASK_ZHENGXIN:
                setErrorInfo(PARAM_TASK_ZHENGXIN, moxieCallBackData.getMessage());
                break;
            case MxParam.PARAM_TASK_CARRIER:
                setErrorInfo(PARAM_TASK_CARRIER, moxieCallBackData.getMessage());
                break;
        }
        moxieContext.finish();
    }

}
