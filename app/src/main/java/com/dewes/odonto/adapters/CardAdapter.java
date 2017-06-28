package com.dewes.odonto.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dewes.odonto.R;
import com.dewes.odonto.activities.ActionActivity;
import com.dewes.odonto.activities.CardDetailActivity;
import com.dewes.odonto.activities.MainActivity;
import com.dewes.odonto.api.client.Callback;
import com.dewes.odonto.api.client.CardResource;
import com.dewes.odonto.domain.Card;
import com.dewes.odonto.domain.Status;

import java.util.List;

/**
 * Created by Dewes on 14/06/2017.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private Context context;
    private List<Card> cards;

    public CardAdapter(Context context, List<Card> cards) {
        this.context = context;
        this.cards = cards;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_item_line, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        final Card card = cards.get(position);
        holder.setCardItem(card);
        holder.tvCardTitle.setText(String.format(this.context.getResources().getString(R.string.title_card), card.getId()));
        holder.tvCardWhatafield.setText(card.getWhatafield());
        holder.tvCardTimeAgo.setText(card.getTimeAgo());
        holder.btCardArchive.setText(card.isDeleted() ? this.context.getResources().getText(R.string.action_recover_card) : this.context.getResources().getText(R.string.action_archive_card));

        final Button btArchive = holder.btCardArchive;
        btArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleArchiveCard(btArchive, card, CardAdapter.this);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    @Override
    public long getItemId(int position) {
        return cards.get(position).getId();
    }

    public void remove(Card card) {
        this.cards.remove(card);
        notifyDataSetChanged();
    }

    public void reloadList(List<Card> moreCards) {
        Log.d("API", "reloadList appended "+ moreCards.size());
        this.cards.addAll(moreCards);
        notifyDataSetChanged();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {

        private Context context;

        private Card cardItem;
        private TextView tvCardTitle;
        private TextView tvCardWhatafield;
        private TextView tvCardTimeAgo;
        private Button btCardView;
        private Button btCardActions;
        private Button btCardArchive;

        public CardViewHolder(final View itemView) {
            super(itemView);
            this.context = itemView.getContext();
            this.tvCardTitle = (TextView) itemView.findViewById(R.id.tvCardTitle);
            this.tvCardWhatafield = (TextView) itemView.findViewById(R.id.tvCardWhatafield);
            this.tvCardTimeAgo = (TextView) itemView.findViewById(R.id.tvCardTimeAgo);
            this.btCardView = (Button) itemView.findViewById(R.id.btCardView);
            this.btCardActions = (Button) itemView.findViewById(R.id.btCardActions);
            this.btCardArchive = (Button) itemView.findViewById(R.id.btCardArchive);

            btCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(
                            CardDetailActivity.getIntent(context, cardItem));
                    //Toast.makeText(itemView.getContext(), "view", Toast.LENGTH_LONG).show();
                }
            });

            btCardActions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(
                            ActionActivity.getIntent(context, cardItem));
                    //Toast.makeText(itemView.getContext(), "actions", Toast.LENGTH_LONG).show();
                }
            });

            /*
            btCardArchive.setVisibility(View.GONE);
            btCardArchive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(itemView.getContext(), btCardArchive.getText(), Toast.LENGTH_LONG).show();
                }
            });
            */
        }

        public void setCardItem(Card card) {
            this.cardItem = card;
        }
    }

    private void toggleArchiveCard(final Button btArchive, final Card card, final CardAdapter adapter) {
        btArchive.setEnabled(false);
        btArchive.setTextColor(Color.GRAY);

        boolean archive = !card.isDeleted();

        CardResource.getInstance().archive(archive, card.getId(), new Callback<Status<Card>>() {
            @Override
            public void onResult(Status<Card> cardStatus) {
                Log.d("API", "onResult "+ cardStatus);

                if (cardStatus != null) {
                    Card resultCard = cardStatus.getData();
                    btArchive.setText(
                            cardStatus.getData().isDeleted()
                                    ? context.getResources().getText(R.string.action_recover_card)
                                    : context.getResources().getText(R.string.action_archive_card));
                    btArchive.setEnabled(true);
                    btArchive.setTextColor(context.getResources().getColor(R.color.colorCardButton));

                    adapter.remove(card);

                    Toast.makeText(context,
                            (resultCard.isDeleted()
                                ? context.getResources().getString(R.string.success_archived_card)
                                : context.getResources().getString(R.string.success_recovered_card)),
                            Toast.LENGTH_LONG)
                        .show();
                }
            }

            @Override
            public void onError() {

            }
        });
    }
}
