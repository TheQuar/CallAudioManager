package com.quar.turnoffsilentmode;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaActionSound;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.quar.turnoffsilentmode.config.AppConfig;
import com.quar.turnoffsilentmode.fragments.NumbersFragment;
import com.quar.turnoffsilentmode.livedata.NumberViewModel;
import com.quar.turnoffsilentmode.room_database.AppDao;
import com.quar.turnoffsilentmode.room_database.AppDatabase;
import com.quar.turnoffsilentmode.room_database.NumbersTable;
import com.quar.turnoffsilentmode.service.BackgroundService;
import com.quar.turnoffsilentmode.service.MediaButtonReceiver;
import com.quar.turnoffsilentmode.service.PowerButtonReceiver;
import com.quar.turnoffsilentmode.service.RingtoneModeReceiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    public static boolean SILENT_ON;
    //    public static Ringtone ringtone;
    public static MediaPlayer mediaPlayer;
    public BottomNavigationView bottomNavigationView;
    public static NumberViewModel numberViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numberViewModel = ViewModelProviders.of(this).get(NumberViewModel.class);

        bottomNavigationView = findViewById(R.id.bottomNavigationView2);
        bottomNavigationView.setOnItemSelectedListener(this);


        //ovoz modelni tekshiradi
        RingtoneModeReceiver ringtoneModeReceiver = new RingtoneModeReceiver();
        IntentFilter ring_filter = new IntentFilter(
                AudioManager.RINGER_MODE_CHANGED_ACTION);
        registerReceiver(ringtoneModeReceiver, ring_filter);


        //power tugmasi bosilganda tekshiradi
        PowerButtonReceiver powerButtonReceiver = new PowerButtonReceiver();
        final IntentFilter power_button_filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        power_button_filter.addAction(Intent.ACTION_SCREEN_OFF);
        power_button_filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(powerButtonReceiver, power_button_filter);


        //media tugma bosilganligini tekshiradi
        MediaButtonReceiver mediaButtonReceiver = new MediaButtonReceiver();
        IntentFilter media_filter = new IntentFilter();
        media_filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(mediaButtonReceiver, media_filter);

//        ringtone = RingtoneManager.getRingtone(this, Settings.System.DEFAULT_RINGTONE_URI);

        //service ni ishga tushiradi bu service doim ishlab turadi
        Intent serviceIntent = new Intent(this, BackgroundService.class);
        serviceIntent.putExtra("inputExtra", "Dastur ish xolatida");
        ContextCompat.startForegroundService(this, serviceIntent);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (check_audio_permission()) {
            checkCallPermission();
        } else {
            showTurnOnAlertDialog();
        }
    }

    private void checkCallPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, 2);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS}, 3);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_CALL_LOG}, 4);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.FOREGROUND_SERVICE}, 5);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED) {
            if ("xiaomi".equalsIgnoreCase(android.os.Build.MANUFACTURER))
                autoRunXiaomiDialog();
            else
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED}, 6);

        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS}, 7);
        } else {
            if (AppConfig.getHaveContact(getBaseContext())) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_controller, new NumbersFragment()).commit();
            } else {
                new GetContacts(numberViewModel, getContentResolver(),
                        MainActivity.this, getSupportFragmentManager()).execute();
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this, "Permission denied to AudioManager", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case 2: {
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission denied to READ_PHONE_STATE", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case 3: {
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission denied to PROCESS_OUTGOING_CALLS", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 4: {
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission denied to READ_CALL_LOG", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 5: {
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission denied to FOREGROUND_SERVICE", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 6: {
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission denied to RECEIVE_BOOT_COMPLETED", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case 7: {
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission denied to READ_CONTACTS", Toast.LENGTH_SHORT).show();

                }
                break;
            }
        }
    }

    private boolean check_audio_permission() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted())
            return false;
        else
            return true;

    }

    private void showTurnOnAlertDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Audio tizimdan foydalanishga ruhsat berasizmi?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Ha",
                (dialog, which) -> {
                    dialog.dismiss();
                    soundModeInfoDialog();

                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yo'q",
                (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                    System.exit(0);
                });
        alertDialog.show();
    }

    private void soundModeInfoDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(getResources().getString(R.string.app_name) + " nomli dasturni dasturlar ro'yxati ichidan tanlab \"bezovta qilmaslikka ruxsat bering\" ni yoqasiz ");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Tushundim",
                (dialog, which) -> {
                    dialog.dismiss();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                        startActivity(intent);
                    }

                });

        alertDialog.show();
    }


    private void autoRunXiaomiDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(getResources().getString(R.string.app_name) + " Dasturni autorun ga qo'shishga rozimisiz ");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ha",
                (dialog, which) -> {
                    dialog.dismiss();
                    autoRunXiaomi();
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Yo'q",
                (dialog, which) -> {
                    dialog.dismiss();
                });

        alertDialog.show();
    }

    private void autoRunXiaomi() {
        if ("xiaomi".equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            startActivity(intent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.phone_number_list_item: {


            }
            break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    public static void init(Context context) {
        try {
            AssetFileDescriptor afd = context.getAssets().openFd("music.mp3");
            if (mediaPlayer != null)
                mediaPlayer.release();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


class GetContacts extends AsyncTask<Void, Void, Void> {
    NumberViewModel numberViewModel;
    ContentResolver contentResolver;
    Context context;
    FragmentManager fragmentManager;
    List<NumbersTable> numbers = new ArrayList<>();


    ProgressDialog dialog;

    public GetContacts(NumberViewModel numberViewModel, ContentResolver contentResolver,
                       Context context, FragmentManager fragmentManager) {
        this.numberViewModel = numberViewModel;
        this.contentResolver = contentResolver;
        this.context = context;
        this.fragmentManager = fragmentManager;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = ProgressDialog.show(context, "", "Loading. Please wait...", true);
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);

        numberViewModel.insertNumber(numbers);
        dialog.dismiss();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_controller, new NumbersFragment()).commit();
        AppConfig.setHaveContact(context, true);

    }

    @Override
    protected Void doInBackground(Void... voids) {
        Cursor cur = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {

            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        numbers.add(new NumbersTable(name, phoneNo.replaceAll("[^0-9]", ""), false));


                    }
                    pCur.close();
                }
            }

        }
        if (cur != null) {
            cur.close();
        }

        return null;
    }


}
