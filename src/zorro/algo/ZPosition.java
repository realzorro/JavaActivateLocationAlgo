package zorro.algo;

import common.api.ZApi;

public class ZPosition {

	public String m_Name;   //设备的MAC地址
	public Integer m_MapID;

	public float m_CoorX;
	public float m_CoorY;
	public float m_CoorZ;

	public float m_CoorTime;

	public ZPosition(String strName, Integer nMapID, float CX, float CY) {
		// TODO Auto-generated method stub
		m_Name = strName;
		m_MapID = nMapID;

		m_CoorX = CX;
		m_CoorY = CY;

		m_CoorTime = ZApi.GetUnixMillis();
	}

	public void Update(Integer nMapID,float CX, float CY )
	{
		m_MapID=nMapID;
		
		m_CoorX=CX;
		m_CoorY=CY;
		
		m_CoorTime = ZApi.GetUnixMillis();
	}
	
	
	public void Update ( ZPosition pInput) {
		m_MapID=pInput.m_MapID;
		
		m_CoorX=pInput.m_CoorX;
		m_CoorY=pInput.m_CoorY;
		
		m_CoorTime = pInput.m_CoorTime;
	}	
	
	public void OffSet(Integer nMapID,float DX, float DY) {
		if( nMapID!=m_MapID )
			return;
	
		m_CoorX+=DX;
		m_CoorY+=DY;
	}
}
