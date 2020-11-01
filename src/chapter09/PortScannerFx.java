package chapter09;

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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/11/1
 */
public class PortScannerFx extends Application {
    private TextArea taDisplay = new TextArea();

    private TextField tfTargetIp = new TextField("192.168.0.1");
    private TextField tfStartPort = new TextField("1");
    private TextField tfEndPort = new TextField("443");

    private Button btnScan = new Button("扫描");
    private Button btnQuickScan = new Button("快速扫描");
    private Button btnMultiThreadScan = new Button("多线程扫描");
    private Button btnStop = new Button("停止");
    private Button btnClear = new Button("清空");
    private Button btnExit = new Button("退出");

    private Thread scanThread;

    private String ip;
    private int startPort;
    private int endPort;
    static AtomicInteger portCount = new AtomicInteger(0);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane mainPane = new BorderPane();

        VBox display = new VBox();
//        display.prefHeight(400);
        display.setSpacing(10);
        display.setPadding(new Insets(10, 20, 10, 20));
        // 自动换行
        taDisplay.setWrapText(true);
        // 只读
        taDisplay.setEditable(false);
        taDisplay.setPrefHeight(250);
        display.getChildren().addAll(new Label("端口扫描结果:"), taDisplay);
        VBox.setVgrow(taDisplay, Priority.ALWAYS);
        mainPane.setTop(display);

        HBox params = new HBox();
        params.setSpacing(10);
        params.setPadding(new Insets(10, 20, 10, 20));
        params.setAlignment(Pos.CENTER);
        tfStartPort.setPrefWidth(40);
        tfEndPort.setPrefWidth(40);
        params.getChildren().addAll(new Label("目标主机IP："), tfTargetIp,
                new Label("起始端口号："), tfStartPort,
                new Label("结束端口号："), tfEndPort);
        mainPane.setCenter(params);

        btnStop.setDisable(true);
        btnScan.setOnAction(event -> {
            String ip = tfTargetIp.getText();
            int startPort = Integer.parseInt(tfStartPort.getText());
            int endPort = Integer.parseInt(tfEndPort.getText());
            scanThread = new Thread(() -> {
                System.out.println("Scan start!");
                for (int i = startPort; i <= endPort; i++) {
                    String msg;
                    System.out.println("Scanning port " + i);
                    try {
                        Socket socket = new Socket(ip, i);
                        socket.close();
                        msg = "端口 " + i + " is open\n";
                    } catch (IOException e) {
                        msg = "端口 " + i + " is not open\n";
                    }
                    taDisplay.appendText(msg);
                }
            });
            // 启用停止按钮 禁用其他按钮
            btnStop.setDisable(false);
            btnScan.setDisable(true);
            btnQuickScan.setDisable(true);
            btnMultiThreadScan.setDisable(true);
            scanThread.start();
        });

        btnQuickScan.setOnAction(event -> {
            String ip = tfTargetIp.getText();
            int startPort = Integer.parseInt(tfStartPort.getText());
            int endPort = Integer.parseInt(tfEndPort.getText());
            scanThread = new Thread(() -> {
                System.out.println("Scan start!");
                for (int i = startPort; i <= endPort; i++) {
                    String msg;
                    System.out.println("Scanning port " + i);
                    try {
                        Socket socket = new Socket();
                        socket.connect(new InetSocketAddress(ip, i), 200);
                        socket.close();
                        msg = "端口 " + i + " is open\n";
                    } catch (IOException e) {
                        msg = "端口 " + i + " is not open\n";
                    }
                    taDisplay.appendText(msg);
                }
            });
            // 启用停止按钮 禁用其他按钮
            btnStop.setDisable(false);
            btnScan.setDisable(true);
            btnQuickScan.setDisable(true);
            btnMultiThreadScan.setDisable(true);
            scanThread.start();
        });

        btnMultiThreadScan.setOnAction(event -> {
            this.ip = tfTargetIp.getText();
            this.startPort = Integer.parseInt(tfStartPort.getText());
            this.endPort = Integer.parseInt(tfEndPort.getText());

            btnStop.setDisable(false);
            btnScan.setDisable(true);
            btnQuickScan.setDisable(true);
            btnMultiThreadScan.setDisable(true);

            int thread = 16;
            for (int i = 0; i < thread; i++) {
                ScanHandler scanHandler = new ScanHandler(i, thread);
                new Thread(scanHandler).start();
            }
        });

        btnStop.setOnAction(event -> {
            btnStop.setDisable(true);
            btnScan.setDisable(false);
            btnQuickScan.setDisable(false);
            btnMultiThreadScan.setDisable(false);
            try {
                scanThread.stop();
            } catch (Exception e) {
            }

        });

        btnExit.setOnAction(event -> {
            try {
                scanThread.stop();
            } catch (Exception e) {
            }
            System.exit(0);
        });

        btnClear.setOnAction(event -> {
            taDisplay.clear();
            portCount.set(0);
        });

        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        buttons.setPadding(new Insets(10, 20, 10, 20));
        buttons.getChildren().addAll(btnScan, btnQuickScan, btnMultiThreadScan, btnStop, btnClear, btnExit);
        mainPane.setBottom(buttons);


        Scene scene = new Scene(mainPane, 700, 400);
        primaryStage.setOnCloseRequest(event -> {
            try {
                scanThread.stop();
            } catch (Exception e) {
            }
            System.exit(0);
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    class ScanHandler implements Runnable {
        private int totalThreadNum;
        private int threadNo;

        public ScanHandler(int threadNo) {
            this.totalThreadNum = 10;
            this.threadNo = threadNo;
        }

        public ScanHandler(int threadNo, int totalThreadNum) {
            this.totalThreadNum = totalThreadNum;
            this.threadNo = threadNo;
        }

        @Override
        public void run() {
            for (int port = startPort + threadNo; port <= endPort; port = port + totalThreadNum) {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(ip, port), 200);
                    socket.close();
                    String msg = "端口 " + port + " is open\n";
                    Platform.runLater(() -> {
                        taDisplay.appendText(msg);
                    });
                } catch (IOException e) {
                }
                portCount.incrementAndGet();//扫描的端口数+1
            }
            if (portCount.get() == (endPort - startPort + 1)) {
                portCount.incrementAndGet();
                Platform.runLater(() -> {
                    taDisplay.appendText("\n----------------多线程扫描结束--------------------\n");
                    btnStop.setDisable(true);
                    btnScan.setDisable(false);
                    btnQuickScan.setDisable(false);
                    btnMultiThreadScan.setDisable(false);
                });
            }
        }
    }
}
