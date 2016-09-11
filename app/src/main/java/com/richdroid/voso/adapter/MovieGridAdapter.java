package com.richdroid.voso.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;
import com.richdroid.voso.R;
import com.richdroid.voso.model.SearchGif;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by richa.khanna on 3/18/16.
 */
public class MovieGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static Context mContext;
    private static List<SearchGif> mDatasetList;
    // Allows to remember the last item shown on screen
    private int lastAnimatedItemPosition = -1;
    private boolean mTwoPane;
    private ShowSimilarInterface mShowSimilarInterface;


    // Provide a suitable constructor (depends on the kind of dataset)
    public MovieGridAdapter(Context context, List<SearchGif> datasetList, boolean twoPane, ShowSimilarInterface showSimilarInterface) {
        mContext = context;
        mDatasetList = datasetList;
        mTwoPane = twoPane;
        mShowSimilarInterface = showSimilarInterface;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MovieViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {
        @BindView(R.id.tv_title)
        TextView mTVTitle;
        @BindView(R.id.card_view)
        CardView mCardView;
        @BindView(R.id.iv_thumbnail)
        ImageView mIVThumbNail;
        @BindView(R.id.ib_similar)
        ImageButton mIbSimilar;


        public MovieViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.mIVThumbNail.setOnClickListener(this);
            this.mIbSimilar.setOnClickListener(this);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int itemPosition = getAdapterPosition();
            if (itemPosition != RecyclerView.NO_POSITION) {
                SearchGif searchGif = mDatasetList.get(itemPosition);

                switch (view.getId()) {
                    case R.id.iv_thumbnail:

                        Toast.makeText(mContext, "You clicked at position " + itemPosition +
                                " on item title : " +
                                searchGif.getTitle(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.ib_similar:
                        Toast.makeText(mContext, "Show similar gif for pos : " + itemPosition, Toast.LENGTH_SHORT).show();
                        mShowSimilarInterface.callApiToShowSimilarGif(searchGif.getGifUrl());
                }
            }

        }


        @Override
        public boolean onLongClick(View v) {
            int itemPosition = getAdapterPosition();
            return true;
        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item_view, parent, false);
        return new MovieViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        MovieViewHolder cusHolder = (MovieViewHolder) holder;
        cusHolder.mTVTitle.setText(mDatasetList.get(position).getTitle());
        String completeGifPath = mDatasetList.get(position).getGifUrl();

        Ion.with(cusHolder.mIVThumbNail)
                .placeholder(R.mipmap.placeholder)
                .error(R.mipmap.placeholder)
                .animateGif(AnimateGifMode.ANIMATE)
                .load(completeGifPath);
        cusHolder.mIVThumbNail.setVisibility(View.VISIBLE);
        setEnterAnimation(cusHolder.mCardView, position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDatasetList.size();
    }

    private void setEnterAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it will be animated
        if (position > lastAnimatedItemPosition) {
            //Animation using xml
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.translate_up);
            viewToAnimate.startAnimation(animation);
            lastAnimatedItemPosition = position;
        }
    }

    /**
     * The view could be reused while the animation is been happening.
     * In order to avoid that is recommendable to clear the animation when is detached.
     */
    @Override
    public void onViewDetachedFromWindow(final RecyclerView.ViewHolder holder) {
        ((MovieViewHolder) holder).mCardView.clearAnimation();
    }

    public interface ShowSimilarInterface {
        void callApiToShowSimilarGif(String url);
    }
}
