package app.android.barkinector;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


public class LoginActivity extends Activity {

    private SharedPreferences mSharedPreferences;
    private WebView mWebView;
    private ImageView mBckgImg;
    private TextView tvIntro;
    private TextView signInTv;
    private Button venmoBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mWebView = (WebView) findViewById(R.id.webview_auth);

        if(mSharedPreferences.contains("venmoAccessToken")){
            Intent intent = new Intent(this, MyActivity.class);
            startActivity(intent);
        }else{
            venmoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWebView.setVisibility(View.VISIBLE);
                    mBckgImg.setVisibility(View.INVISIBLE);
                    tvIntro.setVisibility(View.INVISIBLE);
                    signInTv.setVisibility(View.INVISIBLE);
                    venmoBtn.setVisibility(View.INVISIBLE);
                    mWebView.loadUrl("https://api.venmo.com/v1/oauth/authorize?client_id=2269&scope=make_payments%20access_profile%20access_email%20access_phone%20access_balance&response_type=code");
                    mWebView.setWebViewClient(new MyWebViewClient());
                }
            });
        }

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("adjj")) {
                Log.d("Url is:", url);
                mWebView.setVisibility(View.INVISIBLE);
                String[] parts = url.split("=");
                String authCode = parts[1];
                RestClient.postForToken(authCode,new JsonHttpResponseHandler(){
                    public void onStart() {
                        Log.d("Started"," response");
                    }
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                        try{
                            String accessToken = response.getString("access_token");
                            JSONObject user = response.getJSONObject("user");
                            String name = user.getString("display_name");
                            String id = user.getString("id");

                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putBoolean("hasSignedIn", true);
                            editor.putString("venmoAccessToken", accessToken);
                            editor.apply();

                            Intent intent = new Intent(getApplicationContext(), MyActivity.class);
                            startActivity(intent);

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                    public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable throwable, JSONObject errorResponse){
                        Log.d("Failure Error", errorResponse.toString());
                    }
                });

                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            return false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
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
