package chapter06.multicast;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/10/8
 */
public class MulticastFX extends Application {
    private Button btnExit = new Button("退出");
    private Button btnSend = new Button("发送");

    private TextField tfSend = new TextField();
    private TextArea taDisplay = new TextArea();

    private Multicast multicast;
    private Thread readThread;


    @Override
    public void start(Stage primaryStage) throws Exception {
        multicast = new Multicast();
        // 加载读消息进程
        readThread = new Thread(() -> {
            String msg = null;
            while (true) {
                msg = multicast.receive();
                String msgTemp = msg;
                Platform.runLater(() -> {
                    taDisplay.appendText(msgTemp + "\n");
                });
            }
        });
        readThread.start();

        BorderPane mainPane = new BorderPane();

        // 信息显示区
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 20, 10, 20));
        // 设置发送信息的文本框
        // 自动换行
        taDisplay.setWrapText(true);
        // 只读
        taDisplay.setEditable(false);
        vBox.getChildren().addAll(new Label("组播对话： "), taDisplay, new Label("信息输入区："), tfSend);
        VBox.setVgrow(taDisplay, Priority.ALWAYS);
        mainPane.setCenter(vBox);

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 20, 10, 20));
        hBox.setAlignment(Pos.CENTER_RIGHT);

        // 按钮事件绑定

        btnExit.setOnAction(event -> {
            exit();
        });
        btnSend.setOnAction(event -> {
            String sendMsg = tfSend.getText();
            multicast.send(sendMsg);
            tfSend.clear();
        });


        hBox.getChildren().addAll(btnSend, btnExit);
        mainPane.setBottom(hBox);
        Scene scene = new Scene(mainPane, 700, 400);

        // 回车响应功能
        scene.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER) {
                    String sendMsg = tfSend.getText();
                    multicast.send(sendMsg);
                    tfSend.clear();
                }
            }
        });

        // 响应窗体关闭
        primaryStage.setOnCloseRequest(event -> {
            exit();
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void exit() {
        if (multicast != null) {
            multicast.close();

        }
        // 系统退出时，单独的读线程没有结束，因此会出现异常。
        // 解决方案：在这里通知线程中断，在线程循环中增加条件检测当前线程是否被中断。
//        readThread.interrupt();
        readThread.stop();
        System.exit(0);
    }

}
