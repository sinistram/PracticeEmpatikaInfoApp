package com.marikyan.empatikainfoapp;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pkmmte.view.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;


public class AboutActivity extends FragmentActivity {

    TextView mTextView;
    JSONObject infoJSON = null;
    SharedPreferences data;
    SharedPreferences.Editor editor;
    static public String email;
    static public String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(true);

        mTextView = (TextView)findViewById(R.id.textView_info);

        data = getApplicationContext().getSharedPreferences("data", MODE_PRIVATE);
        editor = data.edit();

        String jsonString = data.getString("jsonData", "");
        if (jsonString.length() > 0) { //data is already loaded
            try {
                infoJSON = new JSONObject(jsonString);
                applyData();
                return;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://m.empatika-resto-test.appspot.com/api/company/get_company?company_id=5629499534213120";
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            infoJSON = new JSONObject(response);
                            editor.putString("jsonData", response);
                            editor.apply();
                            applyData();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextView.setText("Oups!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }




    private void applyData() {
        try {
            email = infoJSON.get("email").toString();
            phone = infoJSON.get("phone").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_about, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void buttonCallClick(View v) {
        try {
            String uri = "tel:"+phone;
            Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
            startActivity(dialIntent);
        }catch(Exception e) {
            Toast.makeText(getApplicationContext(), "Your call has failed...",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    public void buttonSendClick(View v) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(AboutActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }


}
