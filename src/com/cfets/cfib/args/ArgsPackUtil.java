package com.cfets.cfib.args;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.WireFormat;

import cn.com.cfets.data.InvalidDataException;
import cn.com.cfets.data.MetaObject;
import imix.imix20.*;

public class ArgsPackUtil {

	public static final int DEFAULT_BUFFER_CAPACITY = 1024;
	
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
				packageArg(output, currNum, arg);
				numPos = !numPos;
			}
		}//for
		
		output.flush();
		System.out.println(bf.position());
		return (ByteBuffer)bf.flip();
	}
	
	/***
	 * general arg package function
	 * @param output
	 * @param number
	 * @param arg
	 * @throws Exception
	 */
	public void packageArg(CodedOutputStream output, int number, Object arg) throws Exception{
		if(arg == null){
			throw new NullPointerException("Null parameter found!!!");
		}
		if(arg instanceof Integer ||arg instanceof Short){
			packageInt(output, number, (int)arg);
		}else if(arg instanceof Long){
			packageLong(output, number, (long)arg);
		}else if(arg instanceof Double){
			packageDouble(output, number, (double)arg);
		}else if(arg instanceof Float){
			packageFloat(output, number, (float)arg);
		}else if(arg instanceof Character){
			packageChar(output, number, (char)arg);
		}else if(arg instanceof Byte){
			packageByte(output, number, (byte)arg);
		}else if(arg instanceof Boolean){
			packageBool(output, number, (boolean)arg);
		}else if(arg instanceof String){
			packageString(output, number, (String)arg);
		}else if(arg instanceof Message){
			packageImix(output, number, (Message)arg);
		}else if(arg instanceof MetaObject){
			packageMetaObject(output, number, (MetaObject)arg);
		}
		
		else if(arg instanceof Map){
			System.out.println("Map type");
			packageMap(output, number, (Map)arg);
		}else if(arg instanceof List){
			System.out.println("List type");
			packageList(output, number, (List)arg);
		}else{
			System.out.println("Type not supported : "+arg.getClass());
		}
	}
	
	/***
	 * basic package function
	 */
	/***..ooo000OOO000ooo......ooo000OOO000ooo......ooo000OOO000ooo......ooo000OOO000ooo...***/
	
	public void packageInt(CodedOutputStream output, int number, int arg) throws IOException{
		output.writeInt32(number, arg);
		System.out.println("Integer/Short packaged...");
	}
	
	public void packageLong(CodedOutputStream output, int number, long arg) throws IOException{
		output.writeInt64(number, arg);
		System.out.println("Long packaged...");
	}
	
	public void packageDouble(CodedOutputStream output, int number, double arg) throws IOException{
		output.writeDouble(number, arg);
		System.out.println("Double packaged...");
	}
	
	public void packageFloat(CodedOutputStream output, int number, float arg) throws IOException{
		output.writeFloat(number, arg);
		System.out.println("Float packaged...");
	}
	
	public void packageChar(CodedOutputStream output, int number, char arg) throws IOException{
		output.writeInt32(number, arg);//TODO
		System.out.println("Char packaged...");
	}
	
	public void packageByte(CodedOutputStream output, int number, byte arg) throws IOException{
		output.writeInt32(number, arg);//TODO
		System.out.println("Byte packaged...");
	}
	
	public void packageBool(CodedOutputStream output, int number, boolean arg) throws IOException{
		output.writeBool(number, arg);
		System.out.println("Boolean package...");
	}
	
	public void packageString(CodedOutputStream output, int number, String arg) throws IOException{
		com.google.protobuf.GeneratedMessageV3.writeString(output, number, arg);
		System.out.println("String packaged...");
	}

	public void packageImix(CodedOutputStream output, int number, imix.imix20.Message arg) throws IOException{
		packageString(output, number, arg.toString());
		System.out.println("Imix Message packaged...");
	}
	
	public void packageMetaObject(CodedOutputStream output, int number, MetaObject arg) throws IOException, InvalidDataException{
		byte[] bytes = (byte[]) arg.serialize();
		output.writeByteArray(number, bytes);
		System.out.println("Metaobject packaged...");
	}
	/***..ooo000OOO000ooo......ooo000OOO000ooo......ooo000OOO000ooo......ooo000OOO000ooo...***/
	
	
	/***
	 * package Collection arg
	 */
	/***+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++***/
	public <K,V> void packageMap(CodedOutputStream output, int number, Map<K,V> arg) throws Exception{
		ByteBuffer subBuffer = ByteBuffer.allocate(DEFAULT_BUFFER_CAPACITY);
		for(Entry<K,V> e : arg.entrySet()){
			subBuffer = packageParameters(subBuffer, 1, e.getKey(), 2, e.getValue());
			output.writeByteArray(number, subBuffer.array(), 0, subBuffer.limit());
			subBuffer.clear();
		}
	}
	
	public <E> void packageList(CodedOutputStream output, int number, List<E> arg) throws Exception{
/*		if(arg.get(0) instanceof Integer ||
		   arg.get(0) instanceof String ||
		   arg.get(0) instanceof Long ||
		   arg.get(0) instanceof Double ||
		   arg.get(0) instanceof Float ||
		   arg.get(0) instanceof Character ||
		   arg.get(0) instanceof Byte ||
		   arg.get(0) instanceof Boolean ||
		   arg.get(0) instanceof imix.imix20.Message||
		   arg.get(0) instanceof MetaObject){

		}else */
		if(!(arg.get(0) instanceof List||arg.get(0) instanceof Map)){//如果元素"可获取长度"，直接写
			for(E e: arg){
				packageArg(output, number, e);
			}
		}else{//如果元素为List/Map，建子流，递归
			ByteBuffer subBuffer = ByteBuffer.allocate(DEFAULT_BUFFER_CAPACITY);
			for(E e: arg){
				subBuffer = packageParameters(subBuffer, 1, e);
				output.writeByteArray(number, subBuffer.array(), 0, subBuffer.limit());
				subBuffer.clear();
			}
		}
	}
	/***-----------------------------------------------------------------------------------
	 * @throws Exception ***/
	
	public Map<Integer, Object> unpackageParameters(ByteBuffer bf, Object... args) throws Exception{
		CodedInputStream input = CodedInputStream.newInstance(bf);
		return unpackageParameters(input, args);
	}
	
	public Map<Integer, Object> unpackageParameters(CodedInputStream input, Object... args) throws Exception{
		Map<Integer, Object> argsMap = new HashMap<Integer, Object>();
		
		if(args.length%2 == 1){
			throw new Exception("Wrong args amount");
		}
		
		//load all args
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
				argsMap.put(currNum, arg);
				numPos = !numPos;
			}
		}
		
		//deserialize
//		for(Entry<Integer, Object> e: argsMap.entrySet()){
//			
//		}
		Map<Integer, Object> rstMap= new HashMap<Integer, Object>();
		
		boolean done = false;
		while(!done){
			int tag = input.readTag();//wired
			if(tag == 0){
				done = true;
				break;
			}
			
			int fieldNumber = WireFormat.getTagFieldNumber(tag);
			if(argsMap.containsKey(fieldNumber)){
				Object currArg = argsMap.get(fieldNumber);
				if(currArg instanceof Map){
					if(!rstMap.containsKey(fieldNumber)){
						rstMap.put(fieldNumber, new HashMap());
					}
					unpackageMap(input, (Map)currArg, (Map)rstMap.get(fieldNumber));
				}else if(currArg instanceof List){
					if(!rstMap.containsKey(fieldNumber)){
						rstMap.put(fieldNumber, new ArrayList());
					}
					unpackageList(input, (List)currArg, (List)rstMap.get(fieldNumber));
				}else{
					rstMap.put(fieldNumber, unpackageArg(input, currArg));
				}
			}
		}
		return rstMap;
	}
	
	public Object unpackageArg(CodedInputStream input, Object arg) throws IOException{
		if(arg instanceof Integer){
			return unpackageInt(input);
		}else if(arg instanceof String){
			return unpackageString(input);
		}
//		else if(arg instanceof Map){
//			return unpackageMap(input, (Map)arg);
//		}
		return null;
	}
	
	public int unpackageInt(CodedInputStream input) throws IOException{
		int result = input.readInt32();
		System.out.println("result:"+result);
		return result;
	}
	
	public String unpackageString(CodedInputStream input) throws IOException{
		return input.readBytes().toStringUtf8();
	}
	
	/***
	 * 
	 * @param input	
	 * @param arg	map parameter, input
	 * @param rstMap	result map, output
	 * @throws Exception
	 */
	public <K, V> void unpackageMap(CodedInputStream input, Class<K> k, Class<V> v, Map<K,V> arg, Map rst) throws Exception{
		int length = input.readRawVarint32();//map类型必对应inner message，有length
		final int oldLimit = input.pushLimit(length);
//		K k=null;V v=null;
//		K.class.getName();
		Map<Integer,Object> tempMap = (Map<Integer, Object>) unpackageParameters(input, 1, k, 2, v);//TODO 默认是Map<String,String>
		
		input.popLimit(oldLimit);
		rst.put(tempMap.get(1), tempMap.get(2));
	}
	
	public <E> void unpackageList(CodedInputStream input, List<E> arg, List rst) throws Exception{
		//if E是基本类型
		rst.add(unpackageArg(input,""));//TODO 默认是List<String>
		
		//if E 是集合类型
		/*int length = input.readRawVarint32();//
		final int oldLimit = input.pushLimit(length);
		
		Map<Integer, Object> tempMap = unpackageParameters(input, 1, new ArrayList<String>());
		
		input.popLimit(oldLimit);
		rst.add(tempMap.get(1));*/
	}
	
/*public static void main(String[] args) {
	Map<Integer, String> aMap = new HashMap<Integer,String>();
	Type t = aMap.getClass().getGenericSuperclass();
	System.out.println(t);
	Type[] p = ((ParameterizedType) t).getActualTypeArguments();
	for (Type type : p) {
		System.out.println(type);
	}
	System.out.println((Class<?>)p[0]);*/
	
}

