package ndnu.tdy.CreazyCat.Activity;

import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity 基类，提供全屏设置。
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * 全屏设置，隐藏状态栏。
     */
    protected void setFullScreen() {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
