package javah4x;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * An interface that represents information about a type in Java.
 */
public interface JavaType {
    /**
     * Obtain an instance of {@link JavaType} from the given {@link Class}.
     * @param type a {@link Class} instance.
     * @return a {@link JavaType} instance.
     */
    static JavaType fromClass(Class<?> type) {
        if (type == void.class) {
            return Primitive.VOID;
        }
        if (type == boolean.class) {
            return Primitive.BOOLEAN;
        }
        if (type == byte.class) {
            return Primitive.BYTE;
        }
        if (type == char.class) {
            return Primitive.CHAR;
        }
        if (type == short.class) {
            return Primitive.SHORT;
        }
        if (type == int.class) {
            return Primitive.INT;
        }
        if (type == long.class) {
            return Primitive.LONG;
        }
        if (type == float.class) {
            return Primitive.FLOAT;
        }
        if (type == double.class) {
            return Primitive.DOUBLE;
        }
        if (type.isArray()) {
            JavaType innerType = fromClass(type.getComponentType());
            return new JArray(innerType);
        }
        return new JObject(type);
    }

    JniType jniType();

    String jniTypeSign();

    /**
     * Primitive types.
     */
    enum Primitive implements JavaType {
        VOID,
        BOOLEAN,
        BYTE,
        CHAR,
        SHORT,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        ;

        @Override
        public JniType jniType() {
            switch (this) {
                case VOID:
                    return JniType.VOID;
                case BOOLEAN:
                    return JniType.BOOLEAN;
                case BYTE:
                    return JniType.BYTE;
                case CHAR:
                    return JniType.CHAR;
                case SHORT:
                    return JniType.SHORT;
                case INT:
                    return JniType.INT;
                case LONG:
                    return JniType.LONG;
                case FLOAT:
                    return JniType.FLOAT;
                case DOUBLE:
                    return JniType.DOUBLE;
                default:
                    throw new RuntimeException("never happens");
            }
        }

        @Override
        public String jniTypeSign() {
            switch (this) {
                case VOID:
                    return "V";
                case BOOLEAN:
                    return "Z";
                case BYTE:
                    return "B";
                case CHAR:
                    return "C";
                case SHORT:
                    return "S";
                case INT:
                    return "I";
                case LONG:
                    return "J";
                case FLOAT:
                    return "F";
                case DOUBLE:
                    return "D";
                default:
                    throw new RuntimeException("never happens");
            }
        }
    }

    /**
     * Array types.
     */
    @EqualsAndHashCode
    class JArray implements JavaType {
        final JavaType innerType;

        public JArray(JavaType innerType) {
            this.innerType = innerType;
        }

        @Override
        public JniType jniType() {
            if (innerType instanceof Primitive) {
                switch ((Primitive) innerType) {
                    case BOOLEAN:
                        return JniType.BOOLEAN_ARRAY;
                    case BYTE:
                        return JniType.BYTE_ARRAY;
                    case CHAR:
                        return JniType.CHAR_ARRAY;
                    case SHORT:
                        return JniType.SHORT_ARRAY;
                    case INT:
                        return JniType.INT_ARRAY;
                    case LONG:
                        return JniType.LONG_ARRAY;
                    case FLOAT:
                        return JniType.FLOAT_ARRAY;
                    case DOUBLE:
                        return JniType.DOUBLE_ARRAY;
                }
            }
            return JniType.OBJECT_ARRAY;
        }

        @Override
        public String jniTypeSign() {
            return StringUtils.mangle("[") + innerType.jniTypeSign();
        }
    }

    /**
     * Any types other than the above specialized ones.
     */
    @Accessors(fluent = true)
    @EqualsAndHashCode
    class JObject implements JavaType {
        @Getter
        private final Class<?> type;

        public JObject(Class<?> type) {
            this.type = type;
        }

        @Override
        public JniType jniType() {
            if (type == Class.class) {
                return JniType.CLASS;
            }
            if (type == String.class) {
                return JniType.STRING;
            }
            return JniType.OBJECT;
        }

        @Override
        public String jniTypeSign() {
            return StringUtils.mangle('L' + type.getName() + ';');
        }
    }
}

