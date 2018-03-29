package com.cfets.cfib.args;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import imix.imix20.UserRequest;

public class ArgsPackUtilTest {
	/*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/	
	public static void main(String[] args) throws Exception {
		ByteBuffer bf = ByteBuffer.allocate(ArgsPackUtil.DEFAULT_BUFFER_CAPACITY);
		UserRequest msg = new UserRequest();
		
		Map<String,  HashMap<String, String>> aMap = new HashMap<String,  HashMap<String, String>>();
		
		HashMap<String, String> subMap = new HashMap<String, String>();
		subMap.put("name", "jack");
		subMap.put("sex", "male");
		
		aMap.put("people", subMap);

		
		ArrayList<Integer> subSeq = new ArrayList<Integer>();
		subSeq.add(1);
		subSeq.add(2);
		subSeq.add(3);
		subSeq.add(4);
		
		List<ArrayList<Integer>> seq = new ArrayList<ArrayList<Integer>>();
		for(int i=0;i<4;i++){
			seq.add(subSeq);
		}
		
		List<List<ArrayList<Integer>>> seqP = new ArrayList<List<ArrayList<Integer>>>();
		for(int i=0;i<4;i++){
			seqP.add(seq);
		}
		
		List<List<List<ArrayList<Integer>>>> seqPP = new ArrayList<List<List<ArrayList<Integer>>>>();
		for(int i=0;i<4;i++){
			seqPP.add(seqP);
		}
		
		int a = 3;
		
		HashMap<Integer,Integer> intMap = new HashMap<Integer,Integer>();
		intMap.put(1, 2);intMap.put(3, 4);intMap.put(5, 6);intMap.put(7, 8);
		
		Map<Integer,HashMap<Integer,Integer>> intMMap = new HashMap<Integer,HashMap<Integer,Integer>>();
		for(int i =1;i<=4;i++){
			intMMap.put(i, intMap);
		}
		
		List<HashMap<Integer,Integer>> listMap = new ArrayList<HashMap<Integer,Integer>>();
		for(int i =1;i<=4;i++){
			listMap.add(intMap);
		}
		
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		try {
			ArgsPackUtil util = new ArgsPackUtil();
			
			List<String> list = new ArrayList<String>();
			list.add("原木");list.add("得宝");
			
			int aaa =234;
//			bf = util.packageParameters(bf,  1,"suobing", 3,subMap, 2,aaa, 4,list);//调用打包方法
//			bf = util.packageParameters(bf, 1,intMMap);
			bf = util.packageParameters(bf, 1,seq);
//			bf.flip();
			byte[] bytes = new byte[bf.remaining()];
			bf.get(bytes);
			System.out.println(Arrays.toString(bytes));
//			System.out.println(Arrays.toString(bf.array()));
			//[34, 34, 10, 6, 112, 101, 111, 112, 108, 101, 18, 11, 10, 3, 115, 101, 120, 18, 4, 109, 97, 108, 101, 18, 11, 10, 4, 110, 97, 109, 101, 18, 3, 115, 117, 111, 42, 18, 10, 7, 10, 5, 49, 49, 48, 49, 49, 10, 7, 10, 5, 49, 50, 48, 49, 49]
			//[42, 18, 10, 7, 10, 5, 49, 49, 48, 49, 49, 10, 7, 10, 5, 49, 50, 48, 49, 49]
			int b = 0;
			
			bf.flip();
			System.out.println("pos:"+bf.position());
			
			
			int bbb;
			List<String> list2 = new ArrayList<String>();
//			Map<Integer, Object> rst = util.unpackageParameters(bf,  1,"hello", 3,subMap, 2,bbb, 4,list2);//解包
			Map<Integer, Object> rst = util.unpackageParameters(bf,  1,String.class, 3,Map.class, 2,Integer.class, 4,list2);
//			Map<Integer, Object> rst = util.unpackageParameters(bf,  1,intMap);
//			for(Entry<Integer, Object> e: rst.entrySet()){
//				System.out.println(e.getKey()+" "+e.getValue());
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
