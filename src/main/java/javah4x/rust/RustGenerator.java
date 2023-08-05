package javah4x.rust;

import static java.util.stream.Collectors.toList;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

import javah4x.ClassInfo;
import javah4x.CodeGenerator;
import javah4x.JniType;
import javah4x.MethodInfo;
import javah4x.MethodInfo.Param;
import javah4x.StringUtils;

/**
 * A {@link CodeGenerator} implementation that generates JNI code for Rust language with
 * the <a href="https://github.com/jni-rs/jni-rs">jni crate</a>.
 *
 * Given the Java class {@code com.example.Foo}, this generator generates the Rust module
 * {@code com_example_Foo} and puts the trait named {@code JniFoo} in it, with the exported (C compatible)
 * functions that follows the naming convention of JNI.
 * The implementor of the JNI bindings can add a submodule named {@code imp} under the above generated module,
 * and add the implementation for {@code JniFoo} with the name {@code JniFooImpl} such as follows:
 * {@code
 * struct JniFooImpl;
 * impl<'a> super::JniFoo<'a> for JniFooImpl {
 * ...
 * }
 */
public class RustGenerator implements CodeGenerator {
    public static final String IMPL_MOD_NAME = "imp";

    private final boolean applyRustFmt;

    RustGenerator(boolean applyRustFmt) {
        this.applyRustFmt = applyRustFmt;
    }

    public RustGenerator() {
        this(true);
    }

    @Override
    public void generateClass(Path baseDir, ClassInfo classInfo) throws IOException {
        String modName = classInfo.fullyQualifiedNameDelimitedWith("_");
        Path modDir = baseDir.resolve(modName);
        Files.createDirectories(modDir);

        Path modFile = modDir.resolve("mod.rs");
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(modFile.toFile()))) {
            String implTypeName = String.format("Jni%sImpl", classInfo.name());

            pw.println("// THIS FILE IS GENERATED AUTOMATICALLY. DO NOT EDIT!");
            pw.printf("mod %s;\n", IMPL_MOD_NAME);
            pw.println("");
            pw.println("use jni::descriptors::Desc;");
            pw.println("use jni::objects::*;");
            pw.println("use jni::sys::*;");
            pw.println("use jni::JNIEnv;");
            pw.printf("use self::%s::%s;\n", IMPL_MOD_NAME, implTypeName);
            pw.println("");
            pw.println("macro_rules! wrap_error {\n"
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
                       + "}\n");

            pw.printf("trait Jni%s<'a> {\n", classInfo.name());
            pw.println("    type Error: Desc<'a, JThrowable<'a>>;");
            for (MethodInfo method : classInfo.methods()) {
                pw.printf("    fn %s(%s) -> Result<%s, Self::Error>;\n",
                          StringUtils.toSnakeCase(method.name()),
                          toRustParams(method.params(), true),
                          RustJniTypes.rustJniType(method.retType().jniType()));
            }
            pw.println("}");
            pw.println("");

            for (MethodInfo method : classInfo.methods()) {
                pw.println("#[no_mangle]");
                pw.printf("extern \"system\" fn %s(%s)%s {\n",
                          method.jniFuncName(), toRustParams(method.params(), false),
                          RustJniTypes.fnReturnSign(method.retType().jniType()));
                pw.printf("    wrap_error!(env, %s::%s(&mut env, %s), %s)\n",
                          implTypeName, StringUtils.toSnakeCase(method.name()),
                          toRustParamNames(method.params().stream().skip(1).collect(toList())),
                          RustJniTypes.defaultValue(method.retType().jniType()));
                pw.println("}");
                pw.println("");
            }
        }

        maybeApplyRustfmt(modFile);
    }

    private static String toRustParamNames(Collection<Param<?>> params) {
        return params.stream()
                     .map(param -> StringUtils.toSnakeCase(param.name()))
                     .collect(Collectors.joining(", "));
    }

    private static String toRustParams(Collection<Param<JniType>> params, boolean trait) {
        return params.stream()
                     .map(param -> String.format("%s: %s",
                                                 StringUtils.toSnakeCase(param.name()),
                                                 RustJniTypes.rustJniParamType(param.type(), trait)))
                     .collect(Collectors.joining(", "));
    }

    void maybeApplyRustfmt(Path path) {
        if (!applyRustFmt) {
            return;
        }
        try {
            System.err.println("Applying rustfmt for " + path);
            Process proc = Runtime.getRuntime().exec(new String[] { "rustfmt", path.toString() });
            int status = proc.waitFor();
            if (status != 0) {
                throw new RuntimeException("rustfmt exit with error: " + status);
            }
        } catch (InterruptedException | IOException e) {
            System.out.println("rustfmt isn't available in PATH, skipping apply.");
        }
    }
}
