package javah4x.test;

public class TestJniClass {
    public void foo() {}

    public native int getInt();

    private static native void doNothing(String name, int x);
}
