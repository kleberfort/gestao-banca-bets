package com.example.gestaobanca;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.gestaobanca.fragments.CadastrarFragment;
import com.example.gestaobanca.fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements CadastrarFragment.OnCategoryItemsMapListener {


    private HashMap<String, ArrayList<String>> minhaListaMap;  // HashMap to store items for each category

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Definir o BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Carregar o fragmento inicial (HomeFragment) quando a Activity for criada
        //antes era replace
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new CadastrarFragment())
                    .commit();
        }


        // Configurar o listener para troca de fragmentos
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                int id = item.getItemId();

                if (id == R.id.navigation_home) {
                    selectedFragment = new HomeFragment();

                    // Passar o HashMap (minhaListaMap) para o HomeFragment
                    if (minhaListaMap != null) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("categoryItemsMap", minhaListaMap);
                        selectedFragment.setArguments(bundle);
                    }


                } else if (id == R.id.navigation_edit_product) {
                    selectedFragment = new CadastrarFragment();
                }

                // Troca o fragmento
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, selectedFragment)
                        .commit();

                return true;
            }
        });

    }


    @Override
    public void onEnviarCategoryItemsMap(HashMap<String, ArrayList<String>> categoryItemsMap) {

        minhaListaMap = categoryItemsMap;

    }
}