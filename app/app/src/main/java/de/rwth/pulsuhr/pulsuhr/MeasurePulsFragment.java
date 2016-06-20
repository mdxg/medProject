package de.rwth.pulsuhr.pulsuhr;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by steffen on 16.06.16.
 */
public class MeasurePulsFragment extends Fragment implements View.OnClickListener{
    View myView;
    private LineGraphSeries<DataPoint> dataPoints;
    private int LastXValue = 0;
    private PulsUhr pulsUhr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.measure_pulse_fragment, container, false);
        GraphView graph = (GraphView) myView.findViewById(R.id.graph);
        dataPoints = new LineGraphSeries<DataPoint>();
        graph.addSeries(dataPoints);
        Button b = (Button) myView.findViewById(R.id.btnStartMeasurement);
        b.setOnClickListener(this);
      /*  LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        graph.addSeries(series); */
        return myView;
    }



    public void addDataPoint(int receivedPoint)
    {
        LastXValue++;
        dataPoints.appendData(new DataPoint(LastXValue, receivedPoint), true, 480);
    }

    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnStartMeasurement:
                PulsUhr pulsUhr = new PulsUhr(getActivity(), "98:D3:31:90:41:88");
                pulsUhr.connect();
                pulsUhr.startMeasurement();
                break;
        }
    }
}
