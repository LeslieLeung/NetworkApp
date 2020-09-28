package chapter04.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class FileClientFX extends Application {
    private Button btnExit = new Button("退出");
    private Button btnSend = new Button("发送");
    private Button btnDownload = new Button("下载");

    private TextField tfSend = new TextField();
    private TextArea taDisplay = new TextArea();

    private TextField tfIP = new TextField("127.0.0.1");
    private TextField tfPort = new TextField("8008");
    private Button btnConnect = new Button("连接");

    private FileDialogClient fileDialogClient;
    private Thread readThread;

    private String ip;
    private String port;
    private boolean isValidFile = true;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane mainPane = new BorderPane();

        HBox connHbox = new HBox();
        connHbox.setAlignment(Pos.CENTER);
        connHbox.setSpacing(10);
        connHbox.getChildren().addAll(new Label("IP地址："), tfIP, new Label("端口："), tfPort, btnConnect);
        mainPane.setTop(connHbox);

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 20, 10, 20));
        // 设置发送信息的文本框
        // 自动换行
        taDisplay.setWrapText(true);
        // 只读
        taDisplay.setEditable(false);
        // 设置taDisplay监听鼠标拖动行为并复制到发送框
        taDisplay.selectionProperty().addListener(((observable, oldValue, newValue) -> {
            if (!taDisplay.getSelectedText().equals("")) {
                tfSend.setText(taDisplay.getSelectedText());
            }
        }));
        vBox.getChildren().addAll(new Label("信息显示区： "), taDisplay, new Label("信息输入区："), tfSend);
        VBox.setVgrow(taDisplay, Priority.ALWAYS);
        mainPane.setCenter(vBox);

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 20, 10, 20));
        hBox.setAlignment(Pos.CENTER_RIGHT);

        // 按钮事件绑定
        btnConnect.setOnAction(event -> {
            String ip = tfIP.getText().trim();
            String port = tfPort.getText().trim();

            try {
                //tcpClient不是局部变量，是本程序定义的一个TCPClient类型的成员变量
                fileDialogClient = new FileDialogClient(ip, port);
                //成功连接服务器，接收服务器发来的第一条欢迎信息
                String firstMsg = fileDialogClient.receive();
                taDisplay.appendText(firstMsg + "\n");
                // 启用发送按钮
                btnSend.setDisable(false);
                // 停用连接按钮
                btnConnect.setDisable(true);
                // 启用接收信息进程
                readThread = new Thread(() -> {
                    String msg = null;
                    while ((msg = fileDialogClient.receive()) != null) {
                        String msgTemp = msg;
                        Platform.runLater(() -> {
                            taDisplay.appendText(msgTemp + "\n");
                        });
                        if (msg.equals("From 服务器：你输入了不合法的指令或不正确的文件名")) {
                            isValidFile = false;
                        }
                    }
                    Platform.runLater(() -> {
                        taDisplay.appendText("对话已关闭！\n");
                    });
                });
                readThread.start();
            } catch (Exception e) {
                taDisplay.appendText("服务器连接失败！" + e.getMessage() + "\n");
            }

        });
        btnExit.setOnAction(event -> {
            exit();
        });
        btnSend.setOnAction(event -> {
            String sendMsg = tfSend.getText();
            fileDialogClient.send(sendMsg);//向服务器发送一串字符
            taDisplay.appendText("客户端发送：" + sendMsg + "\n");
//            String receiveMsg = tcpClient.receive();//从服务器接收一行字符
//            taDisplay.appendText(receiveMsg + "\n");
            tfSend.clear();
            // 发送bye后重新启用连接按钮，禁用发送按钮
            if (sendMsg.equals("bye")) {
                btnConnect.setDisable(false);
                btnSend.setDisable(true);
            }
        });

        // btnDownload逻辑
        btnDownload.setOnAction(event -> {
            if (tfSend.getText().equals("")) {
                return;
            }
            if (!isValidFile) {
                return;
            }

            String fName = tfSend.getText().trim();
            tfSend.clear();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName(fName);
            File saveFile = fileChooser.showSaveDialog(null);
            if (saveFile == null) {
                return;
            }
            try {
                new FileDataClient(ip, "2020").getFile(saveFile);
                Alert.AlertType alertAlertType;
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(saveFile.getName() + "下载完毕！");
                alert.showAndWait();
                fileDialogClient.send("客户端开启下载");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        // 未连接时禁用发送按钮
        btnSend.setDisable(true);
        hBox.getChildren().addAll(btnDownload, btnSend, btnExit);
        mainPane.setBottom(hBox);
        Scene scene = new Scene(mainPane, 700, 400);

//        // 回车响应功能
//        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
//            final KeyCombination keyCombination = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHIFT_DOWN);
//
//            @Override
//            public void handle(KeyEvent event) {
//                if (event.getCode() == KeyCode.ENTER) {
//                    String sendMsg = tfSend.getText();
//                    tcpClient.send(sendMsg);//向服务器发送一串字符
//                    taDisplay.appendText("客户端发送：" + sendMsg + "\n");
//                    String receiveMsg = tcpClient.receive();//从服务器接收一行字符
//                    taDisplay.appendText(receiveMsg + "\n");
//                    // 发送bye后重新启用连接按钮，禁用发送按钮
//                    if (sendMsg.equals("bye")) {
//                        btnConnect.setDisable(false);
//                        btnSend.setDisable(true);
//                    }
//                }
//            }
//        });

        // 响应窗体关闭
        primaryStage.setOnCloseRequest(event -> {
            exit();
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void exit() {
        if (fileDialogClient != null) {
            fileDialogClient.send("bye");
            fileDialogClient.close();
        }
        System.exit(0);
    }
}
