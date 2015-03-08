package com.jcj.stjosephs.roadblackspots;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import org.osmdroid.api.IMapController;

import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.DirectedLocationOverlay;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    public static Road mRoad;
    public Polyline mRoadOverlay;
    public MapView map;
    public GeoPoint startPoint, destinationPoint;
    public DirectedLocationOverlay myLocationOverlay;
    public ArrayList<GeoPoint> waypoints;
    public RoadManager roadManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(9);
        destinationPoint = new GeoPoint( 52.682,-7.802);
        startPoint = new GeoPoint(52.752, -7.953);;
        mapController.setCenter(startPoint);
        waypoints = new ArrayList<GeoPoint>();
        waypoints.add(startPoint);
        waypoints.add(destinationPoint);
        new UpdateRoadTask(this).execute(waypoints);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private class UpdateRoadTask extends AsyncTask<Object, Void, Road> {
        protected Context context;
        public UpdateRoadTask(Context context){
            this.context=context;
        }
        protected Road doInBackground(Object... params) {
            roadManager = new OSRMRoadManager();
            return roadManager.getRoad(waypoints);
        }

        protected void onPostExecute(Road result) {
            mRoad = result;
            List<Overlay> mapOverlays = map.getOverlays();
            if (mRoad == null)
                return;
            if (mRoadOverlay != null){
                mRoadOverlay = null;
            }
            mRoadOverlay = roadManager.buildRoadOverlay(mRoad, context);
            mapOverlays.add(mRoadOverlay);
            map.invalidate();
        }
    }
}