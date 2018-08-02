package com.neulion.android.upnpcast;

import android.os.Handler;
import android.os.Looper;

import com.neulion.android.upnpcast.device.CastDevice;
import com.neulion.android.upnpcast.util.DeviceUtil;
import com.neulion.android.upnpcast.util.ILogger;
import com.neulion.android.upnpcast.util.ILogger.DefaultLoggerImpl;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

import java.util.ArrayList;
import java.util.List;

/**
 * User: liuwei(wei.liu@neulion.com.com)
 * Date: 2018-07-02
 * Time: 14:12
 */
public class NLDeviceRegistryListener extends DefaultRegistryListener
{
    private ILogger mLog = new DefaultLoggerImpl(this);

    private Handler mHandler = new Handler(Looper.getMainLooper());

    /* Discovery performance optimization for very slow Android devices! */
    @Override
    public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device)
    {
        mLog.i("DeviceDiscovery:" + DeviceUtil.parseDevice(device));
    }

    @Override
    public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final Exception ex)
    {
        mLog.e("DeviceDiscoveryFailed:" + DeviceUtil.parseDevice(device));
    }
    /* End of optimization, you can remove the whole block if your Android handset is fast (>= 600 Mhz) */

    @Override
    public void deviceAdded(Registry registry, final Device device)
    {
        mLog.i("++ deviceAdded:" + DeviceUtil.parseDevice(device));

        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                CastDevice castDevice = new CastDevice(device);

                for (OnRegistryDeviceListener listener : mOnRegistryDeviceListener)
                {
                    if (listener != null) // remove listener empty list now!
                    {
                        listener.onDeviceAdded(castDevice);
                    }
                }
            }
        });
    }

    @Override
    public void deviceRemoved(Registry registry, final Device device)
    {
        mLog.w("-- deviceRemoved:" + DeviceUtil.parseDevice(device));

        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                CastDevice castDevice = new CastDevice(device);

                for (OnRegistryDeviceListener listener : mOnRegistryDeviceListener)
                {
                    if (listener != null) // remove listener empty list now!
                    {
                        listener.onDeviceRemoved(castDevice);
                    }
                }
            }
        });
    }

    private List<OnRegistryDeviceListener> mOnRegistryDeviceListener = new ArrayList<>();

    public void addRegistryDeviceListener(OnRegistryDeviceListener listener)
    {
        if (mOnRegistryDeviceListener != null && listener != null && !mOnRegistryDeviceListener.contains(listener))
        {
            mOnRegistryDeviceListener.add(listener);
        }
    }

    public void removeRegistryListener(OnRegistryDeviceListener listener)
    {
        if (mOnRegistryDeviceListener != null && listener != null && mOnRegistryDeviceListener.contains(listener))
        {
            mOnRegistryDeviceListener.remove(listener);
        }
    }

    public interface OnRegistryDeviceListener
    {
        void onDeviceAdded(CastDevice device);

        void onDeviceRemoved(CastDevice device);
    }
}
