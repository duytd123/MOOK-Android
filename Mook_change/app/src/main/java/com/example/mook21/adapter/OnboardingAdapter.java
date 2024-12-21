package com.example.mook21.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mook21.R;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private final Context context;

    public OnboardingAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_onboarding, parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        // Tùy chỉnh nội dung cho từng màn hình Onboarding
    }

    @Override
    public int getItemCount() {
        return 3; // Số lượng màn hình Onboarding
    }

    public static class OnboardingViewHolder extends RecyclerView.ViewHolder {
        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
