package com.vibeosys.travelapp.tasks;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.vibeosys.travelapp.databaseHelper.NewDataBase;
import com.vibeosys.travelapp.util.ServerSyncManager;
import com.vibeosys.travelapp.util.SessionManager;

/**
 * Created by mahesh on 10/29/2015.
 */
public class BaseFragment extends Fragment {

    /**
     * Base Activity will give the basic implementation with async task support and other things
     */
    protected ServerSyncManager mServerSyncManager;
    protected static SessionManager mSessionManager;
    protected NewDataBase mNewDataBase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSessionManager = SessionManager.getInstance(getContext());
        mServerSyncManager = new ServerSyncManager(getContext(), mSessionManager);
        mNewDataBase = new NewDataBase(getContext(), mSessionManager);
    }
}


