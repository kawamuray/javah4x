package javah4x;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * A wrapper around {@link Method} to let {@link CodeGenerator} access interested information easier.
 */
@Accessors(fluent = true)
@AllArgsConstructor
@EqualsAndHashCode
public class MethodInfo {
    /**
     * A class that represents one parameter (argument) of the method, by its name and type.
     */
    @Value
    public static class Param<T> {
        String name;
        T type;
    }

    @Getter
    private final ClassInfo classInfo;
    @Getter
    private final Method method;

    /**
     * Returns the name of this method.
     * @return name of this method.
     */
    public String name() {
        return method.getName();
    }

    /**
     * Returns the name of the function that needs to be implemented in JNI module.
     * The name is compatible with one that "javah" program generates.
     * @return the name of function to implement in JNI module.
     */
    public String jniFuncName() {
        return String.format("Java_%s_%s",
                             classInfo.fullyQualifiedNameDelimitedWith("_"), name());
    }

    /**
     * Returns the list of {@link Param} that represents the original parameters list of this method.
     * Note that the actual JNI function implementation needs to accept few more arguments at the head of the
     * list. To obtain the parameters list of the JNI function to implement, use {@link #params()} instead.
     * @return list of {@link Param}.
     */
    public Collection<Param<JavaType>> originalParams() {
        List<Param<JavaType>> params = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            JavaType type = JavaType.fromClass(parameter.getType());
            Param<JavaType> param = new Param<>(parameter.getName(), type);
            params.add(param);
        }
        return params;
    }

    /**
     * Returns the list of {@link Param} that represents the parameters list of the JNI function to be
     * implemented.
     * The list basically consists of the arguments of the method headed by {@link JniType#JNI_ENV} and "this"
     * object or "class" depending on the method's modifier.
     * @return list of {@link Param}.
     */
    public Collection<Param<JniType>> params() {
        List<Param<JniType>> params = new ArrayList<>();
        params.add(new Param<>("env", JniType.JNI_ENV));
        if (Modifier.isStatic(method.getModifiers())) {
            params.add(new Param<>("clazz", JniType.CLASS));
        } else {
            params.add(new Param<>("this", JniType.OBJECT));
        }
        for (Param<JavaType> p : originalParams()) {
            params.add(new Param<>(p.name(), p.type().jniType()));
        }
        return params;
    }

    /**
     * Returns the type of this method's return type.
     * If the method has no return type (void method), still the non-null {@link JavaType} is returned.
     * @return a {@link JavaType}.
     */
    public JavaType retType() {
        return JavaType.fromClass(method.getReturnType());
    }

}
