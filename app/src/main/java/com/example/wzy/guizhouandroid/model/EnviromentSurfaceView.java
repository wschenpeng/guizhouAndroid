package com.example.wzy.guizhouandroid.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.example.wzy.guizhouandroid.R;
import com.example.wzy.guizhouandroid.activity.Main_Iot_Data_Activity;
import com.example.wzy.guizhouandroid.activity.Main_Iot_Video_Activity;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;

@SuppressLint("NewApi")
public class EnviromentSurfaceView extends SurfaceView implements Callback {

    private final String TAG = "PlaySurfaceView";
    private int m_iWidth = 0;
    public int getM_iWidth()
    {
        return m_iWidth;
    }

    public void setM_iWidth(int m_iWidth)
    {
        this.m_iWidth = m_iWidth;
    }

    public int getM_iHeight()
    {
        return m_iHeight;
    }

    public void setM_iHeight(int m_iHeight)
    {
        this.m_iHeight = m_iHeight;
    }

    private int m_iHeight = 0;
    public int m_iPreviewHandle = -1;
    public SurfaceHolder m_hHolder;
    public boolean bCreate = false;
    public int m_lUserID = -1;
    public int m_iChan = 0;

    public EnviromentSurfaceView(Main_Iot_Data_Activity demoActivity)
    {
        super((Context) demoActivity);
        
        // TODO Auto-generated constructor stub
        m_hHolder = this.getHolder();
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)
    {
        // TODO Auto-generated method stub
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        System.out.println("surfaceChanged");
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0)
    {
        // TODO Auto-generated method stub
        bCreate = true;
        System.out.println("surfaceCreated");

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0)
    {
        // TODO Auto-generated method stub
        System.out.println("surfaceDestroyed");
        bCreate = false;

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
    {
        super.setMeasuredDimension(m_iWidth - 1, m_iHeight - 1);
    }

    public void setParam(int nScreenSize) 
    {
//        m_iWidth = nScreenSize;
//        m_iHeight = (m_iWidth * 4) / 4;
        /*设置获取到的宽度为屏幕宽度*/
        m_iWidth = nScreenSize;
        /*设置获取到的高度为300dp*/
        m_iHeight = getResources().getDimensionPixelOffset(R.dimen.dp300);
    }


    /*开始预览*/
    public void startPreview(int iUserID, int iChan) 
    {
        Log.i(TAG, "preview channel:" + iChan);
        while (!bCreate) 
        {
            try 
            {
                Thread.sleep(100);
                Log.i(TAG, "wait for surface create");
            } 
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        /*预览参数*/
        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = iChan;
        previewInfo.dwStreamType = 1; // substream
        previewInfo.bBlocked = 1;
        previewInfo.hHwnd = m_hHolder;
        
        // HCNetSDK start preview
        m_iPreviewHandle = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(iUserID, previewInfo, null);
        if (m_iPreviewHandle < 0) 
        {
            Log.e(TAG, "NET_DVR_RealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
        }        
    }

    public void stopPreview() 
    {
        HCNetSDK.getInstance().NET_DVR_StopRealPlay(m_iPreviewHandle);
    }
}
