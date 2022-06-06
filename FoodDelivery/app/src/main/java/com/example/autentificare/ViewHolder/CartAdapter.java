package com.example.autentificare.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.autentificare.Cart;
import com.example.autentificare.Database.Database;
import com.example.autentificare.Interface.ItemClickListener;
import com.example.autentificare.R;
import com.example.autentificare.model.Common;
import com.example.autentificare.model.Order;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnCreateContextMenuListener{
    public TextView cartTextName, cartTextPrice;
    public ElegantNumberButton btn_quantity;

    private ItemClickListener itemClickListener;

    public void setCartTextName(TextView cartTextName) {
        this.cartTextName = cartTextName;
    }

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        cartTextName = itemView.findViewById(R.id.cart_item_name);
        cartTextPrice = itemView.findViewById(R.id.cart_item_price);
        btn_quantity = (ElegantNumberButton) itemView.findViewById(R.id.btn_quantity);

        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Selecteaza actiunea");
        menu.add(0,0,getAdapterPosition(), Common.DELETE);

    }
}

public class CartAdapter extends  RecyclerView.Adapter<CartViewHolder>{

    private List<Order> listData = new ArrayList<>();
    private Cart cart;

    public CartAdapter(List<Order> listData, Cart cart) {
        this.listData = listData;
        this.cart = cart;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(cart);
        View itemView = inflater.inflate(R.layout.cart_layout,parent,false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        /*TextDrawable drawable = TextDrawable.builder()
                .buildRound(listData.get(position).getQuantity(), Color.RED);
        holder.cartItemCount.setImageDrawable(drawable);*/
        holder.btn_quantity.setNumber(listData.get(holder.getAdapterPosition()).getQuantity());
        holder.btn_quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Order order = listData.get(holder.getAdapterPosition());
                order.setQuantity(String.valueOf(newValue));
                new Database(cart).updateCart(order);

                int total = 0;
                List<Order> orders = new Database(cart).getCarts();
                for (Order item:orders)
                    total+=(Integer.parseInt(order.getPrice()))*Integer.parseInt(item.getQuantity());
                Locale locale = new Locale("ro","RO");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                cart.txtTotalPrice.setText(fmt.format(total));
            }
        });

        Locale locale = new Locale("en","US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuantity()));
        holder.cartTextPrice.setText(fmt.format(price));
        holder.cartTextName.setText(listData.get(position).getProductName());

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
