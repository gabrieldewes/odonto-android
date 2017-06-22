package com.dewes.odonto.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dewes.odonto.R;
import com.dewes.odonto.api.client.Callback;
import com.dewes.odonto.api.client.CardResource;
import com.dewes.odonto.domain.Card;
import com.dewes.odonto.services.AuthService;

import java.io.FileDescriptor;
import java.io.IOException;

import retrofit2.Call;

public class CardCreateActivity extends AppCompatActivity {

    private CreateCardTask createCardTask = null;
    private Call currentCall;
    private CardResource cardResource;
    private AuthService authService;

    private EditText etWhatafield;

    private View progressView;
    private View createCardView;
    private View focusView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_create);

        authService = AuthService.getInstance(this, false);
        cardResource = CardResource.getInstance(authService.getToken(), false);

        etWhatafield = (EditText) findViewById(R.id.etWhatafield);

        Button btCreateCard = (Button) findViewById(R.id.btCreateCard);

        btCreateCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptCreate();
            }
        });

        createCardView = findViewById(R.id.createCardForm);
        progressView = findViewById(R.id.progressBar);
    }

    private void attemptCreate() {
        if (createCardTask != null) {
            return;
        }

        etWhatafield.setError(null);

        String whatafield = etWhatafield.getText().toString();

        boolean cancel = false;
        focusView = null;

        if (TextUtils.isEmpty(whatafield)) {
            etWhatafield.setError(getString(R.string.error_field_required));
            setFocusView(etWhatafield);
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }
        else {
            showProgress(true);
            createCardTask = new CreateCardTask(whatafield);
            createCardTask.execute((Void) null);
        }
    }

    private void setFocusView(View v) {
        if (this.focusView == null)
            this.focusView = v;
    }

    public class CreateCardTask extends AsyncTask<Void, Void, Boolean> {

        private final String whatafield;

        CreateCardTask(String whatafield) {
            this.whatafield = whatafield;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            focusView = null;

            currentCall = cardResource.create(whatafield, new Callback<com.dewes.odonto.domain.Status<Card>>() {
                @Override
                public void onResult(com.dewes.odonto.domain.Status<Card> status) {
                    Log.d("API", "Called "+ currentCall.request().url());
                    Log.d("API", "onResult "+ status);

                    showProgress(false);

                    if (status != null) {
                        if (status.getStatus().equals("error_create_card")) {
                            Snackbar.make(createCardView, getResources().getString(R.string.error_create_card), Snackbar.LENGTH_LONG).show();
                        }
                        else if (status.getStatus().equals("create_card")) {
                            Toast.makeText(CardCreateActivity.this, getResources().getString(R.string.success_created_card), Toast.LENGTH_LONG).show();
                            CardCreateActivity.this.finish();
                        }
                    }
                    else {
                        Snackbar.make(createCardView, getResources().getString(R.string.error_api_response), Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onError() {
                    Log.d("API", "onError");
                    showProgress(false);
                    Snackbar.make(createCardView, getResources().getText(R.string.error_no_connection), Snackbar.LENGTH_LONG).show();
                }
            });
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            createCardTask = null;
            //showProgress(false);

            if (success) {} else {}
        }

        @Override
        protected void onCancelled() {
            createCardTask = null;
            showProgress(false);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            createCardView.setVisibility(show ? View.GONE : View.VISIBLE);
            createCardView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    createCardView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            createCardView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
