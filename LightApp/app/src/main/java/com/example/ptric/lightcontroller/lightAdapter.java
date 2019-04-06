package com.example.ptric.lightcontroller;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import java.util.ArrayList;

public class lightAdapter extends RecyclerView.Adapter<lightAdapter.LightViewHolder> {
    public static ArrayList<Integer> lights;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class LightViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView posit;
        public Button color;
        public Button delete;

        public LightViewHolder(View lightItem, final Context c) {
            super(lightItem);
            posit = (TextView) itemView.findViewById(R.id.position);
            color = (Button) itemView.findViewById(R.id.color);
            color.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("BUTON", posit.getText() + " Edit");
                    ColorDrawable buttonColor = (ColorDrawable) view.getBackground();
                    int cc = buttonColor.getColor();
                    int r = (cc & 0xff0000) >> 16;
                    int g = (cc & 0xff00) >> 8;
                    int b = (cc & 0xff);

                    final ColorPicker cp = new ColorPicker((Activity) c, r, g, b);
                    cp.show();

                    Button okColor = (Button)cp.findViewById(R.id.okColorButton);

                    okColor.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            /* You can get single channel (value 0-255) */
                            /*selectedColorR = cp.getRed();
                            selectedColorG = cp.getGreen();
                            selectedColorB = cp.getBlue();*/

                            /* Or the android RGB Color (see the android Color class reference) */
                            Log.e("COLOR", ""+cp.getColor());
                            color.setBackgroundColor(cp.getColor());
                            lights.set(Integer.parseInt((String)posit.getText()), cp.getColor());
                            cp.dismiss();
                        }
                    });
                    //Intent myIntent = new Intent(c, LightActivity.class);
                    //myIntent.putExtra("config", textView.getText()); //Optional parameters
                    //c.startActivity(myIntent);
                }
            });
            delete = (Button) itemView.findViewById(R.id.delete_button);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("BUTON", posit.getText() + " Delete");

                    //removeAt(Integer.parseInt((String)posit.getText()));
                    LightActivity a = (LightActivity) c;
                    a.removeAt(Integer.parseInt((String)posit.getText()));

                }
            });

        }

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public lightAdapter(ArrayList<Integer> myDataset) {
        lights = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public LightViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        // create a new view
        /*TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.config_item, parent, false);*/

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View lightItem = inflater.inflate(R.layout.light_item, parent, false);

        LightViewHolder vh = new LightViewHolder(lightItem, parent.getContext());
        return vh;
    }



    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(LightViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.posit.setText(Integer.toString(position));
        /*Color mColor = new Color();
        mColor.red((lights.get(position) & 0xff0000) >> 16);
        mColor.blue((lights.get(position) & 0xff00) >> 8);
        mColor.green((lights.get(position) & 0xff));*/
        holder.color.setBackgroundColor(lights.get(position)| 0xff000000);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return lights.size();
    }





}