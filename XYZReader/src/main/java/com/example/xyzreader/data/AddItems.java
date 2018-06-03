package com.example.xyzreader.data;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.xyzreader.remote.RemoteEndpointUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by AbdullahAtta on 6/2/2018.
 */
public class AddItems extends IntentService {

    public static final String TAG = AddItems.class.getSimpleName();

    public AddItems() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        addToDatabase();
    }


    private void addToDatabase() {

        ArrayList<ContentProviderOperation> cpo = new ArrayList<>();

        Uri dirUri = ItemsContract.Items.buildDirUri();

        // Delete all items
        cpo.add(ContentProviderOperation.newDelete(dirUri).build());

        try {
            JSONArray array = RemoteEndpointUtil.fetchJsonArray();
            if (array == null) {
                throw new JSONException("Invalid parsed item array");
            }

            for (int i = 0; i < array.length(); i++) {
                ContentValues values = new ContentValues();
                JSONObject object = array.getJSONObject(i);
                values.put(ItemsContract.Items.SERVER_ID, object.optString("id"));
                values.put(ItemsContract.Items.AUTHOR, object.optString("author"));
                values.put(ItemsContract.Items.TITLE, object.optString("title"));
                values.put(ItemsContract.Items.BODY, object.optString("body"));
                values.put(ItemsContract.Items.THUMB_URL, object.optString("thumb"));
                values.put(ItemsContract.Items.PHOTO_URL, object.optString("photo"));
                values.put(ItemsContract.Items.ASPECT_RATIO, object.optString("aspect_ratio"));
                values.put(ItemsContract.Items.PUBLISHED_DATE, object.optString("published_date"));
                cpo.add(ContentProviderOperation.newInsert(dirUri).withValues(values).build());
            }

            getContentResolver().applyBatch(ItemsContract.CONTENT_AUTHORITY, cpo);

        } catch (JSONException | RemoteException | OperationApplicationException e) {
            Log.e(TAG, "Error updating content.", e);
        }
    }
}
