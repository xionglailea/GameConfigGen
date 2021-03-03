
/**
* ${comment}
*/
namespace ${packageName}
{


    public sealed class ${name}
    {

<#list fields as field>
        public const ${field.runType.getCsType()} ${field.name?cap_first} = <#if field.type == "string">"${field.value}" <#else >${field.value}</#if>;
</#list>

    }
}