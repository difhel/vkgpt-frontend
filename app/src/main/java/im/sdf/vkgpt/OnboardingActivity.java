package im.sdf.vkgpt;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;


import im.sdf.vkgpt.databinding.ActivityOnboardingBinding;

public class OnboardingActivity extends AppCompatActivity {
    private ActivityOnboardingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}