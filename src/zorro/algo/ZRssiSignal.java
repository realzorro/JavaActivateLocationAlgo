package zorro.algo;

import common.config.ZConfig;


public class ZRssiSignal implements Comparable<ZRssiSignal> {
	String m_DeviceMAC;
	String m_TagMAC;

	float m_Rssi;
	long m_Time;

	private float m_Distance;
	
	/*
	 * 参数说明 nTime: 坐标生成时间，单位：毫秒
	 */
	public ZRssiSignal(String DevMAC, String TagMAC, float vRssi, long nTime) {
		m_DeviceMAC = DevMAC;
		m_TagMAC = TagMAC;

		m_Rssi = vRssi;
		m_Time = nTime;
		
		UpdateDistance();
	}

	public void Update(float vRssi, long nTime) {
		m_Rssi = vRssi;
		m_Time = nTime;
		
		UpdateDistance();
	}

	// override
	public int compareTo(ZRssiSignal in) {
		long nCha = this.m_Time - in.m_Time;

		if (Math.abs(nCha) <= ZConfig.RSSI_DISTANCE_SORT_TIME) {
			if (this.m_Rssi > in.m_Rssi)
				return -1;
			else if (this.m_Rssi < in.m_Rssi)
				return 1;
		}

		if (nCha >= 0)
			return -1;
		else
			return 1;
	}
	
	public float GetDistanceByRssi()
	{
		return m_Distance;
	}
	
	private void UpdateDistance()
	{
		float fDist=(float)(Math.pow(10, (ZConfig.RSSI_METER_ONE-this.m_Rssi)/ZConfig.RSSI_DAMPING_FACTOR));
		
		if( fDist<ZConfig.BUILDING_HEIGHT )
			fDist=0.5f;
		else
			fDist=(float)Math.pow(Math.pow(fDist, 2) - Math.pow(ZConfig.BUILDING_HEIGHT, 2) ,0.5);
		
		this.m_Distance=fDist;
	}
	
	
	
	
	
	
}
