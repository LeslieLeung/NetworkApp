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

import java.io.*;
import java.net.InetAddress;

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

    private Button btnScan  = new Button("主机扫描");
    private Button btnExecute = new Button("执行命令");

    private Thread scanThread;

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

        btnScan.setOnAction(event -> {
            String startIp = tfStartIp.getText();
            String endIp = tfEndIp.getText();

            int startIpInt = IpUtils.ipToInt(startIp);
            int endIpInt = IpUtils.ipToInt(endIp);

            scanThread = new Thread(() -> {
//                System.out.println("Scan start!");
                for (int i=startIpInt; i<=endIpInt; i++) {
//                    System.out.println("Scanning "+ IpUtils.intToIp(i));
                    try {
                        boolean res = isReachable(IpUtils.intToIp(i));
                        if (!res) {
                            taDisplay.appendText(IpUtils.intToIp(i) + " is not reachable.\n");
                        } else {
                            taDisplay.appendText(IpUtils.intToIp(i) + " is reachable!\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                scanThread.stop();
            });
            scanThread.start();
        });

        btnExecute.setOnAction(event -> {
            scanThread = new Thread(() -> {
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
                scanThread.stop();
            });
            scanThread.start();
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
        cmd.getChildren().addAll(new Label("输入命令格式："), tfCmd, btnExecute);

        VBox vCmd = new VBox();
        vCmd.setAlignment(Pos.CENTER);
        vCmd.setPrefWidth(500);
        vCmd.getChildren().addAll(controls, cmd);
        mainPane.setBottom(vCmd);


        Scene scene = new Scene(mainPane, 700, 400);

        primaryStage.setOnCloseRequest(event -> {
            scanThread.stop();
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
}
