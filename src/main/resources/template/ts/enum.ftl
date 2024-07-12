/**
* ${comment!""}
 */
export class ${name} {
  <#assign x = "">
  <#list fields as field>
    static ${field.name} = ${field.value?c} //${field.alias};
    <#if field?is_last>
        <#assign  x = x + field.value?c>
    <#else >
        <#assign  x = x + field.value?c + ", ">
    </#if>
  </#list>
    static enums: number[] = [${x}]
}
