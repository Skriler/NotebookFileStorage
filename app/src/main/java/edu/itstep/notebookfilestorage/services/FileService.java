package edu.itstep.notebookfilestorage.services;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import edu.itstep.notebookfilestorage.entities.Note;

public class FileService {
    public static void saveNoteList(ArrayList<Note> notes, String fileName, Context context) {
        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fos);
            objectOutputStream.writeObject(notes);
        } catch (Exception e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(e.getMessage())
                    .setPositiveButton("OK", null)
                    .create()
                    .show();
        }
    }

    public static ArrayList<Note> readNoteList(String fileName, Context context) {
        ArrayList<Note> notes  = new ArrayList<>();
        try(FileInputStream fis = context.openFileInput(fileName)){
            ObjectInputStream objectInputStream = new ObjectInputStream(fis);
            notes = (ArrayList<Note>) objectInputStream.readObject();
        } catch (Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return notes;
    }
}
