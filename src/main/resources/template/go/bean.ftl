package ${getRootPkg()}

<#assign structName = "${name?cap_first}">
<#assign parentName = "">
<#if dynamic == true>
type I${structName} interface {
    <#if hasParent>
    I${(parent.name)?cap_first}
    </#if>
<#list fields as field >
    <#if field.canExport() == true>
    Get${field.name?cap_first}() ${field.runType.getGoType()?replace(moduleName + ".", "")}
    </#if>
</#list>
}

<#else>

type ${structName} struct {
<#list allFields as field>
    <#if field.canExport() == true>
    ${(field.name)?cap_first} ${field.runType.getGoType()?replace(moduleName + ".", "")}  <#if field.comment != "">// ${field.comment}</#if>
    </#if>
</#list>
}

<#if hasParent>
<#list parent.allFields as field >
    <#if field.canExport() == true>
func (i ${structName}) Get${field.name?cap_first}() ${field.runType.getGoType()?replace(moduleName + ".", "")} {
    return i.${field.name?cap_first}
}
    </#if>
</#list>

</#if>

</#if>

