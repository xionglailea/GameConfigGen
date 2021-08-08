package ${packageName};
import lombok.Getter;

/**
* ${comment!""}
*/
@Getter
<#if hasParent == true && dynamic == true>
public abstract class ${name} extends ${parent.fullName} {
<#elseif hasParent == false && dynamic == true>
public abstract class ${name} {
<#elseif hasParent == true && dynamic == false>
public final class ${name} extends ${parent.fullName} {
<#else>
public final class ${name} {
</#if>

<#list fields as field>
    <#if field.canExport() == true>
    private final ${field.runType.getJavaType()} ${field.name}; //${field.comment}
    </#if>
</#list>

    public ${name}(datastream.Octets os) {
<#if hasParent == true>
        super(os);
</#if>
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
    public ${field.getRefType()} get${field.name?cap_first}Ref() {
        return cfg.CfgMgr.ins.get${field.ref?cap_first}Map().get(${field.name});
    }
        </#if>
    </#if>
    </#if>
</#list>
}