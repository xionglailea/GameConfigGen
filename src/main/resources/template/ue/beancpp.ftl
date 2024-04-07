#include "cfg/${moduleName}/${name}.h"
<#if getUeNeedIncludeExtensions() == true>
#include "cfg/Extensions.h"
</#if>

<#if hasParent == true>
cfg::${moduleName}::${name}::${name}(FOctets* os) : ${parent.name}(os)
{
<#else>
cfg::${moduleName}::${name}::${name}(FOctets* os)
{
</#if>
<#list fields as field>
<#if field.canExport() == true>
    ${field.name} = ${field.runType.getUeUnmarshal()};
</#if>
</#list>
}

