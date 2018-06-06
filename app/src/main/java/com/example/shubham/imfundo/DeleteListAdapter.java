package com.example.shubham.imfundo;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by shubham on 01-06-2018.
 */

public class DeleteListAdapter extends RecyclerView.Adapter<DeleteListAdapter.DeleteListViewHolder>{

    Context mContext;
    ArrayList<String> mCommandList;
    DeleteListClickListener mListner;

    public DeleteListAdapter(Context mContext, ArrayList<String> mCommandList, DeleteListClickListener mListner) {
        this.mContext = mContext;
        this.mCommandList = mCommandList;
        this.mListner = mListner;
    }

    @Override
    public DeleteListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.delete_list_item,parent,false);

        return new DeleteListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DeleteListViewHolder holder, int position) {


        holder.commandTextview.setText("\n\n" + mCommandList.get(position).toString());
        switch (mCommandList.get(position).toString()){

            case "ON" : holder.commandTextview.setBackground(ContextCompat.getDrawable(mContext,R.drawable.on_button_shape));
                break;
            case "OFF" : holder.commandTextview.setBackground(ContextCompat.getDrawable(mContext,R.drawable.off_button_shape));
                break;
            case "FORWARD" : holder.commandTextview.setBackground(ContextCompat.getDrawable(mContext,R.drawable.fwd_button_shape));
                break;
            case "BACKWARD" : holder.commandTextview.setBackground(ContextCompat.getDrawable(mContext,R.drawable.bkwd_button_shape));
                break;
            default:  holder.commandTextview.setBackground(ContextCompat.getDrawable(mContext,R.drawable.delay_button_shape));
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mCommandList.size();
    }


    public interface DeleteListClickListener{
        void onDeleteListClick(int Position,View view);
    }


    public class DeleteListViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{


        TextView commandTextview;
        DeleteListClickListener commandListener;

        public DeleteListViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            commandTextview = itemView.findViewById(R.id.command_textview_delete);
            commandListener = mListner;
        }


        @Override
        public void onClick(View view) {
            int position=getAdapterPosition();
            commandListener.onDeleteListClick(position,view);
        }
    }
}
