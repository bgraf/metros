package de.uos.informatik.se.metros;

import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the current state of a counting process.
 */
public class Accumulator {
    public final static int[] BRANCH_INSTRUCTIONS = {
			Opcodes.IFEQ,
			Opcodes.IFNE,
            Opcodes.IFLT,
            Opcodes.IFLE,
            Opcodes.IFGT,
            Opcodes.IFGE,
            Opcodes.IF_ICMPEQ,
            Opcodes.IF_ICMPNE,
            Opcodes.IF_ICMPLT,
            Opcodes.IF_ICMPGE,
            Opcodes.IF_ICMPGT,
            Opcodes.IF_ICMPLE,
            Opcodes.IF_ACMPEQ,
            Opcodes.IF_ACMPNE,
            Opcodes.IFNULL,
            Opcodes.IFNONNULL,
    };

    int methodCount;
    int concreteMethodCount;

    int binaryBranchInstructionCount;
    int switchBranchCount;
    int switchBranchInstructionCount;

    Map<Integer, Long> branchInstructionCounters;

    int emptyMethodBranches;

    int publicMethodCount;
    int publicConcreteMethodCount;

    int staticMethodCount;
    int staticConcreteMethodCount;

    int staticPublicMethodCount;
    int staticPublicConcreteMethodCount;

    int fieldCount;
    int publicFieldCount;

    public Accumulator() {
        branchInstructionCounters = new HashMap<>();
    }

    public void incBranchCounter(int opcode, long value) {
        Long current = branchInstructionCounters.getOrDefault(opcode, 0L);
        branchInstructionCounters.put(opcode, current+value);
    }

    public void incBranchCounter(int opcode) {
        incBranchCounter(opcode, 1);
    }

    public void add(Accumulator other) {
        this.methodCount += other.methodCount;
        this.concreteMethodCount += other.concreteMethodCount;

        this.binaryBranchInstructionCount += other.binaryBranchInstructionCount;
        this.switchBranchCount += other.switchBranchCount;
        this.switchBranchInstructionCount += other.switchBranchInstructionCount;

        this.emptyMethodBranches += other.emptyMethodBranches;

        this.publicMethodCount += other.publicMethodCount;
        this.publicConcreteMethodCount += other.publicConcreteMethodCount;

        this.staticMethodCount += other.staticMethodCount;
        this.staticConcreteMethodCount += other.staticConcreteMethodCount;

        this.staticPublicMethodCount += other.staticPublicMethodCount;
        this.staticPublicConcreteMethodCount += other.staticPublicConcreteMethodCount;

        this.fieldCount += other.fieldCount;
        this.publicFieldCount += other.publicFieldCount;

        other.branchInstructionCounters.entrySet().forEach(
                (e) -> { incBranchCounter(e.getKey(), e.getValue()); }
        );

    }

    public Accumulator addPure(Accumulator other) {
        Accumulator acc = new Accumulator();
        acc.add(this);
        acc.add(other);
        return acc;
    }

    public int branchCount() {
        return binaryBranchInstructionCount * 2
                + switchBranchCount
                + emptyMethodBranches;
    }

    public int branchInstructionCount() {
        return switchBranchInstructionCount
                + binaryBranchInstructionCount;
    }
}
