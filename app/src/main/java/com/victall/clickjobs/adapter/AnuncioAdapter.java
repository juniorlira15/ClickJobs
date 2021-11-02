package com.victall.clickjobs.adapter;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AnuncioAdapter extends RecyclerView.Adapter<AnuncioAdapter.AnuncioViewHolder> {

    private ArrayList<Anuncio> anunciosList;
    private OnItemClickListener mListener;
    private Context context;


    public AnuncioAdapter(ArrayList<Anuncio> anunciosList, Context context) {
        this.anunciosList = anunciosList;
        this.context = context;
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
        String categoria = anunciosList.get(position).getCategoria();
        DatabaseReference reference = ConfiguracaoFirebase.getDatabaseReference();
        DatabaseReference fotoAnunciante = reference.child("usuarios").child(idAnunciante).child("foto");

        fotoAnunciante.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String urlFoto = snapshot.getValue(String.class);
                saveToInternalStorage(Uri.parse(urlFoto),idAnunciante);

                if(urlFoto!=null && !urlFoto.equals("")){
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
                                    holder.shimmer_view_container.hideShimmer();
                                }
                            });

                }else{
                    Picasso.get().
                            load(R.drawable.img_placeholder).
                            into(holder.imgPerfil);
                    holder.shimmer_view_container.hideShimmer();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        List<SlideModel> slideModels = new ArrayList<>();

        // Pega Somente a primeira foto
        for(int i=0; i<1;i++){
            int posicao=i+1;
            int total = anunciosList.get(position).getFoto().size();
            slideModels.add(new SlideModel(anunciosList.get(position).getFoto().get(i),categoria));
        }


        holder.imgAnuncio.setImageList(slideModels,true);
//        Picasso.get().
//                load(anunciosList.get(position).getFoto().get(0))
//                .placeholder(R.drawable.img_place_logo)
//                .error(R.drawable.img_placeholder_error)
//                .into(holder.imgPerfil, new Callback() {
//                    @Override
//                    public void onSuccess() {
//                        holder.shimmer_view_container.hideShimmer();
//                    }
//
//                    @Override
//                    public void onError(Exception e) {
//
//                    }
//           });
//

        holder.txtValor.setText(anunciosList.get(position).getValor());

        if(titulo.length()>20){
            String novoTitulo = titulo.substring(0,19)+"...";
            holder.txtNome.setText(novoTitulo);
        }else{
            holder.txtNome.setText(titulo);
        }

        holder.txtData.setText(anunciosList.get(position).getData()+" - "+anunciosList.get(position).getHora());
        holder.txtEnd.setText(anunciosList.get(position).getEndereco());


        //holder.shimmer_view_container.hideShimmer();

    }

    @Override
    public int getItemCount() {
        return anunciosList.size();
    }

    public class AnuncioViewHolder extends RecyclerView.ViewHolder{

        private ImageSlider imgAnuncio;
        private TextView txtNome,txtValor,txtCategoria,txtEnd,txtData;
        private ShimmerFrameLayout shimmer_view_container;
        private CircleImageView imgPerfil;




        public AnuncioViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            imgAnuncio = itemView.findViewById(R.id.imgAnuncio);
            txtCategoria = itemView.findViewById(R.id.txtCategProf);
            txtEnd = itemView.findViewById(R.id.txtEndereco);
            txtNome = itemView.findViewById(R.id.txtNomeProf);
            txtValor = itemView.findViewById(R.id.txtFaixaPrecoServ);
            shimmer_view_container = (ShimmerFrameLayout) itemView.findViewById(R.id.shimmer_view_container);
            imgPerfil = itemView.findViewById(R.id.imgAnunciante);
            txtData = itemView.findViewById(R.id.txtDataPostagem);

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

    private String saveToInternalStorage(Uri uriImage, String idUsuario){

        Bitmap bitmap = null;

        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uriImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,idUsuario+".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private Bitmap getImageFromStorage(String path,String idUsuario)
    {
        try {
            File f=new File(path, "/"+idUsuario+".jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        return  null;

    }
}
