package com.example.joel.myapplication;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class InformationFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.more_info_fragment, container, false);
        TextView groupInfoTV = (TextView) view.findViewById(R.id.infoText);
        groupInfoTV.setMovementMethod(new ScrollingMovementMethod());
        return view;
    }
}
