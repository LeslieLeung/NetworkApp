package chapter01;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;

public class SimpleFX extends Application {

    private Button btnExit = new Button("退出");
    private Button btnSend = new Button("发送");
    private Button btnOpen = new Button("加载");
    private Button btnSave = new Button("保存");

    private TextField tfSend = new TextField();
    private TextArea taDisplay = new TextArea();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
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

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 20, 10, 20));
        hBox.setAlignment(Pos.CENTER_RIGHT);

        // 按钮事件绑定
        btnExit.setOnAction(event -> {
            System.exit(0);
        });
        btnSend.setOnAction(event -> {
            String msg = tfSend.getText();
            taDisplay.appendText(msg + "\n");
            tfSend.clear();
        });
        // 实例化TextFileIO
        TextFileIO textFileIO = new TextFileIO();
        btnSave.setOnAction(event -> {
            textFileIO.append(
                    LocalDateTime.now().withNano(0) + " " + taDisplay.getText()
            );
        });
        btnOpen.setOnAction(event -> {
            String msg = textFileIO.load();
            if (msg != null) {
                taDisplay.clear();
                taDisplay.setText(msg);
            }
        });


        hBox.getChildren().addAll(btnSend, btnSave, btnOpen, btnExit);
        mainPane.setBottom(hBox);
        Scene scene = new Scene(mainPane, 700, 400);

        // 回车响应功能
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination keyCombination = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHIFT_DOWN);

            @Override
            public void handle(KeyEvent event) {
                if (keyCombination.match(event)) {
                    String msg = tfSend.getText();
                    taDisplay.appendText("echo:" + msg + "\n");
                    tfSend.clear();
                } else if (event.getCode() == KeyCode.ENTER) {
                    String msg = tfSend.getText();
                    taDisplay.appendText(msg + "\n");
                    tfSend.clear();
                }
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
