package ${getRootPkg()}

<#assign x = "">
<#list fields as field>
const ${field.name} = ${field.value?c} //${field.alias}
<#if field?is_last>
    <#assign  x = x + field.value?c>
<#else >
    <#assign  x = x + field.value?c + ", ">
</#if>
</#list>
var ${name}_Enums = []int{${x}}