package com.fstm.coredumped.smartwalkabilty.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.fstm.coredumped.android.R;
import com.fstm.coredumped.smartwalkabilty.android.model.bo.UserInfos;
import com.fstm.coredumped.smartwalkabilty.web.Model.bo.Categorie;
import com.fstm.coredumped.smartwalkabilty.web.Model.dao.DAOCategorie;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    public static void loadSet_Settings(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        List<Integer> listCategories = null;
        int method = sp.getInt("itiniraire",1);
        boolean useCategories = sp.getBoolean("useCategories",false);
        if (useCategories) {
            Set<String> categories = sp.getStringSet("categories",null);
            if (categories != null){
                listCategories = new ArrayList<Integer>();
                for (String category : categories){
                    listCategories.add(Integer.parseInt(category));
                }
            }
        }

        int radius = sp.getInt("radius",30);

        UserInfos.getInstance().setRadius(radius);
        if (useCategories){
            UserInfos.getInstance().setCats(listCategories);
        }


    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            Set<Categorie> categories = (Set<Categorie>) DAOCategorie.getDaoCategorie().Retrieve();
            List<Integer> categorisIds = new ArrayList<>();
            List<String> categorisStrings = new ArrayList<>();
            for (Categorie categorie : categories){
                categorisIds.add(categorie.getId());
                categorisStrings.add(categorie.getCategorie());
            }
            CharSequence[] catids = categorisIds.toArray(new CharSequence[categorisIds.size()]);
            CharSequence[] catstrings = categorisStrings.toArray(new CharSequence[categorisStrings.size()]);
            MultiSelectListPreference multiSelectListPreference = findPreference("categories");
            multiSelectListPreference.setEntries(catstrings);
            multiSelectListPreference.setEntryValues(catids);


        }
    }
}