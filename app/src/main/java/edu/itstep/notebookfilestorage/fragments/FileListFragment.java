package edu.itstep.notebookfilestorage.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import edu.itstep.notebookfilestorage.R;
import edu.itstep.notebookfilestorage.activities.MainActivity;

public class FileListFragment extends Fragment {
    String[] files;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            files = getArguments().getStringArray(MainActivity.FILE_LIST_TAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_list, container, false);

        ArrayAdapter<String> adapterFiles = new ArrayAdapter<String>(
                this.getContext(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                files
        );
        ListView lvFiles = view.findViewById(R.id.lvFiles);
        lvFiles.setAdapter(adapterFiles);

        lvFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Bundle args = new Bundle();
                args.putString("fileName", files[position]);
                getParentFragmentManager().setFragmentResult("inputFileNameRequest", args);
            }
        });

        return view;
    }
}