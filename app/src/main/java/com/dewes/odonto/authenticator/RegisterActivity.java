package com.dewes.odonto.authenticator;

import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dewes.odonto.R;
import com.dewes.odonto.api.client.AccountResource;
import com.dewes.odonto.api.client.AuthResource;
import com.dewes.odonto.api.client.Callback;
import com.dewes.odonto.domain.User;
import static com.dewes.odonto.util.StringUtils.*;

import java.util.List;

import retrofit2.Call;

public class RegisterActivity extends AppCompatActivity {

    private RegisterTask registerTask = null;
    private Call currentCall;

    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etPasswordConfirm;

    private View progressView;
    private View registerView;
    private View focusView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPasswordConfirm = (EditText) findViewById(R.id.etPasswordConfirm);

        Button btRegister = (Button) findViewById(R.id.btRegister);
        btRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        registerView = findViewById(R.id.svRegister);
        progressView = findViewById(R.id.pbRegister);
    }

    private void attemptRegister() {
        if (registerTask != null) {
            return;
        }

        etFirstName.setError(null);
        etLastName.setError(null);
        etUsername.setError(null);
        etEmail.setError(null);
        etPassword.setError(null);

        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String email = etEmail.getText().toString();
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String passwordConfirm = etPasswordConfirm.getText().toString();

        boolean cancel = false;
        focusView = null;

        if (TextUtils.isEmpty(firstName)) {
            etFirstName.setError(getString(R.string.error_field_required));
            setFocusView(etFirstName);
            cancel = true;
        }

        if (TextUtils.isEmpty(lastName)) {
            etLastName.setError(getString(R.string.error_field_required));
            setFocusView(etLastName);
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.error_field_required));
            setFocusView(etEmail);
            cancel = true;
        }
        else if (!isEmailValid(email)) {
            etEmail.setError(getString(R.string.error_invalid_email));
            setFocusView(etEmail);
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            etUsername.setError(getString(R.string.error_field_required));
            setFocusView(etUsername);
            cancel = true;
        }
        else if (!isUsernameValid(username)) {
            etUsername.setError(getString(R.string.error_invalid_username));
            setFocusView(etUsername);
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_field_required));
            setFocusView(etPassword);
            cancel = true;
        }
        else if (!isPasswordValid(password)) {
            etPassword.setError(getString(R.string.error_invalid_password));
            setFocusView(etPassword);
            cancel = true;
        }

        if (TextUtils.isEmpty(passwordConfirm)) {
            etPasswordConfirm.setError(getString(R.string.error_field_required));
            setFocusView(etPasswordConfirm);
            cancel = true;
        }
        else if (!passwordConfirm.equals(password)) {
            etPasswordConfirm.setError(getString(R.string.error_incorrect_password_confirm));
            setFocusView(etPasswordConfirm);
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }
        else {
            showProgress(true);
            registerTask = new RegisterTask(firstName, lastName, email, username, password);
            registerTask.execute((Void) null);
        }
    }

    private void setFocusView(View v) {
        if (this.focusView == null)
            this.focusView = v;
    }

    private class RegisterTask extends AsyncTask<Void, Void, Boolean> {
        private final String firstName;
        private final String lastName;
        private final String email;
        private final String username;
        private final String password;

        RegisterTask(String firstName, String lastName, String email, String username, String password) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.username = username;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            focusView = null;

            currentCall = AccountResource.getInstance().register(firstName, lastName, email, username, password,
                    new Callback<com.dewes.odonto.domain.Status<List<com.dewes.odonto.domain.Status<User>>>>() {
                @Override
                public void onResult(com.dewes.odonto.domain.Status<List<com.dewes.odonto.domain.Status<User>>> status) {
                    Log.d("API", "onResult "+ status);

                    showProgress(false);

                    if (status != null) {
                        switch (status.getStatus()) {
                            case "error_empty_fields":
                                for (com.dewes.odonto.domain.Status<User> s : status.getData()) {
                                    switch (s.getStatus()) {
                                        case "error_already_in_use_email":
                                            etEmail.setError(getString(R.string.error_already_in_use_email));
                                            setFocusView(etEmail);
                                            break;
                                        case "error_invalid_email":
                                            etEmail.setError(getString(R.string.error_invalid_email));
                                            setFocusView(etEmail);
                                            break;
                                        case "error_already_in_use_username":
                                            etUsername.setError(getString(R.string.error_already_in_use_username));
                                            setFocusView(etUsername);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                if (focusView != null)
                                    focusView.requestFocus();
                                break;
                            case "error_create_account":
                                Snackbar.make(registerView, status.getMessage(), Snackbar.LENGTH_LONG).show();
                                break;
                            case "success_create_account":
                                if (status.getData() != null) {
                                    if (status.getData().get(0) != null) {
                                        User u = status.getData().get(0).getData();
                                        String email = u.getEmail();
                                        Toast.makeText(RegisterActivity.this,
                                                String.format(getResources().getString(R.string.success_create_account), email),
                                                Toast.LENGTH_LONG).show();

                                        /*
                                        Bundle bnd = getIntent().getExtras();
                                        bnd.putString(AccountManager.KEY_ACCOUNT_NAME, email);
                                        setResult(RESULT_OK, new Intent().putExtras(bnd));
                                        */
                                        RegisterActivity.this.finish();
                                    }
                                    else {
                                        Snackbar.make(registerView, getResources().getString(R.string.error_api_response), Snackbar.LENGTH_LONG).show();
                                    }
                                }
                                else {
                                    Snackbar.make(registerView, getResources().getString(R.string.error_api_response), Snackbar.LENGTH_LONG).show();
                                }
                                break;
                        }
                    }
                    else {
                        Snackbar.make(registerView, getResources().getString(R.string.error_api_response), Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onError() {
                    Log.d("API", "onError");
                    showProgress(false);
                    Snackbar.make(registerView, getResources().getText(R.string.error_no_connection), Snackbar.LENGTH_LONG).show();
                }
            });

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            registerTask = null;
            //showProgress(false);
        }

        @Override
        protected void onCancelled() {
            registerTask = null;
            showProgress(false);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            registerView.setVisibility(show ? View.GONE : View.VISIBLE);
            registerView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    registerView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }
        else {
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            registerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}

