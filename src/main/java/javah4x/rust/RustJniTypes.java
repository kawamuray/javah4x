package javah4x.rust;

import javah4x.JniType;

public final class RustJniTypes {
    private RustJniTypes() {}

    /**
     * Returns the type signature of Rust jni crate that corresponds to the given {@link JniType}.
     * This method returns the type which has a lifetime parameter where possible, so for {@link JniType#OBJECT}
     * "JObject" is returned instead of "jobject".
     * @param type the JNI type.
     * @param trait determine whether to return the trait type or the value type for
     * {@link JniType#JNI_ENV}.
     * @return the type of Rust's jni crate.
     */
    public static String rustJniParamType(JniType type, boolean trait) {
        String ty = null;
        switch (type) {
            case CLASS:
                ty = "JClass";
                break;
            case STRING:
                ty = "JString";
                break;
            case OBJECT:
                ty = "JObject";
                break;
            case JNI_ENV:
                if (trait) {
                    ty = "&mut JNIEnv";
                } else {
                    ty = "JNIEnv";
                }
                break;
        }
        if (ty != null) {
            if (trait) {
                ty += "<'a>";
            }
            return ty;
        }
        return rustJniType(type);
    }

    /**
     * Returns the type signature of Rust jni crate that corresponds to the given {@link JniType}.
     * Rust's jni crate has two possible types for some non-primitive types, such as "jclass" and "JClass" which
     * differs in their lifetime consideration.
     * To obtain the type that is used for function's arguments, use
     * {@link #rustJniParamType(JniType, boolean)} instead.
     * @param type the JNI type.
     * @return the type of Rust's jni crate.
     */
    public static String rustJniType(JniType type) {
        switch (type) {
            case JNI_ENV:
                return "JNIEnv";
            case VOID:
                return "()";
            case BOOLEAN:
                return "jboolean";
            case BYTE:
                return "jbyte";
            case CHAR:
                return "jchar";
            case SHORT:
                return "jshort";
            case INT:
                return "jint";
            case LONG:
                return "jlong";
            case FLOAT:
                return "jfloat";
            case DOUBLE:
                return "jdouble";
            case BOOLEAN_ARRAY:
                return "jbooleanArray";
            case BYTE_ARRAY:
                return "jbyteArray";
            case CHAR_ARRAY:
                return "jcharArray";
            case SHORT_ARRAY:
                return "jshortArray";
            case INT_ARRAY:
                return "jintArray";
            case LONG_ARRAY:
                return "jlongArray";
            case FLOAT_ARRAY:
                return "jfloatArray";
            case DOUBLE_ARRAY:
                return "jdoubleArray";
            case OBJECT_ARRAY:
                return "jobjectArray";
            case CLASS:
                return "jclass";
            case STRING:
                return "jstring";
            case OBJECT:
                return "jobject";
            default:
                throw new RuntimeException("BUG: all JNI types are supposed to have type mapping");
        }
    }

    /**
     * Returns the function return signature that can be embedded into the last part of the function declaration
     * header.
     * E.g, for the type {@link JniType#INT}, "-> jint" is returned, and for the type {@link JniType#VOID} empty
     * string is returned.
     * @param retType type of the function's return value.
     * @return Rust function's return signature.
     */
    public static String fnReturnSign(JniType retType) {
        if (retType == JniType.VOID) {
            return "";
        }
        return " -> " + rustJniType(retType);
    }

    /**
     * Returns the Rust expression to obtain the type's appropriate "default" value in the context it is being
     * required.
     * @param type the Rust type to build an default value expression.
     * @return Rust expression to obtsain the type's default value.
     */
    public static String defaultValue(JniType type) {
        switch (type) {
            case JNI_ENV:
                throw new IllegalArgumentException("no possible default value for JNI type: " + type);
            case VOID:
            case BOOLEAN:
            case BYTE:
            case CHAR:
            case SHORT:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
                return "Default::default()";
            default:
                return "JObject::null().into_raw()";
        }
    }
}
