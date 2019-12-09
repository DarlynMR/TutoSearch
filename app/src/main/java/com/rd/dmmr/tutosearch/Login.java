package com.rd.dmmr.tutosearch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth FAutentic;
    private FirebaseAuth.AuthStateListener FInicionIndicdor;
    private DatabaseReference DBreference;

    private FirebaseFirestore fdb;

    private EditText JtxtCorreo;
    private EditText JtxtPassword;
    private Spinner JSpinnerUniversidad;



    private ProgressDialog progressDialog;
    private TextView txtRecuperarPass;

    private Button btnEntrar;
    private Button btnRegistrar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        JtxtCorreo = (EditText) findViewById(R.id.txtCorreo);
        JtxtPassword = (EditText) findViewById(R.id.txtPassword);

        btnEntrar = (Button)   findViewById(R.id.btnEntrarLogin);
        btnRegistrar = (Button) findViewById(R.id.btnRegistroLogin);

        txtRecuperarPass = (TextView) findViewById(R.id.txtRecuperarPass);

        fdb = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);

        FAutentic = FirebaseAuth.getInstance();


        //Slider
        ImageSlider imageSlider= findViewById(R.id.lgnSlider);
        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.primer_plano_manos, "Aprende desde tu móvil"));
        slideModels.add(new SlideModel(R.drawable.tutoria_exterior, "Reúnete y aprende con un maestro"));
        slideModels.add(new SlideModel(R.drawable.live_stream_tool_blog, "Mira tutorías en vivo"));
        imageSlider.setImageList(slideModels, true);

        FInicionIndicdor = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser usuario = firebaseAuth.getCurrentUser();


                if (usuario != null) {
                    Mtoast("Iniciado como " + firebaseAuth.getCurrentUser().getEmail());
                }
            }
        };


        btnEntrar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Login(JtxtCorreo.getText().toString(),JtxtPassword.getText().toString());
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent activityChangeIntent = new Intent(Login.this, RegistrarUsuario.class);
                Login.this.startActivity(activityChangeIntent);
            }
        });
        txtRecuperarPass.setOnClickListener(this);





    }


    private void goMainScreen(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void Login(String Usuario,String Password){

        if (TextUtils.isEmpty(Usuario)){
            JtxtCorreo.setError("Campo vacío, por favor escriba el correo");
            return;
        }
        if (TextUtils.isEmpty(Password)){
            JtxtPassword.setError("Campo vacío, por favor escriba la contraseña");
            return;
        }

        final SharedPreferences pref = getSharedPreferences("EstudiantePref", Context.MODE_PRIVATE);

        final SharedPreferences.Editor editor = pref.edit();


        progressDialog.setMessage("Iniciando sesión...");
        progressDialog.show();



        FAutentic.signInWithEmailAndPassword(Usuario, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override

                    public void onComplete(@NonNull Task<AuthResult> task) {

                        FirebaseUser user = FAutentic.getCurrentUser();


                        if(user != null && !user.isEmailVerified()){
                            Toast.makeText(Login.this,"Esta cuenta aun no ha sido verificada o no existe", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            FAutentic.signOut();
                            return;
                        }
                        if (task.isSuccessful()) {

                            Toast.makeText(Login.this, "Inicio de sesión exitoso.",
                                    Toast.LENGTH_SHORT).show();
                            DBreference=FirebaseDatabase.getInstance().getReference().child("usuarios").child("estudiantes").child(user.getUid());
                            DBreference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    editor.putString("Matricula",dataSnapshot.child("Matricula").getValue(String.class));
                                    editor.putString("Nombre",dataSnapshot.child("Nombre").getValue(String.class));
                                    editor.putString("Correo",dataSnapshot.child("Correo").getValue(String.class));
                                    editor.putString("Carrera",dataSnapshot.child("Carrera").getValue(String.class));
                                    editor.putString("Telefono",dataSnapshot.child("Telefono").getValue(String.class));
                                    editor.commit();

                                    Intent intent= new Intent(Login.this, pantalla_principal.class);
                                    startActivity(intent);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            DocumentReference estudianteRef = fdb.collection("Estudiantes").document(user.getUid());
                            estudianteRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                                    DocumentSnapshot dc = task.getResult();

                                    Log.i("Prueba",  dc.getString("nombres"));
                                    editor.putString("Nombre",dc.getString("nombres"));
                                    editor.putString("Correo",dc.getString("correo"));
                                    editor.putString("Telefono",dc.getString("telefono"));
                                    editor.apply();
                                    // }

                                }
                            });



                            Toast.makeText(Login.this, "Inicio de sesión exitoso.",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            Intent intent = new Intent(Login.this, pantalla_principal.class);
                            Login.this.startActivity(intent);



                            progressDialog.dismiss();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, "Fallo en el inicio de sesión",
                                    Toast.LENGTH_SHORT).show();
                            Alerta("Error", ""+task.getException().toString());

                            progressDialog.dismiss();
                        }

                        // ...
                    }

                });


    }

    private void abrirDialogRecuperarPass() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recuperar contraseña");



        LinearLayout linearLayout = new LinearLayout(this);
        final EditText txtEmailSent = new EditText(this);
        txtEmailSent.setHint("Correo");
        txtEmailSent.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        txtEmailSent.setMinEms(16);

        linearLayout.addView(txtEmailSent);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);

        builder.setPositiveButton("Solicitar recuperación", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String email = txtEmailSent.getText().toString().trim();
                startRecoveEmail(email);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();

    }

    private void startRecoveEmail(String email) {
        progressDialog.setMessage("Enviando correo recuperación...");
        progressDialog.show();
        FAutentic.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(Login.this, "Se envio un correo de recuperación, por favor revise su correo", Toast.LENGTH_LONG).show();
                }else  {
                    Toast.makeText(Login.this, "Fallo al enviar el correo de recuperación", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FAutentic.addAuthStateListener(FInicionIndicdor);


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (FInicionIndicdor!=null)
        {
        FAutentic.removeAuthStateListener(FInicionIndicdor);


        }
    }

    public void Alerta (String Titulo, String Mensaje){
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(Login.this).setNegativeButton("Ok",null).create();
        alertDialog.setTitle(Titulo);
        alertDialog.setMessage(Mensaje);
        alertDialog.show();

    }


    public void Mtoast(String mensaje){

        Toast toast= Toast.makeText(Login.this, mensaje,Toast.LENGTH_LONG);
        toast.show();
    }


    @Override
    public void onClick(View view) {
        if (view==txtRecuperarPass){
            abrirDialogRecuperarPass();
        }
    }
}
