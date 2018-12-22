package com.ald.ebei.ui.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ald.ebei.R;
import com.ald.ebei.dushi.model.EbeiHomeModel;
import com.ald.ebei.util.EbeiAppUtils;

import java.util.List;

/**
 * 月供/还款明细信息适配器
 * Created by ywd on 2018/11/21.
 */

public class EbeiRepaymentRemitAdapter extends RecyclerView.Adapter<EbeiRepaymentRemitAdapter.MyHolder> {
    private Context context;
    private List<EbeiHomeModel.RemitListModel> remitListModels;

    private String[] strs = {"二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"};

    public EbeiRepaymentRemitAdapter(Context context, List<EbeiHomeModel.RemitListModel> remitListModels) {
        this.context = context;
        this.remitListModels = remitListModels;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_repayment_remit, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        EbeiHomeModel.RemitListModel data = remitListModels.get(i);
        if (data != null) {
            String nper = "一";
            if (2 == data.getNper()) {
                nper = "二";
            }
            if (3 == data.getNper()) {
                nper = "三";
            }
            if (4 == data.getNper()) {
                nper = "四";
            }
            if (5 == data.getNper()) {
                nper = "五";
            }
            if (6 == data.getNper()) {
                nper = "六";
            }
            if (7 == data.getNper()) {
                nper = "七";
            }
            if (8 == data.getNper()) {
                nper = "八";
            }
            if (9 == data.getNper()) {
                nper = "九";
            }
            if (10 == data.getNper()) {
                nper = "十";
            }
            if (11 == data.getNper()) {
                nper = "十一";
            }
            if (12 == data.getNper()) {
                nper = "十二";
            }
            myHolder.tvNper.setText("第" + nper + "期利息");
            myHolder.tvAmount.setText("-" + EbeiAppUtils.formatAmount(data.getRemitFee()));
        }
    }

    @Override
    public int getItemCount() {
        return remitListModels == null || remitListModels.isEmpty() ? 0 : remitListModels.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView tvNper;
        TextView tvAmount;

        public MyHolder(View itemView) {
            super(itemView);
            tvNper = itemView.findViewById(R.id.tv_nper);
            tvAmount = itemView.findViewById(R.id.tv_amount);
        }
    }
}
