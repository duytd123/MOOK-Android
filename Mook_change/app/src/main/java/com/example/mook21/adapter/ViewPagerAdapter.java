package com.example.mook21.adapter;
import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mook21.R;
import com.example.mook21.adapter.SoundAdapter;
import com.example.mook21.databinding.ItemViewPagerBinding;
import com.example.mook21.model.Sound;

import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.PageViewHolder> {

    private final Context context;
    private final List<List<Sound>> paginatedSoundList;

    public ViewPagerAdapter(Context context, List<List<Sound>> paginatedSoundList) {
        this.context = context;
        this.paginatedSoundList = paginatedSoundList;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view_pager, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        List<Sound> sounds = paginatedSoundList.get(position);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL);
        holder.binding.recyclerView.setLayoutManager(layoutManager);
        holder.binding.recyclerView.setAdapter(new SoundAdapter(context, sounds));
    }

    @Override
    public int getItemCount() {
        return paginatedSoundList.size();
    }

    public static class PageViewHolder extends RecyclerView.ViewHolder {
        private final ItemViewPagerBinding binding;

        public PageViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemViewPagerBinding.bind(itemView);
        }
    }
}

