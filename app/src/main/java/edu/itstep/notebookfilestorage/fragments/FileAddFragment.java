package edu.itstep.notebookfilestorage.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import edu.itstep.notebookfilestorage.R;
import edu.itstep.notebookfilestorage.activities.MainActivity;

public class FileAddFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_add, container, false);

        EditText etFileName = view.findViewById(R.id.etFileName);
        Button btnAdd = view.findViewById(R.id.btnAdd);
        Button btnClose = view.findViewById(R.id.btnClose);

        btnClose.setOnClickListener((MainActivity)this.getActivity());
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("fileName", etFileName.getText().toString());
                getParentFragmentManager().setFragmentResult("inputNewFileRequest", args);
            }
        });

        return view;
    }
}