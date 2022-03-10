package com.dduce.expensetracker.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import com.dduce.expensetracker.databinding.ActivitySplashBinding;
import com.dduce.expensetracker.utils.DBHelper;

public class SplashActivity extends AppCompatActivity {
    ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (DBHelper.getLoginUser() == null) {
            goToSignInActivity();
        } else {
            goToMainActivity();
        }
    }
    public void goToSignInActivity(){
        Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }
    public void goToMainActivity(){
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}