<#list tables?values as table>
import { ${table.name} } from './${table.moduleName}/${table.name}';
</#list>
import { Octets, WrapOctets } from './datastream/Octets';
import * as extension from './extension/Extensions';

export class CfgMgr {
    static _dir = "/func/data"; // 根据你的实际路径进行设置

    static async createOctets(file: string): Promise<Octets> {
        const path = this._dir + "/" + file;
        try {
            // 执行fetch请求获取数据
            const response = await fetch(path);
            if (!response.ok) {
            throw new Error(`Failed to fetch data from ${r"${path}"}:${r"${response.statusText}"}`);
            }
            const data = await response.arrayBuffer(); // 获取ArrayBuffer形式的文件内容
            return WrapOctets(new Uint8Array(data));  // 将数据转换为Uint8Array并包装为Octets
        } catch (error) {
            console.error(`Error fetching file ${r"${file}"}:`, error);
            throw error;
        }
    }

<#list tables?values as table>
    <#if table.canExport() == true>
    <#if table.single = true>
    ${table.name?uncap_first}: ${table.readFileType.getTsType()} = <${table.readFileType.getTsType()}>{}
    <#else >
    ${table.name?uncap_first}List: ${table.readFileType.getTsType()} = []
    ${table.name?uncap_first}Map: Map<${table.indexField.runType.getTsType()}, ${table.name}>  = new Map()
    </#if>
    </#if>
</#list>


    async initialize() {
        let os: Octets;
<#list tables?values as table>
    <#if table.canExport() == true>
        os = await CfgMgr.createOctets("${table.name?lower_case}.data");
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
