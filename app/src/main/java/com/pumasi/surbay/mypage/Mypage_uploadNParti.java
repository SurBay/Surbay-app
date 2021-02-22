package com.pumasi.surbay.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.pumasi.surbay.PostDetailActivity;
import com.pumasi.surbay.R;
import com.pumasi.surbay.adapter.ListViewAdapter;
import com.pumasi.surbay.classfile.Post;

import java.util.ArrayList;

public class Mypage_uploadNParti extends AppCompatActivity {

    TextView upNpar_title;
    TextView upNpar_1st;
    TextView upNpar_2nd;
    TextView upNpar_3rd;
    ListView upNpar_listview;
    ListViewAdapter upNpar_listAdapter;

    int what;
    ArrayList upNpar_list;


    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_upload_n_parti);

        this.getSupportActionBar().hide();
        back = findViewById(R.id.uploadNparti_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        upNpar_title = findViewById(R.id.upNPar_title);
        upNpar_1st = findViewById(R.id.upNPar_1st_text);
        upNpar_2nd = findViewById(R.id.upNPar_2nd_text);
        upNpar_3rd = findViewById(R.id.upNPar_3rd_text);
        upNpar_listview = findViewById(R.id.upNPar_list);

        Intent intent = getIntent();
        what = intent.getIntExtra("what", 0);
        upNpar_list = intent.getParcelableArrayListExtra("list");

        upNpar_2nd.setText(upNpar_list.size() + "개");

        switch (what){
            case 0:
                break;
            case 1:
                upNpar_title.setText("참여한 설문");
                upNpar_1st.setText("벌써 ");
                upNpar_3rd.setText(" 참여했어요");
                break;
            case 2:
                upNpar_title.setText("받은 경품");
                upNpar_1st.setText("받은 경품이");
                upNpar_3rd.setText("있어요");
                break;
        }
        upNpar_listAdapter = new ListViewAdapter(upNpar_list);
        upNpar_listview.setAdapter(upNpar_listAdapter);

        upNpar_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //클릭시 글 확대 기능 추가 예정
                Post item = (Post) upNpar_listAdapter.getItem(position);
                Intent intent = new Intent(Mypage_uploadNParti.this, PostDetailActivity.class);
                intent.putExtra("post", item);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }
}