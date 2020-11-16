package chapter11;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jpcap.JpcapSender;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/11/16
 */
public class SendPacketFX extends Application {
    private CheckBox cbSYN = new CheckBox("SYN");
    private CheckBox cbACK = new CheckBox("ACK");
    private CheckBox cbRST = new CheckBox("RST");
    private CheckBox cbFIN = new CheckBox("FIN");
    private TextField tfSrcPort = new TextField();
    private TextField tfDstPort = new TextField();
    private TextField tfSrcHost = new TextField();
    private TextField tfDstHost = new TextField();
    private TextField tfSrcMAC = new TextField();
    private TextField tfDstMAC = new TextField();
    private TextField tfData = new TextField();

    private Button btnSend = new Button("发送TCP包");
    private Button btnSetting = new Button("选择网卡");
    private Button btnExit = new Button("退出");

    private NetworkChoiceDialog dialog;
    private JpcapSender sender;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane mainPain = new BorderPane();

        HBox ports = new HBox();
        ports.setSpacing(10);
        ports.setPadding(new Insets(10, 20, 10, 20));
        ports.getChildren().addAll(new Label("源端口："), tfSrcPort,
                new Label("目的端口："), tfDstPort);
        mainPain.setTop(ports);

        HBox tags = new HBox();
        tags.setSpacing(10);
        tags.setPadding(new Insets(10, 20, 10, 20));
        tags.getChildren().addAll(new Label("TCP标识位"), cbSYN, cbACK, cbRST, cbFIN);

        VBox params = new VBox();
        params.setSpacing(10);
        params.setPadding(new Insets(10, 20, 10, 20));
        params.getChildren().addAll(
                tags,
                new Label("源主机地址"), tfSrcHost,
                new Label("目的主机地址"), tfDstHost,
                new Label("源MAC地址"), tfSrcMAC,
                new Label("目的MAC地址"), tfDstMAC,
                new Label("发送的数据"), tfData);
        mainPain.setCenter(params);

        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        buttons.setPadding(new Insets(10, 20, 10, 20));
        buttons.getChildren().addAll(btnSend, btnSetting, btnExit);
        mainPain.setBottom(buttons);

        btnSend.setOnAction(event -> {
            try {
                int srcPort = Integer.parseInt(tfSrcPort.getText().trim());
                int dstPort = Integer.parseInt((tfDstPort.getText().trim()));
                String srcHost = tfSrcHost.getText().trim();
                String dstHost = tfDstHost.getText().trim();
                String srcMAC = tfSrcMAC.getText().trim();
                String dstMAC = tfDstMAC.getText().trim();
                String data = tfData.getText();
                //调用发包方法
                PacketSender.sendTCPPacket(sender, srcPort, dstPort, srcHost,
                        dstHost, data, srcMAC, dstMAC, cbSYN.isSelected(),
                        cbACK.isSelected(), cbRST.isSelected(), cbFIN.isSelected());

                new Alert(Alert.AlertType.INFORMATION, "已发送！").showAndWait();

            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            }
        });

        btnSetting.setOnAction(event -> {
            if (dialog == null) {
                dialog = new NetworkChoiceDialog(primaryStage);
            }
            dialog.showAndWait();
            sender = dialog.getSender();
        });

        btnExit.setOnAction(event -> {
            exit();
        });

        Scene scene = new Scene(mainPain);
        primaryStage.setScene(scene);
        primaryStage.setTitle("发送自构包");
        primaryStage.setWidth(500);
        dialog = new NetworkChoiceDialog(primaryStage);
        dialog.showAndWait();
        sender = dialog.getSender();
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            exit();
        });

    }

    private void exit() {
        System.exit(0);
    }
}
