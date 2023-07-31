package ${cfgDefine.rootPackage}

import (
    "io"
    "os"
    "path"
    "game/datastream"
)

var Dir = "data"

func createOctets(fileName string) *datastream.Octets {
    file, err := os.Open(path.Join(Dir, fileName))
    if err != nil {
        panic("can not find file " + fileName)
    }
    data, _ := io.ReadAll(file)
    return datastream.NewOctetsByData(data)
}

type ConfigMgr struct {
<#list tables?values as table>
    <#if table.canExport() == true>
        <#if table.single = true>
    ${table.name?cap_first}Data *${table.getName()}
        <#else >
            <#if table.dynamic = true>
    ${table.name?cap_first}Map map[${table.indexField.runType.getGoType()}]I${table.getName()}
            <#else >
    ${table.name?cap_first}Map map[${table.indexField.runType.getGoType()}]*${table.getName()}
            </#if>
        </#if>
    </#if>
</#list>
}

func New() *ConfigMgr {
    ins := new(ConfigMgr)
    var os *datastream.Octets
<#list tables?values as table>
    <#if table.canExport() == true>
    os = createOctets("${table.name?lower_case}.data")
        <#if table.single = true>
    ${table.name?cap_first}Data = ${table.readFileType.getGoUnmarshal()}
        <#else >
    <#if table.dynamic = true>
        <#assign interfaceName = "I${table.getName()}">
    ins.${table.name?cap_first}Map = make(map[${table.indexField.runType.getGoType()}]${interfaceName})
    <#else >
        <#assign typeName = "*${table.getName()}">
    ins.${table.name?cap_first}Map = make(map[${table.indexField.runType.getGoType()}]${typeName})
    </#if>
    ${table.name?uncap_first}List := ${table.readFileType.getGoUnmarshal()}
    for _, Value := range ${table.name?uncap_first}List {
    <#if table.dynamic = true>
        ins.${table.name?cap_first}Map[Value.(${interfaceName}).Get${table.indexField.name?cap_first}()] = Value.(${interfaceName})
    <#else >
        ins.${table.name?cap_first}Map[Value.${table.indexField.name?cap_first}] = Value
    </#if>
    }
        </#if>
    </#if>
</#list>
    return ins
}