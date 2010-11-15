package com.vas.android.bluetooth.sample.sample1;



public class ScalesKLX349  {
	//DATA RECORD FORMAT
	
	private static final byte STX = 0x2;
	private static final byte CR = 0xd;
	
	private final static byte NORMAL_POSITIVE_WEIGHT 		= 	0;
	private final static byte TEST_MODE 					= 	1;
	private final static byte CALIBRATION					=	2;
	private final static byte NORMAL_NEGATIVE_LENGTH		=	3;
	private final static byte LOW_BATTERY					=	4;
	private final static byte OVERLOAD						=	5;
	private final static byte ZERO_COUNTS_TOO_LOW			=	6;
	private final static byte MINUS_WEIGHT_UNDER_0			=	7;
	
	private final static byte FORMAT_STANDARD_POUNDS		=	0;
	private final static byte FORMAT_POUNDS					=	1;
	private final static byte FORMAT_KG						=	2;
	
	//private final static int SLEEP_TIME						=	100;
	
	private final static byte UMask							=	0x70;
	private final static byte UDivisor						=	0x10;
	
	private final static byte SMask							=	0x7;
	private final static byte SDivisor						=	0x1;
	
	private final static int POUND							=	0;
	private final static int KILOGRAM						=	1;
	
	//private static int DEFAULT_METRICS 						=	POUND;
	private static int DEFAULT_METRICS 						=	KILOGRAM;
	
	private static float measured_weight_previous 			= 	(float)0;
	private static float measured_weight_current;
	
	
	
	//private final static int CONFIDENCE_THRESHHOLD				=	100;
	//private static int CONFIDENCE_LEVEL; 
	
	//private static boolean DEBUG							= 	true;
	public static final String TAG 							= 	"Scales KLX349";
	
	//private byte [] raw_data;
	//public float weight;
	
	
	
	public static float klx349getData (byte[] btService, int length){
	
		//we do not want to try to read data indefinitely
		//StopWatch stopwatch = new StopWatch();
		//stopwatch.start();
    
		
    	
			
			//we will parse our array
					
					//int j;
		
					measured_weight_previous 			= 	(float)0;
					
					for (int i=0; i<=length-10; ){
						
						//we need to slice the byte array by STX marker
						if (btService[i] == STX && btService[i+9] == CR){
							//we get new block of data, lets get weight
							measured_weight_current = processWeightPacket(btService[i],btService[i+1],btService[i+2],btService[i+3],btService[i+4],btService[i+5],btService[i+6],btService[i+7],btService[i+8],btService[i+9]);
							i = i+10;
							//if the current weight is higher than the previous one, then this is potentially our candidate for maximum
							if (measured_weight_current > measured_weight_previous  ){
								measured_weight_previous = measured_weight_current;
							}
						}else{
							i++;
						}
					}
					
		
		return measured_weight_previous;
		
	}	



	private static float processWeightPacket(byte s, byte s1,byte s2,byte s3, byte s4, byte s5, byte s6, byte s7, byte s8, byte s9 ) {
		// we want to analyze second byte of the string, it contains the status of the data
		
		
		float weight = 0;
		
		int u2 	= 	(UMask & s1) ;
		u2 = u2 / UDivisor;
		switch (u2){
		
			case NORMAL_POSITIVE_WEIGHT	:	weight = getWeight(s, s1, s2, s3, s4, s5, s6, s7, s8, s9);break;
			case TEST_MODE				: 	break;
			case CALIBRATION			:	break;
			case NORMAL_NEGATIVE_LENGTH	:	break;
			case LOW_BATTERY			:	break;
			case OVERLOAD				:	break;
			case ZERO_COUNTS_TOO_LOW	:	break;
			case MINUS_WEIGHT_UNDER_0	:	break;
		
		}
		
		return weight;
		
		
		
		//float weight = getWeight(s); 
		//return weight;
	}



	private static float getWeight(byte s, byte s1,byte s2,byte s3, byte s4, byte s5, byte s6, byte s7, byte s8, byte s9) {
		float measured_weight = 0;
		
		//weight as reported by scales, we do not know units yet
		try
		{
			
			//we need to make sure that we are reading weight not garbage
			
			if(((int)s4-48) > 9 || ((int)s5-48)>9 || ((int)s6-48)>9 || ((int)s8-48)>9){
				measured_weight = 0;
			}else{
			measured_weight = (float)(100.0*((int)s4-48)) + (float)(10.0*((int)s5-48))+(float)(1.0*((int)s6-48))+(float)(((int)s8-48)/10.0);
			//Log.e(TAG, "measured weight is " + measured_weight + "recorded weight " + s);
			}
		}
		catch (NumberFormatException nfe)
		{
			System.out.println("NumberFormatException: " + nfe.getMessage());
		}
		
		
		
		// we want to analyze second byte of the string, it contains the status of the data
		
		
				
		//now we determine in what units - KILOGRAM or POUND -  we are measuring weight
		int u2 	= 	(SMask & s1) / SDivisor;
		int used_metrics = POUND;
		
		switch (u2){
		case FORMAT_STANDARD_POUNDS		:	used_metrics = POUND; break;
		case FORMAT_POUNDS				:	used_metrics = POUND; break;				
		case FORMAT_KG					:	used_metrics = KILOGRAM; break;
		}
		
		//no matter what metrics scales are using, we will always return weight in DEFAULT METRICS
		switch (DEFAULT_METRICS){
		case POUND						:	if (used_metrics == DEFAULT_METRICS) return measured_weight; else {measured_weight = kilo2pound(measured_weight); return measured_weight;} 
		case KILOGRAM					:	if (used_metrics == DEFAULT_METRICS) return measured_weight; else {measured_weight = pound2kilo(measured_weight); return measured_weight;}	
		}
		
		return measured_weight;
		
	}



	private static float pound2kilo(float m) {
		// converting pounds into kilograms
		float weight = (float) (m * 0.45359237);
		return weight;
	}



	private static float kilo2pound(float m) {
		// converting kilograms into pounds 
		float weight = (float) (m / 0.45359237);
		return weight;
	}
	
}
