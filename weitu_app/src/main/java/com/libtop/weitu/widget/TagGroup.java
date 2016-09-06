package com.libtop.weitu.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.libtop.weitu.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by LianTu on 2016/5/17.
 */
public class TagGroup extends ViewGroup
{

    private final int verticalSpacing = 10;
    private final int horizontalSpacing = 10;
    private final int paddingSpace = 5;
    EditText editText;

    private TextWatcher watcher = new TextWatcher()
    {
        private CharSequence temp;
        private int editStart;
        private int editEnd;


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            temp = s;
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }


        @Override
        public void afterTextChanged(Editable s)
        {
            editStart = editText.getSelectionStart();
            editEnd = editText.getSelectionEnd();

            if (temp.length() > 8)
            {
                Toast.makeText(editText.getContext(), "你输入的字数已经超过了限制！", Toast.LENGTH_SHORT).show();
                s.delete(editStart - 1, editEnd);
                int tempSelection = editStart;
                editText.setText(s);
                editText.setSelection(tempSelection);
            }
        }
    };


    public TagGroup(Context context)
    {
        super(context);
        TagView tagView = new TagView(context, 0, null);
        addView(tagView);
        setTags(new String[]{});
    }


    public TagGroup(final Context context, AttributeSet attrs)
    {
        super(context, attrs);
        editText = new EditText(context);
        editText.setBackgroundResource(R.drawable.shape_bg_tag);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        editText.setHint("请输入8个字符");
        editText.setSingleLine();
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                String textString = editText.getText().toString();
                if (!TextUtils.isEmpty(textString))
                {
                    if (getChildCount() < 5)
                    {
                        TagView tagView = new TagView(context, 0, textString);
                        Log.w("guanglog", "child count =            " + getChildCount());
                        addView(tagView, getChildCount() - 1);
                        editText.setText("");
                        return true;
                    }
                    else
                    {
                        Toast.makeText(editText.getContext(), "标签个数超过4个", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                }
                else
                {
                    return false;
                }
            }
        });
        editText.setOnKeyListener(new OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    if (TextUtils.isEmpty(editText.getText().toString()))
                    {
                        Log.w("guanglog", "child count = " + getChildCount());
                        if (getChildCount() > 1)
                        {
                            removeViewAt(getChildCount() - 2);
                        }
                    }
                }
                return false;
            }
        });
        editText.addTextChangedListener(watcher);
        //        editText.setBackgroundResource(R);
        setTags(new String[]{});

    }


    public TagGroup(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        setTags(new String[]{});
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int width = 0;
        int height = 0;

        int row = 0; // The row counter.
        int rowWidth = 0; // Calc the current row width.
        int rowMaxHeight = 0; // Calc the max tag height, in current row.

        final int count = getChildCount();
        for (int i = 0; i < count; i++)
        {
            final View child = getChildAt(i);
            final int childWidth = child.getMeasuredWidth();
            final int childHeight = child.getMeasuredHeight();

            if (child.getVisibility() != GONE)
            {
                rowWidth += childWidth;
                if (rowWidth > widthSize)
                { // Next line.
                    rowWidth = childWidth; // The next row width.
                    height += rowMaxHeight + verticalSpacing;
                    rowMaxHeight = childHeight; // The next row max height.
                    row++;
                }
                else
                { // This line.
                    rowMaxHeight = Math.max(rowMaxHeight, childHeight);
                }
                rowWidth += horizontalSpacing;
            }
        }
        // Account for the last row height.
        height += rowMaxHeight;

        // Account for the padding too.
        height += getPaddingTop() + getPaddingBottom();

        // If the tags grouped in one row, set the width to wrap the tags.
        if (row == 0)
        {
            width = rowWidth;
            width += getPaddingLeft() + getPaddingRight();
        }
        else
        {// If the tags grouped exceed one line, set the width to match the parent.
            width = widthSize;
        }

        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : width, heightMode == MeasureSpec.EXACTLY ? heightSize : height);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        final int parentLeft = getPaddingLeft();
        final int parentRight = r - l - getPaddingRight();
        final int parentTop = getPaddingTop();
        final int parentBottom = b - t - getPaddingBottom();

        int childLeft = parentLeft;
        int childTop = parentTop;

        int rowMaxHeight = 0;

        final int count = getChildCount();
        for (int i = 0; i < count; i++)
        {
            final View child = getChildAt(i);
            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();

            if (child.getVisibility() != GONE)
            {
                if (childLeft + width > parentRight)
                { // Next line
                    childLeft = parentLeft;
                    childTop += rowMaxHeight + verticalSpacing;
                    rowMaxHeight = height;
                }
                else
                {
                    rowMaxHeight = Math.max(rowMaxHeight, height);
                }
                child.layout(childLeft, childTop, childLeft + width, childTop + height);

                childLeft += width + horizontalSpacing;
            }
        }
    }


    /**
     * Set the tags. It will remove all previous tags first.
     *
     * @param tags the tag list to set.
     */
    public void setTags(String... tags)
    {
        removeAllViews();
        if (tags == null || tags.length == 0)
        {
            addView(editText);
            return;
        }
        for (final String tag : tags)
        {
            if (TextUtils.isEmpty(tag))
            {
                continue;
            }
            appendTag(tag);
        }
        addView(editText);
    }


    /**
     * Returns the tag array in group, except the INPUT tag.
     *
     * @return the tag array.
     */
    public String[] getTags()
    {
        final int count = getChildCount() - 1;
        final List<String> tagList = new ArrayList<>();
        for (int i = 0; i < count; i++)
        {
            //            final TagView tagView = getTagAt(i).gette;
            tagList.add(getTagAt(i).getText().toString());
            Log.w("guanglog", getTagAt(i).getText().toString());
        }

        return tagList.toArray(new String[tagList.size()]);
    }


    /**
     * Returns the tag view at the specified position in the group.
     *
     * @param index the position at which to get the tag view from.
     * @return the tag view at the specified position or null if the position
     * does not exists within this group.
     */
    protected TagView getTagAt(int index)
    {
        return (TagView) getChildAt(index);
    }


    /**
     * Append tag to this group.
     *
     * @param tag the tag to append.
     */
    protected void appendTag(CharSequence tag)
    {
        final TagView newTag = new TagView(getContext(), 0, tag);
        //        newTag.setOnClickListener(mInternalTagClickListener);
        addView(newTag);
    }


    class TagView extends TextView implements OnClickListener
    {

        private Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Path mBorderPath = new Path();

        private RectF mLeftCornerRectF = new RectF();
        private RectF mRightCornerRectF = new RectF();

        private float borderStrokeWidth = 10;

        private Paint paint;


        public TagView(Context context, final int state, CharSequence text)
        {
            super(context);
            setOnClickListener(this);
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setStrokeWidth(borderStrokeWidth);
            mBorderPaint.setColor(Color.WHITE);
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            int paddingLeft = paddingSpace;
            int paddingRight = paddingSpace;
            int paddingTop = paddingSpace;
            int paddingBottom = paddingSpace;

            //            setBackgroundResource(R.mipmap.ic_launcher);
            setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
            setLayoutParams(new TagGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

            //            setBackgroundColor(Color.RED);
            setBackgroundResource(R.drawable.shape_bg_tag);
            setGravity(Gravity.CENTER);
            setText(text);
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            setHintTextColor(Color.WHITE);
            setTextColor(Color.WHITE);
        }


        @Override
        protected void onDraw(Canvas canvas)
        {

            super.onDraw(canvas);


            //            paint.setColor(Color.YELLOW);
            //            canvas.drawRect(0,0,getWidth(),getHeight(),paint);
            //            paint.setColor(Color.BLUE);
            //            paint.setTextSize(20);
            //            String text = "Hello View";
            //            canvas.drawText(text,0,getHeight()/2,paint);
        }


        @Override
        public void onClick(View v)
        {
            removeView(v);
            Log.w("guanglog", "on click........");
            invalidate();
        }
    }
}
