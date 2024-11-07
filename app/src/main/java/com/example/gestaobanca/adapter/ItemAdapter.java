package com.example.gestaobanca.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaobanca.R;

import java.util.ArrayList;
import java.util.Collections;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private ArrayList<String> itemsList;
    private final OnItemLongClickListener onItemLongClickListener;

    public ItemAdapter(ArrayList<String> itemsList, OnItemLongClickListener onItemLongClickListener) {
        this.itemsList = itemsList;
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar o layout do item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_campeonato_mercado, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Obtém o item da lista
        String item = itemsList.get(position);



        // Define o número do item (pode ser indexado a partir de 1)
        holder.itemNumber.setText(String.valueOf(position + 1)); // Número do item
        // Define o nome do item
        holder.itemName.setText(item); // Nome do item

        // Listener para clique longo
        holder.itemView.setOnLongClickListener(v -> {
            if (onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick(position);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemNumber; // TextView para o número do item
        public TextView itemName;   // TextView para o nome do item

        public ViewHolder(View itemView) {
            super(itemView);
            // Inicializa as TextViews a partir do layout
            itemNumber = itemView.findViewById(R.id.item_number);
            itemName = itemView.findViewById(R.id.item_name);
        }
    }
}