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
import com.ald.ebei.dushi.model.EbeiBankCardModel;
import com.ald.ebei.util.EbeiMiscUtils;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * 银行卡列表Adapter
 * Created by ywd on 2018/11/19.
 */

public class EbeiBankCardListAdapter extends RecyclerView.Adapter<EbeiBankCardListAdapter.MyHolder> {
    private Context context;
    private List<EbeiBankCardModel> ebeiBankCardModels;
    private EbeiBankCardListAdapter.OnItemClickListener onItemClickListener;

    public EbeiBankCardListAdapter(Context context, List<EbeiBankCardModel> ebeiBankCardModels, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.ebeiBankCardModels = ebeiBankCardModels;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_bank_card_list, viewGroup, false);
        return new EbeiBankCardListAdapter.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, final int i) {
        final EbeiBankCardModel itemData = ebeiBankCardModels.get(i);
        if (itemData != null) {
            String url = itemData.getBankIcon();
            String bankName = itemData.getBankName();
            String isMain = itemData.getIsMain();
            String bankCardNum = itemData.getCardNumber();

            if (EbeiMiscUtils.isNotEmpty(url)) {
                Glide.with(context).load(url).into(myHolder.ivIcon);
            }
            if (EbeiMiscUtils.isNotEmpty(bankName)) {
                myHolder.tvName.setText(bankName);
            }
            if ("Y".equals(isMain)) {
                myHolder.tvIsMain.setVisibility(View.VISIBLE);
            } else {
                myHolder.tvIsMain.setVisibility(View.GONE);
            }
            if (EbeiMiscUtils.isNotEmpty(bankCardNum)) {
                myHolder.tvCardNum.setText(context.getString(R.string.bank_card_no_list, bankCardNum.substring(bankCardNum.length() - 4)));
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
        return ebeiBankCardModels.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        private ImageView ivIcon;
        private TextView tvName;
        private TextView tvIsMain;
        private TextView tvCardNum;

        public MyHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.image);
            tvName = itemView.findViewById(R.id.tv_name);
            tvIsMain = itemView.findViewById(R.id.tv_is_main);
            tvCardNum = itemView.findViewById(R.id.tv_card_num);
        }
    }

    interface OnItemClickListener {
        void onItemClick(EbeiBankCardModel itemData, int position);
    }
}
