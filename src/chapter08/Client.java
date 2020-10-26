package chapter08;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/10/26
 */
public abstract class Client {
    public abstract String receive();

    public abstract void send(String msg);

    public abstract void close();

}
