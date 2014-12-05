/*****************************************************************************
 * CardPresenter.java
 *****************************************************************************
 * Copyright © 2010-2013 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/
package org.videolan.vlc.gui.tv;

import org.videolan.libvlc.Media;
import org.videolan.vlc.MediaDatabase;
import org.videolan.vlc.R;
import org.videolan.vlc.gui.audio.AudioUtil;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.View;
import android.view.ViewGroup;

public class CardPresenter extends Presenter {

    private static final String TAG = "CardPresenter";

    private static Context sContext;
    private static Resources mRes;
    private static int CARD_WIDTH;
    private static int CARD_HEIGHT = 0;
    private static MediaDatabase sMediaDatabase = MediaDatabase.getInstance();
    private static Drawable sDefaultCardImage;

    public CardPresenter(Context context){
        sContext = context;
        mRes = sContext.getResources();
        sDefaultCardImage = mRes.getDrawable(R.drawable.cone);
        CARD_WIDTH = mRes.getDimensionPixelSize(R.dimen.tv_card_width);
        CARD_HEIGHT = mRes.getDimensionPixelSize(R.dimen.tv_card_height);
    }

    static class ViewHolder extends Presenter.ViewHolder {
        private ImageCardView mCardView;

        public ViewHolder(View view) {
            super(view);
            mCardView = (ImageCardView) view;
        }

        public ImageCardView getCardView() {
            return mCardView;
        }

        protected void updateCardViewImage(Media media) {
            Bitmap picture = null;
            if (media.getType() == Media.TYPE_AUDIO)
                picture = AudioUtil.getCover(sContext, media, 320);
            else if (media.getType() == Media.TYPE_VIDEO)
                picture = sMediaDatabase.getPicture(sContext, media.getLocation());
            if (picture != null && picture.getByteCount() > 4)
                mCardView.setMainImage(new BitmapDrawable(mRes, picture));
            else
                mCardView.setMainImage(sDefaultCardImage);
        }

        protected void updateCardViewImage(Drawable image) {
            mCardView.setMainImage(image);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {

        ImageCardView cardView = new ImageCardView(sContext);
        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        cardView.setBackgroundColor(mRes.getColor(R.color.lb_details_overview_bg_color));
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        ViewHolder holder = ((ViewHolder) viewHolder);
        holder.mCardView.getMainImageView().setAdjustViewBounds(true);
        holder.mCardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);
        if (item instanceof Media) {
            Media media = (Media) item;
            holder.mCardView.setTitleText(media.getTitle());
            holder.mCardView.setContentText(media.getDescription());
            if (media.getType() == Media.TYPE_GROUP)
                holder.updateCardViewImage(mRes.getDrawable(
                        R.drawable.ic_video_collection_big));
            else
                holder.updateCardViewImage(media);
        } else if (item instanceof GridFragment.ListItem) {
            GridFragment.ListItem listItem = (GridFragment.ListItem) item;
            Media media = listItem.mMediaList.get(0);
            holder.mCardView.setTitleText(listItem.mTitle);
            holder.mCardView.setContentText(listItem.mSubTitle);
            holder.updateCardViewImage(media);
        } else if (item instanceof String){
            holder.mCardView.setTitleText((String) item);
            holder.updateCardViewImage(getBitmap(mRes, item.toString()));
        }
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
    }

    @Override
    public void onViewAttachedToWindow(Presenter.ViewHolder viewHolder) {
        // TODO?
    }

    private Drawable getBitmap(Resources res, String item){
        if (res.getString(R.string.preferences).equals(item.toString()))
            return res.getDrawable( R.drawable.ic_menu_preferences_big);
        else if (res.getString(R.string.artists).equals(item.toString()))
            return res.getDrawable(R.drawable.ic_artist_big);
        else if (res.getString(R.string.albums).equals(item.toString()))
            return res.getDrawable(R.drawable.ic_album_big);
        else if (res.getString(R.string.genres).equals(item.toString()))
            return res.getDrawable(R.drawable.ic_genre_big);
        else if (res.getString(R.string.songs).equals(item.toString()))
            return res.getDrawable(R.drawable.ic_song_big);
        else
            return sDefaultCardImage;
    }
}
