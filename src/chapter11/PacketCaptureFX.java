package chapter11;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jpcap.JpcapCaptor;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/11/9
 */
public class PacketCaptureFX extends Application {
    private TextArea taDisplay = new TextArea();

    private Button btnStart = new Button("开始抓包");
    private Button btnStop = new Button("停止抓包");
    private Button btnClear = new Button("清空");
    private Button btnSetting = new Button("设置");
    private Button btnExit = new Button("退出");

    private ConfigDialog configDialog;
    private JpcapCaptor jpcapCaptor;

    private String keyData;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane mainPane = new BorderPane();

        VBox display = new VBox();
        display.setSpacing(10);
        display.setPadding(new Insets(10, 20, 10, 20));
        // 自动换行
        taDisplay.setWrapText(true);
        // 只读
        taDisplay.setEditable(false);
        taDisplay.setPrefHeight(250);
        display.getChildren().addAll(new Label("抓包信息："), taDisplay);
        VBox.setVgrow(taDisplay, Priority.ALWAYS);
        mainPane.setCenter(display);

        btnSetting.setOnAction(event -> {
            if (configDialog == null) {
                configDialog = new ConfigDialog(primaryStage);
            }
            configDialog.showAndWait();
            jpcapCaptor = configDialog.getJpcapCaptor();
            keyData = configDialog.getKeyData();
        });

        btnStart.setOnAction(event -> {
            if (jpcapCaptor == null) {
                if (configDialog == null) {
                    configDialog = new ConfigDialog(primaryStage);
                }
                configDialog.showAndWait();
                jpcapCaptor = configDialog.getJpcapCaptor();
                keyData = configDialog.getKeyData();
            }
            interrupt("captureThread");
            new Thread(() -> {
                while (true) {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    jpcapCaptor.processPacket(1, new PacketHandler());
                }
            }, "captureThread").start();
        });

        btnStop.setOnAction(event -> {
            interrupt("captureThread");
        });

        btnClear.setOnAction(event -> {
            taDisplay.clear();
        });

        btnExit.setOnAction(event -> {
            exit();
        });

        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        buttons.setPadding(new Insets(10, 20, 10, 20));
        buttons.getChildren().addAll(btnStart, btnStop, btnClear, btnSetting, btnExit);
        mainPane.setBottom(buttons);

        Scene scene = new Scene(mainPane, 700, 400);
        primaryStage.setOnCloseRequest(event -> {
            exit();
        });

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    class PacketHandler implements PacketReceiver {
        @Override
        public void receivePacket(Packet packet) {
            Platform.runLater(() -> {
                taDisplay.appendText(packet.toString() + "\n");
            });
            System.out.println(keyData);
            if (keyData == null || keyData.trim().equalsIgnoreCase("")) {
                return;
            }
            try {
                String[] keyList = keyData.split(" ");
                String msg = new String(packet.data, 0, packet.data.length, "utf-8");
                for (String key : keyList) {
                    if (msg.toUpperCase().contains(key.toUpperCase())) {
                        Platform.runLater(() -> {
                            taDisplay.appendText("数据部分：" + msg + "\n\n");
                        });
                        break;
                    }
                }

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private void interrupt(String threadName) {
        ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
        int noThreads = currentGroup.activeCount();
        Thread[] lstThreads = new Thread[noThreads];
        currentGroup.enumerate(lstThreads);
        for (int i = 0; i < noThreads; i++) {
            if (lstThreads[i].getName().equals(threadName)) {
                lstThreads[i].interrupt();
            }
        }
    }

    private void exit() {
        interrupt("captureThread");
        System.exit(0);
    }
}

