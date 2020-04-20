package com.yongxiang.timerulers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import timerulers.yongxiang.com.timerulerslib.views.FixedTimebarView;
import timerulers.yongxiang.com.timerulerslib.views.RecordDataExistTimeSegment;
import timerulers.yongxiang.com.timerulerslib.views.TimebarView;

/**
 * 固定标尺，两头拖动1.5分钟. 90s.
 */
public class FixActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = FixActivity.class.getSimpleName();
    private TextView currentTimeTextView;
    private Button zoomInButton, zoomOutButton;
    private FixedTimebarView mTimebarView;

    private Button mDayBt;
    private Button mHourBt;
    private Button mMinuteBt;
    private Button mMinuteBt3; //限定三分钟内范围可选.
    private TextView mStartTv,mEndTv,mCurrentTv;

    private int recordDays = 7;
    private long currentRealDateTime = System.currentTimeMillis();
    private Calendar calendar;

    private static long ONE_MINUTE_IN_MS = 60 * 1000;//一分钟
    private static long ONE_HOUR_IN_MS = 60 * ONE_MINUTE_IN_MS; //一小时
    private static long ONE_DAY_IN_MS = 24 * ONE_HOUR_IN_MS;    //一天

    private SimpleDateFormat zeroTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat todayTimeFormat = new SimpleDateFormat("HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixed);

        mTimebarView = (FixedTimebarView) findViewById(R.id.my_timebar_view);
        currentTimeTextView = (TextView) findViewById(R.id.current_time_tv);
        zoomInButton = (Button) findViewById(R.id.timebar_zoom_in_btn);
        zoomOutButton = (Button) findViewById(R.id.timebar_zoom_out_btn);
        mDayBt = (Button) findViewById(R.id.day);
        mHourBt = (Button) findViewById(R.id.hour);
        mMinuteBt = (Button) findViewById(R.id.minute);
        mMinuteBt3 = (Button) findViewById(R.id.minute3);
        mStartTv = (TextView) findViewById(R.id.tv_start_time);
        mCurrentTv = (TextView) findViewById(R.id.tv_current_time);
        mEndTv = (TextView) findViewById(R.id.tv_end_time);

        zoomInButton.setOnClickListener(this);
        zoomOutButton.setOnClickListener(this);
        mDayBt.setOnClickListener(this);
        mHourBt.setOnClickListener(this);
        mMinuteBt.setOnClickListener(this);
        mMinuteBt3.setOnClickListener(this);

        calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.SECOND,-90);
        //long timebarLeftEndPointTime = currentRealDateTime - 7 * 24 * 3600 * 1000;
        //当前时间向前1.5分钟.
        long timebarLeftEndPointTime = calendar.getTimeInMillis();

        System.out.println("calendar:" + calendar.getTime() + "  currentRealDateTime:" + currentRealDateTime);
        calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND,90);
        //一天的结束时间（第二天的开始时间.）
        //当前时间向后90s.
        long timebarRightEndPointTime = calendar.getTimeInMillis();
        //long timebarRightEndPointTime = currentRealDateTime + 3 * 3600 * 1000;

        mTimebarView.initTimebarLengthAndPosition(timebarLeftEndPointTime,
                timebarRightEndPointTime - 1000, currentRealDateTime);

        final List<RecordDataExistTimeSegment> recordDataList = new ArrayList<>();
        //记录当前前后1.5分钟的时间.
        RecordDataExistTimeSegment currentSegment = new RecordDataExistTimeSegment(
                currentRealDateTime - ONE_MINUTE_IN_MS/4,//前15s
                currentRealDateTime + ONE_MINUTE_IN_MS/12//后5s
                );
        recordDataList.add(currentSegment);

        mTimebarView.setRecordDataExistTimeClipsList(recordDataList);

        mTimebarView.setOnBarMoveListener(new FixedTimebarView.OnBarMoveListener() {
            @Override
            public void onBarMove(long screenLeftTime, long screenRightTime, long currentTime) {
                if (currentTime == -1) {
                    Toast.makeText(FixActivity.this, "当前时刻没有录像", Toast.LENGTH_SHORT).show();
                }
                currentTimeTextView.setText(zeroTimeFormat.format(currentTime));
                mStartTv.setText(todayTimeFormat.format(screenLeftTime));
                mCurrentTv.setText(todayTimeFormat.format(currentTime));
                mEndTv.setText(todayTimeFormat.format(screenRightTime));
            }

            @Override
            public void OnBarMoveFinish(long screenLeftTime, long screenRightTime, long currentTime) {
                currentTimeTextView.setText(zeroTimeFormat.format(currentTime));
                mStartTv.setText(todayTimeFormat.format(screenLeftTime));
                mCurrentTv.setText(todayTimeFormat.format(currentTime));
                mEndTv.setText(todayTimeFormat.format(screenRightTime));
            }
        });

//        mTimebarView.setOnBarScaledListener(new FixedTimebarView.OnBarScaledListener() {
//            @Override
//            public void onOnBarScaledMode(int mode) {
//                Log.d(TAG, "onOnBarScaledMode()" + mode);
//            }
//
//            @Override
//            public void onBarScaled(long screenLeftTime, long screenRightTime, long currentTime) {
//                currentTimeTextView.setText(zeroTimeFormat.format(currentTime));
//                mStartTv.setText(todayTimeFormat.format(screenLeftTime));
//                mCurrentTv.setText(todayTimeFormat.format(currentTime));
//                mEndTv.setText(todayTimeFormat.format(screenRightTime));
//                Log.d(TAG, "onBarScaled()");
//            }
//
//            @Override
//            public void onBarScaleFinish(long screenLeftTime, long screenRightTime, long currentTime) {
//                Log.d(TAG, "onBarScaleFinish()");
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timebar_zoom_in_btn:
//                mTimebarView.scaleByPressingButton(true);
                break;
            case R.id.timebar_zoom_out_btn:
//                mTimebarView.scaleByPressingButton(false);
                break;
            case R.id.day:
                mTimebarView.setMode(TimebarView.STYLE_STEP_DAY);
                break;
            case R.id.hour:
                mTimebarView.setMode(TimebarView.STYLE_STEP_HOUR);
                break;
            case R.id.minute:
                mTimebarView.setMode(TimebarView.STYLE_STEP_MINUTE);
                break;
            case R.id.minute3://三分钟
                mTimebarView.setMode(TimebarView.STYLE_STEP_MINUTE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimebarView.recycle();
    }
}
