package chapter07;

import chapter02.TCPClient;
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
import sun.misc.BASE64Encoder;

import java.io.IOException;

public class TCPMailClientFX2 extends Application {
    private Button btnExit = new Button("退出");
    private Button btnSend = new Button("发送");

    private TextArea taSend = new TextArea();
    private TextArea taDisplay = new TextArea();

    private TextField tfIP = new TextField("smtp.qq.com");
    private TextField tfPort = new TextField("25");

    private TextField tfSender = new TextField("429242349@qq.com");
    private TextField tfReceiver = new TextField("lesily9@gmail.com");
    private TextField tfTitle = new TextField();

    private TCPMailClient tcpMailClient;
    private Thread readThread;
    private Thread sendThread;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane mainPane = new BorderPane();

        VBox head = new VBox();
        HBox connHbox = new HBox();
        connHbox.setAlignment(Pos.CENTER);
        connHbox.setSpacing(10);
        connHbox.getChildren().addAll(new Label("邮件服务器地址："), tfIP, new Label("邮件服务器端口："), tfPort);

        HBox addrHbox = new HBox();
        addrHbox.setAlignment(Pos.CENTER);
        addrHbox.setSpacing(10);
        addrHbox.getChildren().addAll(new Label("邮件发送者地址："), tfSender, new Label("邮件接收者地址："), tfReceiver);

        HBox titleHbox = new HBox();
        tfTitle.setPrefWidth(400);
        titleHbox.setAlignment(Pos.CENTER);
        titleHbox.getChildren().addAll(new Label("邮件标题："), tfTitle);


        head.getChildren().addAll(connHbox, addrHbox, titleHbox);
        head.setSpacing(20);
        mainPane.setTop(head);

        HBox middle = new HBox();
        middle.setAlignment(Pos.CENTER);
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 20, 10, 20));
        // 设置发送信息的文本框
        // 自动换行
        taDisplay.setWrapText(true);
        // 只读
        taDisplay.setEditable(false);
        vBox.getChildren().addAll(new Label("信息显示区： "), taDisplay);
        VBox.setVgrow(taDisplay, Priority.ALWAYS);

        VBox mailVBox = new VBox();
        mailVBox.setPadding(new Insets(10, 20, 10, 20));
        // 设置发送信息的文本框
        // 自动换行
        taSend.setWrapText(true);
        mailVBox.getChildren().addAll(new Label("邮件正文： "), taSend);
        mailVBox.setSpacing(10);
        VBox.setVgrow(taSend, Priority.ALWAYS);

        middle.getChildren().addAll(mailVBox, vBox);
        mainPane.setCenter(middle);

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 20, 10, 20));
        hBox.setAlignment(Pos.CENTER_RIGHT);

        String ip = tfIP.getText().trim();
        String port = tfPort.getText().trim();

        btnExit.setOnAction(event -> {
            exit();
        });
        btnSend.setOnAction(event -> {
            String smtpAddr = tfIP.getText().trim();
            String smtpPort = tfPort.getText().trim();
            try {
                tcpMailClient = new TCPMailClient(smtpAddr, smtpPort);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 启用接收信息进程
            readThread = new Thread(() -> {
                String msg = null;
                while ((msg = tcpMailClient.receive()) != null) {
                    String msgTemp = msg;
                    Platform.runLater(() -> {
                        taDisplay.appendText(msgTemp + "\n");
                    });
                }
                Platform.runLater(() -> {
                    taDisplay.appendText("对话已关闭！\n");
                    // 连接断开后重新开放连接按钮
//                    btnSend.setDisable(true);
                });
            });
            readThread.start();

            sendThread = new Thread(() -> {
                tcpMailClient.send("HELO myfriend");
                tcpMailClient.send("AUTH LOGIN");

                String userName = "429242349@qq.com";
                String authCode = "yvhdqefhaquxbiee";

                String msg = BASE64.encode(userName);
                tcpMailClient.send(msg);

                msg = BASE64.encode(authCode);
                tcpMailClient.send(msg);

                msg = "MAIL FROM:<" + tfSender.getText().trim() + ">";
                tcpMailClient.send(msg);

                msg = "RCPT TO:<" + tfReceiver.getText().trim() + ">";
                tcpMailClient.send(msg);

                msg = "DATA";
                tcpMailClient.send(msg);

                msg = "FROM:" + tfSender.getText().trim();
                tcpMailClient.send(msg);

                if (!"".equals(tfTitle.getText().trim())) {
                    msg = "Subject:" + tfTitle.getText().trim();
                    tcpMailClient.send(msg);
                }

                msg = "";
                tcpMailClient.send(msg);

                msg = taSend.getText().trim();
                tcpMailClient.send(msg);

                msg = ".";
                tcpMailClient.send(msg);

                msg = "QUIT";
                tcpMailClient.send(msg);

                btnSend.setDisable(false);

            });
            sendThread.start();
            // 禁用发送按钮
            btnSend.setDisable(true);
        });


        hBox.getChildren().addAll(btnSend, btnExit);
        mainPane.setBottom(hBox);
        Scene scene = new Scene(mainPane, 700, 400);

        // 响应窗体关闭
        primaryStage.setOnCloseRequest(event -> {
            exit();
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void exit() {
        // 系统退出时，单独的读线程没有结束，因此会出现异常。
        // 解决方案：在这里通知线程中断，在线程循环中增加条件检测当前线程是否被中断。
//        readThread.interrupt();
        readThread.stop();
        System.exit(0);
    }

}
