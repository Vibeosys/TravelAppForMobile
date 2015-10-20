package com.vibeosys.travelapp.data;

import android.webkit.JavascriptInterface;

import com.google.gson.internal.bind.JsonAdapterAnnotationTypeAdapterFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

/**
 * Created by mahesh on 10/7/2015.
 */
public class JsonData extends JSONStringer{
    JSONArray jsonArray=new JSONArray();
    JSONObject jsonObject=new JSONObject();
    JsonAdapterAnnotationTypeAdapterFactory jsonAdapterAnnotationTypeAdapterFactory;
    JSONException jsonException;
    JsonData jsonData;
    JSONTokener jsonTokener;
    JavascriptInterface js;

    public JsonAdapterAnnotationTypeAdapterFactory getJsonAdapterAnnotationTypeAdapterFactory() {
        return jsonAdapterAnnotationTypeAdapterFactory;
    }

    public void setJsonAdapterAnnotationTypeAdapterFactory(JsonAdapterAnnotationTypeAdapterFactory jsonAdapterAnnotationTypeAdapterFactory) {
        this.jsonAdapterAnnotationTypeAdapterFactory = jsonAdapterAnnotationTypeAdapterFactory;
    }

    public JSONException getJsonException() {
        return jsonException;
    }

    public void setJsonException(JSONException jsonException) {
        this.jsonException = jsonException;
    }

    public JsonData getJsonData() {
        return jsonData;
    }

    public void setJsonData(JsonData jsonData) {
        this.jsonData = jsonData;
    }

    public JSONTokener getJsonTokener() {
        return jsonTokener;
    }

    public void setJsonTokener(JSONTokener jsonTokener) {
        this.jsonTokener = jsonTokener;
    }

    public JavascriptInterface getJs() {
        return js;
    }

    public void setJs(JavascriptInterface js) {
        this.js = js;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    @Override
    public JSONStringer array() throws JSONException {
        return super.array();
    }

    @Override
    public JSONStringer endArray() throws JSONException {
        return super.endArray();
    }

    @Override
    public JSONStringer endObject() throws JSONException {
        return super.endObject();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public JSONStringer value(Object value) throws JSONException {
        return super.value(value);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    @Override
    public JSONStringer object() throws JSONException {
        return super.object();
    }

    @Override
    public JSONStringer value(long value) throws JSONException {
        return super.value(value);
    }

    @Override
    public JSONStringer key(String name) throws JSONException {
        return super.key(name);
    }

    @Override
    public JSONStringer value(boolean value) throws JSONException {
        return super.value(value);
    }

    @Override
    public JSONStringer value(double value) throws JSONException {
        return super.value(value);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}

