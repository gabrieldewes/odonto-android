package com.dewes.odonto.activities;

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
import com.dewes.odonto.R;
import com.dewes.odonto.api.client.AccountResource;
import com.dewes.odonto.api.client.Callback;
import com.dewes.odonto.domain.Principal;
import com.dewes.odonto.services.AuthService;
import com.dewes.odonto.util.StringUtils;

public class LoginActivity extends AppCompatActivity {

    private AccountResource accountResource;
    private AuthService authService;

    private UserLoginTask mAuthTask = null;

    private EditText mEmailView;
    private EditText mPasswordView;

    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("snackbar")) {
            String snackbar = getIntent().getExtras().getString("snackbar");
            Snackbar.make(findViewById(R.id.login_form), snackbar, Snackbar.LENGTH_LONG).show();
        }

        accountResource = new AccountResource();
        authService = AuthService.getInstance(this, true);

        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button btOrRegister = (Button) findViewById(R.id.btOrRegister);

        btOrRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.finish();
                LoginActivity.this.startActivity(
                        new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isLoginValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_login));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }
        else {
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isLoginValid(String login) {
        return !login.contains(" ") && !StringUtils.hasUpperCase(login);
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 3 && !StringUtils.hasSpecial(password);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }
        else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            accountResource.authenticate(mEmail, mPassword, new Callback<com.dewes.odonto.domain.Status<Principal>>() {
                @Override
                public void onResult(com.dewes.odonto.domain.Status status) {
                    Log.d("API", "onResult "+ status);

                    showProgress(false);

                    if (status != null) {
                        if (status.getStatus().equals("connected")) {
                            Principal principal = (Principal) status.getData();
                            if (principal != null) {
                                authService.putPrincipal(principal);
                                LoginActivity.this.finish();
                                LoginActivity.this.startActivity(
                                        new Intent(LoginActivity.this, MainActivity.class));
                            }
                            else {
                                Snackbar.make(mLoginFormView, getResources().getText(R.string.error_api_response), Snackbar.LENGTH_LONG).show();
                            }
                        }
                        else {
                            mPasswordView.setText("");
                            Snackbar.make(mLoginFormView, getResources().getText(R.string.error_bad_credentials), Snackbar.LENGTH_LONG).show();
                        }

                    }
                    else {
                        Snackbar.make(mLoginFormView, getResources().getText(R.string.error_api_response), Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onError() {
                    showProgress(false);
                    Snackbar.make(mLoginFormView, getResources().getText(R.string.error_no_connection), Snackbar.LENGTH_LONG).show();
                }
            });

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            //showProgress(false);

            if (success) {} else {}
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

