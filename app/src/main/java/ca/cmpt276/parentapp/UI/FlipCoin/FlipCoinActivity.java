package ca.cmpt276.parentapp.UI.FlipCoin;

import static ca.cmpt276.parentapp.UI.ConfigChild.ConfigureChildActivity.saveQueue;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

import ca.cmpt276.parentapp.R;
import ca.cmpt276.parentapp.model.Child.Child;
import ca.cmpt276.parentapp.model.Child.ChildManager;
import ca.cmpt276.parentapp.model.Coin.Coin;
import ca.cmpt276.parentapp.model.Coin.CoinManager;

public class FlipCoinActivity extends AppCompatActivity {
    private static int childIndex;
    private static int lastChildIndex;
    private static String GlobalChild;
    private ChildManager childManager;
    private CoinManager coinManager;
    private ImageView coinImage;
    private int PlayerChoice;
    private ListView list;

    public static Intent makeIntent(Context context) {
        return new Intent(context, FlipCoinActivity.class);
    }

    public static ArrayList<Coin> loadSavedFlips(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPrefs.getString("SavedFlips", "");
        Type type = new TypeToken<ArrayList<Coin>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static void saveFlip(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(CoinManager.getInstance().getCoinHistory());
        editor.putString("SavedFlips", json);
        editor.putString("GlobalChild", GlobalChild);
        editor.apply();
    }

    private void initItems() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(FlipCoinActivity.this);
        sharedPrefs.getString(GlobalChild, null);
        childManager = ChildManager.getInstance();
        coinManager = CoinManager.getInstance();
        coinManager.setCoinHistoryArrayList(loadSavedFlips(FlipCoinActivity.this));
        coinImage = findViewById(R.id.coinImage);
    }

    @Override
    protected void onStart() {
        initItems();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        saveFlip(FlipCoinActivity.this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip_coin);
        setTitle("Flip Coin");

        initItems();
        PlayerPickBtn();
        pickKid();
        flipHistoryBtn();
        noChildernCase();
        listAllKids();
    }

    private void noChildernCase() {
        Button flipBtn = findViewById(R.id.Flip);
        flipBtn.setOnClickListener(view -> {
            FlipBtn();
        });
    }


    @Override
    protected void onStop() {
        saveQueue(FlipCoinActivity.this);
        super.onStop();
    }

    private void flipHistoryBtn() {
        Button FlipBtn = findViewById(R.id.FlipHistory);
        FlipBtn.setOnClickListener(view -> {
            Intent intent = FlipHistory.makeIntent(FlipCoinActivity.this);
            startActivity(intent);
        });
    }

    private void pickKid() {
        Spinner dropdown = findViewById(R.id.pickchild);
        ArrayList<String> items = new ArrayList<>();
        ArrayList<Child> ChildArray = childManager.getChildArrayList();
        if (ChildArray.size() == 0) {
            items.add("NO CHILDREN");
        } else {
            items.add("NO CHILD SELECTED");
            for (Child i : ChildArray) {
                items.add(i.getName());
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(FlipCoinActivity.this, R.layout.simple_spinner_dropdown, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).toString() == "NO CHILDREN" || adapterView.getItemAtPosition(i).toString() == "NO CHILD SELECTED") {
                    childIndex = -1;


                    TextView ChildName = findViewById(R.id.CurrChildtextView);
                    ChildName.setText("Current Child: None");

                } else {
                    childIndex = childManager.findChildIndex(adapterView.getItemAtPosition(i).toString());
                    TextView ChildName = findViewById(R.id.CurrChildtextView);
                    ChildName.setText(String.format("Current Child: %s", childManager.getChildArrayList().get(childIndex).getName()));
                    lastChildIndex = childIndex;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                childIndex = -1;
            }
        });
    }

    private void FlipBtn() {
        int pick = new Random().nextInt(2);

        if (pick == 1) coinImage.setImageResource(R.drawable.head);
        else coinImage.setImageResource(R.drawable.tail);
        RotateAnimation rotate = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF,
                0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        MediaPlayer mp = MediaPlayer.create(FlipCoinActivity.this, R.raw.flip_sound);
        mp.start();
        coinImage.startAnimation(rotate);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.now();
        String formattedDateTime = dateTime.format(formatter);

        Coin newCoin = new Coin(formattedDateTime, childIndex, PlayerChoice, PlayerChoice == pick);
        Toast.makeText(FlipCoinActivity.this, newCoin + "", Toast.LENGTH_SHORT).show();
        coinManager.addCoinHistory(newCoin);
        saveFlip(FlipCoinActivity.this);
    }

    private void PlayerPickBtn() {
        Button head = findViewById(R.id.Head);
        head.setOnClickListener(view -> {
            PlayerChoice = 0;
            Toast.makeText(FlipCoinActivity.this, "Player Choice: Head", Toast.LENGTH_SHORT).show();
        });

        Button tail = findViewById(R.id.Tail);
        tail.setOnClickListener(view -> {
            PlayerChoice = 1;
            Toast.makeText(FlipCoinActivity.this, "Player Choice: Tail", Toast.LENGTH_SHORT).show();
        });
    }

    private void listAllKids() {
        if (childManager.getQueue() == null) return;
        ArrayAdapter<Child> adapter = new adapter();
        list = findViewById(R.id.CircularArrayList);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public class adapter extends ArrayAdapter<Child> {
        public adapter() {
            super(FlipCoinActivity.this, R.layout.child_list_layout, childManager.getQueue());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null)
                itemView = getLayoutInflater().inflate(R.layout.child_list_layout, parent, false);

            Child currKid = childManager.getQueue().get(position);
            TextView txt = itemView.findViewById(R.id.childName);
            txt.setText(currKid.getName());

            ImageView childImage = itemView.findViewById(R.id.ChildImageList);
            if (currKid.getImg() != null && currKid.getImg() != "") {
                try {
                    File f = new File(currKid.getImg(), currKid.getImgName());
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                    childImage.setImageBitmap(b);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                childImage.setImageResource(R.drawable.childimg);
            }

            Button FlipBtn = findViewById(R.id.Flip);
            FlipBtn.setOnClickListener(view -> {
                if (childIndex == -1) {
                    FlipBtn();
                } else {
                    Child getChild = childManager.findChildByIndex(childIndex);
                    int CorrectIndex = 0;
                    for (Child i : childManager.getQueue()) {
                        if (i.getName() == getChild.getName()) {
                            break;
                        }
                        CorrectIndex++;
                    }

                    if (CorrectIndex == childManager.getQueue().size() - 1) {
                        FlipBtn();
                    } else if (childIndex != -1 && GlobalChild != getChild.getName()) {
                        FlipBtn();
                        int queueChildIndex = childManager.findTargetChild(getChild.getName());
                        GlobalChild = getChild.getName();
                        childManager.addToQueue(getChild);
                        childManager.removeQueue(queueChildIndex);
                        listAllKids();
                    } else {
                        FlipBtn();
                    }
                }
            });
            return itemView;
        }
    }
}
