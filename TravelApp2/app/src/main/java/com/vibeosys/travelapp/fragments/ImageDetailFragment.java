/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vibeosys.travelapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vibeosys.travelapp.activities.ImageDetailActivity;
import com.vibeosys.travelapp.R;
import com.vibeosys.travelapp.data.Images;
import com.vibeosys.travelapp.data.Like;
import com.vibeosys.travelapp.data.TableDataDTO;
import com.vibeosys.travelapp.tasks.BaseFragment;
import com.vibeosys.travelapp.util.DbTableNameConstants;
import com.vibeosys.travelapp.util.ImageFetcher;
import com.vibeosys.travelapp.util.ImageWorker;
import com.vibeosys.travelapp.util.NetworkUtils;
import com.vibeosys.travelapp.util.UserAuth;
import com.vibeosys.travelapp.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * This fragment will populate the children of the ViewPager from {@link ImageDetailActivity}.
 */
public class ImageDetailFragment extends BaseFragment
        implements ImageView.OnClickListener {
    private static final String IMAGE_DATA_EXTRA = "extra_image_data";
    private String mImageUrl;
    private ImageView mImageView;
    private ImageFetcher mImageFetcher;
    private String ImageId;
    private List<Images> listImages;
    //private NewDataBase newDataBase;
    private Images images;
    private TextView mLikeCountText;
    //SessionManager sessionManager = SessionManager.Instance();

    /**
     * Factory method to generate a new instance of the fragment given an image number.
     *
     * @param imageUrl The image url to load
     * @param imageId
     * @return A new instance of ImageDetailFragment with imageNum extras
     */

    public static ImageDetailFragment newInstance(String imageUrl, String imageId) {
        final ImageDetailFragment f = new ImageDetailFragment();
        final Bundle args = new Bundle();
        args.putString(IMAGE_DATA_EXTRA, imageUrl);
        args.putString("ImageId", imageId);
        f.setArguments(args);

        return f;
    }

    /**
     * Empty constructor as per the Fragment documentation
     */
    public ImageDetailFragment() {
    }


    /**
     * Populate image using a url from extras, use the convenience factory method
     * {@link ImageDetailFragment#(String, int)} to create this fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //newDataBase = new NewDataBase(getActivity());
        listImages = new ArrayList<>();
        ImageId = getArguments() != null ? getArguments().getString("ImageId") : null;
        Log.d("ImageDetails", "" + ImageId);
        mImageUrl = getArguments() != null ? getArguments().getString(IMAGE_DATA_EXTRA) : null;

        Log.e("ListImagesSize", "" + listImages.size());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate and locate the main ImageView
        final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
        TextView usernameText = (TextView) v.findViewById(R.id.usernaemimagetext);
        mLikeCountText = (TextView) v.findViewById(R.id.likecounttext);
        ImageView like = (ImageView) v.findViewById(R.id.likebutton);

        images = mNewDataBase.imageUserLikeCount(ImageId);

        if (images != null) {
            String username = images.getUsername();
            SpannableString ss1 = new SpannableString(username);

            ss1.setSpan(new RelativeSizeSpan(2f), 0, ss1.length(), 0);
            Spanned text = Html.fromHtml("<font >Updated By </font>  " + "<b>" + ss1.toString() + "</b> ");
            usernameText.setText(text);
            mLikeCountText.setText(images.getLikeCount() + "  Likes  ");
        }

        like.setOnClickListener(this);
        mImageView = (ImageView) v.findViewById(R.id.usersimageView);

        return v;
    }

    @Override
    public void onClick(View v) {
        if (!UserAuth.isUserLoggedIn(getContext()))
            return;

        mLikeCountText.setText(images.getLikeCount() + 1 + "  Likes  ");
        updateLike(images.getUserId());
    }

    private boolean updateLike(String imageUserId) {
        Like like = new Like();
        like.setUserId(imageUserId);
        like.setDestId(images.getDestId());

        String serializeString = like.serializeString();
        //ArrayList<TableDataDTO> tableDataList = new ArrayList<TableDataDTO>();
        //tableDataList.add(new TableDataDTO("like", serializeString, null));
        //String currentUserID = SessionManager.Instance().getUserId();
        mNewDataBase.insertOrUpdateLikeCount(imageUserId, images.getDestId(), images.getLikeCount());
        if (NetworkUtils.isActiveNetworkAvailable(getActivity())) {
            TableDataDTO tableDataDTO = new TableDataDTO(DbTableNameConstants.LIKE, serializeString, null);
            mServerSyncManager.uploadDataToServer(tableDataDTO);
            return true;
        } else {
            mNewDataBase.addDataToSync(DbTableNameConstants.LIKE, mSessionManager.getUserId(), serializeString);
            LayoutInflater
                    layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.cust_toast, null);
            Toast toast = new Toast(getActivity());
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setView(view);//setting the view of custom toast layout
            toast.show();
            return false;

        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Use the parent activity to load the image asynchronously into the ImageView (so a single
        // cache can be used over all pages in the ViewPager
        if (ImageDetailActivity.class.isInstance(getActivity())) {
            mImageFetcher = ((ImageDetailActivity) getActivity()).getImageFetcher();
            mImageFetcher.loadImage(mImageUrl, mImageView);
        }

        // Pass clicks on the ImageView to the parent activity to handle
        if (OnClickListener.class.isInstance(getActivity()) && Utils.hasHoneycomb()) {
            mImageView.setOnClickListener((OnClickListener) getActivity());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mImageView != null) {
            // Cancel any pending image work
            ImageWorker.cancelWork(mImageView);
            mImageView.setImageDrawable(null);
        }
    }


}
