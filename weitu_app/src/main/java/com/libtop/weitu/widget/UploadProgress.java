package com.libtop.weitu.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.classify.bean.UploadBean;


/**
 * Created by 陆伟健 on 2016/4/25 0025.
 */
public class UploadProgress extends ProgressBar
{
    String text;
    private String title;
    private String time;
    private String state;
    private float one_size;
    private float two_size;
    private float three_size;
    private Context mContext;
    private Paint onePaint;
    private Paint twoPaint;
    private Paint threePaint;

    UploadBean uploadBean;

    /**
     * 控件高度
     */
    private int heightLength;
    /**
     * 控件宽度
     */
    private int widthLength;


    public UploadProgress(Context context)
    {
        super(context);
        initText(context);
    }


    public UploadProgress(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initText(context);
    }


    public UploadProgress(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.mySize);
        one_size = a.getDimension(R.styleable.mySize_one_size, 0);
        two_size = a.getDimension(R.styleable.mySize_two_size, 0);
        three_size = a.getDimension(R.styleable.mySize_three_size, 0);
        a.recycle();
        initText(context);
    }


    public void setUploadBean(UploadBean uploadBean)
    {
        this.uploadBean = uploadBean;
        postInvalidate();
    }


    @Override
    public synchronized void setProgress(int progress)
    {
        setText(progress);
        super.setProgress(progress);

    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if (uploadBean == null)
        {
            return;
        }
        heightLength = this.getHeight();
        widthLength = this.getWidth();
        int y1 = heightPiont(1, heightLength);
        int y2 = heightPiont(2, heightLength);
        int y3 = heightPiont(3, heightLength);
        int x = widthLength / 5 * 2;
        widthLength = x;
        drawBit(canvas);
        canvas.drawText(uploadBean.getOne(), x, y1, onePaint);
        canvas.drawText(uploadBean.getTwo(), x, y2, twoPaint);
        canvas.drawText(uploadBean.getThree(), x, y3, threePaint);
    }


    private void drawBit(Canvas canvas)
    {
        int x = widthLength - 30;
        int y = heightLength - 30;
        Bitmap bmpCat = BitmapFactory.decodeResource(getResources(), R.drawable.default_file);
        Bitmap newbm = zoomImg(bmpCat, x, y);
        newbm = createBitmap(newbm, "");
        canvas.drawBitmap(newbm, 10, 10, null);
    }


    //初始化，画笔
    private void initText(Context context)
    {
        onePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        twoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        threePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        onePaint.setColor(Color.rgb(51, 51, 51));
        twoPaint.setColor(Color.rgb(153, 153, 153));
        threePaint.setColor(Color.rgb(102, 102, 102));
        onePaint.setTextSize(one_size);
        twoPaint.setTextSize(two_size);
        threePaint.setTextSize(three_size);
        this.mContext = context;
    }


    private void setText()
    {
        setText(this.getProgress());
    }


    //设置文字内容
    private void setText(int progress)
    {
        int i = (progress * 100) / this.getMax();
        this.text = String.valueOf(i) + "%";
    }

    //    @Override
    //    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    //        Rect rect = this.getTextRect();
    //        int textHeight = rect.height();
    //        //int width = measuredWidth(widthMeasureSpec);
    //        widthLength = widthMeasureSpec;
    //        int height = measuredHieght(heightMeasureSpec);
    //        setMeasuredDimension(widthMeasureSpec, height);
    //    }


    private Rect getTextRect()
    {
        Rect rect = new Rect();
        return rect;
    }

    //    private int measuredWidth(int m) {
    //        int mode = MeasureSpec.getMode(m);
    //        int size = MeasureSpec.getSize(m);
    //        int length = px2dip(mContext, 50f);
    //        int width = 0;
    //        if (mode == MeasureSpec.EXACTLY) {
    //            //宽度为match_parent时，固定为50dip。
    //            width = length;
    //        } else if (mode == MeasureSpec.AT_MOST) {
    //            // warp_parent，固定为50dip。
    //            if (px2dip(mContext, 50f) < px2dip(mContext, size))
    //                width = textWidth;
    //            else
    //                width = length;
    //        }
    //        return width;
    //    }


    private int measuredHieght(int heightMeasureSpec)
    {
        int mode = View.MeasureSpec.getMode(heightMeasureSpec);
        int size = View.MeasureSpec.getSize(heightMeasureSpec);
        int length = px2dip(mContext, 250f);
        int Height = 0;
        if (mode == View.MeasureSpec.EXACTLY)
        {
            //高度为match_parent时，固定为50dip。
            Height = length;
            heightLength = length;
        }
        else if (mode == View.MeasureSpec.AT_MOST)
        {
            // 如果warp_parent低于50dip，固定为50dip。
            if (px2dip(mContext, 50f) < px2dip(mContext, size))
            {
                Height = heightMeasureSpec;
                heightLength = heightMeasureSpec;
            }
            else
            {
                Height = length;
                heightLength = length;
            }
        }
        return Height;
    }


    private Bitmap createBitmap(Bitmap src, String str)
    {
        int w = src.getWidth();
        int h = src.getHeight();
        String mstrTitle = uploadBean.getFileLength();
        String sTitle = uploadBean.getTime();
        Bitmap bmpTemp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmpTemp);
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setTextSize(22);
        canvas.drawBitmap(src, 0, 0, p);
        canvas.drawText(mstrTitle, 10, h - 10, p);
        canvas.drawText(sTitle, w - 50, h - 10, p);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return bmpTemp;
    }


    public int px2dip(Context context, float pxValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    private int heightPiont(int times, int heigit)
    {
        int lenght = 0;
        times = times - 1;
        lenght = heigit / 3 * times + 35;
        return lenght;
    }


    /**
     * 处理图片
     *
     * @param bm 所要转换的bitmap
     * @param
     * @param
     * @return 指定宽高的bitmap
     */
    public Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight)
    {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }
}
