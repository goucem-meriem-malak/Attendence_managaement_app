package com.example.attendence_managaement_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendence_managaement_app.classes.attendance;
import com.example.attendence_managaement_app.classes.notification;
import com.example.attendence_managaement_app.classes.user;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;

public class notifications extends AppCompatActivity {
    private Button menu, notification, profile;
    private SessionManager sessionManager;
    private user user;
    private List<notification> notifications;
    private LinearLayout notificationContainer;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GestureDetector gestureDetector;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications);
        
        gestureDetector = new GestureDetector(this, new SwipeGestureListener());

        menu = findViewById(R.id.menu);
        profile = findViewById(R.id.profile);
        swipeRefreshLayout = findViewById(R.id.refresh);
        notification = findViewById(R.id.notifications);

        sessionManager = new SessionManager(getApplicationContext());
        user = sessionManager.getUserDetails();
        db = FirebaseFirestore.getInstance();

        notifications = user.getNotifications();
        getNotifications(notifications);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), courses.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), profile.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshActivity();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void refreshActivity() {
        db.collection("notification").whereEqualTo("idStudent", user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        notifications.clear();
                        notificationContainer.removeAllViews();
                        for (DocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String title = document.getString("title");
                            String subject = document.getString("subject");
                            String idCourse = document.getString("idCourse");
                            Timestamp time = document.getTimestamp("time");
                            String idJustification = document.getString("idJustification");

                            notification notificationObj;
                            if (idJustification!=null){
                                notificationObj = new notification(id, title, subject, user.getUid(), idJustification, idCourse, time);
                            } else {
                                notificationObj = new notification(id, title, subject, user.getUid(), null, idCourse, time);
                            }
                            notifications.add(notificationObj);
                        }
                        user.setNotifications(notifications);
                        sessionManager.saveNotifications(notifications);
                        getNotifications(notifications);
                    }
                });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();

            if (Math.abs(distanceX) > Math.abs(distanceY)
                    && Math.abs(distanceX) > SWIPE_THRESHOLD
                    && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                // Right-to-left swipe detected
                // Start the new activity here
                Toast.makeText(getApplicationContext(), "Swiped left", Toast.LENGTH_SHORT).show();
                // Start your new activity
                Intent intent = new Intent(getApplicationContext(), profile.class);
                startActivity(intent);
                return true;
            }

            return false;
        }
    }

    private void getNotifications(List<notification> notifications) {
        notificationContainer = findViewById(R.id.list_notifications);

        for (int i = 0; i < notifications.size(); i++) {
            notification currentNotification = notifications.get(i);

            View notificationView = LayoutInflater.from(this).inflate(R.layout.notification, null);
            notificationView.setClickable(true);

            Timestamp timestamp = currentNotification.getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

            TextView timeTextView = notificationView.findViewById(R.id.notification_time);
            TextView dateTextView = notificationView.findViewById(R.id.notification_date);
            TextView textTextView = notificationView.findViewById(R.id.notification_text);
            ImageView imgImageView = notificationView.findViewById(R.id.notification_img);

            timeTextView.setText(timeFormat.format(timestamp.toDate()));
            dateTextView.setText(dateFormat.format(timestamp.toDate()));
            textTextView.setText(currentNotification.getSubject());

            if (currentNotification.getIdJustification()!=null){
                imgImageView.setImageResource(R.drawable.accepted);
                imgImageView.setImageDrawable(getTintedDrawable(R.drawable.accepted, R.color.green));
            } else {
                imgImageView.setImageResource(R.drawable.warning);
                imgImageView.setImageDrawable(getTintedDrawable(R.drawable.warning, R.color.yellow));
            }
            imgImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), attendances.class);
                    intent.putExtra("idCourse", currentNotification.getIdCourse());
                    startActivity(intent);
                }
            });
            textTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), attendances.class);
                    intent.putExtra("idCourse", currentNotification.getIdCourse());
                    startActivity(intent);
                }
            });
            notificationView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), attendances.class);
                    intent.putExtra("idCourse", currentNotification.getIdCourse());
                    startActivity(intent);
                }
            });

            notificationContainer.addView(notificationView);
        }
    }

    private Drawable getTintedDrawable(int drawableResId, int colorResId) {
        Drawable drawable = ContextCompat.getDrawable(this, drawableResId);
        int color = ContextCompat.getColor(this, colorResId);
        drawable = DrawableCompat.wrap(drawable.mutate());
        DrawableCompat.setTint(drawable, color);
        return drawable;
    }
}