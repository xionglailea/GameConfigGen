package ui;

import cn.hutool.core.lang.Pair;
import define.BeanDefine;
import define.column.BeanField;
import define.data.type.IData;
import define.data.type.IDataBean;
import define.type.IBean;
import define.type.IList;
import define.type.IMap;
import define.type.IType;
import generator.Context;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.*;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setDataModel(BeanDefine beanDefine) {
        this.beanDefine = beanDefine;
        showRoot();
        save.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                IData data = saveBean(rootGridPane, beanDefine);
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
            choiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    setBeanField(beanDefine, newValue, rootGridPane);
                }
            });
            GridPane.setHalignment(choiceBox, HPos.LEFT);
            rootGridPane.add(choiceBox, 1, 0);
        } else {
            setBeanField(beanDefine, null, rootGridPane);
        }
    }

    private Node createValue(IType data) {
        Node result;
        if (data instanceof IBean) {
            IBean dataBean = (IBean) data;
            result = createBeanPane(dataBean);
        } else if (data instanceof IMap) {
            IMap dataMap = (IMap) data;
            result = createMapPane(dataMap);
        } else if (data instanceof IList) {
            IList dataList = (IList) data;
            result = createListPane(dataList);
        } else {
            result = new javafx.scene.control.TextField();
        }
        return result;
    }

    private Node createBeanPane(IBean dataBean) {
        GridPane gridPane = new GridPane();
        TitledPane titledPane = new TitledPane("", gridPane);
        titledPane.setExpanded(false);
        if (dataBean.getBeanDefine().isDynamic()) {
            List<String> children = new ArrayList<>();
            for (BeanDefine leafChild : dataBean.getBeanDefine().getLeafChildren()) {
                children.add(leafChild.getName());
            }
            ChoiceBox<String> choiceBox = new ChoiceBox<>();
            choiceBox.setTooltip(new Tooltip("选择子类型"));
            choiceBox.getItems().addAll(children);
            choiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    setBeanField(dataBean.getBeanDefine(), newValue, gridPane);
                }
            });
            gridPane.add(choiceBox, 0, 0);
        } else {
            setBeanField(dataBean.getBeanDefine(), null, gridPane);
        }
        return titledPane;
    }

    private void setBeanField(BeanDefine parent, String child, GridPane container) {
        BeanDefine actual = parent;
        if (child != null) {
            actual = Context.getIns().getBean(parent.getPackageName() + "." + child);
            container.getChildren().removeIf(e -> GridPane.getRowIndex(e) != 0);
            container.getProperties().put("subType", actual);
        }
        for (int i = 0; i < actual.getAllFields().size(); i++) {
            var field = actual.getAllFields().get(i);
            String name = field.getName();
            javafx.scene.control.Label label = new javafx.scene.control.Label(name);
            GridPane.setHalignment(label, HPos.LEFT);
            label.setPadding(new Insets(0, 3, 0, 5));
            label.setTooltip(new Tooltip(field.getComment()));
            container.add(label, 0, i + 1);
            Node fieldValue = createValue(field.getRunType());
            GridPane.setHalignment(fieldValue, HPos.LEFT);
            container.add(fieldValue, 1, i + 1);
            markField(container, field, fieldValue);
        }
    }

    private Node createMapPane(IMap map) {
        GridPane gridPane = new GridPane();
        TitledPane titledPane = new TitledPane("", gridPane);
        titledPane.setExpanded(false);
        Button button = new Button("增加");
        button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int rowIndex = gridPane.getRowCount();

                javafx.scene.control.Label keyLabel = new javafx.scene.control.Label("key");
                GridPane.setHalignment(keyLabel, HPos.LEFT);
                keyLabel.setPadding(new Insets(0, 3, 0, 5));
                gridPane.add(keyLabel, 0, rowIndex + 1);
                var keyNode = createValue(map.getKey());
                gridPane.add(keyNode, 1, rowIndex + 1);
                GridPane.setHalignment(keyNode, HPos.LEFT);
                var valueLabel = new Label("value");
                GridPane.setHalignment(valueLabel, HPos.LEFT);
                valueLabel.setPadding(new Insets(0, 3, 0, 5));
                gridPane.add(valueLabel, 0, rowIndex + 2);
                var valueNode = createValue(map.getValue());
                gridPane.add(valueNode, 1, rowIndex + 2);
                GridPane.setHalignment(valueNode, HPos.LEFT);
                Button button1 = new Button("删除");
                gridPane.add(button1, 3, rowIndex + 1);
                button1.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        removeMapEntry(gridPane, keyNode, valueNode);
                        gridPane.getChildren().removeAll(keyLabel, keyNode, valueLabel, valueNode, button1);
                    }
                });
                markMapEntry(gridPane, keyNode, valueNode);
            }
        });
        gridPane.add(button, 0, 0);
        return titledPane;
    }

    private Node createListPane(IList list) {
        GridPane gridPane = new GridPane();
        TitledPane titledPane = new TitledPane("", gridPane);
        titledPane.setExpanded(false);
        Button button = new Button("增加");
        button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                var node = createValue(list.getValueType());
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
        });
        gridPane.add(button, 0, 0);
        return titledPane;
    }

    private void markField(Node parent, BeanField field, Node child) {
        var lists = parent.getProperties().computeIfAbsent("fields", k -> new ArrayList<Node>());
        ((ArrayList)lists).add(new Pair<>(field, child));
    }

    private void markList(Node parent, Node child) {
        var lists = parent.getProperties().computeIfAbsent("list", k -> new ArrayList<Node>());
        ((ArrayList)lists).add(child);
    }

    private void removeList(Node parent, Node child) {
        var lists = parent.getProperties().get("list");
        ((ArrayList)lists).remove(child);
    }

    private void markMapEntry(Node parent, Node key, Node value) {
        var map = parent.getProperties().computeIfAbsent("map", k -> new HashMap<Node, Node>());
        ((Map)map).put(key, value);
    }

    private void removeMapEntry(Node parent, Node key, Node value) {
        var map = parent.getProperties().get("map");
        ((Map)map).remove(key, value);
    }


    private IData saveBean(GridPane gridPane, BeanDefine beanDefine) {
       ArrayList<Pair<BeanField, Node>> allFields = (ArrayList<Pair<BeanField, Node>>) rootGridPane.getProperties().get("fields");
       BeanDefine actual = beanDefine;
       var subType = rootGridPane.getProperties().get("subType");
       if (beanDefine.isDynamic() && subType == null) {
           //没有选择具体的数据类型，得报错
           return null;
       }
       if (subType != null) {
           actual = (BeanDefine) subType;
       }
       List<IData> data = new ArrayList<>();
        for (Pair<BeanField, Node> entry : allFields) {
            var field = entry.getKey();
            var value = entry.getValue();
            data.add(saveField(value, field));
        }
        return new IDataBean(beanDefine, actual, data);
    }

    private IData saveField(Node node, BeanField beanField) {

    }

    private void saveList() {

    }

    private void saveMap() {

    }


}
