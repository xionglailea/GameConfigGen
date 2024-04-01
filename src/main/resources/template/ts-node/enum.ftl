/**
* ${comment!""}
 */
export class ${name} {
  <#assign x = "">
  <#list fields as field>
    static ${field.name} = ${field.value} //${field.alias};
    <#if field?is_last>
        <#assign  x = x + field.value>
    <#else >
        <#assign  x = x + field.value + ", ">
    </#if>
  </#list>
    static enums: number[] = [${x}]
}
