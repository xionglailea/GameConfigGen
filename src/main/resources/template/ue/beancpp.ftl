#include "${getRootPkg()}/${moduleName}/${name}.h"
<#if getUeNeedIncludeExtensions() == true>
#include "${getRootPkg()}/Extensions.h"
</#if>

<#if hasParent == true>
${getRootPkg()}::${moduleName}::${name}::${name}(FOctets* os) : ${parent.name}(os)
{
<#else>
${getRootPkg()}::${moduleName}::${name}::${name}(FOctets* os)
{
</#if>
<#list fields as field>
<#if field.canExport() == true>
    ${field.name} = ${field.runType.getUeUnmarshal()};
</#if>
</#list>
}

