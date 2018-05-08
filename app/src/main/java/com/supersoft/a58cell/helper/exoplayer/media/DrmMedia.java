package com.supersoft.a58cell.helper.exoplayer.media;

/**
 * Created by Centaury on 19/04/2018.
 */
public interface DrmMedia {
    String getType();

    String getLicenseUrl();

    String[] getKeyRequestPropertiesArray();
}
