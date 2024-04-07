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
            inline static int32 ${field.name} = ${field.value}; //${field.alias}
        </#list>
        };
    }
}

