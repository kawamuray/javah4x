package javah4x;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import javah4x.MethodInfo.Param;
import javah4x.test.TestJniClass;
import javah4x.test.with_underscore.With_Underscore;

public class MethodInfoTest {
    private final ClassInfo testClassInfo = new ClassInfo(TestJniClass.class);
    private MethodInfo getIntInfo;
    private MethodInfo doNothingInfo;
    private final ClassInfo withUnderscoreClassInfo = new ClassInfo(With_Underscore.class);
    private MethodInfo withUnderscore1Info;
    private MethodInfo withUnderscore2Info;

    @Before
    public void setUp() {
        Collection<MethodInfo> methods = testClassInfo.methods();
        getIntInfo = methods.stream().filter(m -> "getInt".equals(m.name())).findFirst().get();
        doNothingInfo = methods.stream().filter(m -> "doNothing".equals(m.name())).findFirst().get();
        List<MethodInfo> underscoreInfos = withUnderscoreClassInfo.methods().stream()
                .filter(m -> "with_underscore".equals(m.name()))
                .collect(Collectors.toList());
        withUnderscore1Info = underscoreInfos.get(0);
        withUnderscore2Info = underscoreInfos.get(1);
    }

    @Test
    public void name() {
        assertEquals("getInt", getIntInfo.name());
        assertEquals("doNothing", doNothingInfo.name());
    }

    @Test
    public void jniFuncName() {
        assertEquals("Java_javah4x_test_TestJniClass_getInt", getIntInfo.jniFuncName());
        assertEquals("Java_javah4x_test_TestJniClass_doNothing__Ljava_lang_String_2I", doNothingInfo.jniFuncName());
        assertEquals("Java_javah4x_test_with_1underscore_With_1Underscore_with_1underscore__I", withUnderscore1Info.jniFuncName());
        assertEquals("Java_javah4x_test_with_1underscore_With_1Underscore_with_1underscore__I_3Ljava_lang_String_2", withUnderscore2Info.jniFuncName());
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
