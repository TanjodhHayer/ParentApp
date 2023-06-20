package ca.cmpt276.parentapp.UI.ConfigChild;

import static ca.cmpt276.parentapp.UI.ConfigChild.ConfigureChildActivity.saveKids;
import static ca.cmpt276.parentapp.UI.ConfigChild.ConfigureChildActivity.saveQueue;
import static ca.cmpt276.parentapp.UI.WhoseTurn.TaskHistoryActivity.saveHistory;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import ca.cmpt276.parentapp.R;
import ca.cmpt276.parentapp.UI.WhoseTurn.TaskHistoryActivity;
import ca.cmpt276.parentapp.model.Child.Child;
import ca.cmpt276.parentapp.model.Child.ChildManager;
import ca.cmpt276.parentapp.model.Tasks.TaskHistory;
import ca.cmpt276.parentapp.model.Tasks.TaskHistoryManager;

public class EditChildActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String INDEX_NAME = "ca.cmpt276.project.UI - index";
    private EditText name;
    private String childImage;
    private String imgName;
    private ChildManager childManager;
    private int kidIndex;

    public static Intent makeIntent(Context context, int index) {
        Intent intent = new Intent(context, EditChildActivity.class);
        intent.putExtra(INDEX_NAME, index);
        return intent;
    }

    private void initItems() {
        name = findViewById(R.id.ChildNameEditText);
        childManager = ChildManager.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_child_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_and_edit_child_layout);
        setTitle("Edit Child");

        initItems();
        getIndexFromIntent();
        fillInFields();
        saveEdited();
        addImgBtn();
        takePictureBtn();
    }

    private void takePictureBtn() {
        Button button = findViewById(R.id.takePictureBtn);
        button.setOnClickListener(view -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } catch (ActivityNotFoundException e) {
                // display error state to the user
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageView childImg = findViewById(R.id.ChildImageImageView);
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            childImage = saveToInternalStorage(imageBitmap);
            childImg.setImageBitmap(imageBitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addImgBtn() {
        ActivityResultLauncher<String> getContent = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        ImageView childImg = findViewById(R.id.ChildImageImageView);
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result);
                            childImage = saveToInternalStorage(bitmap);
                            childImg.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        Button button = findViewById(R.id.addImgBtn);
        button.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(EditChildActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getContent.launch("image/*");
            } else {
                requestStoragePermission();
            }
        });
    }

    // https://www.youtube.com/watch?v=SMrB97JuIoM&ab_channel=CodinginFlow
    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", (dialog, which) -> ActivityCompat.requestPermissions(EditChildActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000))
                    .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        String randomChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            sb.append(randomChar.charAt(random.nextInt(randomChar.length())));
        }
        String fileName = sb.toString();
        File myPath = new File(directory, fileName + ".jpg");
        imgName = fileName + ".jpg";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert fos != null;
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void saveEdited() {
        Button editChildBtn = findViewById(R.id.addChildToListBtn);
        editChildBtn.setOnClickListener(view -> {
            if (!name.getText().toString().equals("")) {
                int targetChild = childManager.findTargetChild(childManager.getChildArrayList().get(kidIndex).getName());
                childManager.getQueue().get(targetChild).setName(name.getText().toString());
                childManager.getChildArrayList().get(kidIndex).setName(name.getText().toString());
                if (childImage != null) {
                    childManager.getChildArrayList().get(kidIndex).setImg(childImage);
                    childManager.getChildArrayList().get(kidIndex).setImgName(imgName);
                }
                saveQueue(EditChildActivity.this);
                saveKids(EditChildActivity.this);
                finish();
            } else {
                Toast.makeText(EditChildActivity.this, "Name Cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getIndexFromIntent() {
        Intent intent = getIntent();
        kidIndex = intent.getIntExtra(INDEX_NAME, 0);
    }

    private void fillInFields() {
        name.setText(childManager.getChildArrayList().get(kidIndex).getName());
        Button editChildBtn = findViewById(R.id.addChildToListBtn);
        editChildBtn.setText("Finish Editing");

        if (childManager.getChildArrayList().get(kidIndex).getImg() != null) {
            loadImageFromStorage(childManager.getChildArrayList().get(kidIndex).getImg(),
                    childManager.getChildArrayList().get(kidIndex).getImgName());
        }
    }

    private void loadImageFromStorage(String path, String imgName) {
        try {
            File f = new File(path, imgName);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView childImg = findViewById(R.id.ChildImageImageView);
            childImg.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.helpBtn) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditChildActivity.this);
            builder.setIcon(R.drawable.warning)
                    .setTitle("DELETE CHILD")
                    .setMessage("Are you sure you want to DELETE this kid?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        int targetChild = childManager.findTargetChild(childManager.getChildArrayList().get(kidIndex).getName());

                        TaskHistoryManager.getInstance().getTaskHistoryArrayList()
                                .removeIf(i -> i.currChild() == childManager.getChildArrayList().get(kidIndex));
                        saveHistory(EditChildActivity.this);

                        childManager.getQueue().remove(targetChild);
                        childManager.removeChild(kidIndex);
                        saveQueue(EditChildActivity.this);
                        saveKids(EditChildActivity.this);
                        Toast.makeText(EditChildActivity.this, "KID DELETED", Toast.LENGTH_SHORT).show();
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
