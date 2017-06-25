package com.dewes.odonto.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.dewes.odonto.api.client.ServiceGenerator;
import com.dewes.odonto.authenticator.AccountConstants;

public class SplashActivity extends AppCompatActivity {

    private AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAccountManager = AccountManager.get(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Account[] accounts = mAccountManager.getAccountsByType(AccountConstants.ACCOUNT_TYPE);

        if (accounts.length > 0) {
            getTokenForAccountCreateIfNeeded(AccountConstants.ACCOUNT_TYPE, AccountConstants.AUTHTOKEN_TYPE_FULL_ACCESS);
        }
        else {
            addNewAccount(AccountConstants.ACCOUNT_TYPE, AccountConstants.AUTHTOKEN_TYPE_FULL_ACCESS);
        }
    }

    private void addNewAccount(String accountType, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.addAccount(accountType, authTokenType, null, null, this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle bnd = future.getResult();
                    Log.d("API", "addNewAccount.future " + bnd);
                    openMain();
                    /*
                    final String authToken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    if (authToken != null) {
                        Log.d("API", "Success retrieved token "+ authToken);
                        ServiceGenerator.setApiToken(authToken);
                        openMain();
                    }
                    else {
                        Log.d("API", "Failed to retrieve token");
                    }
                    */
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Log.d("API", "Exception " + e.getMessage());
                }
            }
        }, null);
    }

    private void getTokenForAccountCreateIfNeeded(String accountType, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthTokenByFeatures(accountType, authTokenType, null, this, null, null,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        Bundle bnd = null;
                        try {
                            bnd = future.getResult();

                            Log.d("API", "getTokenForAccountCreateIfNeeded " + bnd);

                            final String authToken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                            if (authToken != null) {
                                Log.d("API", "Success retrieved token "+ authToken);
                                ServiceGenerator.setApiToken(authToken);
                                openMain();
                            }
                            else {
                                Log.d("API", "Failed to retrieve token");
                            }

                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            Log.d("API", "Exception "+ e.getMessage());
                        }
                    }
                }, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public void removeAccount(int which) {
        final Account account = mAccountManager.getAccountsByType(AccountConstants.ACCOUNT_TYPE)[which];
        final AccountManagerFuture<Bundle> future = mAccountManager.removeAccount(account, SplashActivity.this,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        Bundle bnd = null;
                        try {
                            bnd = future.getResult();

                            Log.d("API", "removeAccount " + bnd);

                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            Log.d("API", "Exception "+ e.getMessage());
                        }
                    }
                }, null);
    }

    private void openMain() {
        SplashActivity.this.finish();
        SplashActivity.this.startActivity(
                new Intent(SplashActivity.this, MainActivity.class));
    }
}
