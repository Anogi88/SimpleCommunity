package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Notice;

import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {
    private List<Notice> mNoticeList;
    private OnItemClickListener mOnItemClickListener;
    private OnItemClickListener mOnItemLongClickListener1;

//写公共方法
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public int size() {
        return mNoticeList.size();
    }


    //回调接口
    public interface OnItemClickListener {
        void onItemClick(View view, int position,String id);
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;

    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Notice notice=mNoticeList.get(position);
        holder.title.setText(notice.getTitle());
        holder.content.setText(notice.getContent());
        holder.datetime.setText(notice.getDatetime());
        //为ItemView设置监听器
        holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //判断是否设置了监听器
                    if(mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v,position,mNoticeList.get(position).getId());
                    }
                }
            });

    }

    @Override
    public int getItemCount() {
        return mNoticeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View noticeView;
        TextView title;
        TextView content;
        TextView datetime;
        public ViewHolder(View itemView) {
            super(itemView);
            noticeView=itemView;
            title= itemView.findViewById(R.id.notice_title);
            content= itemView.findViewById(R.id.notice_content);
            datetime= itemView.findViewById(R.id.notice_time);
        }
    }

    public NoticeAdapter(List<Notice> noticeList){
        mNoticeList=noticeList;
    }

}
