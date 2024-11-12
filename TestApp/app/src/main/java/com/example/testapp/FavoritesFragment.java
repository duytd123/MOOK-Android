package com.example.testapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.widget.Toast;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


public class FavoritesFragment extends Fragment implements DataAdapter.OnItemClickListener, DataAdapter.OnItemLongClickListener{

    private RecyclerView favoritesRecyclerView;
    private DataAdapter favoritesAdapter;
    private ArrayList<DataModel> favoritesModelList = new ArrayList<>();
    private TextView emptyText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        favoritesRecyclerView = view.findViewById(R.id.favorites_recycler_view);
        emptyText = view.findViewById(R.id.favorites_empty_text);

        // Load favorites data (you should implement this method)
        loadFavoritesData();
        initializeRecyclerView();
        favoritesAdapter.setOnItemClickListener(this::onItemClick);
        favoritesAdapter.setOnItemLongClickListener(this::onItemLongClick);
        return view;
    }

    private void initializeRecyclerView() {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        favoritesRecyclerView.setLayoutManager(layoutManager);
        favoritesRecyclerView.addItemDecoration(new SpaceItem(4));
        favoritesRecyclerView.setHasFixedSize(true);

        favoritesAdapter = new DataAdapter(getContext(), favoritesModelList);
        favoritesRecyclerView.setAdapter(favoritesAdapter);

//        favoritesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (!recyclerView.canScrollVertically(1)) {
//                    loadMoreGifs();
//                }
//            }
//        });
    }

    public void loadFavoritesData() {
        new Thread(() ->{
            AppDatabase db = AppDatabase.getInstance(getContext());
            List<FavouriteGif> listFavouriteGifList = db.favouriteGifDAO().getAll();
            favoritesModelList.clear();
            for (FavouriteGif gif : listFavouriteGifList){
                System.out.println(gif);
                favoritesModelList.add(new DataModel(gif.getImageUrl(), gif.getHeight(), gif.getTitle()));
            }
            getActivity().runOnUiThread(() -> {
                favoritesAdapter.notifyDataSetChanged();
                checkEmptyList();
            });
        }).start();
    }

    private void checkEmptyList() {
        if (favoritesModelList.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            favoritesRecyclerView.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.GONE);
            favoritesRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        loadFavoritesData();
    }

    @Override
    public void onItemClick(int pos) {
        Intent fullView = new Intent(getContext(), FullActivity.class);
        DataModel clickedItem = favoritesModelList.get(pos);
        fullView.putExtra("imageFull", clickedItem);
        startActivity(fullView);
    }

    @Override
    public void onItemLongClick(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure you want to delete this GIF image?");

        // Nút "Delete"
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteGif(pos);
            }
        });

        // Nút "Cancel"
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Tạo và hiển thị dialog
        AlertDialog dialog = builder.create();
        dialog.show(); // Hiển thị dialog khi long click
    }

    private void deleteGif(int pos) {
        DataModel gifToDelete = favoritesModelList.get(pos);

        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getContext());

            // Tìm ảnh GIF cần xóa bằng URL ảnh hoặc ID, sau đó xóa
            FavouriteGif gif = db.favouriteGifDAO().getFavouriteGifByUrl(gifToDelete.getImageUrl());
            if (gif != null) {
                db.favouriteGifDAO().deleteFavouriteGif(gif);
            }

            // Cập nhật danh sách và giao diện trên luồng UI
            getActivity().runOnUiThread(() -> {
                favoritesModelList.remove(pos);
                favoritesAdapter.notifyItemRemoved(pos);
                checkEmptyList();
                Toast.makeText(getContext(), "Deleted successfully", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }
}
