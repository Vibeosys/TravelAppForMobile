package com.vibeosys.travelapp.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vibeosys.travelapp.QuestionsFromOthers;
import com.vibeosys.travelapp.R;
import com.vibeosys.travelapp.data.UserLikeDTO;
import com.vibeosys.travelapp.util.UserAuth;

import java.util.List;

/**
 * Created by anand on 25-11-2015.
 */
public class UserListAdaptor extends BaseAdapter {
    private QuestionsFromOthers mQuestionsFromOthersForm;
    private List<UserLikeDTO> userDetailsList;
    private Context mContext;

    public UserListAdaptor(QuestionsFromOthers questionsFromOthersForm, List<UserLikeDTO> listUsersDetails) {
        this.mQuestionsFromOthersForm = questionsFromOthersForm;
        userDetailsList = listUsersDetails;
        mContext = questionsFromOthersForm.getActivity();
    }


    @Override
    public int getCount() {
        return userDetailsList.size();
    }

    @Override
    public Object getItem(int position) {
        return userDetailsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.userlikelist, null);
        TextView usernameText = (TextView) view.findViewById(R.id.userlikesname);
        usernameText.setText(userDetailsList.get(position).getUserName());
        final TextView userLikeCount = (TextView) view.findViewById(R.id.userlikecountText);
        ImageView userLikeImage = (ImageView) view.findViewById(R.id.likebutton);
        userLikeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UserAuth.isUserLoggedIn(mQuestionsFromOthersForm.getContext())) {
                    return;
                }
                mQuestionsFromOthersForm.updateLike(userDetailsList.get(position).getUserId(), userDetailsList.get(position).getUserLikeCount());
                userLikeCount.setText(userDetailsList.get(position).getUserLikeCount() + 1 + " Likes");
            }
        });

        userLikeCount.setText(userDetailsList.get(position).getUserLikeCount() + "  Likes");
        return view;
    }
}
