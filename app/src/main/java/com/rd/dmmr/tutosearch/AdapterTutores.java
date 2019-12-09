package com.rd.dmmr.tutosearch;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class AdapterTutores extends RecyclerView.Adapter<AdapterTutores.ViewHolder> implements Filterable {

    private List<ModelTutores> mListTutores;
    private List<ModelTutores> mcopyListTutores;



    public AdapterTutores(List<ModelTutores> mListTutores) {
        this.mListTutores = mListTutores;
        this.mcopyListTutores = mListTutores;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new AdapterTutores.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_tutores,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ArrayList<String> list;
        String materias="";
        int cont = 0;


        ModelTutores modelTutores = mListTutores.get(position);
        holder.item_txtNombreProf.setText(modelTutores.nombres+" "+modelTutores.apellidos);
        holder.item_txtProvincia.setText(modelTutores.provincia);
        list = modelTutores.Materias;

        if (list !=null && list.size()!=0) {
            Log.i("Lista", ""+list);
            do {
                if (!materias.equals("")) {
                    materias = materias + ", ";
                }
                materias = materias + list.get(cont);
                cont++;
            } while (cont < list.size());
        }else {
            materias= "Materias sin especificar.";
        }

        if (!materias.isEmpty()){
            holder.item_txtMaterias.setText(materias);
        }
        if (!modelTutores.about_me.equals("null")) {
            holder.item_txtDescripcion.setText(modelTutores.about_me);
        }else{
            holder.item_txtDescripcion.setText("Descripción no especificada");
        }

        if (!modelTutores.url_thumb_pic.equals("defaultPicProf")){
            Glide.with(holder.itemView.getContext())
                    .load(modelTutores.url_thumb_pic)
                    .fitCenter()
                    .centerCrop()
                    .into(holder.imgProfesor);
        }

        holder.setOnClickListener(position);

    }

    @Override
    public int getItemCount() {
        return mListTutores.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (mListTutores!=null) {
                    mListTutores = (List<ModelTutores>) results.values;
                    notifyDataSetChanged();
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<ModelTutores> filteredResults = null;
                if (constraint.length() == 0) {
                    filteredResults = mcopyListTutores;
                } else {
                    filteredResults = getFilteredResults(constraint.toString());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }
        };
    }

    protected List<ModelTutores> getFilteredResults(String constraint) {
        List<ModelTutores> results = new ArrayList<>();
        for (ModelTutores item : mcopyListTutores) {
            if (item.getMaterias().contains(constraint)) {
                Log.i("ProbandoSPN", "Entra al a la condicion 2");
                results.add(item);
                Log.i("ProbandoSPN", item.getNombres());
            }
            if (item.getProvincia().contains(constraint)) {
                Log.i("ProbandoSPN", "Entra al a la condicion 2");
                results.add(item);
                Log.i("ProbandoSPN", item.getNombres());
            }
        }
        return results;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CardView cardView;
        ImageView imgProfesor;
        TextView item_txtNombreProf, item_txtProvincia, item_txtMaterias, item_txtDescripcion;
        private Context vcontext;
        int idfila;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            vcontext = itemView.getContext();
            cardView = itemView.findViewById(R.id.RCTutores);
            imgProfesor =itemView.findViewById(R.id.imgCircularProf);
            item_txtNombreProf =itemView.findViewById(R.id.txtNombreProf);
            item_txtProvincia =itemView.findViewById(R.id.txtProvinciaRC);
            item_txtMaterias =itemView.findViewById(R.id.txtMaterias);
            item_txtDescripcion =itemView.findViewById(R.id.txtDescripcion);


        }

        void setOnClickListener(int pos){
            idfila = pos;
            cardView.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.RCTutores:
                    String materias = "";
                    ArrayList<String> list;
                    int cont=0;

                    ModelTutores modelTutores = mcopyListTutores.get(idfila);

                    list = modelTutores.Materias;

                    if (list !=null && list.size()!=0) {
                        do {
                            if (!materias.equals("")) {
                                materias = materias + ", ";
                            }
                            materias = materias + list.get(cont);
                            cont++;
                        } while (cont < list.size());
                    }else {
                        materias= "Materias sin especificar.";
                    }
                    Intent detalles = new Intent(vcontext, DetallesEstudentToProf.class);
                    detalles.putExtra("idProf",mcopyListTutores.get(idfila).UIDProf);
                    detalles.putExtra("nombres",mcopyListTutores.get(idfila).nombres);
                    detalles.putExtra("apellidos",mcopyListTutores.get(idfila).apellidos);
                    detalles.putExtra("materias",materias);
                    detalles.putExtra("aboutme",mcopyListTutores.get(idfila).about_me);
                    detalles.putExtra("provincia", mcopyListTutores.get(idfila).provincia);
                    detalles.putExtra("urlpic",mcopyListTutores.get(idfila).url_pic);
                    vcontext.startActivity(detalles);
                    break;
            }

        }
    }

}
