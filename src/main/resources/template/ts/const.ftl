/**
* ${comment!""}
*/
export class ${name} {

<#list fields as field>
  static ${field.name} = <#if field.type == "string">"${field.value}" <#else >${field.value}</#if>
</#list>

}