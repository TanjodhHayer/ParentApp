package ca.cmpt276.parentapp.UI.Help;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ca.cmpt276.parentapp.R;

public class HelpActivity extends AppCompatActivity {

    public static Intent makeIntent(Context context) {
        return new Intent(context, HelpActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        setTitle("Help");

        TextView CMPT276 = findViewById(R.id.help_CMPT276_link);
        CMPT276.setMovementMethod(LinkMovementMethod.getInstance());

        TextView Main_Background = findViewById(R.id.help_CR_main_background);
        Main_Background.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
