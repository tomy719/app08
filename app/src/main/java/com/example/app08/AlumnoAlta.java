package com.example.app08;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AlumnoAlta extends AppCompatActivity {

    private Button btnGuardar, btnRegresar;
    private Alumno alumno;
    private EditText txtNombre, txtMatricula, txtGrado;
    private ImageView imgAlumno;
    private TextView lblImagen, txtId;
    private String carrera = "Ing. Tec. Informacion";
    private Button  btnFoto, btnBorrar;
    private int posicion;
    private Uri imgURI;
    private AlumnoDbHelper dbHelper = new AlumnoDbHelper(this);
    private AlumnosDb db = new AlumnosDb(this, dbHelper);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alumno_alta);
        btnGuardar = (Button) findViewById(R.id.btnSalir);
        btnRegresar = (Button) findViewById(R.id.btnRegresar);
        txtMatricula = (EditText) findViewById(R.id.txtMatricula);
        txtNombre = (EditText) findViewById(R.id.txtNombre);
        txtGrado = (EditText) findViewById(R.id.txtGrado);
        imgAlumno = (ImageView) findViewById(R.id.imgAlumno);
        btnBorrar = findViewById(R.id.btnBorrar);
        btnImagen = (Button) findViewById(R.id.btnCargar);

        Bundle bundle = getIntent().getExtras();
        alumno = (Alumno) bundle.getSerializable("alumno");
        posicion = bundle.getInt("posicion", posicion);

        if(posicion >= 0){
            txtMatricula.setText(alumno.getMatricula());
            txtNombre.setText(alumno.getNombre());
            txtGrado.setText(alumno.getCarrera());
            imgAlumno.setImageURI(Uri.parse(alumno.getImgURI()));
        }

        btnImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alumno == null){
                    //Agregar un nuevo alumno

                    alumno = new Alumno();
                    alumno.setCarrera(txtGrado.getText().toString());
                    alumno.setMatricula(txtMatricula.getText().toString());
                    alumno.setNombre(txtNombre.getText().toString());

                    if(validar()){
                        alumnosDb.insertAlumno(alumno);
                        Aplicacion.getAlumnos().add(alumno);
                        db.openDataBase();
                        db.insertAlumno(alumno);
                        db.closeDataBase();
                        setResult(Activity.RESULT_OK);
                        finish();
                    }else {
                        Toast.makeText(AlumnoAlta.this, "Falta capturar datos", Toast.LENGTH_SHORT).show();
                        txtMatricula.requestFocus();
                    }
                }

                if(posicion >= 0){
                    if(imgURI != null){
                        alumno.setImgURI(imgURI.toString());
                    }
                    if(txtMatricula.getText().toString().matches("")){
                        Toast.makeText(AlumnoAlta.this, "Ingrese la matricula", Toast.LENGTH_SHORT).show();
                        onStop();
                    }
                    else if(txtNombre.getText().toString().matches("")){
                        Toast.makeText(AlumnoAlta.this, "Ingrese el nombre", Toast.LENGTH_SHORT).show();
                        onStop();
                    }
                    else if(txtGrado.getText().toString().matches("")){
                        Toast.makeText(AlumnoAlta.this, "Ingrese el grado", Toast.LENGTH_SHORT).show();
                        onStop();
                    }
                    else{
                        alumno.setMatricula(txtMatricula.getText().toString());
                        alumno.setNombre(txtNombre.getText().toString());
                        alumno.setCarrera(txtGrado.getText().toString());

                        Aplicacion.getAlumnos().get(posicion).setMatricula(alumno.getMatricula());
                        Aplicacion.getAlumnos().get(posicion).setNombre(alumno.getNombre());
                        Aplicacion.getAlumnos().get(posicion).setCarrera(alumno.getCarrera());
                        Aplicacion.getAlumnos().get(posicion).setImgURI(alumno.getImgURI());

                        db.openDataBase();
                        db.updateAlumno(alumno);
                        db.closeDataBase();
                        Toast.makeText(AlumnoAlta.this, "Se modificó con exito", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(posicion >= 0){
                    new AlertDialog.Builder(AlumnoAlta.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Borrar perfil")
                            .setMessage("¿Está seguro que desea borrar este perfil?")
                            .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Aplicacion.getAlumnos().remove(posicion);
                                    db.openDataBase();
                                    db.deleteAlumno(alumno.getId());
                                    db.closeDataBase();

                                    Toast.makeText(AlumnoAlta.this, "Se borró con exito", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                } else {
                    Toast.makeText(AlumnoAlta.this, "El perfil no esta registrado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    private void imageChooser() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.setType("image/*");

        startActivityForResult(Intent.createChooser(i, "Seleccione una imagen"), 200);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == 200) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    imgAlumno.setImageURI(selectedImageUri);
                    imgURI = selectedImageUri;

                    ContentResolver cr = getApplicationContext().getContentResolver();
                    cr.takePersistableUriPermission(imgURI, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                }
            }
        }
    }


    private boolean validar(){
        boolean exito = true;
        Log.d("nombre", "validar: " + txtNombre.getText());
        if(txtNombre.getText().toString().equals("")) exito = false;
        if(txtMatricula.getText().toString().equals("")) exito = false;
        if(txtGrado.getText().toString().equals("")) exito = false;

        return exito;
    }
}