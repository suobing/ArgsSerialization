package com.cfets.cfib.args;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.protobuf.CodedOutputStream;

import imix.imix20.*;

public class MainTest {

	public ByteBuffer packageParameters(ByteBuffer bf, Object... args) throws Exception{
		CodedOutputStream output = CodedOutputStream.newInstance(bf);
		if(args.length%2 == 1){
			throw new Exception("Wrong args amount");
		}
		boolean numPos = true;
		int currNum = 0;
		for(Object arg: args){
			if(numPos){
				if(arg instanceof Integer){
					currNum = (int)arg;
				}else{
					throw new Exception("Arg must be integer in fieldNumber postion");
				}
				numPos = !numPos;
			}else{
				packageArg(output, arg, currNum);
				numPos = !numPos;
			}
		}//for
		
		output.flush();
		System.out.println(bf.position());
		return (ByteBuffer)bf.flip();
	}
	
	public void packageArg(CodedOutputStream output, Object arg, int number) throws Exception{
		if(arg == null){
			throw new NullPointerException("find null parameter!!!");
		}
		if(arg instanceof Integer){
			packageArg(output, (int) arg, number);
		}else if(arg instanceof String){
			packageArg(output, (String)arg, number);
		}else if(arg instanceof Message){
			packageArg(output, (Message)arg, number);
		}else if(arg instanceof Map){
			System.out.println("Map type");
			packageArg(output, (Map)arg, number);
		}else if(arg instanceof List){
			System.out.println("List type");
			packageArg(output, (List)arg, number);
		}
	}
	
	public void packageArg(CodedOutputStream output, int arg, int number) throws IOException{
		output.writeInt32(number, arg);
		System.out.println("Integer packaged...");
	}
	
	public void packageArg(CodedOutputStream output, String arg, int number) throws IOException{
		com.google.protobuf.GeneratedMessageV3.writeString(output, number, arg);
		System.out.println("String packaged...");
	}
	public void packageArgInList(CodedOutputStream output, String arg, int number) throws IOException{
		packageArg(output, arg, number);
	}
	public void packageArg(CodedOutputStream output, imix.imix20.Message arg, int number) throws IOException{
		packageArg(output, arg.toString(), number);
		System.out.println("Imix Message packaged...");
	}
	
	public <K,V> void packageArg(CodedOutputStream output, Map<K,V> arg, int number) throws Exception{
		for(Entry<K,V> e : arg.entrySet()){
			ByteBuffer subBuffer = ByteBuffer.allocate(1024);
			subBuffer = packageParameters(subBuffer, 1, e.getKey(), 2, e.getValue());
			output.writeByteArray(number, subBuffer.array(), 0, subBuffer.limit());
		}
	}
	
	public <E> void packageArg(CodedOutputStream output, List<E> arg, int number) throws Exception{
		for(E e: arg){
			packageArgInList(output, e, number);
			/*ByteBuffer subBuffer = ByteBuffer.allocate(1024);
			subBuffer = packageParameters(subBuffer, 1, e);
			output.writeByteArray(number, subBuffer.array(), 0, subBuffer.limit());*/
		}
	}
	
	public <E> void packageArgInList(CodedOutputStream output, List<E> arg, int number) throws Exception{
		ByteBuffer subBuffer = ByteBuffer.allocate(1024);
		for(E e: arg){
			CodedOutputStream subOutput = CodedOutputStream.newInstance(subBuffer);
			packageArg(subOutput, e, 1);
			
			subBuffer = packageParameters(subBuffer, 1, e);
		}

		output.writeByteArray(number, subBuffer.array(), 0, subBuffer.limit());
	}
	
	public static void main(String[] args) throws Exception {
		ByteBuffer bf = ByteBuffer.allocate(1024);
		UserRequest msg = new UserRequest();
		
		Map<String,  HashMap<String, String>> aMap = new HashMap<String,  HashMap<String, String>>();
		
		HashMap<String, String> subMap = new HashMap<String, String>();
		subMap.put("name", "suo");
		subMap.put("sex", "male");
//		subMap.put("name", "terry");
		
		aMap.put("people", subMap);

//		List<ArrayList<String>> seq = new ArrayList<ArrayList<String>>();
		ArrayList<String> subSeq = new ArrayList<String>();
		
		subSeq.add("11011");
		subSeq.add("12011");
//		seq.add(subSeq);
		
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		try {
			bf = new MainTest().packageParameters(bf,  5, subSeq);//调用打包方法
			byte[] bytes = new byte[bf.remaining()];
			bf.get(bytes, 0, bytes.length);
			System.out.println(Arrays.toString(bytes));
//			System.out.println(Arrays.toString(bf.array()));
			//[34, 34, 10, 6, 112, 101, 111, 112, 108, 101, 18, 11, 10, 3, 115, 101, 120, 18, 4, 109, 97, 108, 101, 18, 11, 10, 4, 110, 97, 109, 101, 18, 3, 115, 117, 111, 42, 18, 10, 7, 10, 5, 49, 49, 48, 49, 49, 10, 7, 10, 5, 49, 50, 48, 49, 49]
			//[42, 18, 10, 7, 10, 5, 49, 49, 48, 49, 49, 10, 7, 10, 5, 49, 50, 48, 49, 49]
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
