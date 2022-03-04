package com.example.dhobi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class activity_login extends AppCompatActivity {

    private static final int REQUEST_CODE_GOOGLE_SIGN_IN = 8788;
    private static final String TAG = "LOGIN";
    private EditText edtxt_email, edtxt_password;
    private Button Login;
    private SignInButton googlesignin;
    private TextView gotosignin;
    public static final String Shared_prefs = "sharedprefs";
    public static final String email = "email";
    public static final String password = "password";
    private String texte, textp;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_login_in);

        initialize ();
    }

    private void initialize(){
        edtxt_email = findViewById (R.id.edtxt_login_email);
        edtxt_password = findViewById (R.id.edtxt_login_password);
        Login = findViewById (R.id.btn_login);
        googlesignin = findViewById (R.id.btn_googlesignin_login_activity);
        gotosignin = findViewById (R.id.btn_gotosignup_fromlogin);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.googlesignin_WebClientID))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient (this, gso);
        edtxt_password.addTextChangedListener (new TextWatcher () {
            @Override
            public void beforeTextChanged (CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged (CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged (Editable editable) {
                savestate ();
            }
        });

        googlesignin_operation();
        signin();
        loadstate ();
        updatestate ();

    }

    private void signin () {
        Login.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                loginIntoAccount ();
            }
        });
    }

    private void googlesignin_operation() {
        googlesignin.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE_SIGN_IN);
            }
        });
    }

    private void firebaseAuthWithGoogle (String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult> () {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI (null);
                        }
                    }
                });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_CODE_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
                Toast.makeText(activity_login.this,"User logged in successfully.",Toast.LENGTH_SHORT).show();
                startActivity(new Intent (activity_login.this,Test.class));
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(activity_login.this,"Login Error: "+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                updateUI (null);
                edtxt_email.requestFocus();
                edtxt_password.requestFocus();
            }
        }
    }


    private void loginIntoAccount() {
        String email = edtxt_email.getText().toString();
        String password = edtxt_password.getText().toString();

        if (email.isEmpty()) {
            edtxt_email.setError("Enter E-mail");
            edtxt_email.requestFocus();
        }else if(password.isEmpty()){
            edtxt_password.setError("Enter Password");
            edtxt_password.requestFocus();
        }else{
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult> () {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(activity_login.this,"User logged in successfully.",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent (activity_login.this,Test.class));
                    }else{
                        Toast.makeText(activity_login.this,"Login Error: "+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        edtxt_email.requestFocus();
                        edtxt_password.requestFocus();
                    }
                }
            });
        }
    }

    public void savestate(){
        SharedPreferences sharedPreferences = getSharedPreferences (Shared_prefs, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit ();
        editor.putString (email, edtxt_email.getText ().toString ());
        editor.putString (password, edtxt_password.getText ().toString ());
        editor.apply ();
    }

    public void loadstate(){
        SharedPreferences sharedPreferences = getSharedPreferences (Shared_prefs, MODE_PRIVATE);
        texte = sharedPreferences.getString (email, "");
        textp = sharedPreferences.getString (password,"");
    }

    public void updatestate(){
        edtxt_email.setText (texte);
        edtxt_password.setText (textp);
    }


    private void updateUI(FirebaseUser user) {

    }
}