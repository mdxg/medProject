package de.rwth.pulsuhr.pulsuhr;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Timer;

/**
 * Created by steffen on 16.06.16.
 */
public class HomeFragment extends Fragment implements View.OnClickListener{
    View myView;
    PulsUhr pulsuhr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.home_fragment, container, false);
        Button b = (Button) myView.findViewById(R.id.btnConnect);
        b.setOnClickListener(this);
        b = (Button) myView.findViewById(R.id.btnContinue);
        b.setOnClickListener(this);
        b = (Button) myView.findViewById(R.id.btnStart);
        b.setOnClickListener(this);
        return myView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //pulsuhr = new PulsUhr(getActivity(), "98:D3:31:90:41:88");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnConnect:
                pulsuhr.connect();
                Log.i("Connection","Trying to establish connection");
                break;
            case R.id.btnContinue:
                pulsuhr.continueBeacon();
                Log.i("Send", "continueBeacon");
                break;
            case R.id.btnStart:
                pulsuhr.startMeasurement();
                //pulsuhr.startBeacon();
                //Log.i("Send", "startBeacon");
                break;
        }
    }
}
