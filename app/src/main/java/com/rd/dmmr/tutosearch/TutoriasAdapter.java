package com.rd.dmmr.tutosearch;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Owner on 10/3/2018.
 */

public class TutoriasAdapter extends RecyclerView.Adapter<TutoriasAdapter.ViewPos> {


    private Context mcontext;
    private List<ModelTutorias> mList;
    TutoriasAdapter (Context context, List<ModelTutorias>list){
        mcontext= context;
        mList= list;
    }

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
        holder.item_txtTiempoRestantePrev.setText(itemTutoria.tiemporestante);


/*
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Tutorias tutorias= new Tutorias();
                Context context= tutorias.getApplicationContext();
                Intent intent = new Intent(mcontext.getApplicationContext(),Tutorias.class);


            }
        });*/



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context= new FragmentOficial().getContext();

                Intent intent= new Intent(context,DetallesTutorias.class);

            }
        });




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

    public class  ViewPos extends RecyclerView.ViewHolder{

        ImageView item_imgFotoPrev;
        TextView item_txtMateriaPrev, item_txtTituloPrev, item_txtDescripcionPrev, item_txtProfesorPrev, item_txtFechaPrev, item_txtHoraPrev, item_txtLugarPrev,
                item_txtTiempoRestantePrev;


        public ViewPos(View itemView) {
            super(itemView);

            item_imgFotoPrev= itemView.findViewById(R.id.imgFotoPrev);
            item_txtMateriaPrev=itemView.findViewById(R.id.txtMateria);
            item_txtTituloPrev= itemView.findViewById(R.id.txtTitulo);
            item_txtDescripcionPrev=itemView.findViewById(R.id.txtDescripcion);
            item_txtProfesorPrev= itemView.findViewById(R.id.txtNombreProfesorTutoria);
            item_txtFechaPrev=itemView.findViewById(R.id.txtFechaTutorias);
            item_txtHoraPrev= itemView.findViewById(R.id.txtHoraTutorias);
            item_txtLugarPrev=itemView.findViewById(R.id.txtLugarTurorias);
            item_txtTiempoRestantePrev=itemView.findViewById(R.id.txtTiempoRestante);



        }
    }
}
