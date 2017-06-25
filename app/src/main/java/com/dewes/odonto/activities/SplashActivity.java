package com.dewes.odonto.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.dewes.odonto.api.client.ServiceGenerator;
import com.dewes.odonto.authenticator.AccountConstants;

public class SplashActivity extends AppCompatActivity {

    private AccountManager mAccountManager;

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAccountManager = AccountManager.get(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //showAccountPicker(AccountConstants.AUTHTOKEN_TYPE_FULL_ACCESS, false);

        Account[] accounts = mAccountManager.getAccountsByType(AccountConstants.ACCOUNT_TYPE);

        if (accounts.length > 0) {
            //invalidateAuthToken(accounts[0], AccountConstants.AUTHTOKEN_TYPE_FULL_ACCESS);
            getExistingAccountAuthToken(accounts[0], AccountConstants.AUTHTOKEN_TYPE_FULL_ACCESS);
            //getTokenForAccount(AccountConstants.ACCOUNT_TYPE, AccountConstants.AUTHTOKEN_TYPE_FULL_ACCESS);
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
                    Log.d("API", TAG +".addNewAccount.future " + bnd);
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

    private void getTokenForAccount(String accountType, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthTokenByFeatures(accountType, authTokenType, null, this, null, null,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        Bundle bnd = null;
                        try {
                            bnd = future.getResult();

                            Log.d("API", TAG +".getTokenForAccount " + bnd);

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

    private void getExistingAccountAuthToken(Account account, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, authTokenType, null, this,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            Bundle bnd = future.getResult();

                            Log.d("API", TAG +".getExistingAccountAuthToken "+ bnd);

                            final String authToken = bnd.getString(AccountManager.KEY_AUTHTOKEN);

                            if (authToken != null) {
                                Log.d("API", "Success retrieved token "+ authToken);
                                ServiceGenerator.setApiToken(authToken);
                                openMain();
                            }
                            else {
                                Log.d("API", "Failed to retrieve token");
                            }

                        } catch (Exception e) {
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

                            Log.d("API", TAG +".removeAccount " + bnd);

                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            Log.d("API", "Exception "+ e.getMessage());
                        }
                    }
                }, null);
    }

    private void showAccountPicker(final String authTokenType, final boolean invalidate) {
        boolean mInvalidate = invalidate;
        AlertDialog mAlertDialog;
        final Account availableAccounts[] = mAccountManager.getAccountsByType(AccountConstants.ACCOUNT_TYPE);

        if (availableAccounts.length == 0) {
            Toast.makeText(this, "No accounts", Toast.LENGTH_SHORT).show();
        } else {
            String name[] = new String[availableAccounts.length];
            for (int i = 0; i < availableAccounts.length; i++) {
                name[i] = availableAccounts[i].name;
            }

            // Account picker
            mAlertDialog = new AlertDialog.Builder(this)
                    .setTitle("Pick Account")
                    .setAdapter(new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, name), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    /*
                    if(invalidate)
                        invalidateAuthToken(availableAccounts[which], authTokenType);
                    else
                        getExistingAccountAuthToken(availableAccounts[which], authTokenType);
                    */
                }
            }).create();
            mAlertDialog.show();
        }
    }

    private void invalidateAuthToken(final Account account, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, authTokenType, null, this,
                new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle bnd = future.getResult();

                    final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    mAccountManager.invalidateAuthToken(account.type, authtoken);
                    Log.d("API", TAG +".invalidateAuthToken " +account.name + " invalidated");
                } catch (Exception e) {
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
