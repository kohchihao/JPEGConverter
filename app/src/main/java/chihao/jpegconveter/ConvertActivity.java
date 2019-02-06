package chihao.jpegconveter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ConvertActivity extends AppCompatActivity {

    private Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.convert_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ImageView imageView = findViewById(R.id.imageView);

        Bundle extras = getIntent().getExtras();
        Uri selectedImage = Uri.parse(extras.getString("UriImage"));

        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Button btnConvert = findViewById(R.id.btnConvert);
        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmap != null) {
                    saveToInternalStorage(bitmap);
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Save Picture to internal storage.
     *
     * @param bitmapImage target bitmap image.
     */
    private void saveToInternalStorage(Bitmap bitmapImage) {

        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "JPEG-Converter");
        // Create imageDir
        if (!directory.exists()) {
            directory.mkdir();
        }
        String fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
        File filePath = new File(directory, fileName);

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(filePath);

            //add white background
            Bitmap bitmap = Bitmap.createBitmap(bitmapImage.getWidth(), bitmapImage.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(bitmapImage, 0, 0, null);
            canvas.save();

            //convert to jpg
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(filePath)));
            this.finish();
            Toast.makeText(this, "Convert done.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
