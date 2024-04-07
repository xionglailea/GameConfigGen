#include "cfg/Extensions.h"
<#list getUeExtensionInclude() as temp>
${temp}
</#list>

<#list name2Type?values as iType>
${iType.getUeType()} cfg::Extensions::${iType.getUeUnmarshalMethodName()}(FOctets* os)
{
    ${iType.getUeExtUnmarshal()}
}

</#list>




