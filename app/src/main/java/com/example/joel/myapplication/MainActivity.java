package com.example.joel.myapplication;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FragmentManager fm = getFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();

        final Fragment listFragment = fm.findFragmentById(R.id.fragmentList);
        final Fragment infoFragment = fm.findFragmentById(R.id.fragmentInfo);
        final Fragment infoFragmentLand = fm.findFragmentById(R.id.fragmentInfoLand);

        final boolean orientLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        if (orientLandscape){
            ft.hide(infoFragmentLand);
            ft.commit();
        }else{
            ft.hide(infoFragment);
            ft.commit();
        }
        ListView itemListView = (ListView) findViewById(R.id.listView);
        setUpListView(this, itemListView, fm);

        Button backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final FragmentTransaction ft = fm.beginTransaction();
                if(orientLandscape) {
                    ft.hide(infoFragmentLand);
                    ft.commit();
                }else{
                    ft.show(listFragment);
                    ft.hide(infoFragment);
                    ft.commit();
                }
            }
        });
    }

    public void setUpListView(final Context context,final ListView listView, final FragmentManager fm) {
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest sr = new JsonObjectRequest(Request.Method.GET,
                "http://tddd80-afteach.rhcloud.com/api/groups",new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray responseMessage = response.getJSONArray("grupper");
                            final ArrayList<String> allGroups = new ArrayList<String>();
                            for (int i = 0; i < responseMessage.length(); i++) {
                                allGroups.add(responseMessage.get(i).toString());
                            }

                            final ArrayAdapter arrayAdapter = new ArrayAdapter<String>(context,
                                    android.R.layout.simple_list_item_1, allGroups);
                            listView.setAdapter(arrayAdapter);
                            listView.setOnItemClickListener(
                                    new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            updateInfoFragment(getBaseContext(),fm, allGroups.get(position));
                                        }
                                    });
                        }catch(JSONException e) {
                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Connection to server failed", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(sr);
    }


    public void updateInfoFragment(final Context context, final FragmentManager fm, final String groupName) {
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest sr = new JsonObjectRequest(Request.Method.GET,
                "http://tddd80-afteach.rhcloud.com/api/groups/"+groupName,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    TextView title = (TextView) findViewById(R.id.groupNameText);
                    TextView members = (TextView) findViewById(R.id.infoText);
                    title.setText(groupName);
                    JSONArray membersArray = response.getJSONArray("medlemmar");
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < membersArray.length(); i++) {
                        JSONObject currentUser = membersArray.getJSONObject(i);
                        stringBuilder.append(currentUser.getString("namn")+"  - ");
                        stringBuilder.append(currentUser.getString("epost"));
                        try{
                            String lastResponded = currentUser.getString("svarade");
                            stringBuilder.append(" "+lastResponded);
                        }catch (JSONException e){e.printStackTrace();}

                        stringBuilder.append("\n"+"\n");
                    }
                    members.setText(stringBuilder);


                    final FragmentTransaction ft = fm.beginTransaction();
                    final Fragment listFragment = fm.findFragmentById(R.id.fragmentList);
                    final Fragment infoFragment = fm.findFragmentById(R.id.fragmentInfo);
                    final Fragment infoFragmentLand = fm.findFragmentById(R.id.fragmentInfoLand);

                    final boolean orientLandscape = getResources().getConfiguration().
                            orientation == Configuration.ORIENTATION_LANDSCAPE;
                    if (orientLandscape){
                        ft.show(infoFragmentLand);
                        ft.commit();
                    }else {
                        ft.show(infoFragment);
                        ft.hide(listFragment);
                        ft.commit();
                    }
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Connection to server failed", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(sr);
    }
}
