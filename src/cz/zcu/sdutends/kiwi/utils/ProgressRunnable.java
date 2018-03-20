package cz.zcu.sdutends.kiwi.utils;

import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;

public class ProgressRunnable<I> {
    private static Logger log = Logger.getLogger(ProgressRunnable.class);

    private final Collection<I> input;

    public ProgressRunnable(Collection<I> input) {
        this.input = input;
    }

    public void run(Consumer<I> action) {
        this.run((I a) -> {
            action.accept(a);

            return null;
        });
    }

    public <T> Collection<T> run(Function<I, T> func) {
        Collection<T> items = new LinkedList<>();
        int count = 0;
        int progressNotch = this.input.size() * 5 / 100;
        if(progressNotch < 5) {
            progressNotch = this.input.size();
        }

        for (I input : this.input) {
            try {
                T result = func.apply(input);
                if(result != null) {
                    items.add(result);
                }
            } catch (Exception ex) {
                log.error("Fail during processing of input #"+ count +": " + input + "\n" + ex.toString());
                continue;
            }

            if (++count % progressNotch == 0) {
                log.info(String.format("%d / %d = %5.2f %% done.", count, this.input.size(), 100.0 * count / this.input.size()));
            }
        }

        return items;
    }
}
