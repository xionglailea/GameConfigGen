package define.type;

import cn.hutool.core.lang.Assert;
import com.google.gson.JsonElement;
import define.data.type.IData;
import define.visit.cs.CsExtUnmarshal;
import define.visit.cs.CsUnmarshal;
import define.visit.go.GoExtUnmarshal;
import define.visit.go.GoUnmarshal;
import define.visit.java.ExtUnmarshal;
import define.visit.java.Unmarshal;
import javafx.scene.Node;

import java.util.List;
import java.util.function.Consumer;


/**
 * 类型 支持9种数据类型的定义
 * <p>
 * create by xiongjieqing on 2020-07-24 16:38
 */
public interface IType {
    String getTypeName();

    String getJavaType();

    String getJavaBoxType();

    String getConstValue(String origin);

    default String getUnmarshal() {
        return Unmarshal.INS.accept(this);
    }

    default String getCsUnmarshal() {
        return CsUnmarshal.INS.accept(this);
    }

    default String getGoUnmarshal() {
        return GoUnmarshal.INS.accept(this);
    }

    default String getExtUnmarshal() {
        return ExtUnmarshal.INS.accept(this);
    }

    default String getCsExtUnmarshal() {
        return CsExtUnmarshal.INS.accept(this);
    }

    default String getGoExtUnmarshal() {
        return GoExtUnmarshal.INS.accept(this);
    }

    String getUnmarshalMethodName();

    default String getCsUnmarshalMethodName() {
        return "Unmarshal_" + this.toString();
    }

    default String getGoUnmarshalMethodName() {
        return "Unmarshal_" + this.toString();
    }

    //增加扩展类型，就是通过list和map扩展的
    void addExtensionType(Consumer<IType> consumer);

    /**
     * 读取数据
     *
     * @param values 原始数据
     * @param sep    分隔符
     * @return
     */
    IData convert(List<String> values, String sep);

    IData convert(JsonElement jsonElement);

    IData convert(Node node);

    boolean canBeIndex();

    boolean simpleType();

    //cs 接口

    String getCsType();

    default String replaceRegex(String original) {
        Assert.notNull(original);
        if (original.equals("|")) {
            return "\\" + original;
        } else {
            return original;
        }
    }

    //go 接口
    String getGoType();
}
