#pragma once
<#list getUeImportInfo() as temp>
${temp}
</#list>

/**
* ${comment!""}
*/
namespace ${getRootPkg()}
{
    namespace ${moduleName}
    {
    <#if hasParent == true>
        class ${name} : public ${parent.name}
        {
    <#else>
        class ${name}
        {
    </#if>
        public:
    <#list fields as field>
        <#if field.canExport() == true>
            ${field.runType.getUeType()} ${field.name}; //${field.comment}
        </#if>
    </#list>
            ${name}(FOctets* os);
<#--    <#if hasParent == true>-->
<#--            ${name}(FOctets* os) : ${parent.name}(os)-->
<#--            {-->
<#--    <#else>-->
<#--            ${name}(FOctets* os)-->
<#--            {-->
<#--    </#if>-->
<#--    <#list fields as field>-->
<#--        <#if field.canExport() == true>-->
<#--                ${field.name} = ${field.runType.getUeUnmarshal()};-->
<#--        </#if>-->
<#--    </#list>-->

<#--    <#list fields as field>-->
<#--        <#if field.canExport() == true>-->
<#--            <#if field.hasRef()>-->
<#--                <#assign typeName = field.runType.getTypeName()>-->
<#--                <#if typeName == "list">-->
<#--                <#else>-->
<#--                    public get${field.name?cap_first}Ref(): ${field.ref} {-->
<#--                    return ins.${field.ref?lower_case}Map.get(this.${field.name})!;-->
<#--                    }-->
<#--                </#if>-->
<#--            </#if>-->
<#--        </#if>-->
<#--    </#list>-->
        };
    }
}
