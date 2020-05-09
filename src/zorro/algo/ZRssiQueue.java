package zorro.algo;

import java.util.ArrayList;

import common.api.ZApi;
import common.config.ZConfig;

public class ZRssiQueue {
	String m_DeviceMAC;
	String m_TagMac;
	
	ArrayList<ZRssiSignal> m_SignalList;
	ZRssiSignal m_AvgRssi;
	
	public ZRssiQueue(String DeviceMac, String TagMac)
	{		
		m_DeviceMAC=DeviceMac;
		m_TagMac=TagMac;
		
		m_SignalList=new ArrayList<ZRssiSignal>();
		
		m_AvgRssi=new ZRssiSignal(DeviceMac, TagMac, ZConfig.RSSI_MIN_VALUE, 0);
	}
	
	//此函数多线程不安全，请不要多线程调用
	public void AddNewRssi(String DeviceMac, String TagMac, float fRssi, long nTime,Integer nMove)
	{
		if( m_DeviceMAC==DeviceMac && m_TagMac==TagMac )
		{	
			int nSize=m_SignalList.size();		
			if(nSize>0)
			{
				ZRssiSignal LastSignal=m_SignalList.get(nSize-1);			
				if( LastSignal.m_Rssi==fRssi &&  Math.abs(nTime-LastSignal.m_Time)<=ZConfig.DEVICE_LOCATE_FREQUENCY )
					return;
			}
			
			ZRssiSignal newRssiSignal=new ZRssiSignal(DeviceMac, TagMac, fRssi, nTime);	
			m_SignalList.add(newRssiSignal);
			
			nSize=m_SignalList.size();
			if( nSize>ZConfig.RSSI_QUEUE_LENGTH )
			{
				for(Integer n=0;n<(nSize-ZConfig.RSSI_QUEUE_LENGTH);n++)
				{
					m_SignalList.remove(0);
				}
			}
		}
		
		UpdateAvgRssi(nMove);		
	}	
	
	
	private void UpdateAvgRssi(Integer nMove)
	{
		float nValid=0;
		long nNow=ZApi.GetUnixMillis();
		int nSize=m_SignalList.size();
		if (nSize==0)
			return;
		
		ZRssiSignal LastSS=m_SignalList.get(nSize-1);
        long nLastTime=LastSS.m_Time;
		
		float fSum=0;
		for(int n=nSize-1;n>=0;n--)
		{
			ZRssiSignal OneSignal=m_SignalList.get(n);
			
			if( (nNow-OneSignal.m_Time)<=ZConfig.RSSI_VALID_TIME  )
			{
				fSum+=OneSignal.m_Rssi;
				nValid++;
			}
			else
				break;
		}
		
		if (nValid==0)
		{
			m_AvgRssi.Update(ZConfig.RSSI_MIN_VALUE,nLastTime);
			return;
		}
		
		float nMax=ZConfig.RSSI_VALID_TIME/ZConfig.DEVICE_LOCATE_FREQUENCY;
		if( nValid/nMax*100 <= ZConfig.RSSI_VALID_PERCENT )
		{
			m_AvgRssi.Update(ZConfig.RSSI_MIN_VALUE,nLastTime);			
			return;
		}
		
		float fVal=fSum/nValid;
		
		float DeltaR=0;
		if(nMove==1)
		{	
			for(int n=nSize-1;n>=0;n--)
			{
				ZRssiSignal OneSignal=m_SignalList.get(n);
				if( (nNow-OneSignal.m_Time)<=ZConfig.RSSI_VALID_TIME  )
				{
					int Mi=Math.abs(nSize-1-n);
					
					DeltaR+=(OneSignal.m_Rssi - fVal) * Math.pow(ZConfig.RSSI_VALID_FACTOR, Mi);
				}
				else
					break;
			}	
			
			fVal=fVal+DeltaR;
		}

		m_AvgRssi.Update(fVal, nLastTime);
		
		return;
	}

	
	public ZRssiSignal GetAvgRssi()
	{
		return m_AvgRssi;
	}
	

}
