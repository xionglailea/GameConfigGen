#pragma once
<#list tables?values as table>
#include "${table.moduleName}/${table.name}.h"
</#list>
#include "Extensions.h"

namespace ${getRootPackage()}
{
    class CfgMgr
    {
        FString Dir = FPaths::ProjectContentDir();
        FOctets* CreateOctets(const FString& cfgName)
        {
            FString FullPath = Dir + "Generated/data/" + cfgName;
            GLog->Log("read file: " + FullPath);
            TArray<uint8> FileData;
            if (FFileHelper::LoadFileToArray(FileData, *FullPath))
            {
                return new FOctets(FileData);
            }
            return nullptr;
        }

    public:
<#list tables?values as table>
    <#if table.canExport() == true>
        <#if table.single = true>
            ${table.readFileType.getUeType()} ${table.name?uncap_first};
        <#else >
            ${table.readFileType.getUeType()} ${table.name?uncap_first}List;
            TMap<${table.indexField.runType.getUeType()}, ${table.moduleName}::${table.name}*> ${table.name?uncap_first}Map;
        </#if>
    </#if>
</#list>
        CfgMgr()
        {
            FOctets* os;
    <#list tables?values as table>
        <#if table.canExport() == true>
            os = CreateOctets("${table.name?lower_case}.data");
            if (os)
            {
            <#if table.single = true>
                ${table.name?uncap_first} = ${table.readFileType.getUeUnmarshal()};
            <#else >
                ${table.name?uncap_first}List = ${table.readFileType.getUeUnmarshal()};
                for (auto temp : ${table.name?uncap_first}List)
                    ${table.name?uncap_first}Map.Add(temp->${table.indexField.name}, temp);
                delete os;
            </#if>
            }
            else
            {
                UE_LOG(LogTemp, Error,TEXT("read file failed: ${table.name?lower_case}.data"))
            }
        </#if>
    </#list>
        }
        ~CfgMgr()
        {
        }

    };
}