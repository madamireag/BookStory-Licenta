package com.example.bookstory.activities;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;

import com.example.bookstory.R;
import com.example.bookstory.database.LibraryDB;
import com.example.bookstory.models.Imprumut;
import com.example.bookstory.models.Utilizator;
import com.example.bookstory.notification.NotificationReceiver;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    CardView cvProfile;
    CardView cvSignOut;
    CardView cvListaCarti;
    CardView cvRecomandari;
    CardView cvImprumuturi;
    CardView cvContact;
    FirebaseAuth auth;
    LibraryDB dbInstance;
    List<Imprumut> imprumuturi = new ArrayList<>();
    List<Imprumut> imprumuturiScadente = new ArrayList<>();
    private final static String default_notification_channel_id = "default";
    public static final String NOTIFICATION_CHANNEL_ID = "10001";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initializeUI();
        auth = FirebaseAuth.getInstance();
        dbInstance = LibraryDB.getInstanta(getApplicationContext());

        Utilizator utilizator = null;

        if (auth.getCurrentUser() != null) {
            utilizator = dbInstance.getUserDao().getUserByUid(auth.getCurrentUser().getUid());
        }
        if (utilizator != null) {
            imprumuturi = dbInstance.getImprumutDao().getAllImprumuturiForUser(utilizator.getId());
        }
        Date dataCurenta = new Date();
        if (!imprumuturi.isEmpty()) {
            for (Imprumut i : imprumuturi) {
                if (i.getDataScadenta().after(dataCurenta)) {
                    imprumuturiScadente.add(i);
                }
            }
        }

        cvSignOut.setOnClickListener(v -> {
            if (auth.getCurrentUser() != null) {
                auth.signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        cvProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
            startActivity(intent);
        });
        cvListaCarti.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ListaCartiUserActivity.class);
            startActivity(intent);
        });
        cvRecomandari.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RecomandariActivity.class);
            startActivity(intent);
        });
        cvImprumuturi.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), VizualizareImprumuturiActivity.class);
            startActivity(intent);
        });
        cvContact.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ContactUsActivity.class);
            startActivity(intent);
        });
        Calendar calendar = Calendar.getInstance();

        if (!imprumuturiScadente.isEmpty()) {
            for (Imprumut i : imprumuturiScadente) {
                calendar.setTime(i.getDataScadenta());
                calendar.add(Calendar.DATE, -1);
                calendar.set(Calendar.HOUR_OF_DAY, 12);
                calendar.set(Calendar.MINUTE, 52);
                calendar.set(Calendar.SECOND, 10);
                String myFormat = "dd/MM/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                Date date = calendar.getTime();
                scheduleNotification(getNotification(sdf.format(date)), calendar.getTimeInMillis());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void scheduleNotification(Notification notification, long delay) {
        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.RTC_WAKEUP, delay, pendingIntent);
    }

    private Notification getNotification(String content) {
        Intent notifyIntent = new Intent(this, VizualizareImprumuturiActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, default_notification_channel_id);
        builder.setContentTitle(getString(R.string.imprumut_scadent_notification));
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_baseline_calendar_today_24);
        builder.setAutoCancel(true);
        builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        builder.setColor(Color.CYAN);
        builder.setContentIntent(notifyPendingIntent);
        return builder.build();
    }

    private void initializeUI() {
        cvProfile = findViewById(R.id.cvProfile);
        cvSignOut = findViewById(R.id.cvLogout);
        cvListaCarti = findViewById(R.id.cvListaCarti);
        cvRecomandari = findViewById(R.id.cvRecomandari);
        cvImprumuturi = findViewById(R.id.cvImprumuturi);
        cvContact = findViewById(R.id.cvContact);
    }
}