package com.example.uchiha.dstask;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    FloatingActionButton gallery_button;
    RecyclerView recyclerView;

    Toolbar toolbar;

    private List<String > fileNameList;
    private List<String> fileDoneList;


    private UploadAdapter uploadAdapter;

    private static final int RESULT_LOAD_IMAGE = 1;

    private StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        //custom toolbar added
        toolbar = findViewById(R.id.tool_Bar);
        setSupportActionBar(toolbar);


        recyclerView = findViewById(R.id.recycler_view);
        gallery_button = findViewById(R.id.floatingActionButton);

        fileNameList = new ArrayList<>();// array for picture name
        fileDoneList = new ArrayList<>();// uploading file

        uploadAdapter = new UploadAdapter(fileNameList, fileDoneList);


        //recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(uploadAdapter);

        //onclick of floating button to select photos from mobile storage
        gallery_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Photos"), RESULT_LOAD_IMAGE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //when file uploading request code an result code . if these code is true then our file uploading is start.

        if(requestCode==RESULT_LOAD_IMAGE && resultCode==RESULT_OK){

            //getClipData for multiple picture select
            if(data.getClipData()!=null){

                int totalSelectedItem=data.getClipData().getItemCount(); //total no. of picture selected store in totalSelectedItem

                for(int i=0;i<totalSelectedItem;i++){

                    Uri fileUri=data.getClipData().getItemAt(i).getUri();

                    String filename=getFileName(fileUri);

                    fileNameList.add(filename);

                    //uplading is in progress then filedonelist array add "uploading"
                    fileDoneList.add("uploading");

                    uploadAdapter.notifyDataSetChanged();

                    StorageReference fileToUpload = mStorageRef.child("Images").child(filename);

                    final int finalI=i;
                    fileToUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileDoneList.remove(finalI);  //remove the value stored in array fileDoneList
                            fileDoneList.add(finalI, "done");// uploading completed then add Done in array
                            uploadAdapter.notifyDataSetChanged();
                        }
                    });



                }
            }else if(data.getData()!=null){
                //if single photos is selected getData()

                Uri fileUri=data.getData();
                String filename=getFileName(fileUri);

                fileNameList.add(filename);

                fileDoneList.add("uploading");

                uploadAdapter.notifyDataSetChanged();

                StorageReference fileToUpload = mStorageRef.child("Images").child(filename);

                final int finalI=0;
                fileToUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                        fileDoneList.remove(finalI);
                        fileDoneList.add(finalI, "done");
                        uploadAdapter.notifyDataSetChanged();//Toast.makeText(MainActivity.this, "Picture is Upload Successfully", Toast.LENGTH_SHORT).show();

                    }
                });


            }
        }
    }


    //getfile name
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

}
