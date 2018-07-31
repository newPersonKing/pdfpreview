package com.gy.pdfxqr.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gy.pdfxqr.R;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MakeBitmapActivity extends AppCompatActivity {

    @BindView(R.id.save_btn)
    Button save_btn;
    @BindView(R.id.image_content)
    ImageView image_content;
    @BindView(R.id.tv_file_path)
    TextView tv_file_path;
    @BindView(R.id.select_file_btn)
    Button select_file_btn;

    private Bitmap mBitmap;
    Uri uri;
    private String absPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_bitmap);
        ButterKnife.bind(this);

        setTitle("QRScanner");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED||
                ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},200);
        }

    }
    @OnClick({
            R.id.save_btn,
            R.id.select_file_btn,
            R.id.btn_make_bitmap
    })
    public void onlick(View view){
        switch (view.getId()){
            case R.id.save_btn:
                saveBitMap();
                break;
            case R.id.select_file_btn:
                selectPdf();
                break;
            case R.id.btn_make_bitmap:
                makeBitmap();
                break;
        }
    }

    private void saveBitMap(){
        File res=new File(absPath);
        String fileName=res.getName();
        String savePath= Environment.getExternalStorageDirectory()+"/saveqcr/";
        File saveDir=new File(savePath);
        if (!saveDir.exists()){
           boolean is= saveDir.mkdirs();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(savePath+"/"+fileName+".jpg");
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            image_content.setVisibility(View.GONE);
            save_btn.setVisibility(View.GONE);
            tv_file_path.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makeBitmap(){
        String textContent = tv_file_path.getText().toString();
        if (TextUtils.isEmpty(textContent)) {
            Toast.makeText(this, "请先选择pdf", Toast.LENGTH_SHORT).show();
            return;
        }
        image_content.setVisibility(View.VISIBLE);
        save_btn.setVisibility(View.VISIBLE);
        mBitmap = CodeUtils.createImage(textContent, 400, 400, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        image_content.setImageBitmap(mBitmap);
        save_btn.setVisibility(View.VISIBLE);
    }

    private void selectPdf(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        try {
            startActivityForResult(intent, 200);
        } catch (ActivityNotFoundException e) {
            //alert user that file manager not working
            Toast.makeText(this, R.string.toast_pick_file_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            uri = data.getData();
            absPath=getRealPathFromURI(uri);
            if (!absPath.endsWith(".pdf")){
                Toast.makeText(this,"你选择的不是pdf",Toast.LENGTH_SHORT).show();
                return;
            }
            tv_file_path.setText(absPath);
        }
    }

    public  String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI,
                new String[]{MediaStore.Images.ImageColumns.DATA},//
                null, null, null);
        if (cursor == null) result = contentURI.getPath();
        else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();
        }
        return result;
    }
}
