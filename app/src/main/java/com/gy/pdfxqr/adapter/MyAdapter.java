package com.gy.pdfxqr.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gy.pdfxqr.R;
import com.gy.pdfxqr.untils.ScreenUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<File> files=new ArrayList<>();
    private Context context;

    private CallBack callBack;

    public MyAdapter(Context context,CallBack callBack){
        this.context=context;
        this.callBack=callBack;
    }
    public void setFiles(List<File> files){
        this.files=files;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=View.inflate(parent.getContext(), R.layout.layout_pdf_name,null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        ViewGroup.LayoutParams params=holder.pdf_name.getLayoutParams();
        params.width= ScreenUtils.getScreenWidth(context);
        holder.pdf_name.setText(files.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.onClick(files.get(position));
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                callBack.onLongClick(files.get(position));
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView pdf_name;

        public MyViewHolder(View itemView) {
            super(itemView);
            pdf_name=itemView.findViewById(R.id.pdf_name);
        }
    }

    public interface CallBack{
       void onClick(File file);
       void onLongClick(File file);
    }
}
