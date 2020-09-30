package javah4x;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class JavaTypeTest {

    @Test
    public void primitive() {
        assertEquals(JniType.VOID, JavaType.fromClass(void.class).jniType());
        assertEquals(JniType.INT, JavaType.fromClass(int.class).jniType());
        assertEquals(JniType.DOUBLE, JavaType.fromClass(double.class).jniType());
    }

    @Test
    public void array() {
        assertEquals(JniType.INT_ARRAY, JavaType.fromClass(int[].class).jniType());
        assertEquals(JniType.FLOAT_ARRAY, JavaType.fromClass(float[].class).jniType());
        assertEquals(JniType.OBJECT_ARRAY, JavaType.fromClass(String[].class).jniType());
    }

    @Test
    public void object() {
        assertEquals(JniType.OBJECT, JavaType.fromClass(JavaTypeTest.class).jniType());
        assertEquals(JniType.STRING, JavaType.fromClass(String.class).jniType());
        assertEquals(JniType.CLASS, JavaType.fromClass(Class.class).jniType());
    }
}
