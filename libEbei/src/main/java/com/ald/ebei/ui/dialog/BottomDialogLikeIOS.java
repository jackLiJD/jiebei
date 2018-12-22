package com.ald.ebei.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ald.ebei.EbeiHtml5WebView;
import com.ald.ebei.R;
import com.ald.ebei.dushi.model.EbeiProtocolModel;
import com.ald.ebei.util.EbeiActivityUtils;

import java.util.List;

/**
 * Created by ywd on 2018/12/6.
 */

public class BottomDialogLikeIOS extends Dialog {

    public BottomDialogLikeIOS(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private BottomDialogLikeIOS dialog;
        private Context context;
        private List<EbeiProtocolModel> list;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setList(List<EbeiProtocolModel> list) {
            this.list = list;
            return this;
        }

        public BottomDialogLikeIOS create() {
            dialog = new BottomDialogLikeIOS(context, R.style.BottomValidateDialog);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
//            requestWindowFeature(Window.FEATURE_NO_TITLE); // 取消标题
            Window dialogWindow = dialog.getWindow();
            dialogWindow.setContentView(R.layout.dialog_protocol);
            View layout = dialogWindow.getDecorView();
            dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
            dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
            dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            DisplayMetrics d = context.getResources().getDisplayMetrics();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.BOTTOM;
            dialogWindow.setAttributes(lp);
            // 设置显示动画
//            dialogWindow.setWindowAnimations(R.style.main_menu_animstyle);

            RecyclerView rvProtocol = layout.findViewById(R.id.rv_protocol);
            rvProtocol.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            BottomDialogLikeIOSAdapter adapter = new BottomDialogLikeIOSAdapter(context, list, model -> {
                if (model != null) {
                    Intent intent = new Intent();
                    intent.putExtra(EbeiHtml5WebView.INTENT_BASE_URL, model.getProtocolUrl());
                    EbeiActivityUtils.push(EbeiHtml5WebView.class, intent);
                }
            });
            rvProtocol.setAdapter(adapter);

            TextView tvCancel = layout.findViewById(R.id.menu_cancel);
            tvCancel.setOnClickListener(view -> dialog.dismiss());
            return dialog;
        }
    }
}
