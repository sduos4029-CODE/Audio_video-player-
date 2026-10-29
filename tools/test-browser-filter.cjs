const assert=require('node:assert/strict');
const fs=require('node:fs');
const {chromium}=require('playwright');
const upstream=fs.readFileSync('app/src/main/assets/adblock/web_rules.js','utf8');
const fallback=fs.readFileSync('app/src/main/assets/web_filter.js','utf8');
const payload=()=>({videoDetails:{videoId:'fixture'},streamingData:{formats:[{url:'https://media.example/video.mp4'}]},playabilityStatus:{status:'OK'},captions:{keep:true},adPlacements:[{ad:true}],playerAds:[1],adSlots:[2]});
(async()=>{
 const browser=await chromium.launch({executablePath:process.env.PULSE_CHROMIUM||'/usr/bin/chromium',headless:true,args:['--no-sandbox','--disable-background-networking']});
 try{
  for(const host of ['m.youtube.com','www.youtube.com','music.youtube.com']){
   for(const combined of [false,true]){
    const context=await browser.newContext();const errors=[];const page=await context.newPage();page.on('pageerror',error=>errors.push(error.message));
    // Every page/request is fulfilled locally. This test never contacts YouTube.
    await context.route('**/*',async route=>{const url=new URL(route.request().url());if(route.request().isNavigationRequest())await route.fulfill({contentType:'text/html',body:'<!doctype html><html><head></head><body><div class="ytp-ad-overlay-container">ad fixture</div><div id="content">Intended video fixture</div></body></html>'});else await route.fulfill({contentType:'application/json',body:JSON.stringify(url.pathname==='/youtubei/v1/player'?payload():{playabilityStatus:{status:'UNPLAYABLE',reason:'Unavailable'},adPlacements:[1]})});});
    await context.addInitScript({content:upstream+(combined?'\n'+fallback:'')});
    await page.goto(`https://${host}/watch?v=fixture`);
    const result=await page.evaluate(async value=>{
     window.ytInitialPlayerResponse=JSON.parse(JSON.stringify(value));
     const initial=window.ytInitialPlayerResponse;
     const response=await fetch('/youtubei/v1/player?fixture=true',{method:'POST',body:'{"fixture":true}',credentials:'include'});const fetched=await response.json();
     const denied=await (await fetch('/unavailable-fixture')).json();
     const xhr=await new Promise(resolve=>{const r=new XMLHttpRequest();r.open('POST','/youtubei/v1/player?fixture=true');r.onload=()=>resolve(JSON.parse(r.responseText));r.send('{"fixture":true}');});
     return {installed:!!window.__pulseEngineRules,initial,fetch:fetched,xhr,denied,styles:!!document.getElementById('pulse-engine-style'),adDisplay:getComputedStyle(document.querySelector('.ytp-ad-overlay-container')).display,content:document.getElementById('content').textContent};
    },payload());
    assert.equal(result.installed,true);assert.equal(result.styles,true);assert.equal(result.initial.adPlacements,undefined);assert.equal(result.initial.playerAds,undefined);assert.equal(result.initial.adSlots,undefined);assert.equal(result.initial.playabilityStatus.status,'OK');
    if(combined){for(const value of [result.fetch,result.xhr]){assert.equal(value.adSlots,undefined);assert.equal(value.adPlacements,undefined);assert.equal(value.streamingData.formats.length,1);assert.equal(value.captions.keep,true);}assert.equal(result.denied.playabilityStatus.status,'UNPLAYABLE');assert.equal(result.adDisplay,'none');}
    assert.equal(result.content,'Intended video fixture');assert.deepEqual(errors,[],`${host}: document-start errors`);
    console.log(host,combined?'combined filters':'upstream scriptlets','passed');await context.close();
   }
  }
 }finally{await browser.close();}
})().catch(error=>{console.error(error);process.exitCode=1;});
