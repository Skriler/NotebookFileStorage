package edu.itstep.notebookfilestorage.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import edu.itstep.notebookfilestorage.R;
import edu.itstep.notebookfilestorage.entities.Note;
import edu.itstep.notebookfilestorage.fragments.FileAddFragment;
import edu.itstep.notebookfilestorage.fragments.FileListFragment;
import edu.itstep.notebookfilestorage.fragments.NoteAddFragment;
import edu.itstep.notebookfilestorage.fragments.NoteDetailsFragment;
import edu.itstep.notebookfilestorage.fragments.NoteListFragment;
import edu.itstep.notebookfilestorage.services.FileService;
import edu.itstep.notebookfilestorage.services.NotebookService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String THEME_LIST_TAG = "themes";
    public static final String FILE_LIST_TAG = "files";
    public static final String FILE_TAG = "file";
    public static final String NOTE_NAME_LIST_TAG = "notes";
    public static final String NOTE_TAG = "note";

    private static final int CONTEXT_FILE_LIST = 666;
    private static final int CONTEXT_CREATE_FILE = 222;

    String[] files;
    ArrayList<Note> notes;
    ArrayList<String> themes;

    TextView tvSelectedFile;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSelectedFile = findViewById(R.id.tvSelectedFile);
        registerForContextMenu(tvSelectedFile);

        files = this.fileList();
        themes = NotebookService.getThemes();
        notes = FileService.readNoteList(tvSelectedFile.getText().toString(), this);

        if (notes == null) {
            notes = NotebookService.getNoteList();
        }

        setFragmentResultListeners();
        showNoteListFragment();
    }

    @Override
    protected void onPause() {
        FileService.saveNoteList(notes, tvSelectedFile.getText().toString(), this);
        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setFragmentResultListeners() {
        getSupportFragmentManager().setFragmentResultListener(
                "inputNodeRequest",
                this,
                new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        String title = result.getString("title");
                        String theme = result.getString("theme");
                        String description = result.getString("description");
                        Note note = new Note(title, theme, description);
                        notes.add(note);
                        showNoteListFragment();
                    }
                }
        );

        getSupportFragmentManager().setFragmentResultListener(
                "inputNewFileRequest",
                this,
                new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        FileService.saveNoteList(notes, tvSelectedFile.getText().toString(), MainActivity.this);
                        notes = new ArrayList<>();
                        String fileName = result.getString("fileName");
                        tvSelectedFile.setText(fileName);
                        showNoteAddFragment();
                    }
                }
        );

        getSupportFragmentManager().setFragmentResultListener(
                "inputNoteNameRequest",
                this,
                new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        String noteName = result.getString("noteName");
                        showNoteDetailsFragment(noteName);
                    }
                }
        );

        getSupportFragmentManager().setFragmentResultListener(
                "inputFileNameRequest",
                this,
                new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        FileService.saveNoteList(notes, tvSelectedFile.getText().toString(), MainActivity.this);
                        String fileName = result.getString("fileName");
                        tvSelectedFile.setText(fileName);
                        notes = FileService.readNoteList(fileName, MainActivity.this);
                        showNoteListFragment();
                    }
                }
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showNoteListFragment() {
        Bundle args = new Bundle();
        args.putSerializable(NOTE_NAME_LIST_TAG, NotebookService.getNoteNameList(notes));
        showCustomFragment(new NoteListFragment(), args);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showNoteDetailsFragment(String noteName) {
        Bundle args = new Bundle();
        args.putSerializable(NOTE_TAG, NotebookService.getNoteByName(notes, noteName));
        showCustomFragment(new NoteDetailsFragment(), args);
    }

    private void showNoteAddFragment() {
        Bundle args = new Bundle();
        args.putSerializable(THEME_LIST_TAG, themes);
        showCustomFragment(new NoteAddFragment(), args);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.showNoteList:
                showNoteListFragment();
                break;
            case R.id.addNote:
                showNoteAddFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.tvSelectedFile) {
            menu.add(0, CONTEXT_FILE_LIST, 0, "File list");
            menu.add(0, CONTEXT_CREATE_FILE, 0, "Create file");
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Bundle args = new Bundle();

        switch (item.getItemId()) {
            case CONTEXT_FILE_LIST:
                args.putSerializable(FILE_LIST_TAG, files);
                showCustomFragment(new FileListFragment(), args);
                break;
            case CONTEXT_CREATE_FILE:
                showCustomFragment(new FileAddFragment(), args);
                break;
        }

        return super.onContextItemSelected(item);
    }

    private void showCustomFragment(Fragment fragment, Bundle args) {
        fragment.setArguments(args);
        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frgContainerView, fragment)
                .commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnClose) {
            showNoteListFragment();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(FILE_TAG, tvSelectedFile.getText().toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String file = (String) savedInstanceState.getSerializable(FILE_TAG);
        tvSelectedFile.setText(file);
        notes = FileService.readNoteList(file, this);
        showNoteListFragment();
    }
}
