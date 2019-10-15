package com.example.instagram.Adapters;

import android.content.Context;
import android.content.Intent;
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
import com.bumptech.glide.request.RequestOptions;
import com.example.instagram.Activities.CommentsActivity;
import com.example.instagram.Activities.FollowersActivity;
import com.example.instagram.Fragments.PostDetailFragment;
import com.example.instagram.Fragments.ProfileFragment;
import com.example.instagram.Models.Post;
import com.example.instagram.Models.User;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private List<Post> mPost;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context context, List<Post> mPost) {
        this.context = context;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_screen_item_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final Post post = mPost.get(i);

        Glide.with(context).load(post.getPostImage()).apply(new RequestOptions().placeholder(R.drawable.image_placeholder)).into(viewHolder.posted_image);


        if (post.getDescription().equals("")) {
            viewHolder.description.setVisibility(View.GONE);
        } else {
            viewHolder.description.setVisibility(View.VISIBLE);
            viewHolder.description.setText(post.getDescription());
        }

        publisherInfo(viewHolder.profile_image, viewHolder.username, viewHolder.publisher, post.getPublisher());
        isLiked(post.getPostID(), viewHolder.like_image);
        nrOfLikes(post.getPostID(), viewHolder.no_of_likes);
        getComments(post.getPostID(), viewHolder.comments);
        getMySaves(post.getPostID(), viewHolder.save_image);

        viewHolder.profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, new ProfileFragment()).commit();
            }
        });

        viewHolder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, new ProfileFragment()).commit();
            }
        });

        viewHolder.publisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, new ProfileFragment()).commit();
            }
        });

        viewHolder.posted_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postid", post.getPostID());
                editor.apply();

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, new PostDetailFragment()).commit();
            }
        });


        viewHolder.save_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.save_image.getTag().equals("save")) {
                    FirebaseDatabase.getInstance().getReference().child("saves").child(firebaseUser.getUid()).child(post.getPostID()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("saves").child(firebaseUser.getUid()).child(post.getPostID()).removeValue();

                }
            }
        });

        viewHolder.like_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.like_image.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostID()).child(firebaseUser.getUid()).setValue(true);
                    addNotification(post.getPublisher(), post.getPostID());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostID()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        viewHolder.comment_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentsIntent = new Intent(context, CommentsActivity.class);
                commentsIntent.putExtra("postID", post.getPostID());
                commentsIntent.putExtra("publisher", post.getPublisher());
                context.startActivity(commentsIntent);
            }
        });

        viewHolder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentsIntent = new Intent(context, CommentsActivity.class);
                commentsIntent.putExtra("postID", post.getPostID());
                commentsIntent.putExtra("publisher", post.getPublisher());
                context.startActivity(commentsIntent);
            }
        });

        viewHolder.no_of_likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FollowersActivity.class);
                intent.putExtra("id", post.getPublisher());
                intent.putExtra("title", "likes");
                context.startActivity(intent);
            }
        });
    }

    private void getComments(String postID, final TextView seeComments) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                seeComments.setText("View All " + dataSnapshot.getChildrenCount() + " Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addNotification(String userID, String postID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userID);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("textnotification", " liked your post");
        hashMap.put("postID", postID);
        hashMap.put("isPost", true);

        reference.push().setValue(hashMap);
    }


    private void isLiked(String postid, final ImageView imageView) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void nrOfLikes(String postID, final TextView likes) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount() + " like");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public void publisherInfo(final ImageView profile, final TextView username, final TextView publisher, String userID) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                Glide.with(context).load(user.getImageURL()).into(profile);
                username.setText(user.getUsername());
                publisher.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView profile_image, posted_image, like_image, comment_image, save_image;
        private TextView username, no_of_likes, publisher, description, comments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_image = itemView.findViewById(R.id.circular_image);
            posted_image = itemView.findViewById(R.id.home_item_image);
            like_image = itemView.findViewById(R.id.like);
            comment_image = itemView.findViewById(R.id.comment);
            save_image = itemView.findViewById(R.id.save);
            username = itemView.findViewById(R.id.username);
            no_of_likes = itemView.findViewById(R.id.likes);
            publisher = itemView.findViewById(R.id.publisher);
            description = itemView.findViewById(R.id.description);
            comments = itemView.findViewById(R.id.viewAllComments);

        }
    }


    private void getMySaves(final String postID, final ImageView save_image) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("saves").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postID).exists()) {
                    save_image.setImageResource(R.drawable.ic_saved);
                    save_image.setTag("saved");
                } else {
                    save_image.setImageResource(R.drawable.ic_save);
                    save_image.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
