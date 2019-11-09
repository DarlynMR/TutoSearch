package com.rd.dmmr.tutosearch;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TutoriasAsist extends AppCompatActivity {


    private RecyclerView RCAsist;

    private FirebaseDatabase FDatabase;
    private DatabaseReference DBRefence,AsistReference;
    private FirebaseAuth FAuth;

    private TutoriasAdapter tutoriasAdapter;

    private List<ModelTutorias> mListTutorias;
    private FirebaseUser user;

    private List<String> list= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorias_asist);

        mListTutorias= new ArrayList<>();

        FAuth= FirebaseAuth.getInstance();

        FDatabase= FirebaseDatabase.getInstance();
        user= FAuth.getCurrentUser();

        AsistReference=FDatabase.getReference().child("UCATECI").child("Asistiran");

        RCAsist= (RecyclerView) findViewById(R.id.RCAsist);
        RCAsist.hasFixedSize();

        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        RCAsist.setLayoutManager(linearLayoutManager);

        tutoriasAdapter= new TutoriasAdapter(mListTutorias);

        RCAsist.setAdapter(tutoriasAdapter);

        TutoriasAsist();

    }


    private void TutoriasAsist(){

        AsistReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("Probando Asist", dataSnapshot.toString());

                Iterable<DataSnapshot> dataSnapshotIterable = dataSnapshot.getChildren();
                Iterator iterator = dataSnapshotIterable.iterator();
                while (iterator.hasNext()) {
                    DataSnapshot AsistEst= (DataSnapshot)iterator.next();

                    //Log.i("Probando Asist", AsistEst.child(user.getUid()).getValue(String.class));
                    if (AsistEst.child(user.getUid()).exists()) {
                        list.add(AsistEst.getKey());
                        upTutorias(AsistEst.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }



    private void upTutorias(String IdTuto){

/*
        DBRefence= FDatabase.getReference().child("UCATECI").child("Tutorias").child(IdTuto);
        DBRefence.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Materia, idProf, idTuto, imgTuto, Titulo, Descripcion, Profesor, Fecha, HoraI, HoraF,  Lugar;

                Log.i("Probando Asist","Entro");
                Log.i("Probando Asist",dataSnapshot.getKey());

                imgTuto = dataSnapshot.child("Imagen").getValue().toString();
                Titulo = dataSnapshot.child("Titulo").getValue().toString();
                Descripcion = dataSnapshot.child("Descripción").getValue().toString();
                Profesor = dataSnapshot.child("Profesor").getValue().toString();
                Fecha = dataSnapshot.child("Fecha").getValue().toString();
                HoraI = dataSnapshot.child("Hora inicial").getValue().toString();
                HoraF = dataSnapshot.child("Hora final").getValue().toString();
                Lugar = dataSnapshot.child("Lugar").getValue().toString();
                idProf = dataSnapshot.child("UIDProfesor").getValue().toString();
                Materia = dataSnapshot.child("Materia").getValue().toString();
                idTuto = dataSnapshot.getKey();

                mListTutorias.add(new ModelTutorias(idTuto, idProf, imgTuto, Materia, Titulo, Descripcion, Profesor, Fecha, HoraI + " - " + HoraF, Lugar, ""));

                tutoriasAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DBRefence= FDatabase.getReference().child("UCATECI").child("Tutorias");
        DBRefence.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //mListTutoria.add(dataSnapshot.getValue(ModelTutorias.class));/
                Log.i("LLEGANDO",dataSnapshot.toString());
                Log.i("LLEGANDO",dataSnapshot.getKey());



                for (int cont=0; cont<list.size();cont++) {

                    if (list.get(cont)==dataSnapshot.getKey()) {

                    }
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

*/
    }


}
