package com.vibeosys.travelapp.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.vibeosys.travelapp.Adaptors.ShowDestinationCommentsAdaptor;
import com.vibeosys.travelapp.R;
import com.vibeosys.travelapp.data.Comment;
import com.vibeosys.travelapp.data.TableDataDTO;
import com.vibeosys.travelapp.data.UserCommentDTO;
import com.vibeosys.travelapp.tasks.BaseFragment;
import com.vibeosys.travelapp.util.DbTableNameConstants;
import com.vibeosys.travelapp.util.NetworkUtils;
import com.vibeosys.travelapp.util.UserAuth;

import java.util.List;

/**
 * Created by mahesh on 10/15/2015.
 */
public class DestinationComments extends BaseFragment
        implements View.OnClickListener {

    private List<UserCommentDTO> mDestinationComments = null;
    private EditText editTextCommentByUser;
    private int DestId;
    private ShowDestinationCommentsAdaptor showDestinationCommentsAdaptor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.destination_comment_listview, null);
        ListView mDestinationCommentListView = (ListView) view.findViewById(R.id.commenntlistview);
        Button submitBtn = (Button) view.findViewById(R.id.submitButton);

        submitBtn.setOnClickListener(this);
        DestId = getActivity().getIntent().getExtras().getInt("DestId");
        Log.d("DestId Comments", "" + DestId);
        editTextCommentByUser = (EditText) view.findViewById(R.id.commnetbyUser);
        mDestinationComments = mNewDataBase.getDestinationComments(DestId);

        Log.d("DestinationComment", String.valueOf(mDestinationComments.size()));

        showDestinationCommentsAdaptor = new ShowDestinationCommentsAdaptor(getActivity(), mDestinationComments);
        mDestinationCommentListView.setAdapter(showDestinationCommentsAdaptor);
        return view;
    }

    @Override
    public void onClick(View v) {

        if (!UserAuth.isUserLoggedIn(this.getContext()))
            return;

        Editable commentByUserText = editTextCommentByUser.getText();

        if (!commentByUserText.toString().isEmpty()) {
            String comment = commentByUserText.toString();
            UploadComment(DestId, mSessionManager.getUserId(), comment);

            Comment comment1 = new Comment();
            comment1.setUserId(mSessionManager.getUserId());
            comment1.setCommentText(comment);
            comment1.setDestId(DestId);

            mNewDataBase.insertOrUpdateComment(comment1);
            mDestinationComments.clear();
            mDestinationComments = mNewDataBase.getDestinationComments(DestId);
            showDestinationCommentsAdaptor.updateResults(mDestinationComments);
            editTextCommentByUser.setText("");
            editTextCommentByUser.clearAnimation();
            editTextCommentByUser.clearFocus();

        } else {
            editTextCommentByUser.setError("Please Enter some Text");
        }

    }


    private void UploadComment(int destId, String userId, String comment) {
        Comment comment1 = new Comment();
        comment1.setCommentText(comment);
        comment1.setDestId(destId);
        comment1.setUserId(userId);
        Gson gson = new Gson();

        String serializedJsonString = gson.toJson(comment1);

        if (NetworkUtils.isActiveNetworkAvailable(getActivity())) {
            TableDataDTO tableDataDTO = new TableDataDTO(DbTableNameConstants.COMMENT, serializedJsonString, null);
            mServerSyncManager.uploadDataToServer(tableDataDTO);

        } else {
            mNewDataBase.addDataToSync(DbTableNameConstants.COMMENT, userId, serializedJsonString);
            /*LayoutInflater
                    layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.cust_toast, null);
            Toast toast = new Toast(getActivity());
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setView(view);//setting the view of custom toast layout
            toast.show();*/
        }

    }
}
