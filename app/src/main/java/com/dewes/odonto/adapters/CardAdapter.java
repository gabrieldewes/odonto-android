package com.dewes.odonto.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dewes.odonto.R;
import com.dewes.odonto.activities.ActionActivity;
import com.dewes.odonto.activities.CardDetailActivity;
import com.dewes.odonto.domain.Card;

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
        Card card = cards.get(position);
        holder.setCardItem(card);
        holder.tvCardTitle.setText("Card #"+ card.getId());
        holder.tvCardWhatafield.setText(card.getWhatafield());
        holder.btCardArchive.setText(card.isDeleted() ? "recover" : "archive");
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {

        private Context context;

        private Card cardItem;
        private TextView tvCardTitle;
        private TextView tvCardWhatafield;
        private Button btCardView;
        private Button btCardActions;
        private Button btCardArchive;

        public CardViewHolder(final View itemView) {
            super(itemView);
            this.context = itemView.getContext();
            this.tvCardTitle = (TextView) itemView.findViewById(R.id.tvCardTitle);
            this.tvCardWhatafield = (TextView) itemView.findViewById(R.id.tvCardWhatafield);
            this.btCardView = (Button) itemView.findViewById(R.id.btCardView);
            this.btCardActions = (Button) itemView.findViewById(R.id.btCardActions);
            this.btCardArchive = (Button) itemView.findViewById(R.id.btCardArchive);

            btCardArchive.setVisibility(View.GONE);

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

            btCardArchive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(itemView.getContext(), btCardArchive.getText(), Toast.LENGTH_LONG).show();
                }
            });
        }

        public void setCardItem(Card card) {
            this.cardItem = card;
        }
    }
}
