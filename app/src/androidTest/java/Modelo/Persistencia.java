package Modelo;

import com.example.app08.Alumno;

public interface Persistencia {

    public void openDataBase();
    public void closeDataBase();
    public long insertAlumno(Alumno alumno);
    public long updateAlumno(Alumno alumno);
    public void deleteAlumnos(int id);

}
