package com.victall.clickjobs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.victall.clickjobs.R;
import com.victall.clickjobs.model.Anuncio;

import java.util.ArrayList;

public class AnuncioAdapter extends RecyclerView.Adapter<AnuncioAdapter.AnuncioViewHolder> {

    private ArrayList<Anuncio> anunciosList;
    private AdapterView.OnItemClickListener mListener;
    private Context context;

    public AnuncioAdapter(ArrayList<Anuncio> anunciosList, Context context) {
        this.anunciosList = anunciosList;
        this.context = context;

    }

    @NonNull
    @Override
    public AnuncioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_anuncio,parent,false);
        AnuncioViewHolder viewHolder = new AnuncioViewHolder(view,mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AnuncioViewHolder holder, int position) {


        Picasso.get().load(String.valueOf(anunciosList.get(position).getFoto().get(0))).into(holder.imgAnuncio);
        holder.txtValor.setText(anunciosList.get(position).getValor());
        holder.txtNome.setText(anunciosList.get(position).getTitulo());
        holder.txtEnd.setText(anunciosList.get(position).getEndereco());
        holder.txtCategoria.setText(anunciosList.get(position).getCategoria());


    }

    @Override
    public int getItemCount() {
        return anunciosList.size();
    }

    public class AnuncioViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgAnuncio;
        private TextView txtNome,txtValor,txtCategoria,txtEnd;


        public AnuncioViewHolder(@NonNull View itemView, AdapterView.OnItemClickListener onItemClickListener) {
            super(itemView);

            imgAnuncio = itemView.findViewById(R.id.imgAnuncio);
            txtCategoria = itemView.findViewById(R.id.txtCategProf);
            txtEnd = itemView.findViewById(R.id.txtEndServ);
            txtNome = itemView.findViewById(R.id.txtNomeProf);
            txtValor = itemView.findViewById(R.id.txtFaixaPrecoServ);

        }
    }
}
