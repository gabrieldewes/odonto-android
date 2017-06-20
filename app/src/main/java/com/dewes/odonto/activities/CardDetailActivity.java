package com.dewes.odonto.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.dewes.odonto.services.AuthService;

import java.util.List;

public class CardDetailActivity extends AppCompatActivity {

    private Card card;
    private TextView tvCardTitle;
    private TextView tvCardWhatafield;
    private Button btCardActions;
    private Button btCardArchive;

    private RecyclerView recyclerView;
    private View progressView;
    private TextView tvCardAttachment;

    private CardResource cardResource;
    private AuthService authService;

    private AttachmentAdapter attachmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        if (getIntent().getExtras() == null)
            finish();

        card = (Card) getIntent().getExtras().get("card");

        this.tvCardTitle = (TextView) findViewById(R.id.tvCardTitle);
        this.tvCardWhatafield = (TextView) findViewById(R.id.tvCardWhatafield);
        this.btCardActions = (Button) findViewById(R.id.btCardActions);
        this.btCardArchive = (Button) findViewById(R.id.btCardArchive);

        btCardArchive.setText(card.isDeleted() ? "recover" : "archive");

        tvCardTitle.setText("Card #"+ card.getId());
        tvCardWhatafield.setText(card.getWhatafield());

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        progressView = findViewById(R.id.fragment_card_progress);
        tvCardAttachment = (TextView) findViewById(R.id.tvCardAttachment);
        tvCardAttachment.setVisibility(View.GONE);
        showProgress(true);

        RecyclerView.LayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        authService = AuthService.getInstance(this, true);
        cardResource = new CardResource(authService.getCurrentUserId());

        cardResource.getAttachments(card.getId(), new Callback<List<Attachment>>() {
            @Override
            public void onResult(List<Attachment> attachments) {
                showProgress(false);
                attachmentAdapter = new AttachmentAdapter(CardDetailActivity.this, attachments);
                recyclerView.setAdapter(attachmentAdapter);

                if (attachments.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    tvCardAttachment.setVisibility(View.GONE);
                }
                else {
                    recyclerView.setVisibility(View.VISIBLE);
                    tvCardAttachment.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError() {
                Snackbar.make(findViewById(R.id.scrollView), "No connection", Snackbar.LENGTH_LONG).show();
            }
        });

        btCardArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btCardArchive.setEnabled(false);
                btCardArchive.setTextColor(Color.GRAY);
                boolean archive = !card.isDeleted();

                cardResource.archive(archive, card.getId(), new Callback<Status<Card>>() {
                    @Override
                    public void onResult(Status<Card> status) {
                        Log.d("API", "onResult "+ status);
                        card = status.getData();
                        btCardArchive.setText(status.getData().isDeleted() ? "recover" : "archive");
                        btCardArchive.setEnabled(true);
                        btCardArchive.setTextColor(getResources().getColor(R.color.colorCardButton));
                        Snackbar.make(recyclerView, "Card "+ (card.isDeleted() ? "archived." : "recovered."), Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError() {
                        Log.d("API", "onError");
                        Snackbar.make(recyclerView, "An error ocurred while updating card.", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public static Intent getIntent(Context context, Card card) {
        return new Intent(context, CardDetailActivity.class).putExtra("card", card);
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
