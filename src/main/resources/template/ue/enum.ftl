#pragma once
/**
* ${comment!""}
*/

namespace ${getRootPkg()}
{
    namespace ${moduleName}
    {
        class ${name}
        {
        public:
        <#list fields as field>
            inline static int32 ${field.name} = ${field.value?c}; //${field.alias}
        </#list>
        };
    }
}

