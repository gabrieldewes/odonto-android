package com.dewes.odonto.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dewes.odonto.R;
import com.dewes.odonto.adapters.AttachmentAdapter;
import com.dewes.odonto.api.client.Callback;
import com.dewes.odonto.api.client.CardResource;
import com.dewes.odonto.domain.Attachment;
import com.dewes.odonto.domain.Card;
import com.dewes.odonto.domain.Status;
import java.util.List;

import retrofit2.Call;

public class CardDetailActivity extends AppCompatActivity {

    private Card card;
    private TextView tvCardTitle;
    private TextView tvCardWhatafield;
    private TextView tvCardCreatedByAt;
    private TextView tvCardLastModifiedByAt;
    private Button btCardActions;
    private Button btCardArchive;
    private RecyclerView recyclerView;
    private View progressView;
    private TextView tvCardAttachment;
    private AttachmentAdapter attachmentAdapter;
    private Resources res;
    private Call currentCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        if (getIntent().getExtras() == null)
            finish();

        card = (Card) getIntent().getExtras().get("card");

        res = getResources();

        this.tvCardTitle = (TextView) findViewById(R.id.tvCardTitle);
        this.tvCardWhatafield = (TextView) findViewById(R.id.tvCardWhatafield);
        this.tvCardCreatedByAt = (TextView) findViewById(R.id.tvCardCreatedByAt);
        this.tvCardLastModifiedByAt = (TextView) findViewById(R.id.tvCardLastModifiedByAt);
        this.btCardActions = (Button) findViewById(R.id.btCardActions);
        this.btCardArchive = (Button) findViewById(R.id.btCardArchive);

        btCardArchive.setText(card.isDeleted() ? getResources().getText(R.string.action_recover_card) : getResources().getText(R.string.action_archive_card));

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        progressView = findViewById(R.id.fragment_card_progress);
        tvCardAttachment = (TextView) findViewById(R.id.tvCardAttachment);
        tvCardAttachment.setVisibility(View.GONE);

        RecyclerView.LayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        btCardArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleArchiveCard();
            }
        });

        btCardActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardDetailActivity.this.startActivity(
                        ActionActivity.getIntent(CardDetailActivity.this, card));
            }
        });

        populateFields();
        fetchAttachments();
    }

    public static Intent getIntent(Context context, Card card) {
        return new Intent(context, CardDetailActivity.class).putExtra("card", card);
    }

    private void fetchAttachments() {
        showProgress(true);
        currentCall = CardResource.getInstance().getAttachments(card.getId(), new Callback<List<Attachment>>() {
            @Override
            public void onResult(List<Attachment> attachments) {
                showProgress(false);

                if (attachments.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    tvCardAttachment.setVisibility(View.GONE);
                }
                else {
                    attachmentAdapter = new AttachmentAdapter(CardDetailActivity.this, attachments);
                    recyclerView.setAdapter(attachmentAdapter);
                    recyclerView.setVisibility(View.VISIBLE);
                    tvCardAttachment.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError() {
                showProgress(false);
                Snackbar.make(findViewById(R.id.scrollView), res.getString(R.string.error_no_connection), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void populateFields() {
        tvCardTitle.setText(String.format(res.getString(R.string.title_card), card.getId()));
        tvCardWhatafield.setText(card.getWhatafield());
        tvCardCreatedByAt.setText(Html.fromHtml(String.format(res.getString(R.string.title_audit_data_created), card.getCreatedBy(), card.getCreatedAtFormatted())));
        tvCardLastModifiedByAt.setText(Html.fromHtml(String.format(res.getString(R.string.title_audit_data_modified), card.getLastModifiedBy(), card.getLastModifiedAtFormatted())));
    }

    private void toggleArchiveCard() {
        btCardArchive.setEnabled(false);
        btCardArchive.setTextColor(Color.GRAY);
        boolean archive = !card.isDeleted();

        currentCall = CardResource.getInstance().archive(archive, card.getId(), new Callback<Status<Card>>() {
            @Override
            public void onResult(Status<Card> status) {
                Log.d("API", "onResult "+ status);

                if (status != null) {
                    card = status.getData();
                    btCardArchive.setText(status.getData().isDeleted() ? getResources().getText(R.string.action_recover_card) : getResources().getText(R.string.action_archive_card));
                    btCardArchive.setEnabled(true);
                    btCardArchive.setTextColor(getResources().getColor(R.color.colorCardButton));

                    Snackbar.make(recyclerView,
                            (card.isDeleted() ? res.getString(R.string.success_archived_card) : res.getString(R.string.success_recovered_card)), Snackbar.LENGTH_LONG).show();
                }

            }

            @Override
            public void onError() {
                Snackbar.make(recyclerView, res.getString(R.string.error_api_response), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentCall != null)
            currentCall.cancel();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

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
        }
    }
}
