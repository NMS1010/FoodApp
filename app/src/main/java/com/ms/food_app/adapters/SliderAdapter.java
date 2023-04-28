package com.ms.food_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ms.food_app.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderViewHolder> {
    private List<String> images;
    private Context context;

    public SliderAdapter(Context context, List<String> images) {
        this.images = images;
        this.context = context;
    }

    @Override
    public SliderViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.slider_item, parent, false);
        return new SliderViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(SliderViewHolder viewHolder, int position) {
        String imgUrl = images.get(position);
        Glide.with(context)
                .load(imgUrl)
                .into(viewHolder.sliderItem_ImgV);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    public class  SliderViewHolder extends ViewHolder {
        private ImageView sliderItem_ImgV;

        public SliderViewHolder(View itemView) {
            super(itemView);
            sliderItem_ImgV = itemView.findViewById(R.id.sliderItem_ImgV);
        }
    }
}
