package info.xonix.zlo.search.test.tmp1;

public class B extends A {
    @Override
    public void m() {
        System.out.println("B method");
    }

    public static void main(String[] args) {
        A b = new B();
        b.m();
    }
}

class A {
    public void m() {
        System.out.println("A method");
    }
}
