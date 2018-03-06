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
        int count = 0;
        int progressNotch = input.size() * 5 / 100;
        if(progressNotch < 5) {
            progressNotch = input.size();
        }

        for (I input : input) {
            action.accept(input);

            if (++count % progressNotch == 0) {
                log.info(count + " / " + this.input.size() + " = " + count *100.0f / this.input.size() + "% done.");
            }
        }
    }

    public <T> Collection<T> run(Function<I, T> urlAction) {
        Collection<T> items = new LinkedList<>();
        int count = 0;
        int progressNotch = input.size() * 5 / 100;
        if(progressNotch < 5) {
            progressNotch = input.size();
        }
        for (I input : input) {
            items.add(urlAction.apply(input));

            if (++count % progressNotch == 0) {
                log.info(String.format("%d / %d = %5.2f %% done.", count, this.input.size(), 100.0 * count / this.input.size()));
            }
        }

        return items;
    }
}
