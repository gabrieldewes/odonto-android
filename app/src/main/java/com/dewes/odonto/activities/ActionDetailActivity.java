package com.dewes.odonto.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.design.widget.Snackbar;
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
import com.dewes.odonto.domain.Action;
import com.dewes.odonto.domain.Attachment;
import com.dewes.odonto.domain.Card;

import java.util.List;

public class ActionDetailActivity extends AppCompatActivity {

    private Card card;
    private Action action;
    private TextView tvActionTitle;
    private TextView tvActionWhatafield;
    private TextView tvActionCreatedByAt;
    private TextView tvActionLastModifiedByAt;
    private Button btSomeAction;

    private RecyclerView recyclerView;
    private View progressView;
    private TextView tvActionAttachment;

    private AttachmentAdapter attachmentAdapter;

    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_detail);

        if (getIntent().getExtras() == null)
            finish();

        action = (Action) getIntent().getExtras().get("action");
        card = (Card) getIntent().getExtras().get("card");

        res = getResources();

        this.tvActionTitle = (TextView) findViewById(R.id.tvActionTitle);
        this.tvActionWhatafield = (TextView) findViewById(R.id.tvActionWhatafield);
        this.tvActionCreatedByAt = (TextView) findViewById(R.id.tvActionCreatedByAt);
        this.tvActionLastModifiedByAt = (TextView) findViewById(R.id.tvActionLastModifiedByAt);
        this.btSomeAction = (Button) findViewById(R.id.btSomeAction);

        tvActionTitle.setText(String.format(res.getString(R.string.title_action), action.getId()));
        tvActionWhatafield.setText(action.getWhatafield());
        tvActionCreatedByAt.setText(Html.fromHtml(String.format(res.getString(R.string.title_audit_data_created), action.getCreatedBy(), action.getCreatedAt().humanReadable())));
        tvActionLastModifiedByAt.setText(Html.fromHtml(String.format(res.getString(R.string.title_audit_data_modified), action.getLastModifiedBy(), action.getLastModifiedAt().humanReadable())));

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        progressView = findViewById(R.id.progressBar);
        tvActionAttachment = (TextView) findViewById(R.id.tvActionAttachment);
        tvActionAttachment.setVisibility(View.GONE);
        showProgress(true);

        RecyclerView.LayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        CardResource.getInstance().getActionAttachments(card.getId(), action.getId(), new Callback<List<Attachment>>() {
            @Override
            public void onResult(List<Attachment> attachments) {
                Log.d("API", "onResult "+ attachments);
                showProgress(false);
                attachmentAdapter = new AttachmentAdapter(ActionDetailActivity.this, attachments);
                recyclerView.setAdapter(attachmentAdapter);

                if (attachments.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    tvActionAttachment.setVisibility(View.GONE);
                }
                else {
                    recyclerView.setVisibility(View.VISIBLE);
                    tvActionAttachment.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError() {
                Snackbar.make(recyclerView, res.getString(R.string.error_no_connection), Snackbar.LENGTH_LONG).show();
            }
        });

        btSomeAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionDetailActivity.this.startActivity(
                        ProfileActivity.getIntent(ActionDetailActivity.this, action.getCreatedBy()));
            }
        });

    }

    public static Intent getIntent(Context context, Card card, Action action) {
        return new Intent(context, ActionDetailActivity.class)
                .putExtra("action", action)
                .putExtra("card", card);
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
