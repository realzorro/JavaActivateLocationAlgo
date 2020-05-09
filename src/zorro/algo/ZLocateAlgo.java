package zorro.algo;

import common.config.ZConfig;

public class ZLocateAlgo {

	ZDeviceMgr m_DevMgr;
	ZTagItem m_Tag;

	public ZLocateAlgo() {
		m_DevMgr = new ZDeviceMgr();
		m_Tag = new ZTagItem(ZConfig.SELF_IDENTIFY, m_DevMgr);
	}

	public void UpdateDevice(String MacAddr, Integer nMapID, float CX, float CY) {
		m_DevMgr.UpdateDeviceInfo(MacAddr, nMapID, CX, CY);
	}

	public void DeleteDevice(String MacAddr)
	{
		m_DevMgr.DeleteDevice(MacAddr);
	}
	
	public void ClearAllDevice() {
		m_DevMgr.Clear();
	}

	public void AddOneRssiData(String DeviceMacAddr, float vRssi, long nTime) {
		m_Tag.AddNewRssi(DeviceMacAddr, ZConfig.SELF_IDENTIFY, vRssi, nTime);
	}

	public void UpdateAccData(float acc_x,float acc_y, float acc_z)
	{
		m_Tag.UpdateTrend(acc_x, acc_y, acc_z);
	}
	
	public ZPosition CalculatePosition(float DirectAngle) {
		return m_Tag.CalculateTagPosition(DirectAngle);
	}

}
