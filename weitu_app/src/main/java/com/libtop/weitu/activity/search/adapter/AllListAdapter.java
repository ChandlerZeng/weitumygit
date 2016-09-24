package com.libtop.weitu.activity.search.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.search.AllFragment;
import com.libtop.weitu.activity.search.dto.AllDto;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.DateUtil;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by LianTu on 2016/6/27.
 */
public class AllListAdapter extends BaseAdapter
{

    private List<AllDto> lists;
    private Context context;
    private LayoutInflater inflater;
    private final int SUBJECT = 0, BOOK = 1,OTHER = 2;


    public AllListAdapter(Context context, List<AllDto> lists)
    {
        this.lists = lists;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getItemViewType(int position)
    {
        if (lists != null && position < lists.size())
        {
            String type = lists.get(position).entityType;
            if (type.equals(AllFragment.SUBJECT))
            {
                return SUBJECT;
            }
            else if (type.equals(AllFragment.BOOK))
            {
                return BOOK;
            }
            else
            {
                return OTHER;

            }
        }
        return super.getItemViewType(position);
    }


    @Override
    public int getViewTypeCount()
    {
        return 3;
    }


    @Override
    public int getCount()
    {
        return lists.size();
    }


    @Override
    public Object getItem(int position)
    {
        return lists.get(position);
    }


    @Override
    public long getItemId(int position)
    {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        int type = getItemViewType(position);
        AllDto allDto = lists.get(position);
        //通常只修改复制黏贴修改这部分代码
        switch (type)
        {
            case SUBJECT:
            {
                viewHolder1 holder = null;
                if (convertView == null)
                {
                    convertView = inflater.inflate(R.layout.item_list_rank_subject, null);
                    holder = new viewHolder1();
                    holder.imageView = (ImageView) convertView.findViewById(R.id.subject_file_image);
                    holder.tvTitle = (TextView) convertView.findViewById(R.id.subject_file_title);
                    holder.tvDesc = (TextView) convertView.findViewById(R.id.subject_file_desc);
                    holder.tvMember = (TextView) convertView.findViewById(R.id.subject_file_member);
                    convertView.setTag(holder);
                }
                else
                {
                    holder = (viewHolder1) convertView.getTag();
                }

                Picasso.with(context).load(allDto.cover).placeholder(R.drawable.default_image).error(R.drawable.default_image).centerInside().fit().into(holder.imageView);
                holder.tvTitle.setText(allDto.title);

                if (!TextUtils.isEmpty(allDto.introduction))
                {
                    holder.tvDesc.setText(allDto.introduction);
                }
                else
                {
                    holder.tvDesc.setText("");
                }
                if (!TextUtils.isEmpty(allDto.uploadUsername))
                {
                    holder.tvMember.setText("成员："+allDto.uploadUsername);
                }
                else
                {
                    holder.tvMember.setText("");
                }

                break;
            }
            case BOOK:
            {
                viewHolder2 holder = null;
                if (convertView == null)
                {
                    convertView = inflater.inflate(R.layout.item_list_rank_book, null);
                    holder = new viewHolder2();
                    holder.imageView = (ImageView) convertView.findViewById(R.id.subject_file_image);
                    holder.tvTitle = (TextView) convertView.findViewById(R.id.subject_file_title);
                    holder.tvDesc = (TextView) convertView.findViewById(R.id.subject_file_desc);
                    holder.tvAuthor = (TextView) convertView.findViewById(R.id.subject_file_author);
                    holder.tvPublisher = (TextView) convertView.findViewById(R.id.subject_file_publisher);
                    convertView.setTag(holder);
                }
                else
                {
                    holder = (viewHolder2) convertView.getTag();
                }

                Picasso.with(context).load(ContantsUtil.IMG_BASE + allDto.cover).placeholder(R.drawable.default_image).error(R.drawable.default_image).centerInside().fit().into(holder.imageView);
                holder.tvTitle.setText(allDto.title);

                if (!TextUtils.isEmpty(allDto.introduction))
                {
                    holder.tvDesc.setText(allDto.introduction);
                }
                else
                {
                    holder.tvDesc.setText("");
                }
                if (!TextUtils.isEmpty(allDto.author))
                {
                    holder.tvAuthor.setText("作者："+allDto.author);
                }
                else
                {
                    holder.tvAuthor.setText("");
                }
                if (!TextUtils.isEmpty(allDto.author))
                {
                    holder.tvPublisher.setText("出版社："+allDto.publisher);
                }
                else
                {
                    holder.tvPublisher.setText("");
                }

                break;
            }
            case OTHER:
            {
                viewHolder3 holder = null;
                if (convertView == null)
                {
                    convertView = inflater.inflate(R.layout.item_list_rank_other, null);
                    holder = new viewHolder3();
                    holder.imageView = (ImageView) convertView.findViewById(R.id.subject_file_image);
                    holder.tvTitle = (TextView) convertView.findViewById(R.id.subject_file_title);
                    holder.tvUploader = (TextView) convertView.findViewById(R.id.subject_file_uploader);
                    holder.tvDate = (TextView) convertView.findViewById(R.id.subject_file_date);
                    convertView.setTag(holder);
                }
                else
                {
                    holder = (viewHolder3) convertView.getTag();
                }

                Picasso.with(context).load(allDto.cover).placeholder(R.drawable.pdf).error(R.drawable.pdf).centerInside().fit().into(holder.imageView);
                holder.tvTitle.setText(allDto.title);
                if (!TextUtils.isEmpty(allDto.uploadUsername))
                {
                    holder.tvUploader.setText("上传："+allDto.uploadUsername);
                }
                else
                {
                    holder.tvUploader.setText("");
                }
                String date = DateUtil.parseToDate(allDto.timeline);
                holder.tvDate.setText("时间："+date);
                break;
            }
        }
        return convertView;
    }


    public void setData(List<AllDto> lists)
    {
        this.lists = lists;
        notifyDataSetChanged();
    }


    //各个布局的控件资源
    class viewHolder1
    {
        ImageView imageView;
        TextView tvTitle;
        TextView tvDesc;
        TextView tvMember;
    }

    class viewHolder2
    {
        ImageView imageView;
        TextView tvTitle;
        TextView tvDesc;
        TextView tvAuthor;
        TextView tvPublisher;
    }

    class viewHolder3
    {
        ImageView imageView;
        TextView tvTitle;
        TextView tvUploader;
        TextView tvDate;
    }

}
