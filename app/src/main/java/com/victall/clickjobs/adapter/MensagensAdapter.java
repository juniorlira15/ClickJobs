package com.victall.clickjobs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.victall.clickjobs.R;
import com.victall.clickjobs.help.UsuarioFirebase;
import com.victall.clickjobs.model.Mensagem;

import java.util.ArrayList;
import java.util.List;

public class MensagensAdapter extends RecyclerView.Adapter<MensagensAdapter.MyViewHolder> {

    private List<Mensagem> mensagens;
    private Context context;
    private static final int TIPO_REMETENTE = 0;
    private static final int TIPO_DESTINATARIO = 1;

    public MensagensAdapter(List<Mensagem> msg,Context c) {
        mensagens = msg;
        context = c;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item = null;

        if(viewType == TIPO_REMETENTE){
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_remetente,parent,false);
        }else if(viewType == TIPO_DESTINATARIO){
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_destinatario,parent,false);
        }

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Mensagem mensagem = mensagens.get(position);

        String msg = mensagem.getMensagem();
        String imagem = mensagem.getImagem();

        if(imagem!=null){

            Picasso.get().load(imagem).into(holder.imagem);
            holder.mensagem.setVisibility(View.GONE);


        }else{
            holder.mensagem.setText(msg);
            holder.imagem.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemViewType(int position) {

        Mensagem mensagem = mensagens.get(position);

        String idUsuario = UsuarioFirebase.getFirebaseUser().getUid();

        if(idUsuario.equals(mensagem.getIdUsuario())){
            return TIPO_REMETENTE;
        }

        return TIPO_DESTINATARIO;

    }

    @Override
    public int getItemCount() {
        return mensagens.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mensagem;
        ImageView imagem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mensagem = itemView.findViewById(R.id.txtMensagemTexto);
            imagem = itemView.findViewById(R.id.imgMensagemFoto);
        }
    }
}
