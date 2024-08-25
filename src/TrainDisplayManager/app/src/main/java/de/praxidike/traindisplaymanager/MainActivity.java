package de.praxidike.traindisplaymanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import de.praxidike.traindisplaymanager.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getApplicationContext().setTheme(R.style.Theme_TrainDisplayManager);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_ip) {
            showIPAlertDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void showIPAlertDialog() {
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        getApplicationContext().setTheme(R.style.Theme_TrainDisplayManager);
        View promptsView = li.inflate(R.layout.alert_dialog_ip, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.userInput);

        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.shared_pref_file), Context.MODE_PRIVATE);
        String ipAddress = sharedPreferences.getString(getString(R.string.shared_pref_ip), null);

        if(ipAddress != null){
            userInput.setText(ipAddress);
        }

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> {
                    SharedPreferences sharedPreferences1 = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_file), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences1.edit();
                    editor.putString(getString(R.string.shared_pref_ip), userInput.getText().toString());
                    editor.apply();
                })
                .setNegativeButton("Abbrechen",
                        (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }
}