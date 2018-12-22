package com.ald.ebei.network;

import android.content.Context;
import android.content.Intent;

import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.network.exception.EbeiApiException;
import com.ald.ebei.network.exception.EbeiApiExceptionEnum;
import com.ald.ebei.util.EbeiUIUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


/*
 * Created by liangchen on 2018/5/28.
 */

public class EbeiBaseObserver<T> implements Observer<T> {
    private Context context;//传了就表示需要加载框，必须传Activity或Fragment的上下文，用于创建dialog
    private RefreshLayout refreshLayout = null;

    public EbeiBaseObserver(RefreshLayout refreshLayout) {
        this.refreshLayout = refreshLayout;
    }

    public EbeiBaseObserver() {
    }

    public EbeiBaseObserver(Context context) {
        this.context = context;
    }

    @Override
    public void onSubscribe(Disposable d) {
        if (context != null) {
            EbeiNetworkUtil.showCutscenesByRx(context, d);
        }
    }

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable t) {
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
                if (!errorHandle(ebeiApiException)) {
                    EbeiUIUtils.showToast(ebeiApiException.getMsg());
                }
            }
            onFailure(ebeiApiException);
        }
        t.printStackTrace();
    }

    public void onFailure(EbeiApiException ebeiApiException) {
        int code = ebeiApiException.getCode();
        if (code == EbeiApiExceptionEnum.TOKEN_INVALID.getErrorCode()) {
            Intent broadcastIntent = new Intent(EbeiConfig.ACTION_API_OPEN);
            broadcastIntent.putExtra(EbeiConfig.EXTRA_ERR_CODE, code);
            EbeiConfig.getLocalBroadcastManager().sendBroadcast(broadcastIntent);
        } else if (code == EbeiApiExceptionEnum.SIGN_EEOR.getErrorCode()) {
            Intent broadcastIntent = new Intent(EbeiConfig.ACTION_API_OPEN);
            broadcastIntent.putExtra(EbeiConfig.EXTRA_ERR_CODE, code);
            EbeiConfig.getLocalBroadcastManager().sendBroadcast(broadcastIntent);
        }
    }

    @Override
    public void onComplete() {
        EbeiNetworkUtil.dismissForceCutscenes();
    }

    protected boolean errorHandle(EbeiApiException ebeiApiException) {
        if (ebeiApiException.getCode() == EbeiApiExceptionEnum.EXIST_MOBILE.getErrorCode()) {
            return true;
        }
        if (ebeiApiException.getCode() == EbeiApiExceptionEnum.EMPTY_TOKEN.getErrorCode()) {
            return true;
        }
        return false;
    }
}
