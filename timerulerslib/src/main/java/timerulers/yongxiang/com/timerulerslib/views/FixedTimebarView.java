package timerulers.yongxiang.com.timerulerslib.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timerulers.yongxiang.com.timerulerslib.R;

/**
 * 固定录播长度的时间选择视图
 * @author poe 2020/04/16.
 */
public class FixedTimebarView extends View {
    private static final String TAG = "FixedTimebarView";

    private static long ONE_MINUTE_IN_MS = 60 * 1000;//一分钟
    /**
     * 每秒钟占用的像素.-0.0125 = 0-1080/86400 .
     */
    private float pixelsPerSecond = 0;

    private OnBarMoveListener mOnBarMoveListener;

    private int screenWidth, screenHeight;

    private int linesColor = Color.BLACK;

    private int recordBackgroundColor = Color.argb(200, 255, 221, 178);

    private int textColor = Color.BLACK;
    /**
     * 当前时间的颜色.
     */
    private int middleCursorColor = Color.argb(200, 255, 164, 159);

    private int sideCursorColor = Color.argb(255, 254, 68, 57);

    private Paint timebarPaint = new Paint();

    private TextPaint keyTickTextPaint = new TextPaint();
    /**
     * 视图高度单位：dp
     */
    private int VIEW_HEIGHT_IN_DP = 150;
    /**
     * 时间文字size default:12sp.
     */
    private final int KEY_TICK_TEXT_SIZE_IN_SP = 12;
    /**
     * tick size big :15dp
     */
    private final int BIG_TICK_HEIGHT_IN_DP = 15;
    /**
     * tick size small : 12dp
     */
    private final int SMALL_TICK_HEIGHT_IN_DP = 12;
    /**
     * big tick width half : 2dp
     */
    private final int BIG_TICK_HALF_WIDTH_IN_DP = 2;

    /**
     * 未选中小圆圈.
     */
    private final int SMALL_CIRCLE_TICK_RADIUS_IN_DP = 5;
    /**
     * 选中的大圆圈.
     */
    private final int BIG_CIRCLE_TICK_RADIUS_IN_DP = 10;

    /**
     * small tick width half :1dp .
     */
    private final int SMALL_TICK_HALF_WIDTH_IN_DP = 1;

    private final int BIG_TICK_HALF_WIDTH = DeviceUtil.dip2px(BIG_TICK_HALF_WIDTH_IN_DP);
    /**
     * 三角形图标长度.
     */
    private final int TRIANGLE_LENGTH = BIG_TICK_HALF_WIDTH * 4;
    /**
     * 刻度高度：高
     */
    private final int BIG_TICK_HEIGHT = DeviceUtil.dip2px(BIG_TICK_HEIGHT_IN_DP);
    /**
     * 小刻度宽度
     */
    private final int SMALL_TICK_HALF_WIDTH = DeviceUtil.dip2px(SMALL_TICK_HALF_WIDTH_IN_DP);
    /**
     * 小刻度高度.
     */
    private final int SMALL_TICK_HEIGHT = DeviceUtil.dip2px(SMALL_TICK_HEIGHT_IN_DP);
    /**
     * 刻度数字尺寸.
     */
    private final int KEY_TICK_TEXT_SIZE = DeviceUtil.dip2px(KEY_TICK_TEXT_SIZE_IN_SP);
    /**
     * 视图高度.
     */
    private int VIEW_HEIGHT;

    /**
     * 常规圆形
     */
    private int CIRCLE_RADIUS_NORMAL;

    /**
     * 选中的圆形
     */
    private int CIRCLE_RADIUS_PRESS;

    /**
     * 圆形线条宽度.default:2dp .
     */
    private int CIRCLE_WIDTH;

    /**
     * 左侧ticket是否选中.
     */
    private boolean isLeftPress = false;

    /**
     * 右侧ticket圆圈是否选中.
     */
    private boolean isRightPress = false;
    /**
     * 中间的时间线条是否显示.
     */
    private boolean middleCursorVisible = true;

    private Map<Integer, TimebarTickCriterion> timebarTickCriterionMap = new HashMap<>();
    /**
     * 时间刻度最大个数. 比如：5个小时，每小时6个刻度.
     */
    private int timebarTickCriterionCount = 5;
    /**
     * 当前时间所在的刻度.
     */
    private int currentTimebarTickCriterionIndex = 4;

    /**
     * 有效录播的范围.
     */
    private List<RecordDataExistTimeSegment> recordDataExistTimeClipsList = new ArrayList<>();

    /**
     * 时间点：录制的视频集合.
     */
    private Map<Long, List<RecordDataExistTimeSegment>> recordDataExistTimeClipsListMap = new HashMap<>();

    /**
     * 当前时间点单位ms UTC时间 .
     */
    private long currentTimeInMillisecond;
    /**
     * 左侧第一个刻度的UTC时间.
     */
    private long mostLeftTimeInMillisecond;
    /**
     * 最右侧的时间刻度UTC时间.
     */
    private long mostRightTimeInMillisecond;

//    private long screenLeftTimeInMillisecond;
//
//    private long screenRightTimeInMillisecond;


    public final static int SECONDS_PER_DAY = 24 * 60 * 60;

    /**
     * 整个视图占用的秒数总和.
     */
    private long WHOLE_TIMEBAR_TOTAL_SECONDS;


    private Calendar calendar;


    private SimpleDateFormat zeroTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /*
     * 设置最大最小缩放级别
     *  0:精度为秒
     *  1:精度为一分钟
     *  2：精度为6分钟
     *  3:精度为30分钟
     *  4:精度为2小时
     */
    private int ZOOMMAX = 3;
    private int ZOOMMIN = 1;

    private static final int MOVEING = 0x001;
    private static final int ACTION_UP = MOVEING + 1;
    /**
     * 缓存画布.
     */
    private Canvas mCanvas;

    public FixedTimebarView(Context context) {
        super(context);
        init(null, 0);

    }

    public FixedTimebarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public FixedTimebarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }


    public List<RecordDataExistTimeSegment> getRecordDataExistTimeClipsList() {
        return recordDataExistTimeClipsList;
    }

    /**
     * 设置录播范围.
     *
     * @param recordDataExistTimeClipsList
     */
    public void setRecordDataExistTimeClipsList(List<RecordDataExistTimeSegment> recordDataExistTimeClipsList) {
        this.recordDataExistTimeClipsList = recordDataExistTimeClipsList;
        arrangeRecordDataExistTimeClipsIntoMap(recordDataExistTimeClipsList);
    }

    public void setMostLeftTimeInMillisecond(long mostLeftTimeInMillisecond) {
        this.mostLeftTimeInMillisecond = mostLeftTimeInMillisecond;
    }

    public void setMostRightTimeInMillisecond(long mostRightTimeInMillisecond) {
        this.mostRightTimeInMillisecond = mostRightTimeInMillisecond;
    }

    public long getMostLeftTimeInMillisecond() {
        return mostLeftTimeInMillisecond;
    }

    public long getMostRightTimeInMillisecond() {
        return mostRightTimeInMillisecond;
    }

    /**
     * 缓存当前绘制的数据 .
     *
     * @param clipsList
     */
    private void arrangeRecordDataExistTimeClipsIntoMap(List<RecordDataExistTimeSegment> clipsList) {
        recordDataExistTimeClipsListMap = new HashMap<>();

        if (clipsList != null) {
            for (RecordDataExistTimeSegment clipItem : clipsList) {
                for (Long dateZeroOClockItem : clipItem.getCoverDateZeroOClockList()) {
                    List<RecordDataExistTimeSegment> list = null;
                    if ((list = recordDataExistTimeClipsListMap.get(dateZeroOClockItem)) == null) {
                        list = new ArrayList<>();
                        recordDataExistTimeClipsListMap.put(dateZeroOClockItem, list);
                    }
                    list.add(clipItem);
                }
            }
        }
        postInvalidate();
    }

    /**
     * 初始化时间刻度相关东西.
     *
     * @param mostLeftTime  开始时间
     * @param mostRightTime 结束时间
     * @param currentTime   当前时间.
     */
    public void initTimebarLengthAndPosition(long mostLeftTime, long mostRightTime, long currentTime) {
        this.mostLeftTimeInMillisecond = mostLeftTime;
        this.mostRightTimeInMillisecond = mostRightTime;
        this.currentTimeInMillisecond = currentTime;
        WHOLE_TIMEBAR_TOTAL_SECONDS = (mostRightTime - mostLeftTime) / 1000;
        Log.i(TAG, "initTimebarLengthAndPosition#currentTimeInMillisecond~" + currentTime +
                "startTime: " + mostLeftTime +
                "endTime: " + mostRightTime);
        //更新刻度标准
        initTimebarTickCriterionMap();
        //默认选择3.
        resetToStandardWidth();
    }

    public int getCurrentTimebarTickCriterionIndex() {
        return currentTimebarTickCriterionIndex;
    }

    //设置当前红线所处index. default:4/5 .
    public void setCurrentTimebarTickCriterionIndex(int currentTimebarTickCriterionIndex) {
//        Log.i(TAG,"setCurrentTimebarTickCriterionIndex#"+currentTimebarTickCriterionIndex);
        this.currentTimebarTickCriterionIndex = currentTimebarTickCriterionIndex;
    }

    private void init(AttributeSet attrs, int defStyleAttr) {

        if(!isInEditMode()){
            TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.TimebarView, defStyleAttr, 0);
            int n = a.getIndexCount();

            for (int i = 0; i < n; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.TimebarView_middleCursorColor) {
                    middleCursorColor = a.getColor(attr, Color.RED);

                } else if (attr == R.styleable.TimebarView_recordBackgroundColor) {// 默认颜色设置为橘黄色
                    recordBackgroundColor = a.getColor(attr, Color.argb(200, 251, 180, 76));

                } else if (attr == R.styleable.TimebarView_recordTextColor) {// 默认颜色设置为黑色
                    textColor = a.getColor(attr, Color.BLACK);

                } else if (attr == R.styleable.TimebarView_timebarColor) {// 默认颜色设置为黑色
                    linesColor = a.getColor(attr, Color.BLACK);

                }

            }
            a.recycle();
        }

        screenWidth = DeviceUtil.getScreenResolution(getContext())[0];
        screenHeight = DeviceUtil.getScreenResolution(getContext())[1];
        // DO: 2020/4/16 初始化圆形相关数据.
        CIRCLE_RADIUS_NORMAL = DeviceUtil.dip2px(SMALL_CIRCLE_TICK_RADIUS_IN_DP);
        CIRCLE_RADIUS_PRESS = DeviceUtil.dip2px(BIG_CIRCLE_TICK_RADIUS_IN_DP);
        CIRCLE_WIDTH = DeviceUtil.dip2px(BIG_TICK_HALF_WIDTH_IN_DP);

        currentTimeInMillisecond = System.currentTimeMillis();
        Log.i(TAG, "init#currentTimeInMillisecond~ ");
        //向前90s.
        calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, -90);
        mostLeftTimeInMillisecond = calendar.getTimeInMillis();
        //向后90s.
        calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 90);
        mostRightTimeInMillisecond = calendar.getTimeInMillis();

        //计算一屏占用的秒数.
        WHOLE_TIMEBAR_TOTAL_SECONDS = (mostRightTimeInMillisecond - mostLeftTimeInMillisecond) / 1000;

        pixelsPerSecond = (float) (getWidth() - screenWidth) / (float) WHOLE_TIMEBAR_TOTAL_SECONDS;
        Log.i(TAG, "init# pixelsPerSecond:" + pixelsPerSecond);
        //初始化刻度表.
        initTimebarTickCriterionMap();
        setCurrentTimebarTickCriterionIndex(currentTimebarTickCriterionIndex);
        //resetToStandardWidth();
        keyTickTextPaint.setAntiAlias(true);
        keyTickTextPaint.setTextSize(KEY_TICK_TEXT_SIZE);
        keyTickTextPaint.setColor(textColor);

        if(isInEditMode()){
            List<RecordDataExistTimeSegment> recordDataList = new ArrayList<>();
            //记录当前前后1.5分钟的时间.
            RecordDataExistTimeSegment currentSegment = new RecordDataExistTimeSegment(
                    currentTimeInMillisecond - ONE_MINUTE_IN_MS/4,//前15s
                    currentTimeInMillisecond + ONE_MINUTE_IN_MS/12//后5s
            );
            recordDataList.add(currentSegment);
            setRecordDataExistTimeClipsList(recordDataList);
        }
    }

    public void setMode(int scalMode) {
//        Log.i(TAG," setMode(int scalMode)#"+scalMode);
        if (scalMode < ZOOMMIN || scalMode > ZOOMMAX || scalMode == currentTimebarTickCriterionIndex)
            return;

        switch (scalMode) {
            case 0:
                setCurrentTimebarTickCriterionIndex(0);
                int newWidth = timebarTickCriterionMap.get(0).getViewLength();
//                justScaledByPressingButton = true;
                ViewGroup.LayoutParams params = getLayoutParams();
                params.width = newWidth;
                setLayoutParams(params);
                break;
            case 1:
                setCurrentTimebarTickCriterionIndex(1);
                int newWidth1 = timebarTickCriterionMap.get(1).getViewLength();
//                justScaledByPressingButton = true;
                ViewGroup.LayoutParams params1 = getLayoutParams();
                params1.width = newWidth1;
                setLayoutParams(params1);
                break;
            case 2:
                setCurrentTimebarTickCriterionIndex(2);
                int newWidth2 = timebarTickCriterionMap.get(2).getViewLength();
//                justScaledByPressingButton = true;
                ViewGroup.LayoutParams params2 = getLayoutParams();
                params2.width = newWidth2;
                setLayoutParams(params2);
                break;
            case 3:
//                Log.i(TAG," setMode(int scalMode)#setCurrentTimebarTickCriterionIndex(3)"+scalMode);
                setCurrentTimebarTickCriterionIndex(3);
                int newWidth3 = timebarTickCriterionMap.get(3).getViewLength();
//                justScaledByPressingButton = true;
                ViewGroup.LayoutParams params3 = getLayoutParams();
                params3.width = newWidth3;
                setLayoutParams(params3);
                break;
            case 4:
                setCurrentTimebarTickCriterionIndex(4);
                int newWidth4 = timebarTickCriterionMap.get(4).getViewLength();
                ViewGroup.LayoutParams params4 = getLayoutParams();
                params4.width = newWidth4;
                setLayoutParams(params4);
                break;
        }
    }

    private float getAverageWidthForTwoCriterion(int criterion1Index, int criterion2Index) {
        int width1 = timebarTickCriterionMap.get(criterion1Index).getViewLength();
        int width2 = timebarTickCriterionMap.get(criterion2Index).getViewLength();
        return (width1 + width2) / 2;
    }

    /**
     * 初始化尺子刻度hashmap。
     */
    private void initTimebarTickCriterionMap() {
        //默认：十分钟.
        TimebarTickCriterion t0 = new TimebarTickCriterion();
        //总秒数.
        t0.setTotalSecondsInOneScreen(3 * 60);
        //每个小刻度多少秒.
        t0.setKeyTickInSecond(1 * 60);
        t0.setMinTickInSecond(60);
        t0.setDataPattern("mm:ss");
        t0.setViewLength((int) ((float) screenWidth * WHOLE_TIMEBAR_TOTAL_SECONDS / (float) t0.getTotalSecondsInOneScreen()));
        timebarTickCriterionMap.put(0, t0);

        //分钟，6分钟.
        TimebarTickCriterion t1 = new TimebarTickCriterion();
        t1.setTotalSecondsInOneScreen(6 * 60);
        t1.setKeyTickInSecond(60);
        t1.setMinTickInSecond(6);
        t1.setDataPattern("HH:mm");
        t1.setViewLength((int) ((float) screenWidth * WHOLE_TIMEBAR_TOTAL_SECONDS / (float) t1.getTotalSecondsInOneScreen()));
        timebarTickCriterionMap.put(1, t1);

        //小时一小时=60*60 s
        TimebarTickCriterion t2 = new TimebarTickCriterion();
        t2.setTotalSecondsInOneScreen(1 * 60 * 60);
        t2.setKeyTickInSecond(10 * 60);
        t2.setMinTickInSecond(1 * 60);
        t2.setDataPattern("HH:mm");
        t2.setViewLength((int) ((float) screenWidth * WHOLE_TIMEBAR_TOTAL_SECONDS / (float) t2.getTotalSecondsInOneScreen()));
        timebarTickCriterionMap.put(2, t2);

        //默认三分钟的实现.
        TimebarTickCriterion t3 = new TimebarTickCriterion();
        t3.setTotalSecondsInOneScreen(3 * 60);
        //每个小刻度多少秒.
        t3.setKeyTickInSecond(1 * 60);
        //每格子代表多少秒.
        t3.setMinTickInSecond(18);
        t3.setDataPattern("HH:mm:ss");
        t3.setViewLength((int) ((float) screenWidth * WHOLE_TIMEBAR_TOTAL_SECONDS / (float) t3.getTotalSecondsInOneScreen()));
        timebarTickCriterionMap.put(3, t3);
        //一周.
        TimebarTickCriterion t4 = new TimebarTickCriterion();
        t4.setTotalSecondsInOneScreen(6 * 24 * 60 * 60);
        t4.setKeyTickInSecond(24 * 60 * 60);
        t4.setMinTickInSecond(2 * 60 * 60);
        t4.setDataPattern("MM.dd");
        // t4.dataPattern = "MM.dd HH:mm:ss";
        t4.setViewLength((int) ((float) screenWidth * WHOLE_TIMEBAR_TOTAL_SECONDS / (float) t4.getTotalSecondsInOneScreen()));
        timebarTickCriterionMap.put(4, t4);
        timebarTickCriterionCount = timebarTickCriterionMap.size();
    }

    /**
     * 重置到标准宽度.3.
     */
    private void resetToStandardWidth() {
        setCurrentTimebarTickCriterionIndex(3);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getViewLength();
        setLayoutParams(params);
    }


    public long getCurrentTimeInMillisecond() {
        return currentTimeInMillisecond;
    }

    public void setCurrentTimeInMillisecond(long currentTimeInMillisecond) {
        this.currentTimeInMillisecond = currentTimeInMillisecond;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            VIEW_HEIGHT = DeviceUtil.dip2px(VIEW_HEIGHT_IN_DP);
        } else {
            VIEW_HEIGHT = heightSize;
        }
        //布局宽高resize.
        setMeasuredDimension(measureWidth(widthMeasureSpec), VIEW_HEIGHT);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        Log.d("onSizeChanged", " w:" + w + " h:" + h + " oldw:" + oldh + " w:" + oldh);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * 手动增加一个扩展屏幕的长度 .
     *
     * @param widthMeasureSpec
     * @return
     */
    private int measureWidth(int widthMeasureSpec) {
        int measureMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureSize = MeasureSpec.getSize(widthMeasureSpec);
        int result = getSuggestedMinimumWidth();
        switch (measureMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = measureSize;//+ screenWidth
                pixelsPerSecond = measureSize / (float) WHOLE_TIMEBAR_TOTAL_SECONDS;
                Log.i(TAG, " measureWidth#pixelsPerSecond:" + pixelsPerSecond);
                break;
            default:
                break;
        }
//        Log.d("measureWidth", "measureMode:" + measureMode + "measureSize:" + measureSize + " result" + result);
        return result;
    }

    private String getTimeStringFromLong(long value) {
        SimpleDateFormat timeFormat = new SimpleDateFormat(timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getDataPattern());
        return timeFormat.format(value);
    }

    public void setMiddleCursorVisible(boolean middleCursorVisible) {
        this.middleCursorVisible = middleCursorVisible;
        invalidate();
    }

    int lastMmiddlecursor = 0;
    long firstTickToSeeInSecondUTC = -1;
    int zoneOffsetInSeconds;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw");
        mCanvas = canvas;
        //计算一个屏幕单位信息 : px/s .
        pixelsPerSecond = (float) (getWidth()) / (float) WHOLE_TIMEBAR_TOTAL_SECONDS;// - screenWidth
        Log.i(TAG, " onDraw#pixelsPerSecond:" + pixelsPerSecond);

        Calendar cal = Calendar.getInstance();
        zoneOffsetInSeconds = cal.get(Calendar.ZONE_OFFSET) / 1000;//时区差值.

        Log.i(TAG, "onDraw#currentTimebarTickCriterionIndex: " + currentTimebarTickCriterionIndex
                + " currentTimeInMillisecond(/1000):" + currentTimeInMillisecond / 1000);
        Log.i(TAG, "zoneOffsetInSeconds: " + zoneOffsetInSeconds);
        //计算左侧开始坐标：  以当前时间点为中轴计算起始位置.
        long forStartUTC = (long) (currentTimeInMillisecond / 1000
                - screenWidth / pixelsPerSecond / 2 // 减去一半的屏幕.
                - timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond()//减去一格(2秒).
        );
        long forEndUTC = (long) (currentTimeInMillisecond / 1000
                + screenWidth / pixelsPerSecond / 2
                + timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond()
        );

        //开始时间.
        long forStartLocalTimezone = forStartUTC + zoneOffsetInSeconds;
        //结束时间.
        long forEndLocalTimezone = forEndUTC + zoneOffsetInSeconds;

        Log.i(TAG, "forStartUTC:" + forStartLocalTimezone + " ~ " + forEndLocalTimezone);
        //轮询设置第一个刻度时间(UTC.).每秒查询.
        for (long i = forStartLocalTimezone; i <= forEndLocalTimezone; i++) {
            if (i % timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond() == 0) {
                firstTickToSeeInSecondUTC = i - zoneOffsetInSeconds;
                Log.i(TAG, "found the firstTickToSeeInSecondUTC: " + firstTickToSeeInSecondUTC);
                break;
            }
        }

        // 画刻度及时间
        drawTick(canvas);
        // 画录像条
        drawRecord(canvas);
        // 画中间刻度
        drawmiddleCursor(canvas);
        //DO: 2020/4/16 画两侧的时间选择竖线.
        drawLeftRightCursor(canvas);
        //画 三个时间 开始、当前、结束.
        drawTimeText(canvas);

        //重新布局
        layout(0,
                getTop(),
                getWidth(),
                getTop() + getHeight());

        if (mOnBarMoveListener != null) {
            long startTime = recordDataExistTimeClipsList.get(0).getStartTimeInMillisecond();
            long endTime = recordDataExistTimeClipsList.get(0).getEndTimeInMillisecond();
            mOnBarMoveListener.onBarMove(startTime, endTime, currentTimeInMillisecond);
        }
    }

    /**
     * 写开始时间，结束时间，当前时间.
     *
     * @param canvas
     */
    private void drawTimeText(Canvas canvas) {
        float keytextY = VIEW_HEIGHT/6;

        long startTime = recordDataExistTimeClipsList.get(0).getStartTimeInMillisecond();
        long endTime = recordDataExistTimeClipsList.get(0).getEndTimeInMillisecond();

        //画时间文字
        //1. start .
        float startX = pixelsPerSecond * (startTime - mostLeftTimeInMillisecond) / 1000;// + screenWidth / 2f;
        String keytext = getTimeStringFromLong(startTime);
        float keyTextWidth = keyTickTextPaint.measureText(keytext);
        float keytextX = startX - keyTextWidth / 2;
        //和中线保持一个字宽.
        if ((screenWidth / 2 - keytextX) < keyTextWidth * 2) {
            keytextX = screenWidth / 2 - keyTextWidth * 2;
        }
        //到左侧0停止.
        if (keytextX < 0) keytextX = 0;

        canvas.drawText(keytext, keytextX, keytextY, keyTickTextPaint);
        //2. end .
        startX = pixelsPerSecond * (endTime - mostLeftTimeInMillisecond) / 1000;// + screenWidth / 2f;
        keytext = getTimeStringFromLong(endTime);
        keyTextWidth = keyTickTextPaint.measureText(keytext);
        keytextX = startX - keyTextWidth / 2;
        if ((keytextX - screenWidth / 2) < keyTextWidth) {
            keytextX = screenWidth / 2 + keyTextWidth;
        }
        //到右侧停止.
        if (screenWidth - keytextX < keyTextWidth) {
            keytextX = screenWidth - keyTextWidth;
        }
        canvas.drawText(keytext, keytextX, keytextY, keyTickTextPaint);
        //3. currentTime.
        startX = pixelsPerSecond * (currentTimeInMillisecond - mostLeftTimeInMillisecond) / 1000;// + screenWidth / 2f;
        keytext = getTimeStringFromLong(currentTimeInMillisecond);
        keyTextWidth = keyTickTextPaint.measureText(keytext);
        keytextX = startX - keyTextWidth / 2;
        canvas.drawText(keytext, keytextX, keytextY, keyTickTextPaint);
    }

    /**
     * draw rule numbs..
     * @param canvas
     */
    private void drawTick(Canvas canvas) {
        //计算一个屏幕绘制多少个刻度.
        int totalTickToDrawInOneScreen = (int) (screenWidth / pixelsPerSecond / timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond()) + 2;
        Log.i(TAG, "drawTick#totalTickToDrawInOneScreen~ " + totalTickToDrawInOneScreen);
        //左侧不可见20个刻度，右侧不可见10个刻度.
        for (int i = -20; i <= totalTickToDrawInOneScreen + 10; i++) {
            //first rule num time minux -20x.
            long drawTickTimeInSecondUTC = firstTickToSeeInSecondUTC + i * timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond();
            //first num x .
            float startX = pixelsPerSecond * (drawTickTimeInSecondUTC - mostLeftTimeInMillisecond / 1000);// + screenWidth / 2f;
            Log.i(TAG, "mostLeftTimeInMillisecond/1000~" + mostLeftTimeInMillisecond / 1000);
            //小刻度
            timebarPaint.setColor(linesColor);
            timebarPaint.setAntiAlias(true);
            timebarPaint.setStyle(Paint.Style.FILL);

            Log.i(TAG, "小刻度:" + i + " startX:" + startX);
            RectF smallTickRect = new RectF(startX - SMALL_TICK_HALF_WIDTH / 2, VIEW_HEIGHT - SMALL_TICK_HEIGHT, (startX + SMALL_TICK_HALF_WIDTH / 2), VIEW_HEIGHT);
            canvas.drawRect(smallTickRect, timebarPaint);
        }


        //bottom line .
        canvas.drawLine(0, VIEW_HEIGHT, getWidth(), VIEW_HEIGHT, timebarPaint);
    }

    /**
     * 视频录制范围绘制.
     *
     * @param canvas
     */
    private void drawRecord(Canvas canvas) {
        //录像从哪个时间点开始，单位是毫秒
        long startDrawTimeInSeconds = firstTickToSeeInSecondUTC + (-20) * timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond();

        if (recordDataExistTimeClipsList != null && recordDataExistTimeClipsList.size() > 0) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String startDrawTimeDateString = dateFormat.format(startDrawTimeInSeconds * 1000);
            String zeroTimeString = startDrawTimeDateString + " 00:00:00";

            long screenLastSecondToSee = (long) (startDrawTimeInSeconds + 30 * timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond()) * 1000L;

            Date startDate;
            try {

                startDate = zeroTimeFormat.parse(zeroTimeString);
                List<RecordDataExistTimeSegment> startList = recordDataExistTimeClipsListMap.get(startDate.getTime());
                if (startList == null) {
                    int afterFindDays = 1;
                    long findTimeInMilliseconds = startDate.getTime();
                    long newFindStartMilliseconds = findTimeInMilliseconds;
                    while (startList == null && newFindStartMilliseconds < screenLastSecondToSee) {
                        newFindStartMilliseconds = findTimeInMilliseconds + (long) SECONDS_PER_DAY * 1000L * (long) afterFindDays;
                        startList = recordDataExistTimeClipsListMap.get(newFindStartMilliseconds);
                        afterFindDays++;
                    }
                }

                if (startList != null && startList.size() > 0) {
                    int thisDateFirstClipStartIndex = recordDataExistTimeClipsList.indexOf(startList.get(0));

                    long endDrawTimeInSeconds = (long) (startDrawTimeInSeconds
                            + timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond() * 30);

                    timebarPaint.setColor(recordBackgroundColor);
                    timebarPaint.setStyle(Paint.Style.FILL);

                    for (int i = thisDateFirstClipStartIndex; i < recordDataExistTimeClipsList.size(); i++) {
                        float leftX = pixelsPerSecond * (recordDataExistTimeClipsList.get(i).getStartTimeInMillisecond() - mostLeftTimeInMillisecond) / 1000;//+ screenWidth / 2f;
                        float rightX = pixelsPerSecond * (recordDataExistTimeClipsList.get(i).getEndTimeInMillisecond() - mostLeftTimeInMillisecond) / 1000;//+ screenWidth / 2f;
                        //draw record video scope .
                        RectF rectF = new RectF(leftX, VIEW_HEIGHT * 1 / 4, rightX, VIEW_HEIGHT);
                        canvas.drawRect(rectF, timebarPaint);
                        if (recordDataExistTimeClipsList.get(i).getEndTimeInMillisecond() >= endDrawTimeInSeconds * 1000) {
                            break;
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * draw middle line with current time .
     *
     * @param canvas
     */
    private void drawmiddleCursor(Canvas canvas) {
        if (middleCursorVisible) {
            timebarPaint.setStyle(Paint.Style.FILL);
            timebarPaint.setColor(middleCursorColor);
            timebarPaint.setStrokeWidth(CIRCLE_WIDTH);
            int currentCursor = (int) ((currentTimeInMillisecond / 1000L - mostLeftTimeInMillisecond / 1000L) * pixelsPerSecond - TRIANGLE_LENGTH / 2);
            lastMmiddlecursor = currentCursor;
            canvas.drawLine(currentCursor + TRIANGLE_LENGTH / 2, VIEW_HEIGHT * 1 / 4, currentCursor + TRIANGLE_LENGTH / 2, VIEW_HEIGHT, timebarPaint);
        }
    }

    /**
     * 画左右两侧的竖线.
     *
     * @param canvas
     */
    private void drawLeftRightCursor(Canvas canvas) {
        //1.左侧竖线+空心圆
        timebarPaint.setStyle(Paint.Style.FILL);
        timebarPaint.setStrokeWidth(CIRCLE_WIDTH);
        timebarPaint.setColor(sideCursorColor);
        //录像从哪个时间点开始，单位是毫秒
        long startDrawTimeInSeconds = firstTickToSeeInSecondUTC + (-20) * timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond();

        if (recordDataExistTimeClipsList != null && recordDataExistTimeClipsList.size() > 0) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String startDrawTimeDateString = dateFormat.format(startDrawTimeInSeconds * 1000);
            String zeroTimeString = startDrawTimeDateString + " 00:00:00";

            long screenLastSecondToSee = (long) (startDrawTimeInSeconds  + 30 * timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond()) * 1000L;

            Date startDate;
            try {
                startDate = zeroTimeFormat.parse(zeroTimeString);
                List<RecordDataExistTimeSegment> startList = recordDataExistTimeClipsListMap.get(startDate.getTime());
                if (startList == null) {
                    int afterFindDays = 1;
                    long findTimeInMilliseconds = startDate.getTime();
                    long newFindStartMilliseconds = findTimeInMilliseconds;
                    while (startList == null && newFindStartMilliseconds < screenLastSecondToSee) {
                        newFindStartMilliseconds = findTimeInMilliseconds + (long) SECONDS_PER_DAY * 1000L * (long) afterFindDays;
                        startList = recordDataExistTimeClipsListMap.get(newFindStartMilliseconds);
                        afterFindDays++;
                    }
                }

                if (startList != null && startList.size() > 0) {
                    int thisDateFirstClipStartIndex = recordDataExistTimeClipsList.indexOf(startList.get(0));

                    long endDrawTimeInSeconds = (long) (startDrawTimeInSeconds
                            + timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond() * 30);

                    for (int i = thisDateFirstClipStartIndex; i < recordDataExistTimeClipsList.size(); i++) {
                        float leftX = pixelsPerSecond * (recordDataExistTimeClipsList.get(i).getStartTimeInMillisecond() - mostLeftTimeInMillisecond) / 1000;// + screenWidth / 2f;
                        float rightX = pixelsPerSecond * (recordDataExistTimeClipsList.get(i).getEndTimeInMillisecond() - mostLeftTimeInMillisecond) / 1000;// + screenWidth / 2f;
                        //记录坐标，拖动使用.
                        currentLeftTicketX = leftX;
                        currentRightTicketX = rightX;
                        //1. draw left line.
                        canvas.drawLine(leftX, VIEW_HEIGHT * 1 / 4, leftX, VIEW_HEIGHT, timebarPaint);
                        //2. draw right line .
                        canvas.drawLine(rightX, VIEW_HEIGHT * 1 / 4, rightX, VIEW_HEIGHT, timebarPaint);
                        //3. draw left circle .
                        int circleY = VIEW_HEIGHT * 5 / 8;
                        canvas.drawCircle(leftX, circleY, isLeftPress ? CIRCLE_RADIUS_PRESS : CIRCLE_RADIUS_NORMAL, timebarPaint);
                        timebarPaint.setColor(Color.WHITE);
                        //选中放大
                        canvas.drawCircle(leftX, circleY, (isLeftPress ? CIRCLE_RADIUS_PRESS : CIRCLE_RADIUS_NORMAL) - CIRCLE_WIDTH, timebarPaint);
                        //4. draw right circle .
                        timebarPaint.setColor(sideCursorColor);
                        canvas.drawCircle(rightX, circleY, isRightPress ? CIRCLE_RADIUS_PRESS : CIRCLE_RADIUS_NORMAL, timebarPaint);
                        timebarPaint.setColor(Color.WHITE);
                        //选中放大圆圈.
                        canvas.drawCircle(rightX, circleY, (isRightPress ? CIRCLE_RADIUS_PRESS : CIRCLE_RADIUS_NORMAL) - CIRCLE_WIDTH, timebarPaint);

                        if (recordDataExistTimeClipsList.get(i).getEndTimeInMillisecond() >= endDrawTimeInSeconds * 1000) {
                            break;
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 当前左侧边界ticket的x坐标.
     */
    float currentLeftTicketX;
    /**
     * 当前右侧边界ticket的x坐标.
     */
    float currentRightTicketX;
    float lastX, lastY;
    long lastcurrentTimeInMillisecond = 0;
    boolean lastCheckState;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                // 先记录进度条移动状态 如果进度条正在移动 先停止
                lastCheckState = checkVideo;
                checkVideo = readyCheck;
                lastcurrentTimeInMillisecond = currentTimeInMillisecond;
                lastX = event.getRawX();
                lastY = event.getRawY();
                refreshRecordScope(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                refreshRecordScope(event);
                break;
            case MotionEvent.ACTION_CANCEL:
                currentTimeInMillisecond = lastcurrentTimeInMillisecond;
                checkVideo = lastCheckState;
                if (mOnBarMoveListener != null) {
                    long startTime = recordDataExistTimeClipsList.get(0).getStartTimeInMillisecond();
                    long endTime = recordDataExistTimeClipsList.get(0).getEndTimeInMillisecond();
                    mOnBarMoveListener.onBarMove(startTime, endTime, currentTimeInMillisecond);
                }
                isLeftPress = false;
                isRightPress = false;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isLeftPress = false;
                isRightPress = false;
                invalidate();
                break;
        }


        return true;
    }

    /**
     * 刷新ticket的值.
     * 手动选择录制时间,当前向前<90s ,向后<90s .
     *
     * @param event
     */
    private void refreshRecordScope(MotionEvent event) {
        //左右各有1/2屏幕隐藏.
        int middleX = getWidth() / 2;
        int eventX = (int) event.getRawX();
        //5s内不可以调整.
        if (Math.abs(eventX - middleX) < 5 * pixelsPerSecond) return;

        if (eventX < middleX) {
            isLeftPress = true;
            isRightPress = false;
        } else if (eventX > middleX) {
            isRightPress = true;
            isLeftPress = false;
        }

        //过滤5s之内的数据.
        long startTime = recordDataExistTimeClipsList.get(0).getStartTimeInMillisecond();
        long endTime = recordDataExistTimeClipsList.get(0).getEndTimeInMillisecond();
        Log.i(TAG, "refreshRecordScope# start: " + startTime + " ~ " + " end: " + endTime);
        //1. 计算x的时间点
        int timeBarLength = getWidth();//- screenWidth;

        long newTime = mostLeftTimeInMillisecond + eventX * WHOLE_TIMEBAR_TOTAL_SECONDS * 1000 / timeBarLength;
        Log.i(TAG, " newTime: " + newTime);

        //2. 如果x不在视频记录范围内，对齐.
        RecordDataExistTimeSegment segment;
        if (eventX < middleX) {
            segment = new RecordDataExistTimeSegment(newTime, endTime);
        } else {
            segment = new RecordDataExistTimeSegment(startTime, newTime);
        }
        recordDataExistTimeClipsList.clear();
        recordDataExistTimeClipsList.add(segment);
        arrangeRecordDataExistTimeClipsIntoMap(recordDataExistTimeClipsList);

        if (mOnBarMoveListener != null) {
            startTime = recordDataExistTimeClipsList.get(0).getStartTimeInMillisecond();
            endTime = recordDataExistTimeClipsList.get(0).getEndTimeInMillisecond();
            mOnBarMoveListener.onBarMove(startTime, endTime, currentTimeInMillisecond);
        }

        // DO: 2020/4/20 局部刷新即可.
        if (null != mCanvas) {
            drawRecord(mCanvas);
            drawLeftRightCursor(mCanvas);
            drawTimeText(mCanvas);
        }
    }

    /**
     * 移动监听.
     */
    public interface OnBarMoveListener {

        void onBarMove(long screenLeftTime, long screenRightTime, long currentTime);

        void OnBarMoveFinish(long screenLeftTime, long screenRightTime, long currentTime);
    }

    public void setOnBarMoveListener(OnBarMoveListener onBarMoveListener) {
        mOnBarMoveListener = onBarMoveListener;
    }

    // 是否检查录像标志位
    private boolean checkVideo = false;
    private boolean readyCheck = false;

    /*
     *
     * 设置是否检查有录像
     *
     * */
    public void checkVideo(boolean check) {
        readyCheck = check;
    }

    /*
     * 返回下一个录像开始点
     * */
    private long locationVideo() {
        if (recordDataExistTimeClipsList == null)
            return -1;
        int size = recordDataExistTimeClipsList.size();
        for (int i = 0; i < size - 1; i++) {
            long lastEndTime = recordDataExistTimeClipsList.get(i).getEndTimeInMillisecond();
            long nextStartTime = recordDataExistTimeClipsList.get(i + 1).getStartTimeInMillisecond();
            if (currentTimeInMillisecond > lastEndTime && currentTimeInMillisecond < nextStartTime) {
                return nextStartTime;
            }
        }
        return -1;
    }

    /*判断是否有录像*/
    private boolean checkHasVideo() {
        if (recordDataExistTimeClipsList != null && recordDataExistTimeClipsList.size() > 0) {
            for (RecordDataExistTimeSegment recordInfo : recordDataExistTimeClipsList) {
                if (recordInfo.getStartTimeInMillisecond() <= currentTimeInMillisecond
                        && currentTimeInMillisecond <= recordInfo.getEndTimeInMillisecond())
                    return true;
            }
        }
        return false;
    }

    public void recycle() {
        if (recordDataExistTimeClipsList != null) {
            recordDataExistTimeClipsList.clear();
            recordDataExistTimeClipsList = null;
        }
        if (recordDataExistTimeClipsListMap != null) {
            recordDataExistTimeClipsListMap.clear();
            recordDataExistTimeClipsListMap = null;
        }
        mOnBarMoveListener = null;
//        mOnBarScaledListener = null;
        timebarPaint = null;
//        scaleGestureDetector = null;
        mCanvas = null;
    }
}

