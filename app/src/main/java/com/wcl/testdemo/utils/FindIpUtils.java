package com.wcl.testdemo.utils;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;

public final class FindIpUtils {

    private static final String SERVICE_NAME = "WCL";
    private static final String SERVICE_TYPE = "_http._tcp";//语法为“_<protocol>._<transportlayer>”
    /**
     * Filed Comment:Nsd服务管理.
     */
    private static NsdManager sNsdManager;
    /**
     * Filed Comment:服务端的注册监听器.
     */
    private static NsdManager.RegistrationListener sServiceListener;

    /**
     * Filed Comment:客户端的发现监听器.
     */
    private static NsdManager.DiscoveryListener sClientListener;


    /**
     * 服务端:开始发现.
     *
     * @param port 要曝露的端口号
     */
    public static void startService(int port) {
        //1.创建ServerSocket,获取端口.
//        ServerSocket serverSocket = null;
//        int servicePort = 0;
//        try {
//            serverSocket = new ServerSocket(0);//设为0,会自动获取没有占用的端口.
//            servicePort = serverSocket.getLocalPort();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //2.创建NsdServiceInfo对象并填充.
        NsdServiceInfo nsdServiceInfo = new NsdServiceInfo();
        nsdServiceInfo.setServiceName(SERVICE_NAME);
        nsdServiceInfo.setServiceType(SERVICE_TYPE);
        nsdServiceInfo.setPort(port);
        //3.NsdManager初始化.
        if (sNsdManager == null) {
            sNsdManager = (NsdManager) Utils.getApp().getSystemService(Context.NSD_SERVICE);
        }
        if (sServiceListener == null) {
            sServiceListener = new NsdManager.RegistrationListener() {

                @Override
                public void onServiceRegistered(NsdServiceInfo nsdServiceInfo) {//注册服务成功
                    LogUtils.i("服务端: 监听注册[成功]: ", nsdServiceInfo);
                }

                @Override
                public void onRegistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {//注册失败回调
                    LogUtils.e("服务端: 监听注册[失败]: ", nsdServiceInfo, i);
                    startService(port); //重试.
                }

                @Override
                public void onServiceUnregistered(NsdServiceInfo nsdServiceInfo) {//取消注册成功
                    LogUtils.v("服务端: 监听[取消]: ", nsdServiceInfo);
                }

                @Override
                public void onUnregistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {//取消注册失败
                    LogUtils.e("服务端: 监听[取消失败]: ", nsdServiceInfo);
                    stopService();
                }
            };
            sNsdManager.registerService(nsdServiceInfo, NsdManager.PROTOCOL_DNS_SD, sServiceListener);//不能重复设置同一个listener.
        } else {
            stopService();
            startService(port);
        }
    }

    /**
     * 服务端:停止发现.
     */
    public static void stopService() {
        if (sNsdManager != null) {
            if (sServiceListener != null) {
                sNsdManager.unregisterService(sServiceListener);
            }
            sServiceListener = null;
        }
    }

    /**
     * 客户端:开始发现.
     */
    public static void startClient(ClientListener listener) {
        //1.NsdManager初始化.
        if (sNsdManager == null) {
            sNsdManager = (NsdManager) Utils.getApp().getSystemService(Context.NSD_SERVICE);
        }
        //2.发现周边的NSD相关网络
        if (sClientListener == null) {
            sClientListener = new NsdManager.DiscoveryListener() {

                @Override
                public void onDiscoveryStarted(String s) {//开始搜索
                    LogUtils.v("客户端: 开始搜索,尝试发现设备: " + s);
                }

                @Override
                public void onServiceFound(NsdServiceInfo nsdServiceInfo) {//搜索到服务信息
                    //这里的nsdServiceInfo只能获取到名字,ip和端口都不能获取到,要想获取到需要调用NsdManager.resolveService方法
                    if ((!TextUtils.isEmpty(nsdServiceInfo.getServiceType()))
                            && nsdServiceInfo.getServiceType().startsWith(SERVICE_TYPE)
                            && TextUtils.equals(nsdServiceInfo.getServiceName().trim(), SERVICE_NAME)) {
                        LogUtils.d("客户端: 搜索到匹配的服务信息: ", nsdServiceInfo);

                        if (sNsdManager != null) {
                            sNsdManager.resolveService(nsdServiceInfo, new NsdManager.ResolveListener() {
                                @Override
                                public void onResolveFailed(NsdServiceInfo nsdServiceInfo, int i) {
                                    LogUtils.e("客户端: 服务端的信息解析失败: ", nsdServiceInfo, i);
                                }

                                @Override
                                public void onServiceResolved(NsdServiceInfo nsdServiceInfo) {
                                    LogUtils.i("客户端: 服务端的信息解析完成: ",
                                            nsdServiceInfo.getServiceName(),
                                            nsdServiceInfo.getServiceType(),
                                            "端口号: " + nsdServiceInfo.getPort(),
                                            "HostName: " + nsdServiceInfo.getHost().getHostName() + " HostAddress: " + nsdServiceInfo.getHost().getHostAddress() + " Address: " + nsdServiceInfo.getHost().getAddress());
                                    listener.onFind(nsdServiceInfo);
                                    stopClient();
                                }
                            });//主动调用resolveService方法
                        }
                    }
                }

                @Override
                public void onServiceLost(NsdServiceInfo nsdServiceInfo) {//服务丢失，也就是对面的服务断开了
                    LogUtils.e("服务丢失: ", nsdServiceInfo);
                }

                @Override
                public void onDiscoveryStopped(String s) {//停止搜索
                    LogUtils.v("停止搜索: " + s);
                }

                @Override
                public void onStartDiscoveryFailed(String s, int i) {//开始搜索失败
                    LogUtils.e("开始搜索失败: ", s, i);
                    stopClient();
                }

                @Override
                public void onStopDiscoveryFailed(String s, int i) {//结束搜索失败
                    LogUtils.e("结束搜索失败: ", s, i);
                    stopClient();
                }
            };
            sNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, sClientListener);//不能重复设置同一个listener.
        } else {
            stopClient();
            startClient(listener);
        }
    }

    /**
     * 客户端:停止发现.
     */
    public static void stopClient() {
        if (sNsdManager != null) {
            if (sClientListener != null) {
                sNsdManager.stopServiceDiscovery(sClientListener);
            }
            sClientListener = null;
        }
    }

    /**
     * @Author WCL
     * @Date 2023/3/30 18:40
     * @Version
     * @Description 客户端发现服务端时的回调.
     */
    public interface ClientListener {
        void onFind(NsdServiceInfo nsdServiceInfo);
    }

}
