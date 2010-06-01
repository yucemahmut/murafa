package com.vas.android.sensors;

import android.util.Log;
import edu.mit.media.android.amarino.BTService;
import edu.mit.media.android.amarino.StopWatch;


public class ScalesKLX349  {
	//DATA RECORD FORMAT
	
	private static final char STX = 0x2;
	private static final char CR = 0xd;
	
	//private int BIT0;
	//private int BIT1;
	//private int BIT2;
	//private final int  BIT3 = 0;
	//private int BIT4;
	//private int BIT5;
	//private int BIT6;
	//private final int BIT7 = 1;
	
	private final static int NORMAL_POSITIVE_WEIGHT 	= 	0;
	private final static int TEST_MODE 				= 	1;
	private final static int CALIBRATION				=	2;
	private final static int NORMAL_NEGATIVE_LENGTH	=	3;
	private final static int LOW_BATTERY				=	4;
	private final static int OVERLOAD					=	5;
	private final static int ZERO_COUNTS_TOO_LOW		=	6;
	private final static int MINUS_WEIGHT_UNDER_0		=	7;
	
	private final static int FORMAT_STANDARD_POUNDS	=	0;
	private final static int FORMAT_POUNDS				=	1;
	private final static int FORMAT_KG					=	2;
	
	private final static int UMask						=	0x70;
	private final static int UDivisor					=	0x10;
	
	private final static int SMask						=	0x7;
	private final static int SDivisor					=	0x1;
	
	private final static int POUND						=	0;
	private final static int KILOGRAM					=	1;
	
	private static int DEFAULT_METRICS 				=	POUND;
	
	private static float measured_weight_previous 		= 	0;
	private static float measured_weight_current;
	
	public static final String TAG 				= 	"Scales KLX349";
	
	
	//public float weight;
	
	
	
	public static float klx349getData (BTService btService){
	
		//we do not want to try to read data indefinitely
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
    
		while (stopwatch.getElapsedTime() < 50000){
    	
			System.out.println("Running within the loop: " + stopwatch.getElapsedTime());
			//we want to read data from this device
			//check if we have any data
    	
			if (btService.response.contains(String.valueOf(CR))){
    		    	
				//OK, we have something to play with
				//we will split the string into segments that starts with STX
    		
				String[] weights = btService.response.split(String.valueOf(STX));
    		
				//loop through the array to verify the weight
			
				for (int array_length = 0; array_length < weights.length; array_length++ ){
				
					//we want to make sure we've captured whole weight packet, not the segment
					//we want to verify that the packet length is 9 chars and it ends with CR, otherwise we discard the packet
				
					if (weights[array_length].length() == 9){
						//we want to verify that the packet ends with CR, otherwise we discard the packet
					
						if (weights[array_length].contains(String.valueOf(CR))){
							//this packet has the right data and we want to analyze it
							measured_weight_current = processWeightPacket(weights[array_length]);
							
							//if the previous weight is higher than the current one, we've got it
							
							if (measured_weight_current < measured_weight_previous){
								//OK, we've got our final weight
									return measured_weight_previous;
									}
										else{
											measured_weight_previous = measured_weight_current;
										}

							}
					
						}
				
					}
    		
				}
    	
			}	
	
		return measured_weight_previous;
		
	}



	private static float processWeightPacket(String s) {
		// we want to analyze third char of the string, it contains the status of the data
		
		int status = 65535 - s.charAt(2);
		
		//now we determine what exactly we are measuring
		int u2 	= 	(UMask & status) / UDivisor;
		//float weight;
		
		switch (u2){
		
			case NORMAL_POSITIVE_WEIGHT	:	float weight = getWeight(s); return weight;
			case TEST_MODE				: 	break;
			case CALIBRATION			:	break;
			case NORMAL_NEGATIVE_LENGTH	:	break;
			case LOW_BATTERY			:	break;
			case OVERLOAD				:	break;
			case ZERO_COUNTS_TOO_LOW	:	break;
			case MINUS_WEIGHT_UNDER_0	:	break;
		
		}
		
		return 0;
	}



	private static float getWeight(String s) {
		float measured_weight = 0;
		
		//weight as reported by scales, we do not know units yet
		try
		{
			//measured_weight_current = Float.valueOf(weights[array_length].substring(4,8).trim()).floatValue();
			measured_weight = Float.parseFloat(s.substring(3,7));
			Log.e(TAG, "measured weight is " + measured_weight + "recorded weight " + s);
		}
		catch (NumberFormatException nfe)
		{
			System.out.println("NumberFormatException: " + nfe.getMessage());
		}
		
		
		
		// we want to analyze third char of the string, it contains the status of the data
		int status = 65535 - s.charAt(2);
		
		//now we determine in what units - KILOGRAM or POUND -  we are measuring weight
		int u2 	= 	(SMask & status) / SDivisor;
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
