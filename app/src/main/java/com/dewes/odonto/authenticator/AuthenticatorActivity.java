package com.dewes.odonto.authenticator;

import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.design.widget.Snackbar;
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
import com.dewes.odonto.activities.RegisterActivity;
import com.dewes.odonto.api.client.AuthResource;
import com.dewes.odonto.api.client.Callback;
import com.dewes.odonto.api.client.ServiceGenerator;
import com.dewes.odonto.domain.Token;
import com.dewes.odonto.util.StringUtils;
import static com.dewes.odonto.authenticator.AccountConstants.*;

public class AuthenticatorActivity extends AccountAuthenticatorActivity {

    private UserLoginTask mAuthTask = null;

    private EditText mLoginView;
    private EditText mPasswordView;

    private View mProgressView;
    private View mLoginFormView;

    private final String TAG = this.getClass().getSimpleName();

    private final int REQ_SIGNUP = 1;

    private AccountManager mAccountManager;
    private String mAuthTokenType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);

        mLoginView = (EditText) findViewById(R.id.accountLogin);
        mPasswordView = (EditText) findViewById(R.id.accountPassword);

        mAccountManager = AccountManager.get(getBaseContext());
        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);

        String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);

        if (mAuthTokenType == null)
            mAuthTokenType = AUTHTOKEN_TYPE_FULL_ACCESS;

        if (accountName != null) {
            mLoginView.setText(accountName);
        }

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
                Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                intent.putExtras(getIntent().getExtras());
                startActivityForResult(intent, REQ_SIGNUP);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {}
        else super.onActivityResult(requestCode, resultCode, data);
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        mLoginView.setError(null);
        mPasswordView.setError(null);

        String email = mLoginView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mLoginView.setError(getString(R.string.error_field_required));
            focusView = mLoginView;
            cancel = true;
        } else if (!isLoginValid(email)) {
            mLoginView.setError(getString(R.string.error_invalid_login));
            focusView = mLoginView;
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

    private void finishLogin(Intent intent) {
        Log.d("API", TAG + ".finishLogin");

        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(AccountConstants.PARAM_USER_PASS);

        final android.accounts.Account account
                = new android.accounts.Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authTokenType = mAuthTokenType;

            Log.d("API", TAG + ".finishLogin.addAccountExplicitly");
            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            Log.d("API", TAG + ".finishLogin.setAuthToken");
            mAccountManager.setAuthToken(account, authTokenType, authToken);

            ServiceGenerator.setApiToken(authToken);
        }
        else {
            Log.d("API", TAG + ".finishLogin.AccountManager.setPassword");
            mAccountManager.setPassword(account, accountPassword);
        }
        Log.d("API", TAG + ".finishLogin return "+ intent.getExtras());
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        AuthenticatorActivity.this.finish();
        /*
        AuthenticatorActivity.this.startActivity(
                new Intent(AuthenticatorActivity.this, MainActivity.class));
        */
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

            AuthResource.getInstance().authenticate(this.mEmail, this.mPassword, new Callback<com.dewes.odonto.domain.Status<Token>>() {
                @Override
                public void onResult(com.dewes.odonto.domain.Status<Token> status) {
                    Log.d("API", "onResult "+ status);

                    showProgress(false);

                    if (status != null) {
                        if (status.getStatus().equals("connected") || status.getStatus().equals("already_connected")) {
                            Token token = status.getData();
                            if (token != null) {

                                final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
                                Bundle data = new Bundle();
                                try {
                                    String authToken = token.getToken();
                                    data.putString(AccountManager.KEY_ACCOUNT_NAME, mEmail);
                                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                                    data.putString(AccountManager.KEY_AUTHTOKEN, authToken);
                                    data.putString(PARAM_USER_PASS, mPassword);
                                }
                                catch (Exception e) {
                                    data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                                }

                                finishLogin(
                                        new Intent()
                                                .putExtras(data));
                            }
                            else {
                                Snackbar.make(mLoginFormView, getResources().getText(R.string.error_api_response), Snackbar.LENGTH_INDEFINITE).show();
                            }
                        }
                        else if (status.getStatus().equals("account_not_activated")) {
                            mPasswordView.setText("");
                            Snackbar.make(mLoginFormView, getResources().getText(R.string.error_account_not_activated), Snackbar.LENGTH_INDEFINITE)
                                    .setAction("reenviar", new SnackbarClick())
                                    .show();
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

    public class SnackbarClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "reenviar", Toast.LENGTH_SHORT).show();
        }
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
}

