package com.immortplanet.drawlove.fragment.chatgroup;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.immortplanet.drawlove.R;
import com.immortplanet.drawlove.model.DataSingleton;
import com.immortplanet.drawlove.model.Group;
import com.immortplanet.drawlove.model.User;
import com.immortplanet.drawlove.model.Message;
import com.immortplanet.drawlove.util.ButtonGroup;
import com.immortplanet.drawlove.util.ConfirmDialog;
import com.immortplanet.drawlove.util.MessageHandler;
import com.immortplanet.drawlove.util.ObjectCallback;
import com.immortplanet.drawlove.util.SliderDialog;
import com.immortplanet.drawlove.util.SocketSubscribe;
import com.immortplanet.drawlove.util.Util;
import com.turkialkhateeb.materialcolorpicker.ColorChooserDialog;
import com.turkialkhateeb.materialcolorpicker.ColorListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tom on 5/10/17.
 */

@SuppressLint("ValidFragment")
public class ChatgroupChatFragment extends Fragment{

    static final int ACK_MESSAGE = -1;
    static final int TEXT_MESSAGE = 0;
    static final int DRAW_MESSAGE = 1;

    Group chatGroup;
    int screenHeightDp;
    MessageHandler handler;

    //-- components
    ListView liMessages;
    ImageView btSend;
    ImageView btSelectText;
    ImageView btSelectImage;
    ButtonGroup groupButton;
    LinearLayout layoutDraw;
    LinearLayout layoutDrawTools;
    EditText txtMessage;
    Spinner spTools;
    int selectedMode;
    int selectedTool;
    FrameLayout frmCanvas;

    //-- tools
    ImageView btColorPalette;
    ImageView btStrokeWeight;
    ImageView btEmoji;
    ImageView btBackground;

    MessageAdater adapter;

    DrawingView dv;
    private Paint mPaint;
    ColorChooserDialog colorDialog;
    SliderDialog sliderDialog;

    int lastColor;
    int stroke;

    User currentUser;

    public ChatgroupChatFragment(Group group){
        super();
        this.chatGroup = group;
        this.groupButton = new ButtonGroup();
        currentUser = (User) DataSingleton.getDataSingleton().get("currentUser");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat_group_chat, container, false);
        liMessages = (ListView) rootView.findViewById(R.id.liMessages);
        btSend = (ImageView) rootView.findViewById(R.id.btSend);
        btSelectText = (ImageView) rootView.findViewById(R.id.btSelectText);
        btSelectImage = (ImageView)rootView.findViewById(R.id.btSelectDraw);
        layoutDraw = (LinearLayout) rootView.findViewById(R.id.layoutDraw);
        layoutDrawTools = (LinearLayout) rootView.findViewById(R.id.layoutDrawTools);
        txtMessage = (EditText) rootView.findViewById(R.id.txtMessage);
        spTools = (Spinner) rootView.findViewById(R.id.spTools);
        frmCanvas = (FrameLayout) rootView.findViewById(R.id.frmCanvas);

        lastColor = Color.BLACK;
        stroke = 15;
        selectedMode = TEXT_MESSAGE;

        adapter = new MessageAdater(getActivity(), 0, this.chatGroup.messages);

        liMessages.setAdapter(adapter);
        liMessages.setDivider(null);

        colorDialog = new ColorChooserDialog(getActivity());
        colorDialog.setTitle("Select pen color");
        colorDialog.setColorListener(new ColorListener() {
            @Override
            public void OnColorClick(View v, int color) {
                dv.setPaintColor(color);
            }
        });
        colorDialog.setCanceledOnTouchOutside(true);

        sliderDialog = new SliderDialog(getActivity(), "Choose desired stroke", 5, stroke, 30, new ObjectCallback(){
            @Override
            public void callback(Object data) {
                mPaint.setStrokeWidth((Integer)data);
            }
        });

        btColorPalette = (ImageView) rootView.findViewById(R.id.btColorPalette);
        btColorPalette.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //-- show color picker dialog
                colorDialog.show();
            }
        });

        btStrokeWeight = (ImageView) rootView.findViewById(R.id.btStrokeWeight);
        btStrokeWeight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sliderDialog.show();
            }
        });

        //-- TODO
        btEmoji = (ImageView) rootView.findViewById(R.id.btEmoji);
        btBackground = (ImageView) rootView.findViewById(R.id.btBackGround);


        btSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean sendReady = false;
                JSONObject jsonObject = new JSONObject();
                String content = "";
                long timestamp = 0;
                try {
                    timestamp = (new Date()).getTime();
                    jsonObject.put("group", chatGroup._id);
                    jsonObject.put("timestamp", timestamp);
                    jsonObject.put("contentType", selectedMode);
                }catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println("selectedMode: " + selectedMode);

                switch (selectedMode){
                    case TEXT_MESSAGE:
                        if (txtMessage.getText().length() > 0){
                            try {
                                content = txtMessage.getText().toString();
                                jsonObject.put("content", content);
                                txtMessage.setText("");
                                sendReady = true;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        break;

                    case DRAW_MESSAGE:
                        try {
                            content = Util.encodeToBase64(dv.mBitmap);
                            jsonObject.put("content", content);
                            System.out.print("iamge content: " + content);
                            dv.clearCanvas();
                            sendReady = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }

                if (sendReady){
                    //-- add new message to adapter
                    Message m = new Message();
                    m.content = content;
                    m.contentType = selectedMode;
                    m.sender = currentUser._id;
                    m.timestamp = timestamp;

                    adapter.add(m);
                    liMessages.post(new Runnable() {
                        @Override
                        public void run() {
                            liMessages.smoothScrollToPosition(adapter.getCount()-1);
                        }
                    });
                    SocketSubscribe.emit("chat:" + chatGroup._id, jsonObject);
                }
            }
        });


        //-- setup painter
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(stroke);


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
                        btColorPalette.setVisibility(View.VISIBLE);
                        mPaint.setColor(lastColor);
                        break;

                    case 1:
                        selectedTool = 1;
                        btColorPalette.setVisibility(View.GONE);
                        lastColor = mPaint.getColor();
                        mPaint.setColor(Color.WHITE);
                        break;
                    case 2:
                        ConfirmDialog dialog = new ConfirmDialog(getActivity(), "Do you want to clear all?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //-- clear the graphics
                                dv.clearCanvas();
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
                        });
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                selectedTool = 0;
                                spTools.setSelection(selectedTool);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedTool = 0;
                spTools.setSelection(selectedTool);
            }
        });

        selectedTool = 0;
        spTools.setSelection(selectedTool);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenHeightDp = (int)(displayMetrics.heightPixels / displayMetrics.density);

        groupButton.add(btSelectText, new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMode = TEXT_MESSAGE;
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
                selectedMode = DRAW_MESSAGE;
                layoutDrawTools.setVisibility(View.VISIBLE);
                txtMessage.setVisibility(View.GONE);
                Util.hideSoftKeyboard(getActivity());
                ValueAnimator va = ValueAnimator.ofInt(40, screenHeightDp-40);
                va.setDuration(300);
                va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Integer value = (Integer) animation.getAnimatedValue();
                        if (value >= screenHeightDp-40){
                            if (dv == null){
                                dv = new DrawingView(getActivity(), frmCanvas.getWidth(), frmCanvas.getHeight());
                                ViewGroup.LayoutParams params  = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                dv.setLayoutParams(params);
                                frmCanvas.addView(dv);
                            }
                        }
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

        if (handler == null) handler = SocketSubscribe.subscribe("chat:" + chatGroup._id, 0, new Handler(){
            @Override
            public void handleMessage(android.os.Message msg){
                JSONObject jsonObject = (JSONObject) msg.obj;
                int contentType = -1;
                try {
                    contentType = jsonObject.getInt("contentType");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                switch (contentType){

                    case ACK_MESSAGE:
                        //-- update message status
                        try {
                            String _id = jsonObject.getString("_id");
                            long timestamp = jsonObject.getLong("timestamp");
                            //-- check messages
                            for (Message m : chatGroup.messages){
                                if (timestamp == m.timestamp){
                                    //-- this is the message to update
                                    m._id = _id;
                                    m.status = Message.SENT;
                                    adapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;

                    case TEXT_MESSAGE:
                    case DRAW_MESSAGE:
                        Message m = new Message(jsonObject);
                        adapter.add(m);
                        liMessages.post(new Runnable() {
                            @Override
                            public void run() {
                                liMessages.smoothScrollToPosition(adapter.getCount()-1);
                            }
                        });
                        break;
                }

            }
        });

        return rootView;
    }

    public class DrawingView extends View {

        public int width;
        public int height;
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;
        Context context;
        private Paint circlePaint;
//        private Path circlePath;

        public DrawingView(Context c, int width, int height) {
            super(c);
            context=c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
//            circlePath = new Path();

            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);

            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            clearCanvas();
        }

        public void setPaintColor(int color){
            mPaint.setColor(color);
        }
        public void clearCanvas(){
            mCanvas.drawColor(Color.WHITE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, mPaint);
//            canvas.drawPath(circlePath,  circlePaint);
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

//                circlePath.reset();
//                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
//            circlePath.reset();
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
                    this.getParent().requestDisallowInterceptTouchEvent(true);
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

    class MessageAdater extends ArrayAdapter<com.immortplanet.drawlove.model.Message>{

        LayoutInflater inflater;
        ArrayList<Message> messages;
        HashMap<String, Bitmap> bitmaps;

        public MessageAdater(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<com.immortplanet.drawlove.model.Message> objects) {
            super(context, resource, objects);
            this.messages = objects;
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            bitmaps = new HashMap<>();
        }

        @Override
        public void add(Message m) {
            super.insert(m, 0);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Message message = messages.get(messages.size() - 1 - position);
            View rootView = null;
            ProgressBar prSent;

            switch (message.contentType){
                case TEXT_MESSAGE:
                    rootView = inflater.inflate(R.layout.message_text, null);
                    TextView txtContent = (TextView) rootView.findViewById(R.id.txtContent);
                    prSent = (ProgressBar) rootView.findViewById(R.id.prSent);
                    txtContent.setText(message.content);

                    if (currentUser._id.equals(message.sender)){
                        LinearLayout layoutContainer = (LinearLayout) rootView.findViewById(R.id.layoutContainer);
                        txtContent.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutContainer.getLayoutParams();
                        params.gravity = Gravity.RIGHT;
                        layoutContainer.setLayoutParams(params);
                        prSent.setVisibility(message.status == Message.SENT? View.GONE : View.VISIBLE);
                        txtContent.setTextColor(Color.WHITE);
                        txtContent.setBackgroundColor(Color.rgb(223, 61, 130));
                    }
                    else{
                        prSent.setVisibility(View.GONE);
                        txtContent.setTextColor(Color.BLACK);
                        txtContent.setBackgroundColor(Color.WHITE);
                    }
                    break;

                case DRAW_MESSAGE:
                    rootView = inflater.inflate(R.layout.message_draw, null);
                    ImageView imgDraw = (ImageView) rootView.findViewById(R.id.imgDraw);
                    prSent = (ProgressBar) rootView.findViewById(R.id.prSent);
                    //-- set image content
                    if (bitmaps.get(message._id) == null) {
                        bitmaps.put(message._id, Util.decodeBase64(message.content));
                    }
                    imgDraw.setImageBitmap(bitmaps.get(message._id));

                    if (currentUser._id.equals(message.sender)){
                        LinearLayout layoutContainer = (LinearLayout) rootView.findViewById(R.id.layoutContainer);
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutContainer.getLayoutParams();
                        params.gravity = Gravity.RIGHT;
                        layoutContainer.setLayoutParams(params);
                        prSent.setVisibility(message.status == Message.SENT? View.GONE : View.VISIBLE);
                    }
                    else{
                        prSent.setVisibility(View.GONE);
                    }
                    break;

                default:
                    break;
            }

            return rootView;
        }
    }

}