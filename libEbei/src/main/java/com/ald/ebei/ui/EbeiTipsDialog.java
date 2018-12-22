package com.ald.ebei.ui;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.ald.ebei.R;
import com.ald.ebei.util.EbeiMiscUtils;


/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/3/9 23:26
 * 描述：tips提示dialog
 * 修订历史：
 */
public class EbeiTipsDialog extends Dialog implements View.OnClickListener {

    private String title;
    private String sureTitle;

    private TextView tvMakeSure;
    private TextView tvTitle;
    private TextView tvContent;

    private MakeSureListener listener;

    public EbeiTipsDialog(@NonNull Context context) {
        this(context, R.style.tipsDialog);
    }

    public EbeiTipsDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        initView();
    }

    private void initView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_tips, null);
        tvMakeSure = (TextView) v.findViewById(R.id.tv_make_sure);
        tvTitle = (TextView) v.findViewById(R.id.tv_title);
        tvContent = (TextView) v.findViewById(R.id.tv_content);
        setContentView(v);

        WindowManager manager = getWindow().getWindowManager();
        Display d = manager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (d.getWidth() * 0.8);
        getWindow().setAttributes(lp);

        tvMakeSure.setOnClickListener(this);
        tvTitle.setText(getTitle());
        tvMakeSure.setText(getSure());
    }

    /*
   *  设置弹出框确认
   * */
    public void setSure(String sureTitle) {
        this.sureTitle = sureTitle;
        if (tvMakeSure != null) {
            tvMakeSure.setText(sureTitle);
        }
    }

    public String getSure() {
        return EbeiMiscUtils.isEmpty(sureTitle) ? getContext().getResources().getString(R.string.permission_make_sure)
                : sureTitle;
    }

    /*
    *  设置弹出框Title
    * */
    public void setTitle(String title) {
        this.title = title;
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    public String getTitle() {
        return EbeiMiscUtils.isEmpty(title) ? getContext().getResources().getString(R.string.permission_title) : title;
    }

    /*
        * 设置提示框内容
        * */
    public void setContent(String tips) {
        if (tvContent != null) {
            tvContent.setText(tips);
        }
    }

    public void setContent(int resId) {
        if (0 != resId) {
            tvContent.setText(resId);
        }
    }

    public interface MakeSureListener {
        void onSureClick(Dialog dialog, View view);
    }

    public void setListener(MakeSureListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id==R.id.tv_make_sure){
            if (listener != null) {
                listener.onSureClick(this, view);
            }
            dismiss();
        }
    }
}
