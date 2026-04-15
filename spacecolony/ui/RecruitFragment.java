package com.example.spacecolony.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spacecolony.R;
import com.example.spacecolony.model.CrewMember;
import com.example.spacecolony.model.Engineer;
import com.example.spacecolony.model.Medic;
import com.example.spacecolony.model.Pilot;
import com.example.spacecolony.model.Scientist;
import com.example.spacecolony.model.Soldier;
import com.example.spacecolony.model.Storage;

public class RecruitFragment extends Fragment {

    private EditText nameInput;
    private Spinner specSpinner;
    private TextView statsPreview;
    private Button createButton;
    private Button cancelButton;

    private OnRecruitListener listener;

    private static final String[] SPECIALIZATIONS = {
            "Pilot", "Engineer", "Medic", "Scientist", "Soldier"
    };

    private static final String[] STATS_DESCRIPTIONS = {
            "Skill: 5, Resilience: 4, Energy: 20 (Balanced)",
            "Skill: 6, Resilience: 3, Energy: 19 (Repair Bonus)",
            "Skill: 7, Resilience: 2, Energy: 18 (Medical Bonus)",
            "Skill: 8, Resilience: 1, Energy: 17 (Research Bonus)",
            "Skill: 9, Resilience: 0, Energy: 16 (Combat Bonus)"
    };

    public interface OnRecruitListener {
        void onCrewCreated();
        void onCancel();
    }

    public void setOnRecruitListener(OnRecruitListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recruit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameInput = view.findViewById(R.id.input_name);
        specSpinner = view.findViewById(R.id.spinner_specialization);
        statsPreview = view.findViewById(R.id.stats_preview);
        createButton = view.findViewById(R.id.btn_create);
        cancelButton = view.findViewById(R.id.btn_cancel);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, SPECIALIZATIONS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        specSpinner.setAdapter(adapter);

        specSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                statsPreview.setText(STATS_DESCRIPTIONS[position]);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        createButton.setOnClickListener(v -> createCrewMember());
        cancelButton.setOnClickListener(v -> {
            if (listener != null) listener.onCancel();
        });
    }

    private Storage getStorage() {
        return Storage.getInstance();
    }

    private void createCrewMember() {
        String name = nameInput.getText().toString().trim();
        if (name.isEmpty()) {
            nameInput.setError("Please enter a name");
            return;
        }

        String specialization = specSpinner.getSelectedItem().toString();

        CrewMember newCrew = null;
        switch (specialization) {
            case "Pilot":
                newCrew = new Pilot(name);
                break;
            case "Engineer":
                newCrew = new Engineer(name);
                break;
            case "Medic":
                newCrew = new Medic(name);
                break;
            case "Scientist":
                newCrew = new Scientist(name);
                break;
            case "Soldier":
                newCrew = new Soldier(name);
                break;
        }

        if (newCrew != null) {
            // 确保位置设置为Quarters（虽然addCrewMember也会设置，但双重保险）
            newCrew.setCurrentLocation("Quarters");
            newCrew.setInMedbay(false);

            getStorage().addCrewMember(newCrew);

            // 显示成功消息
            Toast.makeText(requireContext(),
                    "Recruited " + newCrew.getName() + " (" + newCrew.getSpecialization() + ")",
                    Toast.LENGTH_SHORT).show();

            if (listener != null) {
                listener.onCrewCreated();
            }
        }
    }
}
