package com.rd.dmmr.tutosearch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    private FirebaseAuth FAutentic;
    private FirebaseAuth.AuthStateListener FInicionIndicdor;
    private DatabaseReference DBreference;

    private EditText JtxtCorreo;
    private EditText JtxtPassword;
    private Spinner JSpinnerUniversidad;



    private ProgressDialog progressDialog;

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


        progressDialog = new ProgressDialog(this);

        FAutentic = FirebaseAuth.getInstance();

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
                            DBreference=FirebaseDatabase.getInstance().getReference().child("Estudiantes").child(user.getUid());
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


}
