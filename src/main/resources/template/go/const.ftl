package ${getRootPkg()}

/**
* ${comment!""}
*/
<#list fields as field>
var ${field.name?cap_first} = <#if field.type == "string">"${field.value}" <#else >${field.value}</#if>
</#list>