package ca.cmpt276.parentapp.UI.ConfigChild;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;

import ca.cmpt276.parentapp.R;
import ca.cmpt276.parentapp.model.Child.Child;
import ca.cmpt276.parentapp.model.Child.ChildManager;

public class ConfigureChildActivity extends AppCompatActivity {
    ChildManager childManager = ChildManager.getInstance();
    private TextView NoChildTextView;

    public static Intent makeIntent(Context context) {
        return new Intent(context, ConfigureChildActivity.class);
    }

    public static ArrayList<Child> loadSavedKids(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        // https://stackoverflow.com/questions/22533432/create-object-from-gson-string-doesnt-work
        Gson gson = new GsonBuilder().registerTypeAdapter(Uri.class, new UriAdapter()).create();
        String json = sharedPrefs.getString("SavedKids", "");
        Type type = new TypeToken<ArrayList<Child>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static LinkedList<Child> loadSavedQueue(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        // https://stackoverflow.com/questions/22533432/create-object-from-gson-string-doesnt-work
        Gson gson = new GsonBuilder().registerTypeAdapter(Uri.class, new UriAdapter()).create();
        String json = sharedPrefs.getString("SavedQueue", "");
        Type type = new TypeToken<LinkedList<Child>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static void saveKids(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new GsonBuilder().registerTypeAdapter(Uri.class, new UriAdapter()).create();
        String json = gson.toJson(ChildManager.getInstance().getChildArrayList());
        editor.putString("SavedKids", json);
        editor.apply();
    }

    public static void saveQueue(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new GsonBuilder().registerTypeAdapter(Uri.class, new UriAdapter()).create();
        String json = gson.toJson(ChildManager.getInstance().getQueue());
        editor.putString("SavedQueue", json);
        editor.apply();
    }


    private void initItems() {
        childManager.setChild(loadSavedKids(ConfigureChildActivity.this));
        childManager.setQueue(loadSavedQueue(ConfigureChildActivity.this));
        NoChildTextView = findViewById(R.id.NoChildTextView);
    }

    @Override
    protected void onStart() {
        initItems();
        if (childManager.getChildArrayList().isEmpty())
            NoChildTextView.setVisibility(View.VISIBLE);
        else
            NoChildTextView.setVisibility(View.INVISIBLE);
        listAllKids();
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_child);
        setTitle("Configure Child");

        initItems();
        listAllKids();
        itemClick();
        addChildBtn();
    }

    private void itemClick() {
        ListView listView = findViewById(R.id.ChildListView);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = EditChildActivity.makeIntent(ConfigureChildActivity.this, i);
            startActivity(intent);
        });
    }

    private void addChildBtn() {
        Button button = findViewById(R.id.addChildToListBtn);
        button.setOnClickListener(view -> {
            Intent intent = AddChildActivity.makeIntent(ConfigureChildActivity.this);
            startActivity(intent);
        });
    }

    private void listAllKids() {
        ArrayAdapter<Child> adapter = new adapter();
        ListView list = findViewById(R.id.ChildListView);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        saveKids(ConfigureChildActivity.this);
        super.onDestroy();
    }

    public static final class UriAdapter extends TypeAdapter<Uri> {
        @Override
        public void write(JsonWriter out, Uri uri) throws IOException {
            out.value(uri.toString());
        }

        @Override
        public Uri read(JsonReader in) throws IOException {
            return Uri.parse(in.nextString());
        }
    }

    public class adapter extends ArrayAdapter<Child> {
        public adapter() {
            super(ConfigureChildActivity.this, R.layout.child_list_layout, childManager.getChildArrayList());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null)
                itemView = getLayoutInflater().inflate(R.layout.child_list_layout, parent, false);

            Child currKid = childManager.getChildArrayList().get(position);
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

            return itemView;
        }
    }
}