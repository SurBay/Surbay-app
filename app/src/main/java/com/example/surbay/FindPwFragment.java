package com.example.surbay;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FindPwFragment extends Fragment {

    private View view;
    EditText findpw_PNedit;
    EditText findpw_PNCedit;
    TextView findpw_PAB;
    TextView findpw_Ptimer;
    TextView findpw_PNCtext;
    Button findpw_PB;
    EditText findpw_Eedit;
    EditText findpw_ECedit;
    TextView findpw_Etimer;
    TextView findpw_ECtext;
    TextView findpw_EAB;
    Button findpw_EB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.findpw_fragment,container,false);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        findpw_PNedit = view.findViewById(R.id.find_pw_phonenumber);
        findpw_PAB = view.findViewById(R.id.find_pw_PAB);
        findpw_PNCedit = view.findViewById(R.id.find_pw_PCN);
        findpw_Ptimer = view.findViewById(R.id.findpw_Ptimer);
        findpw_PNCtext = view.findViewById(R.id.findpw_PNCtext);
        findpw_PB = view.findViewById(R.id.find_pw_PB);
        findpw_Eedit = view.findViewById(R.id.find_pw_emailnumber);
        findpw_EAB = view.findViewById(R.id.find_pw_EAB);
        findpw_ECedit = view.findViewById(R.id.find_pw_ECN);
        findpw_Etimer = view.findViewById(R.id.findpw_Etimer);
        findpw_ECtext = view.findViewById(R.id.findpw_ECtext);
        findpw_EB = view.findViewById(R.id.find_pw_EB);

        findpw_PAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = findpw_PNedit.getText().toString();
                if (phone.length() == 11){
                    findpw_Ptimer.setVisibility(View.VISIBLE);
                    findpw_PNCtext.setVisibility(View.VISIBLE);
                    CountDownTimer CDT = new CountDownTimer(180*1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long totalsec = millisUntilFinished / 1000;
                            long min = totalsec/60;
                            long sec = totalsec - min*60;


                            String minstr = String.format("%02d", min);
                            String secstr = String.format("%02d", sec);
                            findpw_Ptimer.setText(minstr+":"+secstr);
                        }

                        @Override
                        public void onFinish() {
                            findpw_PNCtext.setText("유효 시간이 지났습니다. 다시 코드를 발급 받으세요");
                        }
                    };
                    CDT.start();
                } else {
                }

            }
        });
        findpw_PB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        findpw_EAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = findpw_Eedit.getText().toString();
                if (email.contains("@korea.ac.kr")){
                    findpw_Etimer.setVisibility(View.VISIBLE);
                    findpw_ECtext.setVisibility(View.VISIBLE);
                    CountDownTimer CDT = new CountDownTimer(180*1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long totalsec = millisUntilFinished / 1000;
                            long min = totalsec/60;
                            long sec = totalsec - min*60;


                            String minstr = String.format("%02d", min);
                            String secstr = String.format("%02d", sec);
                            findpw_Etimer.setText(minstr+":"+secstr);
                        }

                        @Override
                        public void onFinish() {
                            findpw_ECtext.setText("유효 시간이 지났습니다. 다시 코드를 발급 받으세요");
                        }
                    };
                    CDT.start();
                } else {
                }
            }
        });
        findpw_EB.setOnClickListener(new View.OnClickListener() {
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
