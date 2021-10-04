package com.victall.clickjobs.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.victall.clickjobs.R;

import java.util.ArrayList;

public class FiltroAdapter extends RecyclerView.Adapter<FiltroAdapter.FiltroAdapterViewHolder> {

    private ArrayList<String> lista;
    private OnItemClickListener mListener;

    public FiltroAdapter(ArrayList<String> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public FiltroAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_adapter_recycler,parent,false);
        FiltroAdapterViewHolder viewHolder = new FiltroAdapterViewHolder(view,mListener);
        return viewHolder;

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public void onBindViewHolder(@NonNull FiltroAdapterViewHolder holder, int position) {

        holder.textView.setText(lista.get(position));

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    class FiltroAdapterViewHolder extends RecyclerView.ViewHolder{

        private TextView textView;

        public FiltroAdapterViewHolder(@NonNull View itemView, OnItemClickListener mListener) {
            super(itemView);

            textView = itemView.findViewById(R.id.txtCustomAdapterRecycler);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
