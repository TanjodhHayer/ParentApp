package ca.cmpt276.parentapp.UI.WhoseTurn;

import static ca.cmpt276.parentapp.UI.WhoseTurn.TaskHistoryActivity.loadSavedTaskHistory;
import static ca.cmpt276.parentapp.UI.WhoseTurn.TaskHistoryActivity.saveHistory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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

import ca.cmpt276.parentapp.R;
import ca.cmpt276.parentapp.model.Child.Child;
import ca.cmpt276.parentapp.model.Child.ChildManager;
import ca.cmpt276.parentapp.model.Tasks.Task;
import ca.cmpt276.parentapp.model.Tasks.TaskHistory;
import ca.cmpt276.parentapp.model.Tasks.TaskHistoryManager;
import ca.cmpt276.parentapp.model.Tasks.TaskManager;

public class WhoseTurnActivity extends AppCompatActivity {
    private static final TaskManager taskManager = TaskManager.getInstance();
    private final TaskHistoryManager taskHistoryManager = TaskHistoryManager.getInstance();
    private TextView NoTaskTextView;

    public static Intent makeIntent(Context context) {
        return new Intent(context, WhoseTurnActivity.class);
    }

    public static ArrayList<Task> loadSavedTasks(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("SavedTasks", "");
        Type type = new TypeToken<ArrayList<Task>>() {

        }.getType();
        return gson.fromJson(json, type);
    }

    public static void saveTasks(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(taskManager.getTaskArrayList());
        editor.putString("SavedTasks", json);
        editor.apply();
    }

    private void initItems() {
        taskManager.setTaskArrayList(loadSavedTasks(WhoseTurnActivity.this));
        taskHistoryManager.setTaskHistoryArrayList(loadSavedTaskHistory(WhoseTurnActivity.this));
        NoTaskTextView = findViewById(R.id.NoTaskTextView);
    }

    @Override
    protected void onStop() {
        saveTasks(WhoseTurnActivity.this);
        saveHistory(WhoseTurnActivity.this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        saveTasks(WhoseTurnActivity.this);
        saveHistory(WhoseTurnActivity.this);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        initItems();
        if (taskManager.getTaskArrayList().isEmpty())
            NoTaskTextView.setVisibility(View.VISIBLE);
        else
            NoTaskTextView.setVisibility(View.INVISIBLE);
        listAllTasks();
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whose_turn);
        setTitle("Whose Turn");

        initItems();
        listAllTasks();
        itemClick();
        addTaskBtn();
    }

    private void listAllTasks() {
        ArrayAdapter<Task> adapter = new adapter();
        ListView list = findViewById(R.id.TaskListView);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void itemClick() {
        ListView listView = findViewById(R.id.TaskListView);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = EditTask.makeIntent(WhoseTurnActivity.this, i);
            startActivity(intent);
        });
    }

    private void addTaskBtn() {
        Button button = findViewById(R.id.AddTaskBtn);
        button.setOnClickListener(view -> {
            if (ChildManager.getInstance().getChildArrayList().isEmpty()) {
                Toast.makeText(WhoseTurnActivity.this, "Please add a Child", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = AddTask.makeIntent(WhoseTurnActivity.this);
                startActivity(intent);
            }
        });
    }

    public class adapter extends ArrayAdapter<Task> {
        public adapter() {
            super(WhoseTurnActivity.this, R.layout.task_list_layout, taskManager.getTaskArrayList());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null)
                itemView = getLayoutInflater().inflate(R.layout.task_list_layout, parent, false);

            ImageView childImage = itemView.findViewById(R.id.ChildimageViewTaskList);
            Task CurrTask = taskManager.getTaskArrayList().get(position);
            Child currKid = CurrTask.currChild();

            TextView txt = itemView.findViewById(R.id.TaskDes);
            txt.setText(CurrTask.toString());

            if (currKid == null) {
                return itemView;
            } else {
                Button button = itemView.findViewById(R.id.DoneBtn);
                button.setOnClickListener(view -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");
                    String formatedDate = LocalDateTime.now().format(formatter);
                    taskHistoryManager.addTaskHistory(new TaskHistory(CurrTask.getIndex(),
                            CurrTask.getTaskDesc(), formatedDate));
                    saveHistory(WhoseTurnActivity.this);
                    CurrTask.taskDone();
                    txt.setText(CurrTask.toString());
                    Child newKid = CurrTask.currChild();
                    if (newKid.getImg() != null && newKid.getImg() != "") {
                        try {
                            File f = new File(newKid.getImg(), newKid.getImgName());
                            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                            childImage.setImageBitmap(b);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        childImage.setImageResource(R.drawable.childimg);
                    }
                });

                Button currHistoryBtn = itemView.findViewById(R.id.TaskHistoryBtn);
                currHistoryBtn.setOnClickListener(view -> {
                    if (ChildManager.getInstance().getChildArrayList().isEmpty()) {
                        Toast.makeText(WhoseTurnActivity.this, "Please add a Child", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = TaskHistoryActivity.makeIntent(WhoseTurnActivity.this, CurrTask.getTaskDesc());
                        startActivity(intent);
                    }
                });

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
}