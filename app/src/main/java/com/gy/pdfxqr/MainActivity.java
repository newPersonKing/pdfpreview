package com.gy.pdfxqr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gy.pdfxqr.activity.MakeBitmapActivity;
import com.gy.pdfxqr.activity.PDFViewActivity;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    /**
     * 扫描跳转Activity RequestCode
     */
    public static final int REQUEST_CODE = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setTitle("QRScanner");
    }
    @OnClick({
            R.id.btn_scan,
    })
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_scan:
                if (checkPermission()){
                    Intent intent = new Intent(getApplication(), CaptureActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);
                }
                break;
        }
    }

    private boolean checkPermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat. requestPermissions(this,new String[]{Manifest.permission.CAMERA},300);
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add:
                Intent intent1=new Intent(this, MakeBitmapActivity.class);
                startActivity(intent1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    if (result.endsWith(".pdf")){
                        File pdf=new File(result);
                        if (!pdf.exists()){
                            Toast.makeText(this,"文件不存在",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent=new Intent(this, PDFViewActivity.class);
                        intent.putExtra("path",result);
                        startActivity(intent);
                    }else {
                        Toast.makeText(this,"扫描正确的二维码",Toast.LENGTH_SHORT).show();
                    }
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
