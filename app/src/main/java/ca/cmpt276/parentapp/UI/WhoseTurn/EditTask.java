package ca.cmpt276.parentapp.UI.WhoseTurn;


import static ca.cmpt276.parentapp.UI.WhoseTurn.WhoseTurnActivity.saveTasks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import ca.cmpt276.parentapp.R;
import ca.cmpt276.parentapp.model.Child.Child;
import ca.cmpt276.parentapp.model.Tasks.TaskManager;

public class EditTask extends AppCompatActivity {
    private static final String INDEX_NAME = "ca.cmpt276.project.UI.WhoseTurn - index";
    private EditText taskDesc;
    private TaskManager taskManager;
    private int taskIndex;

    public static Intent makeIntent(Context context, int index) {
        Intent intent = new Intent(context, EditTask.class);
        intent.putExtra(INDEX_NAME, index);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_child_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initItems() {
        taskDesc = findViewById(R.id.TaskDecEditView);
        taskManager = TaskManager.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_and_edit_task_layout);
        setTitle("Edit Task");


        initItems();
        getIndexFromIntent();
        fillInFields();
        saveEdited();
        finishBtnClick();
    }

    private void finishBtnClick() {
        Button button = findViewById(R.id.FinishedTask);
        ImageView imageView = findViewById(R.id.EditTaskChildImageView);

        button.setOnClickListener(view -> {
            String currChildName = taskManager.getTaskArrayList().get(taskIndex).currChild().getName();
            Toast.makeText(EditTask.this, currChildName + " Finished Task!", Toast.LENGTH_SHORT).show();
            taskManager.getTaskArrayList().get(taskIndex).taskDone();
            Child CurrChild = taskManager.getTaskArrayList().get(taskIndex).currChild();
            TextView textView = findViewById(R.id.ChildNameTask);
            textView.setText("Child Name: " + CurrChild.getName());
            if (CurrChild.getImg() != null && CurrChild.getImg() != "") {
                try {
                    File f = new File(CurrChild.getImg(), CurrChild.getImgName());
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                    imageView.setImageBitmap(b);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                imageView.setImageResource(R.drawable.childimg);
            }
        });
    }

    private void saveEdited() {
        Button editTaskBtn = findViewById(R.id.addTaskToList);
        editTaskBtn.setOnClickListener(view -> {
            if (!taskDesc.getText().toString().equals("")) {
                taskManager.getTaskArrayList().get(taskIndex).setTaskDesc(taskDesc.getText().toString());
                saveTasks(EditTask.this);
                finish();
            } else {
                Toast.makeText(EditTask.this, "Task Cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillInFields() {
        Button button = findViewById(R.id.addTaskToList);
        button.setText("Finish Editing");
        taskDesc.setText(taskManager.getTaskArrayList().get(taskIndex).getTaskDesc());
        ImageView imageView = findViewById(R.id.EditTaskChildImageView);
        Child CurrChild = taskManager.getTaskArrayList().get(taskIndex).currChild();
        if (CurrChild != null) {
            TextView textView = findViewById(R.id.ChildNameTask);
            textView.setText("Child Name: " + CurrChild.getName());

            if (CurrChild.getImg() != null && CurrChild.getImg() != "") {
                try {
                    File f = new File(CurrChild.getImg(), CurrChild.getImgName());
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                    imageView.setImageBitmap(b);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                imageView.setImageResource(R.drawable.childimg);
            }
        }
    }

    private void getIndexFromIntent() {
        Intent intent = getIntent();
        taskIndex = intent.getIntExtra(INDEX_NAME, 0);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.helpBtn) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditTask.this);
            builder.setIcon(R.drawable.warning)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to DELETE this TASK?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        taskManager.removeTask(taskIndex);
                        saveTasks(EditTask.this);
                        Toast.makeText(EditTask.this, "TASK DELETED", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.warning)
                .setTitle("Go Back")
                .setMessage("Are you sure you want to close this setting without saving?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }
}
