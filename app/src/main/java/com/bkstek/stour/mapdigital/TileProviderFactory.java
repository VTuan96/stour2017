package com.bkstek.stour.mapdigital;

import com.google.android.gms.maps.model.TileProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

/**
 * Created by duy tho le on 9/18/2017.
 */

public class TileProviderFactory {
    
    // return a geoserver wms tile layer
    public static TileProvider getTileProvider(final String str) {
        TileProvider tileProvider = new WMSTileProvider(768, 427) {

            @Override
            public synchronized URL getTileUrl(int x, int y, int zoom) {
                double[] bbox = getBoundingBox(x, y, zoom);
                String s = String.format(Locale.US, str, bbox[MINX],
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
