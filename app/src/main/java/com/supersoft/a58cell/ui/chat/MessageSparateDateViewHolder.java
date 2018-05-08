package com.supersoft.a58cell.ui.chat;

import android.view.View;
import android.widget.TextView;

import com.supersoft.a58cell.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by itclub21 on 12/16/2017.
 */

public class MessageSparateDateViewHolder extends MessageViewHolder {

    @BindView(R.id.message_body) public TextView txtMessage;
    public MessageSparateDateViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
