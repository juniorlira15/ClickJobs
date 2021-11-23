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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.victall.clickjobs.R;
import com.victall.clickjobs.config.ConfiguracaoFirebase;
import com.victall.clickjobs.help.DataHora;
import com.victall.clickjobs.model.Conversa;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversasAdapter extends RecyclerView.Adapter<ConversasAdapter.ConversasViewHolder> {

    private ArrayList<Conversa> conversas_list;
    private final Context context;
    private OnItemClickListener mListener;

    public ConversasAdapter(ArrayList<Conversa> conversas_list, Context context) {
        this.conversas_list = conversas_list;
        this.context = context;
    }

    @NonNull
    @Override
    public ConversasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversa,parent,false);

        return new ConversasViewHolder(view,mListener);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversasViewHolder holder, int position) {

        Conversa conversa = conversas_list.get(position);


        holder.nome.setText(conversa.getUsuario().getNome());
        holder.ultimaMesg.setText(conversa.getUltimaMensagem());
        holder.horarioUltMsg.setText(conversa.getHorario());

        DatabaseReference databaseReference = ConfiguracaoFirebase.getDatabaseReference()
                .child("usuarios").child(conversa.getUsuario().getId()).child("status");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue().equals("online")){
                    holder.imgOnline.setVisibility(View.VISIBLE);
                }else{
                    holder.imgOnline.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        if(conversa.getUsuario().getFoto().equals("") || conversa.getUsuario().getFoto() == null){
            Picasso.get()
                    .load(R.drawable.img_placeholder)
                    .into(holder.fotoConversa);
        }else{
            Picasso.get()
                    .load(conversa.getUsuario().getFoto())
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.placeholder_error)
                    .into(holder.fotoConversa);
        }




    }

    @Override
    public int getItemCount() {
        return conversas_list.size();
    }

    public static class ConversasViewHolder extends RecyclerView.ViewHolder {

        CircleImageView fotoConversa;
        TextView nome,ultimaMesg,naoLida,horarioUltMsg;
        ImageView imgOnline;


        public ConversasViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            fotoConversa = itemView.findViewById(R.id.imgFotoConversa);
            nome = itemView.findViewById(R.id.txtNomeConversa);
            ultimaMesg = itemView.findViewById(R.id.txtMsgConversa);
            naoLida = itemView.findViewById(R.id.txtMsgNaoLida);
            horarioUltMsg = itemView.findViewById(R.id.txtHorarioUltMsg);
            imgOnline = itemView.findViewById(R.id.imgOnline);


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
