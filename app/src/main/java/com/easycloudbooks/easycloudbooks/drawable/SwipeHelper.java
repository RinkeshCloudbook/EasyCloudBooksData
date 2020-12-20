package com.easycloudbooks.easycloudbooks.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.easycloudbooks.easycloudbooks.util.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public abstract class SwipeHelper extends ItemTouchHelper.SimpleCallback {
    public static String TAG = SwipeHelper.class.getSimpleName();

    public static final int BUTTON_WIDTH = 100;
    private RecyclerView recyclerView;
    private List<UnderlayButton> buttons;
    private GestureDetector gestureDetector;
    private Context context;
    private int swipedPos = -1;
    private float swipeThreshold = 0.5f;
    private Map<Integer, List<UnderlayButton>> buttonsBufferleft;
    private Map<Integer, List<UnderlayButton>> buttonsBufferright;
    private Queue<Integer> recoverQueue;

    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            for (UnderlayButton button : buttons){
                if(button.onClick(button,e,e.getX(), e.getY()))
                    break;
            }

            return true;
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent e) {
            if (swipedPos < 0) return false;
            Point point = new Point((int) e.getRawX(), (int) e.getRawY());
            RecyclerView.ViewHolder swipedViewHolder = recyclerView.findViewHolderForAdapterPosition(swipedPos);
            if(swipedViewHolder != null)
            {
            View swipedItem = swipedViewHolder.itemView;
            Rect rect = new Rect();
            swipedItem.getGlobalVisibleRect(rect);
            if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_UP ||e.getAction() == MotionEvent.ACTION_MOVE) {
                if ((rect.top < point.y && rect.bottom > point.y)) {
                    gestureDetector.onTouchEvent(e);
                }
                else {
                    recoverQueue.add(swipedPos);
                   swipedPos = -1;
                   recoverSwipedItem();
                }
            }}
            return false;
        }
    };


    public SwipeHelper(Context context, RecyclerView recyclerView) {

        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.recyclerView = recyclerView;
        this.context = context;
        this.buttons = new ArrayList<>();
        this.gestureDetector = new GestureDetector(context, gestureListener);
        this.recyclerView.setOnTouchListener(onTouchListener);
        buttonsBufferleft = new HashMap<>();
        buttonsBufferright = new HashMap<>();
        recoverQueue = new LinkedList<Integer>(){
            @Override
            public boolean add(Integer o) {
                if (contains(o))
                    return false;
                else
                    return super.add(o);
            }
        };

        attachSwipe();
    }


    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int pos = viewHolder.getAdapterPosition();
        if (swipedPos != pos)
            recoverQueue.add(swipedPos);

        swipedPos = pos;
        if(direction == ItemTouchHelper.RIGHT)
        {
            if (buttonsBufferright.containsKey(swipedPos))
                buttons = buttonsBufferright.get(swipedPos);
            else
                buttons.clear();
        }
        else {
            if (buttonsBufferleft.containsKey(swipedPos))
                buttons = buttonsBufferleft.get(swipedPos);
            else
                buttons.clear();
        }
        buttonsBufferleft.clear();
        buttonsBufferright.clear();
        swipeThreshold = 0.5f * buttons.size() * BUTTON_WIDTH;
        recoverSwipedItem();
    }

    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return swipeThreshold;
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return 0.1f * defaultValue;
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        return 5.0f * defaultValue;
    }

    public void removeListItem(View rowView, final int position) {

        Animation anim = AnimationUtils.loadAnimation(context,
                android.R.anim.slide_out_right);
        anim.setDuration(500);
        rowView.startAnimation(anim);
    }
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        int pos = viewHolder.getAdapterPosition();
        float translationX = dX;
        View itemView = viewHolder.itemView;
        if (pos < 0){
            swipedPos = pos;
            return;
        }

        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
           // if(dX < 0 || dX > 0) {
            List<UnderlayButton> bufferleft = new ArrayList<>();
            List<UnderlayButton> bufferright = new ArrayList<>();

                if (!buttonsBufferleft.containsKey(pos)) {
                    instantiateUnderlayButton(viewHolder, bufferleft, ItemTouchHelper.LEFT,pos);
                    buttonsBufferleft.put(pos, bufferleft);
                } else {
                    bufferleft = buttonsBufferleft.get(pos);
                }
                if (!buttonsBufferright.containsKey(pos)) {
                    instantiateUnderlayButton(viewHolder, bufferright, ItemTouchHelper.RIGHT,pos);
                    buttonsBufferright.put(pos, bufferright);
                } else {
                    bufferright = buttonsBufferright.get(pos);
                }

            if(dX < 0)
                translationX = dX * bufferleft.size() * BUTTON_WIDTH / itemView.getWidth();
            else
                translationX = dX * bufferright.size() * BUTTON_WIDTH / itemView.getWidth();
                drawButtons(c, itemView, bufferleft,bufferright, pos, translationX);
           // }
        }

        super.onChildDraw(c, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive);
    }
    private synchronized void recoverSwipedItem(){
        while (!recoverQueue.isEmpty()){
            int pos = recoverQueue.poll();
            if (pos > -1) {
                recyclerView.getAdapter().notifyItemChanged(pos);
            }
        }
    }

    private void drawButtons(Canvas c, View itemView, List<UnderlayButton> bufferleft,List<UnderlayButton> bufferright, int pos, float dX){

        float right = itemView.getRight();
        float left = itemView.getLeft();
        float dButtonWidth = 0;
        List<UnderlayButton> buffer;
        if(dX < 0){
            dButtonWidth = (-1) * dX / bufferleft.size();
            buffer = bufferleft;
        }
        else{
            dButtonWidth = (-1) * dX / bufferright.size();
            buffer = bufferright;
        }
        //Log.w(TAG, "drawButtons: ("+ dX+")-"+ buffer.size() );
        for (UnderlayButton button : buffer) {
            if (dX < 0) {
                left = right - dButtonWidth;
                button.onDraw(
                        c,
                        new RectF(
                                left,
                                itemView.getTop(),
                                right,
                                itemView.getBottom()
                        ),
                        pos, dX //(to draw button on right)
                );

                right = left;
            } else if (dX > 0) {
                right = left - dButtonWidth;
                button.onDraw(c,
                        new RectF(
                                right,
                                itemView.getTop(),
                                left,
                                itemView.getBottom()
                        ), pos, dX //(to draw button on left)
                );
                left = right;
            }
        }


        /*float right = itemView.getRight();
        float dButtonWidth = (-1) * dX / buffer.size();

        for (UnderlayButton button : buffer) {
            float left = right - dButtonWidth;
            button.onDraw(
                    c,
                    new RectF(
                            left,
                            itemView.getTop(),
                            right,
                            itemView.getBottom()
                    ),
                    pos
            );

            right = left;
        }*/
    }

    public void attachSwipe(){
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(this);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public abstract void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons,int Direction,int pos);

    public static class UnderlayButton {
        private String text;
        private int imageResId;
        private int color;
        private int pos;
        private int direction;
        private RectF clickRegion;
        private Context context;
        private UnderlayButtonClickListener clickListener;

        public UnderlayButton(Context context, String text,  int direction, int imageResId, int color, UnderlayButtonClickListener clickListener) {
            this.text = text;
            this.context = context;
            this.imageResId = imageResId;
            this.direction = direction;
            this.color = color;
            this.clickListener = clickListener;
        }

        public boolean onClick(UnderlayButton button,MotionEvent e,float x, float y){
            if (clickRegion != null && (button.direction == ItemTouchHelper.LEFT ? clickRegion.contains(x, y) : (clickRegion.top < y &&  y < clickRegion.bottom && clickRegion.left > x &&  x > clickRegion.right))){
                clickListener.onClick(e,pos);
                return true;
            }

            return false;
        }

        public void onDraw(Canvas c, RectF rect, int pos,float dX){


            // Draw background

           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


                if (dX > 0) {
                    Paint p = new Paint();
                    p.setColor(color);

                    c.drawRect(rect, p);
                    p.setColor(Color.WHITE);
                    p.setTextSize(UIUtils.convertDpToPixels(context, 12));
                    //  p.setTextSize(LayoutHelper.getPx(MyApplication.getAppContext(), 12));

                    Rect r = new Rect();
                    float cHeight = rect.height();
                    float cWidth = rect.width();
                    p.setTextAlign(Paint.Align.LEFT);
                    p.getTextBounds(text, 0, text.length(), r);


                    float x = cWidth / 2f - r.width() / 2f - r.left;
                    float y = cHeight / 2f + r.height() / 2f - r.bottom;
                    //Changed
                    c.save();
                    c.rotate(-90f);
                    c.drawText(text, -(y+rect.top)-( r.width()/ 2f) ,  -cWidth / 2f +( r.height()/ 2f) + rect.right, p);
                    c.restore();

                   //Original Without Rotate
                   // c.drawText(text, rect.left + x, rect.top + y , p);

                }
                else if (dX < 0) {

                    Paint p = new Paint();
                    p.setColor(color);

                    c.drawRect(rect, p);
                    p.setColor(Color.WHITE);
                    p.setTextSize(UIUtils.convertDpToPixels(context, 12));

                    Rect r = new Rect();
                    float cHeight = rect.height();
                    float cWidth = rect.width();
                    p.setTextAlign(Paint.Align.LEFT);
                    p.getTextBounds(text, 0, text.length(), r);
                    float x = cWidth / 2f - r.width() / 2f - r.left;
                    float y = cHeight / 2f + r.height() / 2f - r.bottom;
                    //changed
                    c.save();
                    c.rotate(-90f);
                    c.drawText(text, -(y+rect.top)-( r.width()/ 2f) ,  -cWidth / 2f +( r.height()/ 2f) + rect.right, p);
                    c.restore();

                    //Original Without Rotate
                    //c.drawText(text, rect.left + x, rect.top + y, p);
                }
            }

            //Log.w(TAG, "onDrawset: "+ rect.top );
            this.clickRegion = rect;
            this.pos = pos;
        }
    }

    public interface UnderlayButtonClickListener {
        void onClick(MotionEvent e,int pos);
    }
}