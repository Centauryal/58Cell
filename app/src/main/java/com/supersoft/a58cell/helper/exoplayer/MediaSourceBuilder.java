package com.supersoft.a58cell.helper.exoplayer;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;

import static android.text.TextUtils.isEmpty;
import static com.supersoft.a58cell.helper.exoplayer.ToroUtil.LIB_NAME;

/**
 * Created by Centaury on 19/04/2018.
 */
@SuppressWarnings({ "WeakerAccess", "unused" }) //
public class MediaSourceBuilder {

    private final Context context;  // Application context
    private final Uri mediaUri;
    private final String extension;
    private final Handler handler;

    /**
     * Required constructor for a {@link MediaSourceBuilder}.
     *
     * @param context the Application context, used by {@link ExoPlayer} components.
     * @param mediaUri the Uri of media content.
     * @param extension expected extension of the media content.
     * @param handler the {@link Handler} used by {@link ExoPlayer} components.
     */
    public MediaSourceBuilder(@NonNull Context context, @NonNull Uri mediaUri,
                              @Nullable String extension, @Nullable Handler handler) {
        this.context = context.getApplicationContext();
        this.mediaUri = mediaUri;
        this.extension = extension;
        this.handler = handler;
    }

    public MediaSourceBuilder(Context context, Uri mediaUri, String extension) {
        this(context, mediaUri, extension, new Handler());
    }

    public MediaSourceBuilder(Context context, Uri mediaUri) {
        this(context, mediaUri, null);
    }

    public MediaSource build() {
        return this.build(new DefaultBandwidthMeter(this.handler, null));
    }

    @SuppressWarnings("SameParameterValue") //
    public MediaSource build(BandwidthMeter bandwidthMeter) {
        TransferListener<? super DataSource> transferListener;
        try {
            //noinspection unchecked
            transferListener =  //
                    bandwidthMeter != null ? (TransferListener<? super DataSource>) bandwidthMeter : null;
        } catch (ClassCastException er) {
            throw new IllegalArgumentException("BandwidthMeter must implement TransferListener.");
        }

        DataSource.Factory mediaDataSourceFactory = buildDataSourceFactory(transferListener);

        int type =
                Util.inferContentType(isEmpty(extension) ? mediaUri.getLastPathSegment() : "." + extension);
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource(mediaUri, buildDataSourceFactory(transferListener),
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory), handler, null);
            case C.TYPE_DASH:
                return new DashMediaSource(mediaUri, buildDataSourceFactory(transferListener),
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory), handler, null);
            case C.TYPE_HLS:
                return new HlsMediaSource(mediaUri, mediaDataSourceFactory, handler, null);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource(mediaUri, mediaDataSourceFactory,
                        new DefaultExtractorsFactory(), handler, null);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private DataSource.Factory buildDataSourceFactory(TransferListener<? super DataSource> listener) {
        return new DefaultDataSourceFactory(context, listener,
                new DefaultHttpDataSourceFactory(Util.getUserAgent(context, LIB_NAME), listener));
    }
}
