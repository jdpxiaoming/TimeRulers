
package timerulers.yongxiang.com.timerulerslib.views;

/**
 * 尺子单位刻度.
 */
public class TimebarTickCriterion {
    /**
     * 一个刻度长度.(10个小刻度总长度.)
     */
    private int viewLength;
    /**
     * 一屏幕占用的总秒数.
     */
    private int totalSecondsInOneScreen;
    /**
     * 关键刻度 单位:秒.
     */
    private int keyTickInSecond;
    /**
     * 秒表示的最小刻度个数.
     */
    private int minTickInSecond;

    private String dataPattern;

    public int getViewLength() {
        return viewLength;
    }

    public void setViewLength(int viewLength) {
        this.viewLength = viewLength;
    }

    public int getTotalSecondsInOneScreen() {
        return totalSecondsInOneScreen;
    }

    public void setTotalSecondsInOneScreen(int totalSecondsInOneScreen) {
        this.totalSecondsInOneScreen = totalSecondsInOneScreen;
    }

    public int getKeyTickInSecond() {
        return keyTickInSecond;
    }

    public void setKeyTickInSecond(int keyTickInSecond) {
        this.keyTickInSecond = keyTickInSecond;
    }

    public int getMinTickInSecond() {
        return minTickInSecond;
    }

    public void setMinTickInSecond(int minTickInSecond) {
        this.minTickInSecond = minTickInSecond;
    }

    public String getDataPattern() {
        return dataPattern;
    }

    public void setDataPattern(String dataPattern) {
        this.dataPattern = dataPattern;
    }
}
