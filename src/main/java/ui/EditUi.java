package ui;

import cn.hutool.core.lang.Pair;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import define.BeanDefine;
import define.column.BeanField;
import define.data.type.IData;
import define.data.type.IDataBean;
import define.data.type.IDataList;
import define.data.type.IDataMap;
import define.type.IBean;
import define.type.IList;
import define.type.IMap;
import define.type.IType;
import generator.Context;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

/**
 * 编辑界面
 *
 * <p>
 * create by xiongjieqing on 2021/8/8 4:04 下午
 */
public class EditUi implements Initializable {

    @FXML
    private GridPane rootGridPane;

    @FXML
    private Button save;

    private BeanDefine beanDefine;

    private IDataBean originalData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    //增加数据
    public void setDataModel(BeanDefine beanDefine) {
        this.beanDefine = beanDefine;
        startShow();
    }

    //修改数据
    public void changeData(IDataBean originalData) {
        this.originalData = originalData;
        this.beanDefine = originalData.getDefine();
        startShow();
    }

    private void startShow() {
        showRoot();
        save.setOnMouseClicked(event -> {
            //保存数据入口
            //记录每个输入组件对应的字段类型bean field
            IData data = new IBean(beanDefine).convert(rootGridPane);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonElement jsonObject = data.save();
            String text = gson.toJson(jsonObject);
            try {
                Files.writeString(new File("test.json").toPath(), text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void showRoot() {
        if (beanDefine.isDynamic()) {
            javafx.scene.control.Label label = new javafx.scene.control.Label("subType");
            GridPane.setHalignment(label, HPos.LEFT);
            label.setPadding(new Insets(0, 3, 0, 5));
            rootGridPane.add(label, 0, 0);
            List<String> children = new ArrayList<>();
            for (BeanDefine leafChild : beanDefine.getLeafChildren()) {
                children.add(leafChild.getName());
            }
            ChoiceBox<String> choiceBox = new ChoiceBox<>(FXCollections.observableList(children));
            choiceBox.setValue("请选择类型");
            rootGridPane.add(choiceBox, 1, 0);
            if (originalData != null) {
                String actualName = originalData.getActual().getName();
                choiceBox.getSelectionModel().select(actualName);
                setBeanField(originalData.getDefine(), actualName, rootGridPane, originalData);
            }
            choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> setBeanField(beanDefine,
                newValue, rootGridPane, null));
            GridPane.setHalignment(choiceBox, HPos.LEFT);
        } else {
            setBeanField(beanDefine, null, rootGridPane, originalData);
        }
    }

    private Node createValue(IType valueType, IData valueData) {
        Node result;
        if (valueType instanceof IBean) {
            IBean dataBean = (IBean) valueType;
            result = createBeanPane(dataBean, (IDataBean) valueData);
        } else if (valueType instanceof IMap) {
            IMap dataMap = (IMap) valueType;
            result = createMapPane(dataMap, (IDataMap) valueData);
        } else if (valueType instanceof IList) {
            IList dataList = (IList) valueType;
            result = createListPane(dataList, (IDataList) valueData);
        } else {
            var textField = new javafx.scene.control.TextField();
            if (valueData != null) {
                textField.setText(valueData.toString());
            }
            result = textField;
        }
        return result;
    }

    private Node createBeanPane(IBean beanType, IDataBean beanValue) {
        GridPane gridPane = new GridPane();
        TitledPane titledPane = new TitledPane("", gridPane);
        titledPane.setExpanded(false);
        if (beanType.getBeanDefine().isDynamic()) {
            List<String> children = new ArrayList<>();
            for (BeanDefine leafChild : beanType.getBeanDefine().getLeafChildren()) {
                children.add(leafChild.getName());
            }
            ChoiceBox<String> choiceBox = new ChoiceBox<>();
            choiceBox.setTooltip(new Tooltip("选择子类型"));
            choiceBox.getItems().addAll(children);
            gridPane.add(choiceBox, 0, 0);
            if (beanValue != null) {
                String actualName = beanValue.getActual().getName();
                choiceBox.getSelectionModel().select(actualName);
                setBeanField(beanType.getBeanDefine(), actualName, gridPane, beanValue);
            }
            choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> setBeanField(beanType.getBeanDefine(), newValue, gridPane, null));
        } else {
            setBeanField(beanType.getBeanDefine(), null, gridPane, beanValue);
        }
        return titledPane;
    }

    private void setBeanField(BeanDefine parent, String child, GridPane container, IDataBean originalData) {
        BeanDefine actual = parent;
        if (child != null) {
            actual = Context.getIns().getBean(parent.getPackageName() + "." + child);
            container.getChildren().removeIf(e -> GridPane.getRowIndex(e) != 0);
            //记录选择的子类型
            container.getProperties().put("subType", actual);
        }
        for (int i = 0; i < actual.getAllFields().size(); i++) {
            var field = actual.getAllFields().get(i);
            String name = field.getName();
            javafx.scene.control.Label label = new javafx.scene.control.Label(name + "(" + field.getRunType().getTypeName() + ")");
            GridPane.setHalignment(label, HPos.LEFT);
            label.setPadding(new Insets(0, 3, 0, 5));
            label.setTooltip(new Tooltip(field.getComment()));
            container.add(label, 0, i + 1);
            IData fieldData;
            if (originalData != null) {
                fieldData = originalData.getDataByFieldName(name);
            } else {
                fieldData = null;
            }
            Node fieldValue = createValue(field.getRunType(), fieldData);
            GridPane.setHalignment(fieldValue, HPos.LEFT);
            container.add(fieldValue, 1, i + 1);
            markField(container, field, fieldValue);
        }
    }

    private Node createMapPane(IMap map, IDataMap mapValue) {
        GridPane gridPane = new GridPane();
        TitledPane titledPane = new TitledPane("", gridPane);
        titledPane.setExpanded(false);
        Button button = new Button("增加");
        if (mapValue != null) {
            for (Map.Entry<IData, IData> entry : mapValue.getValues().entrySet()) {
                addEntry(gridPane, map.getKey(), entry.getKey(), map.getValue(), entry.getValue());
            }
        }
        button.setOnMouseClicked(event -> {
            addEntry(gridPane, map.getKey(), null, map.getValue(), null);
        });
        gridPane.add(button, 0, 0);
        gridPane.getColumnConstraints().add(0, new ColumnConstraints(40));
        return titledPane;
    }

    private void addEntry(GridPane gridPane, IType keyType, IData keyValue, IType valueType, IData valueValue) {
        int rowIndex = gridPane.getRowCount();
        Label keyLabel = new Label("key");
        GridPane.setHalignment(keyLabel, HPos.LEFT);
        //keyLabel.setPadding(new Insets(0, 3, 0, 5));
        gridPane.add(keyLabel, 0, rowIndex + 1);
        var keyNode = createValue(keyType, keyValue);
        gridPane.add(keyNode, 1, rowIndex + 1);
        GridPane.setHalignment(keyNode, HPos.LEFT);
        var valueLabel = new Label("value");
        GridPane.setHalignment(valueLabel, HPos.LEFT);
        //valueLabel.setPadding(new Insets(0, 3, 0, 5));
        gridPane.add(valueLabel, 0, rowIndex + 2);
        var valueNode = createValue(valueType, valueValue);
        gridPane.add(valueNode, 1, rowIndex + 2);
        GridPane.setHalignment(valueNode, HPos.LEFT);
        Button button1 = new Button("删除");
        gridPane.add(button1, 2, rowIndex + 1);
        button1.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                removeMapEntry(gridPane, keyNode, valueNode);
                gridPane.getChildren().removeAll(keyLabel, keyNode, valueLabel, valueNode, button1);
            }
        });
        markMapEntry(gridPane, keyNode, valueNode);
    }


    private Node createListPane(IList listType, IDataList listValue) {
        GridPane gridPane = new GridPane();
        TitledPane titledPane = new TitledPane("", gridPane);
        titledPane.setExpanded(false);
        Button button = new Button("增加");
        if (listValue != null) {
            for (IData value : listValue.getValues()) {
                addListValue(gridPane, listType.getValueType(), value);
            }
        }
        button.setOnMouseClicked(event -> {
            addListValue(gridPane, listType.getValueType(), null);
        });
        gridPane.add(button, 0, 0);
        return titledPane;
    }

    private void addListValue(GridPane gridPane, IType valueType, IData valueData) {
        var node = createValue(valueType, valueData);
        int rowIndex = gridPane.getRowCount();
        gridPane.add(node, 0, rowIndex + 1);
        Button button1 = new Button("删除");
        gridPane.add(button1, 1, rowIndex + 1);
        button1.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                removeList(gridPane, node);
                gridPane.getChildren().removeAll(node, button1);
            }
        });
        markList(gridPane, node);
    }

    private void markField(Node parent, BeanField field, Node child) {
        var lists = parent.getProperties().computeIfAbsent("fields", k -> new ArrayList<Node>());
        ((ArrayList) lists).add(new Pair<>(field, child));
    }

    private void markList(Node parent, Node child) {
        var lists = parent.getProperties().computeIfAbsent("list", k -> new ArrayList<Node>());
        ((ArrayList) lists).add(child);
    }

    private void removeList(Node parent, Node child) {
        var lists = parent.getProperties().get("list");
        ((ArrayList) lists).remove(child);
    }

    private void markMapEntry(Node parent, Node key, Node value) {
        var map = parent.getProperties().computeIfAbsent("map", k -> new HashMap<Node, Node>());
        ((Map) map).put(key, value);
    }

    private void removeMapEntry(Node parent, Node key, Node value) {
        var map = parent.getProperties().get("map");
        ((Map) map).remove(key, value);
    }

}
