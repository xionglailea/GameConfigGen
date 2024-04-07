#pragma once
/**
* ${comment!""}
*/

namespace cfg
{
    namespace ${moduleName}
    {
        class ${name}
        {
        public:
            <#list fields as field>
            inline static ${field.runType.getUeType()} ${field.name} = <#if field.type == "string">TEXT("${field.value}") <#else >${field.value}</#if>;
            </#list>
        };
    }
}