package ui;

import cn.hutool.core.lang.Pair;
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
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
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
import javafx.util.Callback;
import lombok.Getter;

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

    @Getter
    private IDataBean originalData;

    private MainUi mainUi;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    //增加数据
    public void setDataModel(MainUi mainUi, BeanDefine beanDefine) {
        this.mainUi = mainUi;
        this.beanDefine = beanDefine;
        startShow();
    }

    //修改数据
    public void changeData(MainUi mainUi, IDataBean originalData) {
        this.mainUi = mainUi;
        this.originalData = originalData;
        this.beanDefine = originalData.getDefine();
        startShow();
    }

    private void startShow() {
        showRoot();
        save.setOnMouseClicked(event -> {
            //保存数据入口
            //记录每个输入组件对应的字段类型bean field
            try {
                IData data = new IBean(beanDefine).convert(rootGridPane);
                mainUi.addOriginalData((IDataBean) data);
            } catch (NumberFormatException exception) {
                showException("字符串转数字失败 " + exception.getMessage());
            } catch (RuntimeException exception) {
                showException(exception.getMessage());
            }

        });
    }

    private void showException(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误提示");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

    private Node createValue(IType valueType, IData valueData, String ref) {
        Node result;
        if (valueType instanceof IBean) {
            IBean dataBean = (IBean) valueType;
            result = createBeanPane(dataBean, (IDataBean) valueData);
        } else if (valueType instanceof IMap) {
            IMap dataMap = (IMap) valueType;
            result = createMapPane(dataMap, (IDataMap) valueData, ref);
        } else if (valueType instanceof IList) {
            IList dataList = (IList) valueType;
            result = createListPane(dataList, (IDataList) valueData, ref);
        } else {
            if (ref == null) {
                var textField = new javafx.scene.control.TextField();
                if (valueData != null) {
                    textField.setText(valueData.toString());
                }
                result = textField;
            } else {
                var refDefine = Context.getIns().getTables().get(ref);
                var comboBox = new ComboBox<String>();
                comboBox.setCellFactory(new Callback<>() {
                    @Override
                    public ListCell<String> call(ListView<String> param) {
                        return new ListCell<>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (item != null) {
                                    setText(refDefine.getRecordByIndexString(item));
                                }
                            }
                        };
                    }
                });
                List<String> temp = refDefine.getRecords().stream().map(e -> BeanDefine.getDataIndexString((IDataBean) e).toString()).collect(Collectors.toList());
                comboBox.getItems().addAll(temp);
                comboBox.setVisibleRowCount(6);
                comboBox.setEditable(true);
                if (valueData != null) {
                    comboBox.getEditor().setText(valueData.toString());
                }
                result = comboBox;
            }
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
            choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> setBeanField(beanType.getBeanDefine(),
                newValue, gridPane, null));
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
            clearField(container);
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
            Node fieldValue = createValue(field.getRunType(), fieldData, field.getRef());
            GridPane.setHalignment(fieldValue, HPos.LEFT);
            container.add(fieldValue, 1, i + 1);
            markField(container, field, fieldValue);
        }
    }

    private Node createMapPane(IMap map, IDataMap mapValue, String ref) {
        GridPane gridPane = new GridPane();
        TitledPane titledPane = new TitledPane("", gridPane);
        titledPane.setExpanded(false);
        Button button = new Button("增加");
        if (mapValue != null) {
            for (Map.Entry<IData, IData> entry : mapValue.getValues().entrySet()) {
                addEntry(gridPane, map.getKey(), entry.getKey(), map.getValue(), entry.getValue(), ref);
            }
        }
        button.setOnMouseClicked(event -> {
            addEntry(gridPane, map.getKey(), null, map.getValue(), null, ref);
        });
        gridPane.add(button, 0, 0);
        gridPane.getColumnConstraints().add(0, new ColumnConstraints(40));
        return titledPane;
    }

    private void addEntry(GridPane gridPane, IType keyType, IData keyValue, IType valueType, IData valueValue, String ref) {
        int rowIndex = gridPane.getRowCount();
        Label keyLabel = new Label("key");
        GridPane.setHalignment(keyLabel, HPos.LEFT);
        //keyLabel.setPadding(new Insets(0, 3, 0, 5));
        gridPane.add(keyLabel, 0, rowIndex + 1);
        var keyNode = createValue(keyType, keyValue, null);
        gridPane.add(keyNode, 1, rowIndex + 1);
        GridPane.setHalignment(keyNode, HPos.LEFT);
        var valueLabel = new Label("value");
        GridPane.setHalignment(valueLabel, HPos.LEFT);
        //valueLabel.setPadding(new Insets(0, 3, 0, 5));
        gridPane.add(valueLabel, 0, rowIndex + 2);
        var valueNode = createValue(valueType, valueValue, valueType.canBeMapKey() ? ref : null);
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


    private Node createListPane(IList listType, IDataList listValue, String ref) {
        GridPane gridPane = new GridPane();
        TitledPane titledPane = new TitledPane("", gridPane);
        titledPane.setExpanded(false);
        Button button = new Button("增加");
        if (listValue != null) {
            for (IData value : listValue.getValues()) {
                addListValue(gridPane, listType.getValueType(), value, ref);
            }
        }
        button.setOnMouseClicked(event -> {
            addListValue(gridPane, listType.getValueType(), null, ref);
        });
        gridPane.add(button, 0, 0);
        return titledPane;
    }

    private void addListValue(GridPane gridPane, IType valueType, IData valueData, String ref) {
        var node = createValue(valueType, valueData, valueType.canBeMapKey() ? ref : null);
        int rowIndex = gridPane.getRowCount();
        gridPane.add(node, 0, rowIndex + 1);
        Button button1 = new Button("删除");
        gridPane.add(button1, 1, rowIndex + 1);
        button1.setOnMouseClicked(event -> {
            removeList(gridPane, node);
            gridPane.getChildren().removeAll(node, button1);
        });
        markList(gridPane, node);
    }

    private void clearField(Node parent) {
        parent.getProperties().remove("fields");
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
