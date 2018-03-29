package com.cfets.cfib.args;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

public class Test {
	Map<String,String> map = new HashMap<String,String>();
	/**
	 * @param args
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	public static void main(String[] args) throws NoSuchFieldException, SecurityException {
		
		ParameterizedType pt = (ParameterizedType)Test.class.getDeclaredField("map").getGenericType();
        for(Type type : pt.getActualTypeArguments()) {
            System.out.println(type.toString());
        }
        
        Integer x =1;
        Field field=x.getClass().getDeclaredField("value");
        Type[] t = ((ParameterizedType)field.getGenericType()).getActualTypeArguments();
        System.out.println(t);
	}
}
