package de.uos.informatik.se.metros;

import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Logger;

public class Measurement {
    private static final Logger logger = Logger.getLogger(Measurement.class.getName());

    private class QueueEntry {
        final String binaryClassName;
        final boolean legitInner;

        QueueEntry(String binaryClassName, boolean legitInner) {
            this.binaryClassName = binaryClassName;
            this.legitInner = legitInner;
        }
    }

    private final Queue<QueueEntry> queue = new ArrayDeque<QueueEntry>();

    private final Set<String> handledClassNames = new HashSet<>();

    final String binaryClassName;


    Accumulator accumulator;


    public Measurement(String binaryClassName) {
        this.binaryClassName = binaryClassName.replace('.', '/');

        accumulator = new Accumulator();

        compute();
    }


    void addEntry(String binaryClassName, boolean legitInner) {
        if (!legitInner)
            return;

        if (handledClassNames.contains(binaryClassName))
            return;

        logger.info("adding to queue: " + binaryClassName);

        handledClassNames.add(binaryClassName);

        QueueEntry entry = new QueueEntry(
                binaryClassName,
                legitInner);

        queue.add(entry);
    }

    private void compute() {
        queue.add(new QueueEntry(binaryClassName, true));

        while (!queue.isEmpty()) {
            QueueEntry entry = queue.poll();
            logger.info("Processing: " + entry.binaryClassName);

            try {
                CountingClassVisitor countingClassVisitor = new CountingClassVisitor(this, entry.binaryClassName);

                ClassReader classReader =
                        new ClassReader(entry.binaryClassName);
                classReader.accept(countingClassVisitor, 0);

                accumulator.add(countingClassVisitor.accumulator);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
