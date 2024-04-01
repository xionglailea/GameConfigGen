<#list getTsAllImport() as temp>
${temp}
</#list>

/**
 * 解析工具类
 */
<#list name2Type?values as iType>
export function ${iType.getTsUnmarshalMethodName()}(os: Octets): ${iType.getTsType()} {
    ${iType.getTsExtUnmarshal()}
}
</#list>
