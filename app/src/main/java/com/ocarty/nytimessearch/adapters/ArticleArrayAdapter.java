package com.ocarty.nytimessearch.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ocarty.nytimessearch.model.Article;
import com.ocarty.nytimessearch.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ocarty on 10/21/2016.
 */

public class ArticleArrayAdapter extends ArrayAdapter<Article> {

    public  ArticleArrayAdapter(Context context, List<Article> articles) {
        super(context, R.layout.item_article_result, articles);
    }

    private static class ViewHolder {
        TextView tvTitle;
        ProgressBar progressBar;
        ImageView ivImage;
    }



    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get the data item for position
        Article article = this.getItem(position);
        // check to see if existing view being reused
        ViewHolder viewHolder; // view lookup cache stored in tag

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_article_result, parent, false);
            viewHolder.ivImage = (ImageView)convertView.findViewById(R.id.ivImage);
            viewHolder.tvTitle = (TextView)convertView.findViewById(R.id.tvTitle);
            viewHolder.progressBar = (ProgressBar)convertView.findViewById(R.id.progressBar);
            convertView.setTag(viewHolder);
        }
        else {
           viewHolder = (ViewHolder)convertView.getTag();
        }
        final ProgressBar progressBar = viewHolder.progressBar;
        viewHolder.ivImage.setImageResource(0);
        viewHolder.tvTitle.setText(article.getHeadLine());

        String thumbNail = article.getThumbNail();
        if(!TextUtils.isEmpty(thumbNail)) {
            Picasso.with(getContext()).load(thumbNail).fit()
                    .into(viewHolder.ivImage, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }
        else {
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
        }
        return convertView;
    }
}
