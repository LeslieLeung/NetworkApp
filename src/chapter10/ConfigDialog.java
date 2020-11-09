package chapter10;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/11/9
 */
public class ConfigDialog {
    private JpcapCaptor jpcapCaptor;
    private NetworkInterface[] devices = JpcapCaptor.getDeviceList();
    private Stage stage = new Stage();

    public ConfigDialog(Stage parentStage) {
        stage.initOwner(parentStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false);
        stage.setTitle("选择网卡并设置参数");

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER_LEFT);
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 20 ,10 ,20));

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setMaxWidth(800);
        for (int i = 0; i < devices.length; i++) {
            comboBox.getItems().add(i + " :  " + devices[i].description);
        }
        comboBox.getSelectionModel().selectFirst();

        TextField tfFilter = new TextField();
        TextField tfSize = new TextField("1514");
        CheckBox checkBox = new CheckBox("是否设置为混杂模式");
        checkBox.setSelected(true);

        HBox hBoxBottom = new HBox();
        hBoxBottom.setAlignment(Pos.CENTER_RIGHT);

        Button btnConfirm = new Button("确定");
        Button btnCancel = new Button("取消");
        hBoxBottom.getChildren().addAll(btnConfirm, btnCancel);

        Hyperlink link = new Hyperlink("设置抓包过滤器（例如 ip and tcp）：");
        String url = "https://cosy.univ-reims.fr/~lsteffenel/cours/Master1/Reseaux/0910/captureFilters.htm";
        link.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
        vBox.getChildren().addAll(new Label("请选择网卡："), comboBox,
                link, tfFilter,
                new Label("设置抓包大小（建议介于68~1514之间）："), tfSize, checkBox,
                new Separator(), hBoxBottom);

        Scene scene = new Scene(vBox);
        stage.setScene(scene);

        btnConfirm.setOnAction(event -> {
            try {
                int index = comboBox.getSelectionModel().getSelectedIndex();
                //选择的网卡接口
                NetworkInterface networkInterface = devices[index];
                //抓包大小
                int snapLen = Integer.parseInt(tfSize.getText().trim());
                //是否混杂模式
                boolean promisc = checkBox.isSelected();
                jpcapCaptor = JpcapCaptor.openDevice(networkInterface, snapLen,
                        promisc, 20);
                jpcapCaptor.setFilter(tfFilter.getText().trim(), true);
                stage.hide();

            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            }
        });

        //取消按钮动作事件
        btnCancel.setOnAction(event -> {
            stage.hide();
        });
    }

    //主程序调用，获取设置了参数的JpcapCaptor对象
    public JpcapCaptor getJpcapCaptor() {
        return null;
    }

    //主程序调用，阻塞式显示界面
    public void showAndWait() {
        stage.showAndWait();
    }
}

