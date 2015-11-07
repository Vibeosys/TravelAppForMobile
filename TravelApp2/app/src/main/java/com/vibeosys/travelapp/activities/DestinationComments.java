package com.vibeosys.travelapp.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vibeosys.travelapp.Adaptors.ShowDestinationCommentsAdaptor;
import com.vibeosys.travelapp.R;
import com.vibeosys.travelapp.data.Comment;
import com.vibeosys.travelapp.data.TableDataDTO;
import com.vibeosys.travelapp.data.UserCommentDTO;
import com.vibeosys.travelapp.tasks.BaseFragment;
import com.vibeosys.travelapp.util.NetworkUtils;
import com.vibeosys.travelapp.util.SessionManager;
import com.vibeosys.travelapp.util.UserAuth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mahesh on 10/15/2015.
 */
public class DestinationComments extends BaseFragment implements View.OnClickListener {
    ListView mDestinationCommentListView;
    List<UserCommentDTO> mDestinationComments = null;
    //NewDataBase newDataBase = null;
    EditText editTextCommentByUser;
    Button submitBtn;
    List<Comment> listComment;
    int DestId;
    //String UserId;
    //String UserName;
    //List<Sync> syncDataToUpload = null;
    ShowDestinationCommentsAdaptor showDestinationCommentsAdaptor;

    //protected SessionManager sessionManager=SessionManager.Instance();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.destination_comment_listview, null);
        mDestinationCommentListView = (ListView) view.findViewById(R.id.commenntlistview);
        submitBtn = (Button) view.findViewById(R.id.submitButton);
        submitBtn.setOnClickListener(this);
        mDestinationComments = new ArrayList<>();

        DestId = getActivity().getIntent().getExtras().getInt("DestId");
        Log.d("DestId Comments", "" + DestId);
        editTextCommentByUser = (EditText) view.findViewById(R.id.commnetbyUser);
        //newDataBase = new NewDataBase(getActivity());
        mDestinationComments = new ArrayList<>();
        listComment = new ArrayList<>();
        mDestinationComments = mNewDataBase.getDestinationComments(DestId);

        Log.d("DestinationComment", String.valueOf(mDestinationComments.size()));
        //UserId = SessionManager.Instance().getUserId();
        //UserName = SessionManager.Instance().getUserName();
        showDestinationCommentsAdaptor = new ShowDestinationCommentsAdaptor(getActivity(), mDestinationComments, DestId);
        mDestinationCommentListView.setAdapter(showDestinationCommentsAdaptor);
        return view;
    }

    @Override
    public void onClick(View v) {

        if (!UserAuth.isUserLoggedIn(this.getContext()))
            return;

        //int Destinatinid = DestId;
        if (editTextCommentByUser.getText().toString().length() > 0 && editTextCommentByUser.getText().toString() != null) {
            String comment = editTextCommentByUser.getText().toString();
            UploadComment(DestId, mSessionManager.getUserId(), comment);

            Comment comment1 = new Comment();
            comment1.setUserId(mSessionManager.getUserId());
            comment1.setCommentText(comment);
            comment1.setDestId(DestId);
            //listComment.add(comment1);
            mNewDataBase.insertOrUpdateComment(comment1);
            mDestinationComments.clear();
            mDestinationComments = mNewDataBase.getDestinationComments(DestId);
            showDestinationCommentsAdaptor.updateResults(mDestinationComments);
            editTextCommentByUser.setText("");
            editTextCommentByUser.clearAnimation();
            editTextCommentByUser.clearFocus();

            // Toast.makeText(getApplicationContext(), "Enterted comement" + editTextCommentByUser.getText().toString(), Toast.LENGTH_SHORT).show();
        } else {
            editTextCommentByUser.setError("Please Enter some Text");
        }

    }


    private void UploadComment(int destinatinid, String userId, String comment) {
        Comment comment1 = new Comment();
        comment1.setCommentText(comment);
        comment1.setDestId(destinatinid);
        comment1.setUserId(userId);
        Gson gson = new Gson();

        String serializedJsonString = gson.toJson(comment1);

        ArrayList<TableDataDTO> tableDataList = new ArrayList<TableDataDTO>();

        tableDataList.add(new TableDataDTO("comment", serializedJsonString, null));
        String EmailId = SessionManager.Instance().getUserEmailId();
        Log.d("EmailId", "" + EmailId);
        //String uploadData = gson.toJson(new Upload(new UploadUser(userId, EmailId), tableDataList));

        //Log.d("Uploading", uploadData.toString());

        if (NetworkUtils.isActiveNetworkAvailable(getActivity())) {

            //super.UploadUserDetails();
            //newDataBase.getFromSync();
            mServerSyncManager.uploadDataToServer(tableDataList);

        } else {
            mNewDataBase.addDataToSync("comment", userId, serializedJsonString);
            LayoutInflater
                    layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.cust_toast, null);
            Toast toast = new Toast(getActivity());
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setView(view);//setting the view of custom toast layout
            toast.show();
        }

    }
}
