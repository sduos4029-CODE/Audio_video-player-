// Build-time only. Arguments: uBlock checkout, uAssets checkout, output asset directory.
import fs from 'node:fs';
import path from 'node:path';
import { pathToFileURL } from 'node:url';
import crypto from 'node:crypto';
import { execFileSync } from 'node:child_process';
const [ubo, assets, output] = process.argv.slice(2);
await import(pathToFileURL(path.join(ubo,'src/js/resources/scriptlets.js')));
const {registeredScriptlets}=await import(pathToFileURL(path.join(ubo,'src/js/resources/base.js')));
const {utils}=await import(pathToFileURL(path.join(ubo,'src/js/static-filtering-parser.js')));
fs.mkdirSync(output,{recursive:true});
const sources=['thirdparties/easylist/easylist.txt','filters/filters.txt','filters/filters-2020.txt','filters/filters-2021.txt','filters/filters-2022.txt','filters/filters-2023.txt','filters/filters-2024.txt','filters/filters-2025.txt','filters/filters-2026.txt','filters/filters-general.txt','filters/filters-mobile.txt','filters/quick-fixes.txt','filters/unbreak.txt'];
const env=['chromium','mobile','ublock'];
const rules=sources.map(name=>`! Source: ${name}\n`+utils.preparser.prune(fs.readFileSync(path.join(assets,name),'utf8'),env)).join('\n');
fs.writeFileSync(path.join(output,'rules.txt'),rules);
const resources=registeredScriptlets.map(r=>({name:r.name,aliases:r.aliases||[],kind:{mime:'application/javascript'},content:Buffer.from(r.fn.toString()).toString('base64'),dependencies:r.dependencies||[],permission:r.requiresTrust?1:0}));
const {default:redirects}=await import(pathToFileURL(path.join(ubo,'src/js/redirect-resources.js')));
const mime={js:'application/javascript',html:'text/html',json:'application/json',css:'text/css',xml:'text/xml',gif:'image/gif',png:'image/png',mp3:'audio/mp3',mp4:'video/mp4'};
for(const [name,details] of redirects){if(resources.some(r=>r.name===name))continue;const file=path.join(ubo,'src/web_accessible_resources',name);if(!fs.existsSync(file))throw new Error(`Missing redirect source: ${name}`);resources.push({name,aliases:typeof details.alias==='string'?[details.alias]:details.alias||[],kind:{mime:mime[path.extname(name).slice(1)]||'text/plain'},content:fs.readFileSync(file).toString('base64')});}
fs.writeFileSync(path.join(output,'resources.json'),JSON.stringify(resources));
const revision=(repo,provided)=>{if(provided&&/^[0-9a-f]{40}$/.test(provided))return provided;const root=execFileSync('git',['rev-parse','--show-toplevel'],{cwd:repo,encoding:'utf8'}).trim();if(path.resolve(root)!==path.resolve(repo))throw new Error('Source archives require explicit PULSE_UBLOCK_REVISION and PULSE_UASSETS_REVISION');return execFileSync('git',['rev-parse','HEAD'],{cwd:repo,encoding:'utf8'}).trim();};
const files={};for(const name of ['rules.txt','resources.json'])files[name]=crypto.createHash('sha256').update(fs.readFileSync(path.join(output,name))).digest('hex');
fs.writeFileSync(path.join(output,'manifest.json'),JSON.stringify({engine:'adblock-rust 0.13.5',uBlock:revision(ubo,process.env.PULSE_UBLOCK_REVISION),uAssets:revision(assets,process.env.PULSE_UASSETS_REVISION),sources,resourceCount:resources.length,files},null,2));
console.log('Prepared',rules.split('\n').length,'rule lines and',resources.length,'resources');
