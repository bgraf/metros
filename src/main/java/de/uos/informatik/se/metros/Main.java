package de.uos.informatik.se.metros;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        /*
        args = new String[]{
            //"de/huxhorn/lilith/data/logging/protobuf/LoggingEventProtobufDecoder"
                "de/uos/informatik/se/metros/ExampleClass"
        };
        */

        if (args.length == 0) {
            System.err.println("At least one CLASSNAME required");
            System.exit(1);
        }

        try {
            CSVPrinter printer = new CSVPrinter(System.out, CSVFormat.DEFAULT);

            /*
            printer.printRecord(
                    "class",
                    "NumMethod",
                    "NumConcreteMethod",
                    "NumBranch",
                    "NumBranchInstr",
                    "PNumMethod",
                    "PNumConcreteMethod",
                    "NumStaticMethod",
                    "PNumStaticMethod",
                    "NumField",
                    "PNumField"
					"oIFEQ,
					"oIFNE,
					"oIFLT,
					"oIFLE,
					"oIFGT,
					"oIFGE,
					"oIF_ICMPEQ,
					"oIF_ICMPNE,
					"oIF_ICMPLT,
					"oIF_ICMPGE,
					"oIF_ICMPGT,
					"oIF_ICMPLE,
					"oIF_ACMPEQ,
					"oIF_ACMPNE,
					"oIFNULL,
					"oIFNONNULL,
            );
            */

            for (String className : args) {
                Measurement measurement =
                        new Measurement(className);

                Accumulator accumulator = measurement.accumulator;

                List<Object> record =
                        new ArrayList<>(
                                Arrays.asList(
                                        className,
                                        accumulator.methodCount,
                                        accumulator.concreteMethodCount,
                                        accumulator.branchCount(),
                                        accumulator.branchInstructionCount(),
                                        accumulator.publicMethodCount,
                                        accumulator.publicConcreteMethodCount,
                                        accumulator.staticMethodCount,
                                        accumulator.staticConcreteMethodCount,
                                        accumulator.staticPublicMethodCount,
                                        accumulator.staticPublicConcreteMethodCount,
                                        accumulator.fieldCount,
                                        accumulator.publicFieldCount
                                ));

                for (int op : Accumulator.BRANCH_INSTRUCTIONS) {
                    record.add(accumulator.branchInstructionCounters.getOrDefault(op, 0L));
                }

                printer.printRecord(record);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
