package chapter08;

/**
 * description: Client基类
 * HTTPClient和HTTPSClient通过继承本类实现在FX里面只需要有一个Client就可以调用http或https
 * author: Leslie Leung
 * date: 2020/10/26
 */
public abstract class Client {
    public abstract String receive();

    public abstract void send(String msg);

    public abstract void close();

}
