package com.pumasi.surbay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

public class RecyclerViewDdayAdapter extends RecyclerView.Adapter<RecyclerViewDdayAdapter.MyViewDdayHolder> {
    private RecyclerViewDdayAdapter.OnItemClickListener aListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드

    private LayoutInflater inflater;
    private ArrayList<Post> imageModelArrayList;


    public RecyclerViewDdayAdapter(Context ctx, ArrayList<Post> imageModelArrayList){
        inflater = LayoutInflater.from(ctx);
        this.imageModelArrayList = imageModelArrayList;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    public void setOnItemClickListener(RecyclerViewDdayAdapter.OnItemClickListener listener) {
        this.aListener = listener ;
    }

    @Override
    public RecyclerViewDdayAdapter.MyViewDdayHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.home_recycler_item, parent, false);
        RecyclerViewDdayAdapter.MyViewDdayHolder holder = new RecyclerViewDdayAdapter.MyViewDdayHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewDdayAdapter.MyViewDdayHolder holder, int position) {


        holder.title.setText(imageModelArrayList.get(position).getTitle());
        holder.content.setText(imageModelArrayList.get(position).getContent());
        int dday = calc_dday(imageModelArrayList.get(position).getDeadline());
        Date now = new Date();

        if (dday <= 3 && dday >= 0) {
            if (dday == 0) {
                holder.participate.setText("D-Day");
            } else {
                holder.participate.setText("D-" + (dday));
            }
        } else if(now.after(imageModelArrayList.get(position).getDeadline()) || imageModelArrayList.get(position).isDone()){
            holder.participate.setVisibility(View.VISIBLE);
            holder.participate.setText("종료");
        }else {
            holder.participate.setVisibility(View.INVISIBLE);
        }

        if(UserPersonalInfo.participations.contains(imageModelArrayList.get(position).getID())&&!imageModelArrayList.get(position).getAuthor_userid().equals(UserPersonalInfo.userID)){
            holder.participated.setVisibility(View.VISIBLE);
        }else{
            holder.participated.setVisibility(View.INVISIBLE);
        }

        /*
        if ("D-day".equals(somber)) {

        } else if ("Goal".equals(somber)) {
            holder.participate.setText((100 * imageModelArrayList.get(position).getParticipants() / imageModelArrayList.get(position).getGoal_participants()) + "%");
        } else if ("New".equals(somber)) {
            int diff = calc_dday(imageModelArrayList.get(position).getDate());

            if (diff <= 0 && diff >= -1) {

                Log.d("recyc", diff + " ");

                holder.participate.setText("New");
            } else {
                holder.participate.setVisibility(View.INVISIBLE);
            }
        } else if ("Not".equals(somber)) {
            holder.participate.setVisibility(View.INVISIBLE);
        }
*/

    }

    @Override
    public int getItemCount() {
        return imageModelArrayList.size();
    }

    public Object getItem(int position) {
        return imageModelArrayList.get(position);
    }

    class MyViewDdayHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView content;
        TextView participate;
        TextView participated;

        public MyViewDdayHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            content = (TextView) itemView.findViewById(R.id.content);
            participate = (TextView) itemView.findViewById(R.id.recyc_participants);
            participated = (TextView) itemView.findViewById(R.id.recyc_participated);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (aListener != null) {
                            aListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });
        }

    }

    public int calc_dday(Date goal){
        Date dt = new Date();

        long diff = 0;
        int dday = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MM yyyy");
            SimpleDateFormat fm = new SimpleDateFormat("dd MM yyyy");
            LocalDate date1 = LocalDate.parse(fm.format(goal), dtf);
            LocalDate date2 = LocalDate.parse(fm.format(dt), dtf);
            diff = ChronoUnit.DAYS.between(date2, date1);
            dday = (int) diff;
        }
        else{
            diff = goal.getDate()-dt.getDate();
            dday = (int) diff;
        }
        return dday;
    }
}

