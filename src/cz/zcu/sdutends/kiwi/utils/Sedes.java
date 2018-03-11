package cz.zcu.sdutends.kiwi.utils;

public abstract class Sedes<T> {
    public abstract String serialize(T object);

    public abstract T deserialize(String text);
}
