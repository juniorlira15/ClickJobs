package com.victall.clickjobs.activity.adapter;

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
import com.victall.clickjobs.activity.model.Anuncio;

import java.util.ArrayList;

public class AnuncioAdapter extends RecyclerView.Adapter<AnuncioAdapter.AnuncioViewHolder> {

    private ArrayList<Anuncio> anunciosList;
    private AdapterView.OnItemClickListener mListener;


    public AnuncioAdapter(ArrayList<Anuncio> anunciosList) {
        this.anunciosList = anunciosList;
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


        Picasso.get().load(String.valueOf(anunciosList.get(position).getImageView())).into(holder.imgAnuncio);
        holder.txtValor.setText(anunciosList.get(position).getTxtFaixaValor().getText());
        holder.txtNome.setText(anunciosList.get(position).getTextViewNome().getText());
        holder.txtEnd.setText(anunciosList.get(position).getTxtEndereco().getText());
        holder.txtCategoria.setText(anunciosList.get(position).getTxtCategoria().getText());


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
