package ${packageName}

import(
<#list getGoImportInfo() as temp>
    "${temp}"
</#list>
)

<#assign structName = "${name?cap_first}">
<#assign parentName = "">

<#if dynamic == true>
    type I${structName} interface {
    Get${structName}() *${structName}
    }
</#if>

type ${structName} struct {
<#if hasParent>
    <#assign parentName = "${(parent.name)?cap_first}">
    ${parentName}
</#if>
<#list fields as field>
    <#if field.canExport() == true>
        ${(field.name)?cap_first} ${field.runType.getGoType()}  //${field.comment}
    </#if>
</#list>
}

<#if hasParent>
func (i *${structName}) Get${parentName} *${parentName} {
    return &i.${parentName}
}
</#if>