package com.rd.dmmr.tutosearch; /**
 * Created by Owner on 13/4/2018.
 */

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class AdapterImage extends PagerAdapter {

    private ArrayList<Integer> images;
    private LayoutInflater inflater;
    private Context context;
    private DatabaseReference DBReference;
    private FirebaseUser mCurrentUser;

    private FirebaseFirestore fdb;

    public AdapterImage(Context context, ArrayList<Integer> images) {
        this.context = context;
        this.images = images;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object instantiateItem(final ViewGroup view, final int position) {
        //llenando la pantalla con los datos del doctor
        final View myImageLayout = inflater.inflate(R.layout.slinder, view, false);

        final ImageView myImage = (ImageView) myImageLayout
           .findViewById(R.id.image);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        //identificando el id del que tiene la sesion iniciada
        final String current_uid = mCurrentUser.getUid();
        //especificando donde se buscara

        fdb = FirebaseFirestore.getInstance();

        DocumentReference docRef = fdb.collection("Estudiantes").document(current_uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot dc = task.getResult();
                final String imagen = dc.getString("url_pic");
                Log.i("Probando", imagen);
                if(!imagen.equals("default")){
                    Picasso.with(context).load(imagen).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.defaultimage)
                            .into(myImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    // Picasso.with(context).load(imagen).placeholder(R.drawable.logo3).into(myImage);
                                    Toast.makeText(context, "Se produjo un error al intentar cargar la foto de perfil.", Toast.LENGTH_SHORT).show();;
                                }
                            });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Se produjo un error al intentar cargar la foto de perfil.", Toast.LENGTH_SHORT).show();;
            }
        });
/*
        DBReference = FirebaseDatabase.getInstance().getReference().child("usuarios").child("estudiantes").child(current_uid);
        DBReference.keepSynced(true);

        DBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String imagen = dataSnapshot.child("image").getValue().toString();

                if(!imagen.equals("default")){
                    Picasso.with(context).load(imagen).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.defaultimage)
                            .into(myImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                   // Picasso.with(context).load(imagen).placeholder(R.drawable.logo3).into(myImage);
                                }
                            });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
*/
        myImage.setImageResource(images.get(position));
        view.addView(myImageLayout, 0);
         return 0;
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}