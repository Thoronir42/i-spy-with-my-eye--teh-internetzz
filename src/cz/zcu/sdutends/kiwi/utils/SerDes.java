package cz.zcu.sdutends.kiwi.utils;

public abstract class SerDes<T> {
    public abstract String serialize(T object);

    public abstract T deserialize(String text);
}
