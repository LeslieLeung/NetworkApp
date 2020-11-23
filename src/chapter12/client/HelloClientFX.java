package chapter12.client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import rmi.HelloService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/11/23
 */
public class HelloClientFX extends Application {
    private TextArea taDisplay = new TextArea();
    private TextField tfMessage = new TextField();
    Button btnEcho = new Button("调用echo方法");
    Button btnGetTime = new Button("调用getTime方法");
    //客户端也有一份和服务端相同的远程接口
    private HelloService helloService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        //main区域
        VBox vBoxMain = new VBox();
        vBoxMain.setSpacing(10);//各控件之间的间隔
        //VBoxMain面板中的内容距离四周的留空区域
        vBoxMain.setPadding(new Insets(10, 20, 10, 20));
        HBox hBox = new HBox();
        hBox.setSpacing(10);//各控件之间的间隔
        //HBox面板中的内容距离四周的留空区域
        hBox.setPadding(new Insets(10, 20, 10, 20));
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(new Label("输入信息："), tfMessage,
                btnEcho, btnGetTime);

        vBoxMain.getChildren().addAll(new Label("信息显示区："),
                taDisplay, hBox);
        Scene scene = new Scene(vBoxMain);
        primaryStage.setScene(scene);
        primaryStage.show();

        //初始化rmi相关操作
        new Thread(() -> {
            rmiInit();
        }).start();
        btnEcho.setOnAction(event -> {
            try {
                String msg = tfMessage.getText();
                taDisplay.appendText(helloService.echo(msg) + "\n");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        btnGetTime.setOnAction(event -> {
            try {
                String msg = helloService.getTime().toString();
                taDisplay.appendText(msg + "\n");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 初始化rmi相关操作
     */
    public void rmiInit() {
        try {
            //(1)获取RMI注册器
            Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
            System.out.println("RMI远程服务别名列表：");
            for (String name : registry.list()) {
                System.out.println(name);
            }

            //(2)客户端(调用端)到注册器中使用助记符寻找并创建远程服务对象的客户端(调用端)stub，之后本地调用helloService的方法，实质就是调用了远程服务器上同名的远程接口下的同名方法
            helloService = (HelloService) registry.lookup("HelloService");
            //另外一种创建stub的方式
            //helloService = (HelloService)Naming.lookup("rmi://127.0.0.1:1099/" + "HelloService");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

