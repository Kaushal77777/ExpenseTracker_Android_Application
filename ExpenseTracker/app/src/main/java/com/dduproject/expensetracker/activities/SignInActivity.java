package com.dduproject.expensetracker.activities;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dduproject.expensetracker.R;
import com.dduproject.expensetracker.databinding.ActivitySignInBinding;
import com.dduproject.expensetracker.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {
    ActivitySignInBinding binding;
    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
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
                e.printStackTrace();
                hideProgressView();
                loginError("Google sign in failed.");
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        loginError("Firebase auth failed.");
                        hideProgressView();
                    }
                });
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null) {
            binding.progressBar.setVisibility(View.GONE);
            return;
        }
        showProgressView();
        final DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    finish();
                } else {
                    runTransaction(userReference);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ERROR", databaseError.getDetails());
                loginError("Firebase fetch user data failed.");
                hideProgressView();
            }
        });
    }

    private void loginError(String text) {
        binding.errorTextview.setText(text);
        binding.signInButton.setEnabled(true);
    }

    private void runTransaction(DatabaseReference userReference) {
        showProgressView();
        userReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User user = mutableData.getValue(User.class);
                if (user == null) {
                    mutableData.setValue(new User());
                    return Transaction.success(mutableData);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (committed) {
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    finish();
                } else {
                    loginError("Firebase create user transaction failed.");
                    hideProgressView();
                }
            }
        });
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
