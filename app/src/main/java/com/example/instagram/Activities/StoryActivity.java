package com.example.instagram.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.Models.Story;
import com.example.instagram.Models.User;
import com.example.instagram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    private int counter = 0;
    private long pressTime = 0L;
    private long limit = 500L;

    private StoriesProgressView storiesProgressView;
    private ImageView story_image;
    private CircleImageView user_image;
    private TextView username;

    List<String> images;
    List<String> storyIds;

    String userId;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;

                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }

            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        storiesProgressView = findViewById(R.id.stories_progressview);
        story_image = findViewById(R.id.full_image);
        user_image = findViewById(R.id.user_image);
        username = findViewById(R.id.storiesview_username);

        userId = getIntent().getStringExtra("userid");

        getStories(userId);
        userInfo(userId);
    }

    private void getStories(String userID) {

        images = new ArrayList<>();
        storyIds = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story").child(userID);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                images.clear();
                storyIds.clear();
                long currentTime = System.currentTimeMillis();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Story story = snapshot.getValue(Story.class);
                    assert story != null;
                    if (currentTime > story.getTimestart() && currentTime < story.getTimeend()) {
                        images.add(story.getImageurl());
                        images.add(story.getStoryid());
                    }
                }
                storiesProgressView.setStoriesCount(images.size());
                storiesProgressView.setStoryDuration(5000L);
                storiesProgressView.setStoriesListener(StoryActivity.this);
                storiesProgressView.startStories(counter);

                Glide.with(getApplicationContext()).load(images.get(counter)).into(story_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onNext() {
        Glide.with(getApplicationContext()).load(images.get(++counter)).into(story_image);

    }

    @Override
    public void onPrev() {
        if (counter - 1 < 0) return;
        Glide.with(getApplicationContext()).load(images.get(--counter)).into(story_image);

    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        storiesProgressView.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        storiesProgressView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        storiesProgressView.pause();
    }

    private void userInfo(String userID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageURL()).into(user_image);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
