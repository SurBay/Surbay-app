package com.pumasi.surbay.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.pumasi.surbay.MainActivity;
import com.pumasi.surbay.NoticeDetailActivity;
import com.pumasi.surbay.PostDetailActivity;
import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.Notice;
import com.pumasi.surbay.classfile.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BannerViewPagerAdapter extends PagerAdapter {


    private ArrayList<Integer> IMAGES;
    private LayoutInflater inflater;
    private Context context;


    public BannerViewPagerAdapter(Context context,ArrayList<Integer> IMAGES) {
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


        imageView.setImageResource(IMAGES.get(position));

        view.addView(imageLayout, 0);

        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position==0) {
                    List<Post> result = MainActivity.notreportedpostArrayList.stream().filter(a -> a.getID().equals("60363e6bb25c6932d02dca5a")).collect(Collectors.toList());

                    Post item = result.get(0);
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("post", item);
                    intent.putParcelableArrayListExtra("reply", item.getComments());
                    intent.putExtra("position", position);
                    context.startActivity(intent);
                }else if(position==1){
                    List<Notice> result = MainActivity.NoticeArrayList.stream().filter(a -> a.getID().equals("603bc733f8853d5f37d4e67e")).collect(Collectors.toList());

                    Notice item = result.get(0);
                    Intent intent = new Intent(context, NoticeDetailActivity.class);
                    intent.putExtra("post", item);
                    intent.putExtra("position", position);
                    context.startActivity(intent);
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