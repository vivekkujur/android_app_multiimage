package com.example.uchiha.dstask;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.ViewHolder>{


    public List<String> fileNameList;// array for picture name
    public List<String> fileDoneList;//array for progress update

    //constructor UploadAdapter
    public UploadAdapter (List<String> fileNameList,List<String> fileDoneList){

        this.fileNameList=fileNameList;
        this.fileDoneList = fileDoneList;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_items,parent,false);
       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String filename =fileNameList.get(position);
        holder.nameView.setText(filename);

        String fileDone = fileDoneList.get(position);
        //if fileDoneView array have uploading value then run progress bar.
        if(fileDone.equals("uploading")){

            for(int i=0;i<100;i++){
                holder.fileDoneView.setVisibility(View.VISIBLE);
                holder.fileDoneView.setProgress(i);

            }

        } else { //when process is complete then mark checked . uploading
            holder.fileDoneView.setVisibility(View.GONE);
            holder.uploading_done.setImageResource(R.mipmap.checked);

        }

    }


    @Override
    public int getItemCount() {
        return fileNameList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        View hView;
        public TextView nameView;
        public ProgressBar fileDoneView;
        public ImageView uploading_done;


        public ViewHolder(View itemView) {
            super(itemView);

            hView=itemView;

            nameView=hView.findViewById(R.id.upload_filename);
            fileDoneView = hView.findViewById(R.id.uploading_progressbar);
            uploading_done=hView.findViewById(R.id.upload_loading);

        }
    }


}
