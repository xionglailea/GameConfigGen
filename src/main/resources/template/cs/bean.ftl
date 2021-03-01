using System;
/**
* ${comment}
*/
namespace ${packageName}
{
<#if hasParent == true && dynamic == true>
    public abstract class ${name} : ${parent.fullName}
    {
<#elseif hasParent == false && dynamic == true>
    public abstract class ${name}
    {
<#elseif hasParent == true && dynamic == false>
    public sealed class ${name} : ${parent.fullName}
    {
<#else>
    public sealed class ${name}
    {
</#if>

<#list fields as field>
    <#if field.canExport() == true>
        private ${field.runType.getCsType()} ${field.name} {get;}; //${field.comment}
    </#if>
</#list>

<#if hasParent == true>
        public ${name}(datastream.Octets os) : base(os)
<#else>
        public ${name}(datastream.Octets os)
</#if>
        {
<#list fields as field>
        <#if field.canExport() == true>
            ${field.name} = ${field.runType.getUnmarshal()};
        </#if>
</#list>
        }
<#list fields as field>
    <#if field.canExport() == true>
    <#if field.hasRef()>
        <#assign typeName = field.runType.getTypeName()>
        <#if typeName == "list">
        <#else>
        public ${field.getRefType()} get${field.name?cap_first}Ref()
        {
            return cfg.CfgMgr.ins.get${field.ref?cap_first}Map().get(${field.name});
        }
        </#if>
    </#if>
    </#if>
</#list>
}
}