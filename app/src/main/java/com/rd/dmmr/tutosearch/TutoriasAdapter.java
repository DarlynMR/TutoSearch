package com.rd.dmmr.tutosearch;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Owner on 10/3/2018.
 */

public class TutoriasAdapter extends RecyclerView.Adapter<TutoriasAdapter.ViewPos> {



    private List<ModelTutorias> mList;


    public TutoriasAdapter(List<ModelTutorias> mList) {
        this.mList = mList;
    }

    @Override
    public ViewPos onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewPos(LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_ver_tutorias,parent,false));

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final ViewPos holder, final int position) {

        ModelTutorias itemTutoria= mList.get(position);

        holder.item_txtMateriaPrev.setText(itemTutoria.materias);
        holder.item_txtTituloPrev.setText(itemTutoria.titulo);
        holder.item_txtDescripcionPrev.setText(itemTutoria.descripcion);
        holder.item_txtProfesorPrev.setText(itemTutoria.profesores);
        holder.item_txtFechaPrev.setText(itemTutoria.fecha);
        holder.item_txtHoraPrev.setText(itemTutoria.hora);
        holder.item_txtLugarPrev.setText(itemTutoria.lugar);
        holder.item_txtTiempoRestantePrev.setText("");


        holder.setOnClickListener(position);


        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(holder.getAdapterPosition(), 0, 0,"Ver más detalles");
                contextMenu.add(holder.getAdapterPosition(), 1, 0,"Asistir");
            }
        });




    }

    @Override
    public int getItemCount() {

        return mList.size();
    }

    public class  ViewPos extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView item_imgFotoPrev;
        TextView item_txtMateriaPrev, item_txtTituloPrev, item_txtDescripcionPrev, item_txtProfesorPrev, item_txtFechaPrev, item_txtHoraPrev, item_txtLugarPrev,
                item_txtTiempoRestantePrev;
        CardView cardView;
        Context vcontext;
        Integer idfila;


        public ViewPos(View itemView) {
            super(itemView);
            vcontext = itemView.getContext();
            item_imgFotoPrev= itemView.findViewById(R.id.imgFotoPrev);
            item_txtMateriaPrev=itemView.findViewById(R.id.txtMateria);
            item_txtTituloPrev= itemView.findViewById(R.id.txtTitulo);
            item_txtDescripcionPrev=itemView.findViewById(R.id.txtDescripcion);
            item_txtProfesorPrev= itemView.findViewById(R.id.txtNombreProfesorTutoria);
            item_txtFechaPrev=itemView.findViewById(R.id.txtFechaTutorias);
            item_txtHoraPrev= itemView.findViewById(R.id.txtHoraTutorias);
            item_txtLugarPrev=itemView.findViewById(R.id.txtLugarTurorias);
            item_txtTiempoRestantePrev=itemView.findViewById(R.id.txtTiempoRestante);
            cardView= itemView.findViewById(R.id.RCView);

        }
        void setOnClickListener(Integer pos){
            idfila = pos;
            cardView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.RCView:
                    Intent detalles = new Intent(vcontext, DetallesTutorias.class);
                    detalles.putExtra("idTuto",mList.get(idfila).idTuto);
                    Log.i("Prueba",mList.get(idfila).idTuto+" "+idfila);
                    detalles.putExtra("Materia",mList.get(idfila).materias);
                    detalles.putExtra("idProf",mList.get(idfila).idProf);
                    detalles.putExtra("imgTuto",mList.get(idfila).foto);
                    detalles.putExtra("Titulo", mList.get(idfila).titulo);
                    detalles.putExtra("Descripcion",mList.get(idfila).descripcion);
                    detalles.putExtra("Profesor",mList.get(idfila).profesores);
                    detalles.putExtra("Fecha",mList.get(idfila).fecha);
                    detalles.putExtra("Hora",mList.get(idfila).hora);
                    detalles.putExtra("Lugar",mList.get(idfila).lugar);
                    vcontext.startActivity(detalles);
                    break;
            }
        }
    }
}
