package top.outlands.nonupdate;

import gblodb.nonupdate.NonUpdateTweaker;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;

public class URLTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!Arrays.stream(NonUpdateTweaker.config.targets).toList().contains(name)) {
            return basicClass;
        } else {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(basicClass);
            classReader.accept(classNode, 0);
            if (classNode.methods != null) {
                for (MethodNode methodNode : classNode.methods) {
                    if (methodNode.instructions != null) {
                        for (AbstractInsnNode abstractInsnNode : methodNode.instructions) {
                            if (abstractInsnNode instanceof MethodInsnNode methodInsnNode) {
                                if (methodInsnNode.owner.equals("java/net/URL") && methodInsnNode.name.equals("openConnection") && methodInsnNode.desc.equals("()Ljava/net/URLConnection;")) {
                                    methodInsnNode.owner = "gblodb/nonupdate/NonUpdateTweaker";
                                    methodInsnNode.desc = "(Ljava/net/URL;)Ljava/net/URLConnection;";
                                    methodInsnNode.setOpcode(Opcodes.INVOKESTATIC);
                                }
                                if (methodInsnNode.owner.equals("java/net/URL") && methodInsnNode.name.equals("openStream") && methodInsnNode.desc.equals("()Ljava/io/InputStream;")) {
                                    methodInsnNode.owner = "gblodb/nonupdate/NonUpdateTweaker";
                                    methodInsnNode.desc = "(Ljava/net/URL;)Ljava/io/InputStream;";
                                    methodInsnNode.setOpcode(Opcodes.INVOKESTATIC);
                                }
                            }
                        }
                    }
                }
            }
            ClassWriter writer = new ClassWriter(1);
            classNode.accept(writer);
            return writer.toByteArray();
        }
    }
}
