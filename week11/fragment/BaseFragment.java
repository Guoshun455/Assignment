package com.example.week11.fragment;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import com.example.week11.R;
import java.util.List;
import java.util.function.Function;

public abstract class BaseFragment<T> extends Fragment {
    protected ListView listView;
    protected EditText searchInput;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        listView = v.findViewById(R.id.listView);
        searchInput = v.findViewById(R.id.searchInput);
        Button searchBtn = v.findViewById(R.id.searchBtn);
        Button sortBtn = v.findViewById(R.id.sortBtn);
        Spinner sortSpinner = v.findViewById(R.id.sortSpinner);


        initData();


        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, getSortOptions());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);


        searchBtn.setOnClickListener(x -> search());
        sortBtn.setOnClickListener(x -> sort(sortSpinner.getSelectedItemPosition()));
        listView.setOnItemClickListener((p, v1, pos, id) ->
                Toast.makeText(requireContext(), p.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show());

        showAll();
        return v;
    }

    protected abstract void initData();
    protected abstract String[] getSortOptions();
    protected abstract void showAll();
    protected abstract void search();
    protected abstract void sort(int type);

    protected void updateList(List<?> items) {
        listView.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, items));
    }
}