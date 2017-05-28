package com.immortplanet.drawlove.fragment.chatgroup;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.immortplanet.drawlove.R;
import com.immortplanet.drawlove.model.Group;
import com.immortplanet.drawlove.util.ButtonGroup;
import com.immortplanet.drawlove.util.ConfirmDialog;
import com.immortplanet.drawlove.util.SimpleDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tom on 5/10/17.
 */

@SuppressLint("ValidFragment")
public class ChatgroupChatFragment extends Fragment{

    Group chatGroup;
    int screenHeightDp;

    //-- components
    ImageView btSend;
    ImageView btSelectText;
    ImageView btSelectImage;
    ButtonGroup groupButton;
    LinearLayout layoutDraw;
    LinearLayout layoutDrawTools;
    EditText txtMessage;
    Spinner spTools;
    int selectedTool;
    FrameLayout frmCanvas;

    DrawingView dv ;
    private Paint mPaint;


    public ChatgroupChatFragment(Group group){
        super();
        this.chatGroup = group;
        this.groupButton = new ButtonGroup();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat_group_chat, container, false);
        btSend = (ImageView) rootView.findViewById(R.id.btSend);
        btSelectText = (ImageView) rootView.findViewById(R.id.btSelectText);
        btSelectImage = (ImageView)rootView.findViewById(R.id.btSelectDraw);
        layoutDraw = (LinearLayout) rootView.findViewById(R.id.layoutDraw);
        layoutDrawTools = (LinearLayout) rootView.findViewById(R.id.layoutDrawTools);
        txtMessage = (EditText) rootView.findViewById(R.id.txtMessage);
        spTools = (Spinner) rootView.findViewById(R.id.spTools);
        frmCanvas = (FrameLayout) rootView.findViewById(R.id.frmCanvas);

        //-- setup canvas
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);

        //-- populate tools
        List<SpinnerItemData> liItems = new ArrayList<>();
        liItems.add(new SpinnerItemData("Pen", R.drawable.pencil));
        liItems.add(new SpinnerItemData("Eraser", R.drawable.eraser));
        liItems.add(new SpinnerItemData("Clear", R.drawable.bin));
        spTools.setAdapter(new SpinnerImageItem(getActivity(), R.layout.spinner_image_item, R.id.txtLabel, liItems));
        spTools.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        selectedTool = 0;
                        //-- TODO
                        break;
                    case 1:
                        selectedTool = 1;
                        //-- TODO
                        break;
                    case 2:
                        new ConfirmDialog(getActivity(), "Do you want to clear all?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //-- clear the graphics
                                //-- TODO

                                //-- reselect last tool
                                spTools.setSelection(selectedTool);
                                dialog.dismiss();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //-- reselect last tool
                                spTools.setSelection(selectedTool);
                                dialog.dismiss();
                            }
                        }).show();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        selectedTool = 0;
        spTools.setSelection(selectedTool);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenHeightDp = (int)(displayMetrics.heightPixels / displayMetrics.density);

        groupButton.add(btSelectText, new OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutDrawTools.setVisibility(View.GONE);
                txtMessage.setVisibility(View.VISIBLE);
                ValueAnimator va = ValueAnimator.ofInt(screenHeightDp-40, 40);
                va.setDuration(300);
                va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Integer value = (Integer) animation.getAnimatedValue();
                        ViewGroup.LayoutParams params = layoutDraw.getLayoutParams();
                        params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
                        layoutDraw.setLayoutParams(params);
                    }
                });
                va.start();
            }
        });

        groupButton.add(btSelectImage, new OnClickListener(){
            @Override
            public void onClick(View v) {
                layoutDrawTools.setVisibility(View.VISIBLE);
                txtMessage.setVisibility(View.GONE);
                ValueAnimator va = ValueAnimator.ofInt(40, screenHeightDp-40);
                va.setDuration(300);
                va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Integer value = (Integer) animation.getAnimatedValue();
                        ViewGroup.LayoutParams params = layoutDraw.getLayoutParams();
                        params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
                        layoutDraw.setLayoutParams(params);
                    }
                });
                va.start();
            }
        });

        groupButton.setSelected(0);
        layoutDrawTools.setVisibility(View.GONE);
        txtMessage.setVisibility(View.VISIBLE);

        return rootView;
    }

    public class DrawingView extends View {

        public int width;
        public  int height;
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;
        Context context;
        private Paint circlePaint;
        private Path circlePath;

        public DrawingView(Context c) {
            super(c);
            context=c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath( mPath,  mPaint);
            canvas.drawPath( circlePath,  circlePaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;

                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            circlePath.reset();
            // commit the path to our offscreen
            mCanvas.drawPath(mPath,  mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }

    class SpinnerItemData{
        public String label;
        int imgResource;

        public SpinnerItemData(String label, int img){
            this.label = label;
            this.imgResource = img;
        }
    }

    class SpinnerImageItem extends ArrayAdapter<SpinnerItemData>{

        List<SpinnerItemData> liItems;
        int resource;
        LayoutInflater inflater;

        public SpinnerImageItem(Context context, int resource, int textViewResourceId, List<SpinnerItemData> objects) {
            super(context, resource, textViewResourceId, objects);
            liItems = objects;
            this.resource = resource;
            inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View thisView = convertView != null? convertView : (View) inflater.inflate(resource, parent, false);
            ImageView imgIcon = (ImageView) thisView.findViewById(R.id.imgItem);
            imgIcon.setImageResource(liItems.get(position).imgResource);
            TextView txtLabel = (TextView) thisView.findViewById(R.id.txtLabel);
            txtLabel.setVisibility(View.GONE);
            return thisView;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View thisView = convertView != null? convertView : (View) inflater.inflate(resource, parent, false);
            ImageView imgIcon = (ImageView) thisView.findViewById(R.id.imgItem);
            imgIcon.setImageResource(liItems.get(position).imgResource);
            TextView txtLabel = (TextView) thisView.findViewById(R.id.txtLabel);
            txtLabel.setText(liItems.get(position).label);
            return thisView;
        }
    }

}