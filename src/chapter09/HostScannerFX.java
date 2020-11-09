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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/11/1
 */
public class HostScannerFX extends Application {
    private TextArea taDisplay = new TextArea();

    private TextField tfStartIp = new TextField("192.168.0.1");
    private TextField tfEndIp = new TextField("192.168.0.158");
    private TextField tfCmd = new TextField();

    private Button btnScan = new Button("主机扫描");
    private Button btnExecute = new Button("执行命令");
    private Button btnStop = new Button("停止");
    private Button btnExit = new Button("退出");

    private ThreadGroup threadGroup = new ThreadGroup("scanThread");
    static AtomicInteger hostCount = new AtomicInteger(0);

    private long startIp;
    private long endIp;

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
        display.getChildren().addAll(new Label("扫描结果:"), taDisplay);
        VBox.setVgrow(taDisplay, Priority.ALWAYS);
        mainPane.setCenter(display);

//        btnScan.setOnAction(event -> {
//            String startIp = tfStartIp.getText();
//            String endIp = tfEndIp.getText();
//
//            int startIpInt = IpUtils.ipToInt(startIp);
//            int endIpInt = IpUtils.ipToInt(endIp);
//
//            Thread scanThread = new Thread(threadGroup, () -> {
////                System.out.println("Scan start!");
//                for (int i = startIpInt; i <= endIpInt; i++) {
//                    if (Thread.currentThread().isInterrupted()) {
//                        break;
//                    }
////                    System.out.println("Scanning "+ IpUtils.intToIp(i));
//                    try {
//                        boolean res = isReachable(IpUtils.intToIp(i));
//                        if (!res) {
//                            taDisplay.appendText(IpUtils.intToIp(i) + " is not reachable.\n");
//                        } else {
//                            taDisplay.appendText(IpUtils.intToIp(i) + " is reachable!\n");
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, "scanThread");
//            scanThread.start();
//        });

        btnScan.setOnAction(event -> {
            this.startIp = IpUtils.ipToLong(tfStartIp.getText());
            this.endIp = IpUtils.ipToLong(tfEndIp.getText());

            int thread = 16;
            hostCount.set(0);
            for (int i = 0; i < thread; i++) {
                ScanHandler scanHandler = new ScanHandler(i, thread);
                new Thread(threadGroup, scanHandler, "MultiThread" + i).start();
            }
        });

        btnExecute.setOnAction(event -> {
            Thread scanThread = new Thread(threadGroup, () -> {
                try {
                    String cmd = tfCmd.getText();
                    Process process = Runtime.getRuntime().exec(cmd);
                    InputStream in = process.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(in, "gbk"));
                    String msg;
                    while ((msg = br.readLine()) != null) {
                        String msgTemp = msg;
                        Platform.runLater(() -> {
                            taDisplay.appendText(msgTemp + "\n");
                        });
                    }
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }, "scanThread");
            scanThread.start();
        });

        btnStop.setOnAction(event -> {
            threadGroup.interrupt();
        });

        btnExit.setOnAction(event -> {
            threadGroup.interrupt();
            System.exit(0);
        });

        HBox controls = new HBox();
        controls.setSpacing(10);
        controls.setPadding(new Insets(10, 20, 10, 20));
        controls.setAlignment(Pos.CENTER);
        controls.getChildren().addAll(new Label("起始地址："), tfStartIp,
                new Label("结束地址："), tfEndIp, btnScan);

        HBox cmd = new HBox();
        cmd.setSpacing(10);
        cmd.setPadding(new Insets(10, 20, 10, 20));
        cmd.setAlignment(Pos.CENTER);
        cmd.getChildren().addAll(new Label("输入命令格式："), tfCmd, btnExecute, btnStop, btnExit);

        VBox vCmd = new VBox();
        vCmd.setAlignment(Pos.CENTER);
        vCmd.setPrefWidth(500);
        vCmd.getChildren().addAll(controls, cmd);
        mainPane.setBottom(vCmd);


        Scene scene = new Scene(mainPane, 700, 400);

        primaryStage.setOnCloseRequest(event -> {
            threadGroup.interrupt();
            System.exit(0);
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public boolean isReachable(String host) throws IOException {
        int timeOut = 100;
        InetAddress address = InetAddress.getByName(host);
        return address.isReachable(timeOut);
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
            for (long host = startIp + threadNo; host <= endIp; host = host + totalThreadNum) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("interrupted!");
                    break;
                }
                try {
                    boolean res = isReachable(IpUtils.longToIp(host));
                    if (res) {
                        long finalHost1 = host;
                        Platform.runLater(()->{
                            taDisplay.appendText(IpUtils.longToIp(finalHost1) + " is reachable.\n");
                            try {
                                Socket socket = new Socket();
                                socket.connect(new InetSocketAddress(String.valueOf(finalHost1), 6060), 200);
                                socket.close();
                                String msg = IpUtils.longToIp(finalHost1) + "的端口6060开放了\n";
                                Platform.runLater(() -> {
                                    taDisplay.appendText(msg);
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                hostCount.incrementAndGet();
            }
            if (hostCount.get() == (endIp - startIp +1)) {
                hostCount.incrementAndGet();
                Platform.runLater(() -> {
                    taDisplay.appendText("扫描完毕");
                });
            }
        }
    }
}
