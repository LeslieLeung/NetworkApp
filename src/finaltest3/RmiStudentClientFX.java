package finaltest3;

import finaltest3.rmi.TeacherService;
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

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/11/23
 */
public class RmiStudentClientFX extends Application {
    private TextArea taDisplay = new TextArea();
    private TextField tfMessage = new TextField();
    private TextField tfStunum = new TextField();
    private TextField tfName = new TextField();
    Button btnSendMsg = new Button("发送消息");
    Button btnSendStu = new Button("发送学号和姓名");

    private TeacherService teacherService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        VBox vBoxMain = new VBox();
        vBoxMain.setSpacing(10);
        vBoxMain.setPadding(new Insets(10, 20, 10, 20));
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 20, 10, 20));
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(new Label("输入信息："), tfMessage,
                btnSendMsg, new Label("学号："), tfStunum, new Label("姓名："), tfName, btnSendStu);

        vBoxMain.getChildren().addAll(new Label("信息显示区："),
                taDisplay, hBox);
        Scene scene = new Scene(vBoxMain);
        primaryStage.setScene(scene);
        primaryStage.show();

        // TODO 启动时需要先启动服务器
        //初始化rmi相关操作
        new Thread(() -> {
            rmiInit();
        }).start();
        btnSendMsg.setOnAction(event -> {
            try {
                taDisplay.appendText(teacherService.send(tfMessage.getText().trim()) + "\n");
                tfMessage.clear();

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
            Registry registry = LocateRegistry.getRegistry("202.116.195.71", 1099);
            System.out.println("RMI远程服务别名列表：");
            for (String name : registry.list()) {
                System.out.println(name);
            }

            teacherService = (TeacherService) registry.lookup("TeacherService");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
