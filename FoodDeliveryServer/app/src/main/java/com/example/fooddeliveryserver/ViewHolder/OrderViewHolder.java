package com.example.fooddeliveryserver.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddeliveryserver.Interface.ItemClickListener;
import com.example.fooddeliveryserver.R;

public class OrderViewHolder extends RecyclerView.ViewHolder {

    public TextView txtOrderID, txtOrderStatus, txtOrderPhone, txtOrderAddress;

    public Button btnEdit, btnRemove, btnDetail;



    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderID = itemView.findViewById(R.id.order_id);
        txtOrderStatus = itemView.findViewById(R.id.order_status);
        txtOrderPhone = itemView.findViewById(R.id.order_phone);
        txtOrderAddress = itemView.findViewById(R.id.order_address);

        btnEdit = (Button)itemView.findViewById(R.id.btnEdit);
        btnRemove = (Button)itemView.findViewById(R.id.btnRemove);
        btnDetail = (Button)itemView.findViewById(R.id.btnDetail);

    }

}
