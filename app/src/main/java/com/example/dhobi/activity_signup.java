package com.example.dhobi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
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

public class activity_signup extends AppCompatActivity {

    private static final int REQUEST_CODE_GOOGLE_SIGN_IN = 8788;
    private static final String TAG = "SIGNUP";
    private EditText name,email,password,confirmpassword;
    private SignInButton btn_signup;
    private TextView btn_gotologin;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<String> status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_sign_up);

        initialize();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
    }


    private void googlesignin () {
        btn_signup.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                signIn ();
            }
        });
    }

    private void gotologin() {
        btn_gotologin.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                startActivity (new Intent ());
            }
        });
    }

    private void initialize () {
        btn_signup = findViewById (R.id.btn_googleisgnin_signup_actiivty);
        btn_gotologin = findViewById (R.id.btn_gotologin);
        name = findViewById (R.id.edtxt_signup_name);
        email = findViewById (R.id.edtxt_signup_email);
        password = findViewById (R.id.edtxt_signup_password);
        confirmpassword = findViewById (R.id.edtxt_signup_confirmpassword);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.googlesignin_WebClientID))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient (this, gso);
        mAuth = FirebaseAuth.getInstance ();

        googlesignin();
        gotologin ();
        createAccount ();
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
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
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
                            updateUI(null);
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE_SIGN_IN);
    }

    private void createAccount() {
        String stremail = email.getText().toString();
        String strpassword = password.getText().toString();
        String strconfirmpassword = confirmpassword.getText ().toString ();

        btn_signup.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                if (stremail.isEmpty ()) {
                    email.setError ("Enter E-mail");
                    email.requestFocus ();
                } else if (strpassword.isEmpty ()) {
                    password.setError ("Enter Password");
                    password.requestFocus ();
                } else if (!strpassword.equals (strconfirmpassword)) {
                    confirmpassword.setError ("Passwords do not match");
                    confirmpassword.requestFocus ();
                } else {
                    mAuth.createUserWithEmailAndPassword (stremail, strpassword).addOnCompleteListener (new OnCompleteListener<AuthResult> () {
                        @Override
                        public void onComplete (@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful ()) {
                                Toast.makeText (activity_signup.this, "User created successfully. Continue by Logging in.", Toast.LENGTH_SHORT).show ();
                                startActivity (new Intent (activity_signup.this, activity_login.class));
                            } else {
                                Toast.makeText (activity_signup.this, "Account creation Error: " + task.getException ().getMessage (), Toast.LENGTH_SHORT).show ();
                            }
                        }
                    });
                }

            }
        });
    }

    private void updateUI(FirebaseUser user) {

    }
}
