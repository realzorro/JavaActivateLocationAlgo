package zorro.algo;

public class ZDeviceItem {
	String m_MAC;
	ZPosition m_Pos;
 	
	public ZDeviceItem(String MacAddr, Integer nMapID, float CX,float CY) 
	{
		m_MAC=MacAddr;

		m_Pos=new ZPosition(MacAddr,nMapID,CX,CY);
	}
	
	
	public ZPosition GetPosition()
	{
		return m_Pos;
	}
	
	
	public Integer GetMapID()
	{
		return m_Pos.m_MapID;
	}
	
	public void Update(Integer nMapID, float CX,float CY)
	{
		m_Pos.Update(nMapID, CX, CY);
	}
	
}
