package extension;

import(
"container/list"
<#list getAllImport() as temp>
    "${temp}"
</#list>
)

/**
* 解析工具类
*/
<#list name2Type?values as iType>
    func ${iType.getGoUnmarshalMethodName()}(datastream.Octets os) ${iType.getGoType()} {
    ${iType.getGoExtUnmarshal()}
    }
</#list>
