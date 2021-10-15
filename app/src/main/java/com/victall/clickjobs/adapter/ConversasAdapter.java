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

        holder.nome.setText(conversa.getUsuario().getNomeAnunciante());
        holder.ultimaMesg.setText(conversa.getUltimaMensagem());

        Picasso.get()
                .load(conversa.getUsuario().getFotoAnunciante())
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.placeholder_error)
                .into(holder.fotoConversa);


    }

    @Override
    public int getItemCount() {
        return conversas_list.size();
    }

    public static class ConversasViewHolder extends RecyclerView.ViewHolder {

        CircleImageView fotoConversa;
        TextView nome,ultimaMesg;


        public ConversasViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            fotoConversa = itemView.findViewById(R.id.imgFotoConversa);
            nome = itemView.findViewById(R.id.txtNomeConversa);
            ultimaMesg = itemView.findViewById(R.id.txtMsgConversa);

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
