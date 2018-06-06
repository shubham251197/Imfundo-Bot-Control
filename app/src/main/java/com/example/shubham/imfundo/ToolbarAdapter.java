package com.example.shubham.imfundo;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by shubham on 29-05-2018.
 */

public class ToolbarAdapter extends RecyclerView.Adapter<ToolbarAdapter.ToolbarViewHolder> {

    Context mContext;
    ArrayList<Commands> mCommandList;
    ToolbarClickListener mListner;


    public ToolbarAdapter(Context context,ArrayList<Commands> list,ToolbarClickListener listener){
        mContext=context;
        mCommandList=list;
        mListner=listener;
    }

    @Override
    public ToolbarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(mContext).inflate(R.layout.toolbar_items,parent,false);

        return new ToolbarViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ToolbarViewHolder holder, int position) {

        Commands command=mCommandList.get(position);
        holder.commandTextview.setText("\n"+command.commandTitle);
        switch(command.commandId){
            case Constants.OFF_CMD_ID : holder.commandTextview.setBackground(ContextCompat.getDrawable(mContext,R.drawable.off_button_shape));
                break;
            case Constants.ON_CMD_ID : holder.commandTextview.setBackground(ContextCompat.getDrawable(mContext,R.drawable.on_button_shape));
                break;
            case Constants.DELAY_CMD_ID : holder.commandTextview.setBackground(ContextCompat.getDrawable(mContext,R.drawable.delay_button_shape));
                break;
            case Constants.FWD_CMD_ID : holder.commandTextview.setBackground(ContextCompat.getDrawable(mContext,R.drawable.fwd_button_shape));
                break;
            case Constants.BCKD_CMD_ID :  holder.commandTextview.setBackground(ContextCompat.getDrawable(mContext,R.drawable.bkwd_button_shape));
                break;
            default: break;

        }
    }

    @Override
    public int getItemCount() {
        return mCommandList.size();
    }


    public interface ToolbarClickListener{
        void onToolbarClick(int Position,View view);
    }


    public class ToolbarViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{


        TextView commandTextview;
        ToolbarClickListener commandListener;

        public ToolbarViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            commandTextview = itemView.findViewById(R.id.command_textview);
            commandListener = mListner;
        }


        @Override
        public void onClick(View view) {
            int position=getAdapterPosition();
            commandListener.onToolbarClick(position,view);
        }
    }

}
