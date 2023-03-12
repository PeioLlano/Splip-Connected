package com.example.primerproyecto.BBDD;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.primerproyecto.Clases.Persona;

public class BBDD extends SQLiteOpenHelper {

    public BBDD(@Nullable Context context, @Nullable String name,
                @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("PRAGMA foreign_keys=ON");

        sqLiteDatabase.execSQL("CREATE TABLE Usuarios (Usuario VARCHAR(255), Contraseña VARCHAR(255), PRIMARY KEY (Usuario))");
        sqLiteDatabase.execSQL("CREATE TABLE Grupos (Usuario VARCHAR(255) ,Titulo VARCHAR(255), Divisa VARCHAR(255), CONSTRAINT FK_G_1 FOREIGN KEY (Usuario) REFERENCES Usuarios(Usuario) ON DELETE CASCADE, PRIMARY KEY (Usuario, Titulo))");
        sqLiteDatabase.execSQL("CREATE TABLE Personas (Usuario VARCHAR(255), Grupo VARCHAR(255), Nombre VARCHAR(255), CONSTRAINT FK_P_1 FOREIGN KEY (Usuario,Grupo) REFERENCES Grupos(Usuario,Titulo) ON DELETE CASCADE, PRIMARY KEY (Usuario,Grupo, Nombre))");
        sqLiteDatabase.execSQL("CREATE TABLE Gastos (Codigo INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,Usuario VARCHAR(255), Grupo VARCHAR(255), Persona VARCHAR(255), Titulo VARCHAR(255),  Cantidad FLOAT, Fecha DATE, CONSTRAINT FK_Ga_1 FOREIGN KEY (Usuario,Grupo, Persona) REFERENCES Personas(Usuario,Grupo, Nombre) ON DELETE CASCADE ON UPDATE CASCADE)");
        sqLiteDatabase.execSQL("CREATE TABLE Pagos (Codigo INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,Usuario VARCHAR(255), Grupo VARCHAR(255), PersonaAutora VARCHAR(255), PersonaDestinataria VARCHAR(255), Cantidad FLOAT, Fecha DATE, CONSTRAINT FK_Pa_1 FOREIGN KEY (Usuario,Grupo, PersonaAutora) REFERENCES Personas(Usuario,Grupo, Nombre) ON DELETE CASCADE ON UPDATE CASCADE, CONSTRAINT FK_Pa_1 FOREIGN KEY (Usuario,Grupo, PersonaDestinataria) REFERENCES Personas(Usuario,Grupo, Nombre) ON DELETE CASCADE ON UPDATE CASCADE)");

        sqLiteDatabase.execSQL("INSERT INTO Usuarios ('Usuario', 'Contraseña') VALUES ('admin','admin')");

        sqLiteDatabase.execSQL("INSERT INTO Grupos ('Usuario', 'Titulo', 'Divisa') VALUES ('admin','Viaje a Malaga', '€')");
        sqLiteDatabase.execSQL("INSERT INTO Grupos ('Usuario', 'Titulo', 'Divisa') VALUES ('admin','Cumple de Andoni', '€')");

        sqLiteDatabase.execSQL("INSERT INTO Personas ('Usuario', 'Grupo', 'Nombre') VALUES ('admin','Viaje a Malaga', 'Peio')");
        sqLiteDatabase.execSQL("INSERT INTO Personas ('Usuario', 'Grupo', 'Nombre') VALUES ('admin','Viaje a Malaga', 'Ander')");
        sqLiteDatabase.execSQL("INSERT INTO Personas ('Usuario', 'Grupo', 'Nombre') VALUES ('admin','Viaje a Malaga', 'Patxi')");
        sqLiteDatabase.execSQL("INSERT INTO Personas ('Usuario', 'Grupo', 'Nombre') VALUES ('admin','Viaje a Malaga', 'Madalen')");

        sqLiteDatabase.execSQL("INSERT INTO Gastos ('Usuario', 'Grupo', 'Persona','Titulo', 'Cantidad') VALUES ('admin','Viaje a Malaga', 'Madalen', 'Cena pizzeria', '56.8')");
        sqLiteDatabase.execSQL("INSERT INTO Gastos ('Usuario', 'Grupo', 'Persona','Titulo', 'Cantidad') VALUES ('admin','Viaje a Malaga', 'Ander', 'Gasolina', '80.6')");

        sqLiteDatabase.execSQL("INSERT INTO Pagos ('Usuario', 'Grupo', 'PersonaAutora','PersonaDestinataria', 'Cantidad') VALUES ('admin','Viaje a Malaga', 'Peio', 'Ander', '20.15')");
        sqLiteDatabase.execSQL("INSERT INTO Pagos ('Usuario', 'Grupo', 'PersonaAutora','PersonaDestinataria', 'Cantidad') VALUES ('admin','Viaje a Malaga', 'Patxi', 'Madalen', '10')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void onOpen(SQLiteDatabase db){
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
    }

}
