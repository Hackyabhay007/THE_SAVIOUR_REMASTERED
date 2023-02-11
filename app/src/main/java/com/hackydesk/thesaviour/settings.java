package com.hackydesk.thesaviour;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import components.functiontools;


public class settings extends Fragment {
    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor prefeditor;

    Switch skipsplash;
    Switch rapidsos;
    Button Repairbtn;
    View rootView;
    CardView colorstatecard;
    boolean accessibilityServiceEnabled;

    public settings() {
        // Required empty public constructor
    }


    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         rootView = inflater.inflate(R.layout.fragment_settings, container,false);
        Button addprotector = (Button) rootView.findViewById(R.id.addbodygaurd);
        Button logout = (Button) rootView.findViewById(R.id.logout_btn);
        Button parent_mode = (Button) rootView.findViewById(R.id.parent_mode);
        Button profile = (Button) rootView.findViewById(R.id.profile_user);
        skipsplash = (Switch) rootView.findViewById(R.id.SkipSplash);
        colorstatecard = (CardView) rootView.findViewById(R.id.addresscontainer);
        rapidsos = (Switch) rootView.findViewById(R.id.RapidSos);
        Repairbtn = (Button) rootView.findViewById(R.id.repiarBtn);

        //shared pref
        sharedPreferences = getContext().getSharedPreferences("Thesaviour", Context.MODE_PRIVATE);
        prefeditor = sharedPreferences.edit();
        accessibilityServiceEnabled = isAccessibilityServiceEnabled(getContext(), DangerModeBackgroundListener.class);


        //load last saved state of Switches
        LoadLastState();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "LOGOUT SUCCESSFUL", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(),firsttime.class));
                SharedPreferences preferences = getActivity().getSharedPreferences("Thesaviour", 0);
                SharedPreferences.Editor editor = preferences.edit();
                Toast.makeText(getContext(), "USER DATA CLEARED", Toast.LENGTH_SHORT).show();
                editor.clear();
                editor.apply();
                FirebaseAuth.getInstance().signOut();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UserProfile.class);
                startActivity(intent);
            }
        });

        parent_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), userguide.class);
                startActivity(intent);
            }
        });


        skipsplash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (skipsplash.isChecked())
                {
                    prefeditor.putBoolean("SKIP_SPLASH",true);

                }
                else{
                    prefeditor.putBoolean("SKIP_SPLASH",false);
                }
                prefeditor.apply();
            }
        });

        rapidsos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (rapidsos.isChecked())
                {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);

                    Toast.makeText(getContext(), "Find The Saviour App And Activate Accessibility Permission", Toast.LENGTH_SHORT).show();

                        prefeditor.putBoolean("RAPID_SOS",true);
                        rapidsos.setChecked(true);
                }
                else{
//                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//                    startActivity(intent);
                    prefeditor.putBoolean("RAPID_SOS",false);
                }
                prefeditor.apply();
            }
        });

        Repairbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              functiontools extra = new functiontools(getActivity(),getContext());
              extra.repairDevice();

            }
        });


        return rootView;
    }

    void LoadLastState()
    {
        skipsplash.setChecked(sharedPreferences.getBoolean("SKIP_SPLASH",false));
       // rapidsos.setChecked(sharedPreferences.getBoolean("RAPID_SOS",false));

        if (accessibilityServiceEnabled)
        {
            rapidsos.setChecked(true);
        }
        else {
            rapidsos.setChecked(false);
        }
    }

    public static boolean isAccessibilityServiceEnabled(Context context, Class<?> accessibilityService) {
        ComponentName expectedComponentName = new ComponentName(context, accessibilityService);
        String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(),  Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServicesSetting == null)
            return false;
        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        colonSplitter.setString(enabledServicesSetting);
        while (colonSplitter.hasNext()) {
            String componentNameString = colonSplitter.next();
            ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);
            if (enabledService != null && enabledService.equals(expectedComponentName))
                return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadLastState();
    }

}