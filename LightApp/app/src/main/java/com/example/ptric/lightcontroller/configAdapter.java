package com.example.ptric.lightcontroller;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class configAdapter extends RecyclerView.Adapter<configAdapter.configViewHolder> {
    private ArrayList<String> configs;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class configViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public Button  edit;
        public Button  select;
        public Button remove;
        public configViewHolder(View configItem, final Context c) {
            super(configItem);
            textView = (TextView) itemView.findViewById(R.id.textView);
            edit = (Button) itemView.findViewById(R.id.edit_button);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("BUTON", textView.getText()+" Edit");
                    Intent myIntent = new Intent(c, LightActivity.class);
                    myIntent.putExtra("config", textView.getText());
                    myIntent.putExtra("type", "edit");
                    c.startActivity(myIntent);
                }
            });
            select = (Button) itemView.findViewById(R.id.select_button);
            select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("BUTON", textView.getText()+" Select");
                    ((MainActivity)c).send_select((String)textView.getText());
                }
            });
            remove = (Button) itemView.findViewById(R.id.remove_button);
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity)c).remove_config((String)textView.getText());
                }
            });

        }

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public configAdapter(ArrayList<String> myDataset) {
        configs = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public configViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        // create a new view
        /*TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.config_item, parent, false);*/

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View configItem = inflater.inflate(R.layout.config_item, parent, false);

        configViewHolder vh = new configViewHolder(configItem, parent.getContext());
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(configViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.setText(configs.get(position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return configs.size();
    }
}