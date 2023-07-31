package ${rootPackage}

import(
    "game/datastream"
)

/**
* 解析工具类
*/
<#list name2Type?values as iType>
func ${iType.getGoUnmarshalMethodName()}(os *datastream.Octets) ${getGoReturnType(iType)} {
    ${iType.getGoExtUnmarshal()}
}

</#list>
