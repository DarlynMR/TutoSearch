package com.rd.dmmr.tutosearch;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TutoriasAceptadas extends AppCompatActivity {


    FirebaseFirestore fdb;
    FirebaseUser FUser;

    RecyclerView RCAbajo;

    private TutoriasAceptadasAdapter tutoriasAceptadasAdapter;

    private List<ModelTutoriasEst> mListTutoria;
    private FloatingActionButton fBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorias_aceptadas);

        RCAbajo = (RecyclerView) findViewById(R.id.RCAbajo);
        fBack= (FloatingActionButton) findViewById(R.id.fBackButton);
        fBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        fdb = FirebaseFirestore.getInstance();
        FUser = FirebaseAuth.getInstance().getCurrentUser();

        mListTutoria = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(TutoriasAceptadas.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        RCAbajo.setLayoutManager(layoutManager);

        //CrearTTutorias();

        tutoriasAceptadasAdapter = new TutoriasAceptadasAdapter(mListTutoria);

        RCAbajo.setAdapter(tutoriasAceptadasAdapter);
        tutoriasAceptadasAdapter.notifyDataSetChanged();

        upTutorias();

    }


    private void upTutorias() {

        CollectionReference refList = fdb.collection("Estudiantes").document(FUser.getUid()).collection("Lista_asistir");

        refList.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for (DocumentChange dcList : queryDocumentSnapshots.getDocumentChanges()) {
                    DocumentSnapshot docS = dcList.getDocument();

                    int index = -1;
                    switch (dcList.getType()) {
                        case ADDED:

                            DocumentReference docRef = fdb.collection("Tutorias_institucionales").document(docS.getId());
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    DocumentSnapshot docS = task.getResult();
                                    ModelTutoriasEst modelProf = docS.toObject(ModelTutoriasEst.class);

                                    Log.i("Aceptadas", ""+docS);
                                    if (modelProf.getTipo_tuto().equals("Live")) {
                                        mListTutoria.add(new ModelTutoriasEst(docS.getId(), modelProf.getTitulo(), modelProf.getDescripcion(), modelProf.getBroadcastId(), docS.getString("timestamp_inicial"), docS.getString("timestamp_final"), docS.getString("timestamp_pub") , modelProf.getMateria(), modelProf.getTipo_tuto(), modelProf.getUrl_image_portada(), modelProf.getUrl_thumb_image_portada(), ""));
                                    } else {
                                        mListTutoria.add(new ModelTutoriasEst(docS.getId(), modelProf.getTitulo(), modelProf.getDescripcion(), modelProf.getBroadcastId(), docS.getString("timestamp_inicial"), docS.getString("timestamp_final"), docS.getString("timestamp_pub"), modelProf.getMateria(), modelProf.getTipo_tuto(), modelProf.getUrl_image_portada(), modelProf.getUrl_thumb_image_portada(), modelProf.getLugar()));
                                    }
                                    tutoriasAceptadasAdapter.notifyDataSetChanged();

                                }
                            });

                            break;
                        case MODIFIED:


                            break;

                        case REMOVED:
                            index = getRCIndex(docS.getId());

                            mListTutoria.remove(index);
                            tutoriasAceptadasAdapter.notifyItemRemoved(index);
                            break;

                    }
                }
            }
        });



/*
        CollectionReference ref = fdb.collection("Tutorias_institucionales");
        Query queryTuto = ref.whereEqualTo(FieldPath.documentId(),"-LvCxM4swRgKFctKELwe");


        queryTuto.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i("Listen failed.", "" + e);
                    return;
                }


                for (DocumentChange dc : snapshot.getDocumentChanges()) {
                    DocumentSnapshot docS = dc.getDocument();


                    ModelTutoriasEst modelProf = docS.toObject(ModelTutoriasEst.class);
                    Log.i("probando", ""+docS);

                    int index = -1;
                    switch (dc.getType()) {
                        case ADDED:



                            if (modelProf.getTipo_tuto().equals("Live")) {
                                mListTutoria.add(new ModelTutoriasEst(docS.getId(), modelProf.getTitulo(), modelProf.getDescripcion(), modelProf.getBroadcastId(), docS.getString("timestamp_inicial"), docS.getString("timestamp_final"), docS.getString("timestamp_pub") , modelProf.getMateria(), modelProf.getTipo_tuto(), modelProf.getUrl_image_portada(), modelProf.getUrl_thumb_image_portada(), ""));
                            } else {
                                mListTutoria.add(new ModelTutoriasEst(docS.getId(), modelProf.getTitulo(), modelProf.getDescripcion(), modelProf.getBroadcastId(), docS.getString("timestamp_inicial"), docS.getString("timestamp_final"), docS.getString("timestamp_pub"), modelProf.getMateria(), modelProf.getTipo_tuto(), modelProf.getUrl_image_portada(), modelProf.getUrl_thumb_image_portada(), modelProf.getLugar()));
                            }
                            tutoriasAceptadasAdapter.notifyDataSetChanged();

                            break;
                        case MODIFIED:

                            index = getRCIndex(docS.getId());

                            if (modelProf.getTipo_tuto().equals("Live")) {
                                mListTutoria.set(index, new ModelTutoriasEst(docS.getId(), modelProf.getTitulo(), modelProf.getDescripcion(), modelProf.getBroadcastId(),docS.getString("timestamp_inicial"), docS.getString("timestamp_final"), docS.getString("timestamp_pub"), modelProf.getMateria(), modelProf.getTipo_tuto(), modelProf.getUrl_image_portada(), modelProf.getUrl_thumb_image_portada(), ""));
                            } else {
                                mListTutoria.add(index, new ModelTutoriasEst(docS.getId(), modelProf.getTitulo(), modelProf.getDescripcion(), modelProf.getBroadcastId(),docS.getString("timestamp_inicial"), docS.getString("timestamp_final"), docS.getString("timestamp_pub"), modelProf.getMateria(), modelProf.getTipo_tuto(), modelProf.getUrl_image_portada(), modelProf.getUrl_thumb_image_portada(), modelProf.getLugar()));
                            }
                            tutoriasAceptadasAdapter.notifyDataSetChanged();


                            tutoriasAceptadasAdapter.notifyItemChanged(index);
                            break;
                        case REMOVED:


                            index = getRCIndex(docS.getId());

                            mListTutoria.remove(index);
                            tutoriasAceptadasAdapter.notifyItemRemoved(index);

                            break;
                    }
                }

            }
        });
*/

    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case 0:
                return true;

            case 1:

                break;

        }
        return super.onContextItemSelected(item);
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

}
