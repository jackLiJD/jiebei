package com.ald.ebei.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ald.ebei.R;
import com.ald.ebei.util.EbeiMiscUtils;


/*
 * Created by liangchen on 2018/3/12.
 */

public class EbeiPermissionRequestDialog extends Dialog {

    EbeiPermissionRequestDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }


    public static class Builder {

        Context context;
        String message;
        View.OnClickListener listener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTxtId(int txtId) {
            this.message = context.getResources().getString(txtId);
            return this;
        }

        public Builder setListener(View.OnClickListener listener) {
            this.listener = listener;
            return this;
        }


        public EbeiPermissionRequestDialog creater() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final EbeiPermissionRequestDialog dialog = new EbeiPermissionRequestDialog(context, R.style.CustomDialog);
            dialog.setCancelable(true);
            View layout = inflater.inflate(R.layout.ebei_dialog_permission_request, null);
            TextView btnRequest = (TextView) layout.findViewById(R.id.btn_request);
            TextView tvMessage = (TextView) layout.findViewById(R.id.txt_message);
            if (!EbeiMiscUtils.isEmpty(message)) {
                tvMessage.setText(message);
            }
            btnRequest.setOnClickListener(listener);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            return dialog;
        }
    }
}
