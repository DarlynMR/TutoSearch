package com.rd.dmmr.tutosearch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class FragmentOficial  extends Fragment {

    View view;

    RecyclerView RCAbajo;
    private TutoriasAdapter tutoriasAdapter;
    private FirebaseDatabase FDatabase;
    private DatabaseReference DBReference;

    private List<ModelTutorias> mListTutoria;

    public FragmentOficial() {



    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tuto_oficiales_fragment,container,false);

        FDatabase= FirebaseDatabase.getInstance();
        DBReference= FDatabase.getReference("tutorias").child("institucionales");


        RCAbajo= (RecyclerView) view.findViewById(R.id.RCAbajo);
        //RCAbajo.setHasFixedSize(true);

        mListTutoria= new ArrayList<>();

        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        RCAbajo.setLayoutManager(layoutManager);

        //CrearTTutorias();

        tutoriasAdapter= new TutoriasAdapter(mListTutoria);

        RCAbajo.setAdapter(tutoriasAdapter);
        upTutorias();
        tutoriasAdapter.notifyDataSetChanged();

        return view;
    }


    private void upTutorias(){

        DBReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String Materia, idProf, idTuto, imgTuto, Titulo, Descripcion, Profesor, Fecha, HoraI, HoraF,  Lugar;

                Log.i("Probando",dataSnapshot.getValue().toString());

                imgTuto=dataSnapshot.child("image").getValue().toString();
                Titulo= dataSnapshot.child("Titulo").getValue().toString();
                Descripcion= dataSnapshot.child("Descripción").getValue().toString();
                Profesor= dataSnapshot.child("Profesor").getValue().toString();
                Fecha= dataSnapshot.child("Fecha").getValue().toString();
                HoraI= dataSnapshot.child("Hora inicial").getValue().toString();
                if (dataSnapshot.child("Hora final").getValue()==null){
                    HoraF= "";
                }else {
                    HoraF = dataSnapshot.child("Hora final").getValue().toString();
                }
                if (dataSnapshot.child("Lugar").getValue()==null){
                    Lugar= "";
                }else {
                    Lugar= dataSnapshot.child("Lugar").getValue().toString();
                }

                idProf= dataSnapshot.child("UIDProfesor").getValue().toString();
                Materia= dataSnapshot.child("Materia").getValue().toString();
                idTuto= dataSnapshot.getKey();

                Log.i("Probando", ""+dataSnapshot);
                Log.i("Probando", ""+dataSnapshot.getChildren());
                Log.i("Probando", ""+dataSnapshot.getRef().getParent().getKey());
                Log.i("Probando", ""+dataSnapshot.getKey());

                if (HoraF.equals("")) {
                    mListTutoria.add(new ModelTutorias(idTuto,idProf,imgTuto,Materia,Titulo,Descripcion,Profesor,Fecha,HoraI,Lugar, ""));
                }else {
                    mListTutoria.add(new ModelTutorias(idTuto, idProf, imgTuto, Materia, Titulo, Descripcion, Profesor, Fecha, HoraI + " - " + HoraF, Lugar, ""));
                }
                tutoriasAdapter.notifyDataSetChanged();


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ModelTutorias modelTutorias=dataSnapshot.getValue(ModelTutorias.class);

                Log.i("CAMBIANDO",dataSnapshot.toString()+" "+s);

                String Materia, idProf, idTuto, imgTuto, Titulo, Descripcion, Profesor, Fecha, HoraI, HoraF,  Lugar;
                int index=-1;



                imgTuto=dataSnapshot.child("image").getValue().toString();
                Titulo= dataSnapshot.child("Titulo").getValue().toString();
                Descripcion= dataSnapshot.child("Descripción").getValue().toString();
                Profesor= dataSnapshot.child("Profesor").getValue().toString();
                Fecha= dataSnapshot.child("Fecha").getValue().toString();
                HoraI= dataSnapshot.child("Hora inicial").getValue().toString();
                if (dataSnapshot.child("Hora final").getValue()==null){
                    HoraF= "";
                }else {
                    HoraF = dataSnapshot.child("Hora final").getValue().toString();
                }
                if (dataSnapshot.child("Lugar").getValue()==null){
                    Lugar= "";
                }else {
                    Lugar= dataSnapshot.child("Lugar").getValue().toString();
                }

                idProf= dataSnapshot.child("UIDProfesor").getValue().toString();
                Materia= dataSnapshot.child("Materia").getValue().toString();
                idTuto= dataSnapshot.getKey();

                index= getRCIndex(idTuto);

                if (HoraF.equals("")) {
                    mListTutoria.set(index, new ModelTutorias(idTuto,idProf,imgTuto,Materia,Titulo,Descripcion,Profesor,Fecha,HoraI,Lugar,""));
                }else {
                    mListTutoria.set(index, new ModelTutorias(idTuto,idProf,imgTuto,Materia,Titulo,Descripcion,Profesor,Fecha,HoraI+" - "+HoraF,Lugar,""));
                }
                tutoriasAdapter.notifyItemChanged(index);







            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                Log.i("Remover","Optimus");
                Log.i("Remover",dataSnapshot.toString());

                int index=-1;
                index= getRCIndex(dataSnapshot.getKey());

                mListTutoria.remove(index);
                tutoriasAdapter.notifyItemRemoved(index);

                Log.i("Remover","Optimus");
                Log.i("Remover",dataSnapshot.toString());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private int getRCIndex(String iTuto){

        int index= -1;
        for(int i =0; i< mListTutoria.size(); i++){
            if(mListTutoria.get(i).idTuto.equals(iTuto)){
                Log.i("IndexNO",mListTutoria.get(i).idTuto+"="+iTuto);
                index=i;
                break;
            }
        }

        Log.i("IndexNO",""+index);
        return index;

    }

    public void limpiarRC() {
        mListTutoria.clear();
        tutoriasAdapter.notifyDataSetChanged();
    }

    public void goDetalles(Integer pos){


        Intent detalles = new Intent(getContext(),DetallesTutorias.class);
        detalles.putExtra("idTuto",mListTutoria.get(pos).idTuto);
        Log.i("Prueba",mListTutoria.get(pos).idTuto+" "+pos);
        detalles.putExtra("Materia",mListTutoria.get(pos).materias);
        detalles.putExtra("idProf",mListTutoria.get(pos).idProf);
        detalles.putExtra("imgTuto",mListTutoria.get(pos).foto);
        detalles.putExtra("Titulo", mListTutoria.get(pos).titulo);
        detalles.putExtra("Descripcion",mListTutoria.get(pos).descripcion);
        detalles.putExtra("Profesor",mListTutoria.get(pos).profesores);
        detalles.putExtra("Fecha",mListTutoria.get(pos).fecha);
        detalles.putExtra("Hora",mListTutoria.get(pos).hora);
        detalles.putExtra("Lugar",mListTutoria.get(pos).lugar);
        detalles.putExtra("TipoEs", "institucionales");


        startActivity(detalles);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case 0:
                goDetalles(item.getGroupId());

                break;

            case 1:

                break;

        }

        Log.i("probando", "probando"+item.getGroupId());

        return super.onContextItemSelected(item);


    }


}
