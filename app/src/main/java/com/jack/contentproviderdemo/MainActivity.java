package com.jack.contentproviderdemo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int READ_REQUEST = 42;
    private Button btn_open_pickerUI;
    private Button btn_show_image;
    private ImageView tv_image;
    Uri uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        btn_open_pickerUI.setOnClickListener(this);
        btn_show_image.setOnClickListener(this);
    }

    private void initView() {
        btn_open_pickerUI = (Button) findViewById(R.id.btn_open_pickerUI);
        btn_show_image = (Button) findViewById(R.id.btn_show_image);
        tv_image = (ImageView) findViewById(R.id.tv_image);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_open_pickerUI) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            //开启剪切
            intent.putExtra("crop", "true");
            //剪切的宽高比为1:2
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 2);
            intent.putExtra("outputX", 20);
            intent.putExtra("outputY", 40);
            intent.putExtra("output", Uri.fromFile(new File("/mnt/sdcard/temp"))); // 保存路径
            intent.putExtra("outputFormat", "JPEG");// 返回格式
            startActivityForResult(intent, READ_REQUEST);
        }
        if (id == R.id.btn_show_image) {
            try {
                tv_image.setImageBitmap(getBitmapFromUri(uri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void dumpImageMetaData(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.e("jack", "Display Name:" + displayName);
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                String size = null;
                if (!cursor.isNull(sizeIndex)) {
                    size = cursor.getString(sizeIndex);
                } else {
                    size = "Unknown";
                }
                Log.e("HeHe", "Size: " + size);
            }
        } finally {
            cursor.close();
        }
    }

    //获取Bitmap图像
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    //    从内置的文件存储返回相片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                uri = data.getData();
                Log.e("jack", "Uri:" + uri.toString());
                dumpImageMetaData(uri);
            }
        }
    }
}
