package com.vibeosys.travelapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vibeosys.travelapp.MainActivity;
import com.vibeosys.travelapp.R;
import com.vibeosys.travelapp.RoundedImageView;
import com.vibeosys.travelapp.tasks.BaseActivity;
import com.vibeosys.travelapp.util.SessionManager;

public class ViewProfileActivity extends BaseActivity
        implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("My Profile");
        setContentView(R.layout.activity_view_profile);
        mSessionManager = SessionManager.Instance();

        if (mSessionManager.getUserPhotoUrl() != null && mSessionManager.getUserPhotoUrl() != "") {
            ImageView imageView = (RoundedImageView) findViewById(R.id.userViewProfileImage);
            downloadImageAsync(mSessionManager.getUserPhotoUrl(), imageView);
        }
        TextView userNameTextView = (TextView) findViewById(R.id.userNameViewProfile);
        userNameTextView.setText(mSessionManager.getUserName());
        TextView emailIdTextView = (TextView) findViewById(R.id.emailIdValue);
        emailIdTextView.setText(mSessionManager.getUserEmailId());

        Button logoutButton = (Button) findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        logOut();

        Intent theMainActivityIntent = new Intent(v.getContext(), MainActivity.class);
        //theMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        v.getContext().startActivity(theMainActivityIntent);

        finish();
    }
}
