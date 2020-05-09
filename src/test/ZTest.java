package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import zorro.algo.ZLocateAlgo;

public class ZTest {
	
	public static ArrayList<AccLine> m_AccList=new ArrayList<AccLine>();	
	
	public static void main( String [] Args )
	{
		LoadAccData();
		
		UpdateAccData();
	}
	
	
	
	public static void LoadAccData()
	{
		String strTxt="d:\\acc.txt";
		
		try {
			FileReader  fr = new FileReader (strTxt);
			BufferedReader out = new BufferedReader(fr);
			
			String AccLine=out.readLine();
			int nLine=0;
			
            while (AccLine!=null) 
            {
            	AccLine=AccLine.strip();
            	if(AccLine.length()!=0)
            	{
            		nLine++;
            		
                	String [] AccV= AccLine.split(",");
                	
                	float CX=Float.parseFloat(AccV[0]);
                	float CY=Float.parseFloat(AccV[1]);
                    float CZ=Float.parseFloat(AccV[2]);
                	
                	AccLine newAcc = new AccLine(CX, CY, CZ);
                	m_AccList.add(newAcc);
            	}
            	
            	AccLine=out.readLine();
			} 
			
			out.close();
			fr.close();
            
            System.out.print("AccData Total: "+nLine+"\r\n");
		} catch (Exception e) {
			System.out.print("Load AccData Error: "+e.getMessage());
			
			System.exit(0);			
		}

	}
	

	public static void UpdateAccData()
	{
		int nTotal=m_AccList.size();
		
		ZLocateAlgo mAlgo=new ZLocateAlgo();
		
		
		try {
			for(int n=0;n<nTotal;n++)
			{
				AccLine oneData = m_AccList.get(n);
				
				mAlgo.UpdateAccData(oneData.m_AccX, oneData.m_AccY, oneData.m_AccZ);				
				Thread.sleep(50);
			}			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.print("Update AccData Error: "+e.getMessage());
			
			System.exit(0);				
		}
		
	}
	
	
	
	
	
	
}


class AccLine
{
	public float m_AccX;
	public float m_AccY;
	public float m_AccZ;
	
	public AccLine(float AX,float AY,float AZ) {
		m_AccX=AX;
		m_AccY=AY;
		m_AccZ=AZ;
	}
}
