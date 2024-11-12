package com.example.testapp;

import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private Context context;
    private ArrayList<DataModel> modelList;
    private OnItemClickListener listener;
    private OnItemLongClickListener onItemLongClickListener;

    public DataAdapter(Context context, ArrayList<DataModel> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataModel model = modelList.get(position);
        holder.bind(model, listener, onItemLongClickListener);

        Glide.with(context)
                .asGif()
                .load(model.getImageUrl())
                .override(model.getHeight())
                .into(holder.gifImageView);

        ViewGroup.LayoutParams params = holder.gifImageView.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        holder.gifImageView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView gifImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            gifImageView = itemView.findViewById(R.id.gifImageView);
        }

        public void bind(DataModel model, OnItemClickListener listener, OnItemLongClickListener longClickListener) {
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onItemLongClick(getAdapterPosition());
                    return true;
                }
                return false;
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }


}
