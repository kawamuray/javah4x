package javah4x;

/**
 * JNI types.
 * The actual mapping from below types to types in JNI implementation varies by programming languages and
 * libraries to use.
 */
public enum JniType {
    JNI_ENV,
    VOID,
    BOOLEAN,
    BYTE,
    CHAR,
    SHORT,
    INT,
    LONG,
    FLOAT,
    DOUBLE,
    BOOLEAN_ARRAY,
    BYTE_ARRAY,
    CHAR_ARRAY,
    SHORT_ARRAY,
    INT_ARRAY,
    LONG_ARRAY,
    FLOAT_ARRAY,
    DOUBLE_ARRAY,
    OBJECT_ARRAY,
    CLASS,
    STRING,
    OBJECT,
}
