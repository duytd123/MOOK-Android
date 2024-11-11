
package com.example.testapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<DataModel> dataModelList;

    public SearchAdapter(List<DataModel> dataModelList) {
        this.dataModelList = dataModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_data_model, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataModel dataModel = dataModelList.get(position);
        holder.bind(dataModel);
    }

    @Override
    public int getItemCount() {
        return dataModelList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView gifImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gifImageView = itemView.findViewById(R.id.gifImageView);
        }

        public void bind(DataModel dataModel) {
            Glide.with(itemView.getContext())
                    .asGif() // Specify that this is a GIF
                    .load(dataModel.getImageUrl())
                    .into(gifImageView);
        }
    }
}
