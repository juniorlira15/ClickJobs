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

import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.victall.clickjobs.R;
import com.victall.clickjobs.model.Anuncio;

import java.util.ArrayList;

public class AnuncioAdapter extends RecyclerView.Adapter<AnuncioAdapter.AnuncioViewHolder> {

    private ArrayList<Anuncio> anunciosList;
    private OnItemClickListener mListener;
    private Context context;


    public AnuncioAdapter(ArrayList<Anuncio> anunciosList, Context context) {
        this.anunciosList = anunciosList;
    }

    public ArrayList<Anuncio> getAnuncios(){
        return anunciosList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
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


        Picasso.get().
                load(String.valueOf(anunciosList.get(position).getFoto().get(0)))
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder_error)
                .into(holder.imgAnuncio, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.shimmer_view_container.hideShimmer();
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });


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
        private ShimmerFrameLayout shimmer_view_container;




        public AnuncioViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            imgAnuncio = itemView.findViewById(R.id.imgAnuncio);
            txtCategoria = itemView.findViewById(R.id.txtCategProf);
            txtEnd = itemView.findViewById(R.id.txtEndServ);
            txtNome = itemView.findViewById(R.id.txtNomeProf);
            txtValor = itemView.findViewById(R.id.txtFaixaPrecoServ);
            shimmer_view_container = (ShimmerFrameLayout) itemView.findViewById(R.id.shimmer_view_container);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(position);
                        }
                    }
                }
            });


        }
    }
}
