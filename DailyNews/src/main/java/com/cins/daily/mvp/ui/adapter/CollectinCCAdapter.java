package com.cins.daily.mvp.ui.adapter;


import android.support.annotation.AnimRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cins.daily.App;
import com.cins.daily.R;
import com.cins.daily.listener.OnItemListener;
import com.cins.daily.mvp.entity.NewsSum;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by light on 2017/6/3.
 */

public class CollectinCCAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private OnItemListener onItemClickListener;
    private List<NewsSum> mList;
    protected int mLastPosition = -1;


    public CollectinCCAdapter(List<NewsSum> list) {
       this.mList=list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getView(parent, R.layout.item_news);
        final ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        setItemOnClickEvent(itemViewHolder, false);
        return itemViewHolder;
    }
    private void setItemOnClickEvent(final RecyclerView.ViewHolder holder, final boolean isPhoto) {
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  onItemClickListener.onItemClickLister(v, holder.getLayoutPosition(), isPhoto);
                }
            });
        }
    }
    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.news_summary_photo_iv)
        ImageView mNewsSummaryPhotoIv;
        @BindView(R.id.news_summary_title_tv)
        TextView mNewsSummaryTitleTv;
        @BindView(R.id.news_summary_digest_tv)
        TextView mNewsSummaryDigestTv;
        @BindView(R.id.news_summary_ptime_tv)
        TextView mNewsSummaryPtimeTv;

        public ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    protected View getView(ViewGroup parent, int layoutId) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        setValues(holder, position);
        setItemAppearAnimation(holder, position, R.anim.anim_bottom_in);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
    private void setValues(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            setItemValues((ItemViewHolder) holder, position);
        } else if (holder instanceof NewsListAdapter.PhotoViewHolder){

        }

    }
    protected void setItemAppearAnimation(RecyclerView.ViewHolder holder, int position, @AnimRes int type) {
        if (position > mLastPosition/* && !isFooterPosition(position)*/) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), type);
            holder.itemView.startAnimation(animation);
            mLastPosition = position;
        }
    }
    private void setItemValues(ItemViewHolder holder, int position) {
        NewsSum newsSummary = mList.get(position);
        String title = newsSummary.getTitle();


        String ptime = newsSummary.getPtime();
        String digest = newsSummary.getDigest();
        String imgSrc = newsSummary.getImgsrc();

        holder.mNewsSummaryTitleTv.setText(title);
        holder.mNewsSummaryPtimeTv.setText(ptime);
        holder.mNewsSummaryDigestTv.setText(digest);

        Glide.with(App.getAppContext()).load(imgSrc).asBitmap() // gif格式有时会导致整体图片不显示，貌似有冲突
                .format(DecodeFormat.PREFER_ARGB_8888)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.color.image_place_holder)
                .error(R.drawable.ic_load_fail)
                .into(holder.mNewsSummaryPhotoIv);
    }

}

