package javah4x;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import javah4x.MethodInfo.Param;
import javah4x.test.TestJniClass;

public class MethodInfoTest {
    private final ClassInfo classInfo = new ClassInfo(TestJniClass.class);
    private MethodInfo getIntInfo;
    private MethodInfo doNothingInfo;

    @Before
    public void setUp() {
        Collection<MethodInfo> methods = classInfo.methods();
        getIntInfo = methods.stream().filter(m -> "getInt".equals(m.name())).findFirst().get();
        doNothingInfo = methods.stream().filter(m -> "doNothing".equals(m.name())).findFirst().get();
    }

    @Test
    public void name() {
        assertEquals("getInt", getIntInfo.name());
        assertEquals("doNothing", doNothingInfo.name());
    }

    @Test
    public void jniFuncName() {
        assertEquals("Java_javah4x_test_TestJniClass_getInt", getIntInfo.jniFuncName());
        assertEquals("Java_javah4x_test_TestJniClass_doNothing", doNothingInfo.jniFuncName());
    }

    @Test
    public void originalParams() {
        assertEquals(emptyList(), getIntInfo.originalParams());
        List<Param<JavaType>> expected = Arrays.asList(
                new Param<>("name", JavaType.fromClass(String.class)),
                new Param<>("x", JavaType.fromClass(int.class)));
        Collection<Param<JavaType>> params = doNothingInfo.originalParams();
        assertEquals(expected, params);
    }

    @Test
    public void params() {
        Collection<Param<JniType>> params = getIntInfo.params();
        List<Param<JniType>> expected = Arrays.asList(
                new Param<>("env", JniType.JNI_ENV),
                new Param<>("this", JniType.OBJECT));
        assertEquals(expected, params);
        expected = Arrays.asList(
                new Param<>("env", JniType.JNI_ENV),
                new Param<>("clazz", JniType.CLASS),
                new Param<>("name", JniType.STRING),
                new Param<>("x", JniType.INT));
        params = doNothingInfo.params();
        assertEquals(expected, params);
    }

    @Test
    public void retType() {
        assertEquals(JavaType.fromClass(int.class), getIntInfo.retType());
        assertEquals(JavaType.fromClass(void.class), doNothingInfo.retType());
    }
}
