package de.rwth.pulsuhr.pulsuhr;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by steffen on 16.06.16.
 */
public class SettingsFragment extends Fragment{
    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.settings_fragment, container, false);
        return myView;
    }
}
