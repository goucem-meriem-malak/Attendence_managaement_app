package com.example.attendence_managaement_app;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.attendence_managaement_app.classes.attendance;
import com.example.attendence_managaement_app.classes.course;
import com.example.attendence_managaement_app.classes.group;
import com.example.attendence_managaement_app.classes.notification;
import com.example.attendence_managaement_app.classes.user;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SessionManager {
    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_UID = "uid";
    private static final String KEY_FIRST_NAME = "firstName";
    private static final String KEY_LAST_NAME = "lastName";
    private static final String KEY_EMAIL = "email";
    private static final String PREF_DEPARTMENT = "department";
    private static final String KEY_FACULTY = "faculty";
    private static final String KEY_MAJOR = "major";
    private static final String KEY_DEGREE = "degree";
    private static final String KEY_SPECIALITY = "speciality";
    private static final String KEY_GROUP = "group";
    private static final String KEY_COURSES = "courses";
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_ATTENDANCES = "attendances";
    private static final String KEY_PROFILE_PICTURE_PATH = "profile_pic_path";
    private static final String KEY_COURSE_IDS = "courseIds";
    private static final String KEY_ATTENDANCE_IDS = "attendanceIds";
    private static final String KEY_LIST_OF_COURSES = "listOfCourses";
    private static final String KEY_LIST_OF_STUDENTS = "listOfStudents";


    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    private Gson gson;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        gson = new Gson();
    }

    public void setLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void saveUserDetails(user user) {
        editor.putString(KEY_UID, user.getUid());
        editor.putString(KEY_FIRST_NAME, user.getFirst_name());
        editor.putString(KEY_LAST_NAME, user.getLast_name());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(PREF_DEPARTMENT, user.getDepartment());
        editor.putString(KEY_FACULTY, user.getFaculty());
        editor.putString(KEY_MAJOR, user.getMajor());
        editor.putString(KEY_DEGREE, user.getDegree());
        editor.putString(KEY_SPECIALITY, user.getSpeciality());
        editor.putString(KEY_GROUP, user.getGroup());

        Gson gson = new Gson();

        String coursesJson = gson.toJson(user.getCourses());
        editor.putString(KEY_COURSES, coursesJson);

        String notificationsJson = gson.toJson(user.getNotifications());
        editor.putString(KEY_NOTIFICATIONS, notificationsJson);

        String attendancesJson = gson.toJson(user.getAttendances());
        editor.putString(KEY_ATTENDANCES, attendancesJson);

        editor.apply();
    }
    public user getUserDetails() {
        user user = new user();
        user.setUid(pref.getString(KEY_UID, ""));
        user.setFirst_name(pref.getString(KEY_FIRST_NAME, ""));
        user.setLast_name(pref.getString(KEY_LAST_NAME, ""));
        user.setEmail(pref.getString(KEY_EMAIL, ""));
        user.setDepartment(pref.getString(PREF_DEPARTMENT, ""));
        user.setFaculty(pref.getString(KEY_FACULTY, ""));
        user.setMajor(pref.getString(KEY_MAJOR, ""));
        user.setDegree(pref.getString(KEY_DEGREE, ""));
        user.setSpeciality(pref.getString(KEY_SPECIALITY, ""));
        user.setGroup(pref.getString(KEY_GROUP, ""));

        Gson gson = new Gson();

        String coursesJson = pref.getString(KEY_COURSES, "");
        if (!coursesJson.isEmpty()){
            Type listTypeC = new TypeToken<List<course>>(){}.getType();
            List<course> courses = gson.fromJson(coursesJson, listTypeC);
            user.setCourses(courses);
        }

        String notificationsJson = pref.getString(KEY_NOTIFICATIONS, "");

        if (!notificationsJson.isEmpty()) {
            Type listTypeN = new TypeToken<List<notification>>(){}.getType();
            List<notification> notifications = gson.fromJson(notificationsJson, listTypeN);
            user.setNotifications(notifications);
        }

        String attendancesJson = pref.getString(KEY_ATTENDANCES, "");

        if (!attendancesJson.isEmpty()) {
            Type listTypeA = new TypeToken<List<attendance>>(){}.getType();
            List<attendance> attendances = gson.fromJson(attendancesJson, listTypeA);
            user.setAttendances(attendances);
        }

        return user;
    }
    public void saveGroup(group group) {
        editor.putString(KEY_GROUP, group.getGroup());
        editor.putString(KEY_SPECIALITY, group.getIdSpeciality());

        Gson gson = new Gson();

        String listOfCoursesJson = gson.toJson(group.getListOfCourses());
        editor.putString(KEY_LIST_OF_COURSES, listOfCoursesJson);

        String listOfStudentsJson = gson.toJson(group.getListOfStudents());
        editor.putString(KEY_LIST_OF_STUDENTS, listOfStudentsJson);

        editor.apply();
    }
    public group getGroup() {
        group group = new group();
        group.setGroup(pref.getString(KEY_GROUP, ""));
        group.setIdSpeciality(pref.getString(KEY_SPECIALITY, ""));

        Gson gson = new Gson();

        String coursesJson = pref.getString(KEY_LIST_OF_COURSES, "");
        if (!coursesJson.isEmpty()){
            Type listTypeC = new TypeToken<List<String>>(){}.getType();
            List<String> courses = gson.fromJson(coursesJson, listTypeC);
            group.setListOfCourses(courses);
        }
        String studentsJson = pref.getString(KEY_LIST_OF_STUDENTS, "");
        if (!studentsJson.isEmpty()){
            Type listTypeC = new TypeToken<List<String>>(){}.getType();
            List<String> students = gson.fromJson(studentsJson, listTypeC);
            group.setListOfStudents(students);
        }
        return group;
    }
    public void saveCourses(List<course> courses) {
        Gson gson = new Gson();
        String coursesJson = gson.toJson(courses);
        editor.putString(KEY_COURSES, coursesJson);
        editor.apply();
    }
    public List<course> getCourses() {
        String coursesJson = pref.getString(KEY_COURSES, "");

        if (!coursesJson.isEmpty()) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<course>>() {}.getType();
            return gson.fromJson(coursesJson, listType);
        }

        return new ArrayList<>();
    }
    public void saveAttendances(List<attendance> attendances) {
        Gson gson = new Gson();
        String attendancesJson = gson.toJson(attendances);
        editor.putString(KEY_ATTENDANCES, attendancesJson);
        editor.apply();
    }
    public List<attendance> getAttendances() {
        String attendancesJson = pref.getString(KEY_ATTENDANCES, "");

        if (!attendancesJson.isEmpty()) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<attendance>>() {}.getType();
            return gson.fromJson(attendancesJson, listType);
        }

        return new ArrayList<>();
    }
    public void saveNotifications(List<notification> notifications) {
        Gson gson = new Gson();
        String notificationsJson = gson.toJson(notifications);
        editor.putString(KEY_NOTIFICATIONS, notificationsJson);
        editor.apply();
    }

    public List<notification> getNotifications() {
        String notificationsJson = pref.getString(KEY_NOTIFICATIONS, "");

        if (!notificationsJson.isEmpty()) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<notification>>() {}.getType();
            return gson.fromJson(notificationsJson, listType);
        }

        return new ArrayList<>();
    }

    public String getCourseNameById(String courseId) {
        List<course> courses = getCourses();

        for (course c : courses) {
            if (c.getId().equals(courseId)) {
                return c.getName();
            }
        }

        return null; // Course not found
    }

    public void saveProfilePicturePath(String profilePicturePath) {
        editor.putString(KEY_PROFILE_PICTURE_PATH, profilePicturePath);
        editor.apply();
    }

    public String getProfilePicturePath() {
        return pref.getString(KEY_PROFILE_PICTURE_PATH, "");
    }
    public void clearSession() {
        editor.clear();
        editor.apply();
    }
    public void saveCourseIds(List<String> courseIds) {
        Set<String> courseIdsSet = new HashSet<>(courseIds);
        editor.putStringSet(KEY_COURSE_IDS, courseIdsSet);
        editor.apply();
    }

    public List<String> getCourseIds() {
        Set<String> courseIdsSet = pref.getStringSet(KEY_COURSE_IDS, new HashSet<>());
        return new ArrayList<>(courseIdsSet);
    }

    public void saveAttendanceIds(List<String> AttendanceIds) {
        Set<String> AttendanceIdsSet = new HashSet<>(AttendanceIds);
        editor.putStringSet(KEY_ATTENDANCE_IDS, AttendanceIdsSet);
        editor.apply();
    }

    public List<String> getAttendanceIds() {
        Set<String> AttendanceIdsSet = pref.getStringSet(KEY_ATTENDANCE_IDS, new HashSet<>());
        return new ArrayList<>(AttendanceIdsSet);
    }
}

