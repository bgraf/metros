package de.uos.informatik.se.metros;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.logging.Logger;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;

public class CountingMethodVisitor extends MethodVisitor {
    private final static Logger logger = Logger.getLogger(CountingClassVisitor.class.getName());

    private final CountingClassVisitor parent;

    /**
     * Is `true` iff the method is abstract, i.e it has no implementation in the
     * current class.
     */
    boolean isAbstract;
    boolean isPublic;
    boolean isStatic;


    int access;

    Accumulator accumulator;

    /**
     * Construct a counting method visitor.
     * @param countingClassVisitor      Parent visitor being reported to.
     * @param i
     */
    public CountingMethodVisitor(CountingClassVisitor countingClassVisitor, int i) {
        super(Opcodes.ASM5);
        parent = countingClassVisitor;
        access = i;

        accumulator = new Accumulator();

        isPublic = (access & ACC_PUBLIC) != 0;
        isStatic = (access & ACC_STATIC) != 0;

        isAbstract = true;
    }

    @Override
    public void visitJumpInsn(int i, Label label) {
        switch (i) {
            case Opcodes.JSR:
            case Opcodes.GOTO:
                break;
            default:
                accumulator.incBranchCounter(i);
                accumulator.binaryBranchInstructionCount++;
        }
    }

    @Override
    public void visitLookupSwitchInsn(Label label, int[] keys, Label[] labels) {
        accumulator.switchBranchCount += 1 + labels.length;
        accumulator.switchBranchInstructionCount++;
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        accumulator.switchBranchCount += 1 + labels.length;
        accumulator.switchBranchInstructionCount++;
    }

    @Override
    public void visitCode() {
        isAbstract = false;
    }

    @Override
    public void visitEnd() {
        // check method properties..
        accumulator.methodCount++;
        if (!isAbstract)
            accumulator.concreteMethodCount++;

        if (isPublic) {
            accumulator.publicMethodCount++;
            if (!isAbstract)
                accumulator.publicConcreteMethodCount++;
        }

        if (isStatic) {
            accumulator.staticMethodCount++;
            if (!isAbstract)
                accumulator.staticConcreteMethodCount++;
            if (isPublic) {
                accumulator.staticPublicMethodCount++;
                if (!isAbstract)
                    accumulator.staticPublicConcreteMethodCount++;
            }
        }

        parent.reportMethod(this, accumulator);
    }
}
