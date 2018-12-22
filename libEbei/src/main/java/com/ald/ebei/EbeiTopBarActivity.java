package com.ald.ebei;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.ald.ebei.activity.EbeiBaseActivity;
import com.ald.ebei.ui.EbeiAppBar;
import com.ald.ebei.util.EbeiActivityUtils;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/1/5
 * 描述：
 * 修订历史：
 */
public abstract class EbeiTopBarActivity extends EbeiBaseActivity {
    protected FrameLayout mainContent;
    protected ViewGroup rootView;
    // title bar
    protected EbeiAppBar mEbeiAppBar = null;

    /**
     * 设置除去topBar外的布局
     */
    protected abstract int getLayoutInflate();

    /**
     * 设置ViewModel
     */
    protected abstract void setViewModel();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_bar_activity);
        rootView = findViewById(R.id.root_view);
        mainContent = findViewById(R.id.main_content);
        int layoutId = getLayoutInflate();
        if (layoutId > 0) {
            View view= LayoutInflater.from(this).inflate(layoutId,mainContent,false);
            mainContent.removeAllViews();
            mainContent.addView(view);
            if (null == mEbeiAppBar) {
                mEbeiAppBar = findViewById(R.id.appbar);
            }
            mEbeiAppBar.setLeftParentSelector(getResources().getColor(R.color.fw_btn_white_color),
                    getResources().getColor(R.color.fw_btn_white_color));
            afterOnCreate();
            setViewModel();
        } else {
            throw new IllegalArgumentException("layout is not a inflate");
        }

    }

    /**
     * UI相关操作
     */
    protected void afterOnCreate() {

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public EbeiAppBar getAppTopBar() {
        return mEbeiAppBar;
    }

    /**
     * 为appbar设置标题 hao
     */
    @Override
    public void setTitle(CharSequence title) {
        if (null != mEbeiAppBar && !TextUtils.isEmpty(title)) {
            mEbeiAppBar.setTitle(title);
        }
    }

    public void setTitle(CharSequence title, @ColorInt int color) {
        setTitle(title);
        setTitleColor(color);
    }

    /**
     * 为appbar设置标题
     */
    @Override
    public void setTitle(@StringRes int resId) {
        if (null != mEbeiAppBar && resId != 0) {
            mEbeiAppBar.setTitle(getString(resId));
        }
    }

    public void setTitle(@StringRes int resId, @ColorInt int color) {
        setTitle(resId);
        setTitleColor(color);
    }

    /**
     * 为appbar设置标题的颜色
     */
    @Override
    public void setTitleColor(@ColorInt int color) {
        if (null != mEbeiAppBar && color != 0) {
            mEbeiAppBar.setTitleColor(color);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // left
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 设置是否显示back键
     */
    public void setBackOption(boolean option) {
        if (null != mEbeiAppBar) {
            mEbeiAppBar.setBackOption(option);
        }
    }

    /**
     * 设置左侧文字
     */
    public void setLeftText(CharSequence text, View.OnClickListener listener, @ColorInt int color) {
        if (null != mEbeiAppBar) {
            mEbeiAppBar.setLeftTextOption(text, listener);
            if (color != 0) {
                mEbeiAppBar.setLeftTextColor(color);
            }
        }
    }

    public void setLeftText(CharSequence text, View.OnClickListener listener) {
        setLeftText(text, listener, 0);
    }

    /**
     * 设置左侧文字
     */
    public void setLeftText(int resId, View.OnClickListener listener, @ColorInt int color) {
        if (null != mEbeiAppBar) {
            mEbeiAppBar.setLeftTextOption(getString(resId), listener);
            if (color != 0) {
                mEbeiAppBar.setLeftTextColor(color);
            }
        }
    }

    public void setLeftText(int resId, View.OnClickListener listener) {
        setLeftText(resId, listener, 0);
    }

    /**
     * 设置左侧返回健的图片和监听事件
     */
    public void setLeftImage(@DrawableRes int resId, View.OnClickListener listener) {
        mEbeiAppBar.setLeftIconOption(resId, listener);
    }

    ///////////////////////////////////////////////////////////////////////////
    // right
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 设置右侧文字
     */
    public void setRightText(CharSequence text, View.OnClickListener listener, @ColorInt int color) {
        if (null != mEbeiAppBar) {
            mEbeiAppBar.setRightTextOption(text, listener);
            if (color != 0) {
                mEbeiAppBar.setRightTextColor(color);
            }
        }
    }

    public void removeRightText() {
        if (null != mEbeiAppBar) {
            mEbeiAppBar.removeRightText();
        }
    }

    public void setRightText(CharSequence text, View.OnClickListener listener) {
        setRightText(text, listener, 0);
    }

    /**
     * 设置右侧文字
     */
    public void setRightText(int resId, View.OnClickListener listener, @ColorInt int color) {
        if (null != mEbeiAppBar) {
            mEbeiAppBar.setRightTextOption(getString(resId), listener);
            if (color != 0) {
                mEbeiAppBar.setRightTextColor(color);
            }
        }
    }

    public void setRightText(int resId, View.OnClickListener listener) {
        setRightText(resId, listener, 0);
    }

    public void setReightImage(int resId, View.OnClickListener listener) {
        if (null != mEbeiAppBar) {
            mEbeiAppBar.setRightIconOption(resId, listener);
        }
    }

    /**
     * 设置右侧文字加图片
     */
    public void setRightTextANDImg(int resId, int imgId, View.OnClickListener listener, @ColorInt int color) {
        if (null != mEbeiAppBar) {
            Drawable drawable = ContextCompat.getDrawable(EbeiActivityUtils.peek(), imgId);
            mEbeiAppBar.setRightTextANDImgOption(getString(resId), drawable, listener);
            if (color != 0) {
                mEbeiAppBar.setRightTextColor(color);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // eca_appbar
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 从view中获取color，用以设置appbar的颜色
     */
    public void setAppBarColor(View view) {
        if (null != mEbeiAppBar && null != view) {
            Drawable drawable = view.getBackground();
            if (drawable instanceof GradientDrawable) {
                return;
            } else if (drawable instanceof ColorDrawable) {
                setParentSelector(((ColorDrawable) drawable).getColor());
                return;
            }
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;

            Palette palette = Palette.from(bitmapDrawable.getBitmap()).generate();
            // getVibrantSwatch();      // 有活力
            // getDarkVibrantSwatch();  // 有活力 暗色
            // getLightVibrantSwatch(); // 有活力 亮色
            // getMutedSwatch();        // 柔和
            // getDarkMutedSwatch();    // 柔和 暗色
            // getLightMutedSwatch();   // 柔和 亮色
            Palette.Swatch vibrant = palette.getLightVibrantSwatch();
            if (null != vibrant) {
                // getRgb(): the RGB value of this color.
                // getBodyTextColor(): the RGB value of a text color which can be displayed on top of this color.
                // getTitleTextColor(): the RGB value of a text color which can be displayed on top of this color.
                // getPopulation(): the amount of pixels which this swatch represents.
                // getHsl(): the HSL value of this color.
                setParentSelector(vibrant.getRgb());
            }
        }
    }

    /**
     * 设置appbar的颜色
     */
    public void setAppBarColor(@ColorInt int color) {
        setParentSelector(color);
    }

    /**
     * 设置appbar的背景图片
     */
    public void setAppBarDrawble(@DrawableRes int drawble) {
        if (null != mEbeiAppBar && drawble != 0) {
            mEbeiAppBar.setBackgroundResource(drawble);
        }
    }

    /**
     * 为LeftImage设置Selector
     */
    private void setParentSelector(@ColorInt int color) {
        //int colorBurn = EcaMiscUtils.colorBurn(color);
        if (null != mEbeiAppBar && color != 0) {
            mEbeiAppBar.setBackgroundColor(color);
            mEbeiAppBar.setLeftParentSelector(color, color);
        }

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.setStatusBarColor(color);
            window.setNavigationBarColor(color);
        }
    }

    public ViewGroup getRootView() {
        return rootView;
    }
}
