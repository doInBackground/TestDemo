package com.wcl.testdemo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wcl.testdemo.R;
import com.wcl.testdemo.bean.TestBean;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @Author WCL
 * @Date 2022/11/16 10:20
 * @Version
 * @Description RecyclerView适配器Adapter.
 */
public class TestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private final Context mContext;
    private final List<TestBean> mList;
    private OnItemClickListener mOnItemClickListener;

    public TestAdapter(Context context, List<TestBean> list) {
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    //根据条目类型(viewType),当需要新建条目布局的时候被回调(条目布局够用时即会复用,不会重复新建布局),返回包含条目布局的ViewHolder.
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View rootView = View.inflate(mContext, R.layout.item_recycler_view_text, null);//创建文字条目布局.
            rootView.setOnClickListener(this);//条目点击事件.
            return new TextViewHolder(rootView);
        } else if (viewType == 1) {
            View rootView = View.inflate(mContext, R.layout.item_recycler_view_image, null);//创建视图条目布局.
            rootView.setOnClickListener(this);//条目点击事件.
            return new ImageViewHolder(rootView);
        }
        return null;
    }

    @Override
    //在此处将数据与控件绑定.(为ViewHold中的控件,赋予要展示的内容)
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TestBean bean = mList.get(position);
        if (holder instanceof TextViewHolder) {
            TextViewHolder textViewHolder = (TextViewHolder) holder;
            textViewHolder.itemView.setTag(position);//RecyclerView.ViewHolder.itemView是默认存在的,即为构造时传入的View.
            //设置数据
            textViewHolder.tv.setText(bean.getText());
        } else if (holder instanceof ImageViewHolder) {
            ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
            imageViewHolder.itemView.setTag(position);
            //设置数据
//            imageViewHolder.iv.setImageResource(R.mipmap.ic_launcher);
            imageViewHolder.tv.setText(bean.getText());
        }
    }

    @Override
    //获取条目的总数,决定条目的总数量.相当于ListView中的getCount().
    public int getItemCount() {
        return mList.size();
    }

    @Override
    //在此处设置条目类型,可以在onCreateViewHolder()根据不同条目类型,改变条目的布局类型.
    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
//        TestBean testBean = mList.get(position);
        return (position == 0 || position == 1) ? 1 : 0;
    }

    //点击事件: 参数是被点击的条目的rootView.
    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();//被点击条目的position.
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(position, view);
        }
    }

    /**
     * 设置条目点击事件.
     *
     * @param listener 监听者
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    /**
     * 删除指定条目.
     *
     * @param position 条目索引
     */
    public void removeItem(int position) {
        mList.remove(position);//删除数据.
        notifyItemRemoved(position);//通知删除条目.
        notifyItemRangeChanged(position, getItemCount());//通知刷新被删除条目,以及其后面的数据.
    }


    /**
     * @Author WCL
     * @Date 2023/3/22 16:48
     * @Version
     * @Description 条目点击事件的接口回调.
     */
    public interface OnItemClickListener {
        void onItemClick(int position, View rootView);
    }

    /**
     * @Author WCL
     * @Date 2022/11/16 10:24
     * @Version
     * @Description 文字条目的ViewHolder.
     * ViewHolder:主要作用是减少条目滚动时,频繁的findViewById(),因为findViewById()也比较耗性能.
     */
    private static class TextViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv;

        public TextViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv);
        }
    }

    /**
     * @Author WCL
     * @Date 2022/11/16 10:24
     * @Version
     * @Description 图片条目的ViewHolder.
     * ViewHolder:主要作用是减少条目滚动时,频繁的findViewById(),因为findViewById()也比较耗性能.
     */
    private static class ImageViewHolder extends RecyclerView.ViewHolder {

        private final ImageView iv;
        private final TextView tv;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv);
            tv = itemView.findViewById(R.id.tv);
        }
    }

}