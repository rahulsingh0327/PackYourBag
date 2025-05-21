package com.example.packyourbag;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.packyourbag.Adapter.Adapter;
import com.example.packyourbag.Constants.MyConstants;
import com.example.packyourbag.Data.AppData;
import com.example.packyourbag.Database.RoomDb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<String> titles;
    List<Integer> images;
    Adapter adapter;
    RoomDb database;

    ImageButton btnChooseDate;
    Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        btnChooseDate = findViewById(R.id.btnChooseDate);

        // Set click listener for the "Choose Date" button
        btnChooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);

        // Initialize and set up the RecyclerView and Adapter
        addTitles();  // Add titles to the list
        addImages();  // Add images to the list

        persistAppData();  // Persist application data using SharedPreferences
        database = RoomDb.getInstance(this);

        adapter = new Adapter(this, titles, images, MainActivity.this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    // Handle back press to exit with a delay
    private static final int TIME_INTERVAL = 2000;
    private long nBackPressed;

    @Override
    public void onBackPressed() {
        if (nBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
        }
        nBackPressed = System.currentTimeMillis();
    }

    // Persist application data using SharedPreferences and Room Database
    private void persistAppData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        database = RoomDb.getInstance(this);
        AppData appData = new AppData(database);
        int last = prefs.getInt(AppData.LAST_VERSION, 0);
        if (!prefs.getBoolean(MyConstants.FIRST_TIME, false)) {
            appData.persistAllData();
            editor.putBoolean(MyConstants.FIRST_TIME, true);
            editor.apply();
        } else if (last < AppData.NEW_VERSION) {
            database.mainDao().deleteAllSystemItems((MyConstants.SYSTEM_SMALL));
            appData.persistAllData();
            editor.putInt(AppData.LAST_VERSION, AppData.NEW_VERSION);
            editor.apply();
        }
    }

    // Add titles to the list
    private void addTitles() {
        titles = new ArrayList<>();
        titles.add(MyConstants.BASIC_NEEDS);
        titles.add(MyConstants.CLOTHING);
        titles.add(MyConstants.PERSONAL_CARE);
        titles.add(MyConstants.BABY_NEEDS);
        titles.add(MyConstants.HEALTH);
        titles.add(MyConstants.TECHNOLOGY);
        titles.add(MyConstants.FOOD);
        titles.add(MyConstants.BEACH_SUPPLIES);
        titles.add(MyConstants.CAR_SUPPLIES);
        titles.add(MyConstants.NEEDS);
        titles.add(MyConstants.MY_LIST);
        titles.add(MyConstants.MY_SELECTIONS_CAMEL_CASE);
    }

    // Show DatePickerDialog for choosing a date
    private void showDatePickerDialog() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();

        // Create a DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    // You can now use the selectedDate for further actions
                    setReminder();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        // Show the date picker dialog
        datePickerDialog.show();
    }

    // Set reminders for one day before and on the selected day
    private void setReminder() {
        if (selectedDate != null) {
            // Convert the selected date to milliseconds
            long selectedDateTimeInMillis = selectedDate.getTimeInMillis();
            long oneDayBeforeMillis = selectedDateTimeInMillis - TimeUnit.DAYS.toMillis(1);

            // Create Intents for reminders
            Intent reminderIntentBefore = createReminderIntent("One day before: You still have %d things to pack!", true);
            Intent reminderIntentOnDay = createReminderIntent("On the day: You still have %d things to pack!", false);

            // Create PendingIntents for reminders
            PendingIntent pendingIntentBefore = createPendingIntent(0, reminderIntentBefore);
            PendingIntent pendingIntentOnDay = createPendingIntent(1, reminderIntentOnDay);

            // Send broadcast for testing
            Intent testIntent = new Intent("com.example.packyourbag.REMINDER_BROADCAST");
            sendBroadcast(testIntent);

            // Set alarms for one day before and on the selected day
            setAlarm(oneDayBeforeMillis, pendingIntentBefore);
            setAlarm(selectedDateTimeInMillis, pendingIntentOnDay);
        }
    }

    // Create a reminder Intent with a message
    private Intent createReminderIntent(String messageFormat, boolean isBefore) {
        Intent reminderIntent = new Intent(this, ReminderBroadcastReceiver.class);
        reminderIntent.setAction("com.example.packyourbag.REMINDER_BROADCAST");
        reminderIntent.setComponent(new ComponentName(this, ReminderBroadcastReceiver.class));

        // Get the count of selected items from the database
        Integer itemCount = database.mainDao().getSelectedCount(true);
        if (itemCount == null) itemCount = 5;

        // Construct the reminder message
        String reminderMessage = String.format(messageFormat, itemCount);
        reminderIntent.putExtra("message", reminderMessage);

        return reminderIntent;
    }

    // Create a PendingIntent for reminders
    private PendingIntent createPendingIntent(int requestCode, Intent reminderIntent) {
        return PendingIntent.getBroadcast(
                this,
                requestCode,
                reminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    // Set an alarm for a given time and PendingIntent
    private void setAlarm(long triggerTimeMillis, PendingIntent pendingIntent) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Set the alarm
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + triggerTimeMillis,
                pendingIntent);
    }

    // Add images to the list
    private void addImages() {
        images = new ArrayList<>();
        images.add(R.drawable.p1);
        images.add(R.drawable.p2);
        images.add(R.drawable.p3);
        images.add(R.drawable.p4);
        images.add(R.drawable.p5);
        images.add(R.drawable.p6);
        images.add(R.drawable.p7);
        images.add(R.drawable.p8);
        images.add(R.drawable.p9);
        images.add(R.drawable.p10);
        images.add(R.drawable.p11);
        images.add(R.drawable.p12);
    }
}
