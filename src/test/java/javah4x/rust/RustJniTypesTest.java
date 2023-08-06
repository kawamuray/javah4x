package javah4x.rust;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import javah4x.JniType;

public class RustJniTypesTest {
    @Test
    public void rustJniParamType() {
        assertEquals("jboolean", RustJniTypes.rustJniParamType(JniType.BOOLEAN, false));
        assertEquals("JObject", RustJniTypes.rustJniParamType(JniType.OBJECT, false));
        assertEquals("JNIEnv", RustJniTypes.rustJniParamType(JniType.JNI_ENV, false));
        assertEquals("&mut JNIEnv<'a>", RustJniTypes.rustJniParamType(JniType.JNI_ENV, true));
    }

    @Test
    public void rustJniType() {
        assertEquals("()", RustJniTypes.rustJniType(JniType.VOID));
        assertEquals("jint", RustJniTypes.rustJniType(JniType.INT));
        assertEquals("jintArray", RustJniTypes.rustJniType(JniType.INT_ARRAY));
        assertEquals("jobject", RustJniTypes.rustJniType(JniType.OBJECT));
    }

    @Test
    public void fnReturnSign() {
        assertEquals("", RustJniTypes.fnReturnSign(JniType.VOID));
        assertEquals(" -> jboolean", RustJniTypes.fnReturnSign(JniType.BOOLEAN));
        assertEquals(" -> jobject", RustJniTypes.fnReturnSign(JniType.OBJECT));
    }

    @Test
    public void defaultValue() {
        assertEquals("Default::default()", RustJniTypes.defaultValue(JniType.VOID));
        assertEquals("Default::default()", RustJniTypes.defaultValue(JniType.BOOLEAN));
        assertEquals("JObject::null().into_raw()", RustJniTypes.defaultValue(JniType.STRING));
        assertEquals("JObject::null().into_raw()", RustJniTypes.defaultValue(JniType.OBJECT));
    }
}
