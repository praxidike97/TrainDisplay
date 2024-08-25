package de.praxidike.traindisplaymanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import de.praxidike.traindisplaymanager.databinding.FragmentFirstBinding;
import de.praxidike.traindisplaymanager.models.Station;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    public ArrayList<Station> stationList;
    public int selectedStationEvaNr = -1;

    public static final String LOGTAG = "FirstFragment";

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

        stationList = loadStationsFromAssets();
        Collections.sort(stationList);
        int positionSavedStation = 0;

        SharedPreferences sharedPref1 = getActivity().getSharedPreferences(getString(R.string.shared_pref_file), Context.MODE_PRIVATE);
        int savedEvaNr = sharedPref1.getInt(getString(R.string.shared_pref_eva_nr), 0);
        selectedStationEvaNr = savedEvaNr;

        for(int i = 0; i < stationList.size(); i++){
            if(stationList.get(i).getEvaNr() == savedEvaNr){
                positionSavedStation = i;
                break;
            }
        }

        ArrayAdapter<Station> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, stationList);
        binding.autoComplete.setAdapter(adapter);
        binding.autoComplete.setText(binding.autoComplete.getAdapter().getItem(positionSavedStation).toString(), false);
        binding.autoComplete.setOnItemClickListener((parent, view12, position, id) -> {
            Object item = parent.getItemAtPosition(position);
            if (item instanceof Station){
                Station selecteStation = (Station) item;
                selectedStationEvaNr = selecteStation.getEvaNr();

                SharedPreferences sharedPreferences1 = getActivity().getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_file), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences1.edit();
                editor.putInt(getString(R.string.shared_pref_eva_nr), selectedStationEvaNr);
                editor.apply();

                View focudView = getActivity().getCurrentFocus();
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(focudView.getWindowToken(), 0);
                binding.autoComplete.clearFocus();
            }
        });

        binding.buttonConfirm.setOnClickListener(view1 -> {

            if (selectedStationEvaNr < 0){
                Toast.makeText(getActivity(), "Station mit EVA Nr " + selectedStationEvaNr + " nicht gefunden.", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getActivity(), "Aktualisiere Anzeige...", Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPref2 = getActivity().getSharedPreferences(getString(R.string.shared_pref_file), Context.MODE_PRIVATE);
                String ip = sharedPref2.getString(getString(R.string.shared_pref_ip), null);

                if(ip == null){
                    Log.e(LOGTAG, "IP address is null!");
                }else {
                    // Instantiate the RequestQueue.
                    RequestQueue queue = Volley.newRequestQueue(getContext());
                    String url = "http://" + ip + ":8000/generate_timetable/" + selectedStationEvaNr;
                    Log.d(LOGTAG, url);
                    // Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            response -> Log.d("TAG", response), error -> Log.d("TAG", error.toString()));

                    queue.add(stringRequest);
                }
            }
        });

        binding.checkBoxRotate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.shared_pref_file), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(getString(R.string.shared_pref_rotate), isChecked);
            editor.apply();
        });

        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.shared_pref_file), Context.MODE_PRIVATE);
        boolean rotate = sharedPref.getBoolean(getString(R.string.shared_pref_rotate), false);
        binding.checkBoxRotate.setChecked(rotate);
    }

    public ArrayList<Station> loadStationsFromAssets() {
        String json_string;
        ArrayList<Station> stationList = new ArrayList<>();

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