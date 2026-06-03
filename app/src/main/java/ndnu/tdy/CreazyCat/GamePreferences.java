package ndnu.tdy.CreazyCat;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 游戏数据持久化：最佳成绩、设置项。
 */
public class GamePreferences {

    private static final String PREFS_NAME = "crazycat_prefs";
    private static final String KEY_BEST_STEPS_PREFIX = "best_steps_mode_";
    private static final String KEY_GUIDE_SHOWN = "guide_shown";
    private static final String KEY_ANIM_SPEED = "anim_speed";
    private static final String KEY_COUNTDOWN_SECONDS = "countdown_seconds";
    private static final String KEY_THEME_MODE = "theme_mode";

    private final SharedPreferences prefs;

    public GamePreferences(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // ==================== 最佳成绩 ====================

    /**
     * 获取指定模式的最佳步数。返回 -1 表示未通关。
     */
    public int getBestSteps(int mode) {
        return prefs.getInt(KEY_BEST_STEPS_PREFIX + mode, -1);
    }

    /**
     * 更新最佳步数（仅当新成绩更好时保存）。
     */
    public void updateBestSteps(int mode, int steps) {
        int current = getBestSteps(mode);
        if (current == -1 || steps < current) {
            prefs.edit().putInt(KEY_BEST_STEPS_PREFIX + mode, steps).apply();
        }
    }

    // ==================== 新手引导 ====================

    public boolean isGuideShown() {
        return prefs.getBoolean(KEY_GUIDE_SHOWN, false);
    }

    public void setGuideShown() {
        prefs.edit().putBoolean(KEY_GUIDE_SHOWN, true).apply();
    }

    // ==================== 设置项 ====================

    /**
     * 动画速度：0=慢(80ms), 1=正常(40ms), 2=快(20ms)
     */
    public int getAnimSpeed() {
        return prefs.getInt(KEY_ANIM_SPEED, 1);
    }

    public void setAnimSpeed(int speed) {
        prefs.edit().putInt(KEY_ANIM_SPEED, speed).apply();
    }

    public int getAnimDelayMs() {
        switch (getAnimSpeed()) {
            case 0: return 80;
            case 2: return 20;
            default: return 40;
        }
    }

    /**
     * 限时模式倒计时秒数：10, 20, 30
     */
    public int getCountdownSeconds() {
        return prefs.getInt(KEY_COUNTDOWN_SECONDS, 10);
    }

    public void setCountdownSeconds(int seconds) {
        prefs.edit().putInt(KEY_COUNTDOWN_SECONDS, seconds).apply();
    }

    /**
     * 主题模式：0=跟随系统, 1=浅色, 2=深色
     */
    public int getThemeMode() {
        return prefs.getInt(KEY_THEME_MODE, 0);
    }

    public void setThemeMode(int mode) {
        prefs.edit().putInt(KEY_THEME_MODE, mode).apply();
    }
}
