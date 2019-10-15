package com.example.instagram.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.Activities.AddStoryActivity;
import com.example.instagram.Activities.StoryActivity;
import com.example.instagram.Models.Story;
import com.example.instagram.Models.User;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    private List<Story> storyList;
    private Context context;

    public StoryAdapter(List<Story> storyList, Context context) {
        this.storyList = storyList;
        this.context = context;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == 0) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.add_story_item, viewGroup, false);
            return new StoryViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.story_item, viewGroup, false);
            return new StoryViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final StoryViewHolder storyViewHolder, int i) {

        final Story story = storyList.get(i);

        userInfo(storyViewHolder, story.getUserid(), i);

        if (storyViewHolder.getAdapterPosition() == 0) {
            myStory(storyViewHolder.add_text, storyViewHolder.story_photo, false);
        }

        if (storyViewHolder.getAdapterPosition() != 0) {
            seenStory(storyViewHolder, story.getUserid());
        }

        storyViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storyViewHolder.getAdapterPosition() == 0) {
                    myStory(storyViewHolder.add_text, storyViewHolder.story_photo, true);
                } else {
                    Intent intent = new Intent(context, StoryActivity.class);
                    intent.putExtra("userid",story.getUserid());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    public class StoryViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView story_photo, story_photo_seen, add_story;
        private TextView story_username, add_text;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);

            story_photo = itemView.findViewById(R.id.story_photo);
            story_photo_seen = itemView.findViewById(R.id.story_photo_seen);
            add_story = itemView.findViewById(R.id.addstory_small);
            story_username = itemView.findViewById(R.id.story_text);
            add_text = itemView.findViewById(R.id.add_story_text);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }
        return 1;
    }

    private void userInfo(final StoryViewHolder storyViewHolder, final String userId, final int pos) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(context).load(user.getImageURL()).into(storyViewHolder.story_photo);

                if (pos != 0) {
                    Glide.with(context).load(user.getImageURL()).into(storyViewHolder.story_photo_seen);
                    storyViewHolder.story_username.setText(user.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void myStory(final TextView textView, final ImageView imageView, final boolean click) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                long currentTime = System.currentTimeMillis();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Story story = snapshot.getValue(Story.class);
                    if (currentTime > story.getTimestart() && currentTime < story.getTimeend()) {
                        count++;
                    }
                }

                if (click) {

                    if (count > 0) {
                        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "View Story", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent(context, StoryActivity.class);
                                intent.putExtra("userid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                context.startActivity(intent);
                                dialog.dismiss();
                            }
                        });

                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Add Story", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(context, AddStoryActivity.class);
                                context.startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                        alertDialog.show();
                    } else {
                        Intent intent = new Intent(context, AddStoryActivity.class);
                        context.startActivity(intent);
                    }

                } else {
                    if (count > 0) {
                        textView.setText("My story");
                        imageView.setVisibility(View.GONE);
                    } else {
                        textView.setText("Add Story");
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void seenStory(final StoryViewHolder storyViewHolder, String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (!snapshot.child("views").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists() &&
                            System.currentTimeMillis() < snapshot.getValue(Story.class).getTimeend()) {
                        i++;
                    }
                }
                if (i > 0) {
                    storyViewHolder.story_photo.setVisibility(View.VISIBLE);
                    storyViewHolder.story_photo_seen.setVisibility(View.GONE);
                } else {
                    storyViewHolder.story_photo.setVisibility(View.GONE);
                    storyViewHolder.story_photo_seen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
