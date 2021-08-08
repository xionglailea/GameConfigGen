package ${rootPackage};

/**
 * 解析工具类
 */
public final class ${extensionFileName} {
<#list name2Type?values as iType>
    public static ${iType.getJavaType()} ${iType.getUnmarshalMethodName()}(datastream.Octets os) {
        ${iType.getExtUnmarshal()}
    }

</#list>
}