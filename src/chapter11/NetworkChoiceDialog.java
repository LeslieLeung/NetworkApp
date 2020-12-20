package chapter11;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/11/16
 */
public class NetworkChoiceDialog {
    private JpcapSender sender;
    private NetworkInterface[] devices = JpcapCaptor.getDeviceList();
    private Stage stage = new Stage();

    public NetworkChoiceDialog(Stage parentStage) {
        stage.initOwner(parentStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false);
        stage.setTitle("选择网卡");

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER_LEFT);
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 20, 10, 20));

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setMaxWidth(800);
        for (int i = 0; i < devices.length; i++) {
            comboBox.getItems().add(i + " :  " + devices[i].description);
        }
        comboBox.getSelectionModel().selectFirst();

        HBox hBoxBottom = new HBox();
        hBoxBottom.setAlignment(Pos.CENTER_RIGHT);

        Button btnConfirm = new Button("确定");
        hBoxBottom.getChildren().addAll(btnConfirm);

        vBox.getChildren().addAll(new Label("请选择网卡："), comboBox,
                new Separator(), hBoxBottom);

        Scene scene = new Scene(vBox);
        stage.setScene(scene);

        btnConfirm.setOnAction(event -> {
            try {
                int index = comboBox.getSelectionModel().getSelectedIndex();
                //选择的网卡接口
                NetworkInterface networkInterface = devices[index];
                sender = JpcapSender.openDevice(networkInterface);
                stage.hide();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            }
        });
    }

    public JpcapSender getSender() {
        return sender;
    }

    //主程序调用，阻塞式显示界面
    public void showAndWait() {
        stage.showAndWait();
    }
}
