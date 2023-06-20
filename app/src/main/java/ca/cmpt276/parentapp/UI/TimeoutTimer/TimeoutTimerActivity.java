package ca.cmpt276.parentapp.UI.TimeoutTimer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import java.util.Locale;

import ca.cmpt276.parentapp.R;
import ca.cmpt276.parentapp.UI.Notification.NotificationIntentService;
import ca.cmpt276.parentapp.UI.Notification.NotificationReceiver;

public class TimeoutTimerActivity extends AppCompatActivity {
    public static final String CHANNEL = "Timer";
    private static final String ACTION_START_NOTIFICATION_SERVICE = "ACTION_START_NOTIFICATION_SERVICE";
    private static final String ACTION_DELETE_NOTIFICATION = "ACTION_DELETE_NOTIFICATION";
    private long START_TIME = 60000;
    private float TIMER_SPEED = 1;
    private TextView countDown;
    private EditText userInput;
    private boolean timerRunning;
    private long TIME_LEFT, END_TIME;
    private int progress;
    private CountDownTimer countDownTimer;
    private Button startAndPauseBtn, resetBtn, setBtn;
    private NotificationManagerCompat notificationManager;
    private ProgressBar simpleProgressBar, indeterminateProgressBar;


    public static Intent makeIntent(Context context) {
        return new Intent(context, TimeoutTimerActivity.class);
    }

    private static PendingIntent getStartPendingIntent(Context context) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.setAction(ACTION_START_NOTIFICATION_SERVICE);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeout_timer);
        setTitle("Timeout Timer");
        // code from https://abhiandroid.com/ui/progressbar
        // circular progress bar code from https://stackoverflow.com/questions/21333866/how-to-create-a-circular-progressbar-in-android-which-rotates-on-it
        simpleProgressBar = findViewById(R.id.simpleProgressBar); // initiate the progress bar
        indeterminateProgressBar = findViewById(R.id.indeterminateProgressBar);
        simpleProgressBar.setVisibility(View.INVISIBLE);
        simpleProgressBar.setMax(100);
        progress = 0;

        initAllItems();
        setupTimer();
        setTimerBtn();
        startService(new Intent(this, NotificationIntentService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timeout_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (timerRunning) {
            Toast.makeText(TimeoutTimerActivity.this, "Timer is already running", Toast.LENGTH_SHORT).show();
            return super.onOptionsItemSelected(item);
        }
        switch (item.getItemId()) {
            case R.id.twenty:
                TIMER_SPEED = 0.25f;
                Toast.makeText(TimeoutTimerActivity.this, "20% speed", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.fifty:
                TIMER_SPEED = 0.5f;
                Toast.makeText(TimeoutTimerActivity.this, "50% speed", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.seventyFive:
                TIMER_SPEED = 0.75f;
                Toast.makeText(TimeoutTimerActivity.this, "75% speed", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.Hondos:
                TIMER_SPEED = 1;
                Toast.makeText(TimeoutTimerActivity.this, "100% speed", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.doubleSpeed:
                Toast.makeText(TimeoutTimerActivity.this, "200% speed", Toast.LENGTH_SHORT).show();
                TIMER_SPEED = 2f;
                return true;
            case R.id.tripleDaSpeed:
                TIMER_SPEED = 3f;
                Toast.makeText(TimeoutTimerActivity.this, "300% speed", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.quadroSpeed:
                TIMER_SPEED = 4f;
                Toast.makeText(TimeoutTimerActivity.this, "400% speed", Toast.LENGTH_SHORT).show();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setTimerBtn() {
        Button oneMin, twoMin, threeMin, fourMin, fiveMin;
        oneMin = findViewById(R.id.oneMin);
        twoMin = findViewById(R.id.twoMin);
        threeMin = findViewById(R.id.threeMin);
        fourMin = findViewById(R.id.fourMin);
        fiveMin = findViewById(R.id.fiveMin);

        oneMin.setOnClickListener(view -> setTimer(60000L));
        twoMin.setOnClickListener(view -> setTimer(2 * 60000L));
        threeMin.setOnClickListener(view -> setTimer(3 * 60000L));
        fourMin.setOnClickListener(view -> setTimer(4 * 60000L));
        fiveMin.setOnClickListener(view -> setTimer(5 * 60000L));
    }

    private void initAllItems() {
        setBtn = findViewById(R.id.setBtn);
        resetBtn = findViewById(R.id.resetBtn);
        userInput = findViewById(R.id.userInput);
        countDown = findViewById(R.id.countDown);
        startAndPauseBtn = findViewById(R.id.startAndPauseBtn);
        notificationManager = NotificationManagerCompat.from(this);
    }

    private void setTimer(long time) {
        START_TIME = time;
        resetTimer();
    }

    private void setupTimer() {
        startAndPauseBtn.setOnClickListener(view -> {
            if (timerRunning) pauseTimer();
            else startTimer();
        });
        resetBtn.setOnClickListener(view -> resetTimer());
        setBtn.setOnClickListener(view -> {
            if (!userInput.getText().toString().isEmpty()) {
                if (Long.parseLong(userInput.getText().toString()) == 0) {
                    Toast.makeText(TimeoutTimerActivity.this, "Enter Positive Number", Toast.LENGTH_SHORT).show();
                    return;
                }
                long time = Long.parseLong(userInput.getText().toString()) * 60000;
                setTimer(time);
                userInput.setText("");
            }
        });
    }

    private void startTimer() {
        END_TIME = System.currentTimeMillis() + TIME_LEFT;
        indeterminateProgressBar.setVisibility(View.INVISIBLE);
        simpleProgressBar.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer(TIME_LEFT, (long) (1000 / TIMER_SPEED)) {
            @Override
            public void onTick(long millisUntilFinished) {
                TIME_LEFT -= 1000;
                Log.d("Time Left", String.valueOf(TIME_LEFT));
                updateCounter();
                double math = ((((double) START_TIME - TIME_LEFT) / START_TIME) * 100);
                Log.d("math", String.valueOf(math));
                progress = (int) Math.round(math);
                Log.d("Progress", String.valueOf(progress));
                simpleProgressBar.setProgress(progress);
                if (progress >= 100) {
                    TIME_LEFT = 0;
                    timerRunning = false;
                    updateCounter();
                    updateBtnStates();
                }
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                Log.d("Alarm", "Set");
                setupAlarm(getApplicationContext());
                updateBtnStates();
                simpleProgressBar.setProgress(100);
            }
        }.start();
        timerRunning = true;
        updateBtnStates();
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
        updateBtnStates();
    }

    private void resetTimer() {
        TIME_LEFT = START_TIME;
        updateCounter();
        updateBtnStates();
    }

    private void updateCounter() {
        int hours = (int) (TIME_LEFT / 1000) / 3600;
        int minutes = (int) ((TIME_LEFT / 1000) % 3600) / 60;
        int seconds = (int) (TIME_LEFT / 1000) % 60;

        String timeLeftFormatted;
        if (hours > 0) timeLeftFormatted = String.format(Locale.getDefault(),
                "%d:%02d:%02d", hours, minutes, seconds);
        else timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        countDown.setText(timeLeftFormatted);
    }

    private void updateBtnStates() {
        if (timerRunning) {
            userInput.setVisibility(View.INVISIBLE);
            setBtn.setVisibility(View.INVISIBLE);
            resetBtn.setVisibility(View.INVISIBLE);
            startAndPauseBtn.setText(R.string.Pause);
        } else {
            userInput.setVisibility(View.VISIBLE);
            setBtn.setVisibility(View.VISIBLE);
            startAndPauseBtn.setText(R.string.Start);

            if (TIME_LEFT < 1000) {
                startAndPauseBtn.setVisibility(View.INVISIBLE);
            } else {
                startAndPauseBtn.setVisibility(View.VISIBLE);
            }

            if (TIME_LEFT < START_TIME) {
                resetBtn.setVisibility(View.VISIBLE);
            } else {
                resetBtn.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onStop() {
        SharedPreferences sp = getSharedPreferences("timerOutPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putLong("START_TIME", START_TIME);
        editor.putLong("TIME_LEFT", TIME_LEFT);
        editor.putBoolean("timerRunning", timerRunning);
        editor.putFloat("TIMER_SPEED", TIMER_SPEED);
        editor.putLong("END_TIME", END_TIME);
        editor.apply();

        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("timerOutPref", MODE_PRIVATE);
        START_TIME = prefs.getLong("START_TIME", 60000);
        TIME_LEFT = prefs.getLong("TIME_LEFT", START_TIME);
        TIMER_SPEED = prefs.getFloat("TIMER_SPEED", 1f);
        timerRunning = prefs.getBoolean("timerRunning", false);

        updateCounter();
        updateBtnStates();

        if (timerRunning) {
            END_TIME = prefs.getLong("END_TIME", 0);
            TIME_LEFT = END_TIME - System.currentTimeMillis();
            if (TIME_LEFT < 0) {
                TIME_LEFT = 0;
                timerRunning = false;
                updateCounter();
                updateBtnStates();
            } else startTimer();
        }
    }

    public void setupAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        AlarmManager.AlarmClockInfo alarmManager = new AlarmManager.AlarmClockInfo(END_TIME, getStartPendingIntent(context));
        am.setAlarmClock(alarmManager, getStartPendingIntent(context));
    }
}