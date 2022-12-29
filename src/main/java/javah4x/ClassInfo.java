package javah4x;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * A wrapper around {@link Class} to let {@link CodeGenerator} access interested information easier.
 */
@Accessors(fluent = true)
@AllArgsConstructor
@EqualsAndHashCode
public class ClassInfo {
	@Getter
	private final Class<?> clazz;

	/**
	 * Returns the fully-qualified name of the class with replacing delimiter with the given string.
	 * E.g, for the class com.example.Foo with the argument "_", "com_example_Foo" will be returned.
	 * @param delim the delimiter.
	 * @return fully-qualified name with the given delimiter.
	 */
	public String fullyQualifiedNameDelimitedWith(String delim) {
		return clazz.getName().replace(".", delim);
	}

	public String fqn() {
		return clazz.getName();
	}

	/**
	 * Returns the name of this class.
	 * @return name of this class.
	 */
	public String name() {
		return clazz.getSimpleName();
	}

	Stream<Method> declaredNativeMethods() {
		return Arrays.stream(clazz.getDeclaredMethods())
					 .filter(method -> Modifier.isNative(method.getModifiers()));
	}

	/**
	 * Returns the list of native methods that needs to be implemented in JNI.
	 * @return the list of {@link MethodInfo} that represents native methods.
	 */
	public Collection<MethodInfo> methods() {
		return declaredNativeMethods()
					 .sorted(Comparator.comparing(Method::getName))
					 .map(m -> new MethodInfo(this, m)).collect(toList());
	}
}
