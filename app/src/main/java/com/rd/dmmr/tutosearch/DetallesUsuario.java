package com.rd.dmmr.tutosearch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import id.zelory.compressor.Compressor;

public class DetallesUsuario extends AppCompatActivity {


    private DatabaseReference DBReference;


    private FirebaseUser mCurrentUser;
    private TextView txtNombre, txtMatricula, txtCarrera, txtCorreo, txtTelefono;
    private static int currentPage = 0;
    private static final int GALERRY_PICK = 1;
    private ProgressDialog mProgressDialog;
    private static ViewPager mPager;


    private StorageReference mImageStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_detalles_usuario);
            imagen_slider();

            mImageStorage = FirebaseStorage.getInstance().getReference();
            //llamando la base de datos
            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
            //identificando el id del que tiene la sesion iniciada
            final String current_uid = mCurrentUser.getUid();
            //especificando donde se buscara
            DBReference = FirebaseDatabase.getInstance().getReference().child("UCATECI").child("Estudiantes").child(current_uid);
            DBReference.keepSynced(true);

            txtNombre = (TextView) findViewById(R.id.txtNombre);
            txtMatricula = (TextView) findViewById(R.id.txtMatricula);
            txtCorreo = (TextView) findViewById(R.id.txtCorreo);
            txtCarrera = (TextView) findViewById(R.id.txtCarrera);
            txtTelefono = (TextView) findViewById(R.id.txtTelefono);

            //LlAMANDO EL TOOLBAR
            final Toolbar toolbar = (Toolbar) findViewById(R.id.MyToolbar);
            //  toolbar.setTitleTextColor(Color.parseColor("#00FF00"));
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            //Colapsando la barra
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
            collapsingToolbarLayout.setTitle("Estudiante");
            collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
            collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);

            //Color de la barra
            Context context = this;
            collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(context, R.color.colorPrimary));

            //llenando la pantalla con los datos del doctor
            DBReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Toast.makeText(Detalles_Usuario.this,dataSnapshot.toString(),Toast.LENGTH_LONG).show();
                    String name = dataSnapshot.child("Nombre").getValue().toString();
                    String apellido = dataSnapshot.child("Apellido").getValue().toString();
                    String telefono = dataSnapshot.child("Telefono").getValue().toString();
                    String carrera = dataSnapshot.child("Carrera").getValue().toString();
                    String correo = dataSnapshot.child("Correo").getValue().toString();

                    CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
                    collapsingToolbarLayout.setTitle(name + " " + apellido);

                    //para separar el numero de telefono
                    String te = telefono.replace(" ", "");
                    String tress = te.substring(0, 3);
                    String seis = te.substring(3, 6);
                    String cuatro = te.substring(6, 10);
                    String telefonoo = tress + "-" + seis + "-" + cuatro;

                    txtTelefono.setText(telefonoo);
                    txtCarrera.setText(carrera);
                    txtCorreo.setText(correo);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(DetallesUsuario.this, "Error de la base de datos", Toast.LENGTH_SHORT).show();
                }
            });


            //boton agregar imagen
            final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fltImage);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(galleryIntent, "Seleccione una imagen"), GALERRY_PICK);
                     /* .setAction("Action", null).show();
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(Detalles_Usuario.this);*/


                }
            });

        } catch (Exception e) {

            Toast.makeText(DetallesUsuario.this, "Error", Toast.LENGTH_LONG).show();
        }
    }

    private void imagen_slider() {
        try{

            final Integer[] imagen = {R.drawable.defaultimage};
            ArrayList<Integer> imagenes = new ArrayList<Integer>();

            for (int i = 0; i < imagen.length; i++)
                imagenes.add(imagen[i]);

            mPager = (ViewPager) findViewById(R.id.pager);
            mPager.setAdapter(new AdapterImage(DetallesUsuario.this, imagenes));

        }catch (Exception e){
            Toast.makeText(DetallesUsuario.this,"Error al cargar la imagen", Toast.LENGTH_SHORT).show();
        }
    }
    //para elegir la imagen del usuario
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == GALERRY_PICK && resultCode == RESULT_OK) {
                Uri imageurl = data.getData();

                CropImage.activity(imageurl)
                        .setAspectRatio(1, 1)
                        .start(this);

                //Toast.makeText(Detalles_Usuario.this, imageurl,Toast.LENGTH_LONG).show();
            }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                Uri resultUri = result.getUri();

                //CREANDO EL PROGREES BAR
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setTitle("Cargando imagen...");
                mProgressDialog.setMessage("Cargando imagen espere mientras se carga la imagen.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                String current_user_id=mCurrentUser.getUid();

                File thumb_filePath = new File(resultUri.getPath());

                final Bitmap thumb_bitmap=new Compressor(this)
                        .setMaxWidth(100)
                        .setMaxHeight(100)
                        .setQuality(100)
                        .compressToBitmap(thumb_filePath);

                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                final byte[] thumb_byte=baos.toByteArray();

                StorageReference filepath=mImageStorage.child("estudiantes_imagenes").child(current_user_id + ".jpg");
                final StorageReference thumb_filepath =mImageStorage.child("estudiantes_imagenes").child("thumbs").child(current_user_id+".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            final String download_url=task.getResult().getStorage().getDownloadUrl().toString();

                            UploadTask uploadTask=thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_downloadUrl=thumb_task.getResult().getStorage().getDownloadUrl().toString();

                                    if(thumb_task.isSuccessful()){

                                        Map update_hashmMap=new HashMap();
                                        update_hashmMap.put("image",download_url);
                                        update_hashmMap.put("thumb_image",thumb_downloadUrl);

                                        DBReference.updateChildren(update_hashmMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(DetallesUsuario.this,"Imagen Subida.",Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });

                                    }else{
                                        Toast.makeText(DetallesUsuario.this,"Error al subir la imagen.",Toast.LENGTH_LONG).show();
                                        mProgressDialog.dismiss();
                                    }
                                }
                            });

                        }else{
                            Toast.makeText(DetallesUsuario.this,"Error al subir la imagen.",Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                        }
                    }
                });

            }
        }catch (Exception e){
            Toast.makeText(DetallesUsuario.this,"No se cambio la imagen!",Toast.LENGTH_LONG).show();
        }
    }

    public static String random(){
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLegth=generator.nextInt(10);
        char tempchar;
        for(int i=0; i<randomLegth;i++){
            tempchar=(char)(generator.nextInt(96)+32);
            randomStringBuilder.append(tempchar);
        }
        return randomStringBuilder.toString();
    }



    private void sendToStart() {
        try {
            Intent startintent = new Intent(DetallesUsuario.this, Login.class);
            startActivity(startintent);
            finish();
        }catch (Exception e){
            Toast.makeText(DetallesUsuario.this,"Error al abrir la app", Toast.LENGTH_SHORT).show();
        }
    }

}




