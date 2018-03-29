package com.cfets.cfib.args;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TypeTest {

	public static void main(String[] args) throws NoSuchFieldException, SecurityException {
		// TODO Auto-generated method stub
		int i = 3;
		short s =4;
		double d = 1.23;
		float f = 1.23f;
		char c = 3;
		byte by = 3;
		
		System.out.println(50>>>3);
		foo(s);
		
		int var = new Integer(4);
		foo2(var);
		System.out.println("var: "+var);

		
		Map amap = new HashMap();
		amap.put(1, 12);
		amap.put(2, "122");
//		amap.put(key, value)
		System.out.println(amap.get(1) instanceof Short);
		
		Integer ii = null;
//		Map
		System.out.println(Integer.class.getSimpleName());
		
		Map amap2 = new HashMap();
		amap2.put(3, 'a');
		
		amap2.putAll(amap);
		
		System.out.println(amap2);
		
		Map<String,String> map = new HashMap<String,String>();
		ParameterizedType pt = (ParameterizedType)TypeTest.class.getDeclaredField("map").getGenericType();
        for(Type type : pt.getActualTypeArguments()) {
            System.out.println(type.toString());
        }
	}
	
	public static void foo2(Integer i){
		i++;
	}
	
	public static void foo(Object obj){
		if(obj instanceof Double){
			System.out.println("double");
		}
		if(obj instanceof Float){
			System.out.println("float");
		}
		if(obj instanceof Boolean){
			System.out.println("boolean");
		}
		if(obj instanceof Byte){
			System.out.println("byte");
		}
		if(obj instanceof Character){
			System.out.println("char");
		}
		if(obj instanceof Short){
			System.out.println("short");
		}
		if(obj instanceof Long){
			System.out.println("long");
		}
		if(obj instanceof Integer){
			System.out.println("int");
		}
	}
}
