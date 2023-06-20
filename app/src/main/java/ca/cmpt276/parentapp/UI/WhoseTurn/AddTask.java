package ca.cmpt276.parentapp.UI.WhoseTurn;

import static ca.cmpt276.parentapp.UI.WhoseTurn.WhoseTurnActivity.saveTasks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import ca.cmpt276.parentapp.R;
import ca.cmpt276.parentapp.model.Tasks.Task;
import ca.cmpt276.parentapp.model.Tasks.TaskManager;

public class AddTask extends AppCompatActivity {
    private EditText taskDesc;

    public static Intent makeIntent(Context context) {
        return new Intent(context, AddTask.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_and_edit_task_layout);
        setTitle("Create Task");

        addItemBtn();
    }

    @Override
    protected void onStart() {
        ImageView imageView = findViewById(R.id.EditTaskChildImageView);
        imageView.setVisibility(View.INVISIBLE);

        Button button = findViewById(R.id.FinishedTask);
        button.setVisibility(View.INVISIBLE);

        TextView textView = findViewById(R.id.ChildNameTask);
        textView.setVisibility(View.INVISIBLE);

        super.onStart();
    }

    private void addItemBtn() {
        TaskManager taskManager = TaskManager.getInstance();
        taskDesc = findViewById(R.id.TaskDecEditView);
        Button button = findViewById(R.id.addTaskToList);
        button.setOnClickListener(view -> {
            if (!taskDesc.getText().toString().equals("")) {
                String TaskDesc = taskDesc.getText().toString();
                taskManager.addTask(new Task(TaskDesc));
                saveTasks(AddTask.this);
                finish();
            } else {
                Toast.makeText(AddTask.this, "Describe The task, cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.warning)
                .setTitle("Go back")
                .setMessage("Are you sure you want to close this setting without saving?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }
}
