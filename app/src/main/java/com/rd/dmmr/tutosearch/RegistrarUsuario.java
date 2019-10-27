package com.rd.dmmr.tutosearch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrarUsuario extends AppCompatActivity implements View.OnClickListener {


    //firebase variables
    private FirebaseAuth FAutentic;
    private FirebaseAuth.AuthStateListener FInicionIndicdor;
    private DatabaseReference DBReference;


    private ProgressDialog progressDialog;

    //botones
    private Button JbtnRegistrar;

    //campos de datos del profesor
    private EditText  nombres,apellidos, telefono, correo, password, password2;


    private FloatingActionButton fback_button;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);


        nombres= (EditText) findViewById(R.id.txt_nombres_registrar);
        apellidos= (EditText) findViewById(R.id.txt_apellidos_registrar);
        telefono= (EditText) findViewById(R.id.txtTelefono);
        correo= (EditText) findViewById(R.id.txtCorreo);
        password= (EditText) findViewById(R.id.txtPassword);
        password2= (EditText) findViewById(R.id.txtPassword2);

        FAutentic = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        JbtnRegistrar = (Button) findViewById(R.id.btnRegistrar);

        fback_button = (FloatingActionButton) findViewById(R.id.fBackButton);


        FInicionIndicdor = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser usuario = firebaseAuth.getCurrentUser();
                if (usuario != null) {
                    Mtoast("Iniciado como "+firebaseAuth.getCurrentUser().getEmail());
                } else {
                    Mtoast("Cierre de sesión correcto");

                }
            }
        };

        fback_button.setOnClickListener(this);

       JbtnRegistrar.setOnClickListener(this);


    }

   public void Registrar(String email, String password){

        progressDialog.setMessage("Creando cuenta...");
        progressDialog.show();


        FAutentic.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                     Alerta("Ocurrio un error", "Direccion de correo en uso, falla de conexión o correo mal escrito.");
                     Log.i("Probando", task.getException().toString());

                }else {
                    Alerta("Estado de registro","Su cuenta ha sido creada correctamente, solo resta ir a su correo y " +
                            "entrar al enlace de verificación para activarla");


                    final FirebaseUser user=FAutentic.getCurrentUser();
                    DBReference= FirebaseDatabase.getInstance().getReference().child("usuarios").child("estudiantes").child(user.getUid());


                    HashMap<String,String> hashMap= new HashMap<>();
                    hashMap.put("Nombres",nombres.getText().toString());
                    hashMap.put("Apellidos",apellidos.getText().toString());
                    hashMap.put("Telefono",telefono.getText().toString());
                    hashMap.put("Correo",correo.getText().toString());


                    DBReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                FirebaseUser user= FAutentic.getCurrentUser();
                                user.sendEmailVerification();
                                FAutentic.signOut();

                                progressDialog.dismiss();
                                Intent i = new Intent(RegistrarUsuario.this, Login.class);
                                RegistrarUsuario.this.startActivity(i);

                                Mtoast("Usuario registrado correctamente");
                            }else {

                                Mtoast("Ocurrio un error al registrar el usuario");
                                user.delete();
                                FAutentic.signOut();
                                progressDialog.dismiss();
                            }

                        }
                    });


                }
                progressDialog.dismiss();
            }
        });


    }


    public void Alerta (String Titulo, String Mensaje){
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(RegistrarUsuario.this).setNegativeButton("Ok",null).create();
        alertDialog.setTitle(Titulo);
        alertDialog.setMessage(Mensaje);
        alertDialog.show();

    }

    public void Mtoast(String mensaje){

        Toast toast= Toast.makeText(RegistrarUsuario.this, mensaje,Toast.LENGTH_SHORT);
        toast.show();
    }


    @Override
    public void onClick(View view) {

        if (view==JbtnRegistrar){
            if (password.getText().equals(password2.getText())) {
                Registrar(correo.getText().toString(), password.getText().toString());
            }else  {
                Alerta("Las contraseñas no coinciden","Vuelva a escrbir la contraseña en ambos campos.");
                password.setError("Las contraseñas no coinciden.");
            }
        }
        if (view==fback_button){
            onBackPressed();
        }
    }
}
