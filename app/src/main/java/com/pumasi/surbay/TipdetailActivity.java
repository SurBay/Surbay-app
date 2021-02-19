package com.pumasi.surbay;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.classfile.Surveytip;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TipdetailActivity extends AppCompatActivity {
    static final int LIKED = 5;
    static final int DISLIKED = 4;
    int LIKE_CHANGE = 0;
    int ORIGIN_LIKE = 0;

    TextView author;
    TextView level;
    TextView title;
    TextView category;
    TextView content;
    LinearLayout likesbutton;
    TextView likescount;

    Surveytip surveytip;
    ArrayList<String> likedlist;
    private AlertDialog dialog;

    boolean likedselected;
    private int position;

    private static final String TAG_TEXT = "text";
    private static final String TAG_IMAGE = "image";
    String[] text = {" 카카오톡으로 공유하기 "};
    int[] image = {R.drawable.kakaotalk};
    List<Map<String, Object>> dialogItemList;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipdetail);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        surveytip = intent.getParcelableExtra("post");
        position = intent.getIntExtra("position", 0);

        author = findViewById(R.id.tipdetail_author);
        level = findViewById(R.id.tipdetail_level);
        title = findViewById(R.id.tipdetail_title);
        category = findViewById(R.id.tipdetail_category);
        content = findViewById(R.id.tipdetail_content);
        likesbutton = findViewById(R.id.tipdetail_likesbutton);
        likescount = findViewById(R.id.tipdetail_likes);

        dialogItemList = new ArrayList<>();

        for(int i=0;i<image.length;i++)
        {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put(TAG_IMAGE, image[i]);
            itemMap.put(TAG_TEXT, text[i]);

            dialogItemList.add(itemMap);
        }

        likedlist = new ArrayList<>(surveytip.getLiked_users());
        author.setText(surveytip.getAuthor());
        level.setText(surveytip.getAuthor_lvl().toString());
        title.setText(surveytip.getTitle());
        category.setText(surveytip.getCategory());
        content.setText(surveytip.getContent());
        likescount.setText(surveytip.getLikes().toString());
        if (likedlist.contains(UserPersonalInfo.userID)){
            likedselected = true;
            ORIGIN_LIKE = LIKED;
        } else {
            likedselected = false;
            ORIGIN_LIKE = DISLIKED;
        }
        setLikesbutton();

        likesbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (likedselected){
                    LIKE_CHANGE = DISLIKED;
                    likescount.setText((Integer.valueOf(likescount.getText().toString())-1)+"");
                } else {
                    LIKE_CHANGE = LIKED;
                    likescount.setText((Integer.valueOf(likescount.getText().toString())+1)+"");
                }
                likedselected = !likedselected;
                setLikesbutton();
            }
        });

        Intent resultIntent = new Intent(getApplicationContext(), BoardFragment2.class);
        resultIntent.putExtra("position", position);
        setResult(RESULT_OK, resultIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @SuppressLint("ResourceAsColor")
    public void setLikesbutton(){
        if (likedselected){
            likesbutton.setBackgroundResource(R.drawable.round_border_teal_list);
            likescount.setTextColor(R.color.teal_200);
        } else {
            likesbutton.setBackgroundResource(R.drawable.round_border_gray_list);
            likescount.setTextColor(R.color.gray2);
        }
    }


    public void likepost(){
        String requestURL = getString(R.string.server)+"/api/surveytips/like/"+surveytip.getID();
        try{
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("userID",UserPersonalInfo.userID);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, response -> {
                        Log.d("response is", ""+response);

                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed posting");
            e.printStackTrace();
        }
    }

    public void dislikepost(){
        String requestURL = getString(R.string.server)+"/api/surveytips/dislike/"+surveytip.getID();
        try{
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("userID",UserPersonalInfo.userID);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, response -> {
                        Log.d("response is", ""+response);

                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed posting");
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.post_detail_bar, menu);
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);
        menu.getItem(2).setVisible(false);

        menu.getItem(3).setVisible(true);
        menu.getItem(4).setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if(ORIGIN_LIKE==LIKED && LIKE_CHANGE==DISLIKED){
            dislikepost();
            surveytip.setLikes(surveytip.getLikes()-1);
            surveytip.getLiked_users().remove(UserPersonalInfo.userID);
        }
        else if(ORIGIN_LIKE==DISLIKED && LIKE_CHANGE==LIKED){
            likepost();
            surveytip.setLikes(surveytip.getLikes()+1);
            surveytip.getLiked_users().add(UserPersonalInfo.userID);
        }
        Intent intent = new Intent(TipdetailActivity.this, BoardFragment2.class);
        intent.putExtra("position", position);
        intent.putExtra("surveyTip", surveytip);

        Log.d("surveytip date", "date is"+ surveytip.getDate());
        setResult(0, intent);
        finish();
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if(ORIGIN_LIKE==LIKED && LIKE_CHANGE==DISLIKED){
                    dislikepost();
                    surveytip.setLikes(surveytip.getLikes()-1);
                    surveytip.getLiked_users().remove(UserPersonalInfo.userID);
                }
                else if(ORIGIN_LIKE==DISLIKED && LIKE_CHANGE==LIKED){
                    likepost();
                    surveytip.setLikes(surveytip.getLikes()+1);
                    surveytip.getLiked_users().add(UserPersonalInfo.userID);
                }
                Intent intent = new Intent(TipdetailActivity.this, BoardFragment2.class);
                intent.putExtra("position", position);
                intent.putExtra("surveyTip", surveytip);

                Log.d("surveytip date", "date is"+ surveytip.getDate());
                setResult(0, intent);
                finish();
                break;
            case R.id.share:
                ShareDialog();
                break;
            case R.id.report:
                //select back button
                break;
            case R.id.fix:
                break;
            case R.id.remove:
                RemoveDialog();
                break;
            case R.id.note:
                break;
            case R.id.resultrequest:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void ShareDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(TipdetailActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.share_dialog, null);
        builder.setView(view);

        final ListView listview = (ListView)view.findViewById(R.id.listview_alterdialog_list);
        final AlertDialog dialog = builder.create();

        SimpleAdapter simpleAdapter = new SimpleAdapter(TipdetailActivity.this, dialogItemList,
                R.layout.share_listitem,
                new String[]{TAG_IMAGE, TAG_TEXT},
                new int[]{R.id.share_item_image, R.id.share_item_name});

        listview.setAdapter(simpleAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent Sharing_intent = new Intent(Intent.ACTION_SEND);
                        Sharing_intent.setType("text/plain");

                        Intent Sharing = Intent.createChooser(Sharing_intent, "공유하기");
                        startActivity(Sharing);
                        dialog.dismiss();
                        break;
                }
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = 800;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        Window window = dialog.getWindow();
        window.setAttributes(lp);
    }

    private void RemoveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TipdetailActivity.this);
        dialog = builder.setMessage("설문을 삭제하겠습니까?")
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which){

                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.teal_200);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.gray);
            }
        });
        dialog.show();
    }




}