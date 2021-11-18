package com.example.digitalbuddy;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>  {

    private ArrayList<Room> localDataSet;
    private OnRoomListener monRoomListener;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView textView;
        private final ImageView imageView;
        OnRoomListener onRoomListener;

        public ViewHolder(View view, OnRoomListener onRoomListener) {
            super(view);
            // Define click listener for the ViewHolder's View

            this.onRoomListener = onRoomListener;
            textView = (TextView) view.findViewById(R.id.tv_roomname);
            imageView = (ImageView) view.findViewById(R.id.iv_chatroom_logo);
            view.setOnClickListener(this);
        }

        public TextView getTextView() {
            return textView;
        }

        public ImageView getImageView() {
            return imageView;
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
    public CustomAdapter(ArrayList<Room> dataSet, OnRoomListener onRoomListener) {
        localDataSet = dataSet;
        this.monRoomListener = onRoomListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);

        return new ViewHolder(view,monRoomListener);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTextView().setText(localDataSet.get(position).getRoomName());
        viewHolder.getImageView().setImageResource(R.mipmap.digita_budddy_icon1_round);
        viewHolder.getImageView().setBackgroundResource(R.drawable.picture_circle_bg);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
