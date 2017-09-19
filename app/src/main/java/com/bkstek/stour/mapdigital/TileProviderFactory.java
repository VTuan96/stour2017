package com.bkstek.stour.mapdigital;

import com.google.android.gms.maps.model.TileProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

/**
 * Created by duy tho le on 9/18/2017.
 */

public class TileProviderFactory {

//    private static final String GEOSERVER_FORMAT =
//            "http://yourApp.org/geoserver/wms" +
//                    "?service=WMS" +
//                    "&version=1.1.1" +
//                    "&request=GetMap" +
//                    "&layers=yourLayer" +
//                    "&bbox=%f,%f,%f,%f" +
//                    "&width=256" +
//                    "&height=256" +
//                    "&srs=EPSG:900913" +
//                    "&format=image/png" +
//                    "&transparent=true";

    private static final String GEOSERVER_FORMAT =
            "http://sanslab.vn:5588/geoserver/KVBK/wms?service=WMS&version=1.1.1&request=GetMap&layers=KVBK:road_split&bbox=%f,%f,%f,%f&width=768&height=427&srs=EPSG:900913&&format=image/png&transparent=true";

    // return a geoserver wms tile layer
    public static TileProvider getTileProvider() {
        TileProvider tileProvider = new WMSTileProvider(768, 427) {

            @Override
            public synchronized URL getTileUrl(int x, int y, int zoom) {
                double[] bbox = getBoundingBox(x, y, zoom);
                String s = String.format(Locale.US, GEOSERVER_FORMAT, bbox[MINX],
                        bbox[MINY], bbox[MAXX], bbox[MAXY]);
                URL url = null;
                try {
                    url = new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
                return url;
            }
        };
        return tileProvider;
    }

}
