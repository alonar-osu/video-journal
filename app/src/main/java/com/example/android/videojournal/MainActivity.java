package com.example.android.videojournal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import android.support.v7.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    static final int REQUEST_VIDEO_CAPTURE = 1;
    ArrayList dinoImages = new ArrayList<>(Arrays.asList(R.drawable.dino1, R.drawable.dino2, R.drawable.dino3, R.drawable.dino4,
            R.drawable.dino5, R.drawable.dino7, R.drawable.dino8, R.drawable.dino9, R.drawable.dino10, R.drawable.dino11,
            R.drawable.dino12, R.drawable.dino13, R.drawable.dino14, R.drawable.dino15, R.drawable.dino16, R.drawable.dino17,
            R.drawable.dino18, R.drawable.dino19, R.drawable.dino20));

    VideoView mOneVideoView;
    MediaController mMediaControls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d(TAG, "in onCreate");
        //ArrayList<String> galleryImages = getAllShownImagesPath(MainActivity.this);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        CustomAdapter customAdapter = new CustomAdapter(MainActivity.this, dinoImages);
        recyclerView.setAdapter(customAdapter);



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // on click action
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                }
            }
        });
    }
    /*
    // get image Uris for displaying
    private ArrayList<String> getAllShownImagesPath(Activity activity) {
        Log.d(TAG, "at start of get - ImagesPath()");
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Log.d(TAG, "after getting uri - ImagesPath()");

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        Log.d(TAG, "after projection - ImagesPath()");

        cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        Log.d(TAG, "after cursor - ImagesPath()");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        //column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        Log.d(TAG, "after getcolumnindex - ImagesPath()");

        try {
            Log.d(TAG, "before while - ImagesPath()");
           // while (cursor.moveToNext()) {
            cursor.moveToFirst();
                Log.d(TAG, "INSIDE WHILE - ImagesPath()");
                absolutePathOfImage = cursor.getString(column_index_data);
                Log.d(TAG, absolutePathOfImage);
                listOfAllImages.add(absolutePathOfImage);
          //  }
        } finally {
            cursor.close();
        }
        // while (cursor.moveToNext()) {
                return listOfAllImages;
    }
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = intent.getData();
            Toast.makeText(getApplicationContext(),
                    ""+ videoUri,
                    Toast.LENGTH_LONG).show();
            SQLiteDBHelper sqliteDB = new SQLiteDBHelper(MainActivity.this);
            if (videoUri != null) {
                sqliteDB.saveToDB(MainActivity.this, videoUri);
            }

           // showVideo(videoUri);
        }
    }
/**
    private void showVideo(Uri videoUri) {
        mOneVideoView = (VideoView) findViewById(R.id.oneVideoView);

        if (mMediaControls == null) {
            mMediaControls = new MediaController(MainActivity.this);
            mMediaControls.setAnchorView(mOneVideoView);
        }

        mOneVideoView.setMediaController(mMediaControls);
        mOneVideoView.setVideoURI(videoUri);
        mOneVideoView.start();
    }
*/

}
