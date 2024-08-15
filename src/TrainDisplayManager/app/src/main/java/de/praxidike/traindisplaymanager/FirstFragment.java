package de.praxidike.traindisplaymanager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.praxidike.traindisplaymanager.databinding.FragmentFirstBinding;
import de.praxidike.traindisplaymanager.models.Station;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Station> stationList = loadStationsFromAssets();
        Collections.sort(stationList);
        String[] stationNames = new String[stationList.size()];

        for (int i = 0; i < stationList.size(); i++){
            stationNames[i] = stationList.get(i).getName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_dropdown_item_1line, stationNames);

        binding.autoComplete.setAdapter(adapter);
        binding.autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                View focudView = getActivity().getCurrentFocus();
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(focudView.getWindowToken(), 0);
                binding.autoComplete.clearFocus();
            }
        });

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    public List<Station> loadStationsFromAssets() {
        String json_string;
        List<Station> stationList = new ArrayList<>();

        try {
            InputStream is = getActivity().getAssets().open("name_eva_mapping.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json_string = new String(buffer, "UTF-8");

            JSONObject jsonObject = new JSONObject(json_string);
            Iterator<String> keys = jsonObject.keys();

            while(keys.hasNext()) {
                String name = keys.next();
                stationList.add(new Station(name, jsonObject.getInt(name)));
            }

        } catch (IOException | JSONException ex) {
            ex.printStackTrace();
            return null;
        }

        return stationList;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}