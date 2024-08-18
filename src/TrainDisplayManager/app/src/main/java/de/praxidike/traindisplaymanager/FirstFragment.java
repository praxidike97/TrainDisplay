package de.praxidike.traindisplaymanager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
        /*String[] stationNames = new String[stationList.size()];

        for (int i = 0; i < stationList.size(); i++){
            stationNames[i] = stationList.get(i).getName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_dropdown_item_1line, stationNames);*/


        ArrayAdapter<Station> adapter = new ArrayAdapter<Station>(getContext(), android.R.layout.simple_dropdown_item_1line, stationList);
        binding.autoComplete.setAdapter(adapter);
        binding.autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                if (item instanceof Station){
                    Station selecteStation = (Station) item;
                    selectedStationEvaNr = selecteStation.getEvaNr();

                    View focudView = getActivity().getCurrentFocus();
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(focudView.getWindowToken(), 0);
                    binding.autoComplete.clearFocus();
                }
            }
        });

        /*binding.autoComplete.setAdapter(adapter);
        binding.autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                View focudView = getActivity().getCurrentFocus();
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(focudView.getWindowToken(), 0);
                binding.autoComplete.clearFocus();
            }
        });*/

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (selectedStationEvaNr < 0){
                    Toast.makeText(getActivity(), "Station mit EVA Nr " + selectedStationEvaNr + " nicht gefunden.", Toast.LENGTH_LONG).show();
                }else {

                    // Instantiate the RequestQueue.
                    RequestQueue queue = Volley.newRequestQueue(getContext());
                    String url = "http://192.168.0.59:8000/generate_timetable/" + selectedStationEvaNr;
                    Log.d("TAG", url);
                    // Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d("TAG", response);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("TAG", error.toString());
                        }
                    });

                    queue.add(stringRequest);
                }
            }
        });
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