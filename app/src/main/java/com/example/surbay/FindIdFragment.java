package com.example.surbay;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Timer;
import java.util.TimerTask;

public class FindIdFragment extends Fragment {

    private View view;
    EditText findid_PNedit;
    EditText findid_PNCedit;
    TextView findid_AB;
    TextView findid_timer;
    TextView findid_PNCtext;
    Button findid_button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.findid_fragment,container,false);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        findid_PNedit = view.findViewById(R.id.find_id_phonenumber);
        findid_AB = view.findViewById(R.id.find_id_AB);
        findid_PNCedit = view.findViewById(R.id.find_id_PCN);
        findid_button = view.findViewById(R.id.find_id_button);
        findid_timer = view.findViewById(R.id.findid_timer);
        findid_PNCtext = view.findViewById(R.id.findid_PCNtext);

        findid_AB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = findid_PNedit.getText().toString();
                if (phone.length() == 11){
                    findid_timer.setVisibility(View.VISIBLE);
                    findid_PNCtext.setVisibility(View.VISIBLE);
                    CountDownTimer CDT = new CountDownTimer(180*1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long totalsec = millisUntilFinished / 1000;
                            long min = totalsec/60;
                            long sec = totalsec - min*60;


                            String minstr = String.format("%02d", min);
                            String secstr = String.format("%02d", sec);
                            findid_timer.setText(minstr+":"+secstr);
                        }

                        @Override
                        public void onFinish() {
                            findid_PNCtext.setText("유효 시간이 지났습니다. 다시 코드를 발급 받으세요");
                        }
                    };
                    CDT.start();
                } else {
                }
            }
        });
        findid_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
