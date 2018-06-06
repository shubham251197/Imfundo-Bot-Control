package com.example.shubham.imfundo;

import android.app.Dialog;
import android.app.admin.DeviceAdminService;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jmedeisis.draglinearlayout.DragLinearLayout;

import org.w3c.dom.Text;

import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class MainActivity extends AppCompatActivity implements ToolbarAdapter.ToolbarClickListener{


    RelativeLayout loopLayout;
    RelativeLayout setupLayout;
    FrameLayout mainView;
    CoordinatorLayout coordinatorLayout;
    DragLinearLayout dragLinearLayoutSetup;
    DragLinearLayout dragLinearLayoutLoop;
    RecyclerView ToolbarView;
    FloatingActionButton fabUpload,fabClear;
    int LastIdSetup,LastIdLoop,delayTime;
    ArrayList<Commands> commandlist;
    ToolbarAdapter commandAdapter;
    ArrayList<String> commandListSetup;
    ArrayList<String> commandListLoop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setupLayout=(RelativeLayout) findViewById(R.id.relativeLayoutSetup);
        loopLayout=(RelativeLayout) findViewById(R.id.relativeLayoutLoop);
        dragLinearLayoutLoop=(DragLinearLayout) findViewById(R.id.drag_layout_loop);
        dragLinearLayoutSetup=(DragLinearLayout) findViewById(R.id.drag_layout_setup);
        mainView=(FrameLayout) findViewById(R.id.main_view);
        ToolbarView=(RecyclerView) findViewById(R.id.recyclerView);
        fabUpload=(FloatingActionButton) findViewById(R.id.fab_upload);
        fabClear=(FloatingActionButton) findViewById(R.id.fab_clear);
        LastIdSetup=200;
        LastIdLoop=500;
        mainView.getForeground().setAlpha(0);
        commandListLoop=new ArrayList<>();
        commandListSetup=new ArrayList<>();
        commandlist=new ArrayList<>();
        commandAdapter= new ToolbarAdapter(this, commandlist, this);
        ToolbarView.setAdapter(commandAdapter);
        ToolbarView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true));
        ToolbarView.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(5f)));
        ToolbarView.getItemAnimator().setAddDuration(1000);
        ToolbarView.getItemAnimator().setRemoveDuration(1000);
        ToolbarView.getItemAnimator().setMoveDuration(1000);
        ToolbarView.getItemAnimator().setChangeDuration(1000);
        AddCommands();

        dragLinearLayoutSetup.setOnViewSwapListener(new DragLinearLayout.OnViewSwapListener() {
            @Override
            public void onSwap(View firstView, int firstPosition, View secondView, int secondPosition) {
                //Toast.makeText(MainActivity.this, firstPosition+ " "+ secondPosition+" "+firstView.getId()+" "+secondView.getId(), Toast.LENGTH_SHORT).show();

                String temp=commandListSetup.get(firstPosition);
                commandListSetup.set(firstPosition,commandListSetup.get(secondPosition));
                commandListSetup.set(secondPosition,temp);
            }
        });

        dragLinearLayoutLoop.setOnViewSwapListener(new DragLinearLayout.OnViewSwapListener() {
            @Override
            public void onSwap(View firstView, int firstPosition, View secondView, int secondPosition) {

                String temp=commandListLoop.get(firstPosition);
                commandListLoop.set(firstPosition,commandListLoop.get(secondPosition));
                commandListLoop.set(secondPosition,temp);
            }
        });

        fabUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String display =" SETUP : ";
                for (String s: commandListSetup)
                    display=display+" "+s;
                display+=" \nLOOP : ";
                for (String s: commandListLoop)
                    display=display+" "+s;

                Toast.makeText(MainActivity.this, display, Toast.LENGTH_SHORT).show();
            }
        });

        fabClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteCommands(view);
            }
        });
    }


    private void AddCommands() {

        commandlist.add(new Commands("ON",Constants.ON_CMD_ID));
        commandAdapter.notifyItemInserted(0);
        commandlist.add(new Commands("OFF",Constants.OFF_CMD_ID));
        commandAdapter.notifyItemInserted(1);
        commandlist.add(new Commands("DELAY",Constants.DELAY_CMD_ID));
        commandAdapter.notifyItemInserted(2);
        commandlist.add(new Commands("FORWARD",Constants.FWD_CMD_ID));
        commandAdapter.notifyItemInserted(3);
        commandlist.add(new Commands("BACKWARD",Constants.BCKD_CMD_ID));
        commandAdapter.notifyItemInserted(4);

    }

    @Override
    public void onToolbarClick(int Position,View view) {

        if(commandlist.get(Position).commandId== Constants.DELAY_CMD_ID){

            OnDelayClick(Position,view);

        }
        else
            PlaceCommand(Position,view);


    }


    private void PlaceCommand(final int position, View view) {

        // making the new view

        final TextView newCommand= new TextView(this);
        if(commandlist.get(position).commandId==Constants.DELAY_CMD_ID)
            newCommand.setText("\n\n"+delayTime+" SECONDS "+commandlist.get(position).commandTitle.toString());
        else
            newCommand.setText("\n\n"+commandlist.get(position).commandTitle.toString());
        newCommand.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        newCommand.setTextSize(8f);
        newCommand.setTextColor(Color.WHITE);

        DragLinearLayout.LayoutParams params=new DragLinearLayout.LayoutParams(0,0);
        switch(commandlist.get(position).commandId){

            case Constants.ON_CMD_ID : newCommand.setBackground(ContextCompat.getDrawable(this,R.drawable.on_button_shape));
                        params=new DragLinearLayout.LayoutParams(200,100);
                break;
            case Constants.OFF_CMD_ID : newCommand.setBackground(ContextCompat.getDrawable(this,R.drawable.off_button_shape));
                        params=new DragLinearLayout.LayoutParams(200,100);
                break;
            case Constants.DELAY_CMD_ID : newCommand.setBackground(ContextCompat.getDrawable(this,R.drawable.delay_button_shape));
                        params=new DragLinearLayout.LayoutParams(300,100);
                break;
            case Constants.FWD_CMD_ID : newCommand.setBackground(ContextCompat.getDrawable(this,R.drawable.fwd_button_shape));
                        params=new DragLinearLayout.LayoutParams(250,100);
                break;
            case Constants.BCKD_CMD_ID : newCommand.setBackground(ContextCompat.getDrawable(this,R.drawable.bkwd_button_shape));
                        params=new DragLinearLayout.LayoutParams(250,100);
                break;
            default:
                break;
        }




        // inserting the new view

        final View PopupView=getLayoutInflater().inflate(R.layout.command_dialog_layout,null);
        final PopupWindow popUp=new PopupWindow(PopupView,380,230,true);
        TextView choose_setup=(TextView) PopupView.findViewById(R.id.setup_dialog);
        TextView choose_loop=(TextView) PopupView.findViewById(R.id.loop_dialog);
        popUp.setBackgroundDrawable(new BitmapDrawable());
        popUp.setOutsideTouchable(true);
        popUp.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mainView.getForeground().setAlpha(0);
                popUp.dismiss();
            }
        });
        final DragLinearLayout.LayoutParams finalParams = params;

        choose_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newCommand.setId(++LastIdSetup);
                finalParams.setMargins(16,0,0,0);

                if(commandListSetup.size()>1) {
                    setupLayout.getLayoutParams().height = setupLayout.getHeight() + 100;
                    setupLayout.requestLayout();
                    dragLinearLayoutSetup.getLayoutParams().height=dragLinearLayoutSetup.getHeight()+100;
                    dragLinearLayoutSetup.requestLayout();
                }
                dragLinearLayoutSetup.addView(newCommand, finalParams);
                for(int i = 0; i < dragLinearLayoutSetup.getChildCount(); i++){
                    View child = dragLinearLayoutSetup.getChildAt(i);
                    dragLinearLayoutSetup.setViewDraggable(child, child);
                }
                commandListSetup.add(commandlist.get(position).commandTitle);
                popUp.dismiss();
            }
        });
        choose_loop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newCommand.setId(++LastIdLoop);
                finalParams.setMargins(16,0,0,0);

                if(commandListLoop.size()>1) {
                    loopLayout.getLayoutParams().height = loopLayout.getHeight() + 100;
                    loopLayout.requestLayout();
                    dragLinearLayoutLoop.getLayoutParams().height=dragLinearLayoutLoop.getHeight()+100;
                    dragLinearLayoutLoop.requestLayout();
                }
                dragLinearLayoutLoop.addView(newCommand, finalParams);
                for(int i = 0; i < dragLinearLayoutLoop.getChildCount(); i++){
                    View child = dragLinearLayoutLoop.getChildAt(i);
                    dragLinearLayoutLoop.setViewDraggable(child, child);
                }
                commandListLoop.add(commandlist.get(position).commandTitle);
                popUp.dismiss();
            }
        });
        mainView.getForeground().setAlpha(150);
        popUp.showAsDropDown(view);

        //ApplyDimBackground(view);


    }



    private void OnDelayClick(final int pos, final View view) {

        final View popUplayout=getLayoutInflater().inflate(R.layout.delay_dialog_layout,null);
        final PopupWindow popupWindow=new PopupWindow(popUplayout, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setContentView(popUplayout);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mainView.getForeground().setAlpha(0);
                popupWindow.dismiss();
            }
        });
        final NumberPicker nPick=(NumberPicker) popUplayout.findViewById(R.id.delay_seconds);
        nPick.setMaxValue(60);
        nPick.setMinValue(1);
        Button submit=(Button) popUplayout.findViewById(R.id.delay_dialog_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View submitview) {
                delayTime=nPick.getValue();
                popupWindow.dismiss();
                PlaceCommand(pos,view);
            }
        });
        mainView.getForeground().setAlpha(150);
        popupWindow.showAsDropDown(view);

        // ApplyDimBackground(popUplayout);
    }

    private void ApplyDimBackground(View nonDimlayout) {

        WindowManager wManager= (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params=(WindowManager.LayoutParams) nonDimlayout.getLayoutParams();
        params.flags=WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        params.dimAmount= 0.6f;
        wManager.updateViewLayout(nonDimlayout,params);
    }



    DeleteListAdapter LoopAdapter,SetupAdapter;

    private void deleteCommands(View view) {


        final View popUplayout=getLayoutInflater().inflate(R.layout.delete_popup,null);
        final PopupWindow popupWindow=new PopupWindow(popUplayout, 600,800,true);
        popupWindow.setContentView(popUplayout);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mainView.getForeground().setAlpha(0);
                popupWindow.dismiss();
            }
        });

        TextView clearAll=(TextView) popUplayout.findViewById(R.id.clear_all);
        TextView clearSetup=(TextView) popUplayout.findViewById(R.id.clear_setup);
        TextView clearLoop=(TextView) popUplayout.findViewById(R.id.clear_loop);
        RecyclerView Rsetup=(RecyclerView) popUplayout.findViewById(R.id.recycler_delete_setup);
        RecyclerView Rloop=(RecyclerView) popUplayout.findViewById(R.id.recycler_delete_loop);
        
        SetupAdapter=new DeleteListAdapter(this, commandListSetup, new DeleteListAdapter.DeleteListClickListener() {
            @Override
            public void onDeleteListClick(int Position, View view) {
               // deleteSingleCommandSetup(Position);
            }
        });

        LoopAdapter= new DeleteListAdapter(this, commandListLoop, new DeleteListAdapter.DeleteListClickListener() {
            @Override
            public void onDeleteListClick(int Position, View view) {
              //  deleteSingleCommandLoop(Position);
            }
        });


        Rsetup.setAdapter(SetupAdapter);
        Rsetup.setLayoutManager(new LinearLayoutManager(this));
        Rsetup.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(5f)));

        Rloop.setAdapter(LoopAdapter);
        Rloop.setLayoutManager(new LinearLayoutManager(this));
        Rloop.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(5f)));

        ItemTouchHelper itemTouchHelperSetup=new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN|ItemTouchHelper.UP,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                deleteSingleCommandSetup(viewHolder.getAdapterPosition());
            }
        });
        itemTouchHelperSetup.attachToRecyclerView(Rsetup);

        ItemTouchHelper itemTouchHelperLoop=new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN|ItemTouchHelper.UP,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                deleteSingleCommandLoop(viewHolder.getAdapterPosition());
            }
        });
        itemTouchHelperLoop.attachToRecyclerView(Rloop);
        
        
        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(commandListLoop.size()>2)
                    reduceLayoutLoop(commandListLoop.size()-2);
                if(commandListSetup.size()>2)
                    reduceLayoutSetup(commandListSetup.size()-2);
                dragLinearLayoutSetup.removeAllViews();
                dragLinearLayoutLoop.removeAllViews();
                commandListLoop.clear();
                commandListSetup.clear();
                SetupAdapter.notifyDataSetChanged();
                LoopAdapter.notifyDataSetChanged();
            }
        });


        clearSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(commandListSetup.size()>2)
                    reduceLayoutSetup(commandListSetup.size()-2);
                dragLinearLayoutSetup.removeAllViews();
                commandListSetup.clear();
                SetupAdapter.notifyDataSetChanged();
            }
        });

        clearLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(commandListLoop.size()>2)
                    reduceLayoutLoop(commandListLoop.size()-2);
                dragLinearLayoutLoop.removeAllViews();
                commandListLoop.clear();
                LoopAdapter.notifyDataSetChanged();
            }
        });


        mainView.getForeground().setAlpha(180);
        popupWindow.showAtLocation(view,Gravity.CENTER,0,0);
    }

    private void deleteSingleCommandLoop(int position) {
        if(commandListLoop.size()>2)
            reduceLayoutLoop(1);
        dragLinearLayoutLoop.removeViewAt(position);
        commandListLoop.remove(position);
        LoopAdapter.notifyDataSetChanged();
    }

    private void deleteSingleCommandSetup(int position) {
        if(commandListSetup.size()>2)
            reduceLayoutSetup(1);
        dragLinearLayoutSetup.removeViewAt(position);
        commandListSetup.remove(position);
        SetupAdapter.notifyDataSetChanged();
    }

    private void reduceLayoutSetup(int reduction){
        
        setupLayout.getLayoutParams().height=setupLayout.getHeight() - (reduction*100);
        setupLayout.requestLayout();
        
        dragLinearLayoutSetup.getLayoutParams().height=dragLinearLayoutSetup.getHeight() - (reduction*100);
        dragLinearLayoutSetup.requestLayout();
    }

    private void reduceLayoutLoop(int reduction){

        loopLayout.getLayoutParams().height=loopLayout.getHeight() - (reduction*100);
        loopLayout.requestLayout();

        dragLinearLayoutLoop.getLayoutParams().height=dragLinearLayoutLoop.getHeight() - (reduction*100);
        dragLinearLayoutLoop.requestLayout();
    }

}
