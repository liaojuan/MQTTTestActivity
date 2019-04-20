package com.example.asus.mqtttestactivity;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttManager {
    private static MqttManager mqttUtil;
    private Context mContext;

    //客户端
    private MqttAndroidClient client;

    //连接选项
    private MqttConnectOptions mqttConnectOptions;

    public static MqttManager getInstall(Context context) {
        if (mqttUtil == null)
            mqttUtil = new MqttManager(context);
        return mqttUtil;
    }


    public MqttManager(Context context) {
        mContext = context.getApplicationContext();
        initMqtt();
    }

    public void initMqtt(){
        //第一个参数上下文，第二个 服务器地址，注意下面的格式!!!! 第三个是 客户端ID，注意 必须唯一，如果存在此ID连接了服务器。那么连接失败！
        client = new MqttAndroidClient(mContext, "tcp://101.132.24.242:1884", "androidID"+System.currentTimeMillis());

        //配置连接信息
        mqttConnectOptions=new MqttConnectOptions();
        //是否清除缓存
        mqttConnectOptions.setCleanSession(true);
        //是否重连
        mqttConnectOptions.setAutomaticReconnect(true);
        //设置心跳,30s
        mqttConnectOptions.setKeepAliveInterval(30);
        //登陆的名字,根据服务器要求，一般不用写
//        mqttConnectOptions.setUserName("xuhong");
//        //登陆的密码,
//        mqttConnectOptions.setPassword("123545".toCharArray());
        //超时时间
        mqttConnectOptions.setConnectionTimeout(30);
        //监听服务器发来的 信息
        client.setCallback(mqttCallback);

        doConnect();

    }

    private MqttCallback mqttCallback = new MqttCallback(){
        @Override
        public void connectionLost(Throwable cause) {
            //连接丢失异常
            Log.e("Mqtt","---mqtt----connectionLost");
            doConnect();
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            //收到服务器的信息
            Log.e("Mqtt","---mqtt----messageArrived");
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            Log.e("Mqtt","---mqtt----deliveryComplete");
        }
    };

    /**
     * 连接
     */
    public void doConnect(){
        //开始连接服务器
        try {
            client.connect(mqttConnectOptions, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.e("Mqtt","---mqtt----connect--success");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("Mqtt","---mqtt----connect--failure");
                    doConnect();
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
            //连接失败
        }
    }

    /**
     * 订阅
     */
    public void subrice(final TextView tvShow) {
        if (client != null){
            try {
                client.subscribe("/xuhong", 0, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.e("Mqtt","---mqtt----订阅--success");
                        tvShow.setText("订阅成功！");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.e("Mqtt","---mqtt----订阅--failure");
                        tvShow.setText("订阅失败！" + exception);
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送消息
     */
    public void sendMessage(final TextView tvShow){
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload("hello xuhong".getBytes());

        try {
            client.publish("/xuhong", mqttMessage, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.e("Mqtt","---mqtt----发布--success");
                    tvShow.setText("发布消息成功");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("Mqtt","---mqtt----发布--failure");
                    tvShow.setText("发布消息失败");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            Log.e("Mqtt","---mqtt----发布--exception");
            tvShow.setText("发布消息失败");
        }
    }

    public void disConnect(){
        try {
            client.disconnect();
            client = null;
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
