package javah4x;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import javah4x.test.TestJniClass;

public class ClassInfoTest {
    private final ClassInfo info = new ClassInfo(TestJniClass.class);

    @Test
    public void fullyQualifiedNameDelimitedWith() {
        assertEquals("javah4x_test_TestJniClass", info.fullyQualifiedNameDelimitedWith("_"));
    }

    @Test
    public void name() {
        assertEquals("TestJniClass", info.name());
    }

    @Test
    public void methods() {
        List<String> expected = Arrays.asList("getInt", "doNothing");
        Collections.sort(expected);
        List<String> methods = info.methods().stream().map(MethodInfo::name).collect(toList());
        assertEquals(expected, methods);
    }
}
