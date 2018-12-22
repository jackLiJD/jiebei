package com.ald.ebei.ui.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ald.ebei.R;
import com.ald.ebei.dushi.model.EbeiProtocolModel;
import com.ald.ebei.util.EbeiMiscUtils;

import java.util.List;

/**
 * Created by ywd on 2018/11/26.
 */

public class BottomDialogLikeIOSAdapter extends RecyclerView.Adapter<BottomDialogLikeIOSAdapter.MyHolder> {
    private Context context;
    private List<EbeiProtocolModel> list;
    private OnItemClickListener onItemClickListener;

    public BottomDialogLikeIOSAdapter(Context context, List<EbeiProtocolModel> list, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.list = list;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bottom_dialog_like_ios, parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        EbeiProtocolModel model = list.get(position);
        if(EbeiMiscUtils.isNotEmpty(model.getProtocolName())){
            holder.tvItem.setText(model.getProtocolName());
        }

        holder.tvItem.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView tvItem;

        public MyHolder(View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(R.id.tv_item);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(EbeiProtocolModel model);
    }
}
