package com.gy.pdfxqr.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.gy.pdfxqr.R;
import com.gy.pdfxqr.adapter.MyAdapter;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PDFViewActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener,
        OnPageErrorListener {

    @BindView(R.id.pdfView)
    PDFView pdfView;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    private final static int REQUEST_CODE = 42;
    public static final int PERMISSION_CODE = 42042;

    private List<File> datas=new ArrayList<>();

    String pdfFileName;

    Uri uri;

    Integer pageNumber = 0;

    public static final String SAMPLE_FILE = "sample.pdf";

    private String filePath;

    private MyAdapter adapter;

    private File deleteFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview);
        ButterKnife.bind(this);

        filePath= getIntent().getStringExtra("path");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        createDialong();
        adapter=new MyAdapter(this, new MyAdapter.CallBack() {
            @Override
            public void onClick(File file) {
                filePath=file.getAbsolutePath();
                launchPicker();
            }

            @Override
            public void onLongClick(File file) {
                deleteFile=file;
                dialog.show();
            }
        });
        recyclerView.setAdapter(adapter);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_CODE);
        }else {
            launchPicker();
            setData();
        }
    }


    private void setData(){
        File file=new File(filePath);
        final File parent=file.getParentFile();
        File[] files=parent.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.getName().endsWith(".pdf")){
                    return true;
                }
                return false;
            }
        });
      for (File child:files){
          datas.add(child);
      }
       adapter.setFiles(datas);
    }

    private void launchPicker(){
         pdfView.setVisibility(View.VISIBLE);
        pdfView.setBackgroundColor(Color.LTGRAY);
        displayFromFile();
        setTitle(pdfFileName);
    }


    private void displayFromFile() {
        File file=new File(filePath);
        pdfFileName = file.getName();
        pdfView.fromFile(file)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError(this)
                .pageFitPolicy(FitPolicy.BOTH)
                .load();
    }

    private void displayFromAsset(String assetFileName) {
        pdfFileName = assetFileName;
        pdfView.fromAsset(SAMPLE_FILE)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError(this)
                .pageFitPolicy(FitPolicy.BOTH)
                .load();
    }

    private void displayFromUri(Uri uri) {
        pdfFileName = getFileName(uri);
        pdfView.fromUri(uri)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError(this)
                .load();
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            uri = data.getData();
            displayFromUri(uri);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchPicker();
                setData();
            }
        }
    }

    /*回掉方法*/
    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
//        Log.e(TAG, "title = " + meta.getTitle());
//        Log.e(TAG, "author = " + meta.getAuthor());
//        Log.e(TAG, "subject = " + meta.getSubject());
//        Log.e(TAG, "keywords = " + meta.getKeywords());
//        Log.e(TAG, "creator = " + meta.getCreator());
//        Log.e(TAG, "producer = " + meta.getProducer());
//        Log.e(TAG, "creationDate = " + meta.getCreationDate());
//        Log.e(TAG, "modDate = " + meta.getModDate());
        printBookmarksTree(pdfView.getTableOfContents(), "-");
    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }

    @Override
    public void onPageError(int page, Throwable t) {

    }

    @Override
    public void onBackPressed() {
        /*gone 8 visible 0*/
        int visible=pdfView.getVisibility();
        if (visible==0){
            pdfView.setVisibility(View.GONE);
            setTitle("pdf列表");
        }else {
            finish();
        }
    }
    AlertDialog dialog;
    private void createDialong(){
         dialog=new  AlertDialog.Builder(this).setMessage("确定删除吗")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                         if (deleteFile.exists()){
                             String name=deleteFile.getName();
                             /*对应二维码路径*/
                             String savePath= Environment.getExternalStorageDirectory()+"/saveqcr/"+name+".jpg";
                             deleteFile.delete();
                             File qcr=new File(savePath);
                             if (qcr.exists()){
                                 qcr.delete();
                             }
                             setData();
                         }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
    }
}
