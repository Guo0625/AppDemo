package com.example.appdemo;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.qq.e.base.IStart;
import com.vimedia.core.common.utils.LogUtil;
import com.vimedia.game.AdLiveData;
import com.vimedia.game.GameEvent;
import com.vimedia.game.GameManager;
import com.vimedia.game.LifecycleManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class MainActivity extends Activity implements LifecycleOwner, View.OnClickListener, IStart {

    GameManager gameManager;

    LifecycleManager gameLifecycle = new LifecycleManager(this);

    Button btn1, btn2, btn3;
    Button exit;

    @Override
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        btn1 = (Button) findViewById(R.id.button1);
        btn2 = (Button) findViewById(R.id.button2);
        btn3 = (Button) findViewById(R.id.button3);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);

        exit = (Button) findViewById(R.id.exit);
        exit.setOnClickListener(this);

        EventBus.getDefault().register(this);
        initViewModel();
        gameLifecycle.onCreate(bundle, this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.exit:
                GameManager.getInstance().openAd("exit_game");
                break;
            case R.id.button1:
                GameManager.getInstance().openAd("banner");
                break;
            case R.id.button2:
                GameManager.getInstance().closeAd("banner");
                break;
            case R.id.button3:
                GameManager.getInstance().openAd("exit_game");
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        gameLifecycle.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameManager.setKeyEnable(gameManager.isKey(), 600);
        gameLifecycle.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameManager.setKeyEnable(false, 0);
        gameLifecycle.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameLifecycle.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        gameLifecycle.onDestroy();
        if (!isTaskRoot()) {
            return;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!gameManager.getKeyEnable()) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (!gameManager.getKeyEnable()) {
            return false;
        }
        return super.onKeyUp(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        gameLifecycle.onBackPressed();
    }

    @Override
    public void onWindowFocusChanged(boolean b) {
        super.onWindowFocusChanged(b);
        gameLifecycle.onWindowFocusChanged(b);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        gameLifecycle.onNewIntent(this, intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        gameLifecycle.onActivityResult(this, requestCode, resultCode, data);
    }

    /**
     * ?????????
     */
    void initViewModel() {
        gameManager = GameManager.getInstance();
        gameManager.initContext(this);
        getLifecycle().addObserver(gameManager);
    }


    /**
     * ??????????????????
     *
     * @param result
     * @param data
     */
    public void AdResultCall(boolean result, AdLiveData data) {
    }

    /**
     * ??????????????????
     *
     * @param adName
     */
    public void AdClickedCall(final String adName) {
        Log.e("AdClickedCall" ,"AdClickedCall");
        GameManager.getInstance().openAd("banner");
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return gameLifecycle.getLifecycle();
    }

    /**
     * ??????GameEvent??????
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(GameEvent event) {
        if (event != null) {
            Object[] objects = event.getObjs();
            switch (event.getEventType()) {
                case GameEvent.GAME_EVENT_LOGIN:
                    loginResultCallCreator((boolean) objects[0]);
                    break;
                case GameEvent.GAME_EVENT_LOGIN_INFO:
                case GameEvent.GAME_EVENT_USERINFO:
                    getUserInfoResultCallCreator((boolean) objects[0], (String) objects[1]);
                    break;
                case GameEvent.GAME_EVENT_GAME_PARAM_INFO:
                    requestGameParamCallCreator((String) objects[0], (int) objects[1]);
                    break;
                case GameEvent.GAME_EVENT_CASH_INFO:
                    requestCashInfoCallCreator((int) objects[0], (int) objects[1], (String) objects[2]);
                    break;
                case GameEvent.GAME_EVENT_INTEGRAL_DATA:
                    requestIntegralDataCallCreator((String) objects[0], (String) objects[1]);
                    break;
                case GameEvent.GAME_EVENT_NET_CASH_INFO:
                    requestNetCashInfoCallCreator((int) objects[0], (String) objects[1], (String) objects[2]);
                    break;
                case GameEvent.GAME_EVENT_INVITE_INFO:
                    requestInviteInfoCallCreator((int) objects[0], (String) objects[1], (String) objects[2]);
                    break;
                case GameEvent.GAME_EVENT_HB_GROUP:
                    requestHbGroupInfoCallCreator((int) objects[0], (String) objects[1], (String) objects[2]);
                    break;
                case GameEvent.GAME_EVENT_PVP:
                    requestPvpInfoCallCreator((int) objects[0], (String) objects[1], (String) objects[2]);
                    break;
                case GameEvent.GAME_EVENT_CDKEY:
                    requestCDKeyCallCreator((String) objects[0], (String) objects[1], (String) objects[2]);
                    break;
                case GameEvent.GAME_EVENT_RESULT_PAY:
                    PayResultCallUnity((String) objects[0], (int) objects[1], (String) objects[2]);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * ??????AdLiveData??????
     *
     * @param adLiveData
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void adevent(AdLiveData adLiveData) {
        if (adLiveData != null) {
            String adName = adLiveData.getAdName();
            String adType = adLiveData.getAdType();
            int adResult = adLiveData.getAdResult();
            LogUtil.e("LayaWbActivity", "???????????? code:" + adLiveData.getCode() + ",adName:" + adName);

            if (adLiveData.getCode() == AdLiveData.AD_CLICK_CODE) {
                AdClickedCall(adName);
            } else if (adLiveData.getCode() == AdLiveData.AD_OPENRESULT_CODE) {
                if (!adType.equals("banner"))//banner????????????????????????????????????unity
                {
                    LogUtil.e("LayaWbActivity", "???????????? adResult:" + adResult);
                    if (adResult == AdLiveData.ADRESULT_SUCCESS) {
                        AdResultCall(true, adLiveData);
                    } else {
                        AdResultCall(false, adLiveData);
                    }
                }
            }
        }
    }

    /**
     * ?????????????????????
     *
     * @param data
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(final String data) {
        if (!TextUtils.isEmpty(data) && data.contains("outOrderId#")) {
            final String outOrderId = data.split("#")[1];

        }
    }

    public void requestGameParamCallCreator(final String info, final int result) {
        //result 0?????????0??????
    }

    /**
     * ?????????????????? Creator
     *
     * @param callType
     * @param info
     */
    public void requestIntegralDataCallCreator(final String callType, final String info) {
    }

    /**
     * ??????????????? ??????creator
     *
     * @param result
     * @param action
     * @param desc
     */
    public void requestNetCashInfoCallCreator(final int result, final String action, final String desc) {
    }

    /**
     * ???????????? ??????creator
     *
     * @param result
     * @param action
     * @param desc
     */
    public void requestInviteInfoCallCreator(final int result, final String action, final String desc) {
    }

    /**
     * ????????????
     *
     * @param result
     */
    public void loginResultCallCreator(final boolean result) {
    }

    /**
     * ??????????????????
     *
     * @param result
     * @param info
     */
    public void getUserInfoResultCallCreator(final boolean result, final String info) {
    }

    /**
     * ????????? ??????creator
     *
     * @param result
     * @param action
     * @param info
     */
    public void requestHbGroupInfoCallCreator(final int result, final String action, final String info) {
    }

    /**
     * ????????????
     */
    public void requestCashInfoCallCreator(final int tag, final int status, final String info) {
    }

    /**
     * ??????????????????
     *
     * @return
     */
    public int getMusicVolume() {
        return GameManager.getInstance().getMusicVolume();
    }

    /**
     * ?????? ??????
     *
     * @param result
     * @param action
     * @param info
     */
    public void requestPvpInfoCallCreator(final int result, final String action, final String info) {
    }

    /**
     * ????????? ??????
     *
     * @param price
     * @param status
     * @param msg
     */
    public void requestCDKeyCallCreator(final String price, final String status, final String msg) {
    }


    /**
     * ??????????????????
     */
    public ArrayList<Integer> payList = new ArrayList<Integer>();

    public int[] GetPayList() {
        int[] d = new int[payList.size()];
        for (int i = 0; i < payList.size(); i++) {
            d[i] = payList.get(i);
        }
        return d;
    }

    public void ClearPayList(int id) {
        for (int i = 0; i < payList.size(); i++) {
            if (payList.get(i) == id) {
                payList.remove(i);
                break;
            }
        }
    }

    /**
     * ?????????????????????unity
     *
     * @param result
     * @param id
     */
    public void PayResultCallUnity(String result, int id, String userData) {
        String params;
        params = "defult";
        if (result.equals("1")) {
            payList.add(id);
            params = "Paysuccess";
        } else if (result.equals("2")) {
            params = "Payfail";
        } else if (result.equals("3")) {
            params = "Paycancel";
        }
    }
}