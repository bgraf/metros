package de.uos.informatik.se.metros;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.logging.Logger;


/**
 * Created by bgraf on 17.11.16.
 */
public class CountingClassVisitor extends ClassVisitor {
    private final static Logger logger = Logger.getLogger(CountingClassVisitor.class.getName());

    private final Measurement parent;
    private final String binaryName;

    Accumulator accumulator;

    public CountingClassVisitor(Measurement measurement, String binaryName) {
        super(Opcodes.ASM5);

        parent = measurement;
        this.binaryName = binaryName;

        accumulator = new Accumulator();
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        logger.info("Visiting field: " + name);

        accumulator.fieldCount++;

        if ((access & Opcodes.ACC_PUBLIC) != 0) {
            accumulator.publicFieldCount++;
        }

        return null;
    }

    @Override
    public MethodVisitor visitMethod(int i, String name, String desc, String signature, String[] exceptions) {
        logger.info("Visiting method '" + name + "'");

        CountingMethodVisitor countingMethodVisitor = new CountingMethodVisitor(this, i);
        return countingMethodVisitor;
    }

    /**
     * Takes a report form a finished CountingMethodVisitor
     * @param countingMethodVisitor
     */
    public void reportMethod(CountingMethodVisitor countingMethodVisitor, Accumulator otherAccumulator) {

        accumulator.add(otherAccumulator);

        int totalBranchesInMethod =
                otherAccumulator.binaryBranchInstructionCount
                + otherAccumulator.switchBranchInstructionCount;

        if (!countingMethodVisitor.isAbstract && totalBranchesInMethod == 0) {
            // account for empty methods having a single branch.
            logger.info("single branch empty method");
            accumulator.emptyMethodBranches++;
        }
    }


    /* When visiting an inner classes, there are various choices regarding the origin of
     * said class.
     * The visitable will present inner classes of parent classes of the visited class.
     *
     * If the outer class name of the inner class is not equal to the current class,
     * then the inner class' origin is out of scope of the analysis.
     */
    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        logger.info("INNER CLASS: " + name);
        logger.info("INNER CLASS 1>: " + parent.binaryClassName);
        logger.info("INNER CLASS 2>: " + outerName);
        parent.addEntry(name,
                parent.binaryClassName.equals(outerName)
                || binaryName.equals(outerName));
    }

    @Override
    public void visitEnd() {
    }
}
