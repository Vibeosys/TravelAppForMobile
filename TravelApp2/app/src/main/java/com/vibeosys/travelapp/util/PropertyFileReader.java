package com.vibeosys.travelapp.util;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by anand on 28-10-2015.
 */
public class PropertyFileReader {
    private static PropertyFileReader mPropertyFileReader = null;
    private static Context mContext;
    protected static Properties mProperties;

    public static PropertyFileReader getInstance(Context context) {
        if (mPropertyFileReader != null)
            return mPropertyFileReader;

        mContext = context;
        mProperties = getProperties();
        mPropertyFileReader = new PropertyFileReader();
        return mPropertyFileReader;
    }

    protected static Properties getProperties() {
        try {
            AssetManager assetManager = mContext.getAssets();
            InputStream inputStream = assetManager.open("app.properties");
            mProperties = new Properties();
            mProperties.load(inputStream);

        } catch (Exception e) {
            System.out.print(e.getMessage());
        }

        return mProperties;
    }

    protected String getEndPointUri() {
        return mProperties.getProperty(PropertyTypeConstants.API_ENDPOINT_URI);
    }

    public String getDownloadDbUrl() {
        return getEndPointUri() + mProperties.getProperty(PropertyTypeConstants.API_DOWNLOAD_DB_URI);
    }

    public String getDownloadUrl() {
        return getEndPointUri() + mProperties.getProperty(PropertyTypeConstants.API_DOWNLOAD_URI);
    }

    public String getUploadUrl() {
        return getEndPointUri() + mProperties.getProperty(PropertyTypeConstants.API_UPLOAD_URL);
    }

    public String getDatabaseName() {
        return mProperties.getProperty(PropertyTypeConstants.DATABASE_NAME);
    }

    public float getVersion() {
        String versionNumber = mProperties.getProperty(PropertyTypeConstants.VERSION_NUMBER);
        return Float.valueOf(versionNumber);
    }

    public String getImageUploadUrl() {
        return getEndPointUri() + mProperties.getProperty(PropertyTypeConstants.API_UPLOAD_IMAGE_URL);
    }

    public String getUploadUserDetails() {
        return getEndPointUri() + mProperties.getProperty(PropertyTypeConstants.API_UPDATE_USERS_DETAILS);
    }

    public String getDatabasePath()
    {
        return mProperties.getProperty(PropertyTypeConstants.DATABASE_PATH);
    }
}
