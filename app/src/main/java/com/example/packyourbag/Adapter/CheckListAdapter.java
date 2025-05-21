package com.example.packyourbag.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.packyourbag.Constants.MyConstants;
import com.example.packyourbag.Database.RoomDb;
import com.example.packyourbag.Models.Items;
import com.example.packyourbag.R;

import java.util.List;

public class CheckListAdapter extends RecyclerView.Adapter<CheckListViewHolder> {
    Context context;
    List<Items> itemsList;
    RoomDb database;
    String show;

    public CheckListAdapter() {

    }

    public CheckListAdapter(Context context, List<Items> itemsList, RoomDb database, String show) {
        this.context = context;
        this.itemsList = itemsList;
        this.database = database;
        this.show = show;
        if (itemsList.size() == 0) {
            Toast.makeText(context.getApplicationContext(), "Nothing to show", Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    @Override
    public CheckListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CheckListViewHolder(LayoutInflater.from(context).inflate(R.layout.check_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CheckListViewHolder holder, int position) {
        holder.checkBox.setText(itemsList.get(position).getItemname());
        holder.checkBox.setChecked(itemsList.get(position).isChecked());

        if (MyConstants.FALSE_STRING.equals(show)) {
            holder.btnDelete.setVisibility(View.GONE);
            holder.itemView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.border_1));
        } else {
            if (itemsList.get(position).isChecked()) {
                holder.itemView.setBackgroundColor(Color.parseColor("#8e546f"));
            } else {
                holder.itemView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.border_1));
            }
        }
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                boolean check = holder.checkBox.isChecked();
                database.mainDao().checkUncheck(itemsList.get(position).getID(), check);
                if (MyConstants.FALSE_STRING.equals(show)) {
                    itemsList = database.mainDao().getallSellected(true);
                    notifyDataSetChanged();
                } else {
                    itemsList.get(position).setChecked(check);
                    notifyDataSetChanged();
                    Toast toastMessage = null;
                    if (toastMessage != null) {
                        toastMessage.cancel();
                    }
                    if (itemsList.get(position).isChecked()) {
                        toastMessage = Toast.makeText(context, "( " + holder.checkBox.getText() + " ) Packed", Toast.LENGTH_SHORT);
                    } else {
                        toastMessage = Toast.makeText(context, "( " + holder.checkBox.getText() + " )Un-Packed", Toast.LENGTH_SHORT);
                    }
                    toastMessage.show();
                }
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete ( " + itemsList.get(position).getItemname() + " )")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                database.mainDao().delete(itemsList.get(position));
                                itemsList.remove(itemsList.get(position));
                                notifyDataSetChanged();
                            }
                        }).setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
                            }
                        }).setIcon(R.drawable.delete_forever)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }
}

class CheckListViewHolder extends RecyclerView.ViewHolder {

    LinearLayout layout;
    CheckBox checkBox;
    Button btnDelete;

    public CheckListViewHolder(@NonNull View itemView) {
        super(itemView);
        layout = itemView.findViewById(R.id.linearLayout);
        checkBox = itemView.findViewById(R.id.checkbox);
        btnDelete = itemView.findViewById((R.id.btndelete));

    }
}