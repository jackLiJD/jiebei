package com.ald.ebei.dushi.fragment;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ald.ebei.R;
import com.ald.ebei.dushi.model.EbeiHomeTermModel;
import com.ald.ebei.ui.EbeiChoiceItemLayout;
import com.ald.ebei.util.EbeiMiscUtils;

import java.util.List;

/**
 * 单选借款期限Adapter
 * Created by ywd on 2018/11/18.
 */

public class EbeiSingleChoiceTermAdapter extends RecyclerView.Adapter<EbeiSingleChoiceTermAdapter.MyHolder> {

    private static final String TAG = "EbeiSingleChoiceTermAdapter";

    private Context context;
    private List<EbeiHomeTermModel> dataList;
    private OnItemClickListener onItemClickListener;

    private int currentCheckPosition = 0;

    public int getCurrentCheckPosition() {
        return currentCheckPosition;
    }

    public void setDataList(List<EbeiHomeTermModel> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public EbeiSingleChoiceTermAdapter(Context context, List<EbeiHomeTermModel> dataList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.dataList = dataList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_term, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, final int i) {
        final String itemData = dataList.get(i).getTerm();
        Log.d(TAG, itemData);
        if (!EbeiMiscUtils.isEmpty(itemData)) {
            myHolder.textView.setText(itemData);
        }
        if(dataList.get(i).isChecked()){
            myHolder.textView.setTextColor(context.getResources().getColor(R.color.white));
        }else {
            myHolder.textView.setTextColor(Color.parseColor("#e5e5e5"));
        }
        myHolder.ebeiChoiceItemLayout.setChecked(dataList.get(i).isChecked());
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    currentCheckPosition = i;
                    onItemClickListener.onItemClick(itemData, i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView textView;
        EbeiChoiceItemLayout ebeiChoiceItemLayout;

        public MyHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_term);
            ebeiChoiceItemLayout = itemView.findViewById(R.id.cil_main);
        }
    }

    interface OnItemClickListener {
        void onItemClick(String itemData, int position);
    }
}
