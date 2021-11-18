package com.example.digitalbuddy;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>  {

    private ArrayList<Message> localDataSet;
    private String userId;
    private OnRoomListener monRoomListener;
    private MediaPlayer player;
//    private String fileName;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView username;
        private final TextView msg;
        private final TextView time;
        private final ImageView playBtn;
        OnRoomListener onRoomListener;

        public ViewHolder(View view, OnRoomListener onRoomListener) {
            super(view);
            // Define click listener for the ViewHolder's View

            this.onRoomListener = onRoomListener;
            username = (TextView) view.findViewById(R.id.tv_cr_name);
            msg = (TextView) view.findViewById(R.id.tv_cr_msg);
            time = (TextView) view.findViewById(R.id.tv_cr_time);
            playBtn = (ImageView)view.findViewById(R.id.iv_btn_play_msg_audio);

            view.setOnClickListener(this);
        }

        public TextView getUsername() {
            return username;
        }
        public TextView getMsg() {
            return msg;
        }
        public TextView getTime() {
            return time;
        }

        public ImageView getPlayBtn() {
            return playBtn;
        }

        @Override
        public void onClick(View view) {
            onRoomListener.onRoomClick(getAdapterPosition());
        }
    }

    public interface OnRoomListener{
        void onRoomClick(int position);
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public MessageAdapter(ArrayList<Message> dataSet, OnRoomListener onRoomListener , String userId) {
        localDataSet = dataSet;
        this.monRoomListener = onRoomListener;
        this.userId = userId;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.message_list_rv, viewGroup, false);

        return new ViewHolder(view,monRoomListener);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        String msg = localDataSet.get(position).getMessage();

        viewHolder.getUsername().setText(localDataSet.get(position).getUserName());
        viewHolder.getMsg().setText(msg);
        viewHolder.getTime().setText(localDataSet.get(position).getTime());
        ImageView playBtn =  viewHolder.getPlayBtn();
        playBtn.setVisibility(View.GONE);
        if(msg.contains("audio-")){
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("Audio/" + msg);
            try {
                File localFile = File.createTempFile("audio", "3gp");
                Log.d("local file", "onCreate: " + localFile.getAbsolutePath().toString());
                storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.d("local file", "onCreate: " + localFile.getAbsolutePath().toString());
                        playBtn.setVisibility(View.VISIBLE);
                        playBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onPlay(true , localFile.getAbsolutePath().toString());
                            }
                        });
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(userId.equals(localDataSet.get(position).getUserId()) ){
            viewHolder.itemView.setBackgroundResource(R.drawable.speech_bubble);
        }
        else{
            viewHolder.itemView.setBackgroundResource(R.drawable.speech_bubble2);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    private void onPlay(boolean start , String nameOfFile) {
        if (start) {
            startPlaying(nameOfFile);
        } else {
            stopPlaying();
        }
    }

    private void startPlaying(String fileName) {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e("LOG_TAG", "prepare() failed");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }
}
