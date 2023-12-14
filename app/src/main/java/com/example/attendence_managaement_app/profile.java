package com.example.attendence_managaement_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendence_managaement_app.classes.user;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;

public class profile extends AppCompatActivity {
    private Button menu, notification, profile;
    private TextView name, department, faculty, major, degree, speciality, group;
    private ImageView pic;
    private Button logout;
    private SessionManager sessionManager;
    private user user;
    private FirebaseAuth mAuth;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        mAuth = FirebaseAuth.getInstance();

        sessionManager = new SessionManager(getApplicationContext());
        user = sessionManager.getUserDetails();

        menu = findViewById(R.id.menu);
        notification = findViewById(R.id.notification);
        profile = findViewById(R.id.profile);

        pic = findViewById(R.id.pic);

        name = findViewById(R.id.name);
        department = findViewById(R.id.department);
        faculty = findViewById(R.id.faculty);
        major = findViewById(R.id.major);
        degree = findViewById(R.id.degree);
        speciality = findViewById(R.id.speciality);
        group = findViewById(R.id.group);

        logout = findViewById(R.id.logout);

        name.setText(user.getFirst_name()+ " " + user.getLast_name());
        department.setText(user.getDepartment() + " Department");
        faculty.setText(user.getFaculty() + " Faculty");
        major.setText(user.getMajor() + " Major");
        degree.setText(user.getDegree());
        speciality.setText(user.getSpeciality() + " Speciality");
        group.setText("Group 0" + user.getGroup());

        String profilePicturePath = sessionManager.getProfilePicturePath();

        Glide.with(this)
                .load(profilePicturePath)
                .apply(RequestOptions.circleCropTransform())
                .into(pic);

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
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(profile.this);
                builder.setMessage("Are you sure you want to log out? All unsaved changes will be lost.").setTitle("Logout")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            sessionManager.clearSession();
                            mAuth.signOut();

                            Intent intent = new Intent(getApplicationContext(), launch_screen.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            dialog.dismiss();
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background);
                alertDialog.show();
            }
        });
    }
}