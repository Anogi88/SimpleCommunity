package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Record;

import java.util.List;

public class ComplainAdapter extends RecyclerView.Adapter<ComplainAdapter.ViewHolder>{
    private Context context;
    private List<Record> recordList;

    /**
     * 接受外部传来的数据
     */
    public ComplainAdapter(Context context, List<Record> recyclerViewItemList) {
        this.context = context;
        this.recordList = recyclerViewItemList;
    }
    /**
     * 填充视图
     */
    @NonNull
    @Override
    public  ComplainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.complain_list, parent, false);
        return new ComplainAdapter.ViewHolder(view);
    }

    /**
     * 获取控件
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        View recordView;

        //private final ImageView avatar;
        private final TextView tv_title;
        private final TextView tv_createTime;
        private final TextView tv_content;
        private final TextView tv_reply;
        //private  final TextView num_comment;
        // private final NineGridViewGroup nineGridViewGroup;
        // private final ImageView iv_eye;
        // private final ImageView iv_share;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recordView=itemView;
            // 头像
            //avatar = itemView.findViewById(R.id.avatar);
            // 投诉标题
            tv_title = itemView.findViewById(R.id.complain_title);
            // 投诉时间
            tv_createTime = itemView.findViewById(R.id.complain_time);
            // 内容
            tv_content = itemView.findViewById(R.id.complain_content);
            //回复内容
            tv_reply=itemView.findViewById(R.id.complain_reply);


        }
    }

    /**
     * 绑定控件
     */
    @Override
    public void onBindViewHolder(@NonNull  ComplainAdapter.ViewHolder holder, int position) {
        // 获取对应的数据
        Record record = recordList.get(position);

        // 往控件上绑定数据
        //NineGridViewGroup.getImageLoader().onDisplayImage(context,holder.avatar,article.getHeadImageUrl());
        holder.tv_title.setText(record.getNickName());
        holder.tv_createTime.setText(record.getCreateTime());
        holder.tv_content.setText(record.getContent());
        if(record.getReply()==null){
            holder.tv_reply.setText("暂无回复");
        }else{
            holder.tv_reply.setText(record.getReply());
        }
    }
    @Override
    public int getItemCount() {
        return recordList.size();
    }
}
