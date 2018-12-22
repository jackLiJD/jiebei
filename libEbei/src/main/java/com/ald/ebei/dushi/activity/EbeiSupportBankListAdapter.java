package com.ald.ebei.dushi.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ald.ebei.R;
import com.ald.ebei.dushi.model.EbeiSupportBankModel;
import com.ald.ebei.util.EbeiMiscUtils;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * 支持的银行卡列表Adapter
 * Created by ywd on 2018/11/19.
 */

public class EbeiSupportBankListAdapter extends RecyclerView.Adapter<EbeiSupportBankListAdapter.MyHolder> {
    private Context context;
    private List<EbeiSupportBankModel> bankCardModels;
    private EbeiSupportBankListAdapter.OnItemClickListener onItemClickListener;

    public EbeiSupportBankListAdapter(Context context, List<EbeiSupportBankModel> bankCardModels, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.bankCardModels = bankCardModels;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_support_bank_list, viewGroup, false);
        return new EbeiSupportBankListAdapter.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, final int i) {
        final EbeiSupportBankModel itemData = bankCardModels.get(i);
        if (itemData != null) {
            String url = itemData.getBankIcon();
            String bankName = itemData.getBankName();

            if (EbeiMiscUtils.isNotEmpty(url)) {
                Glide.with(context).load(url).into(myHolder.ivIcon);
            }
            if (EbeiMiscUtils.isNotEmpty(bankName)) {
                myHolder.tvName.setText(bankName);
            }
        }
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(itemData, i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return bankCardModels.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        private ImageView ivIcon;
        private TextView tvName;

        public MyHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.image);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }

    interface OnItemClickListener {
        void onItemClick(EbeiSupportBankModel itemData, int position);
    }
}
