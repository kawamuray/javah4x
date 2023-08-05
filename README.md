javah4x
=======

Extensible javah (JNI header file generation) for any programming language.

We can use not only C but other programming languages to write Java Native Interface (JNI).
One of the most practical example is to write it in [Rust](https://www.rust-lang.org/) with [jni crate](https://github.com/jni-rs/jni-rs).
However, JDK's `javah` command currently supports generating only C header files. This makes us need to add declaration of exported functions in conventional naming and signature with carefully crafting them from definitions in Java class.
For example, given the below Java class:

```java
package javah4x.test;
public class TestClass {
    private static native void doNothing(String name, int x);
    public native int getInt();
```

we have to add following function declarations to our Rust code:

```rust
#[no_mangle]
extern "system" fn Java_javah4x_test_TestClass_doNothing(env: JNIEnv, clazz: JClass, name: JString, x: jint) { ... }
#[no_mangle]
extern "system" fn Java_javah4x_test_TestClass_getInt(env: JNIEnv, this: JObject) -> jint { ... }
```

It works well when there's a few classes and methods to write JNI bindings, but it becomes error prone as the number of classes and methods increase, especially when making change in method signature.

`javah4x` aims to provide a framework for writing a programming language-specific `javah` with minimal effort.

Currently `javah4x` supports Rust language only.

# Usage - Rust

For writing JNI bindings in Rust, you can first execute the following command to generate modules with traits from Java class file.

```sh
$ CLASSPATH=/path/to/your.jar javah4x rust ./src javah4x.test.TestClass
```

This command generates a module `javah4x_test_TestClass` in `./src` directory with the following trait declaration, along with the exported methods that uses the expected implementation of the trait functions.

```rust
trait JniTestClass<'a> {
    type Error: Desc<'a, JThrowable<'a>>;
    fn do_nothing(env: &mut JNIEnv, clazz: JClass, name: JString, x: jint) -> Result<(), Self::Error>;
    fn get_int(env: &mut JNIEnv, this: JObject) -> Result<jint, Self::Error>;
}

#[no_mangle]
extern "system" fn Java_javah4x_test_TestClass_doNothing(env: JNIEnv, clazz: JClass, name: JString, x: jint) {
    wrap_error!(env, JniTestClassImpl::do_nothing(&env, clazz, name, x), Default::default())
}
```

You can then add a submodule named `imp` (`javah4x_test_TestClass/imp.rs`) under the above module, with the trait implementation as follows:

```rust
struct JniTestClassImpl;
impl<'a> JniTestClass<'a> for JniTestClassImpl {
    type Error: Desc<'a, JThrowable<'a>> = MyErrorType;
    fn do_nothing(env: &mut JNIEnv, clazz: JClass, name: JString, x: jint) -> Result<(), Self::Error> {
        // doing nothing...
    }
    fn get_int(env: &mut JNIEnv, this: JObject) -> Result<jint, Self::Error> { ... }
}
```

This way benefits you in following ways compared to doing it manually and naively.

* By implementing auto-generated trait that contains up-to-date native methods signature, you can warned by compiler when you forget to update your JNI implementation after changing Java class.
* Because the trait functions returns `Result<..., Self::Error>`, you can use `?` operator in their function body which is not possible to put directly in exported function's body due to its predefined return type.
* The generated exported functions takes care of the return value when your trait function returns error (which then converted into an exception), so you don't have to be bothered by making sure to return the default value which varies by return type knowing it won't be used.


# License

Apache License Version 2.0
