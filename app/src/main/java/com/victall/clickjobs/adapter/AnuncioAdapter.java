package com.victall.clickjobs.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;

import com.squareup.picasso.Picasso;
import com.victall.clickjobs.R;
import com.victall.clickjobs.config.ConfiguracaoFirebase;
import com.victall.clickjobs.model.Anuncio;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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


    public void filterList(ArrayList<Anuncio> filteredList) {
        anunciosList = filteredList;
        notifyDataSetChanged();
        Log.d("FILTER", "filterList: "+filteredList.size());
    }


    @NonNull
    @Override
    public AnuncioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_anuncio2,parent,false);
        AnuncioViewHolder viewHolder = new AnuncioViewHolder(view,mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AnuncioViewHolder holder, int position) {

        String titulo = anunciosList.get(position).getNomeAnunciante();
        String idAnunciante = anunciosList.get(position).getIdAnunciante();
        DatabaseReference reference = ConfiguracaoFirebase.getDatabaseReference();
        DatabaseReference fotoAnunciante = reference.child("usuarios").child(idAnunciante).child("foto");

        fotoAnunciante.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String urlFoto = snapshot.getValue(String.class);
                if(urlFoto!=null){
                    Picasso.get().
                            load(urlFoto)
                            .placeholder(R.drawable.img_placeholder)
                            .error(R.drawable.img_placeholder_error)
                            .into(holder.imgPerfil, new Callback() {
                                @Override
                                public void onSuccess() {
                                    holder.shimmer_view_container.hideShimmer();
                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        List<SlideModel> slideModels = new ArrayList<>();

        for(int i=0; i<anunciosList.get(position).getFoto().size();i++){
            slideModels.add(new SlideModel(anunciosList.get(position).getFoto().get(i), ""));
        }
        holder.imgAnuncio.setImageList(slideModels,true);

//        if(anunciosList.get(position).getFotoAnunciante().isEmpty()){
//            Picasso.get().load(R.drawable.icon_perfil);
//        }else {
//
//            Picasso.get().
//                    load("gs://clickjobs-3378d.appspot.com/imagens/usuarios/95kgyg4M6bMCmWxjNU7xfsCSTQB2/perfil")
//                    .placeholder(R.drawable.img_placeholder)
//                    .error(R.drawable.img_placeholder_error)
//                    .into(holder.imgPerfil, new Callback() {
//                        @Override
//                        public void onSuccess() {
//                            holder.shimmer_view_container.hideShimmer();
//                        }
//
//                        @Override
//                        public void onError(Exception e) {
//
//                        }
//                    });
//        }
        holder.txtValor.setText(anunciosList.get(position).getValor());

        if(titulo.length()>20){
            String novoTitulo = titulo.substring(0,19)+"...";
            holder.txtNome.setText(novoTitulo);
        }else{
            holder.txtNome.setText(titulo);
        }

//        holder.txtEnd.setText(anunciosList.get(position).getEndereco());
        holder.txtCategoria.setText(anunciosList.get(position).getCategoria());

        holder.shimmer_view_container.hideShimmer();
    }

    @Override
    public int getItemCount() {
        return anunciosList.size();
    }

    public class AnuncioViewHolder extends RecyclerView.ViewHolder{

        private ImageSlider imgAnuncio;
        private TextView txtNome,txtValor,txtCategoria,txtEnd;
        private ShimmerFrameLayout shimmer_view_container;
        private CircleImageView imgPerfil;




        public AnuncioViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            imgAnuncio = itemView.findViewById(R.id.imgAnuncio);
            txtCategoria = itemView.findViewById(R.id.txtCategProf);
//            txtEnd = itemView.findViewById(R.id.txtEndServ);
            txtNome = itemView.findViewById(R.id.txtNomeProf);
            txtValor = itemView.findViewById(R.id.txtFaixaPrecoServ);
            shimmer_view_container = (ShimmerFrameLayout) itemView.findViewById(R.id.shimmer_view_container);
            imgPerfil = itemView.findViewById(R.id.imgAnunciante);

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
