package com.example.myapplication.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.CircleImg;
import com.example.myapplication.R;
import com.example.myapplication.model.CommentDetailBean;



import java.util.List;

/**
 * Desc: 评论与回复列表的适配器
 */

public class CommentExpandAdapter extends RecyclerView.Adapter<CommentExpandAdapter.ViewHolder> {

    private List<CommentDetailBean> commentBeanList;
    private Context context;


    /**
     * 接受外部传来的数据
     */
    public CommentExpandAdapter(Context context, List<CommentDetailBean> commentBeanList) {
        this.context = context;
        this.commentBeanList = commentBeanList;
    }

    /**
     * 填充视图
     */
    @NonNull
    @Override
    public  CommentExpandAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item_layout, parent, false);
        return new CommentExpandAdapter.ViewHolder(view);
    }

    /**
     * 获取控件
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        View commentView;

        //private final ImageView avatar;
        private  TextView tv_name;
        private  TextView tv_time;
        private  TextView tv_content;
        private  TextView jubao;

        public ViewHolder(@NonNull View view) {
            super(view);
            tv_content =  view.findViewById(R.id.comment_item_content);
            tv_name =  view.findViewById(R.id.comment_item_userName);
            tv_time =  view.findViewById(R.id.comment_item_time);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 获取对应的数据
        CommentDetailBean commentDetailBean = commentBeanList.get(position);

        // 往控件上绑定数据
        //NineGridViewGroup.getImageLoader().onDisplayImage(context,holder.avatar,article.getHeadImageUrl());
        holder.tv_name.setText(commentDetailBean.getNickName());
        holder.tv_time.setText(commentDetailBean.getCreateDate());
        holder.tv_content.setText(commentDetailBean.getContent());

    }

    @Override
    public int getItemCount() {
        return commentBeanList.size();
    }





    /**
     * by moos on 2018/04/20
     * func:评论成功后插入一条数据
     * @param commentDetailBean 新的评论数据
     */
    public void addTheCommentData(CommentDetailBean commentDetailBean){
        if(commentDetailBean!=null){
            commentBeanList.add(commentDetailBean);
            notifyDataSetChanged();
        }else {
            throw new IllegalArgumentException("评论数据为空!");
        }

    }





}
