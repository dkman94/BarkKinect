package app.android.barkinector;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by deepakkumar on 1/17/15.
 */
public class RestClient {

    private static String BASE_URL = "https://api.venmo.com/v1/";
    private static String CLIENT_ID = "2269";
    private static String CLIENT_SECRET = "hVBuZRfcB4NvSLgQaUnXphpNPf9fu8ef";
    private static String BASE_AZURE_URL = "http://adjj-bartender.cloudapp.net/";
    private static String CAPITALONE_KEY = "CUST2f0ea18b557d9d053a27d80fdc72648b";
    private static String CAPITALONE_URL = "http://api.reimaginebanking.com:80/atms";
    private static String API_KEY = "AIzaSyBEPYgWoTvBINVY8-yf-157Fk5XztbjhG8";
    private static String GOOGLE_BASE_URL = "https://maps.googleapis.com/maps/api/place/search/json";
    private static String GOOGLE_MATRIX_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?";

    private static Context mContext;

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void getPayments(String accessToken, JsonHttpResponseHandler handler){
        //https://api.venmo.com/v1/payments?access_token=<access_token>
        String url = BASE_URL+"payments?access_token="+accessToken;

        client.get(url, handler);
    }

    public static void getUserInfo(String accessToken, JsonHttpResponseHandler handler) {
        String url = BASE_URL+"me?"+"access_token="+accessToken;

        client.get(url, handler);
    }

    public static void getCheckInInfo(String accessToken, JsonHttpResponseHandler handler) {
        String url = BASE_URL+"me?"+"access_token="+accessToken;

        client.get(url, handler);
    }

    public static void postForToken(String code, JsonHttpResponseHandler handler){
        String url = "https://api.venmo.com/v1/oauth/access_token";

        RequestParams params = new RequestParams();
        params.put("client_id",CLIENT_ID);
        params.put("client_secret",CLIENT_SECRET);
        params.put("code",code);

        client.post(url,params,handler);
    }

    public static void user_create(String name, String venmo_id, File picture, JsonHttpResponseHandler handler){
        String url = BASE_AZURE_URL+"/user_create.php";
        try{
            FileInputStream fis = new FileInputStream(picture);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            try {
                for (int readNum; (readNum = fis.read(buf)) != -1; ) {
                    bos.write(buf, 0, readNum); //no doubt here is 0
                    //Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
                    System.out.println("read " + readNum + " bytes,");
                }
            }catch (IOException e){
                e.printStackTrace();
            }
            byte[] bytes = bos.toByteArray();
            Log.e("Bytes:",bytes.toString());
            JSONObject jsonParams = new JSONObject();
            StringEntity entity = null;
            try {
                jsonParams.put("name", name);
                jsonParams.put("venmo_id",venmo_id);
                jsonParams.put("picture",bytes);
                try {
                    entity = new StringEntity(jsonParams.toString());
                }catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
            client.post(mContext, url, entity, "application/json", handler);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    public static void user_connect(String venmo_id, JsonHttpResponseHandler handler){
        String url = BASE_AZURE_URL+"/user_connect.php";

        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;
        try {
            jsonParams.put("venmo_id", venmo_id);
            try {
                entity = new StringEntity(jsonParams.toString());
            } catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        client.post(mContext, url, entity, "application/json", handler);
    }

    public static void check_in(String bar_name, String user_id, JsonHttpResponseHandler handler){
        String url = BASE_AZURE_URL+"/check_in.php";

        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;
        try {
            jsonParams.put("bar_name", bar_name);
            jsonParams.put("user_id", user_id);
            try {
                entity = new StringEntity(jsonParams.toString());
            } catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        client.post(mContext, url, entity, "application/json", handler);
    }

    public static void get_nearby_atms(JsonHttpResponseHandler handler){
        String url = CAPITALONE_URL+"?key="+CAPITALONE_KEY;
        client.get(url,handler);
    }

    public static void searchBarsNearby(double lat, double lon, JsonHttpResponseHandler handler){
        String searchURL = GOOGLE_BASE_URL +
                "?location="+lat+","+lon+
                "&radius=4000"+
                "&types=bar" +
                "&key="+API_KEY;

        Log.e("Search url", searchURL);

        client.get(searchURL, handler);
    }

    public static void getMatrixDist(double origLat, double orignLon, double destLat, double destLon, JsonHttpResponseHandler handler){
        String matrixUrl = GOOGLE_MATRIX_URL+
                "origins="+
                origLat+","+orignLon+"|"+
                destLat+","+destLon+"&"+
                "mode=walking";

        client.get(matrixUrl,handler);
    }

    /* TODO Maybe later

    public static void get_nearby_bar(String bar_name, JsonHttpResponseHandler handler){
        String url = BASE_AZURE_URL+"/check_in.php";

        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;
        try {
            jsonParams.put("bar_name", bar_name);
            try {
                entity = new StringEntity(jsonParams.toString());
            } catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        client.post(mContext, url, entity, "application/json", handler);
    }
    */

}
