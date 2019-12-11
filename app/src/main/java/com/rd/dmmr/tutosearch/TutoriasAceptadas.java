package com.rd.dmmr.tutosearch;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

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

public class TutoriasAceptadas extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    FirebaseFirestore fdb;
    FirebaseUser FUser;

    RecyclerView RCAbajo;

    private TutoriasAceptadasAdapter tutoriasAceptadasAdapter;

    private List<ModelTutoriasEst> mListTutoria;
    private FloatingActionButton fBack;

    private boolean create = false;

    private Spinner spnMateriasBuscar;

    private Button btnLimpiar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorias_aceptadas);

        RCAbajo = (RecyclerView) findViewById(R.id.RCAbajo);
        fBack = (FloatingActionButton) findViewById(R.id.fBackButton);
        fBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        spnMateriasBuscar = (Spinner) findViewById(R.id.spnMateria);
        spnMateriasBuscar.setOnItemSelectedListener(this);

        btnLimpiar = (Button) findViewById(R.id.btnLimpiar);
        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutoriasAceptadasAdapter.getFilter().filter("");

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

                final List<String> materias = new ArrayList<>();

                spnMateriasBuscar = (Spinner) findViewById(R.id.spnMateria);
                final ArrayAdapter<String> materiasAdapter = new ArrayAdapter<String>(TutoriasAceptadas.this, android.R.layout.simple_spinner_item, materias);
                materiasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnMateriasBuscar.setAdapter(materiasAdapter);

                for (DocumentChange dcList : queryDocumentSnapshots.getDocumentChanges()) {
                    final DocumentSnapshot docS = dcList.getDocument();

                    int index = -1;
                    switch (dcList.getType()) {
                        case ADDED:

                            DocumentReference docRef = fdb.collection("Tutorias_institucionales").document(docS.getId());
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    DocumentSnapshot docS = task.getResult();
                                    ModelTutoriasEst modelTutoriasEst = docS.toObject(ModelTutoriasEst.class);

                                    if (modelTutoriasEst.getMateria() != null) {
                                        if (!materias.contains(modelTutoriasEst.getMateria())) {
                                            materias.add(modelTutoriasEst.getMateria());
                                            materiasAdapter.notifyDataSetChanged();
                                        }
                                    }
                                    Log.i("AceptadasProf", "" + docS.getString("profesor"));
                                    if (modelTutoriasEst.getTipo_tuto().equals("Live")) {
                                        mListTutoria.add(new ModelTutoriasEst(docS.getId(), modelTutoriasEst.getTitulo(), modelTutoriasEst.getDescripcion(), modelTutoriasEst.getBroadcastId(), docS.getString("timestamp_inicial"), docS.getString("timestamp_final"), docS.getString("timestamp_pub"), modelTutoriasEst.getMateria(), modelTutoriasEst.getTipo_tuto(), modelTutoriasEst.getUrl_image_portada(), modelTutoriasEst.getUrl_thumb_image_portada(), "", docS.getString("profesor")));
                                    } else {
                                        mListTutoria.add(new ModelTutoriasEst(docS.getId(), modelTutoriasEst.getTitulo(), modelTutoriasEst.getDescripcion(), modelTutoriasEst.getBroadcastId(), docS.getString("timestamp_inicial"), docS.getString("timestamp_final"), docS.getString("timestamp_pub"), modelTutoriasEst.getMateria(), modelTutoriasEst.getTipo_tuto(), modelTutoriasEst.getUrl_image_portada(), modelTutoriasEst.getUrl_thumb_image_portada(), modelTutoriasEst.getLugar(),docS.getString("profesor")));
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


    }


    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        if (!create) {
            create = true;
            return;
        }

        if (parent.getId() == R.id.spnMateria) {
            if (spnMateriasBuscar.getSelectedItem() != null) {
                tutoriasAceptadasAdapter.getFilter().filter(spnMateriasBuscar.getSelectedItem().toString());
            } else {
                tutoriasAceptadasAdapter.getFilter().filter("");
            }
            Log.i("ProbandoSPN", "Entro a la condicion");
        }
        Log.i("ProbandoSPN", "Entro pero posiblemente se volo la condicion");

    }

    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
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
