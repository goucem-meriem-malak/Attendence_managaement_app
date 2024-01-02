package com.example.attendence_managaement_app;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendence_managaement_app.classes.attendance;
import com.example.attendence_managaement_app.classes.course;
import com.example.attendence_managaement_app.classes.group;
import com.example.attendence_managaement_app.classes.notification;
import com.example.attendence_managaement_app.classes.user;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class login extends AppCompatActivity {
    private Button login;
    private TextView forget_password;
    private EditText email, password;
    private CheckBox remember;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List<course> userCourses;
    private List<String> courseIds;
    private List<String> attendanceIds;
    private List<String> notificationIds;

    private SessionManager sessionManager;
    private user user;
    private group group;
    private course course;
    private notification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        group = new group();
        user = new user();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        sessionManager = new SessionManager(getApplicationContext());
        
        login = findViewById(R.id.login);
        forget_password = findViewById(R.id.forget_password);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        remember = findViewById(R.id.remember);

        progressBar = findViewById(R.id.progressBar);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                email.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(10);
                    loginUser(email.getText().toString(), password.getText().toString());
                } else if (email.getText().toString().isEmpty()) {
                    email.setError(getString(R.string.error_empty_email));
                } else if (password.getText().toString().isEmpty()) {
                    password.setError(getString(R.string.error_empty_password));
                }
            }
        });
        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!email.getText().toString().isEmpty()){
                    mAuth.sendPasswordResetEmail(email.getText().toString())
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Password reset email sent.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Failed to send password reset email.", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    email.setError(getString(R.string.error_empty_email));
                }
            }
        });
    }
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, taskk -> {
                    if (taskk.isSuccessful()) {
                        String userId = mAuth.getUid();

                        db.collection("user").document(userId).get().addOnCompleteListener(userTask -> {
                            if (userTask.isSuccessful()) {
                                        DocumentSnapshot document = userTask.getResult();
                                        if (document.exists()) {
                                            String firstName = document.getString("first_name");
                                            String lastName = document.getString("last_name");

                                            user.setUid(userId);
                                            user.setFirst_name(firstName);
                                            user.setLast_name(lastName);
                                            user.setEmail(email);

                                            userCourses = new ArrayList<>();

                                            db.collection("group").whereArrayContains("listOfStudents", userId)
                                                    .get()
                                                    .addOnCompleteListener(task -> {
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot documentGroup : task.getResult()) {
                                                                user.setGroup(documentGroup.getString("group"));
                                                                user.setSpeciality(documentGroup.getString("idSpeciality"));
                                                                group.setGroup(user.getGroup());
                                                                group.setIdSpeciality(user.getSpeciality());
                                                                group.setListOfCourses((List<String>) documentGroup.get("listOfCourses"));
                                                                group.setListOfStudents((List<String>) documentGroup.get("listOfStudents"));

                                                                getCourses(group.getListOfCourses(), userId);


                                                                db.collection("speciality").document(user.getSpeciality()).get().addOnCompleteListener(taskSpeciality -> {
                                                                    if (taskSpeciality.isSuccessful()) {
                                                                        DocumentSnapshot documentSpeciality = taskSpeciality.getResult();
                                                                        if (documentSpeciality.exists()) {
                                                                            user.setDegree(documentSpeciality.getString("degree"));
                                                                            user.setMajor(documentSpeciality.getString("idMajor"));
                                                                            db.collection("major").document(user.getMajor()).get().addOnCompleteListener(taskMajor -> {
                                                                                if (taskMajor.isSuccessful()) {
                                                                                    DocumentSnapshot documentMajor = taskMajor.getResult();
                                                                                    if (documentMajor.exists()) {
                                                                                        user.setFaculty(documentMajor.getString("idFaculty"));
                                                                                        db.collection("faculty").document(user.getFaculty()).get().addOnCompleteListener(taskFaculty -> {
                                                                                            if (taskFaculty.isSuccessful()) {
                                                                                                DocumentSnapshot documentFaculty = taskFaculty.getResult();
                                                                                                if (documentFaculty.exists()) {
                                                                                                    user.setDepartment(documentFaculty.getString("idDepartment"));
                                                                                                    sessionManager.saveUserDetails(user);
                                                                                                    sessionManager.saveGroup(group);
                                                                                                }
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });
                                            getNotification(userId);

                                            /*db.collection("attendance").get().addOnCompleteListener(taskAttendance -> {
                                                if (taskAttendance.isSuccessful()) {
                                                    for (DocumentSnapshot documentAttendance : taskAttendance.getResult()) {
                                                        List<String> listOfPresence = (List<String>) documentAttendance.get("listOfPresence");

                                                        if (listOfPresence != null && listOfPresence.contains(userId)) {
                                                            attendanceIds.add(documentAttendance.getId());
                                                        }
                                                        for (String attendanceId : attendanceIds) {
                                                            db.collection("attendance")
                                                                    .document(attendanceId)
                                                                    .get()
                                                                    .addOnCompleteListener(attendanceTask -> {
                                                                        if (attendanceTask.isSuccessful()) {
                                                                            DocumentSnapshot attendanceDocument = attendanceTask.getResult();
                                                                            if (attendanceDocument.exists()) {

                                                                                String idCourse = attendanceDocument.getString("idCourse");
                                                                                Boolean stat = attendanceDocument.getBoolean("stat");
                                                                                Timestamp time = attendanceDocument.getTimestamp("time");

                                                                                attendance attendanceObj;
                                                                                attendanceObj = new attendance(attendanceId, userId, idCourse, stat, time);

                                                                                userAttendances.add(attendanceObj);
                                                                                if (userAttendances.size() == attendanceIds.size()) {
                                                                                    user.setAttendances(userAttendances);
                                                                                    sessionManager.saveAttendances(userAttendances);
                                                                                }
                                                                                Toast.makeText(this, attendanceObj.toString(), Toast.LENGTH_SHORT).show();
                                                                            }}
                                                                    });
                                                        }
                                                    }
                                                }
                                            });*/
                                            downloadProfilePicture(userId);
                                            progressBar.setProgress(80);
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressBar.setProgress(100);
                                                    sessionManager.setLoggedIn(remember.isChecked());
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    Intent intent = new Intent(getApplicationContext(), courses.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                    startActivity(intent);
                                                }
                                            }, 3000);
                                            sessionManager.saveUserDetails(user);
                                            Intent intent = new Intent(getApplicationContext(), courses.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            startActivity(intent);
                                        }
                                    }
                                });
                    }
                });
    }
    private void getUserData(String uid){

    }
    private void getCourses(List<String> courseIds, String userid){
        userCourses = new ArrayList<>();

        for (String courseId : courseIds) {
            db.collection("course")
                    .document(courseId)
                    .get()
                    .addOnCompleteListener(courseTask -> {
                        if (courseTask.isSuccessful()) {
                            DocumentSnapshot courseDocument = courseTask.getResult();
                            if (courseDocument.exists()) {
                                String courseName = courseDocument.getString("name");
                                String idProf = courseDocument.getString("idProf");
                                course courseObj = new course(courseId, courseName, idProf);
                                userCourses.add(courseObj);
                                if (userCourses.size() == courseIds.size()) {
                                    user.setCourses(userCourses);
                                    sessionManager.saveCourses(userCourses);
                                }
                            }
                            getAttendances(courseIds, userid);
                        }
                    });
        }
    }
    private void getAttendances(List<String> courseIds, String userid){
        List<attendance> attendanceList = new ArrayList<>();

        db.collection("attendance").whereIn("idCourse", courseIds)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String idCourse = document.getString("idCourse");
                            Timestamp time = document.getTimestamp("time");
                            List<String> listOfPresence = (List<String>) document.get("listOfPresence");
                            boolean stat = listOfPresence != null && listOfPresence.contains(userid);
                            attendance attendance = new attendance(id, idCourse, stat, time);
                            attendanceList.add(attendance);
                        }
                        user.setAttendances(attendanceList);
                        sessionManager.saveAttendances(attendanceList);
                    } else {
                        System.out.println("Error getting documents: " + task.getException());
                    }
                });
    }
    private void getNotification(String uid){
        List<notification> userNotifications = new ArrayList<>();

        db.collection("notification").whereEqualTo("idStudent", uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String title = document.getString("title");
                            String subject = document.getString("subject");
                            String idStudent = document.getString("idStudent");
                            String idCourse = document.getString("idCourse");
                            Timestamp time = document.getTimestamp("time");
                            String idJustification = document.getString("idJustification");

                            notification notificationObj;
                            if (idJustification!=null){
                                notificationObj = new notification(id, title, subject, idStudent, idJustification, idCourse, time);
                            } else {
                                notificationObj = new notification(id, title, subject, idStudent, null, idCourse, time);
                            }
                            userNotifications.add(notificationObj);
                        }
                        user.setNotifications(userNotifications);
                        sessionManager.saveNotifications(userNotifications);
                    } else {
                        System.out.println("Error getting documents: " + task.getException());
                    }
                });
    }

    private void downloadProfilePicture(String userId) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference userFolderRef = storageRef.child("user/" + userId);

        userFolderRef.listAll()
                .addOnSuccessListener(listResult -> {
                    if (!listResult.getItems().isEmpty()) {
                        StorageReference userPicRef = listResult.getItems().get(0);

                        File localFile = null;
                        try {
                            localFile = File.createTempFile("images", "png");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        File finalLocalFile = localFile;
                        userPicRef.getFile(localFile)
                                .addOnSuccessListener(taskSnapshot -> {
                                    String localFilePath = finalLocalFile.getAbsolutePath();
                                    sessionManager.saveProfilePicturePath(localFilePath);
                                })
                                .addOnFailureListener(exception -> {
                                    Log.e(TAG, "Error downloading profile picture: " + exception.getMessage());
                                });
                    } else {
                        Log.w(TAG, "No profile picture found for user: " + userId);
                    }
                })
                .addOnFailureListener(exception -> {
                    // Handle failure to list files
                    Log.e(TAG, "Error listing files in user's folder: " + exception.getMessage());
                });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}