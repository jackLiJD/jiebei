package com.ald.ebei.ui;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.ald.ebei.R;
import com.ald.ebei.dushi.fragment.EbeiMonthlySupplyAdapter;
import com.ald.ebei.dushi.model.EbeiAnalyzePeriodsModelData;
import com.ald.ebei.util.EbeiDataUtils;
import com.ald.ebei.util.EbeiDensityUtils;

import java.util.List;

import static com.ald.ebei.util.EbeiConstant.DIALOG_MAX_DP_HEIGHT;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/3/9 23:26
 * 描述：tips提示dialog
 * 修订历史：
 */
public class EbeiMonthlySupplyDialog extends Dialog {

    public EbeiMonthlySupplyDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private Context context;
        private List<EbeiAnalyzePeriodsModelData> monthlySupplyModels;
        private EbeiMonthlySupplyDialog dialog;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setData(List<EbeiAnalyzePeriodsModelData> monthlySupplyModels) {
            this.monthlySupplyModels = monthlySupplyModels;
            return this;
        }

        public EbeiMonthlySupplyDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.dialog_monthly_supply, null);
            dialog = new EbeiMonthlySupplyDialog(context, R.style.tipsDialog);
            dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    EbeiDensityUtils.getPxByDip(DIALOG_MAX_DP_HEIGHT)));
            dialog.setCanceledOnTouchOutside(false);
            Window window = dialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.CENTER;
            lp.width = EbeiDataUtils.getCurrentDisplayMetrics().widthPixels - 100;
            lp.dimAmount = 0.5f;
            window.setAttributes(lp);

            ImageView ivClose = view.findViewById(R.id.iv_close);
            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            RecyclerView rvList = view.findViewById(R.id.iv_monthly_supply);
            rvList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            rvList.setAdapter(new EbeiMonthlySupplyAdapter(context, monthlySupplyModels));
            return dialog;
        }
    }

}
