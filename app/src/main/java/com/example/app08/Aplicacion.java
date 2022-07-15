package com.example.app08;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;

public class Aplicacion extends Application {
    public static ArrayList<Alumno> alumnos;
    private MiAdaptador adaptador;
    private AlumnoDbHelper dbHelper = new AlumnoDbHelper(this);
    private AlumnosDb db = new AlumnosDb(this, dbHelper);

    public static ArrayList<Alumno> getAlumnos() {
        return alumnos;
    }

    public MiAdaptador getAdaptador() {
        return adaptador;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        alumnosDb = new AlumnosDb(getApplicationContext());
        alumnos = alumnosDb.allAlumnos();
        //alumnos = Alumno.llenarAlumnos();
        adaptador = new MiAdaptador(alumnos, this);
        //Log.d("", "onCreate: tamaño array list " + alumnos.size());
    }
}