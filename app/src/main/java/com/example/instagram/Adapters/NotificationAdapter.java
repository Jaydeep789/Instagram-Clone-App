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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.Fragments.PostDetailFragment;
import com.example.instagram.Fragments.ProfileFragment;
import com.example.instagram.Models.Notification;
import com.example.instagram.Models.Post;
import com.example.instagram.Models.User;
import com.example.instagram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewholder> {

    private Context context;
    private List<Notification> notificationList;

    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notification_item, viewGroup, false);

        return new NotificationViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewholder notificationViewholder, int i) {
        final Notification notification = notificationList.get(i);

        notificationViewholder.notificationText.setText(notification.getTextnotification());

        getUserInfo(notificationViewholder.profile_image, notificationViewholder.username, notification.getUserid());

        if (notification.getPost()) {
            notificationViewholder.post_image.setVisibility(View.VISIBLE);
            getPostImageInfo(notificationViewholder.post_image, notification.getPostID());
        } else {
            notificationViewholder.post_image.setVisibility(View.GONE);
        }

        notificationViewholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notification.getPost()) {
                    SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("postid", notification.getPostID());
                    editor.apply();

                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, new PostDetailFragment()).commit();
                } else {
                    SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileid", notification.getUserid());
                    editor.apply();

                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, new ProfileFragment()).commit();

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class NotificationViewholder extends RecyclerView.ViewHolder {

        private ImageView profile_image, post_image;
        private TextView username, notificationText;

        public NotificationViewholder(@NonNull View itemView) {
            super(itemView);

            profile_image = itemView.findViewById(R.id.circular_image);
            post_image = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.username);
            notificationText = itemView.findViewById(R.id.comment);
        }
    }

    private void getUserInfo(final ImageView profile_pic, final TextView name, String publisherID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(publisherID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name.setText(user.getUsername());
                Glide.with(context).load(user.getImageURL()).into(profile_pic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPostImageInfo(final ImageView post_pic, final String postID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts").child(postID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                Glide.with(context).load(post.getPostImage()).into(post_pic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
