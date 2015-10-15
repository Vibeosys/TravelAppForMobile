package com.vibeosys.travelapp;

/**
 * Created by mahesh on 10/15/2015.
 */
public class CommentsAndLikes extends UserDetails {
    int mId;
    String mCommentText;
    int mLikeCount;
    String mLikeDate;
    String mCommentDate;

    CommentsAndLikes(int cId, int cUserId, int cDestId, int cLikeCount, String cCommentText, String cLikeDate, String cCommentDate) {

    }

    CommentsAndLikes() {

    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmCommentText() {
        return mCommentText;
    }

    public void setmCommentText(String mCommentText) {
        this.mCommentText = mCommentText;
    }

    public int getmLikeCount() {
        return mLikeCount;
    }

    public void setmLikeCount(int mLikeCount) {
        this.mLikeCount = mLikeCount;
    }

    public String getmLikeDate() {
        return mLikeDate;
    }

    public void setmLikeDate(String mLikeDate) {
        this.mLikeDate = mLikeDate;
    }

    public String getmCommentDate() {
        return mCommentDate;
    }

    public void setmCommentDate(String mCommentDate) {
        this.mCommentDate = mCommentDate;
    }
}
