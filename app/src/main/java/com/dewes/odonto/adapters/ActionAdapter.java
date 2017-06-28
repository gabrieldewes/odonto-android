package com.dewes.odonto.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dewes.odonto.R;
import com.dewes.odonto.activities.ActionDetailActivity;
import com.dewes.odonto.domain.Action;
import com.dewes.odonto.domain.Card;
import com.dewes.odonto.util.ImageHelper;

import java.util.List;

/**
 * Created by Dewes on 14/06/2017.
 */

public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ActionViewHolder> {

    private Context context;
    private Card card;
    private List<Action> actions;
    private Resources res;

    public ActionAdapter(Context context, Card card, List<Action> actions) {
        this.context = context;
        this.card = card;
        this.actions = actions;
        this.res = this.context.getResources();
    }

    @Override
    public ActionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.action_item_line, parent, false);
        return new ActionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ActionViewHolder holder, int position) {
        Action action = actions.get(position);
        holder.setActionItem(action);
        holder.tvActionType.setText(String.format(res.getString(R.string.title_action), action.getId()));
        holder.tvActionWhatafield.setText(action.getWhatafield());
        holder.tvActionTimeAgo.setText(action.getTimeAgo());
        new ImageHelper(holder.ivActionThumb).execute(action.getThumbUrl());
    }

    @Override
    public int getItemCount() {
        return actions.size();
    }

    public void reloadList(List<Action> moreActions) {
        Log.d("API", "reloadList appended "+ moreActions.size());
        this.actions.addAll(moreActions);
        //notifyItemInserted(moreCards.size() - 1);
        notifyDataSetChanged();
    }

    public class ActionViewHolder extends RecyclerView.ViewHolder {

        private Context context;

        private Action actionItem;
        private ImageView ivActionThumb;
        private TextView tvActionType;
        private TextView tvActionWhatafield;
        private TextView tvActionTimeAgo;
        private Button btActionView;

        public ActionViewHolder(final View itemView) {
            super(itemView);
            this.context = itemView.getContext();
            this.ivActionThumb = (ImageView) itemView.findViewById(R.id.ivActionThumb);
            this.tvActionType = (TextView) itemView.findViewById(R.id.tvActionType);
            this.tvActionWhatafield = (TextView) itemView.findViewById(R.id.tvActionWhatafield);
            this.tvActionTimeAgo = (TextView) itemView.findViewById(R.id.tvActionTimeAgo);
            this.btActionView = (Button) itemView.findViewById(R.id.btActionView);

            btActionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(itemView.getContext(), "view", Toast.LENGTH_LONG).show();
                    context.startActivity(
                            ActionDetailActivity.getIntent(context, card, actionItem));
                }
            });
        }

        public void setActionItem(Action action) {
            this.actionItem = action;
        }
    }
}
