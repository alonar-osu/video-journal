package com.example.android.videojournal;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import static android.content.ContentValues.TAG;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private static final String TAG = CustomAdapter.class.getSimpleName();

    ArrayList dinoImages;
    Context context;

    public CustomAdapter(Context context, ArrayList dinoImages) {
       this.context = context;
       this.dinoImages = dinoImages;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Log.d(TAG, "#" + position);
       // holder.image.setImageResource(galleryImages.get(position));
       // holder.image.setImageResource(R.drawable.dino1);
        holder.image.setImageResource((int) dinoImages.get(position));
       // holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return dinoImages.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }

        /*
        void bind (int position) {
           // image.setImageResource(galleryImages.get(position));
            image.setImageResource(dinoImages.get(position));
        }
        */
    }
}
