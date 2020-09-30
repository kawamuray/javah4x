package javah4x;

import java.io.IOException;
import java.nio.file.Path;

import javah4x.rust.RustGenerator;

/**
 * The code generator interface.
 * Each language/library combination may have different implementation of this interface.
 */
public interface CodeGenerator {
    /**
     * Generate JNI bindings interface file for the given class information.
     * See {@link RustGenerator} for the reference implementation.
     * The generated directory structure and file contents is different among programming languages and
     * libraries.
     * @param baseDir the base directory to create files.
     * @param classInfo a {@link ClassInfo} instance that describes the target class.
     * @throws IOException when there is an issue with filesystem IO.
     */
    void generateClass(Path baseDir, ClassInfo classInfo) throws IOException;
}
