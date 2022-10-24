package com.mifos.mifosxdroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.models.BluetoothModel;

import java.util.ArrayList;

public class BluetoothListAdapter extends RecyclerView.Adapter {
    private final ArrayList<BluetoothModel> dataSet;
    Context mContext;

    public BluetoothListAdapter(ArrayList<BluetoothModel> model, Context context){
        this.dataSet=model;
        this.mContext = context;
    }

    public static class BlueToothViewHolder  extends RecyclerView.ViewHolder {

        TextView txtType;
        CardView cardView;

        public BlueToothViewHolder(View itemView) {
            super(itemView);
            this.txtType = (TextView) itemView.findViewById(R.id.bluetooth_name);
            this.cardView = (CardView) itemView.findViewById(R.id.bluetooth_item);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bluetooth_device, parent, false);
        return new BlueToothViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BluetoothModel model = dataSet.get(position);
        if(model != null){
            ((BlueToothViewHolder) holder).txtType.setText(model.getBluetoothName());
            ((BlueToothViewHolder) holder).cardView.setOnClickListener(v -> {

            });
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
