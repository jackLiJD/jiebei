package com.ald.ebei.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ald.ebei.R;


/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/1/3
 * 描述：dialog消息提示界面
 * 修订历史：
 */
public class EbeiCustomDialog extends Dialog {
    public EbeiCustomDialog(Context context) {
        super(context);
    }

    public EbeiCustomDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private int titleTextColor = Integer.MAX_VALUE;
        private int messageTextColor = Integer.MAX_VALUE;
        private int buttonTextColor = Integer.MAX_VALUE;
        private int negativeBtnTextColor = Integer.MAX_VALUE;
        private boolean isPositiveDismiss = true;
        private boolean canceledOnTouchOutside = false;
        private boolean cancelable = true;
        private OnClickListener positiveButtonClickListener, negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Set the Dialog title from resource
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * Set the Dialog title color
         */
        public Builder setTitleTextColor(int titleTextColor) {
            this.titleTextColor = titleTextColor;
            return this;
        }

        /**
         * Set the Dialog message from resource
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog message from String
         */
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message color
         */
        public Builder setMessageTextColor(int messageTextColor) {
            this.messageTextColor = messageTextColor;
            return this;
        }

        /**
         * Set the Dialog button color
         */
        public Builder setButtonTextColor(int buttonTextColor) {
            this.buttonTextColor = buttonTextColor;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         */
        public Builder setPositiveButton(int positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = (String) context.getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the positive button text and it's listener
         */
        public Builder setPositiveButton(String positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button resource and it's listener
         */
        public Builder setNegativeButton(int negativeButtonText, OnClickListener listener) {
            this.negativeButtonText = (String) context.getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButtonTextColor(int resId){
            this.negativeBtnTextColor=resId;
            return this;
        }

        /**
         * Set the negative button text and it's listener
         */
        public Builder setNegativeButton(String negativeButtonText, OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * Set a custom content view for the Dialog.
         * If a message is set, the contentView is not
         * added to the Dialog...
         */
        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the dialog is dismiss when positive button is click
         */
        public Builder setIsPositiveDismiss(boolean b) {
            this.isPositiveDismiss = b;
            return this;
        }

        /**
         * Sets whether this dialog is canceled when touched outside the window's
         * bounds. If setting to true, the dialog is set to be cancelable if not
         * already set.
         */
        public Builder setCanceledOnTouchOutside(boolean b) {
            this.canceledOnTouchOutside = b;
            return this;
        }

        /**
         * Sets whether this dialog is cancelable with the key
         */
        public Builder setCancelable(boolean b) {
            this.cancelable = b;
            return this;
        }

        /**
         * Create the custom dialog
         */
        public EbeiCustomDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final EbeiCustomDialog dialog = new EbeiCustomDialog(context, R.style.CustomDialog);
            dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
            dialog.setCancelable(cancelable);
            View layout = inflater.inflate(R.layout.ebei__dialog_custom, null);
            dialog.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            // set the dialog title
            if (title != null) {
                ((TextView) layout.findViewById(R.id.msg_title)).setText(title);
            }
            if (titleTextColor != Integer.MAX_VALUE) {
                ((TextView) layout.findViewById(R.id.msg_title)).setTextColor(titleTextColor);
            }

            // set the content message
            if (message != null) {
                ((TextView) layout.findViewById(R.id.msg_message)).setText(message);
            } else if (contentView != null) {
                // if no message set
                // add the contentView to the dialog body
                ((LinearLayout) layout.findViewById(R.id.msg_content)).removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.msg_content)).addView(contentView, new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
            }
            if (messageTextColor != Integer.MAX_VALUE) {
                ((TextView) layout.findViewById(R.id.msg_message)).setTextColor(messageTextColor);
            }

            // set the confirm button
            if (positiveButtonText != null) {
                ((TextView) layout.findViewById(R.id.msg_positive)).setText(positiveButtonText);
                (layout.findViewById(R.id.msg_positive)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (isFastTrigger(1000)) {
                            dialog.dismiss();
                            return;
                        }
                        if (positiveButtonClickListener != null) {
                            positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                        }
                        if (isPositiveDismiss) {
                            dialog.dismiss();
                        }
                    }
                });
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.msg_positive).setVisibility(View.GONE);
            }

            if (negativeButtonText != null) {
                ((TextView) layout.findViewById(R.id.msg_negative)).setText(negativeButtonText);
                (layout.findViewById(R.id.msg_negative)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (negativeButtonClickListener != null) {
                            negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                        }
                        dialog.dismiss();
                    }
                });
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.msg_negative).setVisibility(View.GONE);
                layout.findViewById(R.id.msg_line).setVisibility(View.GONE);
            }
            if (buttonTextColor != Integer.MAX_VALUE) {
                ((TextView) layout.findViewById(R.id.msg_positive)).setTextColor(buttonTextColor);
                ((TextView) layout.findViewById(R.id.msg_negative)).setTextColor(buttonTextColor);
            }

            if(negativeBtnTextColor!= Integer.MAX_VALUE){
                ((TextView) layout.findViewById(R.id.msg_negative)).setTextColor(negativeBtnTextColor);
            }

            dialog.setContentView(layout);
            return dialog;
        }
    }

    /**
     * 点击时间
     */
    private static long lastTriggerTime;

    /**
     * @param interval 时间间隔
     *                 <p/>
     * @return 是否在interval毫秒内连续触发
     * true - 是快速点击
     * false - 不是快速点击
     */
    public static boolean isFastTrigger(long interval) {
        long time = System.currentTimeMillis();
        long timeD = time - lastTriggerTime;
        if (0 < timeD && timeD < interval) {
            return true;
        }
        lastTriggerTime = time;
        return false;
    }
}
