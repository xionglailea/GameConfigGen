using System;
/**
 * 解析工具类
 */

namespace ${rootPackage}
{
    public class ${extensionFileName}
    {
<#list name2Type?values as iType>
        public static ${iType.getCsType()} ${iType.getCsUnmarshalMethodName()}(datastream.Octets os)
        {
            ${iType.getCsExtUnmarshal()}
        }

</#list>
}
}