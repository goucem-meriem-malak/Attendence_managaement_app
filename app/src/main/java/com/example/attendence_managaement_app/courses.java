package com.example.attendence_managaement_app;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendence_managaement_app.Interfaces.CircularProgressView;
import com.example.attendence_managaement_app.classes.attendance;
import com.example.attendence_managaement_app.classes.course;
import com.example.attendence_managaement_app.classes.group;
import com.example.attendence_managaement_app.classes.user;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class courses extends AppCompatActivity {
    private Button menu, notification, profile;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private SessionManager sessionManager;
    private user user;
    private com.example.attendence_managaement_app.classes.group group;
    private List<course> courses;
    private List<attendance> attendances;
    private LinearLayout courseContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.courses);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        sessionManager = new SessionManager(getApplicationContext());
        user = sessionManager.getUserDetails();
        courses = sessionManager.getCourses();

        menu = findViewById(R.id.menu);
        notification = findViewById(R.id.notification);
        profile = findViewById(R.id.profile);

        group = sessionManager.getGroup();
        getCourses();

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
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), notifications.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
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
    }

    private void getCourses() {
        attendances = user.getAttendances();
        courseContainer = findViewById(R.id.list_courses);

        if (courses!=null){
            for (int i = 0; i < courses.size(); i++) {

                final course currentCourse = courses.get(i);
                View courseView = LayoutInflater.from(this).inflate(R.layout.course, null);

                TextView courseNameTextView = courseView.findViewById(R.id.course_name);
                CircularProgressView courseStatCircularProgress = courseView.findViewById(R.id.course_stat);

                courseNameTextView.setText(currentCourse.getName());

                int progress = calculateProgress(currentCourse.getId());
                courseStatCircularProgress.setProgress(progress);
                courseNameTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), com.example.attendence_managaement_app.attendances.class);
                        intent.putExtra("idCourse", currentCourse.getId());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    }
                });
                courseStatCircularProgress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), com.example.attendence_managaement_app.attendances.class);
                        intent.putExtra("idCourse", currentCourse.getId());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    }
                });
                courseView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), com.example.attendence_managaement_app.attendances.class);
                        intent.putExtra("idCourse", currentCourse.getId());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    }
                });
                courseContainer.addView(courseView);
            }
        }
    }

    private int calculateProgress(String id) {
        int total = 0, present = 0;

        for (attendance attendance : attendances) {
            if (id.equals(attendance.getIdCourse())) {
                total++;
                if (attendance.getStat()) {
                    present++;
                }
            }
        }
        return (total == 0) ? 0 : ((present * 100) / total);
    }
}