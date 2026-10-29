const assert = require('node:assert/strict');
const fs = require('node:fs');
const vm = require('node:vm');
const script = fs.readFileSync('app/src/main/assets/web_filter.js', 'utf8');
function setup(responseData, options = {}) {
  const requests = [], styles = new Map(); let skipClicks = 0;
  const document = { documentElement: { appendChild(s) { styles.set(s.id,s); } },
    getElementById(id) { return styles.get(id); }, createElement() { return {}; },
    querySelectorAll() { return [{disabled:false,getClientRects(){return [1];},click(){skipClicks++;}}]; }, addEventListener() {} };
  class XHR { constructor(){this.listeners=[];this.readyState=0;this.status=200;this.responseType='';this._text=JSON.stringify(responseData);}get responseText(){return this._text;}get response(){return this._text;}addEventListener(name,fn){this.listeners.push(fn);}open(method,url){this.url=url;this.readyState=1;}finish(){this.readyState=4;for(const fn of this.listeners)fn.call(this);} }
  const context = {location:{hostname:options.host||'m.youtube.com',href:'https://m.youtube.com/watch?v=example'},document,URL,Headers,Response,XMLHttpRequest:XHR,MutationObserver:class {observe(){}},setTimeout(fn){fn();},fetch:async(...args)=>{requests.push(args);return new Response(JSON.stringify(responseData),{headers:{'content-type':'application/json'}});}};
  context.window=context;if(options.initial)context.ytInitialPlayerResponse=options.initial;vm.runInNewContext(script,context);
  return {context,requests,styles,clicks:()=>skipClicks};
}
const payload=()=>({videoDetails:{videoId:'example'},streamingData:{formats:[{url:'https://example.test/video'}]},playabilityStatus:{status:'OK'},captions:{keep:true},adPlacements:[{ad:true}],playerAds:[1],adSlots:[2]});
(async()=>{
  const initial=payload(), test=setup(payload(),{initial});assert.equal(initial.adPlacements,undefined);assert.equal(initial.streamingData.formats.length,1);
  test.context.ytInitialPlayerResponse=payload();assert.equal(test.context.ytInitialPlayerResponse.playerAds,undefined);
  const body='{request-body-is-preserved}';const r=await test.context.fetch('https://www.youtube.com/youtubei/v1/player',{method:'POST',body,credentials:'include'});const clean=await r.json();assert.equal(clean.adSlots,undefined);assert.equal(clean.playabilityStatus.status,'OK');assert.equal(clean.captions.keep,true);assert.equal(test.requests[0][1].body,body);assert.equal(test.requests[0][1].credentials,'include');
  const untouched=await test.context.fetch('https://www.youtube.com/youtubei/v1/browse');assert.equal((await untouched.json()).adSlots.length,1);
  const xhr=new test.context.XMLHttpRequest();xhr.open('POST','https://www.youtube.com/youtubei/v1/player');xhr.finish();assert.equal(JSON.parse(xhr.responseText).playerAds,undefined);xhr.open('GET','https://www.youtube.com/youtubei/v1/browse');xhr.finish();assert.equal(JSON.parse(xhr.responseText).adSlots.length,1);
  const other=new test.context.XMLHttpRequest();other.open('GET','https://example.test/player');other.finish();assert.equal(JSON.parse(other.responseText).adSlots.length,1);
  assert.equal(test.styles.size,1);assert.equal(test.clicks(),1);const foreign=setup(payload(),{host:'accounts.google.com'});assert.equal(foreign.context.__pulseFilterInstalled,undefined);assert.equal(foreign.styles.size,0);
  // Errors remain errors; the filter does not convert an unavailable video into an allowed one.
  const blocked=setup({playabilityStatus:{status:'UNPLAYABLE',reason:'Unavailable'},adPlacements:[1]});const denied=await blocked.context.fetch('https://www.youtube.com/youtubei/v1/player');assert.equal((await denied.json()).playabilityStatus.status,'UNPLAYABLE');
  console.log('Web filter: bootstrap, fetch, XHR, preserved playback/auth data, non-player APIs, cosmetic rule, skip control, origin isolation and unavailable-video status passed.');
})().catch(e=>{console.error(e);process.exitCode=1;});
