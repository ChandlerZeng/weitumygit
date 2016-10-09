package com.libtop.weitu.activity.main.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.dto.SubjectBean;
import com.libtop.weitu.utils.ImageLoaderUtil;
import com.libtop.weitu.viewadapter.CommonAdapter;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.libtop.weitu.widget.view.GridViewForScrollView;
import com.paging.gridview.PagingGridView;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.libtop.weitu.application.AppApplication.getContext;


/**
 * Created by Zeng on 2016/9/7.
 */
public class MoreSubjectAdapter extends CommonAdapter<SubjectBean>
{
    private GridViewForScrollView gridViewForScrollView;
    private PagingGridView pagingGridView;
    private Context context;

    public MoreSubjectAdapter(Context context, List<SubjectBean> datas) {
        super(context, R.layout.item_main_fragment_subject, datas);
        this.context = context;
    }

    public MoreSubjectAdapter(Context context, List<SubjectBean> datas,PagingGridView pagingGridView) {
        super(context, R.layout.item_main_fragment_subject, datas);
        this.pagingGridView = pagingGridView;
        this.context = context;
    }

    @Override
    public void convert(ViewHolderHelper helper, SubjectBean object, int position) {
        ImageView imageView = helper.getView(R.id.img_item_subject);
//        setImageScale(imageView);
        ImageView imageNew = helper.getView(R.id.img_item_subject_new);
        Picasso.with(context).load(object.getCover()).placeholder(ImageLoaderUtil.RESOURCE_ID_IMAGE_BIG).error(ImageLoaderUtil.RESOURCE_ID_IMAGE_BIG).centerInside().fit().into(imageView);
        helper.setText(R.id.tv_item_subject, object.getTitle());
        if(object.getResourceUpdateCount()==0){
            imageNew.setVisibility(View.GONE);
        }else if(object.getResourceUpdateCount() == 1) {
            imageNew.setVisibility(View.VISIBLE);
        }
    }
    public void setData(List<SubjectBean> list){
        this.datas = list ;
        notifyDataSetChanged();
    }

    /**
     * 获得屏幕高度
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    private void setImageScale(ImageView image){
        ViewGroup.MarginLayoutParams margin9 = new ViewGroup.MarginLayoutParams(
                image.getLayoutParams());
        margin9.setMargins(5, 5, 5, 5);//在左边距400像素，顶边距10像素的位置显示图片
        int screenWidth = getScreenWidth(getContext());
        int numColumns = 0;
        int padding = 0;
        if(gridViewForScrollView!=null){
            numColumns = gridViewForScrollView.getNumColumns();
            padding = gridViewForScrollView.getPaddingLeft() + gridViewForScrollView.getPaddingRight();
        }else if(pagingGridView!=null){
            numColumns = pagingGridView.getNumColumns();
            padding = pagingGridView.getPaddingLeft() + pagingGridView.getPaddingRight();
        }

        int horizontalSpacing = context.getResources().getDimensionPixelSize(R.dimen.margin_tiny);
        int itemWidth = ((screenWidth - padding - (numColumns - 1) * horizontalSpacing) / numColumns);

        LinearLayout.LayoutParams layoutParams9 = new LinearLayout.LayoutParams(margin9);
        layoutParams9.height = (itemWidth*9)/16;//设置图片的高度
        layoutParams9.width = itemWidth; //设置图片的宽度
        image.setLayoutParams(layoutParams9);
    }
}
