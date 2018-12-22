package com.ald.ebei.network;

import android.content.Context;
import android.content.Intent;

import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.network.exception.EbeiApiException;
import com.ald.ebei.network.exception.EbeiApiExceptionEnum;
import com.ald.ebei.util.EbeiUIUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/1/6
 * 描述：网络请求回调封装类
 * 修订历史：
 */
public abstract class EbeiRequestCallBack<T> implements Callback<T> {
//    private Context context;
    private RefreshLayout refreshLayout;
//    private PtrFrameLayout ptrFrame;
//    private boolean isShow = false;

    public EbeiRequestCallBack() {

    }

    public EbeiRequestCallBack(Context context) {
//        this.context = context;

    }

//    public EbeiRequestCallBack(boolean isShow) {
//        this.isShow = isShow;
//    }

//    public EbeiRequestCallBack(PtrFrameLayout ptrFrame) {
//        this.ptrFrame = ptrFrame;
//    }

    public EbeiRequestCallBack(RefreshLayout refreshLayout) {
        this.refreshLayout = refreshLayout;
    }

//    public EbeiRequestCallBack(PtrFrameLayout ptrFrame, boolean isShow) {
//        this.ptrFrame = ptrFrame;
//        this.isShow = isShow;
//    }

    public abstract void onSuccess(Call<T> call, Response<T> response);

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
//        if (ptrFrame != null && ptrFrame.isRefreshing()) {
//            ptrFrame.refreshComplete();
//        }
        if (null != refreshLayout) {
            if (refreshLayout.isRefreshing()) refreshLayout.finishRefresh(1500);
            if (refreshLayout.isLoading()) refreshLayout.finishLoadmore(1500);
        }
        EbeiNetworkUtil.dismissCutscenes();
        if (response != null && response.isSuccessful()) {
            onSuccess(call, response);
        }


    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
//        if (ptrFrame != null && ptrFrame.isRefreshing()) {
//            ptrFrame.refreshComplete();
//        }
        if (null != refreshLayout) {
            if (refreshLayout.isRefreshing()) refreshLayout.finishRefresh(1500);
            if (refreshLayout.isLoading()) refreshLayout.finishLoadmore(1500);
        }
        EbeiNetworkUtil.dismissCutscenes();
        if (t instanceof EbeiApiException) {
            EbeiApiException ebeiApiException = (EbeiApiException) t;
            int code = ebeiApiException.getCode();
            //消息提醒
            if (code != EbeiApiExceptionEnum.SUCCESS.getErrorCode()) {
                if (EbeiApiExceptionEnum.REQUEST_INVALID_SIGN_ERROR.getErrorCode() == code) {
                    //兼容后台在1005时的用户不存在的错误
                    if (EbeiApiExceptionEnum.USER_NOT_EXIST_ERROR.getDesc().equals(ebeiApiException.getMsg())) {
                        if (!errorHandle(call, ebeiApiException)) {
                            EbeiUIUtils.showToast(ebeiApiException.getMsg());
                        }
                        t.printStackTrace();
                    } else {
                        Intent broadcastIntent = new Intent(EbeiConfig.ACTION_API_OPEN);
                        broadcastIntent.putExtra(EbeiConfig.EXTRA_ERR_CODE, code);
                        EbeiConfig.getLocalBroadcastManager().sendBroadcast(broadcastIntent);
                    }
                } else if (code == EbeiApiExceptionEnum.EMPTY.getErrorCode()
                        || code == EbeiApiExceptionEnum.DEALWITH_YOUDUN_NOTIFY_ERROR.getErrorCode()) {
                    //不做toast提醒
                    t.printStackTrace();
                } else if (code == EbeiApiExceptionEnum.USER_PWD_INPUT_ERROR.getErrorCode()
                        || code == EbeiApiExceptionEnum.USER_PWD_FORBID.getErrorCode()) {
                    Intent broadcastIntent = new Intent(EbeiConfig.ACTION_API_OPEN);
                    broadcastIntent.putExtra(EbeiConfig.EXTRA_ERR_CODE, code);
                    broadcastIntent.putExtra(EbeiConfig.EXTRA_ERR_MSG, ebeiApiException.getMsg());
                    EbeiConfig.getLocalBroadcastManager().sendBroadcast(broadcastIntent);
                } else if (code > EbeiApiExceptionEnum.REQUEST_PARAM_METHOD_NOT_EXIST.getErrorCode()
                        && code != EbeiApiExceptionEnum.REQUEST_INVALID_SIGN_ERROR.getErrorCode() && code != EbeiApiExceptionEnum.REQUEST_PARAM_TOKEN_ERROR.getErrorCode()) {
                    if (code != 1130) {    // 1130 设备不可信，登录的时候去验证登录码
                        if (!errorHandle(call, ebeiApiException)) {
                            EbeiUIUtils.showToast(ebeiApiException.getMsg());
                        }
                    }
                } else if (code == EbeiApiExceptionEnum.TOKEN_INVALID.getErrorCode()) {
                    Intent broadcastIntent = new Intent(EbeiConfig.ACTION_API_OPEN);
                    broadcastIntent.putExtra(EbeiConfig.EXTRA_ERR_CODE, code);
                    EbeiConfig.getLocalBroadcastManager().sendBroadcast(broadcastIntent);
                } else {
                    if (!errorHandle(call, ebeiApiException)) {
                        EbeiUIUtils.showToast(ebeiApiException.getMsg());
                    }
                }
            }
        }
//        else if (isShow) {
//            EbeiUIUtils.showToast(R.string.msg_net_error);
//        }
        t.printStackTrace();
    }

    protected boolean errorHandle(Call<T> call, EbeiApiException ebeiApiException) {
        return false;
    }

}
