package com.example.wzy.guizhouandroid.model;

import android.util.Log;

import com.example.wzy.guizhouandroid.jna.HCNetSDKByJNA;
import com.example.wzy.guizhouandroid.jna.HCNetSDKJNAInstance;
import com.hikvision.netsdk.INT_PTR;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;


public class JNATest {

	static JNATest jnaTest = new JNATest();
	public int m_lAlarmID = -1;
	public int m_lAlarmIDV41 = -1;
	public int m_lLongConfig = -1;
	public int m_lDisplay = -1;
	public int m_UploadHandle = -1;
	public int m_UploadStatus = -1;
	
	public void Test_GetSDKVersion()
	{
		System.out.println("get sdk version by jna:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetSDKVersion());
	}
	
	public void Test_TimeCfg(int iUserID)
	{
		HCNetSDKByJNA.NET_DVR_TIME time = new HCNetSDKByJNA.NET_DVR_TIME();
		Pointer lpPicConfig = time.getPointer();
		IntByReference pInt = new IntByReference(0);
		boolean bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_GetDVRConfig(iUserID, HCNetSDKByJNA.NET_DVR_GET_TIMECFG, 0, lpPicConfig, time.size(), pInt);
		time.read();
		if(bRet)
		{
			System.out.println("dvr time:" + time.toString());
		}
		
		time.dwHour = time.dwHour + 1;
		time.write();
		bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_SetDVRConfig(iUserID, HCNetSDKByJNA.NET_DVR_SET_TIMECFG, 0, lpPicConfig, time.size());
		if(!bRet)
		{
			System.out.println("HCNetSDKByJNA.NET_DVR_SET_TIMECFG succ");
		}
	}
	
	public void Test_MultiStreamCompression(int iUserID)
	{
		HCNetSDKByJNA.NET_DVR_MULTI_STREAM_COMPRESSIONCFG_COND[] compressCond = (HCNetSDKByJNA.NET_DVR_MULTI_STREAM_COMPRESSIONCFG_COND[])new HCNetSDKByJNA.NET_DVR_MULTI_STREAM_COMPRESSIONCFG_COND().toArray(2);
		HCNetSDKByJNA.NET_DVR_MULTI_STREAM_COMPRESSIONCFG[]	compress = (HCNetSDKByJNA.NET_DVR_MULTI_STREAM_COMPRESSIONCFG[])new HCNetSDKByJNA.NET_DVR_MULTI_STREAM_COMPRESSIONCFG().toArray(2);
		Pointer lpCond = compressCond[0].getPointer();
		Pointer lpParam = compress[0].getPointer();
		
		HCNetSDKByJNA.INT_ARRAY pInt = new HCNetSDKByJNA.INT_ARRAY(2);    		
		
		pInt.iValue[0] = -1;
		pInt.iValue[1] = -1;
		System.out.println("NET_DVR_GetDeviceConfig status:" + pInt.iValue[0] + pInt.iValue[1]);
		
		compressCond[0].dwSize = compressCond[0].size();
		compressCond[0].dwStreamType = 0;
		compressCond[0].struStreamInfo.dwSize = compressCond[0].struStreamInfo.size();
		compressCond[0].struStreamInfo.dwChannel = 1;
		compressCond[0].write();
		
		compressCond[1].dwSize = compressCond[1].size();
		compressCond[1].dwStreamType = 1;
		compressCond[1].struStreamInfo.dwSize = compressCond[1].struStreamInfo.size();
		compressCond[1].struStreamInfo.dwChannel = 1;
		compressCond[1].write();
		boolean bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_GetDeviceConfig(iUserID, HCNetSDKByJNA.NET_DVR_GET_MULTI_STREAM_COMPRESSIONCFG, 2, 
				lpCond, compressCond[0].size() * 2, pInt.getPointer(), lpParam, compress[0].size() * 2);
		compress[0].read();
		compress[1].read();
		pInt.read();
		if(!bRet)
		{
			System.out.println("NET_DVR_GET_MULTI_STREAM_COMPRESSIONCFG failed:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
		}    		
		else
		{
			System.out.println("NET_DVR_GetDeviceConfig status:" + pInt.iValue[0] + pInt.iValue[1]);
		}
	}
	
	public static class FMSGCallBack implements HCNetSDKByJNA.FMSGCallBack
	{

		@Override
		public void invoke(int lCommand, HCNetSDKByJNA.NET_DVR_ALARMER pAlarmer,
                           Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
			// TODO Auto-generated method stub
			System.out.println("alarm type:" + lCommand);
			if(lCommand == HCNetSDKByJNA.COMM_ALARM_V30)
			{
				HCNetSDKByJNA.NET_DVR_ALARMINFO_V30	struAlarmInfo = new HCNetSDKByJNA.NET_DVR_ALARMINFO_V30(pAlarmInfo);
				struAlarmInfo.read();
				System.out.println("COMM_ALARM_V30 alarm type:" + struAlarmInfo.dwAlarmType);
			}
			else if(lCommand == HCNetSDKByJNA.COMM_ALARM_V40)
			{
				HCNetSDKByJNA.NET_DVR_ALARMINFO_V40	struAlarmInfo = new HCNetSDKByJNA.NET_DVR_ALARMINFO_V40(pAlarmInfo);
				struAlarmInfo.read();
				System.out.println("COMM_ALARM_V40 alarm type:" + struAlarmInfo.struAlarmFixedHeader.dwAlarmType);
			}
			else if(lCommand == HCNetSDKByJNA.COMM_UPLOAD_PLATE_RESULT)
			{
				HCNetSDKByJNA.NET_DVR_PLATE_RESULT	struAlarmInfo = new HCNetSDKByJNA.NET_DVR_PLATE_RESULT(pAlarmInfo);
				struAlarmInfo.read();
				
    		    try {
    		    	SimpleDateFormat sDateFormat   =   new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
        		    String date   =   sDateFormat.format(new   java.util.Date());
    		    	FileOutputStream file = new FileOutputStream("/mnt/sdcard/" + date + ".bmp");
					file.write(struAlarmInfo.pBuffer1.getPointer().getByteArray(0, struAlarmInfo.dwPicLen), 0, struAlarmInfo.dwPicLen);
					file.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		    
				System.out.println("COMM_UPLOAD_PLATE_RESULT license:" + CommonMethod.toValidString(new String(struAlarmInfo.struPlateInfo.sLicense)));
			}
			else if(lCommand == HCNetSDKByJNA.COMM_ITS_PLATE_RESULT)
			{
				HCNetSDKByJNA.NET_ITS_PLATE_RESULT	struAlarmInfo = new HCNetSDKByJNA.NET_ITS_PLATE_RESULT(pAlarmInfo);
				struAlarmInfo.read();
				System.out.println("COMM_ITS_PLATE_RESULT license:" + CommonMethod.toValidString(new String(struAlarmInfo.struPlateInfo.sLicense)));
			}
			else if(lCommand == HCNetSDKByJNA.COMM_ALARM_RULE)
			{
				HCNetSDKByJNA.NET_VCA_RULE_ALARM	struAlarmInfo = new HCNetSDKByJNA.NET_VCA_RULE_ALARM(pAlarmInfo);
				struAlarmInfo.read();
				if(struAlarmInfo.struRuleInfo.wEventTypeEx == HCNetSDKByJNA.ENUM_VCA_EVENT_EXIT_AREA)
				{
					HCNetSDKByJNA.NET_VCA_AREA	struExit = new HCNetSDKByJNA.NET_VCA_AREA(struAlarmInfo.struRuleInfo.uEventParam.getPointer()); 
					struExit.read();
				}
				
				System.out.println("COMM_ALARM_RULE rule name:" + CommonMethod.toValidString(new String(struAlarmInfo.struRuleInfo.byRuleName)));
			}
			
			else if(lCommand == HCNetSDKByJNA.COMM_VEHICLE_CONTROL_ALARM)
			{
				HCNetSDKByJNA.NET_DVR_VEHICLE_CONTROL_ALARM	struAlarmInfo = new HCNetSDKByJNA.NET_DVR_VEHICLE_CONTROL_ALARM(pAlarmInfo);
				struAlarmInfo.read();
				System.out.println("NET_DVR_VEHICLE_CONTROL_ALARM license:" + struAlarmInfo.byListType + "byPlateType:" + struAlarmInfo.byPlateType +
						"sLicense:" +  CommonMethod.toValidString(new String(struAlarmInfo.sLicense)));
			}
			else if(lCommand == HCNetSDKByJNA.COMM_UPLOAD_FACESNAP_RESULT)
			{
				HCNetSDKByJNA.NET_VCA_FACESNAP_RESULT struFaceSnapAlarm = new HCNetSDKByJNA.NET_VCA_FACESNAP_RESULT(pAlarmInfo);
				struFaceSnapAlarm.read();
				System.out.println("COMM_UPLOAD_FACESNAP_RESULT dwFacePicID:" + struFaceSnapAlarm.dwFacePicID + "FaceScore:" + struFaceSnapAlarm.dwFaceScore);
			}
			else if(lCommand == HCNetSDKByJNA.COMM_ALARM_PDC)
			{
				HCNetSDKByJNA.NET_DVR_PDC_ALRAM_INFO struAlarmPdc = new HCNetSDKByJNA.NET_DVR_PDC_ALRAM_INFO(pAlarmInfo);
				struAlarmPdc.read();
				System.out.println("COMM_ALARM_PDC dwSnapFacePicID:" + struAlarmPdc.dwEnterNum);
			}
			else if(lCommand == HCNetSDKByJNA.COMM_ALARM_FACE_DETECTION)
			{
				HCNetSDKByJNA.NET_DVR_FACE_DETECTION struFaceDetect = new HCNetSDKByJNA.NET_DVR_FACE_DETECTION(pAlarmInfo);
				struFaceDetect.read();
				System.out.println("COMM_ALARM_FACE_DETECTION byFacePicNum:" + struFaceDetect.byFacePicNum);
			}
			else if(lCommand == HCNetSDKByJNA.COMM_SNAP_MATCH_ALARM)
			{
				HCNetSDKByJNA.NET_VCA_FACESNAP_MATCH_ALARM struFaceSnapMatchAlarm = new HCNetSDKByJNA.NET_VCA_FACESNAP_MATCH_ALARM(pAlarmInfo);
				struFaceSnapMatchAlarm.read();
				System.out.println("COMM_SNAP_MATCH_ALARM dwSnapFacePicID:" + struFaceSnapMatchAlarm.byMatchPicNum);
			}		
			
		}
	}
	
	private static final HCNetSDKByJNA.FMSGCallBack fMSFCallBack = new FMSGCallBack();
	
	public void Test_Alarm(int iUserID)
	{
		if(m_lAlarmID < 0)
		{
			Pointer pUser = null;
			if(!HCNetSDKJNAInstance.getInstance().NET_DVR_SetDVRMessageCallBack_V30(fMSFCallBack, pUser))
			{
				System.out.println("NET_DVR_SetDVRMessageCallBack_V30 failed:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}

			m_lAlarmID = HCNetSDKJNAInstance.getInstance().NET_DVR_SetupAlarmChan_V30(iUserID);
		}
		else
		{
			HCNetSDKJNAInstance.getInstance().NET_DVR_CloseAlarmChan_V30(m_lAlarmID);
			m_lAlarmID = -1;
		}
	}
	
	public void Test_Alarm_V41(int iUserID)
	{
		if(m_lAlarmIDV41 < 0)
		{		
			Pointer pUser = null;
			if(!HCNetSDKJNAInstance.getInstance().NET_DVR_SetDVRMessageCallBack_V30(fMSFCallBack, pUser))
			{
				System.out.println("NET_DVR_SetDVRMessageCallBack_V30 failed:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}

			HCNetSDKByJNA.NET_DVR_SETUPALARM_PARAM 	arlarmParam = new HCNetSDKByJNA.NET_DVR_SETUPALARM_PARAM();  
			arlarmParam.dwSize = arlarmParam.size();
			arlarmParam.byRetDevInfoVersion = 1;
			arlarmParam.byFaceAlarmDetection = 1;  // 1 - COMM_ALARM_FACE_DETECTION; 0 - COMM_UPLOAD_FACESNAP_RESULT
			arlarmParam.write();
			m_lAlarmIDV41 = HCNetSDKJNAInstance.getInstance().NET_DVR_SetupAlarmChan_V41(iUserID, arlarmParam.getPointer());
		}
		else
		{
			HCNetSDKJNAInstance.getInstance().NET_DVR_CloseAlarmChan_V30(m_lAlarmIDV41);
			m_lAlarmIDV41 = -1;
		}
	}
	
	public void Test_LoiteringDetection(int iUserID, int iChan)
	{
		System.out.println("Test_LoiteringDetection channel:" + iChan);
		
		HCNetSDKByJNA.INT_ARRAY ptrInt = new HCNetSDKByJNA.INT_ARRAY(1);
		ptrInt.iValue[0] = 1;
		ptrInt.write();
		
		HCNetSDKByJNA.NET_DVR_LOITERING_DETECTION struParam = new HCNetSDKByJNA.NET_DVR_LOITERING_DETECTION(); 
		HCNetSDKByJNA.BYTE_ARRAY ptrByte = new HCNetSDKByJNA.BYTE_ARRAY(HCNetSDKByJNA.BYTE_ARRAY_LEN);  
				
		HCNetSDKByJNA.NET_DVR_STD_CONFIG stdCfg = new HCNetSDKByJNA.NET_DVR_STD_CONFIG();
		stdCfg.lpCondBuffer = ptrInt.getPointer();
		stdCfg.dwCondSize = ptrInt.size();
		stdCfg.lpOutBuffer = struParam.getPointer();
		stdCfg.dwOutSize = struParam.size();
		stdCfg.lpStatusBuffer = ptrByte.getPointer();
		stdCfg.dwStatusSize = ptrByte.size();
		stdCfg.write();
		boolean bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_GetSTDConfig(iUserID, HCNetSDKByJNA.NET_DVR_GET_LOITERING_DETECTION, stdCfg.getPointer());
		stdCfg.read();
		struParam.read();
		ptrByte.read();
		if(!bRet)
		{
			System.out.println("NET_DVR_GET_LOITERING_DETECTION failed:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError() + CommonMethod.toValidString(new String(ptrByte.byValue)));
		}
		else
		{
			System.out.println("NET_DVR_GET_LOITERING_DETECTION succ");
		}
	}
	
	
	
	public static class FRemoteConfigCallBack implements HCNetSDKByJNA.fRemoteConfigCallback
	{

		@Override
		public void invoke(int dwType, Pointer lpBuffer, int dwBufLen,
                           Pointer pUserData) {
			// TODO Auto-generated method stub
			int dwVolume = lpBuffer.getInt(0);
			
			System.out.println("NET_DVR_START_GET_INPUTVOLUME volume:" + dwVolume);
		}
		
	}
	
	private static final HCNetSDKByJNA.fRemoteConfigCallback fCallback = new	FRemoteConfigCallBack();
	public void Test_GetInputVolume(int iUserID)
	{
		if(m_lLongConfig == -1)
		{
			HCNetSDKByJNA.NET_DVR_INPUTVOLUME struInputVolume = new HCNetSDKByJNA.NET_DVR_INPUTVOLUME();
			struInputVolume.dwSize = struInputVolume.size();
			struInputVolume.byAudioInputChan = 1;
			struInputVolume.write();
			
			m_lLongConfig = HCNetSDKJNAInstance.getInstance().NET_DVR_StartRemoteConfig(iUserID, HCNetSDKByJNA.NET_DVR_START_GET_INPUTVOLUME, struInputVolume.getPointer(), struInputVolume.size(), fCallback, null);
		}
		else
		{
			HCNetSDKJNAInstance.getInstance().NET_DVR_StopRemoteConfig(m_lLongConfig);
			m_lLongConfig = -1;
		}
	}
	
	private int m_lCardCfg = -1;
	public static class fCardCfgCallBack implements HCNetSDKByJNA.fRemoteConfigCallback
	{

		@Override
		public void invoke(int dwType, Pointer lpBuffer, int dwBufLen,
                           Pointer pUserData) {
			// TODO Auto-generated method stub
			if(dwType == HCNetSDKByJNA.NET_SDK_CALLBACK_TYPE_DATA)
			{
				HCNetSDKByJNA.NET_DVR_CARD_CFG struCardCfg = new HCNetSDKByJNA.NET_DVR_CARD_CFG(lpBuffer); 
				struCardCfg.read();
				System.out.println("card no:" + CommonMethod.toValidString(new String(struCardCfg.byCardNo)));
			}
		}		
	}
	
	private static final HCNetSDKByJNA.fRemoteConfigCallback fCardCallback = new	fCardCfgCallBack();
	public void Test_GetCardCfg(int iUserID)
	{
		if(m_lCardCfg == -1)
		{
			HCNetSDKByJNA.NET_DVR_CARD_CFG_COND struCardCond = new HCNetSDKByJNA.NET_DVR_CARD_CFG_COND();
			struCardCond.dwSize = struCardCond.size();
			struCardCond.dwCardNum = 0xffffffff;
			struCardCond.write();
			
			m_lCardCfg = HCNetSDKJNAInstance.getInstance().NET_DVR_StartRemoteConfig(iUserID, HCNetSDKByJNA.NET_DVR_GET_CARD_CFG, struCardCond.getPointer(), struCardCond.size(), fCardCallback, null);
			if(m_lCardCfg >= 0)
			{
				System.out.println("NET_DVR_StartRemoteConfig NET_DVR_GET_CARD_CFG succ");
			}
			else
			{
				System.out.println("NET_DVR_StartRemoteConfig NET_DVR_GET_CARD_CFG failed:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}
		}
		else
		{
			HCNetSDKJNAInstance.getInstance().NET_DVR_StopRemoteConfig(m_lCardCfg);
			m_lCardCfg = -1;
		}
	}
	
	public void Test_LED_Area(int iUserID)
	{		
		HCNetSDKByJNA.NET_DVR_STD_CONFIG stdCfg = new HCNetSDKByJNA.NET_DVR_STD_CONFIG();

		HCNetSDKByJNA.NET_DVR_LED_AREA_COND struCond = new HCNetSDKByJNA.NET_DVR_LED_AREA_COND();
		struCond.dwLEDAreaNo = 1;
		struCond.dwSize = struCond.size();
		struCond.dwVideoWallNo = 1;
		HCNetSDKByJNA.BYTE_ARRAY ptrByte = new HCNetSDKByJNA.BYTE_ARRAY(HCNetSDKByJNA.BYTE_ARRAY_LEN);
		struCond.write();
		
		HCNetSDKByJNA.NET_DVR_LED_AREA_INFO_LIST struInfoList = new HCNetSDKByJNA.NET_DVR_LED_AREA_INFO_LIST();
		struInfoList.dwSize = struInfoList.size();
		struInfoList.dwLEDAreaNum =1;
		HCNetSDKByJNA.NET_DVR_LED_AREA_INFO [] astruInfo = (HCNetSDKByJNA.NET_DVR_LED_AREA_INFO[])new HCNetSDKByJNA.NET_DVR_LED_AREA_INFO().toArray(128);
		astruInfo[0].dwSize = astruInfo[0].size();
		struInfoList.lpstruBuffer = null;//astruInfo[0].getPointer();
		struInfoList.dwBufferSize = 0;//astruInfo[0].size()*128;
		
		stdCfg.lpCondBuffer = struCond.getPointer();
		stdCfg.dwCondSize = struCond.size();
		stdCfg.lpInBuffer = null;
		stdCfg.dwInSize = 0;
		stdCfg.lpStatusBuffer = ptrByte.getPointer();
		stdCfg.dwStatusSize = ptrByte.size();
		
		stdCfg.lpOutBuffer = struInfoList.getPointer();
		stdCfg.dwOutSize = struInfoList.size();
		struInfoList.write();
		stdCfg.write();
		//astruInfo[0].write();
		boolean bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_GetSTDConfig(iUserID, HCNetSDKByJNA.NET_DVR_GET_LED_AREA_INFO_LIST, stdCfg.getPointer());		
		stdCfg.read();
		struInfoList.read();
		ptrByte.read();
		if(!bRet)
		{
			System.out.println("NET_DVR_GET_LED_AREA_INFO_LIST failed:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError() + CommonMethod.toValidString(new String(ptrByte.byValue)));
			return;
		}
		else
		{
			System.out.println("NET_DVR_GET_LED_AREA_INFO_LIST succ,num="+struInfoList.dwLEDAreaNum);
		}
		
		
	}
	
	public void TEST_Passive_Decode(int iUserID)
	{
		int iChan = 0x01010001;
		HCNetSDKByJNA.NET_DVR_MATRIX_PASSIVEMODE struPasMode = new HCNetSDKByJNA.NET_DVR_MATRIX_PASSIVEMODE();
		struPasMode.byStreamType=1;
		struPasMode.wPassivePort = 9000;
		struPasMode.wTransProtol=0;
		struPasMode.write();
		int iHandle = HCNetSDKJNAInstance.getInstance().NET_DVR_MatrixStartPassiveDecode(iUserID,iChan,struPasMode.getPointer());
		if(iHandle < 0)
		{
			System.out.println("NET_DVR_MATRIX_PASSIVEMODE failed:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			return;
		}
		else
		{
			System.out.println("NET_DVR_MATRIX_PASSIVEMODE succ");
		}
		
		HCNetSDKByJNA.BYTE_ARRAY ptrByte = new HCNetSDKByJNA.BYTE_ARRAY(30*HCNetSDKByJNA.BYTE_ARRAY_LEN);
		ptrByte.write();
		boolean bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_MatrixSendData(iHandle, ptrByte.getPointer(), ptrByte.size());
		if(!bRet)
		{
			System.out.println("NET_DVR_MatrixSendData failed:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError() + CommonMethod.toValidString(new String(ptrByte.byValue)));
			return;
		}
		else
		{
			System.out.println("NET_DVR_MatrixSendData succ");
		}
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int iRet = HCNetSDKJNAInstance.getInstance().NET_DVR_MatrixGetPassiveDecodeStatus(iHandle);
		if(iRet < 0)
		{
			System.out.println("NET_DVR_MatrixGetPassiveDecodeStatus fail="+HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
		}
		else
		{
			System.out.println("NET_DVR_MatrixGetPassiveDecodeStatus succ,iRet = "+iRet);
		}

		
		bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_MatrixStopPassiveDecode(iHandle);
		if(!bRet)
		{
			System.out.println("NET_DVR_MatrixStopPassiveDecode failed:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			return;
		}
		else
		{
			System.out.println("NET_DVR_MatrixStopPassiveDecode succ");
		}
	}
	
	public void Test_Login_V40()
	{
		HCNetSDKByJNA.NET_DVR_USER_LOGIN_INFO loginInfo = new HCNetSDKByJNA.NET_DVR_USER_LOGIN_INFO();
		System.arraycopy("10.17.132.118".getBytes(), 0, loginInfo.sDeviceAddress, 0, "10.17.132.118".length());
		System.arraycopy("admin".getBytes(), 0, loginInfo.sUserName, 0, "admin".length());
		System.arraycopy("hik12345".getBytes(), 0, loginInfo.sPassword, 0, "hik12345".length());
		loginInfo.wPort = (short)65534;
		HCNetSDKByJNA.NET_DVR_DEVICEINFO_V40	deviceInfo = new HCNetSDKByJNA.NET_DVR_DEVICEINFO_V40();
		loginInfo.write();
		int lUserID = HCNetSDKJNAInstance.getInstance().NET_DVR_Login_V40(loginInfo.getPointer(), deviceInfo.getPointer());
		if(lUserID < 0)
		{
			System.out.println("NET_DVR_Login_V40 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
		}
		else
		{
			deviceInfo.read();
			System.out.println("NET_DVR_Login_V40 succ with:" + lUserID);
		}
		
		if(!HCNetSDKJNAInstance.getInstance().NET_DVR_Logout(lUserID))
		{
			System.out.println("NET_DVR_Logout failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
		}
		else
		{
			System.out.println("NET_DVR_Logout succ");
		}
	}
	
	public void Test_Other(int iUserID)
	{
		HCNetSDKByJNA.NET_DVR_ALARMHOST_OTHER_STATUS_V50 struCfg = new HCNetSDKByJNA.NET_DVR_ALARMHOST_OTHER_STATUS_V50();
		Pointer lpPicConfig = struCfg.getPointer();
		IntByReference pInt = new IntByReference(0);
		boolean bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_GetDVRConfig(iUserID, HCNetSDKByJNA.NET_DVR_GET_ALARMHOST_OTHER_STATUS_V50, 0, lpPicConfig, struCfg.size(), pInt);
		struCfg.read();
		if(!bRet)
		{
			System.out.println("NET_DVR_GetDVRConfig(NET_DVR_GET_ALARMHOST_OTHER_STATUS_V50) failed:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			return;
		}
		else
		{
			System.out.println("NET_DVR_GetDVRConfig(NET_DVR_GET_ALARMHOST_OTHER_STATUS_V50) succ");
		}
	}
	
	public void Test_AlarmHostSubsystemCfg(int iUserID)
	{
		HCNetSDKByJNA.NET_DVR_ALARMSUBSYSTEMPARAM struCfg = new HCNetSDKByJNA.NET_DVR_ALARMSUBSYSTEMPARAM();
		Pointer lpPicConfig = struCfg.getPointer();
		IntByReference pInt = new IntByReference(0);
		boolean bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_GetDVRConfig(iUserID, HCNetSDKByJNA.NET_DVR_GET_ALARMHOSTSUBSYSTEM_CFG, 0, lpPicConfig, struCfg.size(), pInt);
		struCfg.read();
		if(!bRet)
		{
			System.out.println("NET_DVR_GetDVRConfig(NET_DVR_GET_ALARMHOSTSUBSYSTEM_CFG) failed:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			return;
		}
		else
		{
			System.out.println("NET_DVR_GetDVRConfig(NET_DVR_GET_ALARMHOSTSUBSYSTEM_CFG) succ");
		}
		
		struCfg.wEnterDelay += 1;
		struCfg.write();
		bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_SetDVRConfig(iUserID, HCNetSDKByJNA.NET_DVR_SET_ALARMHOSTSUBSYSTEM_CFG, 1, lpPicConfig, struCfg.size());
		if(!bRet)
		{
			System.out.println("NET_DVR_GetDVRConfig(NET_DVR_SET_ALARMHOSTSUBSYSTEM_CFG) failed:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			return;
		}
		else
		{
			System.out.println("NET_DVR_GetDVRConfig(NET_DVR_SET_ALARMHOSTSUBSYSTEM_CFG) succ");
		}
	}
	
	public void POST_STD_XML(int iUserID, String str, String strInBuffer)
	{
		HCNetSDKByJNA.NET_DVR_XML_CONFIG_INPUT	struInput = new HCNetSDKByJNA.NET_DVR_XML_CONFIG_INPUT();		
		struInput.dwSize = struInput.size();		
							
		HCNetSDKByJNA.BYTE_ARRAY ptrUrl = new HCNetSDKByJNA.BYTE_ARRAY(HCNetSDKByJNA.BYTE_ARRAY_LEN);
		System.arraycopy(str.getBytes(), 0, ptrUrl.byValue, 0, str.length());
		ptrUrl.write();		
		struInput.lpRequestUrl = ptrUrl.getPointer();		
        struInput.dwRequestUrlLen = str.length();				
        		
		HCNetSDKByJNA.BYTE_ARRAY ptrByte = new HCNetSDKByJNA.BYTE_ARRAY(10*HCNetSDKByJNA.BYTE_ARRAY_LEN);
		ptrByte.byValue = strInBuffer.getBytes();
		ptrByte.write();
		struInput.lpInBuffer = ptrByte.getPointer();		
        struInput.dwInBufferSize = strInBuffer.length();      		
		struInput.write();
			
        HCNetSDKByJNA.NET_DVR_XML_CONFIG_OUTPUT struOutput = new HCNetSDKByJNA.NET_DVR_XML_CONFIG_OUTPUT();
        struOutput.dwSize = struOutput.size();
        
        HCNetSDKByJNA.BYTE_ARRAY ptrOutByte = new HCNetSDKByJNA.BYTE_ARRAY(HCNetSDKByJNA.ISAPI_DATA_LEN);
        struOutput.lpOutBuffer = ptrOutByte.getPointer();	
		struOutput.dwOutBufferSize = HCNetSDKByJNA.ISAPI_DATA_LEN;;
		
		HCNetSDKByJNA.BYTE_ARRAY ptrStatusByte = new HCNetSDKByJNA.BYTE_ARRAY(HCNetSDKByJNA.ISAPI_STATUS_LEN);
        struOutput.lpStatusBuffer = ptrStatusByte.getPointer();
		struOutput.dwStatusSize = HCNetSDKByJNA.ISAPI_STATUS_LEN;
		struOutput.write();	
		
		if(!HCNetSDKJNAInstance.getInstance().NET_DVR_STDXMLConfig(iUserID, struInput, struOutput))
		{
			System.out.println("lpRequestUrl:" + str);
			System.out.println("NET_DVR_STDXMLConfig POST failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
		}
		else
		{
			System.out.println("lpRequestUrl:" + str);
			System.out.println("NET_DVR_STDXMLConfig POST Succ!!!!!!!!!!!!!!!");
		}
	}
	
	public void CreateFaceLib(int iUserID)
	{
		String str = new String("POST /ISAPI/Intelligent/FDLib");
		String strInBuffer = new String("<CreateFDLibList><CreateFDLib><id>2</id><name>FaceLib</name><thresholdValue>1</thresholdValue><customInfo /></CreateFDLib></CreateFDLibList>");
				
		POST_STD_XML(iUserID, str, strInBuffer);							
	}
	
	public void DELETE_STD_XML(int iUserID, String str)
	{
		HCNetSDKByJNA.NET_DVR_XML_CONFIG_INPUT	struInput = new HCNetSDKByJNA.NET_DVR_XML_CONFIG_INPUT();		
		struInput.dwSize = struInput.size();		
								
		HCNetSDKByJNA.BYTE_ARRAY ptrDeleteFaceLibUrl = new HCNetSDKByJNA.BYTE_ARRAY(HCNetSDKByJNA.BYTE_ARRAY_LEN);
		System.arraycopy(str.getBytes(), 0, ptrDeleteFaceLibUrl.byValue, 0, str.length());
		ptrDeleteFaceLibUrl.write();
		struInput.lpRequestUrl = ptrDeleteFaceLibUrl.getPointer();
        struInput.dwRequestUrlLen = str.length();	
        struInput.lpInBuffer = null;
        struInput.dwInBufferSize = 0;			
		struInput.write();
		
		HCNetSDKByJNA.NET_DVR_XML_CONFIG_OUTPUT struOutput = new HCNetSDKByJNA.NET_DVR_XML_CONFIG_OUTPUT();
		struOutput.dwSize = struOutput.size();
        struOutput.lpOutBuffer = null;	
		struOutput.dwOutBufferSize = 0;

		HCNetSDKByJNA.BYTE_ARRAY ptrStatusByte = new HCNetSDKByJNA.BYTE_ARRAY(HCNetSDKByJNA.ISAPI_STATUS_LEN);
        struOutput.lpStatusBuffer = ptrStatusByte.getPointer();
		struOutput.dwStatusSize = HCNetSDKByJNA.ISAPI_STATUS_LEN; 
        struOutput.write();
		
		if(!HCNetSDKJNAInstance.getInstance().NET_DVR_STDXMLConfig(iUserID, struInput, struOutput))
		{
			System.out.println("lpRequestUrl:" + str);
			System.out.println("NET_DVR_STDXMLConfig DELETE failed with:" + " "+ HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
		}
		else
		{
			System.out.println("lpRequestUrl:" + str);
			System.out.println("NET_DVR_STDXMLConfig DELETE Succ!!!!!!!!!!!!!!!");
		}
	}
	public void DeleteFaceLib(int iUserID)
	{
		
		String str = new String("DELETE /ISAPI/Intelligent/FDLib/1");
		
		DELETE_STD_XML(iUserID, str);	
	}
	
	public void GetVcsTerminals(int iUserID)
	{
		HCNetSDKByJNA.NET_DVR_XML_CONFIG_INPUT	inputCfg = new HCNetSDKByJNA.NET_DVR_XML_CONFIG_INPUT();
		inputCfg.dwSize = inputCfg.size();
		String str = "GET /ISAPI/VCS/terminals\r\n";
		HCNetSDKByJNA.BYTE_ARRAY byteArr = new  HCNetSDKByJNA.BYTE_ARRAY(str.length());
		System.arraycopy(str.getBytes(), 0, byteArr.byValue, 0, str.length());
		byteArr.write();
		inputCfg.lpRequestUrl = byteArr.getPointer();
		inputCfg.dwRequestUrlLen = str.length();
		inputCfg.write();
		
		HCNetSDKByJNA.NET_DVR_XML_CONFIG_OUTPUT outputCfg = new HCNetSDKByJNA.NET_DVR_XML_CONFIG_OUTPUT();
		HCNetSDKByJNA.BYTE_ARRAY outBuf = new HCNetSDKByJNA.BYTE_ARRAY(100*1024); 
		outBuf.write();
		outputCfg.dwSize = outputCfg.size();
		outputCfg.lpOutBuffer = outBuf.getPointer();
		outputCfg.dwOutBufferSize = 100*1024;
		HCNetSDKByJNA.BYTE_ARRAY statusBuf = new HCNetSDKByJNA.BYTE_ARRAY(1024);
		outputCfg.lpStatusBuffer = statusBuf.getPointer();
		outputCfg.dwStatusSize = 1024;
		statusBuf.write();
		outputCfg.write();
		
		if(!HCNetSDKJNAInstance.getInstance().NET_DVR_STDXMLConfig(iUserID, inputCfg, outputCfg))
		{
			System.out.println("NET_DVR_STDXMLConfig failed with:" + iUserID + " "+ HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
		}
		else
		{
			System.out.println("NET_DVR_STDXMLConfig succ");
			outputCfg.read();
			System.out.println(outputCfg.lpOutBuffer.getString(0));
		}
	}
	
	public void Test_STDXMLConfig(int iUserID)
	{
		
		GetVcsTerminals(iUserID);
		
		CreateFaceLib(iUserID);  		
		
		DeleteFaceLib(iUserID);
		
		FCSearchDescription(iUserID);	
		
		FDSearchDescription(iUserID);
		
		GetFaceAppendData(iUserID);
		
		SetFaceAppendData(iUserID);
		
		DeleteFaceAppendData(iUserID);
	}
	
	public void SetFaceAppendData(int iUserID)
	{
		HCNetSDKByJNA.NET_DVR_XML_CONFIG_INPUT	struInput = new HCNetSDKByJNA.NET_DVR_XML_CONFIG_INPUT();
		struInput.dwSize = struInput.size();
		
		String str = "PUT /ISAPI/Intelligent/FDLib/1/picture/1";
		HCNetSDKByJNA.BYTE_ARRAY ptrSetFaceAppendDataUrl = new HCNetSDKByJNA.BYTE_ARRAY(HCNetSDKByJNA.BYTE_ARRAY_LEN);
		System.arraycopy(str.getBytes(), 0, ptrSetFaceAppendDataUrl.byValue, 0, str.length());
		ptrSetFaceAppendDataUrl.write();
		struInput.lpRequestUrl = ptrSetFaceAppendDataUrl.getPointer();
		struInput.dwRequestUrlLen = str.length();

		
		String strInBuffer = "<FaceAppendData><name>Name</name><bornTime>2000-01-01</bornTime><sex>male</sex><province /><certificateType /><certificateNumber /></FaceAppendData>";

		HCNetSDKByJNA.BYTE_ARRAY ptrByte = new HCNetSDKByJNA.BYTE_ARRAY(10*1024);
		ptrByte.byValue = strInBuffer.getBytes();
		ptrByte.write();
		struInput.lpInBuffer = ptrByte.getPointer();
		struInput.dwInBufferSize = strInBuffer.length();					
		struInput.write();
		
		HCNetSDKByJNA.NET_DVR_XML_CONFIG_OUTPUT struOutput = new HCNetSDKByJNA.NET_DVR_XML_CONFIG_OUTPUT();
		struOutput.dwSize = struOutput.size();
		
		HCNetSDKByJNA.BYTE_ARRAY ptrOutByte = new HCNetSDKByJNA.BYTE_ARRAY(HCNetSDKByJNA.ISAPI_DATA_LEN);
        struOutput.lpOutBuffer = ptrOutByte.getPointer();	
		struOutput.dwOutBufferSize = HCNetSDKByJNA.ISAPI_DATA_LEN;
		
		HCNetSDKByJNA.BYTE_ARRAY ptrStatusByte = new HCNetSDKByJNA.BYTE_ARRAY(HCNetSDKByJNA.ISAPI_STATUS_LEN);
        struOutput.lpStatusBuffer = ptrStatusByte.getPointer();
		struOutput.dwStatusSize = HCNetSDKByJNA.ISAPI_STATUS_LEN;
		struOutput.write();
		
		if(!HCNetSDKJNAInstance.getInstance().NET_DVR_STDXMLConfig(iUserID, struInput, struOutput))
		{
			System.out.println("PUT /ISAPI/Intelligent/FDLib/1/picture/1 failed with:" + iUserID + " "+ HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			return;
		}
		else
		{
			Log.i("","PUT /ISAPI/Intelligent/FDLib/1/picture/1 success");
			Log.i("","dwReturnXMLSize="+struOutput.dwReturnedXMLSize);
		}
	}
	
	public void DeleteFaceAppendData(int iUserID)
	{	
		
		String str = "DELETE /ISAPI/Intelligent/FDLib/1/picture/1";
		DELETE_STD_XML(iUserID, str);	
	}
	
	public void GetFaceAppendData(int iUserID)
	{
		HCNetSDKByJNA.NET_DVR_XML_CONFIG_INPUT	struInput = new HCNetSDKByJNA.NET_DVR_XML_CONFIG_INPUT();
		struInput.dwSize = struInput.size();
		
		String str = "GET /ISAPI/Intelligent/FDLib/1/picture/1";
		HCNetSDKByJNA.BYTE_ARRAY ptrGetFaceAppendDataUrl = new HCNetSDKByJNA.BYTE_ARRAY(HCNetSDKByJNA.BYTE_ARRAY_LEN);
		System.arraycopy(str.getBytes(), 0, ptrGetFaceAppendDataUrl.byValue, 0, str.length());
		ptrGetFaceAppendDataUrl.write();
		struInput.lpRequestUrl = ptrGetFaceAppendDataUrl.getPointer();
		struInput.dwRequestUrlLen = str.length();
		
		
		struInput.lpInBuffer = null;
		struInput.dwInBufferSize = 0;					
		struInput.write();
		
		HCNetSDKByJNA.NET_DVR_XML_CONFIG_OUTPUT struOutput = new HCNetSDKByJNA.NET_DVR_XML_CONFIG_OUTPUT();
		struOutput.dwSize = struOutput.size();
		
		HCNetSDKByJNA.BYTE_ARRAY ptrOutByte = new HCNetSDKByJNA.BYTE_ARRAY(HCNetSDKByJNA.ISAPI_DATA_LEN);
        struOutput.lpOutBuffer = ptrOutByte.getPointer();	
		struOutput.dwOutBufferSize = HCNetSDKByJNA.ISAPI_DATA_LEN;
		
		HCNetSDKByJNA.BYTE_ARRAY ptrStatusByte = new HCNetSDKByJNA.BYTE_ARRAY(HCNetSDKByJNA.ISAPI_STATUS_LEN);
        struOutput.lpStatusBuffer = ptrStatusByte.getPointer();
		struOutput.dwStatusSize = HCNetSDKByJNA.ISAPI_STATUS_LEN;
		struOutput.write();
		
		if(!HCNetSDKJNAInstance.getInstance().NET_DVR_STDXMLConfig(iUserID, struInput, struOutput))
		{
			System.out.println("GET /ISAPI/Intelligent/FDLib/1/picture failed with:" + iUserID + " "+ HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			return;
		}
		else
		{
			System.out.println("GET /ISAPI/Intelligent/FDLib/1/picture success");
			Log.i("","dwReturnXMLSize="+struOutput.dwReturnedXMLSize);
		}
	}
	
	public void FDSearchDescription(int iUserID)
	{

		String str =  new String("POST /ISAPI/Intelligent/FDLib/FDSearch\r\n");
		String strInBuffer = new String("<FDSearchDescription><FDID>1</FDID><startTime>1970-01-01</startTime><endTime>2017-04-19</endTime><searchID>C77EDED5-1C40-0001-DE57-8B4C2E402C00</searchID><maxResults>50</maxResults><searchResultPosition>1</searchResultPosition></FDSearchDescription>");
		
		POST_STD_XML(iUserID, str, strInBuffer);		
	}
	
	public void FCSearchDescription(int iUserID)
	{
	
		String str = "POST /ISAPI/Intelligent/FDLib/FCSearch";
		String strInBuffer = "<FCSearchDescription><searchID>C77ED6B2-CDB0-0001-106B-BA6057CFAA90</searchID><snapStartTime>2017-04-17T00:00:00Z</snapStartTime><snapEndTime>2017-04-19T23:59:59Z</snapEndTime><maxResults>500</maxResults><searchResultPosition>1</searchResultPosition></FCSearchDescription>";

		POST_STD_XML(iUserID, str, strInBuffer);
	}
	
	
	public int Test_CreateOpenEzvizUser()
	{
		HCNetSDKByJNA.NET_DVR_OPEN_EZVIZ_USER_LOGIN_INFO loginInfo = new HCNetSDKByJNA.NET_DVR_OPEN_EZVIZ_USER_LOGIN_INFO();
		
		System.arraycopy("pbopen.ys7.com".getBytes(), 0, loginInfo.sEzvizServerAddress, 0, "pbopen.ys7.com".length());
		System.arraycopy("at.7z3qhjhi1k0kpk88am6wm2p00x1xc397-7p0c49v5ms-1sn768m-gocfbh7v1".getBytes(), 0, loginInfo.sAccessToken, 0, "at.7z3qhjhi1k0kpk88am6wm2p00x1xc397-7p0c49v5ms-1sn768m-gocfbh7v1".length());
		System.arraycopy("626969511".getBytes(), 0, loginInfo.sDeviceID, 0, "626969511".length());
		
		System.arraycopy("/api/device/transmission".getBytes(), 0, loginInfo.sUrl, 0, "/api/device/transmission".length());
		System.arraycopy("2".getBytes(), 0, loginInfo.sClientType, 0, "2".length());
		System.arraycopy("226f102a99ad0e078504d380b9ddf760".getBytes(), 0, loginInfo.sFeatureCode, 0, "226f102a99ad0e078504d380b9ddf760".length());
		System.arraycopy("5.0.1".getBytes(), 0, loginInfo.sOsVersion, 0, "5.0.1".length());
		System.arraycopy("UNKNOWN".getBytes(), 0, loginInfo.sNetType, 0, "UNKNOWN".length());
		System.arraycopy("v.5.1.5.30".getBytes(), 0, loginInfo.sSdkVersion, 0, "v.5.1.5.30".length());
		System.arraycopy("com.hik.visualintercom".getBytes(), 0, loginInfo.sAppID, 0, "com.hik.visualintercom".length());
		loginInfo.wPort = (short)443;
		
		HCNetSDKByJNA.NET_DVR_DEVICEINFO_V40	deviceInfo = new HCNetSDKByJNA.NET_DVR_DEVICEINFO_V40();
		loginInfo.write();
		
		int lUserID = HCNetSDKJNAInstance.getInstance().NET_DVR_CreateOpenEzvizUser(loginInfo.getPointer(), deviceInfo.getPointer());
		if(lUserID < 0)
		{
			System.out.println("NET_DVR_CreateOpenEzvizUser failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
		}
		else
		{
			deviceInfo.read();
			System.out.println("NET_DVR_CreateOpenEzvizUser succ with:" + lUserID);
		}
		
		return lUserID;
		
		/*
		if(!HCNetSDKJNAInstance.getInstance().NET_DVR_DeleteOpenEzvizUser(lUserID))
		{
			System.out.println("NET_DVR_DeleteOpenEzvizUser failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
		}
		else
		{
			System.out.println("NET_DVR_DeleteOpenEzvizUser succ");
		}
		*/
	}
	
	public void Test_EzvizAccessCfg(int iUserID)
    {
         HCNetSDKByJNA.NET_DVR_EZVIZ_ACCESS_CFG struCfg = new HCNetSDKByJNA.NET_DVR_EZVIZ_ACCESS_CFG();
         Pointer lpPicConfig = struCfg.getPointer();
         IntByReference pInt = new IntByReference(0);
         boolean bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_GetDVRConfig(iUserID, HCNetSDKByJNA.NET_DVR_GET_EZVIZ_ACCESS_CFG , 0, lpPicConfig, struCfg.size(), pInt);
         struCfg.read();
         if(!bRet)
         {
             System.out.println("NET_DVR_GetDVRConfig(NET_DVR_GET_ALARMHOSTSUBSYSTEM_CFG) failed:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
             return;
         }
         else
         {
             System.out.println("NET_DVR_GetDVRConfig(NET_DVR_GET_ALARMHOSTSUBSYSTEM_CFG) succ");
         }
         
//       struCfg.write();
         bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_SetDVRConfig(iUserID, HCNetSDKByJNA.NET_DVR_SET_EZVIZ_ACCESS_CFG, 1, lpPicConfig, struCfg.size());
         if(!bRet)
         {
             System.out.println("NET_DVR_GetDVRConfig(NET_DVR_SET_ALARMHOSTSUBSYSTEM_CFG) failed:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
             return;
         }
         else
         {
             System.out.println("NET_DVR_GetDVRConfig(NET_DVR_SET_ALARMHOSTSUBSYSTEM_CFG) succ");
         }
    }
	
	
	public void TestDisplayNo(int iLoginId)
	{
		HCNetSDKByJNA.NET_DVR_DISPLAYCFG struCfg = new HCNetSDKByJNA.NET_DVR_DISPLAYCFG();
		struCfg.dwSize = struCfg.size();
		IntByReference pInt = new IntByReference(0);
		if(HCNetSDKJNAInstance.getInstance().NET_DVR_GetDVRConfig(iLoginId, HCNetSDKByJNA.NET_DVR_GET_VIDEOWALLDISPLAYNO,0, struCfg.getPointer(), struCfg.size(), pInt))
		{
			System.out.println("NET_DVR_GET_VIDEOWALLDISPLAYNO success");
		}
		else {
			System.out.println("NET_DVR_GET_VIDEOWALLDISPLAYNO fail,error="+HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
		}
		
	}
	
	public void SendUploadData()
	{	            
        FileInputStream picfile = null;
        FileInputStream xmlfile = null;
        int picdataLength = 0;
        int xmldataLength = 0;            
        
         try{
        	 picfile = new FileInputStream(new File("/mnt/sdcard/1.jpg"));
        	 xmlfile = new FileInputStream(new File("/mnt/sdcard/1.xml"));
         }
         catch(FileNotFoundException e)
         {
        	 e.printStackTrace();
         }
         
        
        try{
        	picdataLength = picfile.available();
        	xmldataLength = xmlfile.available();
        }
        catch(IOException e1)
        {
        	e1.printStackTrace();
        }
        
        
        if(picdataLength < 0 || xmldataLength < 0)
        {
        	System.out.println("input file/xml dataSize < 0");
        	return;
        }
        
        HCNetSDKByJNA.BYTE_ARRAY ptrpicByte = new HCNetSDKByJNA.BYTE_ARRAY(picdataLength); 
        HCNetSDKByJNA.BYTE_ARRAY ptrxmlByte = new HCNetSDKByJNA.BYTE_ARRAY(xmldataLength); 

        

        try {
        	picfile.read(ptrpicByte.byValue);
        	xmlfile.read(ptrxmlByte.byValue);
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        
        ptrpicByte.write();
        ptrxmlByte.write();
        

        HCNetSDKByJNA.NET_DVR_SEND_PARAM_IN struSendParam = new HCNetSDKByJNA.NET_DVR_SEND_PARAM_IN();
                   		
		struSendParam.pSendData = ptrpicByte.getPointer();
		struSendParam.dwSendDataLen = picdataLength;
		struSendParam.pSendAppendData = ptrxmlByte.getPointer();
		struSendParam.dwSendAppendDataLen = xmldataLength;
		if(struSendParam.pSendData == null || struSendParam.pSendAppendData == null || struSendParam.dwSendDataLen == 0 || struSendParam.dwSendAppendDataLen == 0)
		{
			System.out.println("input file/xml data err");
        	return;
		}

		struSendParam.byPicType = 1;
		struSendParam.dwPicManageNo = 0;
		struSendParam.write();
		
		int iRet = HCNetSDKJNAInstance.getInstance().NET_DVR_UploadSend(m_UploadHandle, struSendParam.getPointer(), null);
		
		System.out.println("iRet="+iRet);
		if(iRet < 0)
		{
			System.out.println("NET_DVR_UploadSend fail,error="+HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
		}
		else
		{
			System.out.println("NET_DVR_UploadSend success");
			System.out.println("dwSendDataLen ="+struSendParam.dwSendDataLen);
			System.out.println("dwSendAppendDataLen ="+struSendParam.dwSendAppendDataLen);
		}		
		
		try {
			picfile.close();
			xmlfile.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }		
	}
		
	
	public void Test_UploadFaceLinData(int iUserID)
	{
		if(m_UploadHandle != -1)
		{
			if(HCNetSDKJNAInstance.getInstance().NET_DVR_UploadClose(m_UploadHandle))
			{
				System.out.println("NET_DVR_UploadClose success");
			}
			else
			{
				System.out.println("NET_DVR_UploadClose fail,error="+HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}
		}
		HCNetSDKByJNA.NET_DVR_FACELIB_COND struCfg = new HCNetSDKByJNA.NET_DVR_FACELIB_COND();
		struCfg.dwSize = struCfg.size();	
		struCfg.szFDID = "1".getBytes();
		struCfg.byConcurrent = 0;		
		struCfg.write();		
			
		m_UploadHandle = HCNetSDKJNAInstance.getInstance().NET_DVR_UploadFile_V40(iUserID, HCNetSDKByJNA.NET_SDK_UPLOAD_TYPE.IMPORT_DATA_TO_FACELIB, struCfg.getPointer(), struCfg.size(), null, null, 0);
		if (m_UploadHandle < 0)
		{
			System.out.println("NET_DVR_UploadFile_V40 fail,error="+HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
		}
		else
		{
			System.out.println("NET_DVR_UploadFile_V40 success");
		}
		
						
		Thread thread = new Thread()
		{
			public void run()
	   		{
				SendUploadData();	
				
				while(true)
				{
					 if (-1 == m_UploadHandle)
				     {
				         return;
				     }
					 
					m_UploadStatus = getUploadState();
					if(m_UploadStatus == 1)
					{
						HCNetSDKByJNA.NET_DVR_UPLOAD_FILE_RET  struPicRet = new HCNetSDKByJNA.NET_DVR_UPLOAD_FILE_RET();
						Pointer lpPic= struPicRet.getPointer();
						
						boolean bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_GetUploadResult(m_UploadHandle, lpPic, struPicRet.size());		
						if(!bRet)
						{
							System.out.println("NET_DVR_GetUploadResult failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
						}						
						else
						{
							System.out.println("NET_DVR_GetUploadResult succ");
							struPicRet.read();	
							System.out.println("PicID" + struPicRet.sUrl[0]);
						}						
						
						
						if(HCNetSDKJNAInstance.getInstance().NET_DVR_UploadClose(m_UploadHandle))
						{
							 m_UploadHandle = -1;
						}
				       
					}

					else if(m_UploadStatus >= 3 || m_UploadStatus == -1)
					{
						System.out.println("m_UploadStatus = " + m_UploadStatus);
						HCNetSDKJNAInstance.getInstance().NET_DVR_UploadClose(m_UploadHandle);
				        m_UploadHandle = -1;
				        break;
					}
				}				
	   		}
	   	};
	   	thread.start();						   		   									
	}
	
	public int getUploadState()
	{	
		IntByReference pInt = new IntByReference(0);
		m_UploadStatus = HCNetSDKJNAInstance.getInstance().NET_DVR_GetUploadState(m_UploadHandle, pInt);
		if(m_UploadStatus == -1)
		{
			System.out.println("NET_DVR_GetUploadState fail,error="+HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
		}
		else if(m_UploadStatus == 2)
		{
			System.out.println("is uploading!!!!  progress = " + pInt.getValue());
		}
		else if(m_UploadStatus == 1)
		{
			System.out.println("progress = " + pInt.getValue());
			System.out.println("Uploading Succ!!!!!");
		}
		else
		{
			System.out.println("NET_DVR_GetUploadState fail  m_UploadStatus="+m_UploadStatus);
			System.out.println("NET_DVR_GetUploadState fail,error="+HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
		}
		return m_UploadStatus;
	}
	
	
	public static class FDisplayNoCallBack implements HCNetSDKByJNA.fRemoteConfigCallback
	{

		@Override
		public void invoke(int dwType, Pointer lpBuffer, int dwBufLen,
                           Pointer pUserData) {
			// TODO Auto-generated method stub
			if(dwType == HCNetSDKByJNA.NET_SDK_CALLBACK_TYPE_DATA)
			{
				HCNetSDKByJNA.NET_DVR_DISPLAYPARAM struDisplayNo = new HCNetSDKByJNA.NET_DVR_DISPLAYPARAM(lpBuffer); 
				struDisplayNo.read();
				System.out.println("DisplayNo=" + struDisplayNo.dwDisplayNo+",Type="+struDisplayNo.byDispChanType);
			}
		}
		
	}
	
	/*public void TEST_Alarm_Param_List(int iUserID)
	{
		HCNetSDKByJNA.NET_DVR_STD_CONFIG struAlarmInCfgListSTD = new HCNetSDKByJNA.NET_DVR_STD_CONFIG();

		HCNetSDKByJNA.NET_DVR_MULTI_ALARMIN_COND struCond = new HCNetSDKByJNA.NET_DVR_MULTI_ALARMIN_COND();

		HCNetSDKByJNA.NET_DVR_ALARMIN_PARAM_LIST struList = new HCNetSDKByJNA.NET_DVR_ALARMIN_PARAM_LIST();
		struCond.dwSize = struCond.size();

		for (int k = 0; k < 64; k++)

		{

			struCond.iZoneNo[k] = -1;

		}

		for(int m = 0; m < 64; m++)

		{

			struCond.iZoneNo[m] = 1111;

		}
		struCond.write();
		struList.write();
		
		struAlarmInCfgListSTD.lpCondBuffer = struCond.getPointer();
		struAlarmInCfgListSTD.dwCondSize = struCond.size();
		struAlarmInCfgListSTD.lpOutBuffer = struList.getPointer();
		struAlarmInCfgListSTD.dwOutSize = struList.size();
		struAlarmInCfgListSTD.write();
		
		if (!HCNetSDKJNAInstance.getInstance().NET_DVR_GetSTDConfig(iUserID, HCNetSDKByJNA.NET_DVR_GET_ALARMIN_PARAM_LIST, struAlarmInCfgListSTD.getPointer()))
		{

			System.out.println("NET_DVR_GetSTDConfig" + " err: " + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
		}
		else
		{
			struAlarmInCfgListSTD.read();
			System.out.println("NET_DVR_GetSTDConfig success");
		}
	}*/
	
	private static final HCNetSDKByJNA.fRemoteConfigCallback fDisplayCB = new	FDisplayNoCallBack();
	public void TestDisplayNoLongCfg(int iLogId)
	{
		if(m_lDisplay == -1)
		{
		    HCNetSDKByJNA.INT_ARRAY pInt = new HCNetSDKByJNA.INT_ARRAY(1);
		    pInt.iValue[0]= 0xffffffff;
		    pInt.write();
		    INT_PTR poiInt_PTR = new INT_PTR();
			m_lDisplay = HCNetSDKJNAInstance.getInstance().NET_DVR_StartRemoteConfig(iLogId, HCNetSDKByJNA.NET_SDK_GET_VIDEOWALLDISPLAYNO, pInt.getPointer(), 4, fDisplayCB, null);
			if(m_lDisplay ==-1)
			{
				System.out.println("NET_DVR_StartRemoteConfig failed,error code= "+HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}
		}
		else
		{
			HCNetSDKJNAInstance.getInstance().NET_DVR_StopRemoteConfig(m_lDisplay);
			m_lDisplay = -1;
		}
	}
	
	
	/*public void Test_EzvizNTPCfg(int iUserID)
	{
		HCNetSDKByJNA.NET_DVR_NTPPARA struNTPPara = new HCNetSDKByJNA.NET_DVR_NTPPARA();
		IntByReference pInt = new IntByReference(0);
		struNTPPara.write();
		
		if(!HCNetSDKJNAInstance.getInstance().NET_DVR_GetDVRConfig(iUserID, HCNetSDKByJNA.NET_DVR_GET_NTPCFG, 0, struNTPPara.getPointer(), struNTPPara.size(), pInt))
		{
			System.out.println("NET_DVR_GET_NTPCFG failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
		}
		else
		{
			System.out.println("NET_DVR_GET_NTPCFG succ");
		}
		
		struNTPPara.wInterval = (short)(struNTPPara.wInterval + 1);
		struNTPPara.write();
		if (!HCNetSDKJNAInstance.getInstance().NET_DVR_SetDVRConfig(iUserID, HCNetSDKByJNA.NET_DVR_SET_NTPCFG, 0, struNTPPara.getPointer(), struNTPPara.size()))
		{
			System.out.println("NET_DVR_SET_NTPCFG failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError()); 
		}
		else
		{
			System.out.println("NET_DVR_SET_NTPCFG succ");
		}
	}*/
	
	public void Test_EzvizCompleteRestore(int iUserID)
	{
		HCNetSDKByJNA.NET_DVR_COMPLETE_RESTORE_INFO struRestore = new HCNetSDKByJNA.NET_DVR_COMPLETE_RESTORE_INFO();
		struRestore.dwSize = struRestore.size();
		struRestore.dwChannel = 1;
		struRestore.write();
		
		if(!HCNetSDKJNAInstance.getInstance().NET_DVR_RemoteControl(iUserID, HCNetSDKByJNA.NET_DVR_COMPLETE_RESTORE_CTRL, struRestore.getPointer(), struRestore.size()))
		{
			System.out.println("NET_DVR_COMPLETE_RESTORE_CTRL failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
		}
		else
		{
			System.out.println("NET_DVR_COMPLETE_RESTORE_CTRL succ");
		}
	}
	
	public void Test_EzvizControlGateway(int iUserID)
	{
		if(!HCNetSDKJNAInstance.getInstance().NET_DVR_ControlGateway(iUserID, -1, 1))
		{
			System.out.println("NET_DVR_ControlGateway failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
		}
		else
		{
			System.out.println("NET_DVR_ControlGateway succ");
		}
	}
	
	public void Test_EzvizReStoreCfg(int iUserID)
	{
		if(!HCNetSDKJNAInstance.getInstance().NET_DVR_RestoreConfig(iUserID))
		{
			System.out.println("NET_DVR_RestoreConfig failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
		}
		else
		{
			System.out.println("NET_DVR_RestoreConfig succ");
		}
	}
	
	public void Test_EzvizClearAcsParamCfg(int iUserID)
	{
		HCNetSDKByJNA.NET_DVR_ACS_PARAM_TYPE struClearAcsParam = new HCNetSDKByJNA.NET_DVR_ACS_PARAM_TYPE();
		struClearAcsParam.dwSize = struClearAcsParam.size();
		struClearAcsParam.dwParamType = 0xffffffff;
		struClearAcsParam.write();
		
		if(!HCNetSDKJNAInstance.getInstance().NET_DVR_RemoteControl(iUserID, HCNetSDKByJNA.NET_DVR_CLEAR_ACS_PARAM, struClearAcsParam.getPointer(), struClearAcsParam.size()))
		{
			System.out.println("NET_DVR_CLEAR_ACS_PARAM failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
		}
		else
		{
			System.out.println("NET_DVR_CLEAR_ACS_PARAM succ");
		}
	}
	
	public void Test_EzvizAcsWorkStatusCfg(int iUserID)
	{
		HCNetSDKByJNA.NET_DVR_ACS_WORK_STATUS_V50 struAcsWorkStatusCfg = new HCNetSDKByJNA.NET_DVR_ACS_WORK_STATUS_V50();
		struAcsWorkStatusCfg.dwSize = struAcsWorkStatusCfg.size();
		IntByReference pInt = new IntByReference(0);
		struAcsWorkStatusCfg.write();
		
		if(!HCNetSDKJNAInstance.getInstance().NET_DVR_GetDVRConfig(iUserID, HCNetSDKByJNA.NET_DVR_GET_ACS_WORK_STATUS_V50, 0, struAcsWorkStatusCfg.getPointer(), struAcsWorkStatusCfg.size(), pInt))
		{
			System.out.println("NET_DVR_GET_ACS_WORK_STATUS_V50 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
		}
		else
		{
			System.out.println("NET_DVR_GET_ACS_WORK_STATUS_V50 succ"+ pInt.getValue());
		}
	}
	
	public void Test_EzvizCardReaderCfg(int iUserID)
	{
		HCNetSDKByJNA.NET_DVR_CARD_READER_CFG_V50 struCardReaderCfg = new HCNetSDKByJNA.NET_DVR_CARD_READER_CFG_V50();
		struCardReaderCfg.dwSize = struCardReaderCfg.size();
		IntByReference pInt = new IntByReference(0);
		struCardReaderCfg.write();
		
		if(!HCNetSDKJNAInstance.getInstance().NET_DVR_GetDVRConfig(iUserID, HCNetSDKByJNA.NET_DVR_GET_CARD_READER_CFG_V50, 1, struCardReaderCfg.getPointer(), struCardReaderCfg.size(), pInt))
		{
			System.out.println("NET_DVR_GET_CARD_READER_CFG_V50 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
		}
		else
		{
			System.out.println("NET_DVR_GET_CARD_READER_CFG_V50 succ "+ pInt.getValue());
		}
		
		//struCardReaderCfg.write();
		if (!HCNetSDKJNAInstance.getInstance().NET_DVR_SetDVRConfig(iUserID, HCNetSDKByJNA.NET_DVR_SET_CARD_READER_CFG_V50, 1, struCardReaderCfg.getPointer(), struCardReaderCfg.size()))
		{
			System.out.println("NET_DVR_SET_CARD_READER_CFG_V50 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
		}
		else
		{
			System.out.println("NET_DVR_SET_CARD_READER_CFG_V50 succ");
		}
	}
	
	public void Test_EzvizDoorCfg(int iUserID)
	{
		HCNetSDKByJNA.NET_DVR_DOOR_CFG struDoorCfg = new HCNetSDKByJNA.NET_DVR_DOOR_CFG();
		struDoorCfg.dwSize = struDoorCfg.size();
		IntByReference pInt = new IntByReference(0);
		struDoorCfg.write();
		
		if(!HCNetSDKJNAInstance.getInstance().NET_DVR_GetDVRConfig(iUserID, HCNetSDKByJNA.NET_DVR_GET_DOOR_CFG, 1, struDoorCfg.getPointer(), struDoorCfg.size(), pInt))
		{
			System.out.println("NET_DVR_GET_DOOR_CFG failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
		}
		else
		{
			System.out.println("NET_DVR_GET_DOOR_CFG succ"+ pInt.getValue());
		}
		struDoorCfg.read();
		struDoorCfg.byOpenDuration = 100;
		struDoorCfg.write();
		if (!HCNetSDKJNAInstance.getInstance().NET_DVR_SetDVRConfig(iUserID, HCNetSDKByJNA.NET_DVR_SET_DOOR_CFG, 1, struDoorCfg.getPointer(), struDoorCfg.size()))
		{
			System.out.println("NET_DVR_SET_DOOR_CFG failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
		}
		else
		{
			System.out.println("NET_DVR_SET_DOOR_CFG succ");
		}
	}
	
	public void Test_EzvizPlanTemplateCfg(int iUserID)
	{
		HCNetSDKByJNA.NET_DVR_PLAN_TEMPLATE struPlanTemplate = new HCNetSDKByJNA.NET_DVR_PLAN_TEMPLATE();
		struPlanTemplate.dwSize = struPlanTemplate.size();
		IntByReference pInt = new IntByReference(-1);
		struPlanTemplate.write();
		
		int dwGetCommand = HCNetSDKByJNA.NET_DVR_GET_DOOR_STATUS_PLAN_TEMPLATE;  //NET_DVR_GET_VERIFY_PLAN_TEMPLATE, NET_DVR_GET_CARD_RIGHT_PLAN_TEMPLATE
		int dwSetCommand = HCNetSDKByJNA.NET_DVR_SET_DOOR_STATUS_PLAN_TEMPLATE;  //NET_DVR_SET_VERIFY_PLAN_TEMPLATE, NET_DVR_SET_CARD_RIGHT_PLAN_TEMPLATE
		switch(dwGetCommand)
		{
		case HCNetSDKByJNA.NET_DVR_GET_CARD_RIGHT_PLAN_TEMPLATE:
			dwSetCommand = HCNetSDKByJNA.NET_DVR_SET_CARD_RIGHT_PLAN_TEMPLATE;
		case HCNetSDKByJNA.NET_DVR_GET_VERIFY_PLAN_TEMPLATE:
			dwSetCommand = HCNetSDKByJNA.NET_DVR_SET_VERIFY_PLAN_TEMPLATE;
		case HCNetSDKByJNA.NET_DVR_GET_DOOR_STATUS_PLAN_TEMPLATE:
			if(!HCNetSDKJNAInstance.getInstance().NET_DVR_GetDVRConfig(iUserID, dwGetCommand, 1, struPlanTemplate.getPointer(), struPlanTemplate.size(), pInt))
			{
				System.out.println("PLAN_TEMPLATE_CFG1 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}
			else
			{
				System.out.println("PLAN_TEMPLATE_CFG1 succ"+ pInt.getValue());
			}
			
			struPlanTemplate.byEnable = 1;
			System.arraycopy("New Year Template!".getBytes(), 0, struPlanTemplate.byTemplateName, 1, "New Year Template!".length());
			struPlanTemplate.dwWeekPlanNo = 1;
			struPlanTemplate.dwHolidayGroupNo[0] = 1;
			struPlanTemplate.write();
			// ��������������ƻ���
			if (!HCNetSDKJNAInstance.getInstance().NET_DVR_SetDVRConfig(iUserID, dwSetCommand, 1, struPlanTemplate.getPointer(), struPlanTemplate.size()))
			{
				System.out.println("PLAN_TEMPLATE_CFG2 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}
			else
			{
				System.out.println("PLAN_TEMPLATE_CFG2 succ");
			}
			
			if(!HCNetSDKJNAInstance.getInstance().NET_DVR_GetDVRConfig(iUserID, dwGetCommand, 1, struPlanTemplate.getPointer(), struPlanTemplate.size(), pInt))
			{
				System.out.println("PLAN_TEMPLATE_CFG3 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}
			else
			{
				System.out.println("PLAN_TEMPLATE_CFG3 succ"+ pInt.getValue());
			}
			break;
		case HCNetSDKByJNA.NET_DVR_GET_CARD_RIGHT_PLAN_TEMPLATE_V50:
			dwSetCommand = HCNetSDKByJNA.NET_DVR_SET_CARD_RIGHT_PLAN_TEMPLATE_V50;
			HCNetSDKByJNA.NET_DVR_PLAN_TEMPLATE_COND struPlanTemplateCond = new HCNetSDKByJNA.NET_DVR_PLAN_TEMPLATE_COND();
			struPlanTemplateCond.dwSize = struPlanTemplateCond.size();
			struPlanTemplateCond.dwPlanTemplateNumber = 1;
			struPlanTemplateCond.wLocalControllerID = (short)1;
			struPlanTemplateCond.write();
			
			//get
			if(!HCNetSDKJNAInstance.getInstance().NET_DVR_GetDeviceConfig(iUserID, dwGetCommand, 1, struPlanTemplateCond.getPointer(), struPlanTemplateCond.size(), pInt.getPointer(), struPlanTemplate.getPointer(), struPlanTemplate.size()))
			{
				System.out.println("PLAN_TEMPLATE_CFG1 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}
			else
			{
				System.out.println("PLAN_TEMPLATE_CFG1 succ status:" + pInt.getValue());
			}
			
			//set
			struPlanTemplate.byEnable = 1;
			System.arraycopy("New Year Template!".getBytes(), 0, struPlanTemplate.byTemplateName, 0, "New Year Template!".length());
			struPlanTemplate.dwWeekPlanNo = 1;
			struPlanTemplate.dwHolidayGroupNo[0] = 1;
			struPlanTemplate.write();
			if(!HCNetSDKJNAInstance.getInstance().NET_DVR_SetDeviceConfig(iUserID, dwGetCommand, 1, struPlanTemplateCond.getPointer(), struPlanTemplateCond.size(), pInt.getPointer(), struPlanTemplate.getPointer(), struPlanTemplate.size()))
			{
				System.out.println("PLAN_TEMPLATE_CFG2 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}
			else
			{
				System.out.println("PLAN_TEMPLATE_CFG2 succ");
			}
			
			//get
			if(!HCNetSDKJNAInstance.getInstance().NET_DVR_GetDeviceConfig(iUserID, dwGetCommand, 1, struPlanTemplateCond.getPointer(), struPlanTemplateCond.size(), pInt.getPointer(), struPlanTemplate.getPointer(), struPlanTemplate.size()))
			{
				System.out.println("PLAN_TEMPLATE_CFG3 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}
			else
			{
				System.out.println("PLAN_TEMPLATE_CFG3 succ status:" + pInt.getValue());
			}
			break;
		default:
			break;
		}
	}
	
	public void Test_EzvizHolidayGroupCfg(int iUserID)
	{
		HCNetSDKByJNA.NET_DVR_HOLIDAY_GROUP_CFG struHolidayGroup = new HCNetSDKByJNA.NET_DVR_HOLIDAY_GROUP_CFG();
		struHolidayGroup.dwSize = struHolidayGroup.size();
		IntByReference pInt = new IntByReference(-1);
		struHolidayGroup.write();
		
		int dwGetCommand = HCNetSDKByJNA.NET_DVR_GET_DOOR_STATUS_HOLIDAY_GROUP;  //NET_DVR_GET_VERIFY_HOLIDAY_GROUP, NET_DVR_GET_CARD_RIGHT_HOLIDAY_GROUP
		int dwSetCommand = HCNetSDKByJNA.NET_DVR_SET_DOOR_STATUS_HOLIDAY_GROUP;  //NET_DVR_SET_VERIFY_HOLIDAY_GROUP, NET_DVR_SET_CARD_RIGHT_HOLIDAY_GROUP
		
		switch(dwGetCommand)
		{
		case HCNetSDKByJNA.NET_DVR_GET_CARD_RIGHT_HOLIDAY_GROUP:
			dwSetCommand = HCNetSDKByJNA.NET_DVR_SET_CARD_RIGHT_HOLIDAY_GROUP;
		case HCNetSDKByJNA.NET_DVR_GET_VERIFY_HOLIDAY_GROUP:
			dwSetCommand = HCNetSDKByJNA.NET_DVR_SET_VERIFY_HOLIDAY_GROUP;
		case HCNetSDKByJNA.NET_DVR_GET_DOOR_STATUS_HOLIDAY_GROUP:
			if(!HCNetSDKJNAInstance.getInstance().NET_DVR_GetDVRConfig(iUserID, dwGetCommand, 1, struHolidayGroup.getPointer(), struHolidayGroup.size(), pInt))
			{
				System.out.println("HOLIDAY_GROUP_CFG1 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}
			else
			{
				System.out.println("HOLIDAY_GROUP_CFG1 succ"+ pInt.getValue());
			}
			
			struHolidayGroup.byEnable = 1;
			System.arraycopy("New Year!".getBytes(), 0, struHolidayGroup.byGroupName, 0, "New Year!".length());
			struHolidayGroup.dwHolidayPlanNo[0] = 1;
			struHolidayGroup.write();
			if (!HCNetSDKJNAInstance.getInstance().NET_DVR_SetDVRConfig(iUserID, dwSetCommand, 1, struHolidayGroup.getPointer(), struHolidayGroup.size()))
			{
				System.out.println("HOLIDAY_GROUP_CFG2 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}
			else
			{
				System.out.println("HOLIDAY_GROUP_CFG2 succ");
			}
			
			if(!HCNetSDKJNAInstance.getInstance().NET_DVR_GetDVRConfig(iUserID, dwGetCommand, 1, struHolidayGroup.getPointer(), struHolidayGroup.size(), pInt))
			{
				System.out.println("HOLIDAY_GROUP_CFG3 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}
			else
			{
				System.out.println("HOLIDAY_GROUP_CFG3 succ"+ pInt.getValue());
			}
			break;
		case HCNetSDKByJNA.NET_DVR_GET_CARD_RIGHT_HOLIDAY_GROUP_V50:
			dwSetCommand = HCNetSDKByJNA.NET_DVR_SET_CARD_RIGHT_HOLIDAY_GROUP_V50;
			HCNetSDKByJNA.NET_DVR_HOLIDAY_GROUP_COND struHolidayGroupCond = new HCNetSDKByJNA.NET_DVR_HOLIDAY_GROUP_COND();
			struHolidayGroupCond.dwSize = struHolidayGroupCond.size();
			struHolidayGroupCond.dwHolidayGroupNumber = 1;
			struHolidayGroupCond.wLocalControllerID = (short)1;
			struHolidayGroupCond.write();
			
			//get
			if(!HCNetSDKJNAInstance.getInstance().NET_DVR_GetDeviceConfig(iUserID, dwGetCommand, 1, struHolidayGroupCond.getPointer(), struHolidayGroupCond.size(), pInt.getPointer(), struHolidayGroup.getPointer(), struHolidayGroup.size()))
			{
				System.out.println("HOLIDAY_PLAN_CFG1 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}
			else
			{
				System.out.println("HOLIDAY_PLAN_CFG1 succ status:" + pInt.getValue());
			}
			
			//set
			struHolidayGroup.byEnable = 1;
			System.arraycopy("New Year!".getBytes(), 0, struHolidayGroup.byGroupName, 0, "New Year!".length());
			struHolidayGroup.dwHolidayPlanNo[0] = 1;
			struHolidayGroup.write();
			if(!HCNetSDKJNAInstance.getInstance().NET_DVR_SetDeviceConfig(iUserID, dwGetCommand, 1, struHolidayGroupCond.getPointer(), struHolidayGroupCond.size(), pInt.getPointer(), struHolidayGroup.getPointer(), struHolidayGroup.size()))
			{
				System.out.println("HOLIDAY_PLAN_CFG2 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}
			else
			{
				System.out.println("HOLIDAY_PLAN_CFG2 succ");
			}
			
			//get
			if(!HCNetSDKJNAInstance.getInstance().NET_DVR_GetDeviceConfig(iUserID, dwGetCommand, 1, struHolidayGroupCond.getPointer(), struHolidayGroupCond.size(), pInt.getPointer(), struHolidayGroup.getPointer(), struHolidayGroup.size()))
			{
				System.out.println("HOLIDAY_PLAN_CFG3 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}
			else
			{
				System.out.println("HOLIDAY_PLAN_CFG3 succ status:" + pInt.getValue());
			}
			break;
		default:
			break;
		}
	}
	
	public void Test_EzvizHolidayPlanCfg(int iUserID)
	{
		HCNetSDKByJNA.NET_DVR_HOLIDAY_PLAN_CFG struHolidayPlan = new HCNetSDKByJNA.NET_DVR_HOLIDAY_PLAN_CFG();
		struHolidayPlan.dwSize = struHolidayPlan.size();
		IntByReference pInt = new IntByReference(-1);
		struHolidayPlan.write();
		
		int dwGetCommand = HCNetSDKByJNA.NET_DVR_GET_DOOR_STATUS_HOLIDAY_PLAN;  //NET_DVR_GET_VERIFY_HOLIDAY_PLAN,NET_DVR_GET_CARD_RIGHT_HOLIDAY_PLAN
		int dwSetCommand = HCNetSDKByJNA.NET_DVR_SET_DOOR_STATUS_HOLIDAY_PLAN;  //NET_DVR_SET_VERIFY_HOLIDAY_PLAN,NET_DVR_SET_CARD_RIGHT_HOLIDAY_PLAN
		
		switch(dwGetCommand)
		{
		case HCNetSDKByJNA.NET_DVR_GET_CARD_RIGHT_HOLIDAY_PLAN:
			dwSetCommand = HCNetSDKByJNA.NET_DVR_SET_CARD_RIGHT_HOLIDAY_PLAN;
		case HCNetSDKByJNA.NET_DVR_GET_VERIFY_HOLIDAY_PLAN:
			dwSetCommand = HCNetSDKByJNA.NET_DVR_SET_VERIFY_HOLIDAY_PLAN;
		case HCNetSDKByJNA.NET_DVR_GET_DOOR_STATUS_HOLIDAY_PLAN:
			if(!HCNetSDKJNAInstance.getInstance().NET_DVR_GetDVRConfig(iUserID, dwGetCommand, 1, struHolidayPlan.getPointer(), struHolidayPlan.size(), pInt))
			{
				System.out.println("HOLIDAY_PLAN_CFG1 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}
			else
			{
				System.out.println("HOLIDAY_PLAN_CFG1 succ status:" + pInt.getValue());
			}
			
			struHolidayPlan.byEnable = 1;
			struHolidayPlan.struBeginDate.wYear = 0x7e0; //2016-11-20
			struHolidayPlan.struBeginDate.byMonth = 0x0b;
			struHolidayPlan.struBeginDate.byDay = 0x14;
			
			struHolidayPlan.struEndDate.wYear = 0x7e1; //2017-02-01
			struHolidayPlan.struEndDate.byMonth = 0x02;
			struHolidayPlan.struEndDate.byDay = 0x01;
			
			struHolidayPlan.struPlanCfg[0].byEnable = 1;
			
			if(dwGetCommand == HCNetSDKByJNA.NET_DVR_GET_DOOR_STATUS_HOLIDAY_PLAN)
			{
				struHolidayPlan.struPlanCfg[0].byDoorStatus = 2;
				struHolidayPlan.struPlanCfg[0].byVerifyMode = 0;
			}
			else
			{	
				struHolidayPlan.struPlanCfg[0].byDoorStatus = 0;
				struHolidayPlan.struPlanCfg[0].byVerifyMode = 2; //2-ˢ��+����(��������֤��ʽ�ƻ�ʹ��),8-ָ��+ˢ��
			}
			
			struHolidayPlan.struPlanCfg[0].struTimeSegment.struBeginTime.byHour = 0x08; //8
			struHolidayPlan.struPlanCfg[0].struTimeSegment.struEndTime.byHour = 0x0e; //14
			struHolidayPlan.write();
			if (!HCNetSDKJNAInstance.getInstance().NET_DVR_SetDVRConfig(iUserID, dwSetCommand, 1, struHolidayPlan.getPointer(), struHolidayPlan.size()))
			{
				System.out.println("HOLIDAY_PLAN_CFG2 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}
			else
			{
				System.out.println("HOLIDAY_PLAN_CFG2 succ");
			}
			
			if(!HCNetSDKJNAInstance.getInstance().NET_DVR_GetDVRConfig(iUserID, dwGetCommand, 1, struHolidayPlan.getPointer(), struHolidayPlan.size(), pInt))
			{
				System.out.println("HOLIDAY_PLAN_CFG3 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}
			else
			{
				System.out.println("HOLIDAY_PLAN_CFG3 succ status:" + pInt.getValue());
			}
			break;
		case HCNetSDKByJNA.NET_DVR_GET_CARD_RIGHT_HOLIDAY_PLAN_V50:
			dwSetCommand = HCNetSDKByJNA.NET_DVR_SET_CARD_RIGHT_HOLIDAY_PLAN_V50;
			HCNetSDKByJNA.NET_DVR_HOLIDAY_PLAN_COND struHolidayPlanCond = new HCNetSDKByJNA.NET_DVR_HOLIDAY_PLAN_COND();
			struHolidayPlanCond.dwSize = struHolidayPlanCond.size();
			struHolidayPlanCond.dwHolidayPlanNumber = 1;
			struHolidayPlanCond.wLocalControllerID = (short)1;
			struHolidayPlanCond.write();
			
			//get
			if(!HCNetSDKJNAInstance.getInstance().NET_DVR_GetDeviceConfig(iUserID, dwGetCommand, 1, struHolidayPlanCond.getPointer(), struHolidayPlanCond.size(), pInt.getPointer(), struHolidayPlan.getPointer(), struHolidayPlan.size()))
			{
				System.out.println("HOLIDAY_PLAN_CFG1 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}
			else
			{
				System.out.println("HOLIDAY_PLAN_CFG1 succ status:" + pInt.getValue());
			}
			
			//set
			struHolidayPlan.struPlanCfg[0].byDoorStatus = 0;
			struHolidayPlan.struPlanCfg[0].byVerifyMode = 8;
			struHolidayPlan.struBeginDate.wYear = 0x7e0; //2016-11-20
			struHolidayPlan.struBeginDate.byMonth = 0x0b;
			struHolidayPlan.struBeginDate.byDay = 0x14;
			struHolidayPlan.struEndDate.wYear = 0x7e1; //2017-02-01
			struHolidayPlan.struEndDate.byMonth = 0x02;
			struHolidayPlan.struEndDate.byDay = 0x01;
			struHolidayPlan.struPlanCfg[0].byEnable = 1;
			struHolidayPlan.byEnable = 1;
			struHolidayPlan.write();
			if(!HCNetSDKJNAInstance.getInstance().NET_DVR_SetDeviceConfig(iUserID, dwGetCommand, 1, struHolidayPlanCond.getPointer(), struHolidayPlanCond.size(), pInt.getPointer(), struHolidayPlan.getPointer(), struHolidayPlan.size()))
			{
				System.out.println("HOLIDAY_PLAN_CFG2 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}
			else
			{
				System.out.println("HOLIDAY_PLAN_CFG2 succ");
			}
			
			//get
			if(!HCNetSDKJNAInstance.getInstance().NET_DVR_GetDeviceConfig(iUserID, dwGetCommand, 1, struHolidayPlanCond.getPointer(), struHolidayPlanCond.size(), pInt.getPointer(), struHolidayPlan.getPointer(), struHolidayPlan.size()))
			{
				System.out.println("HOLIDAY_PLAN_CFG3 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}
			else
			{
				System.out.println("HOLIDAY_PLAN_CFG3 succ status:" + pInt.getValue());
			}
			break;
		default:
			break;
		}
	}
	
	public void Test_EzvizWeekPlanCfg(int iUserID)
	{
		HCNetSDKByJNA.NET_DVR_WEEK_PLAN_CFG struWeekPlan = new HCNetSDKByJNA.NET_DVR_WEEK_PLAN_CFG();
		struWeekPlan.dwSize = struWeekPlan.size();
		IntByReference pInt = new IntByReference(0);
		struWeekPlan.write();
		
		int dwGetCommand = HCNetSDKByJNA.NET_DVR_GET_WEEK_PLAN_CFG;  //NET_DVR_GET_VERIFY_WEEK_PLAN,NET_DVR_GET_CARD_RIGHT_WEEK_PLAN
		int dwSetCommand = HCNetSDKByJNA.NET_DVR_SET_WEEK_PLAN_CFG;  //NET_DVR_SET_VERIFY_WEEK_PLAN,NET_DVR_SET_CARD_RIGHT_WEEK_PLAN
		
		switch(dwGetCommand)
		{
		case HCNetSDKByJNA.NET_DVR_GET_CARD_RIGHT_WEEK_PLAN:
			dwSetCommand = HCNetSDKByJNA.NET_DVR_SET_CARD_RIGHT_WEEK_PLAN;
		case HCNetSDKByJNA.NET_DVR_GET_VERIFY_WEEK_PLAN:
			dwSetCommand = HCNetSDKByJNA.NET_DVR_SET_VERIFY_WEEK_PLAN;
		case HCNetSDKByJNA.NET_DVR_GET_WEEK_PLAN_CFG:
			if(!HCNetSDKJNAInstance.getInstance().NET_DVR_GetDVRConfig(iUserID, dwGetCommand, 1, struWeekPlan.getPointer(), struWeekPlan.size(), pInt))
			{
				System.out.println("WEEK_PLAN_CFG1 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}
			else
			{
				System.out.println("WEEK_PLAN_CFG1 succ status:" + pInt.getValue());
			}
			
			struWeekPlan.byEnable = 1;
			struWeekPlan.struPlanCfg[0].struDaysPlanCfg[1].byEnable = 1;
			if(dwGetCommand == HCNetSDKByJNA.NET_DVR_GET_WEEK_PLAN_CFG)
			{
				struWeekPlan.struPlanCfg[0].struDaysPlanCfg[1].byDoorStatus = 2;
				struWeekPlan.struPlanCfg[0].struDaysPlanCfg[1].byVerifyMode = 0;
			}
			else
			{
				struWeekPlan.struPlanCfg[0].struDaysPlanCfg[1].byDoorStatus = 0;
				struWeekPlan.struPlanCfg[0].struDaysPlanCfg[1].byVerifyMode = 2;
			}
			struWeekPlan.struPlanCfg[0].struDaysPlanCfg[1].struTimeSegment.struBeginTime.byHour = 0x08; //8
			struWeekPlan.struPlanCfg[0].struDaysPlanCfg[1].struTimeSegment.struEndTime.byHour = 0x0e;   //14
			struWeekPlan.write();
			if (!HCNetSDKJNAInstance.getInstance().NET_DVR_SetDVRConfig(iUserID, dwSetCommand, 1, struWeekPlan.getPointer(), struWeekPlan.size()))
			{
				System.out.println("WEEK_PLAN_CFG2 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}
			else
			{
				System.out.println("WEEK_PLAN_CFG2 succ");
			}
			
			if(!HCNetSDKJNAInstance.getInstance().NET_DVR_GetDVRConfig(iUserID, dwGetCommand, 1, struWeekPlan.getPointer(), struWeekPlan.size(), pInt))
			{
				System.out.println("WEEK_PLAN_CFG3 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}
			else
			{
				System.out.println("WEEK_PLAN_CFG3 succ status:" + pInt.getValue());
			}
			break;
		case HCNetSDKByJNA.NET_DVR_GET_CARD_RIGHT_WEEK_PLAN_V50:
			dwSetCommand = HCNetSDKByJNA.NET_DVR_SET_CARD_RIGHT_HOLIDAY_PLAN_V50;
			HCNetSDKByJNA.NET_DVR_WEEK_PLAN_COND struWeekPlanCond = new HCNetSDKByJNA.NET_DVR_WEEK_PLAN_COND();
			struWeekPlanCond.dwSize = struWeekPlanCond.size();
			struWeekPlanCond.dwWeekPlanNumber = 1;
			struWeekPlanCond.wLocalControllerID = (short)1;
			struWeekPlanCond.write();
			//get
			if(!HCNetSDKJNAInstance.getInstance().NET_DVR_GetDeviceConfig(iUserID, dwGetCommand, 1, struWeekPlanCond.getPointer(), struWeekPlanCond.size(), pInt.getPointer(), struWeekPlan.getPointer(), struWeekPlan.size()))
			{
				System.out.println("HOLIDAY_PLAN_CFG1 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}
			else
			{
				System.out.println("HOLIDAY_PLAN_CFG1 succ status:" + pInt.getValue());
			}
			
			//set
			struWeekPlan.byEnable = 1;
			struWeekPlan.struPlanCfg[0].struDaysPlanCfg[1].byEnable = 1;
			struWeekPlan.struPlanCfg[0].struDaysPlanCfg[1].byDoorStatus = 0;
			struWeekPlan.struPlanCfg[0].struDaysPlanCfg[1].byVerifyMode = 8; //8-ָ��+ˢ��
			struWeekPlan.struPlanCfg[0].struDaysPlanCfg[1].struTimeSegment.struBeginTime.byHour = 0x08; //8
			struWeekPlan.struPlanCfg[0].struDaysPlanCfg[1].struTimeSegment.struEndTime.byHour = 0x0e;   //14
			struWeekPlan.write();
			
			if(!HCNetSDKJNAInstance.getInstance().NET_DVR_SetDeviceConfig(iUserID, dwGetCommand, 1, struWeekPlanCond.getPointer(), struWeekPlanCond.size(), pInt.getPointer(), struWeekPlan.getPointer(), struWeekPlan.size()))
			{
				System.out.println("HOLIDAY_PLAN_CFG2 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}
			else
			{
				System.out.println("HOLIDAY_PLAN_CFG2 succ");
			}
			
			//get
			if(!HCNetSDKJNAInstance.getInstance().NET_DVR_GetDeviceConfig(iUserID, dwGetCommand, 1, struWeekPlanCond.getPointer(), struWeekPlanCond.size(), pInt.getPointer(), struWeekPlan.getPointer(), struWeekPlan.size()))
			{
				System.out.println("HOLIDAY_PLAN_CFG3 failed with:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			}
			else
			{
				System.out.println("HOLIDAY_PLAN_CFG3 succ status:" + pInt.getValue());
			}
			break;
		default:
			break; 
		}
	}
	
	
	public void TEST_EzvizXMLConfig(int iUserID)
	{
		HCNetSDKByJNA.NET_DVR_XML_CONFIG_INPUT	inputCfg = new HCNetSDKByJNA.NET_DVR_XML_CONFIG_INPUT();
		inputCfg.dwSize = inputCfg.size();
		String str = "POST /ISAPI/AccessControl/MobileSetCard\r\n";
		HCNetSDKByJNA.BYTE_ARRAY byteArr = new  HCNetSDKByJNA.BYTE_ARRAY(str.length());
		System.arraycopy(str.getBytes(), 0, byteArr.byValue, 0, str.length());
		byteArr.write();
		inputCfg.lpRequestUrl = byteArr.getPointer();
		inputCfg.dwRequestUrlLen = str.length();
		inputCfg.write();
		
		HCNetSDKByJNA.NET_DVR_XML_CONFIG_OUTPUT outputCfg = new HCNetSDKByJNA.NET_DVR_XML_CONFIG_OUTPUT();
		HCNetSDKByJNA.BYTE_ARRAY outBuf = new HCNetSDKByJNA.BYTE_ARRAY(100*1024); 
		outBuf.write();
		outputCfg.dwSize = outputCfg.size();
		outputCfg.lpOutBuffer = outBuf.getPointer();
		outputCfg.dwOutBufferSize = 100*1024;
		HCNetSDKByJNA.BYTE_ARRAY statusBuf = new HCNetSDKByJNA.BYTE_ARRAY(1024);
		outputCfg.lpStatusBuffer = statusBuf.getPointer();
		outputCfg.dwStatusSize = 1024;
		statusBuf.write();
		outputCfg.write();
		
		/*if(!HCNetSDKJNAInstance.getInstance().NET_DVR_STDXMLConfig(iUserID, inputCfg, outputCfg))
		{
			System.out.println("NET_DVR_STDXMLConfig failed with:" + iUserID + " "+ HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
		}
		else
		{
			System.out.println("NET_DVR_STDXMLConfig succ");
			outputCfg.read();
			System.out.println(outputCfg.lpOutBuffer.getString(0));
		}*/
		
        //Set
        String strInBuffer = new String("<MobileSetCardCond version=\"2.0\" xmlns=\"http://www.isapi.org/ver20/XMLSchema\">\r\n" +
        		"<checkCardNo>false</checkCardNo>\r\n" +
        		"<CardList>\r\n" +
        		"<CardData>\r\n" +
        		"<cardNo>1234567891</cardNo>\r\n" +
        		"<cardValid>true</cardValid>\r\n" +
        		"<cardType>1</cardType>\r\n" +
        		"<leaderCard>true</leaderCard>\r\n" +
        		"<doorRight>1</doorRight>\r\n" +
        		"<validEnable>true</validEnable>\r\n" +
        		"<beginTime>2016-11-07T08:30:00</beginTime>\r\n" +
        		"<endTime>2017-11-07T17:00:00</endTime>\r\n" +
        		"<belongGroup>0</belongGroup>\r\n" +
        		"<cardPassword>1234</cardPassword>\r\n" +
        		"<RightList>\r\n" +
        		"<RightData>\r\n" +
        		"<doorNo>1</doorNo>\r\n" +
        		"<cardRight>1,2,3,4</cardRight>\r\n" +
        		"</RightData>\r\n" +
        		"</RightList>\r\n" +
        		"<maxSwipeTime>10</maxSwipeTime>\r\n" +
        		"<swipeTime>0</swipeTime>\r\n" +
        		"<roomNumber>1</roomNumber>\r\n" +
        		"<floorNumber>1</floorNumber>\r\n" +
        		"<employeeNo>1</employeeNo>\r\n" +
        		"<name>1234</name>\r\n" +
        		"<departmentNo>1</departmentNo>\r\n" +
        		"<schedulePlanNo>1</schedulePlanNo>\r\n" +
        		"<schedulePlanType>2</schedulePlanType>\r\n" +
        		"</CardData>\r\n" +
        		"</CardList>\r\n" +
        		"</MobileSetCardCond>");
        HCNetSDKByJNA.BYTE_ARRAY byteArr2 = new  HCNetSDKByJNA.BYTE_ARRAY(strInBuffer.length());
		System.arraycopy(strInBuffer.getBytes(), 0, byteArr2.byValue, 0, strInBuffer.length());
		byteArr2.write();
		inputCfg.lpInBuffer = byteArr2.getPointer();
		inputCfg.dwInBufferSize = strInBuffer.length();
		
		inputCfg.write();
        if (!HCNetSDKJNAInstance.getInstance().NET_DVR_STDXMLConfig(iUserID, inputCfg, outputCfg))
		{
			System.out.println("NET_DVR_STDXMLConfig" + " err: " + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
		}
		else
		{
			System.out.println("NET_DVR_STDXMLConfig success");
			outputCfg.read();
			System.out.println(outputCfg.lpOutBuffer.getString(0));
		}    
	}

	public void PicCfg(int iUserID)
	{
		HCNetSDKByJNA.NET_DVR_PICCFG_V40 struCfg = new HCNetSDKByJNA.NET_DVR_PICCFG_V40();
		struCfg.dwSize = struCfg.size();
		Pointer lpPicConfig = struCfg.getPointer();
		IntByReference pInt = new IntByReference(0);
		boolean bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_GetDVRConfig(iUserID, HCNetSDKByJNA.NET_DVR_GET_PICCFG_V40, 1, lpPicConfig, struCfg.size(), pInt);
		struCfg.read();
		if(!bRet)
		{
			System.out.println("NET_DVR_GetDVRConfig(NET_DVR_GET_PICCFG_V40) failed:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			return;
		}
		else
		{
			System.out.println("NET_DVR_GetDVRConfig(NET_DVR_GET_PICCFG_V40) succ");
		}

		struCfg.write();
		bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_SetDVRConfig(iUserID, HCNetSDKByJNA.NET_DVR_SET_PICCFG_V40, 1, lpPicConfig, struCfg.size());
		if(!bRet)
		{
			System.out.println("NET_DVR_GetDVRConfig(NET_DVR_SET_PICCFG_V40) failed:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
			return;
		}
		else
		{
			System.out.println("NET_DVR_GetDVRConfig(NET_DVR_SET_PICCFG_V40) succ");
		}
	}
	
	public static void TEST_Config(int iPreviewID, int iUserID, int iChan)
	{
//		jnaTest.Test_Login_V40();
//		jnaTest.Test_Other(iUserID);
//		jnaTest.Test_AlarmHostSubsystemCfg(iUserID);
//		jnaTest.Test_GetSDKVersion();
//		jnaTest.Test_TimeCfg(iUserID);
//		jnaTest.Test_LoiteringDetection(iUserID, iChan);
//		jnaTest.Test_MultiStreamCompression(iUserID);
//		jnaTest.Test_Alarm(iUserID);
//		jnaTest.Test_Alarm_V41(iUserID);
		jnaTest.Test_SetSDKLocalCfg();
//		jnaTest.Test_GetInputVolume(iUserID);
//		jnaTest.Test_GetCardCfg(iUserID);
//		jnaTest.Test_LED_Area(iUserID);
//		jnaTest.TEST_Passive_Decode(iUserID);
//		jnaTest.Test_LED_Area(iUserID);
//		jnaTest.TEST_Passive_Decode(iUserID);
//		jnaTest.Test_STDXMLConfig(iUserID);
		//jnaTest.Test_EzvizAccessCfg(iUserID);
		//jnaTest.TestDisplayNo(iUserID);
//		jnaTest.TestDisplayNoLongCfg(iUserID);
//		jnaTest.Test_UploadFaceLinData(iUserID);
        //jnaTest.TEST_GetCurTriggerMode(iUserID);
        //jnaTest.TEST_RS485AccessInfo(iUserID, iChan);
        //jnaTest.Test_TriggerCfg(iUserID);
        //jnaTest.Test_ShowString(iUserID, iChan);
//        jnaTest.Test_FindPicture(iUserID, iChan);
        
	}
	
	public static void TEST_EzvizConfig(int iPreviewID, int iUserID, int iChan)
	{
		//jnaTest.Test_TimeCfg(iUserID);
		//jnaTest.TEST_EzvizXMLConfig(iUserID);
		//jnaTest.Test_EzvizControlGateway(iUserID);
		//jnaTest.Test_EzvizWeekPlanCfg(iUserID);
		//jnaTest.Test_EzvizHolidayPlanCfg(iUserID);
		//jnaTest.Test_EzvizHolidayGroupCfg(iUserID);
		//jnaTest.Test_EzvizPlanTemplateCfg(iUserID);
		jnaTest.Test_EzvizDoorCfg(iUserID);
		//jnaTest.Test_EzvizCardReaderCfg(iUserID);
		//jnaTest.Test_EzvizAcsWorkStatusCfg(iUserID);
		//jnaTest.Test_EzvizControlGateway(iUserID);
		//jnaTest.Test_EzvizClearAcsParamCfg(iUserID);
		//jnaTest.Test_EzvizReStoreCfg(iUserID);
		
		//jnaTest.Test_EzvizCompleteRestore(iUserID);
		//jnaTest.TEST_Alarm_Param_List(iUserID);
        //jnaTest.TestDisplayNoLongCfg(iUserID);
	}
	
	public static int TEST_EzvizLogin()
	{
		int iloginID = -1;
		iloginID = jnaTest.Test_CreateOpenEzvizUser();
		return iloginID;
	}
	
	public void Test_SetSDKLocalCfg()
	{
		int dwType = HCNetSDKByJNA.NET_SDK_LOCAL_CFG_TYPE.NET_SDK_LOCAL_CFG_TYPE_CHECK_DEV;
		HCNetSDKByJNA.NET_DVR_LOCAL_CHECK_DEV struCheckDev = new HCNetSDKByJNA.NET_DVR_LOCAL_CHECK_DEV();
		Pointer lpCheckDev = struCheckDev.getPointer();
		struCheckDev.dwCheckOnlineTimeout = 30000;
		struCheckDev.dwCheckOnlineNetFailMax = 3;
		struCheckDev.write();
		boolean bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_SetSDKLocalCfg(dwType, lpCheckDev);
		if(!bRet)
        {
            System.out.println("NET_DVR_SetSDKLocalCfg(NET_SDK_LOCAL_CFG_TYPE_CHECK_DEV) failed:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
            return;
        }
        else
        {
            System.out.println("NET_DVR_SetSDKLocalCfg(NET_SDK_LOCAL_CFG_TYPE_CHECK_DEV) succ!");
        }
	}
	
    public void TEST_GetCurTriggerMode(int iUserID)
    {
        HCNetSDKByJNA.NET_DVR_CURTRIGGERMODE struCurTriggerMode = new HCNetSDKByJNA.NET_DVR_CURTRIGGERMODE();
        Pointer lpCurTriggerMode = struCurTriggerMode.getPointer();
        IntByReference pInt = new IntByReference(0);
        boolean bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_GetDVRConfig(iUserID, HCNetSDKByJNA.NET_DVR_GET_CURTRIGGERMODE, 0, lpCurTriggerMode, struCurTriggerMode.size(), pInt);
        struCurTriggerMode.read();
        if(!bRet)
        {
            System.out.println("NET_DVR_GetDVRConfig(NET_DVR_GET_CURTRIGGERMODE) failed:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
            return;
        }
        else
        {
            System.out.println("NET_DVR_GetDVRConfig(NET_DVR_GET_CURTRIGGERMODE) succ! Current mode is " + struCurTriggerMode.dwTriggerType);
        }
    }   
    
    public void TEST_RS485AccessInfo(int iUserID, int iStartChan)
    {
        HCNetSDKByJNA.NET_ITC_RS485_ACCESS_INFO_COND[] accessInfoCond = (HCNetSDKByJNA.NET_ITC_RS485_ACCESS_INFO_COND[])new HCNetSDKByJNA.NET_ITC_RS485_ACCESS_INFO_COND().toArray(2);
        HCNetSDKByJNA.NET_ITC_RS485_ACCESS_CFG[] accessInfoCfg = (HCNetSDKByJNA.NET_ITC_RS485_ACCESS_CFG[])new HCNetSDKByJNA.NET_ITC_RS485_ACCESS_CFG().toArray(2);
        Pointer lpCond = accessInfoCond[0].getPointer();
        Pointer lpParam = accessInfoCfg[0].getPointer();
        
        HCNetSDKByJNA.INT_ARRAY pInt = new HCNetSDKByJNA.INT_ARRAY(2);          
        
        pInt.iValue[0] = -1;
        pInt.iValue[1] = -1;
        System.out.println("NET_DVR_GetDeviceConfig status:" + pInt.iValue[0] + pInt.iValue[1]);
        
        accessInfoCond[0].dwSize = accessInfoCond[0].size();
        accessInfoCond[0].dwTriggerModeType = 8;
        accessInfoCond[0].dwChannel = iStartChan;
        accessInfoCond[0].byAssociateRS485No = 1;
        accessInfoCond[0].write();
        
        accessInfoCond[1].dwSize = accessInfoCond[1].size();
        accessInfoCond[1].dwTriggerModeType = 8;
        accessInfoCond[1].dwChannel = iStartChan;
        accessInfoCond[1].byAssociateRS485No = 2;
        accessInfoCond[1].write();
        
        boolean bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_GetDeviceConfig(iUserID, HCNetSDKByJNA.NET_ITC_GET_RS485_ACCESSINFO, 2, 
                lpCond, accessInfoCond[0].size() * 2, pInt.getPointer(), lpParam, accessInfoCfg[0].size() * 2);
        accessInfoCfg[0].read();
        accessInfoCfg[1].read();
        pInt.read();
        if(!bRet)
        {
            System.out.println("NET_ITC_GET_RS485_ACCESSINFO failed:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
        }           
        else
        {
            System.out.println("NET_DVR_GetDeviceConfig NET_ITC_GET_RS485_ACCESSINFO status:" + pInt.iValue[0] + pInt.iValue[1] + " " + accessInfoCfg[0].byModeType + " " + accessInfoCfg[0].dwSize);
        }
        
        pInt.iValue[0] = -1;
        pInt.iValue[1] = -1;
        System.out.println("NET_DVR_SetDeviceConfig status:" + pInt.iValue[0] + pInt.iValue[1]);
        bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_SetDeviceConfig(iUserID, HCNetSDKByJNA.NET_ITC_SET_RS485_ACCESSINFO, 2, 
                lpCond, accessInfoCond[0].size() * 2, pInt.getPointer(), lpParam, accessInfoCfg[0].size() * 2);
        accessInfoCfg[0].read();
        accessInfoCfg[1].read();
        pInt.read();
        if(!bRet)
        {
            System.out.println("NET_ITC_SET_RS485_ACCESSINFO failed:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
        }           
        else
        {
            System.out.println("NET_DVR_SetDeviceConfig NET_ITC_SET_RS485_ACCESSINFO status:" + pInt.iValue[0] + pInt.iValue[1]);
        }
    }

    public void Test_TriggerCfg(int iUserID)
    {
        HCNetSDKByJNA.NET_ITC_TRIGGERCFG struTriggerCfg = new HCNetSDKByJNA.NET_ITC_TRIGGERCFG();
        Pointer lpTriggerConfig = struTriggerCfg.getPointer();
        struTriggerCfg.struTriggerParam.uTriggerParam.setType("struPostRadar");
        IntByReference pInt = new IntByReference(0);
        boolean bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_GetDVRConfig(iUserID, HCNetSDKByJNA.NET_ITC_GET_TRIGGERCFG, 0, lpTriggerConfig, struTriggerCfg.size(), pInt);
        struTriggerCfg.read();
        if(!bRet)
        {
            System.out.println("NET_DVR_GetDVRConfig(NET_ITC_GET_TRIGGERCFG) failed:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
            return;
        }
        else
        {
            struTriggerCfg.struTriggerParam.uTriggerParam.read();
            System.out.println("NET_DVR_GetDVRConfig(NET_ITC_GET_TRIGGERCFG) succ! Enable: " + struTriggerCfg.struTriggerParam.byEnable + " ;dwTriggerType is " + struTriggerCfg.struTriggerParam.dwTriggerType + "; byRadarType is" + struTriggerCfg.struTriggerParam.uTriggerParam.struPostRadar.struRadar.byRadarType);
        }
        
        struTriggerCfg.write();
        bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_SetDVRConfig(iUserID, HCNetSDKByJNA.NET_ITC_SET_TRIGGERCFG, 0, lpTriggerConfig, struTriggerCfg.size());
        if(!bRet)
        {
            System.out.println("HCNetSDKByJNA.NET_ITC_SET_TRIGGERCFG succ");
        }
    }
    
    public void Test_ShowString(int iUserID, int iStartChan)
    {
        HCNetSDKByJNA.NET_DVR_SHOWSTRING_V30 struShowString = new HCNetSDKByJNA.NET_DVR_SHOWSTRING_V30();
        Pointer lpShowStringConfig = struShowString.getPointer();
        IntByReference pInt = new IntByReference(0);
        boolean bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_GetDVRConfig(iUserID, HCNetSDKByJNA.NET_DVR_GET_SHOWSTRING_V30, iStartChan, lpShowStringConfig, struShowString.size(), pInt);
        struShowString.read();
        if(!bRet)
        {
            System.out.println("NET_DVR_GetDVRConfig(NET_DVR_GET_SHOWSTRING_V30) failed:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
            return;
        }
        else
        {
            System.out.println("NET_DVR_GetDVRConfig(NET_DVR_GET_SHOWSTRING_V30) succ! wShowString : " + struShowString.struStringInfo[0].wShowString);
        }
        
        struShowString.write();
        bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_SetDVRConfig(iUserID, HCNetSDKByJNA.NET_DVR_SET_SHOWSTRING_V30, iStartChan, lpShowStringConfig, struShowString.size());
        if(!bRet)
        {
            System.out.println("NET_DVR_GetDVRConfig(NET_DVR_SET_SHOWSTRING_V30) failed:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
        }
        else
        {
            System.out.println("HCNetSDKByJNA.NET_DVR_SET_SHOWSTRING_V30 succ");
        }
    }
    
    public void Test_FindPicture(int iUserID, int iStartChan)
    {
        HCNetSDKByJNA.NET_DVR_FIND_PICTURE_PARAM struPicParam = new HCNetSDKByJNA.NET_DVR_FIND_PICTURE_PARAM();
        struPicParam.dwSize = struPicParam.size();
        struPicParam.lChannel = iStartChan;
        struPicParam.byFileType = 1;
        struPicParam.struStartTime.dwDay = 1;
        struPicParam.struStartTime.dwHour = 1;
        struPicParam.struStartTime.dwMinute = 1;
        struPicParam.struStartTime.dwSecond = 1;
        struPicParam.struStartTime.dwYear = 2017;
        struPicParam.struStartTime.dwMonth = 1;
        
        struPicParam.struStopTime.dwDay = 8;
        struPicParam.struStopTime.dwHour = 1;
        struPicParam.struStopTime.dwMinute = 1;
        struPicParam.struStopTime.dwSecond = 1;
        struPicParam.struStopTime.dwYear = 2017;
        struPicParam.struStopTime.dwMonth = 5;
        struPicParam.write();
        Pointer lpPicParam = struPicParam.getPointer();

        
        int handle = HCNetSDKJNAInstance.getInstance().NET_DVR_FindPicture(iUserID, lpPicParam);

        if(handle < 0)
        {
            System.out.println("NET_DVR_FindPicture failed:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
            return;
        }
        else
        {
            System.out.println("NET_DVR_FindPicturesucc! return handle: " + handle);
        }
        
        HCNetSDKByJNA.NET_DVR_FIND_PICTURE_V40 struPicV40 = new HCNetSDKByJNA.NET_DVR_FIND_PICTURE_V40();
        Pointer lpstruPicV40 = struPicV40.getPointer();

        int iFindPicRet = 0;
        
        while(iFindPicRet != -1)
        {
            iFindPicRet = HCNetSDKJNAInstance.getInstance().NET_DVR_FindNextPicture_V40(handle, lpstruPicV40);
            if(iFindPicRet < 0)
            {
                System.out.println("NET_DVR_FindNextPicture_V40 failed:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
                break;
            }
            struPicV40.read();
            if (iFindPicRet == HCNetSDKByJNA.NET_DVR_FILE_SUCCESS)
            {
                System.out.println("~~~~~Find Pic" + CommonMethod.toValidString(new String(struPicV40.sFileName)));
                System.out.println("~~~~~File Size" + struPicV40.dwFileSize);
                
                IntByReference pInt = new IntByReference(0);
                HCNetSDKByJNA.BYTE_ARRAY ptrFileName = new HCNetSDKByJNA.BYTE_ARRAY(64);  
                System.arraycopy(struPicV40.sFileName, 0, ptrFileName.byValue, 0, 64);
                ptrFileName.write();
                
                if(!HCNetSDKJNAInstance.getInstance().NET_DVR_GetPicture_V30(iUserID, ptrFileName.getPointer(), null, 0, pInt))
                {
                    System.out.println("NET_DVR_GetPicture_V30 failed1:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
                }
                if(pInt.getValue() <= 0)
                {
                    System.out.println("~~~~~data error");
                    break;
                }
                HCNetSDKByJNA.BYTE_ARRAY ptrByte = new HCNetSDKByJNA.BYTE_ARRAY(pInt.getValue());  
                
                ptrFileName.write();
                IntByReference pInt1 = new IntByReference(0);
                if(!HCNetSDKJNAInstance.getInstance().NET_DVR_GetPicture_V30(iUserID, ptrFileName.getPointer(), ptrByte.getPointer(), pInt.getValue(), pInt1))
                {
                    System.out.println("NET_DVR_GetPicture_V30 failed2:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
                }
                else
                {
                    System.out.println("NET_DVR_GetPicture_V30 success");
                }
                break;
            }
            else if (HCNetSDKByJNA.NET_DVR_FILE_NOFIND == iFindPicRet)
            {
                System.out.println("No file found");
                break;
            }
            else if (HCNetSDKByJNA.NET_DVR_NOMOREFILE == iFindPicRet)
            {
                System.out.println("All files are listed");
                break;
            }
            else if (HCNetSDKByJNA.NET_DVR_FILE_EXCEPTION == iFindPicRet)
            {
                System.out.println("Exception in searching");
                break;
            }
            else if (HCNetSDKByJNA.NET_DVR_ISFINDING == iFindPicRet)
            {
                System.out.println("NET_DVR_ISFINDING");
            }
        }
        
        if(!HCNetSDKJNAInstance.getInstance().NET_DVR_CloseFindPicture(handle))
        {
            System.out.println("NET_DVR_CloseFindPicture failed:" + HCNetSDKJNAInstance.getInstance().NET_DVR_GetLastError());
        }
        else
        {
            System.out.println("NET_DVR_CloseFindPicture succ!");
        }

    }
    
}


