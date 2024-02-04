package ${rootPackage}

/**
* 解析工具类
*/
<#list name2Type?values as iType>
func ${iType.getGoUnmarshalMethodName()}(os *Octets) ${getGoReturnType(iType)} {
    ${iType.getGoExtUnmarshal()}
}

</#list>
