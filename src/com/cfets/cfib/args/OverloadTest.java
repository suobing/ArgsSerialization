package com.cfets.cfib.args;

public class OverloadTest {
	public static void main(String[] args) {
		Animal obj = new Dog();
		new OverloadTest().foo(obj);
	}
	
	public void foo(int a ){System.out.println("int");}
	public void foo(String a){System.out.println("String");}
	public void foo(Animal a){System.out.println("Animal");a.say();}
	public void foo(Dog a){System.out.println("Dog");}
}

class Animal{
	void say(){System.out.println("Animal say");}
}
class Dog extends Animal{
	void say(){System.out.println("Dog say");}
}
