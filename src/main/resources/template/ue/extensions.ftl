#pragma once
#include "cfg/FOctets.h"

/**
 * 解析工具类
 */

namespace cfg
{
${getUeForwardDefine()}
    class Extensions
    {
    public:
<#list name2Type?values as iType>
        static ${iType.getUeType()} ${iType.getUeUnmarshalMethodName()}(FOctets* os);
</#list>
    };
}



