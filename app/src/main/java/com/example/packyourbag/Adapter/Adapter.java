package com.example.packyourbag.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.packyourbag.CheckList;
import com.example.packyourbag.Constants.MyConstants;
import com.example.packyourbag.R;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    // Data variables
    @NonNull
    private List<String> titles;
    private List<Integer> images;

    //to inflate the item layout
    private LayoutInflater inflater;

    private Activity activity;

    // Constructor to initialize the adapter with data and context
    public Adapter(Context context, @NonNull List<String> title, List<Integer> images, Activity activity) {
        this.titles = title;
        this.images = images;
        this.activity = activity;
        this.inflater = LayoutInflater.from(context);
    }

    //inflates the item layout and returns a new ViewHolder
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.main_item, parent, false);
        return new MyViewHolder(view);
    }

    //binds data to the views within each item
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Set the title and image for the current item
        holder.title.setText(titles.get(position));
        holder.img.setImageResource(images.get(position));

        // Set alpha for the item's LinearLayout
        holder.linearLayout.setAlpha(0.8f);

        // Set a click listener for the item
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an intent to start the CheckList activity
                Intent intent = new Intent(view.getContext(), CheckList.class);

                // Pass data to the CheckList activity via intent extras
                intent.putExtra(MyConstants.HEADER_SMALL, titles.get(position));
                if (MyConstants.MY_SELECTIONS.equals(titles.get(position))) {
                    intent.putExtra(MyConstants.SHOW_SMALL, MyConstants.FALSE_STRING);
                } else {
                    intent.putExtra(MyConstants.SHOW_SMALL, MyConstants.TRUE_STRING);
                }

                // Start the activity
                view.getContext().startActivity(intent);
            }
        });
    }

    //returns the number of items in the data set
    @Override
    public int getItemCount() {
        return titles.size();
    }

    //holds references to views within each item
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView img;
        LinearLayout linearLayout;

        // Constructor to initialize views
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            img = itemView.findViewById(R.id.img);
            linearLayout = itemView.findViewById(R.id.linearLayout);
        }
    }
}