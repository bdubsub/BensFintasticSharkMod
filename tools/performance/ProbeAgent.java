package bfs.verification;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/** Observes the packaged server without replacing its mod artifact. */
public final class ProbeAgent {
    public static void premain(String options, Instrumentation instrumentation) {
        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String name, Class<?> type,
                                    ProtectionDomain domain, byte[] bytes) {
                boolean server = name.equals("net/minecraft/server/MinecraftServer");
                boolean navigation = name.equals("net/minecraft/world/entity/ai/navigation/PathNavigation");
                boolean sensing = name.startsWith("net/minecraft/world/entity/ai/sensing/")
                        || name.startsWith("net/tslat/smartbrainlib/api/core/sensor/");
                boolean policy = name.equals("tfar/bensfintasticsharks/entity/SpeciesBehaviorEngine");
                boolean animal = name.equals("tfar/bensfintasticsharks/entity/SmartWaterAnimal");
                boolean living = name.equals("net/minecraft/world/entity/LivingEntity");
                if (!(server || navigation || sensing || policy || animal || living)) return null;
                ClassReader reader = new ClassReader(bytes);
                ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
                reader.accept(new ClassVisitor(Opcodes.ASM9, writer) {
                    @Override
                    public MethodVisitor visitMethod(int access, String method, String descriptor,
                                                     String signature, String[] exceptions) {
                        MethodVisitor delegate = super.visitMethod(access, method, descriptor, signature, exceptions);
                        boolean tick = server && method.equals("m_5705_");
                        String counter = navigation && method.equals("m_148222_") ? "navigation_calls"
                                : sensing && method.equals("m_5578_") ? "sensor_calls"
                                : policy && method.equals("boundedLiving") ? "bounded_living_scans"
                                : living && method.equals("m_6667_") ? "living_deaths" : null;
                        boolean action = animal && method.equals("beginBfsBehaviorAction");
                        boolean death = living && method.equals("m_6667_");
                        if (!tick && counter == null && !action) return delegate;
                        System.out.println("PERF_HOOK " + name + "." + method + descriptor);
                        return new MethodVisitor(Opcodes.ASM9, delegate) {
                            @Override
                            public void visitCode() {
                                super.visitCode();
                                if (tick) invoke("beforeTick", "()V");
                                if (counter != null) {
                                    visitLdcInsn(counter);
                                    invoke("count", "(Ljava/lang/String;)V");
                                }
                                if (action) {
                                    visitVarInsn(Opcodes.ALOAD, 1);
                                    invoke("action", "(Ljava/lang/String;)V");
                                }
                                if (death) {
                                    visitVarInsn(Opcodes.ALOAD, 0);
                                    visitVarInsn(Opcodes.ALOAD, 1);
                                    invoke("death", "(Ljava/lang/Object;Ljava/lang/Object;)V");
                                }
                            }
                            @Override
                            public void visitInsn(int opcode) {
                                if (tick && opcode == Opcodes.RETURN) {
                                    visitVarInsn(Opcodes.ALOAD, 0);
                                    invoke("afterTick", "(Ljava/lang/Object;)V");
                                }
                                super.visitInsn(opcode);
                            }
                            private void invoke(String target, String descriptor) {
                                super.visitMethodInsn(Opcodes.INVOKESTATIC, "bfs/verification/TickProbe",
                                        target, descriptor, false);
                            }
                        };
                    }
                }, 0);
                return writer.toByteArray();
            }
        });
    }
}
