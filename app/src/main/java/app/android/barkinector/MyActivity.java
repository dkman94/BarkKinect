package app.android.barkinector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MyActivity extends Activity {

    private String mUserAccessToken;
    private WebView mWebView;
    private SharedPreferences mSharedPreferences;
    private TextView mWelcomeBackTv;
    private TextView mUserName;
    private double mCurrLat;
    private double mCurrLon;
    private ListView mBarList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> items;
    private SwipeDetector swipeDetector;
    private CustomListAdapter mCustListAdapter;
    private ArrayList<ListObj> customListObjs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        items = new ArrayList<String>();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mWebView = (WebView) findViewById(R.id.webview_auth);
        mWelcomeBackTv = (TextView) findViewById(R.id.welcome_back_tv);
        mUserName = (TextView) findViewById(R.id.user_name);
        mBarList = (ListView) findViewById(R.id.list_of_bars);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
        customListObjs = new ArrayList<ListObj>();
        mCustListAdapter = new CustomListAdapter(this,customListObjs);

        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mCurrLat = location.getLatitude();
        mCurrLon = location.getLongitude();


        Intent intent = new Intent(getApplicationContext(), ImageCaptureActivity.class);
        startActivity(intent);


        mUserAccessToken = mSharedPreferences.getString("venmoAccessToken",null);
        checkIn(mUserAccessToken);
        Log.e("curr token:",mUserAccessToken);
        createUser(mUserAccessToken);
        getBarsNearby(mCurrLat, mCurrLon);

    }

   public void createUser(String token){
        RestClient.getUserInfo(token, new JsonHttpResponseHandler(){
            public void onStart() {
                Log.d("Started"," response");
            }
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                try{
                    JSONObject data = response.getJSONObject("data");
                    JSONObject user = data.getJSONObject("user");
                    final String name = user.getString("display_name");
                    String id = user.getString("id");

                    //Log.e("Vals:", name + "," + id);

                    Log.d("Nah","Doesnt exist");

                    RestClient.user_connect(id, new JsonHttpResponseHandler() {
                        public void onStart() {
                            Log.d("Started", "response user connect");
                        }

                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d("Successful", Integer.toString(statusCode));
                            Log.d("Exists", response.toString());
                            try {
                                boolean doesExist = response.getBoolean("exists");
                                Log.d("Here","before doesExist");
                                if(doesExist){
                                    Log.d("Yes","Exists");
                                    mUserName.setText(name+"!");
                                }else{
                                    Log.d("asd","asda");
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }

                        public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("Failure", Integer.toString(statusCode));
                        }
                    });

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
            public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONObject errorResponse){
                Log.d("Failure Error", errorResponse.toString());
            }
        });
    }

    public void checkIn(String token){
        RestClient.getCheckInInfo(token, new JsonHttpResponseHandler() {
            public void onStart() {
                Log.d("Started", " response");
            }

            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");
                    JSONObject user = data.getJSONObject("user");
                    String id = user.getString("id");

                    String bar_name = "bar";

                    Log.e("Vals:", id);
                    //mCheckIn.setText(bar_name); TODO where to set text for bar name and username etc.

                    RestClient.check_in(bar_name, id, new JsonHttpResponseHandler() {
                        public void onStart() {
                            Log.d("Started", " check_in request");
                        }

                        public void onSuccess(int statusCode,    Header[] headers, JSONObject response) {
                            Log.e("Success Code of bar check in", "Was Success");
                        }

                        public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.e("Error Code of bar check in", Integer.toString(statusCode));
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Failure Error", errorResponse.toString());
            }
        });
    }

    public void nearbyAtms(){
        final ArrayList<String> atms = new ArrayList<String>();

        RestClient.get_nearby_atms(new JsonHttpResponseHandler(){
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try{
                    for(int i = 0; i < 5; i++){
                        JSONObject item = response.getJSONObject(i);
                        String name = item.getString("name");
                        JSONObject address = item.getJSONObject("address");
                        String streetName = address.getString("street name");
                        String state = address.getString("state");
                        String streetNumber = address.getString("street number");
                        String zip = address.getString("zip");
                        String city = address.getString("city");

                        String atmLoc = name+" at "+streetNumber+" "+streetName+", "+city+", "+state+" "+zip;
                        atms.add(i,atmLoc);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

                for(int j = 0; j < atms.size(); j++){
                    Log.e("AList: ",atms.get(j));
                }
            }

            public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("Error of atms", Integer.toString(statusCode));
            }
        });
    }

    public void getBarsNearby(double lat, double lon){
        RestClient.searchBarsNearby(lat,lon,new JsonHttpResponseHandler(){
            public void onStart(){
                Log.d("Just started","waiting for response");
            }
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("Status code",Integer.toString(statusCode));
                try {
                    JSONArray results = response.getJSONArray("results");
                    for(int i = 0; i < 5; i++) {
                        JSONObject item = results.getJSONObject(i);
                        String name = item.getString("name");
                        String address = item.getString("vicinity");
                        JSONObject geometry = item.getJSONObject("geometry");
                        JSONObject location = geometry.getJSONObject("location");
                        double lng = location.getDouble("lng");
                        double lat = location.getDouble("lat");

                        double miDist = distFrom(mCurrLat,mCurrLon,lat,lng);
                        String barName = name;
                        String barAddr = address+","+miDist+"mi";
                        ListObj itemObj = new ListObj(barName,barAddr);
                        customListObjs.add(itemObj);
                        mCustListAdapter.notifyDataSetChanged();

                        Log.d("Bars:",name+","+address+","+miDist);
                    }
                    mBarList.setVisibility(View.VISIBLE);
                    mBarList.setAdapter(mCustListAdapter);

                    Log.d("result",results.toString());

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
            public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Failure Error", errorResponse.toString());
            }
        });
    }

    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371; //kilometers
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = (double) (earthRadius * c);

        return dist;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
