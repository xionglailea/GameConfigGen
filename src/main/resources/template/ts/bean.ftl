<#list getTsImportInfo() as temp>
${temp}
</#list>

/**
* ${comment!""}
*/
<#if hasParent == true && dynamic == true>
export abstract class ${name} extends ${parent.name} {
<#elseif hasParent == false && dynamic == true>
export abstract class ${name} {
<#elseif hasParent == true && dynamic == false>
export class ${name} extends ${parent.name} {
<#else>
export class ${name} {
</#if>

<#list fields as field>
    <#if field.canExport() == true>
    ${field.name}: ${field.runType.getTsType()} //${field.comment}
    </#if>
</#list>

<#if dynamic == true>
    protected constructor(os: Octets) {
 <#else>
    constructor(os: Octets) {
 </#if>
<#if hasParent == true>
        super(os);
</#if>
<#list fields as field>
        <#if field.canExport() == true>
        this.${field.name} = ${field.runType.getTsUnmarshal()}
        </#if>
</#list>
    }

<#list fields as field>
    <#if field.canExport() == true>
    <#if field.hasRef()>
        <#assign typeName = field.runType.getTypeName()>
        <#if typeName == "list">
        <#else>
    public get${field.name?cap_first}Ref(): ${field.ref} {
        return ins.${field.ref?lower_case}Map.get(this.${field.name})!;
    }
        </#if>
    </#if>
    </#if>
</#list>

}