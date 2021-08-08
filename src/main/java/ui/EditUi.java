package ui;

import cn.hutool.core.lang.Pair;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import define.BeanDefine;
import define.column.BeanField;
import define.data.type.*;
import define.type.*;
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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
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
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonElement jsonObject = data.save();
                String text = gson.toJson(jsonObject);
                try {
                    Files.writeString(new File("test.json").toPath(), text);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            //记录选择的子类型
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

    //保存数据入口
    //记录每个输入组件对应的字段类型bean field
    private IData saveBean(GridPane gridPane, BeanDefine beanDefine) {
        ArrayList<Pair<BeanField, Node>> allFields = (ArrayList<Pair<BeanField, Node>>) gridPane.getProperties().get("fields");
        BeanDefine actual = beanDefine;
        var subType = gridPane.getProperties().get("subType");
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
            var temp = saveField(value, field.getRunType());
            if (temp != null) {
                data.add(temp);
            }
        }
        return new IDataBean(beanDefine, actual, data);
    }

    private IData saveField(Node node, IType fieldType) {
        var runType = fieldType;
        if (runType instanceof IList) {
            TitledPane titledPane = (TitledPane) node;
            GridPane gridPane = (GridPane) titledPane.getContent();
            ArrayList<Node> values = (ArrayList<Node>) gridPane.getProperties().get("list");
            IDataList iDataList = new IDataList();
            IList temp = (IList) runType;
            for (Node value : values) {
                iDataList.getValues().add(saveField(value, temp.getValueType()));
            }
            return iDataList;
        } else if (runType instanceof IMap) {
            TitledPane titledPane = (TitledPane) node;
            GridPane gridPane = (GridPane) titledPane.getContent();
            Map<Node, Node> nodeMap = (Map<Node, Node>) gridPane.getProperties().get("map");
            IMap temp = (IMap) runType;
            IDataMap iDataMap = new IDataMap();
            for (Map.Entry<Node, Node> nodeNodeEntry : nodeMap.entrySet()) {
                var keyNode = nodeNodeEntry.getKey();
                var valueNode = nodeNodeEntry.getValue();
                iDataMap.getValues().put(saveField(keyNode, temp.getKey()), saveField(valueNode, temp.getValue()));
            }
            return iDataMap;
        } else if (runType instanceof IBean) {
            IBean temp = (IBean) runType;
            TitledPane titledPane = (TitledPane) node;
            return saveBean((GridPane) titledPane.getContent(), temp.getBeanDefine());
        } else {
            TextField textField = (TextField) node;
            String content = textField.getText();
            if (runType instanceof IEnum) {
                IEnum iEnum = (IEnum) runType;
                return new IDataEnum(iEnum.getEnumDefine(), content);
            } else if (runType instanceof IInt) {
                return new IDataInt(Integer.parseInt(content));
            } else if (runType instanceof ILong) {
                return new IDataLong(Long.parseLong(content));
            } else if (runType instanceof IString) {
                return new IDataString(content);
            } else if (runType instanceof IFloat) {
                return new IDataFloat(Float.parseFloat(content));
            }
        }
        return null;
    }


}
