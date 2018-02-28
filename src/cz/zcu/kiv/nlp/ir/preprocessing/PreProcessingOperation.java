package cz.zcu.kiv.nlp.ir.preprocessing;

public interface PreProcessingOperation<T> {
    T apply(T value);
}
