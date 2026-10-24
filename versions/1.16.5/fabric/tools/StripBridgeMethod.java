import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public final class StripBridgeMethod {

    public static void main(String[] args) throws Exception {
        if (args.length < 5) {
            System.err.println("usage: StripBridgeMethod <in.jar> <out.jar> <class/Path> <method> <desc>");
            System.exit(2);
        }
        final Path in = Paths.get(args[0]);
        final Path out = Paths.get(args[1]);
        final String className = args[2].replace('.', '/');
        final String methodName = args[3];
        final String methodDesc = args[4];

        int[] stripped = {0};
        try (ZipFile zip = new ZipFile(in.toFile());
             ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(out))) {
            final Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();
                byte[] data = readAll(zip.getInputStream(entry));
                if (entry.getName().equals(className + ".class")) {
                    data = strip(data, methodName, methodDesc, stripped);
                }
                final ZipEntry copy = new ZipEntry(entry.getName());
                copy.setTime(entry.getTime());
                zos.putNextEntry(copy);
                zos.write(data);
                zos.closeEntry();
            }
        }
        System.out.println("stripped " + stripped[0] + " method(s) from " + className
                + " -> " + out);
        if (stripped[0] == 0) {
            System.err.println("WARNING: nothing was removed");
            System.exit(1);
        }
    }

    private static byte[] strip(byte[] input, String name, String desc, int[] counter) {
        final ClassReader reader = new ClassReader(input);
        final ClassWriter writer = new ClassWriter(0);
        final ClassVisitor visitor = new ClassVisitor(Opcodes.ASM9, writer) {
            @Override
            public MethodVisitor visitMethod(int access, String methodName, String methodDesc,
                                             String signature, String[] exceptions) {
                if (methodName.equals(name) && methodDesc.equals(desc)) {
                    counter[0]++;
                    return null;
                }
                return super.visitMethod(access, methodName, methodDesc, signature, exceptions);
            }
        };
        reader.accept(visitor, 0);
        return writer.toByteArray();
    }

    private static byte[] readAll(InputStream stream) throws IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final byte[] chunk = new byte[8192];
        int read;
        while ((read = stream.read(chunk)) > 0) {
            buffer.write(chunk, 0, read);
        }
        return buffer.toByteArray();
    }
}
