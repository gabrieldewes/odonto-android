package com.dewes.odonto.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dewes.odonto.R;
import com.dewes.odonto.activities.LoginActivity;
import com.dewes.odonto.domain.Principal;
import com.dewes.odonto.services.AuthService;
import com.dewes.odonto.util.ImageHelper;

import java.util.Random;

import retrofit2.Call;

/**
 * Created by Dewes on 18/06/2017.
 */

public class ProfileFragment extends Fragment {

    private FrameLayout fragmentContainer;

    private AuthService authService;

    private ImageView ivUserProfilePhoto;
    private ImageView ivHeaderCover;

    private TextView tvUserProfileName;
    private TextView tvUserProfileBio;
    private TextView tvUserProfileEmail;
    private TextView tvUserProfileUsername;
    private TextView tvUserProfileRoles;

    private Call currentCall;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();

        if (view != null) {

            authService = AuthService.getInstance(view.getContext(), true);
            Principal principal = authService.getPrincipal();

            ivUserProfilePhoto    = (ImageView) view.findViewById(R.id.user_profile_photo);
            ivHeaderCover         = (ImageView) view.findViewById(R.id.header_cover_image);
            tvUserProfileName     = (TextView)  view.findViewById(R.id.user_profile_name);
            tvUserProfileBio      = (TextView)  view.findViewById(R.id.user_profile_short_bio);
            tvUserProfileEmail    = (TextView)  view.findViewById(R.id.user_profile_email);
            tvUserProfileUsername = (TextView)  view.findViewById(R.id.user_profile_username);
            tvUserProfileRoles    = (TextView)  view.findViewById(R.id.user_profile_roles);

            String fullName = (principal.getFirstName() +" "+ principal.getLastName()).trim();

            tvUserProfileName.setText(fullName);
            tvUserProfileBio.setText(principal.getFirstName() +" dont provides a bio");
            tvUserProfileEmail.setText(principal.getEmail());
            tvUserProfileUsername.setText(principal.getUsername());
            tvUserProfileRoles.setText(principal.getRoles().get(0).replaceAll("ROLE_", ""));

            Random random = new Random();
            String[] p = new String[]{"men", "women"};
            String userProfilePhotoUrl = "https://randomuser.me/api/portraits/"+ p[random.nextInt(2)] +"/"+ random.nextInt(100) +".jpg";

            new ImageHelper(ivUserProfilePhoto).execute(userProfilePhotoUrl);
            new ImageHelper(ivHeaderCover, true).execute(userProfilePhotoUrl);

            Button btLogout = (Button) view.findViewById(R.id.btLogout);

            btLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    authService.logout();
                    getActivity().finish();
                    getActivity().startActivity(
                            new Intent(getActivity(), LoginActivity.class)
                                    .putExtra("snackbar", "You logged out successfully."));

                }
            });

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        fragmentContainer = (FrameLayout) view.findViewById(R.id.fragmentProfile);
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (currentCall != null)
            currentCall.cancel();
    }

}