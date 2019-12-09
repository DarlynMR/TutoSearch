package com.rd.dmmr.tutosearch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class Tutorias extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {


    //private TabLayout tabLayout;
    //private ViewPager viewPager;


    RecyclerView RCAbajo;
    private TutoriasAdapter tutoriasAdapter;
    FirebaseFirestore fdb;

    private List<ModelTutorias> mListTutoria;
    private FloatingActionButton fBack;

    private SearchView searchView;

    private Spinner spnMaterias, spnTipoTuto;

    boolean create = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorias);

       /* tabLayout = (TabLayout) findViewById(R.id.tab_tutorias);
        viewPager = (ViewPager) findViewById(R.id.pagerTutorias);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.AddFragment(new FragmentOficial(), "Oficiales");
        viewPagerAdapter.AddFragment(new FragmentExtraoficial(), "Extraoficial");
        viewPagerAdapter.AddFragment(new FragmentHelptuto(), "Ayuda un compañero");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        */
        fBack = (FloatingActionButton) findViewById(R.id.fBackButton);
        spnMaterias = (Spinner) findViewById(R.id.spnMateria);
        spnTipoTuto = (Spinner) findViewById(R.id.spnTipoTuto);

        spnMaterias.setOnItemSelectedListener(this);
        spnTipoTuto.setOnItemSelectedListener(this);

        //Para buscar tutorias por texto

        searchView = (SearchView) findViewById(R.id.txtBuscar);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //tutoriasAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                tutoriasAdapter.getFilter().filter(s);
                return false;
            }
        });


        //-----------------------

        RCAbajo = (RecyclerView) findViewById(R.id.RCAbajo);
        //RCAbajo.setHasFixedSize(true);

        mListTutoria = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(Tutorias.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        fdb = FirebaseFirestore.getInstance();

        RCAbajo.setLayoutManager(layoutManager);

        //CrearTTutorias();

        tutoriasAdapter = new TutoriasAdapter(mListTutoria);

        RCAbajo.setAdapter(tutoriasAdapter);
        upTutorias();
        tutoriasAdapter.notifyDataSetChanged();


        fBack.setOnClickListener(this);

    }

    private void upTutorias() {

        CollectionReference ref = fdb.collection("Tutorias_institucionales");
        ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i("Listen failed.", "" + e);
                    return;
                }
                String Materia, idProf, idTuto, url_imagePortada, url_thumbPortada, Titulo, Descripcion, Profesor, timestampI, timestampF, timestampPub, Lugar, TipoTuto;

                final List<String> materias = new ArrayList<>();
                final List<String> tipotupo = new ArrayList<>();
                tipotupo.add("Live");
                tipotupo.add("Presencial");

                spnMaterias = (Spinner) findViewById(R.id.spnMateria);
                ArrayAdapter<String> materiasAdapter = new ArrayAdapter<String>(Tutorias.this, android.R.layout.simple_spinner_item, materias);
                materiasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnMaterias.setAdapter(materiasAdapter);

                spnTipoTuto = (Spinner) findViewById(R.id.spnTipoTuto);
                ArrayAdapter<String> tipotutoAdapter = new ArrayAdapter<String>(Tutorias.this, android.R.layout.simple_spinner_item, tipotupo);
                tipotutoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnTipoTuto.setAdapter(tipotutoAdapter);


                for (DocumentChange dc : snapshot.getDocumentChanges()) {
                    DocumentSnapshot docS = dc.getDocument();
                    int index = -1;
                    switch (dc.getType()) {
                        case ADDED:


                            Log.i("Probando", "" + docS.getData());


                            url_imagePortada = docS.getString("url_image_portada");
                            url_thumbPortada = docS.getString("url_thumb_image_portada");
                            Titulo = docS.getString("titulo");
                            Descripcion = docS.getString("descripcion");
                            Profesor = docS.getString("profesor");
                            timestampI = docS.getString("timestamp_inicial");
                            timestampF = docS.getString("timestamp_final");
                            timestampPub = docS.getString("timestamp_pub");

                            if (docS.getString("lugar") == null) {
                                Lugar = "";
                            } else {
                                Lugar = docS.getString("lugar");
                            }

                            idProf = docS.getString("UIDProfesor");
                            Materia = docS.getString("materia");
                            TipoTuto = docS.getString("tipo_tuto");
                            idTuto = docS.getId();

                            Log.i("Probando", "" + docS);


                            if (!materias.contains(Materia)) {
                                materias.add(Materia);
                                materiasAdapter.notifyDataSetChanged();
                            }


                            mListTutoria.add(new ModelTutorias(url_imagePortada, url_thumbPortada, idTuto, idProf, Materia, Titulo, Descripcion, Profesor, timestampI, timestampF, timestampPub, Lugar, TipoTuto));

                            tutoriasAdapter.notifyDataSetChanged();

                            break;
                        case MODIFIED:

                            Log.i("Probando", "" + docS.getData());


                            url_imagePortada = docS.getString("url_image_portada");
                            url_thumbPortada = docS.getString("url_thumb_image_portada");
                            Titulo = docS.getString("titulo");
                            Descripcion = docS.getString("descripcion");
                            Profesor = docS.getString("profesor");
                            timestampI = docS.getString("timestamp_inicial");
                            timestampF = docS.getString("timestamp_final");
                            timestampPub = docS.getString("timestamp_pub");

                            if (docS.getString("lugar") == null) {
                                Lugar = "";
                            } else {
                                Lugar = docS.getString("lugar");
                            }

                            idProf = docS.getString("UIDProfesor");
                            Materia = docS.getString("materia");
                            TipoTuto = docS.getString("tipo_tuto");
                            idTuto = docS.getId();

                            Log.i("Probando", "" + docS);

                            index = getRCIndex(idTuto);

                            mListTutoria.set(index, new ModelTutorias(url_imagePortada, url_thumbPortada, idTuto, idProf, Materia, Titulo, Descripcion, Profesor, timestampI, timestampF, timestampPub, Lugar, TipoTuto));


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


        Intent detalles = new Intent(Tutorias.this, DetallesTutorias.class);
        detalles.putExtra("idTuto", mListTutoria.get(pos).idTuto);
        Log.i("Prueba", mListTutoria.get(pos).idTuto + " " + pos);
        detalles.putExtra("Materia", mListTutoria.get(pos).materia);
        detalles.putExtra("idProf", mListTutoria.get(pos).idProf);
        detalles.putExtra("imgTuto", mListTutoria.get(pos).url_imagePortada);
        detalles.putExtra("Titulo", mListTutoria.get(pos).titulo);
        detalles.putExtra("Descripcion", mListTutoria.get(pos).descripcion);
        detalles.putExtra("Profesor", mListTutoria.get(pos).nombreProf);
        detalles.putExtra("timestampI", mListTutoria.get(pos).timestampI);
        detalles.putExtra("timestampF", mListTutoria.get(pos).timestampF);
        detalles.putExtra("timestampPub", mListTutoria.get(pos).timestampPub);
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

    @Override
    public void onClick(View view) {


        if (view == fBack) {
            onBackPressed();
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (!create) {
            create = true;
            return;
        }

        switch (adapterView.getId()) {
            case R.id.spnMateria:
                if (spnMaterias.getSelectedItem() != null) {
                    tutoriasAdapter.getFilter().filter(spnMaterias.getSelectedItem().toString());
                } else {
                    tutoriasAdapter.getFilter().filter("");
                }
                Log.i("ProbandoSPN", "Entro a la condicion");
                break;
            case R.id.spnTipoTuto:
                Log.i("ProbandoSPNProv", "Entro a la provincia");
                if (spnTipoTuto.getSelectedItem() != null) {
                    tutoriasAdapter.getFilter().filter(spnTipoTuto.getSelectedItem().toString());

                } else {
                    tutoriasAdapter.getFilter().filter("");
                }
                Log.i("ProbandoSPN", "Entro a la condicion");
                break;

        }
        Log.i("ProbandoSPN", "Entro pero posiblemente se volo la condicion");
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
