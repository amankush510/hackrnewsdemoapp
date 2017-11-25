package com.example.hackernews;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hackernews.utils.Utils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private GoogleSignInOptions googleSignInOptions;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneVerificationCallback;

    private GoogleApiClient googleApiClient;
    private FirebaseAuth auth;

    private String otp;

    private EditText et_phone_number;
    private EditText et_password;
    private Button but_login, but_get_otp;
    private ImageButton ib_google_sign_in;

    private static final int GOOGLE_SIGN_IN_CODE = 1001;

    private ProgressDialog loginProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initUI();
        initUIActions();
    }

    private void init(){
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
            .build();


        phoneVerificationCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                Log.e("MainActivity", "Verification Completed");
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.e("MainActivity", "Verification Failed");
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                otp = verificationId;
                updateUI();
            }
        };
        auth = FirebaseAuth.getInstance();
        loginProgressBar = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        loginProgressBar.setIndeterminate(true);
        loginProgressBar.setMessage("Signing In...");
        loginProgressBar.setCancelable(false);
    }

    private void initUI(){
        et_phone_number = (EditText)findViewById(R.id.et_phone_number);
        et_password = (EditText)findViewById(R.id.et_password);
        but_login = (Button) findViewById(R.id.but_login);
        but_get_otp = (Button) findViewById(R.id.but_get_otp);
        ib_google_sign_in = (ImageButton) findViewById(R.id.ib_google_sign_in);
    }

    private void initUIActions(){
        but_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_password.getText() != null) {
                    loginProgressBar.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otp, et_password.getText().toString());
                    signInWithPhoneNumber(credential);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter your OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });

        but_get_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_phone_number.getText() != null && et_phone_number.getText().toString().startsWith("+")) {
                    startPhoneNumberLogin();
                } else {
                    Toast.makeText(MainActivity.this, "Please enter your phone number preceded by Country code", Toast.LENGTH_SHORT).show();
                }
            }
        });


        ib_google_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utils.isDataConnectivityOn(MainActivity.this)) {
                    loginProgressBar.show();
                    signInWithGoogle();
                } else {
                    Toast.makeText(MainActivity.this, "Please check your Internet Connection and try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                authenticateWithFirebase(account);
            } else {

            }
        }
    }

    private void authenticateWithFirebase(GoogleSignInAccount account){
        AuthCredential credentials = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credentials)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            redirectToArticles();
                        } else{
                            Toast.makeText(MainActivity.this, "Login Unsuccessful...Please try again", Toast.LENGTH_SHORT).show();
                        }
                        loginProgressBar.cancel();
                    }
                });
    }

    private void signInWithPhoneNumber(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            redirectToArticles();
                        } else {
                            Toast.makeText(MainActivity.this, "Login Unsuccessful...Please try again", Toast.LENGTH_SHORT).show();
                        }
                        loginProgressBar.cancel();
                    }
                });
    }

    private void updateUI(){
        et_password.setVisibility(View.VISIBLE);
        but_get_otp.setVisibility(View.GONE);
        but_login.setVisibility(View.VISIBLE);
    }

    private void startPhoneNumberLogin(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(et_phone_number.getText().toString(), 120, TimeUnit.SECONDS, this, phoneVerificationCallback);
    }

    private void redirectToArticles(){
        Intent in = new Intent(this, ArticlesListActivity.class);
        startActivity(in);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
