package com.example.instagram.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.instagram.Fragments.PostDetailFragment;
import com.example.instagram.Models.Post;
import com.example.instagram.R;

import java.util.List;

public class photosAdapter extends RecyclerView.Adapter<photosAdapter.photosViewHolder> {

    Context context;
    List<Post> postList;

    public photosAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public photosViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.photos_item, viewGroup, false);
        return new photosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull photosViewHolder photosViewHolder, int i) {

        final Post post = postList.get(i);

        Glide.with(context).load(post.getPostImage()).into(photosViewHolder.imageView);

        photosViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postid", post.getPostID());
                editor.apply();

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, new PostDetailFragment()).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class photosViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public photosViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.grid_image);
        }
    }
}
