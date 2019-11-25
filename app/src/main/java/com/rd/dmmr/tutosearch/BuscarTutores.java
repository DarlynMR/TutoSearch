package com.rd.dmmr.tutosearch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

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

public class BuscarTutores extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FirebaseFirestore fdb;

    RecyclerView RCTutores;
    private AdapterTutores adapterTutores;
    private FirebaseDatabase FDatabase;

    private Spinner spnProvinciasBuscar, spnMateriasBuscar;
    private Button btnLimpiar;

    private List<ModelTutores> mListTutores;
    private boolean create = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_tutores);



        spnProvinciasBuscar = (Spinner) findViewById(R.id.spnProvinciaBuscar);
        spnMateriasBuscar = (Spinner) findViewById(R.id.spnMateriaBuscar);
        btnLimpiar = (Button) findViewById(R.id.btnLimpiar);
        RCTutores = (RecyclerView) findViewById(R.id.RCProfesores);
        //RCAbajo.setHasFixedSize(true);

        mListTutores = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(BuscarTutores.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        fdb = FirebaseFirestore.getInstance();

        RCTutores.setLayoutManager(layoutManager);

        //CrearTTutorias();

        spnMateriasBuscar.setOnItemSelectedListener(this);

        adapterTutores = new AdapterTutores(mListTutores);
        RCTutores.setAdapter(adapterTutores);
        upTutores();
        adapterTutores.notifyDataSetChanged();
        fdb = FirebaseFirestore.getInstance();

    }


    private void upTutores() {

        CollectionReference ref = fdb.collection("Profesores");
        ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i("Listen failed.", "" + e);
                    return;
                }

                final List<String> materias = new ArrayList<>();


                spnMateriasBuscar = (Spinner) findViewById(R.id.spnMateriaBuscar);
                ArrayAdapter<String> materiasAdapter = new ArrayAdapter<String>(BuscarTutores.this, android.R.layout.simple_spinner_item, materias);
                materiasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnMateriasBuscar.setAdapter(materiasAdapter);

                for (DocumentChange dc : snapshot.getDocumentChanges()) {
                    DocumentSnapshot docS = dc.getDocument();

                    ModelTutores modelTutores = docS.toObject(ModelTutores.class);

                    int index = -1;
                    switch (dc.getType()) {
                        case ADDED:


                            Log.i("Probando", "" + docS.getData());

                            if (modelTutores.getMaterias()!=null){
                                for (String mate : modelTutores.getMaterias()){
                                    if (!materias.contains(mate)){
                                        materias.add(mate);
                                        materiasAdapter.notifyDataSetChanged();
                                    }
                                }
                            }

                            //ArrayList<String> list = (ArrayList) docS.get("Materias");

                            mListTutores.add(new ModelTutores(docS.getId(), modelTutores.getNombres(), modelTutores.getApellidos(), modelTutores.getAbout_me(),
                                    modelTutores.getProvincia(), modelTutores.getUrl_pic(), modelTutores.getUrl_thumb_pic(), (ArrayList) modelTutores.getMaterias()));

                            adapterTutores.notifyDataSetChanged();

                            break;
                        case MODIFIED:


                            index = getRCIndex(docS.getId());


                            mListTutores.set(index, new ModelTutores(docS.getId(), modelTutores.getNombres(), modelTutores.getApellidos(), modelTutores.getAbout_me(),
                                    modelTutores.getProvincia(), modelTutores.getUrl_pic(), modelTutores.getUrl_thumb_pic(), (ArrayList) modelTutores.getMaterias()));


                            adapterTutores.notifyItemChanged(index);
                            break;
                        case REMOVED:


                            index = getRCIndex(docS.getId());

                            mListTutores.remove(index);
                            adapterTutores.notifyItemRemoved(index);

                            break;
                    }
                }

            }
        });


    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        if(!create){
            create = true;
            return;
        }

        switch (parent.getId()){
            case R.id.spnMateriaBuscar:
                if (spnMateriasBuscar.getSelectedItem()!=null) {
                    adapterTutores.getFilter().filter(spnMateriasBuscar.getSelectedItem().toString());
                }
                Log.i("ProbandoSPN", "Entro a la condicion");
                break;

        }
        Log.i("ProbandoSPN", "Entro pero posiblemente se volo la condicion");

    }

    public void onNothingSelected(AdapterView<?> parent) {

    }

    private int getRCIndex(String iTutor) {

        int index = -1;
        for (int i = 0; i < mListTutores.size(); i++) {
            if (mListTutores.get(i).UIDProf.equals(iTutor)) {

                index = i;
                break;
            }
        }

        return index;

    }

   /* protected class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        private SpinnerSelectedListener() {
            super();
        }

        public SpinnerSelectedListener(String initialValue) {
            this();
            this.initialValue = initialValue;
        }

        private String initialValue;

        // getter and setter removed.

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            final String newValue = (String) spinHeight.getItemAtPosition(position);
            if (newValue.equals(initialValue) == false) {
                // Add your code here.  The spinner has changed value.

                // Maybe useful.
                // initialValue = newValue;
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Maybe useful.
            // initialValue = null;
        }
    }
*/
}
