package ${getRootPkg()}

<#assign x = "">
<#list fields as field>
const ${field.name} = ${field.value} //${field.alias}
<#if field?is_last>
    <#assign  x = x + field.value>
<#else >
    <#assign  x = x + field.value + ", ">
</#if>
</#list>
var ${name}_Enums = []int{${x}}