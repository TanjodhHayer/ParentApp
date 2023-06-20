package ca.cmpt276.parentapp;

import static ca.cmpt276.parentapp.UI.ConfigChild.ConfigureChildActivity.loadSavedKids;
import static ca.cmpt276.parentapp.UI.ConfigChild.ConfigureChildActivity.loadSavedQueue;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import ca.cmpt276.parentapp.UI.ConfigChild.ConfigureChildActivity;
import ca.cmpt276.parentapp.UI.FlipCoin.FlipCoinActivity;
import ca.cmpt276.parentapp.UI.Help.HelpActivity;
import ca.cmpt276.parentapp.UI.TakeBreath.TakeBreathActivity;
import ca.cmpt276.parentapp.UI.TimeoutTimer.TimeoutTimerActivity;
import ca.cmpt276.parentapp.UI.WhoseTurn.WhoseTurnActivity;
import ca.cmpt276.parentapp.model.Child.ChildManager;

public class MainActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initChildList();
        FlipCoinBtn();
        TimeoutTimerBtn();
        ConfigChildBtn();
        WhoseTurnBtn();
        TakeBreathBtn();
    }

    private void initChildList() {
        ChildManager childManager = ChildManager.getInstance();
        childManager.setChild(loadSavedKids(MainActivity.this));
        childManager.setQueue(loadSavedQueue(MainActivity.this));
    }

    private void TakeBreathBtn() {
        Button button = findViewById(R.id.BreathBtnMainMenu);
        button.setOnClickListener(view -> {
            Intent intent = TakeBreathActivity.makeIntent(MainActivity.this);
            startActivity(intent);
        });
    }

    private void FlipCoinBtn() {
        Button button = findViewById(R.id.FlipBtn);
        button.setOnClickListener(view -> {
            Intent intent = FlipCoinActivity.makeIntent(MainActivity.this);
            startActivity(intent);
        });
    }

    private void TimeoutTimerBtn() {
        Button button = findViewById(R.id.TimeoutBtn);
        button.setOnClickListener(view -> {
            Intent intent = TimeoutTimerActivity.makeIntent(MainActivity.this);
            startActivity(intent);
        });
    }

    private void ConfigChildBtn() {
        Button button = findViewById(R.id.ConfigChildBtn);
        button.setOnClickListener(view -> {
            Intent intent = ConfigureChildActivity.makeIntent(MainActivity.this);
            startActivity(intent);
        });
    }

    private void WhoseTurnBtn() {
        Button button = findViewById(R.id.WhoseTurnBtn);
        button.setOnClickListener(view -> {
            Intent intent = WhoseTurnActivity.makeIntent(MainActivity.this);
            startActivity(intent);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.helpBtn) {
            Intent intent = HelpActivity.makeIntent(MainActivity.this);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}