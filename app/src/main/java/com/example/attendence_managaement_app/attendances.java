package com.example.attendence_managaement_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.attendence_managaement_app.classes.attendance;
import com.example.attendence_managaement_app.classes.course;
import com.example.attendence_managaement_app.classes.group;
import com.example.attendence_managaement_app.classes.justification;
import com.example.attendence_managaement_app.classes.user;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.divider.MaterialDivider;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class attendances extends AppCompatActivity {
    private Button justification, menu, notification, profile, scanQR;
    private TextView courseName;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final int REQUEST_PDF_PICKER = 1;
    private static final int REQUEST_IMAGE_PICKER = 2;

    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int REQUEST_QR_CODE_SCANNER = 4;
    private static final int REQUEST_FILE_PICKER = 5;
    private SessionManager sessionManager;
    private user user;
    private List<attendance> attendances;
    private LinearLayout attendanceContainer;
    private String idCourse, uid;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendances);

        justification = findViewById(R.id.justification);
        menu = findViewById(R.id.menu);
        notification = findViewById(R.id.notification);
        profile = findViewById(R.id.profile);
        scanQR = findViewById(R.id.scan_qr);
        courseName = findViewById(R.id.course_name);
        swipeRefreshLayout = findViewById(R.id.refresh);


        sessionManager = new SessionManager(getApplicationContext());
        user = sessionManager.getUserDetails();

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        idCourse = getIntent().getStringExtra("idCourse");
        String course_name = sessionManager.getCourseNameById(idCourse);

        if (courseName != null) {
            courseName.setText(course_name);
        }

        attendances = user.getAttendances();
        getAttendances(idCourse);

        justification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUploadOptionsDialog();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshActivity(idCourse);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
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
        scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanQRCode(scanQR);
            }
        });
    }
    public static boolean isWithinTwoDays(Date attendanceTime) {
        Calendar attendanceCalendar = Calendar.getInstance();
        attendanceCalendar.setTime(attendanceTime);

        Calendar currentCalendar = Calendar.getInstance();

        long daysDifference = (currentCalendar.getTimeInMillis() - attendanceCalendar.getTimeInMillis()) / (24 * 60 * 60 * 1000);

        return daysDifference < 2;
    }
    private void getAttendances(String idCourse) {
        attendances = sessionManager.getAttendances();
        attendanceContainer = findViewById(R.id.list_attendances);

        for (int i = 0; i < attendances.size(); i++) {
            final attendance currentAttendance= attendances.get(i);

            if (idCourse.equals(currentAttendance.getIdCourse())) {

                View attendanceView = LayoutInflater.from(this).inflate(R.layout.attendance, null);

                TextView timeTextView = attendanceView.findViewById(R.id.time);
                TextView dateTextView = attendanceView.findViewById(R.id.date);
                ImageView statImageView = attendanceView.findViewById(R.id.stat);

                Timestamp timestamp = currentAttendance.getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                if (!currentAttendance.getStat() && isWithinTwoDays(currentAttendance.getTime().toDate())){
                    justification.setVisibility(View.VISIBLE);
                }

                timeTextView.setText(timeFormat.format(timestamp.toDate()));
                dateTextView.setText(dateFormat.format(timestamp.toDate()));
                statImageView.setImageResource(currentAttendance.getStat() ? R.drawable.right : R.drawable.wrong);
                attendanceContainer.addView(attendanceView);

                if (i < attendances.size() - 1) {
                    View dividerView = new MaterialDivider(this);
                    attendanceContainer.addView(dividerView);

                    LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, (int) 0.5);
                    dividerView.setLayoutParams(dividerParams);
                }
            }
        }
    }
    public void scanQRCode(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan a QR Code");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }
    private void showUploadOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Upload Option")
                .setItems(new CharSequence[]{"Upload a file","Cancel"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("*/*");
                                startActivityForResult(intent, REQUEST_FILE_PICKER);
                                break;
                            case 1:
                                dialogInterface.dismiss();
                                break;
                        }
                    }
                });

        builder.create().show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() != null) {
                String scannedText = result.getContents();
                getAttendance(scannedText);
            }
        }
        if (requestCode == REQUEST_FILE_PICKER && resultCode == RESULT_OK && data != null) {
            Uri Uri = data.getData();
            showFilePreviewDialog(Uri);
        }
    }
    private void createJustification(Uri fileUrl){

        Map<String, Object> justificationData = new HashMap<>();

        justificationData.put("idStudent", user.getUid());
        justificationData.put("stat", false);
        justificationData.put("time", new Date());

        db.collection("justification").add(justificationData).addOnSuccessListener(documentReference -> {
                    String id = documentReference.getId();
                    uploadFileToFirebaseStorage(fileUrl, id);
                })
                .addOnFailureListener(e -> {
                    message("Error","An error occurred please try later");
                });
    }
    private void uploadFileToFirebaseStorage(Uri fileUri, String idJustification) {
        String userId = sessionManager.getUserDetails().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("user/" + userId + "/justifications/" + idJustification);
        storageReference.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    message("Successful", "The file has been uploaded successfully, wait for the result");
                })
                .addOnFailureListener(exception -> {
                    message("Error", "An error occurred, please check your network and try again");
                });
    }
    private void showFilePreviewDialog(Uri fileUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_preview, null);
        builder.setView(dialogView);

        ImageView justification = dialogView.findViewById(R.id.justification);


        Glide.with(this)
                .load(fileUri)
                .into(justification);

        Button buttonYes = dialogView.findViewById(R.id.buttonYes);
        Button buttonNo = dialogView.findViewById(R.id.buttonNo);

        AlertDialog dialog = builder.create();

        buttonYes.setOnClickListener(v -> {
            createJustification(fileUri);
            dialog.dismiss();
        });

        buttonNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    private void getAttendance(String Qrcode) {
        db.collection("attendance").document(Qrcode).update("listOfPresence", FieldValue.arrayUnion(user.getUid()))
                .addOnSuccessListener(aVoid -> {
                    refreshActivity(idCourse);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Error updating array: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void refreshActivity(String idCourse){
        group group = sessionManager.getGroup();
        List<String> courseIds = group.getListOfCourses();

        db.collection("attendance").whereIn("idCourse", courseIds)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        attendances.clear();
                        attendanceContainer.removeAllViews();
                        for (DocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String idcourse = document.getString("idCourse");
                            Timestamp time = document.getTimestamp("time");
                            List<String> listOfPresence = (List<String>) document.get("listOfPresence");
                            boolean stat = listOfPresence != null && listOfPresence.contains(user.getUid());
                            attendance attendance = new attendance(id, idcourse, stat, time);
                            attendances.add(attendance);
                        }
                        user.setAttendances(attendances);
                        sessionManager.saveAttendances(attendances);
                        getAttendances(idCourse);
                    } else {
                        System.out.println("Error getting documents: " + task.getException());
                    }
                });
    }
    private void message(String title, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(attendances.this);
        builder.setMessage(msg).setTitle(title)
                .setPositiveButton("ok", (dialog, which) -> {
                    dialog.dismiss();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background);
        alertDialog.show();
    }
}