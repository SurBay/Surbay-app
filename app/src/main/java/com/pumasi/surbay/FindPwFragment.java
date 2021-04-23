package com.pumasi.surbay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.pumasi.surbay.classfile.CustomDialog;

import org.json.JSONException;
import org.json.JSONObject;

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
    private CustomDialog customDialog;
    String id;
    String confirm_email;
    String email;
    private String mVerificationId;

    public static FindPwFragment newInstance() {
        return new FindPwFragment();
    }
    Boolean confirmed = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.findpw_fragment,container,false);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        findpw_PNedit = view.findViewById(R.id.find_pw_email);
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

        Intent intent = getActivity().getIntent();
        confirmed = intent.getBooleanExtra("confirmed", false);
        if(confirmed==true){
            FragmentTransaction trans = getFragmentManager().beginTransaction();
            trans.replace(R.id.fragment_root, ChangePwFragment.newInstance());
            trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            trans.addToBackStack(null);
            trans.commit();
            SharedPreferences auto = getActivity().getSharedPreferences("auto", Activity.MODE_PRIVATE);
            confirm_email = auto.getString("pwdchangeemail", null);
            if(confirm_email!=null){
                ChangePwActivity.email = confirm_email;
                findpw_PNedit.setText(confirm_email);
            }
        }

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_MOVE) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (getActivity().getCurrentFocus() != null)
                        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    return true;
                }
                return true;
            }
        });

        findpw_PAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm_email = findpw_PNedit.getText().toString();

                Log.d("confirmemailis", confirm_email);

                if (confirm_email.length() != 0){
                    ActionCodeSettings actionCodeSettings =
                            ActionCodeSettings.newBuilder()
                                    // URL you want to redirect back to. The domain (www.example.com) for this
                                    // URL must be whitelisted in the Firebase Console.
                                    .setUrl("http://ec2-13-209-96-165.ap-northeast-2.compute.amazonaws.com:3000/passwordchange/?email=" + confirm_email)
                                    // This must be true
                                    .setHandleCodeInApp(true)
                                    .setAndroidPackageName(
                                            "com.pumasi.surbay",
                                            true, /* installIfNotAvailable */
                                            "29"    /* minimumVersion */)
                                    .build();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.sendSignInLinkToEmail(confirm_email, actionCodeSettings)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("email", "Email sent.");
                                    }
                                }
                            });

                    CustomDialog customDialog = new CustomDialog(getActivity(), null);
                    customDialog.show();
                    customDialog.setMessage("비밀번호 재설정 링크가 발송되었습니다");
                   customDialog.setNegativeButton("확인");
                    SharedPreferences auto = getActivity().getSharedPreferences("auto", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor autoLogin = auto.edit();
                    autoLogin.putString("pwdchangeemail", confirm_email);
                    autoLogin.commit();
                   ChangePwActivity.email = confirm_email;

                    FragmentTransaction trans = getFragmentManager().beginTransaction();
                    trans.replace(R.id.fragment_root, ChangePwFragment.newInstance());
                    trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    trans.addToBackStack(null);
                    trans.commit();

                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), "이메일을 확인해주세요", Toast.LENGTH_SHORT);
                }
            }
        });
        findpw_PNCedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {            }
            @Override
            public void afterTextChanged(Editable s) {
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
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.d("signupauth", "signInWithCredential:failefail");

                                        findpw_PNCtext.setText("인증번호가 일치하지 않습니다");
                                        // ...
                                    }
                                }
                            });
                }
            }
        });
                /*setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
        });*/
        findpw_PB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone_check){
                    FragmentTransaction trans = getFragmentManager().beginTransaction();
                    trans.replace(R.id.fragment_root, ChangePwFragment.newInstance());
                    trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    trans.addToBackStack(null);
                    trans.commit();
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
        PhoneAuthOptions.Builder optionsBuilder = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(120L, TimeUnit.SECONDS)
                .setActivity(getActivity())
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        // Save the verification id somewhere
                        // ...

                        // The corresponding whitelisted code above should be used to complete sign-in.
                        Log.d("signupauth", forceResendingToken.toString() + verificationId);
                        Log.d("signupauth", "onCodeSent:" + verificationId);
                        mVerificationId = verificationId;
                        Toast.makeText(getActivity().getApplicationContext(), "인증번호를 발송했습니다", Toast.LENGTH_SHORT);
                        PTimerStart();
                        findpw_PNCedit.setEnabled(true);
                        findpw_PAB.setEnabled(false);
                        findpw_PNedit.setEnabled(false);
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
                        String code = credential.getSmsCode();
                        findpw_PAB.setEnabled(true);
                        findpw_PNedit.setEnabled(true);

                        //sometime the code is not detected automatically
                        //in this case the code will be null
                        //so user has to manually enter the code
                        if (code != null) {
                            findpw_PNCedit.setText(code);
                            //verifying the code
                            verifyVerificationCode(code);
                        }

                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        // This callback is invoked in an invalid request for verification is made,
                        // for instance if the the phone number format is not valid.
                        Log.w("phone auth", "onVerificationFailed", e);
                        findpw_PAB.setEnabled(true);
                        findpw_PNedit.setEnabled(true);

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
                });

        if(mResendToken!=null) optionsBuilder.setForceResendingToken(mResendToken);
        PhoneAuthOptions options = optionsBuilder.build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void verifyVerificationCode(String otp) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential);
    }


    private void phoneCheck() throws Exception{
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String requestURL = getString(R.string.server)+"/phonenumbers/duplicate?phoneNumber=" + confirm_email;
            JSONObject params = new JSONObject();
            params.put("phoneNumber", confirm_email);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, requestURL, params, response -> {
                        Log.d("response is", "" + response);
                        try {
                            JSONObject nameObj = new JSONObject(response.toString());
                            Boolean success = nameObj.getBoolean("type");
                            if (!success) {
                                String realphone = "+82"+ confirm_email.substring(1);
                                Log.d("phone", "num is "+ realphone);

                                customDialog = new CustomDialog(getActivity(), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        customDialog.dismiss();
                                    }
                                });
                                customDialog.show();
                                customDialog.setMessage("인증번호를 발송했습니다");
                                customDialog.setPositiveButton("확인");
                                customDialog.hideNegativeButton(true);
                                PhoneAuth(realphone);
                            } else {
                                customDialog = new CustomDialog(getActivity(), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        customDialog.dismiss();
                                    }
                                });
                                customDialog.show();
                                customDialog.setMessage("가입하지 않은 번호입니다.");
                                customDialog.setPositiveButton("확인");
                                customDialog.hideNegativeButton(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
