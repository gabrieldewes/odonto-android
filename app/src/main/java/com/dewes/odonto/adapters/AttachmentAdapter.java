package com.dewes.odonto.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.dewes.odonto.R;
import com.dewes.odonto.domain.Attachment;
import com.dewes.odonto.util.ImageHelper;

import java.util.List;

/**
 * Created by Dewes on 18/06/2017.
 */

public class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.AttachmentViewHolder>  {

    private Context context;
    private List<Attachment> attachments;
    private Resources res;

    public AttachmentAdapter(Context context, List<Attachment> attachments) {
        this.context = context;
        this.attachments = attachments;
        this.res = this.context.getResources();
    }

    @Override
    public AttachmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.attachment_item_line, parent, false);
        return new AttachmentAdapter.AttachmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AttachmentViewHolder holder, int position) {
        Attachment attachment = attachments.get(position);
        holder.setAttachmentItem(attachment);
        holder.tvAttachTitle.setText(String.format(res.getString(R.string.title_attachment), attachment.getId()));
        holder.tvAttachAlt.setText(attachment.getAlt());
        new ImageHelper(holder.ivAttachThumb).execute(attachment.getThumbUrl());
    }

    @Override
    public int getItemCount() {
        return attachments.size();
    }

    public class AttachmentViewHolder extends RecyclerView.ViewHolder {

        private Context context;

        private Attachment attachmentItem;
        private TextView tvAttachTitle;
        private TextView tvAttachAlt;
        private ImageView ivAttachThumb;

        public AttachmentViewHolder(final View itemView) {
            super(itemView);
            this.context = itemView.getContext();
            this.tvAttachTitle = (TextView) itemView.findViewById(R.id.tvAttachTitle);
            this.tvAttachAlt = (TextView) itemView.findViewById(R.id.tvAttachAlt);
            this.ivAttachThumb = (ImageView) itemView.findViewById(R.id.ivAttachThumb);
        }

        public void setAttachmentItem(Attachment attachment) {
            this.attachmentItem = attachment;
        }
    }
}
