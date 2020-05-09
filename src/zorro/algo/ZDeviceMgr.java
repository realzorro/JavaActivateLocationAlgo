package zorro.algo;

import java.util.HashMap;
import java.util.Map;


public class ZDeviceMgr {
	Map<String, ZDeviceItem> m_DevMgr;

	public ZDeviceMgr() {
		m_DevMgr = new HashMap<String, ZDeviceItem>();
	}

	public void UpdateDeviceInfo(String MacAddr, Integer nMapID, float CX, float CY) {
		ZDeviceItem pOne = m_DevMgr.get(MacAddr);
		if (pOne == null) {
			pOne = new ZDeviceItem(MacAddr, nMapID, CX, CY);
			m_DevMgr.put(MacAddr, pOne);
		}
	}

	public ZDeviceItem DeleteDevice(String MacAddr) 
	{
		return m_DevMgr.remove(MacAddr);
	}
	
	public boolean HasDevice(String MacAddr)
	{
		return m_DevMgr.containsKey(MacAddr);
	}

	public ZDeviceItem GetDevice(String MacAddr) {
		return m_DevMgr.get(MacAddr);		
	}
	
	
	public void Clear()
	{
		m_DevMgr.clear();
	}
	
}
