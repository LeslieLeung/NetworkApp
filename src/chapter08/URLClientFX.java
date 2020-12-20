package chapter08;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/10/26
 */
public class URLClientFX extends Application {
    private Button btnExit = new Button("退出");
    private Button btnSend = new Button("发送");

    private TextField tfSend = new TextField();
    private TextArea taDisplay = new TextArea();

    private BufferedReader br;
    private Thread readThread;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane mainPane = new BorderPane();

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 20, 10, 20));
        // 设置发送信息的文本框
        // 自动换行
        taDisplay.setWrapText(true);
        // 只读
        taDisplay.setEditable(false);
        vBox.getChildren().addAll(new Label("信息显示区： "), taDisplay, new Label("信息输入区："), tfSend);
        VBox.setVgrow(taDisplay, Priority.ALWAYS);
        mainPane.setCenter(vBox);

        btnSend.setOnAction(event -> {
            taDisplay.clear();
            String address = tfSend.getText().trim();
            // URL匹配
            if (!address.matches("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]")) {
                taDisplay.appendText("URL地址输入不合规则！");
            } else {
                try {
                    URL url = new URL(address);
                    System.out.printf("连接%s成功！\n", address);
                    InputStream in = url.openStream();

                    br = new BufferedReader(new InputStreamReader(in, "utf-8"));

                    readThread = new Thread(() -> {
                        String msg = null;

                        while ((msg = receive()) != null) {
                            String msgTemp = msg;
                            Platform.runLater(() -> {
                                taDisplay.appendText(msgTemp + "\n");
                            });
                        }
                        Platform.runLater(() -> {
                            taDisplay.appendText("连接已断开！\n");
                        });
                    });
                    readThread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        btnExit.setOnAction(event -> {
            readThread.stop();
            System.exit(0);
        });


        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 20, 10, 20));
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().addAll(btnSend, btnExit);
        mainPane.setBottom(hBox);
        Scene scene = new Scene(mainPane, 700, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public String receive() {
        String msg = null;
        try {
            msg = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }
}
