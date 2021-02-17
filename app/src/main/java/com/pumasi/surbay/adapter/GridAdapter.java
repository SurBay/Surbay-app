package com.pumasi.surbay.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.R;

import java.util.ArrayList;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.MyViewHolder> {
    private static GridAdapter.OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드

    private LayoutInflater inflater;
    private ArrayList<Bitmap> imageModelArrayList;

    public GridAdapter(Context ctx, ArrayList<Bitmap> imageModelArrayList) {
        inflater = LayoutInflater.from(ctx);
        this.imageModelArrayList = imageModelArrayList;
    }


    public int getCount() {
        return (null != imageModelArrayList) ? imageModelArrayList.size() : 0;
    }


    public Object getItem(int position) {
        return (null != imageModelArrayList) ? imageModelArrayList.get(position) : 0;
    }

    public long getItemId(int position) {
        return position;
    }


    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    public void setOnItemClickListener(GridAdapter.OnItemClickListener listener) {
        this.mListener = listener ;
    }

    @Override
    public GridAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.image_listitem, parent, false);
        GridAdapter.MyViewHolder holder = new GridAdapter.MyViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(GridAdapter.MyViewHolder holder, int position) {
        Bitmap bm;

        bm = imageModelArrayList.get(position);
        bm = Bitmap.createScaledBitmap(bm, 320, 320, false);
        holder.imageView.setImageBitmap(bm);


        //---------------------------------------------------------------
        // GridView 뷰를 구성할 ImageView 뷰들을 정의합니다.
        // 뷰에 지정할 이미지는 앞에서 정의한 비트맵 객체입니다.


        //---------------------------------------------------------------
        // 지금은 사용하지 않는 코드입니다.

        //imageView.setMaxWidth(320);
        //imageView.setMaxHeight(240);
        //imageView.setImageResource(imageIDs[position]);

        //---------------------------------------------------------------
        // 사진 항목들의 클릭을 처리하는 ImageClickListener 객체를 정의합니다.
        // 그리고 그것을 ImageView의 클릭 리스너로 설정합니다.
    }

    @Override
    public int getItemCount() {
        return imageModelArrayList.size();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageitemview);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });
        }

    } /* extends BaseAdapter {

    Context context = null;

    //-----------------------------------------------------------
    // imageIDs는 이미지 파일들의 리소스 ID들을 담는 배열입니다.
    // 이 배열의 원소들은 자식 뷰들인 ImageView 뷰들이 무엇을 보여주는지를
    // 결정하는데 활용될 것입니다.

    private ArrayList<URL> imageModelArrayList;

    public GridAdapter(Context context, ArrayList<URL> imageModelArrayList) {
        this.context = context;
        this.imageModelArrayList = imageModelArrayList;
    }


    public int getCount() {
        return (null != imageModelArrayList) ? imageModelArrayList.size() : 0;
    }


    public Object getItem(int position) {
        return (null != imageModelArrayList) ? imageModelArrayList.get(position) : 0;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = convertView.findViewById(R.id.gridimageitemview);
        Bitmap bm;

        try {
            URL url = imageModelArrayList.get(position);
            URLConnection conn = url.openConnection();
            conn.connect();
            BufferedInputStream bis = new
                    BufferedInputStream(conn.getInputStream());
            bm = BitmapFactory.decodeStream(bis);
            bm = Bitmap.createScaledBitmap(bm, 320, 320, false);
            bis.close();
            imageView.setImageBitmap(bm);
        } catch (IOException e) {
        }


        //---------------------------------------------------------------
        // GridView 뷰를 구성할 ImageView 뷰들을 정의합니다.
        // 뷰에 지정할 이미지는 앞에서 정의한 비트맵 객체입니다.


        //---------------------------------------------------------------
        // 지금은 사용하지 않는 코드입니다.

        //imageView.setMaxWidth(320);
        //imageView.setMaxHeight(240);
        //imageView.setImageResource(imageIDs[position]);

        //---------------------------------------------------------------
        // 사진 항목들의 클릭을 처리하는 ImageClickListener 객체를 정의합니다.
        // 그리고 그것을 ImageView의 클릭 리스너로 설정합니다.

        ImageClickListener imageViewClickListener
                = new ImageClickListener(context, imageModelArrayList.get(position));
        imageView.setOnClickListener(imageViewClickListener);

        return imageView;
    }*/

}