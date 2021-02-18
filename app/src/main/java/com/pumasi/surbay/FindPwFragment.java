package com.pumasi.surbay;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

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


    String getverificationId;
    FirebaseAuth auth;
    CountDownTimer CDT;
    String typesmsCode;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private Boolean phone_check = false;

    String id;
    String phone;
    String email;

    public static FindPwFragment newInstance() {
        return new FindPwFragment();
    }

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
                phone = findpw_PNedit.getText().toString();

                if (phone.length() == 11){
                    String realphone = "+82"+phone.substring(1);
                    Log.d("phone", "num is "+ realphone);
                    PhoneAuth(realphone);
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), "휴대폰 번호를 확인해주세요", Toast.LENGTH_SHORT);
                }
            }
        });
        findpw_PNCedit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    typesmsCode = findpw_PNCedit.getText().toString();
                    Log.d("code is", "sms code" + typesmsCode);
                    if (typesmsCode.length() == 6){
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(getverificationId, typesmsCode);
                        Task<AuthResult> result = auth.signInWithCredential(credential)
                                .addOnCompleteListener((AppCompatActivity) getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            FirebaseUser user = task.getResult().getUser();
                                            Log.d("signupauth", "signInWithCredential:success");
                                            CDT.cancel();
                                            findpw_PNCtext.setText("인증이 완료되었습니다");
                                            phone_check = true;
                                            FindIdActivity.phone = phone;
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.d("signupauth", "signInWithCredential:failefail");

                                            findpw_PNCtext.setText("인증번호가 일치하지 않습니다.");
                                            // ...
                                        }
                                    }
                                });
                    }
                }
            }
        });
        findpw_PB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone_check){
                }
            }
        });
        findpw_EAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = findpw_Eedit.getText().toString();
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

    public void PTimerStart(){
        findpw_Ptimer.setVisibility(View.VISIBLE);
        findpw_PNCtext.setVisibility(View.VISIBLE);
        CDT = new CountDownTimer(180*1000, 1000) {
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
    }

    public void PhoneAuth(String phoneNumber){
//        phoneNumber = "+1 1231231234";
        auth = FirebaseAuth.getInstance();
//        FirebaseAuth.getInstance().getFirebaseAuthSettings().forceRecaptchaFlowForTesting(true);
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(120L, TimeUnit.SECONDS)
                .setActivity((AppCompatActivity)getActivity())
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        // Save the verification id somewhere
                        // ...

                        // The corresponding whitelisted code above should be used to complete sign-in.
                        Log.d("signupauth", forceResendingToken.toString() + verificationId);
                        Log.d("signupauth", "onCodeSent:" + verificationId);
                        PTimerStart();
                        findpw_PNCedit.setEnabled(true);
                        getverificationId = verificationId;
                        mResendToken = forceResendingToken;

                    }

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        // This callback will be invoked in two situations:
                        // 1 - Instant verification. In some cases the phone number can be instantly
                        //     verified without needing to send or enter a verification code.
                        // 2 - Auto-retrieval. On some devices Google Play services can automatically
                        //     detect the incoming verification SMS and perform verification without
                        //     user action.
                        Log.d("phone auth", "onVerificationCompleted:" + credential);

                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        // This callback is invoked in an invalid request for verification is made,
                        // for instance if the the phone number format is not valid.
                        Log.w("phone auth", "onVerificationFailed", e);

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            // ...
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            // ...
                        }

                        // Show a message and update the UI
                        // ...
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}
