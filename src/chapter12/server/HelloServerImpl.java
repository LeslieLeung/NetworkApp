package chapter12.server;

import rmi.HelloService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/11/23
 */
public class HelloServerImpl extends UnicastRemoteObject implements HelloService {
    private String name;

    public HelloServerImpl() throws RemoteException{

    }

    public HelloServerImpl(String name) throws RemoteException {
        this.name = name;
    }

    @Override
    public String echo(String msg) throws RemoteException {
        System.out.println("服务端完成一些echo方法相关任务......");
        return "echo: " + msg + " from " + name;
    }

    @Override
    public Date getTime() throws RemoteException {
        System.out.println("服务端完成一些getTime方法相关任务......");
        return new Date();
    }
}
