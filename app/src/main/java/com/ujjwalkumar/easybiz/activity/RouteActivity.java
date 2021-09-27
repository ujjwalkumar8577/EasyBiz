package com.ujjwalkumar.easybiz.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ujjwalkumar.easybiz.R;
import com.ujjwalkumar.easybiz.helper.RoutePoint;
import com.ujjwalkumar.easybiz.util.GoogleMapController;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RouteActivity extends AppCompatActivity {

    private ArrayList<HashMap<String, String>> filtered;
    private ArrayList<RoutePoint> routePoints;

    private ImageView backBtn;
    private TextView textviewStatus;
    private MapView mapView;

    private GoogleMapController mapviewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        backBtn = findViewById(R.id.backBtn);
        textviewStatus = findViewById(R.id.textviewStatus);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        String tmp = getIntent().getStringExtra("orderList");
        Log.d("Order List", tmp);
        filtered = new Gson().fromJson(tmp, new TypeToken<ArrayList<HashMap<String, String>>>() {}.getType());

        backBtn.setOnClickListener(view -> {
            Intent in = new Intent();
            in.setAction(Intent.ACTION_VIEW);
            in.setClass(getApplicationContext(), DashboardActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(in);
            finish();
        });

        mapviewController = new GoogleMapController(mapView, googleMap -> {
            mapviewController.setGoogleMap(googleMap);
            loadRouteOrder();
            mapviewController.zoomTo(15);
            googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        });

        loadRouteOrder();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder exit = new AlertDialog.Builder(this);
        exit.setTitle("Exit");
        exit.setMessage("Do you want to exit?");
        exit.setPositiveButton("Yes", (dialog, which) -> {
            Intent inf = new Intent();
            inf.setAction(Intent.ACTION_VIEW);
            inf.setClass(getApplicationContext(), DashboardActivity.class);
            inf.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(inf);
            finish();
        });
        exit.setNegativeButton("No", (dialog, which) -> {

        });
        exit.create().show();
    }

    // Required if using mapView
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    // Required if using mapView
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    private void loadRouteOrder() {
        String url = getRestURL();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        routePoints = new ArrayList<>();
                        try {
                            JSONArray arr = response.getJSONArray("results").getJSONObject(0).getJSONArray("waypoints");
                            for(int i=0; i<arr.length(); i++) {
                                Log.i("item" + i, arr.getJSONObject(i).getString("id"));
                                HashMap<String, String> mp = new HashMap<>();
                                String id = arr.getJSONObject(i).getString("id");
                                double lat = arr.getJSONObject(i).getDouble("lat");
                                double lng = arr.getJSONObject(i).getDouble("lng");
                                int sequence = arr.getJSONObject(i).getInt("sequence");
//                                routePoints.add(new RoutePoint(id, sequence, lat, lng));

                                mapviewController.moveCamera(lat,lng);
                                mapviewController.addMarker(id, lat, lng);
                                mapviewController.setMarkerInfo(id, String.valueOf(sequence), id);
                                mapviewController.setMarkerIcon(id, R.drawable.ic_location_on_black);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

//                        for(RoutePoint routePoint: routePoints) {
//
//                        }

                        textviewStatus.setText("Route data retrieved successfully");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        textviewStatus.setText("Error in retrieving route data");
                        Toast.makeText(RouteActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private String getRestURL() {
        String API_KEY = "a9RwlkZUwWzfSfkTicFtNohjwXfNIKzcoLSq8sFM-y0";
        String head = "https://wse.ls.hereapi.com/2/findsequence.json?apiKey=" + API_KEY;
        String tail = "&improveFor=time&departure=2014-12-09T09:30:00%2b01:00&mode=fastest;car;traffic:enabled;";

        int n = filtered.size();

        if(n<2)
            return "https://www.google.com";

        String a[][] = new String[n][3];
        for(int i=0; i<n; i++) {
            a[i][0] = filtered.get(i).get("name");
            a[i][1] = filtered.get(i).get("lat");
            a[i][2] = filtered.get(i).get("lng");
        }

        StringBuilder tmp = new StringBuilder();
        tmp.append("&start=" + a[0][0] + ";" + a[0][1] + "," + a[0][2]);
        for(int i=1; i<n-1; i++) {
            tmp.append("&destination" + i + "=" + a[i][0] + ";" + a[i][1] + "," + a[i][2]);
        }
        tmp.append("&end=" + a[n-1][0] + ";" + a[n-1][1] + "," + a[n-1][2]);

        return head + tmp.toString() + tail;
//        return "https://wse.ls.hereapi.com/2/findsequence.json?apiKey=a9RwlkZUwWzfSfkTicFtNohjwXfNIKzcoLSq8sFM-y0&start=start;25.4267419,81.8162144&destination1=A;25.4365431,81.8083711&destination2=B;25.4342022,81.8149761&destination3=C;25.432922,81.8090178&destination4=D;25.4306126,81.8027044&destination5=E;25.4349526,81.8193058&destination6=F;25.4352439,81.8198071&destination7=G;25.433884,81.8194958&destination8=H;25.4328454,81.819513&destination9=I;25.4346534,81.8222264&destination10=J;25.4349254,81.8223654&destination11=K;25.4348914,81.8223917&destination12=L;25.4385669,81.8278010&end=end;25.4267419,81.8162144&improveFor=time&departure=2014-12-09T09:30:00%2b01:00&mode=fastest;car;traffic:enabled;";
    }
}