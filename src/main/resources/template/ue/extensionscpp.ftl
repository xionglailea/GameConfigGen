#include "${rootPackage}/Extensions.h"
<#list getUeExtensionInclude() as temp>
${temp}
</#list>

<#list name2Type?values as iType>
${iType.getUeType()} ${rootPackage}::Extensions::${iType.getUeUnmarshalMethodName()}(FOctets* os)
{
    ${iType.getUeExtUnmarshal()}
}

</#list>




