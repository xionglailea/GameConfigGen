<#list tables?values as table>
import { ${table.name} } from './${table.moduleName}/${table.name}'
</#list>
import { Octets, WrapOctets } from './datastream/Octets';
import * as extension from './extension/Extensions';
import fs from 'fs';

export class CfgMgr {
    static _dir = "../data"
    static createOctets(file: string): Octets {
        let path = this._dir + "/" + file
        let data = fs.readFileSync(path);
        return WrapOctets(data)
    }

<#list tables?values as table>
    <#if table.canExport() == true>
    <#if table.single = true>
    ${table.name?uncap_first}: ${table.readFileType.getTsType()}
    <#else >
    ${table.name?uncap_first}List: ${table.readFileType.getTsType()}
    ${table.name?uncap_first}Map: Map<${table.indexField.runType.getTsType()}, ${table.name}>  = new Map()
    </#if>
    </#if>
</#list>


    constructor() {
        let os: Octets;
<#list tables?values as table>
    <#if table.canExport() == true>
        os = CfgMgr.createOctets("${table.name?lower_case}.data");
    <#if table.single = true>
        this.${table.name?uncap_first} = ${table.readFileType.getTsUnmarshal()};
    <#else >
        this.${table.name?uncap_first}List = ${table.readFileType.getTsUnmarshal()};
        for (let temp of this.${table.name?uncap_first}List) {
            this.${table.name?uncap_first}Map.set(temp.${table.indexField.name}, temp);
        }
    </#if>
    </#if>
</#list>
    }
}

export const ins = new CfgMgr()
