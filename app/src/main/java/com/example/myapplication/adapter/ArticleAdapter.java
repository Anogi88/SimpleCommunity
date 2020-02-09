package com.example.myapplication.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.myapplication.R;
import com.example.myapplication.model.Article;

import java.util.List;



/**
 * 社区页面文章的适配器
 */

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder>{
    private Context context;
    private List<Article> articleList;
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    //回调接口
    public interface OnItemClickListener {
        void onItemClick(View view, int position,String id);
    }



    /**
     * 接受外部传来的数据
     */
    public ArticleAdapter(Context context, List<Article> recyclerViewItemList) {
        this.context = context;
        this.articleList = recyclerViewItemList;
    }

    /**
     * 填充视图
     */
    @NonNull
    @Override
    public  ArticleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new ArticleAdapter.ViewHolder(view);
    }

    /**
     * 获取控件
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        View articleView;

       // private final ImageView avatar;
        private final TextView tv_username;
        private final TextView tv_createTime;
        private final TextView tv_title;
        private final TextView jubao;
        //private  final TextView num_comment;
       // private final NineGridViewGroup nineGridViewGroup;
       // private final ImageView iv_eye;
       // private final ImageView iv_share;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            articleView=itemView;
            // 头像
            //avatar = itemView.findViewById(R.id.avatar);
            // 用户名
            tv_username = itemView.findViewById(R.id.tv_username);
            // 创建时间
            tv_createTime = itemView.findViewById(R.id.tv_createTime);
            // 标题
            tv_title = itemView.findViewById(R.id.tv_content);
            //举报
            jubao=itemView.findViewById(R.id.jubao);

        }
    }

    /**
     * 绑定控件
     */
    @Override
    public void onBindViewHolder(@NonNull  ArticleAdapter.ViewHolder holder, int position) {
        // 获取对应的数据
        Article article = articleList.get(position);

        // 往控件上绑定数据
        //NineGridViewGroup.getImageLoader().onDisplayImage(context,holder.avatar,article.getHeadImageUrl());
        /*if (article.getHeadImageUrl()==null){
            holder.avatar.findViewById(R.id.avatar);
        }else {
            Bitmap bitmap=base64ToImage(article.getHeadImageUrl());
            holder.avatar.setImageBitmap(bitmap);
        }*/
        holder.tv_username.setText(article.getCreateTime());
        holder.tv_createTime.setText(article.getNickName());
        holder.tv_title.setText(article.getTitle());
            //为ItemView设置监听器
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //判断是否设置了监听器
                    if(mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v,position,articleList.get(position).getChannelId());
                    }
                }
            });
        }
    private Bitmap base64ToImage(String text) {
        //同样的，用base64.decode解析编码，格式跟上面一致
        byte[] bytes = Base64.decode(text, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }
}
