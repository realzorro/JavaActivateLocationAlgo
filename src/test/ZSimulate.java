package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;


import org.json.JSONArray;
import org.json.JSONObject;

import common.api.ZApi;
import zorro.algo.ZLocateAlgo;
import zorro.algo.ZPosition;

public class ZSimulate {	
	public static void main( String [] Args )
	{
		ZLocateAlgo m_Algo=new ZLocateAlgo();
		
		LoadDevice(m_Algo);
		
		SimulateLocation(m_Algo);
	}



	static void LoadDevice( ZLocateAlgo pAlgo )
	{
		String deviceFile="d:\\simulate\\device.txt";
		
		try {
			FileReader  fr = new FileReader (deviceFile);
			BufferedReader out = new BufferedReader(fr);
			
			String deviceLine=out.readLine();

			out.close();
			fr.close();
			
			JSONObject pJson =new JSONObject(deviceLine);
			
			JSONArray devList=pJson.getJSONArray("deviceList");
			
			int nSize=devList.length();
			
			for(int n=0;n<nSize;n++)
			{
				JSONObject pOne = devList.getJSONObject(n);
				
				String strMac=pOne.getString("deviceMacId");
				int nMapID=pOne.getInt("floor");
				float  CX=(float)pOne.getDouble("coordinateX");
			    float  CY=(float)pOne.getDouble("coordinateY");
				
			    pl("Device Update: "+ n+" "+ strMac+" X-"+CX+"  Y-"+CY);
			    
				pAlgo.UpdateDevice(strMac, nMapID, CX, CY);
			}
		} catch (Exception e) {
			pl("Load Device Error: "+e.getMessage());
			
			System.exit(0);			
		}

	}


	
	static void SimulateLocation(ZLocateAlgo mAlgo)
	{
		//String SignalFile="d:\\simulate\\signal.txt";
		//String tagMac="3AD4EED9";
	
		long nNow=ZApi.GetUnixMillis();
		pl("Time: "+nNow);
		ZPosition NowPos =null;
		
		try 
		{
			/*
			 C2AAF9F28494         -62
			 C20041E69C9C         -60
			 C274F280A240         -70

			 C2FA2666D851         -55
			 C2EC2DEA3A45         -76
			 C29EF2C3549D         -74

			 C2F901B18CD6         -72
			 C2B72F81880E         -72
			 C28ED6ABF165         -68
			 * */
			
			mAlgo.AddOneRssiData("C2AAF9F28494", -72, nNow-5000);
			mAlgo.AddOneRssiData("C2AAF9F28494", -72, nNow-4000);
			mAlgo.AddOneRssiData("C2AAF9F28494", -72, nNow-3000);
			mAlgo.AddOneRssiData("C2AAF9F28494", -72, nNow-2000);
			mAlgo.AddOneRssiData("C2AAF9F28494", -72, nNow-1000);
			mAlgo.AddOneRssiData("C2AAF9F28494", -72, nNow);
			
			mAlgo.AddOneRssiData("C20041E69C9C", -63, nNow-5000);
			mAlgo.AddOneRssiData("C20041E69C9C", -63, nNow-4000);
			mAlgo.AddOneRssiData("C20041E69C9C", -63, nNow-3000);
			mAlgo.AddOneRssiData("C20041E69C9C", -63, nNow-2000);
			mAlgo.AddOneRssiData("C20041E69C9C", -63, nNow-1000);
			mAlgo.AddOneRssiData("C20041E69C9C", -63, nNow);
			
			mAlgo.AddOneRssiData("C274F280A240", -68, nNow-4800);
			mAlgo.AddOneRssiData("C274F280A240", -68, nNow-4000);
			mAlgo.AddOneRssiData("C274F280A240", -68, nNow-3200);
			mAlgo.AddOneRssiData("C274F280A240", -68, nNow-2400);
			mAlgo.AddOneRssiData("C274F280A240", -68, nNow-1600);
			mAlgo.AddOneRssiData("C274F280A240", -68, nNow-800);
			mAlgo.AddOneRssiData("C274F280A240", -68, nNow);			
			
			mAlgo.AddOneRssiData("D1554CEE14A3", -71, nNow-5000);
			mAlgo.AddOneRssiData("D1554CEE14A3", -71, nNow-4000);
			mAlgo.AddOneRssiData("D1554CEE14A3", -71, nNow-3000);
			mAlgo.AddOneRssiData("D1554CEE14A3", -71, nNow-2000);
			mAlgo.AddOneRssiData("D1554CEE14A3", -71, nNow-1000);
			mAlgo.AddOneRssiData("D1554CEE14A3", -71, nNow);

			mAlgo.AddOneRssiData("C2FA2666D851", -70, nNow-5000);
			mAlgo.AddOneRssiData("C2FA2666D851", -70, nNow-4000);
			mAlgo.AddOneRssiData("C2FA2666D851", -70, nNow-3000);
			mAlgo.AddOneRssiData("C2FA2666D851", -70, nNow-2000);
			mAlgo.AddOneRssiData("C2FA2666D851", -70, nNow-1000);
			mAlgo.AddOneRssiData("C2FA2666D851", -70, nNow);			
			
			NowPos=mAlgo.CalculatePosition(0);
			
			pl("Coor Now: "+NowPos.m_CoorX+" - "+NowPos.m_CoorY);
		} 
		catch (Exception e) 
		{
			pl("Simulate Signal Error : "+e.getMessage());
		}

	}	
	
	
	
//	static void SimulateLocation(ZLocateAlgo mAlgo)
//	{
//		String SignalFile="d:\\simulate\\signal.txt";
//		String tagMac="3AD4EED9";
//	
//		long nNow=ZApi.GetUnixMillis();
//		pl("Time: "+nNow);
//		ZPosition NowPos =null;
//		
//		ArrayList<String> sLines=new ArrayList<String>();
//		
//		try 
//		{
//			FileReader  fr = new FileReader (SignalFile);
//			BufferedReader out = new BufferedReader(fr);
//			
//			String SignalLine=out.readLine();
//			while(SignalLine!=null)
//			{
//				if(SignalLine.indexOf(tagMac)>=0)
//					sLines.add(SignalLine);
//				
//				SignalLine=out.readLine();
//			}
//			
//			int nTotal=sLines.size();
//			int nBlock=nTotal/10;
//			for(int n=0;n<nTotal;n++) 
//			{
//				long nTime=nNow-(10-n/nBlock)*1000;
//				//pl("Signal Time: "+nTime);
//				
//				SignalLine=sLines.get(n);
//				JSONObject pOne = new JSONObject(SignalLine);
//				
//				String strType=pOne.getString("Type");
//				if (strType.equals("Light"))
//				{
//					Set<String> pKey = pOne.keySet();
//					pKey.remove("Type");
//					
//					ArrayList<String> dList=new ArrayList<>(pKey);
//					String DevMac =dList.get(0);
//					
//					JSONObject pSS=pOne.getJSONObject(DevMac);
//					
//					if(pSS.has(tagMac))
//					{
//						float fRssi=Float.parseFloat( pSS.getString(tagMac) );
//						mAlgo.AddOneRssiData(DevMac, fRssi, nTime);
//					}
//				}			
//			}
//	
//			out.close();
//			fr.close();		
//			
//			NowPos=mAlgo.CalculatePosition();
//			
//			pl("Coor Now: "+NowPos.m_CoorX+" - "+NowPos.m_CoorY);
//		} 
//		catch (Exception e) 
//		{
//			pl("Simulate Signal Error : "+e.getMessage());
//		}
//
//	}
	
	
	static void pl(String OneLog) 
	{
		Date now = new Date();		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strT = sdf.format(now);
		
		System.out.println(strT+" : "+OneLog);
	}	
	
	
	
}
