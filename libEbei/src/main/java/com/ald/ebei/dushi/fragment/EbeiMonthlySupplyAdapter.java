package com.ald.ebei.dushi.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ald.ebei.R;
import com.ald.ebei.dushi.model.EbeiAnalyzePeriodsModelData;
import com.ald.ebei.util.EbeiAppUtils;

import java.util.List;

/**
 * 月供/还款明细信息适配器
 * Created by ywd on 2018/11/21.
 */

public class EbeiMonthlySupplyAdapter extends RecyclerView.Adapter<EbeiMonthlySupplyAdapter.MyHolder> {
    private Context context;
    private List<EbeiAnalyzePeriodsModelData> monthlySupplyModels;

    private String[] strs = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"};

    public EbeiMonthlySupplyAdapter(Context context, List<EbeiAnalyzePeriodsModelData> monthlySupplyModels) {
        this.context = context;
        this.monthlySupplyModels = monthlySupplyModels;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_monthly_supply, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        EbeiAnalyzePeriodsModelData data = monthlySupplyModels.get(i);
        if (data != null) {
            myHolder.tvPeriod.setText("第" + strs[i] + "期：" + EbeiAppUtils.formatAmount(data.getAmount()) + "元");
            myHolder.tvInterest.setText("含利息" + EbeiAppUtils.formatAmount(data.getInterestFee()) + "元 服务费"
                    + EbeiAppUtils.formatAmount(data.getServiceFee()));
            myHolder.tvLastRepaymentDate.setText("最后还款日" + data.getRepayDay().split(" ")[0]);
        }
    }

    @Override
    public int getItemCount() {
        return monthlySupplyModels == null || monthlySupplyModels.isEmpty() ? 0 : monthlySupplyModels.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView tvPeriod;
        TextView tvInterest;
        TextView tvLastRepaymentDate;

        public MyHolder(View itemView) {
            super(itemView);
            tvPeriod = itemView.findViewById(R.id.tv_period);
            tvInterest = itemView.findViewById(R.id.tv_interest);
            tvLastRepaymentDate = itemView.findViewById(R.id.tv_repayment_date_last);
        }
    }
}
