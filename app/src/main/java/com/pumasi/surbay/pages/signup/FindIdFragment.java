package com.pumasi.surbay.pages.signup;

import android.content.Context;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.CustomDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class FindIdFragment extends Fragment {

    private View view;
    EditText findid_PNedit;
    EditText findid_PNCedit;
    TextView findid_AB;
    TextView findid_timer;
    TextView findid_PNCtext;
    Button findid_button;

    String getverificationId;
    FirebaseAuth auth;
    CountDownTimer CDT;
    String typesmsCode;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private Boolean phone_check = false;

    String id;
    String phone;
    private String mVerificationId;

    public static FindIdFragment newInstance() {
        return new FindIdFragment();
    }

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

        findid_AB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = findid_PNedit.getText().toString();

                if (phone.length() == 11){
                    String realphone = "+82"+phone.substring(1);
                    Log.d("phone", "num is "+ realphone);

                    CustomDialog customDialog = new CustomDialog(getActivity(), null);
                    customDialog.show();
                    customDialog.setMessage("??????????????? ?????????????????????.");
                    customDialog.setNegativeButton("??????");

                    PhoneAuth(realphone);
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), "????????? ????????? ??????????????????", Toast.LENGTH_SHORT);
                }
//                String realphone = "+82 "+phone.substring(1, 3)+"-"+phone.substring(3,7)+"-"+phone.substring(7,11);

            }
        });
        findid_PNCedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                typesmsCode = findid_PNCedit.getText().toString();
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
                                        findid_PNCtext.setText("????????? ?????????????????????");
                                        phone_check = true;
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.d("signupauth", "signInWithCredential:failefail");

                                        findid_PNCtext.setText("??????????????? ???????????? ????????????.");
                                        // ...
                                    }
                                }
                            });
                }
            }
        });
        findid_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone_check){
                    getID(phone);
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    public void TimerStart(){
        findid_timer.setVisibility(View.VISIBLE);
        findid_PNCtext.setVisibility(View.VISIBLE);
        CDT = new CountDownTimer(180*1000, 1000) {
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
                findid_PNCtext.setText("?????? ????????? ???????????????. ?????? ????????? ?????? ????????????");
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
                        Toast.makeText(getActivity().getApplicationContext(), "??????????????? ??????????????????", Toast.LENGTH_SHORT);
                        TimerStart();
                        findid_PNCedit.setEnabled(true);
                        findid_AB.setEnabled(false);
                        findid_PNedit.setEnabled(false);
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
                        findid_AB.setEnabled(true);
                        findid_PNedit.setEnabled(true);

                        //sometime the code is not detected automatically
                        //in this case the code will be null
                        //so user has to manually enter the code
                        if (code != null) {
                            findid_PNCedit.setText(code);
                            //verifying the code
                            verifyVerificationCode(code);
                        }

                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        // This callback is invoked in an invalid request for verification is made,
                        // for instance if the the phone number format is not valid.
                        Log.w("phone auth", "onVerificationFailed", e);
                        findid_AB.setEnabled(true);
                        findid_PNedit.setEnabled(true);

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

    private void getID(String phoneNumber) {
        String requestURL = getString(R.string.server)+"/api/users/finduserid?phonenumber="+phoneNumber;
        try{
            RequestQueue requestQueue = Volley.newRequestQueue((AppCompatActivity)getActivity());
            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest
                    (Request.Method.GET, requestURL, null, response -> {
                        try {
                            JSONObject res = new JSONObject(response.toString());
                            Log.d("response is", ""+response);
                            id = res.getString("userID");
                            if (id != null){

                                CustomDialog customDialog = new CustomDialog(getActivity(), null);
                                customDialog.show();
                                customDialog.setMessage("?????????: "+ idchange(id));
                                customDialog.setNegativeButton("??????");
                            } else {

                                CustomDialog customDialog = new CustomDialog(getActivity(), null);
                                customDialog.show();
                                customDialog.setMessage("???????????? ???????????? ????????????");
                                customDialog.setNegativeButton("??????");
                            }
                        } catch (JSONException e) {
                            Log.d("exception", "JSON error");
                            e.printStackTrace();
                        }
                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
        }
    }

    public String idchange(String id){
        String[] idlist = id.split("@");
        String forid = idlist[0];
        String backid = idlist[1];
        forid = forid.substring(0, forid.length()-3)+"***";

        return forid+"@"+backid;
    }
}
