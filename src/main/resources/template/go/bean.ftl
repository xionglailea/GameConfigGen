package ${moduleName}

//go的结构体要不用全展开的方式 然后定义接口

import(
<#list getGoImportInfo() as temp>
    "${temp}"
</#list>
)

<#assign structName = "${name?cap_first}">
<#assign parentName = "">

<#if dynamic == true>
    type I${structName} interface {
    <#list allFields as field >
        <#if field.canExport() == true>
            Get${field.name?cap_first}() ${field.runType.getGoType()}
        </#if>
    </#list>
    }
</#if>

type ${structName} struct {
<#list allFields as field>
    <#if field.canExport() == true>
        ${(field.name)?cap_first} ${field.runType.getGoType()}  //${field.comment}
    </#if>
</#list>
}

<#if hasParent>
    <#list parent.allFields as field >
        <#if field.canExport() == true>
            func (i ${structName}) Get${field.name?cap_first}() ${field.runType.getGoType()} {
            return i.${field.name?cap_first}
            }
        </#if>
    </#list>

</#if>