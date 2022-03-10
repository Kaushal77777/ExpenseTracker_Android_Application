package com.dduce.expensetracker.activities;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.dduce.expensetracker.R;
import com.dduce.expensetracker.databinding.ActivitySignInBinding;
import com.dduce.expensetracker.utils.DBHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends AppCompatActivity {
    ActivitySignInBinding binding;
    private static final int RC_SIGN_IN = 123;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.signInButton.setOnClickListener(v -> {
            signIn();
            binding.signInButton.setEnabled(false);
            binding.errorTextview.setText("");
        });
    }

    private void signIn() {
        showProgressView();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                hideProgressView();
                loginError("Google sign in failed.");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        DBHelper.getFirebaseAuth().signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                if (DBHelper.getLoginUser() == null) {
                    loginError("Firebase fetch user data failed.");
                    hideProgressView();
                } else {
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    finish();
                }
            } else {
                loginError("Firebase auth failed.");
                hideProgressView();
            }
        });
    }

    private void loginError(String text) {
        binding.errorTextview.setText(text);
        binding.signInButton.setEnabled(true);
    }

    private void showProgressView() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressView() {
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
