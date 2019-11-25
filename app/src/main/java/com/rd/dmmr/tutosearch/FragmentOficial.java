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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentOficial extends Fragment {

    View view;

    RecyclerView RCAbajo;
    private TutoriasAdapter tutoriasAdapter;
    private FirebaseDatabase FDatabase;
    private DatabaseReference DBReference;
    FirebaseFirestore fdb;

    private List<ModelTutorias> mListTutoria;

    public FragmentOficial() {


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tuto_oficiales_fragment, container, false);

        FDatabase = FirebaseDatabase.getInstance();
        DBReference = FDatabase.getReference("tutorias").child("institucionales");


        RCAbajo = (RecyclerView) view.findViewById(R.id.RCAbajo);
        //RCAbajo.setHasFixedSize(true);

        mListTutoria = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        fdb = FirebaseFirestore.getInstance();

        RCAbajo.setLayoutManager(layoutManager);

        //CrearTTutorias();

        tutoriasAdapter = new TutoriasAdapter(mListTutoria);

        RCAbajo.setAdapter(tutoriasAdapter);
        upTutorias();
        tutoriasAdapter.notifyDataSetChanged();

        return view;
    }


    private void upTutorias() {

        CollectionReference ref = fdb.collection("Tutorias_institucionales");
        ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i("Listen failed.", ""+e);
                    return;
                }
                String Materia, idProf, idTuto, url_imagePortada, url_thumbPortada, Titulo, Descripcion, Profesor, Fecha, HoraI, HoraF, Lugar, TipoTuto,fecha_pub, hora_pub;



                for (DocumentChange dc : snapshot.getDocumentChanges()) {
                    DocumentSnapshot docS = dc.getDocument();
                    int index = -1;
                    switch (dc.getType()) {
                        case ADDED:


                            Log.i("Probando", ""+docS.getData());


                            url_imagePortada = docS.getString("url_image_portada");
                            url_thumbPortada = docS.getString("url_thumb_image_portada");
                            Titulo = docS.getString("titulo");
                            Descripcion = docS.getString("descripcion");
                            Profesor = docS.getString("profesor");
                            Fecha = docS.getString("fecha");
                            HoraI = docS.getString("hora_inicial");
                            HoraF = docS.getString("hora_final");
                            fecha_pub = docS.getString("fecha_pub");
                            hora_pub = docS.getString("hora_pub");
                            if (Objects.equals(docS.getString("hora_final"), "")) {
                                HoraF = "";
                            } else {
                                HoraF = docS.getString("hora_final");
                            }
                            if (docS.getString("Lugar") == null) {
                                Lugar = "";
                            } else {
                                Lugar = docS.getString("Lugar");
                            }

                            idProf = docS.getString("UIDProfesor");
                            Materia = docS.getString("materia");
                            TipoTuto = docS.getString("tipo_tuto");
                            idTuto = docS.getId();

                            Log.i("Probando", "" + docS);


                            if (Objects.equals(HoraF, null)) {
                                mListTutoria.add(new ModelTutorias(url_imagePortada,  url_thumbPortada,  idTuto,  idProf,  Materia,  Titulo,  Descripcion,  Profesor,  Fecha,  HoraI ,  Lugar,  TipoTuto,  fecha_pub,  hora_pub));
                            } else {
                                mListTutoria.add(new ModelTutorias(url_imagePortada,  url_thumbPortada,  idTuto,  idProf,  Materia,  Titulo,  Descripcion,  Profesor,  Fecha,  HoraI+ " - " + HoraF ,  Lugar,  TipoTuto,  fecha_pub,  hora_pub));
                            }
                            tutoriasAdapter.notifyDataSetChanged();

                            break;
                        case MODIFIED:

                            Log.i("Probando", ""+docS.getData());


                            url_imagePortada = docS.getString("url_image_portada");
                            url_thumbPortada = docS.getString("url_thumb_image_portada");
                            Titulo = docS.getString("titulo");
                            Descripcion = docS.getString("descripcion");
                            Profesor = docS.getString("profesor");
                            Fecha = docS.getString("fecha");
                            HoraI = docS.getString("hora_inicial");
                            HoraF = docS.getString("hora_final");
                            fecha_pub = docS.getString("fecha_pub");
                            hora_pub = docS.getString("hora_pub");
                            if (Objects.equals(docS.getString("hora_final"), "")) {
                                HoraF = "";
                            } else {
                                HoraF = docS.getString("hora_final");
                            }
                            if (docS.getString("Lugar") == null) {
                                Lugar = "";
                            } else {
                                Lugar = docS.getString("Lugar");
                            }

                            idProf = docS.getString("UIDProfesor");
                            Materia = docS.getString("materia");
                            TipoTuto = docS.getString("tipo_tuto");
                            idTuto = docS.getId();

                            Log.i("Probando", "" + docS);

                            index = getRCIndex(idTuto);

                            if (Objects.equals(HoraF, null)) {
                                mListTutoria.set(index, new ModelTutorias(url_imagePortada,  url_thumbPortada,  idTuto,  idProf,  Materia,  Titulo,  Descripcion,  Profesor,  Fecha,  HoraI ,  Lugar,  TipoTuto,  fecha_pub,  hora_pub));
                            } else {
                                mListTutoria.set(index, new ModelTutorias(url_imagePortada,  url_thumbPortada,  idTuto,  idProf,  Materia,  Titulo,  Descripcion,  Profesor,  Fecha,  HoraI+ " - " + HoraF ,  Lugar,  TipoTuto,  fecha_pub,  hora_pub));
                            }


                            tutoriasAdapter.notifyItemChanged(index);
                            break;
                        case REMOVED:


                            index = getRCIndex(docS.getId());

                            mListTutoria.remove(index);
                            tutoriasAdapter.notifyItemRemoved(index);

                            break;
                    }
                }

            }
        });



/*
        DBReference.addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded (DataSnapshot dataSnapshot, String s){

            String Materia, idProf, idTuto, imgTuto, Titulo, Descripcion, Profesor, Fecha, HoraI, HoraF, Lugar, TipoTuto;


            imgTuto = dataSnapshot.child("image").getValue().toString();
            Titulo = dataSnapshot.child("Titulo").getValue().toString();
            Descripcion = dataSnapshot.child("Descripción").getValue().toString();
            Profesor = dataSnapshot.child("Profesor").getValue().toString();
            Fecha = dataSnapshot.child("Fecha").getValue().toString();
            HoraI = dataSnapshot.child("Hora inicial").getValue().toString();
            if (dataSnapshot.child("Hora final").getValue() == null) {
                HoraF = "";
            } else {
                HoraF = dataSnapshot.child("Hora final").getValue().toString();
            }
            if (dataSnapshot.child("Lugar").getValue() == null) {
                Lugar = "";
            } else {
                Lugar = dataSnapshot.child("Lugar").getValue().toString();
            }

            idProf = dataSnapshot.child("UIDProfesor").getValue().toString();
            Materia = dataSnapshot.child("Materia").getValue().toString();
            TipoTuto = dataSnapshot.child("tipo_tuto").getValue().toString();
            idTuto = dataSnapshot.getKey();

            Log.i("Probando", "" + dataSnapshot);
            Log.i("Probando", "" + dataSnapshot.getChildren());
            Log.i("Probando", "" + dataSnapshot.getRef().getParent().getKey());
            Log.i("Probando", "" + dataSnapshot.getKey());

            if (HoraF.equals("")) {
                mListTutoria.add(new ModelTutorias(idTuto, idProf, imgTuto, Materia, Titulo, Descripcion, Profesor, Fecha, HoraI, Lugar, TipoTuto));
            } else {
                mListTutoria.add(new ModelTutorias(idTuto, idProf, imgTuto, Materia, Titulo, Descripcion, Profesor, Fecha, HoraI + " - " + HoraF, Lugar, TipoTuto));
            }
            tutoriasAdapter.notifyDataSetChanged();


        }

        @Override
        public void onChildChanged (DataSnapshot dataSnapshot, String s){
            ModelTutorias modelTutorias = dataSnapshot.getValue(ModelTutorias.class);

            Log.i("CAMBIANDO", dataSnapshot.toString() + " " + s);

            String Materia, idProf, idTuto, imgTuto, Titulo, Descripcion, Profesor, Fecha, HoraI, HoraF, Lugar, TipoTuto;
            int index = -1;


            imgTuto = dataSnapshot.child("image").getValue().toString();
            Titulo = dataSnapshot.child("Titulo").getValue().toString();
            Descripcion = dataSnapshot.child("Descripción").getValue().toString();
            Profesor = dataSnapshot.child("Profesor").getValue().toString();
            Fecha = dataSnapshot.child("Fecha").getValue().toString();
            HoraI = dataSnapshot.child("Hora inicial").getValue().toString();
            if (dataSnapshot.child("Hora final").getValue() == null) {
                HoraF = "";
            } else {
                HoraF = dataSnapshot.child("Hora final").getValue().toString();
            }
            if (dataSnapshot.child("Lugar").getValue() == null) {
                Lugar = "";
            } else {
                Lugar = dataSnapshot.child("Lugar").getValue().toString();
            }

            idProf = dataSnapshot.child("UIDProfesor").getValue().toString();
            Materia = dataSnapshot.child("Materia").getValue().toString();
            TipoTuto = dataSnapshot.child("tipo_tuto").getValue().toString();
            idTuto = dataSnapshot.getKey();

            index = getRCIndex(idTuto);

            if (HoraF.equals("")) {
                mListTutoria.set(index, new ModelTutorias(idTuto, idProf, imgTuto, Materia, Titulo, Descripcion, Profesor, Fecha, HoraI, Lugar, TipoTuto));
            } else {
                mListTutoria.set(index, new ModelTutorias(idTuto, idProf, imgTuto, Materia, Titulo, Descripcion, Profesor, Fecha, HoraI + " - " + HoraF, Lugar, TipoTuto));
            }
            tutoriasAdapter.notifyItemChanged(index);


        }

        @Override
        public void onChildRemoved (DataSnapshot dataSnapshot){

            Log.i("Remover", "Optimus");
            Log.i("Remover", dataSnapshot.toString());

            int index = -1;
            index = getRCIndex(dataSnapshot.getKey());

            mListTutoria.remove(index);
            tutoriasAdapter.notifyItemRemoved(index);

            Log.i("Remover", "Optimus");
            Log.i("Remover", dataSnapshot.toString());
        }

        @Override
        public void onChildMoved (DataSnapshot dataSnapshot, String s){

        }

        @Override
        public void onCancelled (DatabaseError databaseError){

        }
    });

 */
}

    private int getRCIndex(String iTuto) {

        int index = -1;
        for (int i = 0; i < mListTutoria.size(); i++) {
            if (mListTutoria.get(i).idTuto.equals(iTuto)) {

                index = i;
                break;
            }
        }

        return index;

    }

    public void limpiarRC() {
        mListTutoria.clear();
        tutoriasAdapter.notifyDataSetChanged();
    }

    public void goDetalles(Integer pos) {


        Intent detalles = new Intent(getContext(), DetallesTutorias.class);
        detalles.putExtra("idTuto", mListTutoria.get(pos).idTuto);
        Log.i("Prueba", mListTutoria.get(pos).idTuto + " " + pos);
        detalles.putExtra("Materia", mListTutoria.get(pos).materia);
        detalles.putExtra("idProf", mListTutoria.get(pos).idProf);
        detalles.putExtra("imgTuto", mListTutoria.get(pos).url_imagePortada);
        detalles.putExtra("Titulo", mListTutoria.get(pos).titulo);
        detalles.putExtra("Descripcion", mListTutoria.get(pos).descripcion);
        detalles.putExtra("Profesor", mListTutoria.get(pos).nombreProf);
        detalles.putExtra("Fecha", mListTutoria.get(pos).fecha);
        detalles.putExtra("Hora", mListTutoria.get(pos).hora);
        detalles.putExtra("Lugar", mListTutoria.get(pos).lugar);
        detalles.putExtra("TipoEs", "institucionales");


        startActivity(detalles);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 0:
                goDetalles(item.getGroupId());

                break;

            case 1:

                break;

        }

        Log.i("probando", "probando" + item.getGroupId());

        return super.onContextItemSelected(item);


    }


}
