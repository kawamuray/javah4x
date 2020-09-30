package javah4x;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javah4x.rust.RustGenerator;

public final class Javah4x {
    public static void main(String[] args) throws ClassNotFoundException, IOException {
        if (args.length < 3) {
            System.err.println("Usage: javah4x LANG|CLASS OUTPUT_DIR TARGET_CLASS1[ TARGET_CLASS2...]");
            System.exit(1);
        }

        CodeGenerator generator = createGenerator(args[0]);
        Path outputDir = Paths.get(args[1]);
        for (int i = 2; i < args.length; i++) {
            Class<?> clazz = Class.forName(args[i]);
            ClassInfo classInfo = new ClassInfo(clazz);
            generator.generateClass(outputDir, classInfo);
        }
    }

    private static CodeGenerator createGenerator(String spec) {
        Class<? extends CodeGenerator> clazz = mapToLangGeneratorClass(spec).orElseGet(() -> {
            try {
                return Class.forName(spec).asSubclass(CodeGenerator.class);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        });
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Optional<Class<? extends CodeGenerator>> mapToLangGeneratorClass(String spec) {
        switch (spec) {
            case "rust":
                return Optional.of(RustGenerator.class.asSubclass(CodeGenerator.class));
            default:
                return Optional.empty();
        }
    }
}
