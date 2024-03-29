package com.rd.dmmr.tutosearch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Owner on 10/3/2018.
 */

public class TutoriasAceptadasAdapter extends RecyclerView.Adapter<TutoriasAceptadasAdapter.ViewPos> implements Filterable {


    private static Long dia = Long.parseLong("86400000");
    private static Long hora = Long.parseLong("3600000");
    private static Long minuto = Long.parseLong("60000");
    private static Long segundo = Long.parseLong("1000");

    private List<ModelTutoriasEst> mList;
    private List<ModelTutoriasEst> mcopyListTutorias;

    public TutoriasAceptadasAdapter(List<ModelTutoriasEst> mList) {

        this.mList = mList;
        this.mcopyListTutorias = mList;
    }

    @Override
    public ViewPos onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewPos(LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_ver_tutorias,parent,false));

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final ViewPos holder, final int position) {

        ModelTutoriasEst itemTutoria= mList.get(position);
        Calendar calInicial = Calendar.getInstance();
        Calendar calFinal = Calendar.getInstance();


        calInicial.setTimeInMillis(Long.parseLong(itemTutoria.timestampInicial));


        Long milisInicial, milisActual, milisRestantes;

        milisInicial = Long.parseLong(itemTutoria.timestampInicial);
        milisActual = System.currentTimeMillis();

        milisRestantes= milisInicial - milisActual;

        if (milisRestantes<0){
            holder.item_txtTiempoRestantePrev.setText("Empezó hace: \n"+ obtenerTiempoRestante(milisRestantes*-1));
            holder.item_txtTiempoRestantePrev.setTextColor(Color.parseColor("#FFEE4747"));
        }else {
            holder.item_txtTiempoRestantePrev.setText("Tiempo restante: \n"+ obtenerTiempoRestante(milisRestantes));
            holder.item_txtTiempoRestantePrev.setTextColor(Color.parseColor("#ffffff"));
        }

        String fecha = calInicial.get(Calendar.DAY_OF_MONTH)+"/"+(calInicial.get(Calendar.MONTH)+1)+"/"+calInicial.get(Calendar.YEAR);
        String hora="";

        holder.item_txtNombreProf.setText(itemTutoria.getNombreProf());
        holder.item_txtMateriaPrev.setText(itemTutoria.materia);
        holder.item_txtTituloPrev.setText(itemTutoria.titulo);
        holder.item_txtDescripcionPrev.setText(itemTutoria.descripcion);
        holder.item_txtFechaPrev.setText("Fecha: "+fecha);
        if (!itemTutoria.lugar.equals("")) {
            holder.item_txtLugarPrev.setText("Lugar: " + itemTutoria.lugar);
        }else {
            holder.item_txtLugarPrev.setText("");
        }

        if (itemTutoria.url_image_portada.equals("defaultPresencial")){
            holder.item_imgFotoPrev.setImageResource(R.mipmap.presencial_background);
        } else if(itemTutoria.url_image_portada.equals("defaultLive")){
            holder.item_imgFotoPrev.setImageResource(R.drawable.video_camera_live);

        } else {
            try {
                Glide.with(holder.itemView.getContext())
                        .load(itemTutoria.url_image_portada)
                        .fitCenter()
                        .centerCrop()
                        .into(holder.item_imgFotoPrev);

            }catch (Exception e){
                Log.i("Error", ""+e.getMessage());
            }

        }

        if (itemTutoria.tipo_tuto.equals("Live")){
            hora = calInicial.get(Calendar.HOUR)+":"+calInicial.get(Calendar.MINUTE);
            holder.item_imgType.setImageResource(R.drawable.ic_iconfinder_facebook_live_icon_1083837);

        }else if (itemTutoria.tipo_tuto.equals("Presencial")){
            calFinal.setTimeInMillis(Long.parseLong(itemTutoria.timestampFinal));
            hora = calInicial.get(Calendar.HOUR)+":"+calInicial.get(Calendar.MINUTE)+" - " + calFinal.get(Calendar.HOUR)+":"+ calFinal.get(Calendar.MINUTE);
            holder.item_imgType.setImageResource(R.drawable.ic_iconfinder_dicument_4115232);
        }
        holder.item_txtHoraPrev.setText("Hora: "+hora);


        holder.setOnClickListener(position);


        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(holder.getAdapterPosition(), 0, 0,"Ver más detalles");
            }
        });




    }

    private String obtenerTiempoRestante(Long milisRestantes) {

        Long di, hor, min, seg;
        String textRestante="";

        if (milisRestantes>=dia){
            di = milisRestantes / dia;
            milisRestantes -= dia*di;
            textRestante += (milisRestantes >= hora ? di+" días, " :  di+" días, ");
        }
        if (milisRestantes>=hora){
            hor = milisRestantes / hora;
            milisRestantes -= hora*hor;
            textRestante += (milisRestantes >= minuto ? hor+" horas, " : hor+" horas ");
        }
        if (milisRestantes>=minuto){
            min = milisRestantes / minuto;
            milisRestantes -= minuto*min;
            textRestante += (milisRestantes >= segundo ? min+" minutos y " : min+" minutos ");
        }

        if (milisRestantes>=segundo){
            seg = milisRestantes / segundo;
            textRestante += seg+" segundos";
        }
        return textRestante;
    }

    @Override
    public int getItemCount() {

        return mList.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (mList!=null) {
                    mList = (List<ModelTutoriasEst>) results.values;
                    notifyDataSetChanged();
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<ModelTutoriasEst> filteredResults = null;
                if (constraint.length() == 0) {
                    filteredResults = mcopyListTutorias;
                } else {
                    filteredResults = getFilteredResults(constraint.toString());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }
        };
    }

    protected List<ModelTutoriasEst> getFilteredResults(String constraint) {
        List<ModelTutoriasEst> results = new ArrayList<>();
        for (ModelTutoriasEst item : mcopyListTutorias) {
            if (item.getMateria().contains(constraint)) {
                Log.i("ProbandoSPN", "Entra al a la condicion 2");
                results.add(item);

            }
        }
        return results;
    }


    public class  ViewPos extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView item_imgFotoPrev, item_imgType;
        TextView item_txtNombreProf,item_txtMateriaPrev, item_txtTituloPrev, item_txtDescripcionPrev, item_txtFechaPrev, item_txtHoraPrev, item_txtLugarPrev,item_txtTiempoRestantePrev, item;
        CardView cardView;
        private Context vcontext;
        Integer idfila;


        public ViewPos(View itemView) {
            super(itemView);
            vcontext = itemView.getContext();
            item_txtNombreProf= itemView.findViewById(R.id.txtNombreProfesorTutoria);
            item_imgFotoPrev= itemView.findViewById(R.id.imgFotoPrev);
            item_txtMateriaPrev=itemView.findViewById(R.id.txtMateria);
            item_txtTituloPrev= itemView.findViewById(R.id.txtTitulo);
            item_txtDescripcionPrev=itemView.findViewById(R.id.txtDescripcion);
            item_txtFechaPrev=itemView.findViewById(R.id.txtFechaTutorias);
            item_txtHoraPrev= itemView.findViewById(R.id.txtHoraTutorias);
            item_txtLugarPrev=itemView.findViewById(R.id.txtLugarTurorias);
            item_imgType=itemView.findViewById(R.id.imgType);
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
                    Class clase;

                    if (mList.get(idfila).tipo_tuto.equals("Live")){
                        clase = TransmisionLive.class;
                    }else {
                        clase = DetallesTutorias.class;
                    }

                    Intent detalles = new Intent(vcontext, clase);
                    detalles.putExtra("idTuto",mList.get(idfila).idTuto);
                    detalles.putExtra("Profesor",mList.get(idfila).nombreProf);
                    detalles.putExtra("Materia",mList.get(idfila).materia);
                    detalles.putExtra("imgTuto",mList.get(idfila).url_image_portada);
                    detalles.putExtra("Titulo", mList.get(idfila).titulo);
                    detalles.putExtra("Descripcion",mList.get(idfila).descripcion);
                    detalles.putExtra("timestampI",mList.get(idfila).timestampInicial);
                    detalles.putExtra("timestampF",mList.get(idfila).timestampFinal);
                    detalles.putExtra("timestampPub",mList.get(idfila).timestampPub);
                    detalles.putExtra("Lugar",mList.get(idfila).lugar);
                    detalles.putExtra("TipoEs",mList.get(idfila).tipo_tuto);



                    vcontext.startActivity(detalles);
                    break;
            }
        }
    }
}
