package com.pumasi.surbay.adapter;

import android.content.Context;

import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.GeneralWriteActivity;
import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.General;
import com.pumasi.surbay.classfile.Poll;

import java.util.ArrayList;

public class PollWriteAdapter extends RecyclerView.Adapter<PollWriteAdapter.MyViewHolder> {
    private static OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드

    private LayoutInflater inflater;
    private Context ctx;
    private ArrayList<String> contents = new ArrayList<>();

    public PollWriteAdapter(Context ctx){
        inflater = LayoutInflater.from(ctx);
        this.ctx = ctx;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    @Override
    public PollWriteAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.general_write_poll_recyclerview_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view, new MyCustomEditTextListener());

        return holder;
    }

    @Override
    public void onBindViewHolder(PollWriteAdapter.MyViewHolder holder, int position) {
        if(GeneralWriteActivity.image_uris.get(position)!=null){
            DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
            holder.add_pic.setLayoutParams(new LinearLayout.LayoutParams(
                    Math.round(63 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)),
                    Math.round(63 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))));
            holder.add_pic.setImageURI(GeneralWriteActivity.image_uris.get(position));
        }else{
            holder.add_pic.setImageDrawable(ctx.getDrawable(R.drawable.poll_add_pic));
        }

        holder.add_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralWriteActivity.goToAlbum(ctx, position);
            }
        });
        Log.d("arrayis", ""+GeneralWriteActivity.write_polls);
        Log.d("settingtext", ""+position+GeneralWriteActivity.write_polls.get(position));
        holder.myCustomEditTextListener.updatePosition(holder.getAdapterPosition());
        if(GeneralWriteActivity.write_polls.get(position).length()!=0){
            holder.content.setText(GeneralWriteActivity.write_polls.get(position));
        }
        else{
            holder.content.setText("");
        }


        if(position>1){
            holder.erase_poll.setVisibility(View.VISIBLE);
        }else{
            holder.erase_poll.setVisibility(View.GONE);
        }
        holder.erase_poll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("arraysizeis", ""+GeneralWriteActivity.write_polls.size()+"   "+position);
                try{
                    holder.content.clearFocus();
                    GeneralWriteActivity.write_polls.remove(position);
                    GeneralWriteActivity.image_uris.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                }catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return GeneralWriteActivity.write_polls.size();
    }

    public Object getItem(int position) {
        return GeneralWriteActivity.write_polls.get(position);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        EditText content;
        ImageButton add_pic;
        ImageButton erase_poll;
        MyCustomEditTextListener myCustomEditTextListener;
        public MyViewHolder(View itemView, MyCustomEditTextListener myCustomEditTextListener) {
            super(itemView);

            erase_poll = itemView.findViewById(R.id.erase_poll);
            content = itemView.findViewById(R.id.content);
            add_pic = itemView.findViewById(R.id.add_pic);

            content.addTextChangedListener(myCustomEditTextListener);
            this.myCustomEditTextListener = myCustomEditTextListener;


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

    }
    private class MyCustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            GeneralWriteActivity.write_polls.set(position, charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }


}

