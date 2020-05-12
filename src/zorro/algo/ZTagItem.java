package zorro.algo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import common.api.ZApi;
import common.config.ZConfig;
import common.log.ZLog;

public class ZTagItem {
	String m_TagMAC;
	ZPosition m_Pos;

	int m_MoveFlag; // 1-move (default) ; 0-stop
	long m_MoveChangeTime;
	int m_MoveTrend;

	int m_MoveStill;

	Map<String, ZRssiQueue> m_RssiQueue = new HashMap<String, ZRssiQueue>();

	ZDeviceMgr m_DeviceMgr;

	ZPosition m_LastPos;

	// Trend
	int m_StepNow;
	int m_LastStep;
	long m_LastStepTime;

	int m_TrendUp;

	float m_LastAngle;
	
	public ZTagItem(String MacAddr, ZDeviceMgr pDeviceMgr) {
		m_TagMAC = MacAddr;
		m_Pos = new ZPosition(MacAddr, 0, 0, 0);

		m_MoveFlag = 1;
		m_MoveChangeTime = 0;
		m_MoveTrend = 1;

		m_MoveStill = 0;

		m_DeviceMgr = pDeviceMgr;

		m_LastPos = new ZPosition(MacAddr, 0, 0, 0);

		// Trend
		m_StepNow = 0;
		m_LastStep = 0;
		m_LastStepTime = 0;

		m_TrendUp = 0;
		
		m_LastAngle=-999f;
	}

	public void AddNewRssi(String DeviceMac, String TagMac, float fRssi, long nTime) {
		ZRssiQueue pQueue = m_RssiQueue.get(DeviceMac);
		if (pQueue == null) {
			pQueue = new ZRssiQueue(DeviceMac, TagMac);
			m_RssiQueue.put(DeviceMac, pQueue);
		}

		pQueue.AddNewRssi(DeviceMac, TagMac, fRssi, nTime, m_MoveFlag);
	}

	public void SetTagMoveStatus(Integer nMove) {
		m_MoveFlag = nMove;
	}

	public Integer GetTagMoveStatus() {
		return m_MoveFlag;
	}

	public ZPosition CalculateTagPosition(float DirectAngle) {
		boolean bCalculate = CombineAlgorithm();
		if (bCalculate == true) {
			OptimizeStep(DirectAngle);
		}
		else {
			ZLog.pl("Position Calculate Failed!");
		}

		return m_Pos;
	}

	private boolean CombineAlgorithm() {
		boolean bResult = false;

		long nNow = ZApi.GetUnixMillis();

		// Filter Rssi
		ArrayList<ZRssiSignal> RssiSort = new ArrayList<ZRssiSignal>();
		for (String DevMac : m_RssiQueue.keySet()) {
			if (!m_DeviceMgr.HasDevice(DevMac))
				continue;

			ZRssiSignal pOne = m_RssiQueue.get(DevMac).GetAvgRssi();
			if (nNow - pOne.m_Time > ZConfig.RSSI_QUEUE_EXPIRETIME) {
				m_RssiQueue.remove(DevMac);
				continue;
			}

			if (pOne.m_Rssi <= ZConfig.RSSI_MIN_VALUE)
				continue;

			RssiSort.add(pOne);
		}

		int nValid = RssiSort.size();
		if (nValid == 0)
			return false;

		// Filter MapID
		int nMapID = -1;

		Collections.sort(RssiSort);

		Map<Integer, Integer> MapS = new HashMap<Integer, Integer>();
		for (int m = 0; m < nValid; m++) {
			ZRssiSignal pOne = RssiSort.get(m);
			int Tid = m_DeviceMgr.GetDevice(pOne.m_DeviceMAC).GetPosition().m_MapID;
			if (m == 0)
				nMapID = Tid;

			int nCount = 0;
			if (MapS.containsKey(Tid)) {
				nCount = MapS.get(Tid);
				nCount++;
				MapS.put(Tid, nCount);
			} else {
				nCount = 1;
				MapS.put(Tid, nCount);
			}

			if (nCount >= 2) {
				nMapID = Tid;
				break;
			}
		}

		for (int m = 0; m < nValid; m++) {
			ZRssiSignal pOne = RssiSort.get(m);
			int Tid = m_DeviceMgr.GetDevice(pOne.m_DeviceMAC).GetPosition().m_MapID;

			if (Tid != nMapID) {
				RssiSort.remove(m);
				m--;
			}
		}

		// Calculate
		nValid = RssiSort.size();
		if (nValid == 0) {
			// Unvalid
			return false;
		} else if (nValid < ZConfig.ALGO_MIN_DEVICE) {
			// With Less Device
			if (nValid == 1 && RssiSort.get(0).m_Rssi >= ZConfig.ALGO_SINGLE_CALCULATE_RSSI) {
				// 1 device
				ZRssiSignal pNow = RssiSort.get(0);
				ZPosition pDevPos = m_DeviceMgr.GetDevice(pNow.m_DeviceMAC).GetPosition();

				m_LastPos.Update(nMapID, pDevPos.m_CoorX, pDevPos.m_CoorY);

				ZLog.pl("Single-Device Location");
				
				bResult = true;
			} else if (nValid == 2) {
				// 2 device
				float fX = 0;
				float fY = 0;

				ZRssiSignal s1 = RssiSort.get(0);
				ZRssiSignal s2 = RssiSort.get(1);

				if (s1.m_Rssi >= ZConfig.ALGO_SINGLE_CALCULATE_RSSI
						&& s2.m_Rssi >= ZConfig.ALGO_SINGLE_CALCULATE_RSSI) {
					ZPosition p1 = m_DeviceMgr.GetDevice(s1.m_DeviceMAC).GetPosition();
					ZPosition p2 = m_DeviceMgr.GetDevice(s2.m_DeviceMAC).GetPosition();

					float fm = 1 / s1.GetDistanceByRssi() + 1 / s2.GetDistanceByRssi();

					fX = (p1.m_CoorX / s1.GetDistanceByRssi() + p2.m_CoorX / s2.GetDistanceByRssi()) / fm;
					fY = (p1.m_CoorY / s1.GetDistanceByRssi() + p2.m_CoorY / s2.GetDistanceByRssi()) / fm;

					m_LastPos.Update(nMapID, fX, fY);

					ZLog.pl("Two-Device Location");
					
					bResult = true;
				}
			}

			return bResult;
		} else {
			bResult = TriangleLocation(RssiSort);
		}

		return bResult;
	}

	private boolean TriangleLocation(ArrayList<ZRssiSignal> RssiSort) {
		ZLog.pl("Begin TriangleLocation.  \r\n");
		int nValid = RssiSort.size();
		if (nValid < ZConfig.ALGO_MIN_DEVICE)
			return false;

		nValid = nValid > ZConfig.ALGO_MAX_DEVICE ? ZConfig.ALGO_MAX_DEVICE : nValid;

		int nMapID = m_DeviceMgr.GetDevice(RssiSort.get(0).m_DeviceMAC).GetPosition().m_MapID;

		// Area
		float CX_min = 0, CX_max = 0, CY_min = 0, CY_max = 0;
		int n = 0;
		for (n = 0; n < nValid; n++) {
			ZRssiSignal pOne = RssiSort.get(n);
			ZPosition DevPos = m_DeviceMgr.GetDevice(pOne.m_DeviceMAC).GetPosition();

			if (n == 0) {
				CX_min = DevPos.m_CoorX;
				CX_max = DevPos.m_CoorX;

				CY_min = DevPos.m_CoorY;
				CY_max = DevPos.m_CoorY;
			} else {
				if (DevPos.m_CoorX < CX_min) {
					CX_min = DevPos.m_CoorX;
				}

				if (DevPos.m_CoorX > CX_max) {
					CX_max = DevPos.m_CoorX;
				}

				if (DevPos.m_CoorY < CY_min) {
					CY_min = DevPos.m_CoorY;
				}

				if (DevPos.m_CoorY > CY_max) {
					CY_max = DevPos.m_CoorY;
				}
			}

			if ((CX_max - CX_min) * (CY_max - CY_min) >= ZConfig.ALGO_AREA_MAX) {
				break;
			}
		}
		nValid = n - 1;

		if (nValid < ZConfig.ALGO_MIN_DEVICE)
			return false;

		CX_min = CX_min - ZConfig.ALGO_AREA_BLOCK_SIZE / 2;
		CX_max = CX_max + ZConfig.ALGO_AREA_BLOCK_SIZE / 2;
		CY_min = CY_min - ZConfig.ALGO_AREA_BLOCK_SIZE / 2;
		CY_max = CY_max + ZConfig.ALGO_AREA_BLOCK_SIZE / 2;

		// ZLog.pl("X_min: "+CX_min+" X_max: "+CX_max+" Y_min: "+CY_min+" Y_max:
		// "+CY_max);

		// Loss Calculate
		int sX = (int) Math.floor((CX_max - CX_min) / ZConfig.ALGO_AREA_BLOCK_SIZE);
		int sY = (int) Math.floor((CY_max - CY_min) / ZConfig.ALGO_AREA_BLOCK_SIZE);

		float LossMin = Float.MAX_VALUE;
		float fX = -1;
		float fY = -1;

		for (int i = 0; i < sX; i++) {
			for (int j = 0; j < sY; j++) {
				float TX = CX_min + i * ZConfig.ALGO_AREA_BLOCK_SIZE + ZConfig.ALGO_AREA_BLOCK_SIZE / 2;
				float TY = CY_min + j * ZConfig.ALGO_AREA_BLOCK_SIZE + ZConfig.ALGO_AREA_BLOCK_SIZE / 2;

				ZPosition pTemp = new ZPosition("", nMapID, TX, TY);

				float tLoss = 0;

				for (n = 0; n < nValid; n++) {
					ZRssiSignal pOne = RssiSort.get(n);
					ZDeviceItem pDev = m_DeviceMgr.GetDevice(pOne.m_DeviceMAC);
					ZPosition DevPos = pDev.GetPosition();

					tLoss += Math.abs(CalculateDistance(pTemp, DevPos) - pOne.GetDistanceByRssi())
							/ pOne.GetDistanceByRssi();
//					tLoss += Math.abs(CalculateDistance(pTemp, DevPos))
//							/ pOne.GetDistanceByRssi();
				}

				if (tLoss < LossMin) {
					fX = TX;
					fY = TY;

					LossMin = tLoss;
				}
			}
		}

		ZLog.pl("Triangle Result:"+nMapID+"-"+fX+"-"+fY+"\r\n");
		
		m_LastPos.Update(nMapID, fX, fY);

		return true;
	}

	private float CalculateDistance(ZPosition p1, ZPosition p2) {
		return (float) Math.pow(Math.pow(p1.m_CoorX - p2.m_CoorX, 2) + Math.pow(p1.m_CoorY - p2.m_CoorY, 2), 0.5);
	}

	private void OptimizeStep(float DirectAngle) {
		if( m_LastAngle==-999f )
			m_LastAngle=DirectAngle;
		
		if (m_MoveStill==1)
		{
			ZLog.pl("Tag Still! No Optimize");
			return;
		}
		
		if (m_LastPos.m_MapID != m_Pos.m_MapID) {
			m_Pos.Update(m_LastPos);
			
			ZLog.pl("Tag MapID Changde: "+m_LastPos.m_MapID);
			
			return;
		}

		float DX = m_LastPos.m_CoorX - m_Pos.m_CoorX;
		float DY = m_LastPos.m_CoorY - m_Pos.m_CoorY;

		if (DX == 0 && DY == 0)
		{
			ZLog.pl("New Position is same with old!");
			
			return;
		}

		if (m_MoveFlag == 0) {
			if (Math.abs(DX) > ZConfig.OFFSET_STOP_STEP) {
				DX = DX * ZConfig.OFFSET_LONG_FACTOR;
			} else {
				DX = DX * ZConfig.OFFSET_SHORT_FACTOR;
			}

			if (Math.abs(DY) > ZConfig.OFFSET_STOP_STEP) {
				DY = DY * ZConfig.OFFSET_LONG_FACTOR;
			} else {
				DY = DY * ZConfig.OFFSET_SHORT_FACTOR;
			}
		} else {
			if (Math.abs(DX) > ZConfig.OFFSET_MOVE_STEP) {
				DX = DX * ZConfig.OFFSET_LONG_FACTOR;
			} else {
				DX = DX * ZConfig.OFFSET_SHORT_FACTOR;
			}

			if (Math.abs(DY) > ZConfig.OFFSET_MOVE_STEP) {
				DY = DY * ZConfig.OFFSET_LONG_FACTOR;
			} else {
				DY = DY * ZConfig.OFFSET_SHORT_FACTOR;
			}
		}

		if(ZConfig.TREND_SWITCH)
		{
			float StepFactor=0;
			float AngleChange=Math.abs(DirectAngle-m_LastAngle);
			if(AngleChange>180)
			{
				AngleChange=360-AngleChange;
			}
			StepFactor=(180-AngleChange)/180;
			
			float nStepC = GetStepChange();
			if (nStepC > 0) {
				DX = (float) (Math.sin(DirectAngle * 3.1416 / 180) * nStepC * 0.44f * StepFactor) + DX * 0.33f;
				DY = (float) (Math.cos(DirectAngle * 3.1416 / 180) * nStepC * 0.44f * StepFactor) + DY * 0.33f;
			}
		}
		
		m_LastAngle=DirectAngle;
		
		ZLog.pl("OffSet:  CX- "+DX+" CY-"+DY);
		
		m_Pos.OffSet(m_LastPos.m_MapID, DX, DY);

		return;
	}

	// 惯导
	public void UpdateTrend(float acc_x, float acc_y, float acc_z) {
		long nTime = ZApi.GetUnixMillis();
        if(m_MoveChangeTime==0)
        	m_MoveChangeTime=nTime;
		
		double fSum = 0;

		fSum = Math.pow(Math.pow(acc_x, 2) + Math.pow(acc_y, 2) + Math.pow(acc_z, 2), 0.5);

		if (fSum > ZConfig.TREND_ACC_UP) {
			if (m_TrendUp == 0)
				m_MoveChangeTime = nTime;

			m_TrendUp = 1;

			m_MoveFlag = 1;
		}

		if (fSum < ZConfig.TREND_ACC_DOWN) {
			if (m_TrendUp == 1) {
				m_StepNow++;
			}
			m_TrendUp = 0;			
		}

		double fSum_xy, fSum_xz, fSum_yz;

		fSum_xy = Math.pow(Math.pow(acc_x, 2) + Math.pow(acc_y, 2), 0.5);
		fSum_xz = Math.pow(Math.pow(acc_x, 2) + Math.pow(acc_z, 2), 0.5);
		fSum_yz = Math.pow(Math.pow(acc_y, 2) + Math.pow(acc_z, 2), 0.5);

		if (fSum_xy < ZConfig.TREND_ACC_STOP || fSum_xz < ZConfig.TREND_ACC_STOP || fSum_yz > ZConfig.TREND_ACC_STOP) {
			if (m_MoveTrend == 1) {
				m_MoveChangeTime = nTime;
			} else {
				if ((nTime - m_MoveChangeTime) >= ZConfig.STOP_ENTER_TIME)
					m_MoveFlag = 0;
				if ((nTime - m_MoveChangeTime) >= ZConfig.STILL_ENTER_TIME) {
					m_MoveStill = 1;
				}
			}

			m_MoveTrend = 0;
		} else {
			if (m_MoveTrend == 0)
				m_MoveChangeTime = ZApi.GetUnixMillis();

			m_MoveTrend = 1;
			m_MoveFlag = 1;

			m_MoveStill = 0;
		}
		
		ZLog.pl("MoveFlag-"+m_MoveFlag+"  TrendUp-"+m_TrendUp+"  Still-"+m_MoveStill+"\r\n");
	}

	public float GetStepChange() {
	    float nVal =(float)(m_StepNow - m_LastStep);

		m_LastStep = m_StepNow;
		m_LastStepTime = ZApi.GetUnixMillis();

		return nVal;
	}

}
