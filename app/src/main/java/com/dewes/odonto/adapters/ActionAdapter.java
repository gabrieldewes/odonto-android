package com.dewes.odonto.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dewes.odonto.R;
import com.dewes.odonto.activities.CardDetailActivity;
import com.dewes.odonto.domain.Action;
import com.dewes.odonto.domain.Card;

import java.util.List;

/**
 * Created by Dewes on 14/06/2017.
 */

public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ActionViewHolder> {

    private Context context;
    private List<Action> actions;

    public ActionAdapter(Context context, List<Action> actions) {
        this.context = context;
        this.actions = actions;
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
        holder.tvActionType.setText("Action #"+ action.getId());
        holder.tvActionWhatafield.setText(action.getWhatafield());
    }

    @Override
    public int getItemCount() {
        return actions.size();
    }

    public class ActionViewHolder extends RecyclerView.ViewHolder {

        private Context context;

        private Action actionItem;
        private TextView tvActionType;
        private TextView tvActionWhatafield;
        private Button btActionView;

        public ActionViewHolder(final View itemView) {
            super(itemView);
            this.context = itemView.getContext();
            this.tvActionType = (TextView) itemView.findViewById(R.id.tvActionType);
            this.tvActionWhatafield = (TextView) itemView.findViewById(R.id.tvActionWhatafield);
            this.btActionView = (Button) itemView.findViewById(R.id.btActionView);

            btActionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Toast.makeText(itemView.getContext(), "view", Toast.LENGTH_LONG).show();
                }
            });
        }

        public void setActionItem(Action action) {
            this.actionItem = action;
        }
    }
}
