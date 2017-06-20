package com.dewes.odonto.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.dewes.odonto.R;
import com.dewes.odonto.domain.Principal;
import com.dewes.odonto.services.AuthService;

import retrofit2.Call;

/**
 * Created by Dewes on 18/06/2017.
 */

public class HomeFragment extends Fragment {

    private FrameLayout fragmentContainer;

    private AuthService authService;

    private Call currentCall;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();

        if (view != null) {

            authService = AuthService.getInstance(view.getContext(), true);
            Principal principal = authService.getPrincipal();

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        fragmentContainer = (FrameLayout) view.findViewById(R.id.fragmentHome);
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (currentCall != null)
            currentCall.cancel();
    }

}