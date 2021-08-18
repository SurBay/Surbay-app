package com.pumasi.surbay.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.pumasi.surbay.pages.MainActivity;
import com.pumasi.surbay.pages.homepage.NoticeDetailActivity;
import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.Notice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BannerViewPagerAdapter extends PagerAdapter {


    private ArrayList<String> IMAGES;
    private LayoutInflater inflater;
    private Context context;


    public BannerViewPagerAdapter(Context context, ArrayList<String> IMAGES) {
        this.context = context;
        this.IMAGES=IMAGES;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.bannerviewpageritem, view, false);

        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout
                .findViewById(R.id.imageview);


        Glide.with(context).load(IMAGES.get(position)).into(imageView);

        view.addView(imageLayout, 0);

        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position==0) {
//                    List<Post> result = MainActivity.notreportedpostArrayList.stream().filter(a -> a.getID().equals("60363e6bb25c6932d02dca5a")).collect(Collectors.toList());
//
//                    Post item = result.get(0);
//                    Intent intent = new Intent(context, PostDetailActivity.class);
//                    intent.putExtra("post", item);
//                    intent.putParcelableArrayListExtra("reply", item.getComments());
//                    intent.putExtra("position", position);
//                    context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    return;
                }else if(position==1){
                    List<Notice> result = MainActivity.NoticeArrayList.stream().filter(a -> a.getID().equals("603bc733f8853d5f37d4e67e")).collect(Collectors.toList());

                    Notice item = result.get(0);
                    Intent intent = new Intent(context, NoticeDetailActivity.class);
                    intent.putExtra("post", item);
                    intent.putExtra("position", position);
                    context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }
        });
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}