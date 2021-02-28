package com.pumasi.surbay.mypage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.pumasi.surbay.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class GiftDetailActivity extends AppCompatActivity {
    private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_gift_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.gray_FB)));


        ImageView imageView = findViewById(R.id.imageview);

        Intent intent = getIntent();
        File imagePath = new File(getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        uri = FileProvider.getUriForFile(GiftDetailActivity.this, "com.pumasi.surbay.fileprovider", newFile);

        if (uri != null) {
            imageView.setImageURI(uri);
//            Intent shareIntent = new Intent();
//            shareIntent.setAction(Intent.ACTION_SEND);
//            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
//            shareIntent.setDataAndType(uri, getContentResolver().getType(uri));
//            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
//            startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }

        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.gift_download_bar, menu);
        for(int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString spanString = new SpannableString(menu.getItem(i).getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(getColor(R.color.teal_200)), 0, spanString.length(), 0); //fix the color to white
            item.setTitle(spanString);
        }
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.download_bar:
                saveToInternalStorage(getContactBitmapFromURI(GiftDetailActivity.this, uri));
                Toast.makeText(GiftDetailActivity.this, "다운로드가 완료되었습니다", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public  Bitmap getContactBitmapFromURI(Context context, Uri uri) {
        try {

            InputStream input = context.getContentResolver().openInputStream(uri);
            if (input == null) {
                return null;
            }
            return BitmapFactory.decodeStream(input);
        }
        catch (FileNotFoundException e)
        {

        }
        return null;

    }

    private void saveToInternalStorage(Bitmap bitmapImage){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmapImage, "Title", null);
//        ContextWrapper cw = new ContextWrapper(getApplicationContext());
//        // path to /data/data/yourapp/app_data/imageDir
//        String root =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()+ "/SurBay";
////        String root = Environment.getExternalStorageDirectory().toString() + "/saved_images";
////         Create imageDir
//        File myDir = new File(root);
//        Boolean made = myDir.mkdirs();
//        Log.d("directory", "made"+ made);
//        String fname = System.currentTimeMillis()+"prize.png";
//        File file = new File(myDir, fname);
//        if (file.exists()) file.delete();
//        try {
//            FileOutputStream out = new FileOutputStream(file);
//            bitmapImage.compress(Bitmap.CompressFormat.PNG, 90, out);
//            out.flush();
//            out.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        MediaScannerConnection.scanFile(GiftDetailActivity.this, new String[]{file.getPath()}, new String[]{"image/jpeg"}, null);
//        return file.getAbsolutePath();
    }

}
