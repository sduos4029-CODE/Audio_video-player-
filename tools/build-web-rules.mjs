import fs from 'node:fs';
import crypto from 'node:crypto';
const root=process.argv[2]||'app/src/main/assets/adblock';
const pages=JSON.parse(fs.readFileSync(`${root}/pages.json`,'utf8'));
let output=`/* Generated from pinned GPL-3.0-or-later uBlock Origin scriptlets. See adblock/NOTICES.txt. */\n(function(){'use strict';if(!/(^|\\.)youtube\\.com$/i.test(location.hostname)||window.__pulseEngineRules)return;Object.defineProperty(window,'__pulseEngineRules',{value:true});const scriptletGlobals=new Map();\nswitch(location.hostname){\n`;
for(const [host,page] of Object.entries(pages)){
 output+=`case ${JSON.stringify(host)}:{\n${page.injected_script}\n`;
 const css=[...page.hide_selectors].map(selector=>`${selector}{display:none!important}`).join('\n');
 output+=`const install=()=>{if(!document.documentElement||document.getElementById('pulse-engine-style'))return;const style=document.createElement('style');style.id='pulse-engine-style';style.textContent=${JSON.stringify(css)};document.documentElement.appendChild(style);};if(document.documentElement)install();else document.addEventListener('DOMContentLoaded',install,{once:true});break;}\n`;
}
output+='}\n})();\n';
fs.writeFileSync(`${root}/web_rules.js`,output);
const manifest=JSON.parse(fs.readFileSync(`${root}/manifest.json`,'utf8'));
for(const name of ['pages.json','web_rules.js'])manifest.files[name]=crypto.createHash('sha256').update(fs.readFileSync(`${root}/${name}`)).digest('hex');
fs.writeFileSync(`${root}/manifest.json`,JSON.stringify(manifest,null,2));
console.log('Prepared document-start script:',output.length,'bytes');
