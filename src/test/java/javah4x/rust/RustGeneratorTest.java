package javah4x.rust;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javah4x.ClassInfo;
import javah4x.test.TestJniClass;

public class RustGeneratorTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void generateClass() throws IOException {
        Path root = folder.getRoot().toPath();
        ClassInfo classInfo = new ClassInfo(TestJniClass.class);
        RustGenerator generator = new RustGenerator(false);
        generator.generateClass(root, classInfo);

        Path modFile = root.resolve(Paths.get("javah4x_test_TestJniClass", "mod.rs"));
        assertTrue(String.format("%s exists", modFile), Files.exists(modFile));
        String rustCode = new String(Files.readAllBytes(modFile));
        assertEquals("// THIS FILE IS GENERATED AUTOMATICALLY. DO NOT EDIT!\n"
                     + "mod imp;\n"
                     + '\n'
                     + "use jni::descriptors::Desc;\n"
                     + "use jni::objects::*;\n"
                     + "use jni::sys::*;\n"
                     + "use jni::JNIEnv;\n"
                     + "use self::imp::JniTestJniClassImpl;\n"
                     + '\n'
                     + "macro_rules! wrap_error {\n"
                     + "    ($env:expr, $body:expr, $default:expr) => {\n"
                     + "        match $body {\n"
                     + "            Ok(v) => v,\n"
                     + "            Err(e) => {\n"
                     + "                if let Err(err) = $env.throw(e) {\n"
                     + "                    $env.exception_describe().ok();\n"
                     + "                    panic!(\"error in throwing exception: {}\", err);\n"
                     + "                }\n"
                     + "                $default\n"
                     + "            }\n"
                     + "        }\n"
                     + "    };\n"
                     + "}\n"
                     + '\n'
                     + "trait JniTestJniClass<'a> {\n"
                     + "    type Error: Desc<'a, JThrowable<'a>>;\n"
                     + "    fn do_nothing(env: &mut JNIEnv, clazz: JClass, name: JString, x: jint) -> Result<(), Self::Error>;\n"
                     + "    fn get_int(env: &mut JNIEnv, this: JObject) -> Result<jint, Self::Error>;\n"
                     + "}\n"
                     + '\n'
                     + "#[no_mangle]\n"
                     + "extern \"system\" fn Java_javah4x_test_TestJniClass_doNothing__Ljava_lang_String_2I(mut env: JNIEnv, clazz: JClass, name: JString, x: jint) {\n"
                     + "    wrap_error!(env, JniTestJniClassImpl::do_nothing(&mut env, clazz, name, x), Default::default())\n"
                     + "}\n"
                     + '\n'
                     + "#[no_mangle]\n"
                     + "extern \"system\" fn Java_javah4x_test_TestJniClass_getInt(mut env: JNIEnv, this: JObject) -> jint {\n"
                     + "    wrap_error!(env, JniTestJniClassImpl::get_int(&mut env, this), Default::default())\n"
                     + "}\n\n", rustCode);
    }
}
