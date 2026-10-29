/* Generated from pinned GPL-3.0-or-later uBlock Origin scriptlets. See adblock/NOTICES.txt. */
(function(){'use strict';if(!/(^|\.)youtube\.com$/i.test(location.hostname)||window.__pulseEngineRules)return;Object.defineProperty(window,'__pulseEngineRules',{value:true});const scriptletGlobals=new Map();
switch(location.hostname){
case "m.youtube.com":{
function objectPruneFn(
    obj,
    rawPrunePaths,
    rawNeedlePaths,
    stackNeedleDetails = { matchAll: true },
    extraArgs = {}
) {
    if ( typeof rawPrunePaths !== 'string' ) { return; }
    const safe = safeSelf();
    const prunePaths = rawPrunePaths !== ''
        ? safe.String_split.call(rawPrunePaths, / +/)
        : [];
    const needlePaths = prunePaths.length !== 0 && rawNeedlePaths !== ''
        ? safe.String_split.call(rawNeedlePaths, / +/)
        : [];
    if ( stackNeedleDetails.matchAll !== true ) {
        if ( matchesStackTraceFn(stackNeedleDetails, extraArgs.logstack) === false ) {
            return;
        }
    }
    if ( objectPruneFn.mustProcess === undefined ) {
        objectPruneFn.mustProcess = (root, needlePaths) => {
            for ( const needlePath of needlePaths ) {
                if ( objectFindOwnerFn(root, needlePath) === false ) {
                    return false;
                }
            }
            return true;
        };
    }
    if ( prunePaths.length === 0 ) { return; }
    let outcome = 'nomatch';
    if ( objectPruneFn.mustProcess(obj, needlePaths) ) {
        for ( const path of prunePaths ) {
            if ( objectFindOwnerFn(obj, path, true) ) {
                outcome = 'match';
            }
        }
    }
    if ( outcome === 'match' ) { return obj; }
}
function matchesStackTraceFn(
    needleDetails,
    logLevel = ''
) {
    const safe = safeSelf();
    const exceptionToken = getExceptionTokenFn();
    const error = new safe.Error(exceptionToken);
    const docURL = new URL(self.location.href);
    docURL.hash = '';
    // Normalize stack trace
    const reLine = /(.*?@)?(\S+)(:\d+):\d+\)?$/;
    const lines = [];
    for ( let line of safe.String_split.call(error.stack, /[\n\r]+/) ) {
        if ( line.includes(exceptionToken) ) { continue; }
        line = line.trim();
        const match = safe.RegExp_exec.call(reLine, line);
        if ( match === null ) { continue; }
        let url = match[2];
        if ( url.startsWith('(') ) { url = url.slice(1); }
        if ( url === docURL.href ) {
            url = 'inlineScript';
        } else if ( url.startsWith('<anonymous>') ) {
            url = 'injectedScript';
        }
        let fn = match[1] !== undefined
            ? match[1].slice(0, -1)
            : line.slice(0, match.index).trim();
        if ( fn.startsWith('at') ) { fn = fn.slice(2).trim(); }
        let rowcol = match[3];
        lines.push(' ' + `${fn} ${url}${rowcol}:1`.trim());
    }
    lines[0] = `stackDepth:${lines.length-1}`;
    const stack = lines.join('\t');
    const r = needleDetails.matchAll !== true &&
        safe.testPattern(needleDetails, stack);
    if (
        logLevel === 'all' ||
        logLevel === 'match' && r ||
        logLevel === 'nomatch' && !r
    ) {
        safe.uboLog(stack.replace(/\t/g, '\n'));
    }
    return r;
}
function getExceptionTokenFn() {
    const token = getRandomTokenFn();
    const oe = self.onerror;
    self.onerror = function(msg, ...args) {
        if ( typeof msg === 'string' && msg.includes(token) ) { return true; }
        if ( oe instanceof Function ) {
            return oe.call(this, msg, ...args);
        }
    }.bind();
    return token;
}
function getRandomTokenFn() {
    const safe = safeSelf();
    return safe.String_fromCharCode(Date.now() % 26 + 97) +
        safe.Math_floor(safe.Math_random() * 982451653 + 982451653).toString(36);
}
function safeSelf() {
    if ( safeSelf.safe ) {
        return safeSelf.safe;
    }
    const self = globalThis;
    const safe = {
        'Array_from': Array.from,
        'Error': self.Error,
        'Function_toString': Function.prototype.call.bind(self.Function.prototype.toString),
        'Math_floor': Math.floor,
        'Math_max': Math.max,
        'Math_min': Math.min,
        'Math_random': Math.random,
        'Object': Object,
        'Object_defineProperty': Object.defineProperty.bind(Object),
        'Object_defineProperties': Object.defineProperties.bind(Object),
        'Object_fromEntries': Object.fromEntries.bind(Object),
        'Object_getOwnPropertyDescriptor': Object.getOwnPropertyDescriptor.bind(Object),
        'Object_hasOwn': Object.hasOwn.bind(Object),
        'Object_toString': Object.prototype.toString,
        'RegExp': self.RegExp,
        'RegExp_test': Function.prototype.call.bind(self.RegExp.prototype.test),
        'RegExp_exec': self.RegExp.prototype.exec,
        'Request_clone': self.Request.prototype.clone,
        'String': self.String,
        'String_fromCharCode': String.fromCharCode,
        'String_split': String.prototype.split,
        'XMLHttpRequest': self.XMLHttpRequest,
        'addEventListener': self.EventTarget.prototype.addEventListener,
        'removeEventListener': self.EventTarget.prototype.removeEventListener,
        'fetch': self.fetch,
        'JSON': self.JSON,
        'JSON_parse': Function.prototype.call.bind(self.JSON.parse, self.JSON),
        'JSON_stringify': Function.prototype.call.bind(self.JSON.stringify, self.JSON),
        'log': console.log.bind(console),
        // Properties
        logLevel: 0,
        // Methods
        makeLogPrefix(...args) {
            return this.sendToLogger && `[${args.join(' \u205D ')}]` || '';
        },
        uboLog(...args) {
            if ( this.sendToLogger === undefined ) { return; }
            if ( args === undefined || args[0] === '' ) { return; }
            return this.sendToLogger('info', ...args);
            
        },
        uboErr(...args) {
            if ( this.sendToLogger === undefined ) { return; }
            if ( args === undefined || args[0] === '' ) { return; }
            return this.sendToLogger('error', ...args);
        },
        escapeRegexChars(s) {
            return s.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
        },
        initPattern(pattern, options = {}) {
            if ( pattern === '' ) {
                return { matchAll: true, expect: true };
            }
            const expect = (options.canNegate !== true || pattern.startsWith('!') === false);
            if ( expect === false ) {
                pattern = pattern.slice(1);
            }
            const match = /^\/(.+)\/([gimsu]*)$/.exec(pattern);
            if ( match !== null ) {
                return {
                    re: new this.RegExp(
                        match[1],
                        match[2] || options.flags
                    ),
                    expect,
                };
            }
            if ( options.flags !== undefined ) {
                return {
                    re: new this.RegExp(this.escapeRegexChars(pattern),
                        options.flags
                    ),
                    expect,
                };
            }
            return { pattern, expect };
        },
        testPattern(details, haystack) {
            if ( details.matchAll ) { return true; }
            if ( details.re ) {
                return this.RegExp_test(details.re, haystack) === details.expect;
            }
            return haystack.includes(details.pattern) === details.expect;
        },
        patternToRegex(pattern, flags = undefined, verbatim = false) {
            if ( pattern === '' ) { return /^/; }
            const match = /^\/(.+)\/([gimsu]*)$/.exec(pattern);
            if ( match === null ) {
                const reStr = this.escapeRegexChars(pattern);
                return new RegExp(verbatim ? `^${reStr}$` : reStr, flags);
            }
            try {
                return new RegExp(match[1], match[2] || undefined);
            }
            catch {
            }
            return /^/;
        },
        parseVarargs(varargs) {
            const entries = varargs.reduce((out, v, i, a) => {
                if ( i & 1 ) { return out; }
                const rawValue = a[i+1];
                const value = /^\d+$/.test(rawValue)
                    ? parseInt(rawValue, 10)
                    : rawValue;
                out.push([ a[i], value ]);
                return out;
            }, []);
            return this.Object_fromEntries(entries);
        },
    };
    safeSelf.safe = safe;
    if ( scriptletGlobals.bcSecret === undefined ) { return safe; }
    // This is executed only when the logger is opened
    safe.logLevel = scriptletGlobals.logLevel || 1;
    let lastLogType = '';
    let lastLogText = '';
    let lastLogTime = 0;
    safe.toLogText = (type, ...args) => {
        if ( args.length === 0 ) { return; }
        const text = `[${document.location.hostname || document.location.href}]${args.join(' ')}`;
        if ( text === lastLogText && type === lastLogType ) {
            if ( (Date.now() - lastLogTime) < 5000 ) { return; }
        }
        lastLogType = type;
        lastLogText = text;
        lastLogTime = Date.now();
        return text;
    };
    try {
        const bc = new self.BroadcastChannel(scriptletGlobals.bcSecret);
        let bcBuffer = [];
        safe.sendToLogger = (type, ...args) => {
            const text = safe.toLogText(type, ...args);
            if ( text === undefined ) { return; }
            if ( bcBuffer === undefined ) {
                return bc.postMessage({ what: 'messageToLogger', type, text });
            }
            bcBuffer.push({ type, text });
        };
        bc.onmessage = ev => {
            const msg = ev.data;
            switch ( msg ) {
            case 'iamready!':
                if ( bcBuffer === undefined ) { break; }
                bcBuffer.forEach(({ type, text }) =>
                    bc.postMessage({ what: 'messageToLogger', type, text })
                );
                bcBuffer = undefined;
                break;
            case 'setScriptletLogLevelToOne':
                safe.logLevel = 1;
                break;
            case 'setScriptletLogLevelToTwo':
                safe.logLevel = 2;
                break;
            }
        };
        bc.postMessage('areyouready?');
    } catch {
        safe.sendToLogger = (type, ...args) => {
            const text = safe.toLogText(type, ...args);
            if ( text === undefined ) { return; }
            safe.log(`uBO ${text}`);
        };
    }
    return safe;
}
function objectFindOwnerFn(
    root,
    path,
    prune = false
) {
    const safe = safeSelf();
    let owner = root;
    let chain = path;
    for (;;) {
        if ( typeof owner !== 'object' || owner === null  ) { return false; }
        const pos = chain.indexOf('.');
        if ( pos === -1 ) {
            if ( prune === false ) {
                return safe.Object_hasOwn(owner, chain);
            }
            let modified = false;
            if ( chain === '*' ) {
                for ( const key in owner ) {
                    if ( safe.Object_hasOwn(owner, key) === false ) { continue; }
                    delete owner[key];
                    modified = true;
                }
            } else if ( safe.Object_hasOwn(owner, chain) ) {
                delete owner[chain];
                modified = true;
            }
            return modified;
        }
        const prop = chain.slice(0, pos);
        const next = chain.slice(pos + 1);
        let found = false;
        if ( prop === '[-]' && Array.isArray(owner) ) {
            let i = owner.length;
            while ( i-- ) {
                if ( objectFindOwnerFn(owner[i], next) === false ) { continue; }
                owner.splice(i, 1);
                found = true;
            }
            return found;
        }
        if ( prop === '{-}' && owner instanceof Object ) {
            for ( const key of Object.keys(owner) ) {
                if ( objectFindOwnerFn(owner[key], next) === false ) { continue; }
                delete owner[key];
                found = true;
            }
            return found;
        }
        if (
            prop === '[]' && Array.isArray(owner) ||
            prop === '{}' && owner instanceof Object ||
            prop === '*' && owner instanceof Object
        ) {
            for ( const key of Object.keys(owner) ) {
                if (objectFindOwnerFn(owner[key], next, prune) === false ) { continue; }
                found = true;
            }
            return found;
        }
        if ( safe.Object_hasOwn(owner, prop) === false ) { return false; }
        owner = owner[prop];
        chain = chain.slice(pos + 1);
    }
}
function proxyApplyFn(
    target = '',
    handler = '',
    options = {}
) {
    let context = globalThis;
    let prop = target;
    for (;;) {
        const pos = prop.indexOf('.');
        if ( pos === -1 ) { break; }
        context = context[prop.slice(0, pos)];
        if ( context instanceof Object === false ) { return; }
        prop = prop.slice(pos+1);
    }
    const fn = context[prop];
    if ( typeof fn !== 'function' ) { return; }
    if ( proxyApplyFn.CtorContext === undefined ) {
        proxyApplyFn.ctorContexts = [];
        proxyApplyFn.CtorContext = class {
            constructor(...args) {
                this.init(...args);
            }
            init(callFn, callArgs) {
                this.callFn = callFn;
                this.callArgs = callArgs;
                return this;
            }
            reflect() {
                const r = Reflect.construct(this.callFn, this.callArgs);
                this.callFn = this.callArgs = this.private = undefined;
                proxyApplyFn.ctorContexts.push(this);
                return r;
            }
            static factory(...args) {
                return proxyApplyFn.ctorContexts.length !== 0
                    ? proxyApplyFn.ctorContexts.pop().init(...args)
                    : new proxyApplyFn.CtorContext(...args);
            }
        };
        proxyApplyFn.applyContexts = [];
        proxyApplyFn.ApplyContext = class {
            constructor(...args) {
                this.init(...args);
            }
            init(callFn, thisArg, callArgs) {
                this.callFn = callFn;
                this.thisArg = thisArg;
                this.callArgs = callArgs;
                return this;
            }
            reflect() {
                const r = Reflect.apply(this.callFn, this.thisArg, this.callArgs);
                this.callFn = this.thisArg = this.callArgs = this.private = undefined;
                proxyApplyFn.applyContexts.push(this);
                return r;
            }
            static factory(...args) {
                return proxyApplyFn.applyContexts.length !== 0
                    ? proxyApplyFn.applyContexts.pop().init(...args)
                    : new proxyApplyFn.ApplyContext(...args);
            }
        };
        proxyApplyFn.isCtor = new Map();
        proxyApplyFn.proxies = new WeakMap();
        if ( (options.skipToString || proxyApplyFn.skipToString) !== true ) {
            proxyApplyFn.nativeToString = Function.prototype.toString;
            const proxiedToString = new Proxy(Function.prototype.toString, {
                apply(target, thisArg) {
                    let proxied = thisArg;
                    for(;;) {
                        const fn = proxyApplyFn.proxies.get(proxied);
                        if ( fn === undefined ) { break; }
                        proxied = fn;
                    }
                    return proxyApplyFn.nativeToString.call(proxied);
                }
            });
            proxyApplyFn.proxies.set(proxiedToString, proxyApplyFn.nativeToString);
            Function.prototype.toString = proxiedToString;
        }
    }
    if ( proxyApplyFn.isCtor.has(target) === false ) {
        proxyApplyFn.isCtor.set(target, fn.prototype?.constructor === fn);
    }
    const proxyDetails = {
        apply(target, thisArg, args) {
            return handler(proxyApplyFn.ApplyContext.factory(target, thisArg, args));
        }
    };
    if ( proxyApplyFn.isCtor.get(target) ) {
        proxyDetails.construct = function(target, args) {
            return handler(proxyApplyFn.CtorContext.factory(target, args));
        };
    }
    const proxiedTarget = new Proxy(fn, proxyDetails);
    proxyApplyFn.proxies.set(proxiedTarget, fn);
    context[prop] = proxiedTarget;
}
function jsonPrune(
    rawPrunePaths = '',
    rawNeedlePaths = '',
    stackNeedle = '',
    ...varargs
) {
    const safe = safeSelf();
    const logPrefix = safe.makeLogPrefix('json-prune', rawPrunePaths, rawNeedlePaths, stackNeedle);
    const stackNeedleDetails = safe.initPattern(stackNeedle, { canNegate: true });
    const extraArgs = safe.parseVarargs(varargs);
    proxyApplyFn('JSON.parse', function(context) {
        const objBefore = context.reflect();
        if ( rawPrunePaths === '' ) {
            safe.uboLog(logPrefix, safe.JSON_stringify(objBefore, null, 2));
        }
        const objAfter = objectPruneFn(
            objBefore,
            rawPrunePaths,
            rawNeedlePaths,
            stackNeedleDetails,
            extraArgs
        );
        if ( objAfter === undefined ) { return objBefore; }
        safe.uboLog(logPrefix, 'Pruned');
        if ( safe.logLevel > 1 ) {
            safe.uboLog(logPrefix, `After pruning:\n${safe.JSON_stringify(objAfter, null, 2)}`);
        }
        return objAfter;
    });
}
function setConstantFn(
    trusted = false,
    chain = '',
    rawValue = '',
    ...varargs
) {
    if ( chain === '' ) { return; }
    const safe = safeSelf();
    const logPrefix = safe.makeLogPrefix('set-constant', chain, rawValue);
    const extraArgs = safe.parseVarargs(varargs);
    function setConstant(chain, rawValue) {
        const trappedProp = (( ) => {
            const pos = chain.lastIndexOf('.');
            if ( pos === -1 ) { return chain; }
            return chain.slice(pos+1);
        })();
        const cloakFunc = fn => {
            safe.Object_defineProperty(fn, 'name', { value: trappedProp });
            return new Proxy(fn, {
                defineProperty(target, prop) {
                    if ( prop !== 'toString' ) {
                        return Reflect.defineProperty(...arguments);
                    }
                    return true;
                },
                deleteProperty(target, prop) {
                    if ( prop !== 'toString' ) {
                        return Reflect.deleteProperty(...arguments);
                    }
                    return true;
                },
                get(target, prop) {
                    if ( prop === 'toString' ) {
                        return function() {
                            return `function ${trappedProp}() { [native code] }`;
                        }.bind(null);
                    }
                    return Reflect.get(...arguments);
                },
            });
        };
        if ( trappedProp === '' ) { return; }
        const thisScript = document.currentScript;
        let normalValue = validateConstantFn(trusted, rawValue, extraArgs);
        if ( rawValue === 'noopFunc' || rawValue === 'trueFunc' || rawValue === 'falseFunc' ) {
            normalValue = cloakFunc(normalValue);
        }
        let aborted = false;
        const mustAbort = function(v) {
            if ( trusted ) { return false; }
            if ( aborted ) { return true; }
            aborted =
                (v !== undefined && v !== null) &&
                (normalValue !== undefined && normalValue !== null) &&
                (typeof v !== typeof normalValue);
            if ( aborted ) {
                safe.uboLog(logPrefix, `Aborted because value set to ${v}`);
            }
            return aborted;
        };
        // https://github.com/uBlockOrigin/uBlock-issues/issues/156
        //   Support multiple trappers for the same property.
        const trapProp = function(owner, prop, configurable, handler) {
            if ( handler.init(configurable ? owner[prop] : normalValue) === false ) { return; }
            const odesc = safe.Object_getOwnPropertyDescriptor(owner, prop);
            let prevGetter, prevSetter;
            if ( odesc instanceof safe.Object ) {
                owner[prop] = normalValue;
                if ( odesc.get instanceof Function ) {
                    prevGetter = odesc.get;
                }
                if ( odesc.set instanceof Function ) {
                    prevSetter = odesc.set;
                }
            }
            try {
                safe.Object_defineProperty(owner, prop, {
                    configurable,
                    get() {
                        if ( prevGetter !== undefined ) {
                            prevGetter();
                        }
                        return handler.getter();
                    },
                    set(a) {
                        if ( prevSetter !== undefined ) {
                            prevSetter(a);
                        }
                        handler.setter(a);
                    }
                });
                safe.uboLog(logPrefix, 'Trap installed');
            } catch(ex) {
                safe.uboErr(logPrefix, ex);
            }
        };
        const trapChain = function(owner, chain) {
            const pos = chain.indexOf('.');
            if ( pos === -1 ) {
                trapProp(owner, chain, false, {
                    v: undefined,
                    init: function(v) {
                        if ( mustAbort(v) ) { return false; }
                        this.v = v;
                        return true;
                    },
                    getter: function() {
                        if ( document.currentScript === thisScript ) {
                            return this.v;
                        }
                        safe.uboLog(logPrefix, 'Property read');
                        return normalValue;
                    },
                    setter: function(a) {
                        if ( mustAbort(a) === false ) { return; }
                        normalValue = a;
                    }
                });
                return;
            }
            const prop = chain.slice(0, pos);
            const v = owner[prop];
            chain = chain.slice(pos + 1);
            if ( v instanceof safe.Object || typeof v === 'object' && v !== null ) {
                trapChain(v, chain);
                return;
            }
            trapProp(owner, prop, true, {
                v: undefined,
                init: function(v) {
                    this.v = v;
                    return true;
                },
                getter: function() {
                    return this.v;
                },
                setter: function(a) {
                    this.v = a;
                    if ( a instanceof safe.Object ) {
                        trapChain(a, chain);
                    }
                }
            });
        };
        trapChain(window, chain);
    }
    runAt(( ) => {
        setConstant(chain, rawValue);
    }, extraArgs.runAt);
}
function runAt(fn, when) {
    const intFromReadyState = state => {
        const targets = {
            'loading': 1, 'asap': 1,
            'interactive': 2, 'end': 2, '2': 2,
            'complete': 3, 'idle': 3, '3': 3,
        };
        const tokens = Array.isArray(state) ? state : [ state ];
        for ( const token of tokens ) {
            const prop = `${token}`;
            if ( Object.hasOwn(targets, prop) === false ) { continue; }
            return targets[prop];
        }
        return 0;
    };
    const runAt = intFromReadyState(when);
    if ( intFromReadyState(document.readyState) >= runAt ) {
        fn(); return;
    }
    const onStateChange = ( ) => {
        if ( intFromReadyState(document.readyState) < runAt ) { return; }
        fn();
        safe.removeEventListener.apply(document, args);
    };
    const safe = safeSelf();
    const args = [ 'readystatechange', onStateChange, { capture: true } ];
    safe.addEventListener.apply(document, args);
}
function validateConstantFn(trusted, raw, extraArgs = {}) {
    const safe = safeSelf();
    let value;
    if ( raw === 'undefined' ) {
        value = undefined;
    } else if ( raw === 'false' ) {
        value = false;
    } else if ( raw === 'true' ) {
        value = true;
    } else if ( raw === 'null' ) {
        value = null;
    } else if ( raw === "''" || raw === '' ) {
        value = '';
    } else if ( raw === '[]' || raw === 'emptyArr' ) {
        value = [];
    } else if ( raw === '{}' || raw === 'emptyObj' ) {
        value = {};
    } else if ( raw === 'noopFunc' ) {
        value = function(){};
    } else if ( raw === 'trueFunc' ) {
        value = function(){ return true; };
    } else if ( raw === 'falseFunc' ) {
        value = function(){ return false; };
    } else if ( raw === 'throwFunc' ) {
        value = function(){ throw ''; };
    } else if ( /^-?\d+$/.test(raw) ) {
        value = parseInt(raw);
        if ( isNaN(raw) ) { return; }
        if ( Math.abs(raw) > 0x7FFF ) { return; }
    } else if ( trusted ) {
        if ( raw.startsWith('json:') ) {
            try { value = safe.JSON_parse(raw.slice(5)); } catch { return; }
        } else if ( raw.startsWith('{') && raw.endsWith('}') ) {
            try { value = safe.JSON_parse(raw).value; } catch { return; }
        }
    } else {
        return;
    }
    if ( extraArgs.as !== undefined ) {
        if ( extraArgs.as === 'function' ) {
            return ( ) => value;
        } else if ( extraArgs.as === 'callback' ) {
            return ( ) => (( ) => value);
        } else if ( extraArgs.as === 'resolved' ) {
            return Promise.resolve(value);
        } else if ( extraArgs.as === 'rejected' ) {
            return Promise.reject(value);
        }
    }
    return value;
}
function setConstant(
    ...args
) {
    setConstantFn(false, ...args);
}
function collateFetchArgumentsFn(resource, options) {
    const safe = safeSelf();
    const props = [
        'body', 'cache', 'credentials', 'duplex', 'headers',
        'integrity', 'keepalive', 'method', 'mode', 'priority',
        'redirect', 'referrer', 'referrerPolicy', 'url'
    ];
    const out = {};
    if ( collateFetchArgumentsFn.collateKnownProps === undefined ) {
        collateFetchArgumentsFn.collateKnownProps = (src, out) => {
            for ( const prop of props ) {
                if ( src[prop] === undefined ) { continue; }
                out[prop] = src[prop];
            }
        };
    }
    if (
        typeof resource !== 'object' ||
        safe.Object_toString.call(resource) !== '[object Request]'
    ) {
        out.url = `${resource}`;
    } else {
        let clone;
        try {
            clone = safe.Request_clone.call(resource);
        } catch {
        }
        collateFetchArgumentsFn.collateKnownProps(clone || resource, out);
    }
    if ( typeof options === 'object' && options !== null ) {
        collateFetchArgumentsFn.collateKnownProps(options, out);
    }
    return out;
}
function matchObjectPropertiesFn(propNeedles, ...objs) {
    const safe = safeSelf();
    const matched = [];
    for ( const obj of objs ) {
        if ( obj instanceof Object === false ) { continue; }
        for ( const [ prop, details ] of propNeedles ) {
            let value = obj[prop];
            if ( value === undefined ) { continue; }
            if ( typeof value !== 'string' ) {
                try { value = safe.JSON_stringify(value); }
                catch { }
                if ( typeof value !== 'string' ) { continue; }
            }
            if ( safe.testPattern(details, value) === false ) { return; }
            matched.push(`${prop}: ${value}`);
        }
    }
    return matched;
}
function parsePropertiesToMatchFn(propsToMatch, implicit = '') {
    const safe = safeSelf();
    const needles = new Map();
    if ( propsToMatch === undefined || propsToMatch === '' ) { return needles; }
    const options = { canNegate: true };
    for ( const needle of safe.String_split.call(propsToMatch, /\s+/) ) {
        let [ prop, pattern ] = safe.String_split.call(needle, ':');
        if ( prop === '' ) { continue; }
        if ( pattern !== undefined && /[^$\w -]/.test(prop) ) {
            prop = `${prop}:${pattern}`;
            pattern = undefined;
        }
        if ( pattern !== undefined ) {
            needles.set(prop, safe.initPattern(pattern, options));
        } else if ( implicit !== '' ) {
            needles.set(implicit, safe.initPattern(prop, options));
        }
    }
    return needles;
}
function jsonPruneFetchResponse(
    rawPrunePaths = '',
    rawNeedlePaths = '',
    ...varargs
) {
    const safe = safeSelf();
    const logPrefix = safe.makeLogPrefix('json-prune-fetch-response', rawPrunePaths, rawNeedlePaths);
    const extraArgs = safe.parseVarargs(varargs);
    const propNeedles = parsePropertiesToMatchFn(extraArgs.propsToMatch, 'url');
    const stackNeedle = safe.initPattern(extraArgs.stackToMatch || '', { canNegate: true });
    const logall = rawPrunePaths === '';
    const applyHandler = function(target, thisArg, args) {
        const fetchPromise = Reflect.apply(target, thisArg, args);
        if ( propNeedles.size !== 0 ) {
            const props = collateFetchArgumentsFn(...args);
            const matched = matchObjectPropertiesFn(propNeedles, props);
            if ( matched === undefined ) { return fetchPromise; }
            if ( safe.logLevel > 1 ) {
                safe.uboLog(logPrefix, `Matched "propsToMatch":\n\t${matched.join('\n\t')}`);
            }
        }
        return fetchPromise.then(responseBefore => {
            const response = responseBefore.clone();
            return response.json().then(objBefore => {
                if ( typeof objBefore !== 'object' ) { return responseBefore; }
                if ( logall ) {
                    safe.uboLog(logPrefix, safe.JSON_stringify(objBefore, null, 2));
                    return responseBefore;
                }
                const objAfter = objectPruneFn(
                    objBefore,
                    rawPrunePaths,
                    rawNeedlePaths,
                    stackNeedle,
                    extraArgs
                );
                if ( typeof objAfter !== 'object' ) { return responseBefore; }
                safe.uboLog(logPrefix, 'Pruned');
                const responseAfter = Response.json(objAfter, {
                    status: responseBefore.status,
                    statusText: responseBefore.statusText,
                    headers: responseBefore.headers,
                });
                Object.defineProperties(responseAfter, {
                    ok: { value: responseBefore.ok },
                    redirected: { value: responseBefore.redirected },
                    type: { value: responseBefore.type },
                    url: { value: responseBefore.url },
                });
                return responseAfter;
            }).catch(reason => {
                safe.uboErr(logPrefix, 'Error:', reason);
                return responseBefore;
            });
        }).catch(reason => {
            safe.uboErr(logPrefix, 'Error:', reason);
            return fetchPromise;
        });
    };
    self.fetch = new Proxy(self.fetch, {
        apply: applyHandler
    });
}
function jsonEditFetchResponseFn(trusted, jsonq = '', ...varargs) {
    const safe = safeSelf();
    const logPrefix = safe.makeLogPrefix(
        `${trusted ? 'trusted-' : ''}json-edit-fetch-response`,
        jsonq
    );
    const jsonp = JSONPath.create(jsonq);
    if ( jsonp.valid === false || jsonp.value !== undefined && trusted !== true ) {
        return safe.uboLog(logPrefix, 'Bad JSONPath query');
    }
    const extraArgs = safe.parseVarargs(varargs);
    const propNeedles = parsePropertiesToMatchFn(extraArgs.propsToMatch, 'url');
    proxyApplyFn('fetch', function(context) {
        const args = context.callArgs;
        const fetchPromise = context.reflect();
        if ( propNeedles.size !== 0 ) {
            const props = collateFetchArgumentsFn(...args);
            const matched = matchObjectPropertiesFn(propNeedles, props);
            if ( matched === undefined ) { return fetchPromise; }
            if ( safe.logLevel > 1 ) {
                safe.uboLog(logPrefix, `Matched "propsToMatch":\n\t${matched.join('\n\t')}`);
            }
        }
        return fetchPromise.then(responseBefore => {
            const response = responseBefore.clone();
            return response.json().then(obj => {
                if ( typeof obj !== 'object' ) { return responseBefore; }
                const objAfter = jsonp.apply(obj);
                if ( objAfter === undefined ) { return responseBefore; }
                safe.uboLog(logPrefix, 'Edited');
                const responseAfter = Response.json(objAfter, {
                    status: responseBefore.status,
                    statusText: responseBefore.statusText,
                    headers: responseBefore.headers,
                });
                Object.defineProperties(responseAfter, {
                    ok: { value: responseBefore.ok },
                    redirected: { value: responseBefore.redirected },
                    type: { value: responseBefore.type },
                    url: { value: responseBefore.url },
                });
                return responseAfter;
            }).catch(reason => {
                safe.uboErr(logPrefix, 'Error:', reason);
                return responseBefore;
            });
        }).catch(reason => {
            safe.uboErr(logPrefix, 'Error:', reason);
            return fetchPromise;
        });
    });
}
class JSONPath {
    static create(query) {
        const jsonp = new JSONPath();
        jsonp.compile(query);
        return jsonp;
    }
    static toJSON(obj, stringifier, ...args) {
        return (stringifier || JSON.stringify)(obj, ...args)
            .replace(/\//g, '\\/');
    }
    static keys = Object.keys;
    static entries = Object.entries;
    static hasOwn = Object.hasOwn;
    static Regex = RegExp;
    get value() {
        return this.#compiled && this.#compiled.rval;
    }
    set value(v) {
        if ( this.#compiled === undefined ) { return; }
        this.#compiled.rval = v;
    }
    get valid() {
        return this.#compiled !== undefined;
    }
    compile(query) {
        this.#compiled = undefined;
        this.v2 = query.startsWith('v2:');
        if ( this.v2 ) { query = query.slice(3); }
        const r = this.#compile(query, 0);
        if ( r === undefined ) { return; }
        if ( r.i !== query.length ) {
            let val;
            if ( query.startsWith('=', r.i) ) {
                const match = this.#reRval.exec(query.slice(r.i));
                if ( match ) {
                    r.modify = match[1];
                    val = match[2];
                } else {
                    val = query.slice(r.i+1);
                }
            } else if ( query.startsWith('+=', r.i) ) {
                r.modify = '+';
                val = query.slice(r.i+2);
            }
            try { r.rval = JSON.parse(val); }
            catch { return; }
        }
        r.v2 = this.v2;
        this.#compiled = r;
    }
    evaluate(root) {
        if ( this.valid === false ) { return []; }
        this.#root = { '$': root };
        const paths = this.#evaluate(this.#compiled.steps, []);
        this.#root = null;
        return paths;
    }
    apply(root) {
        if ( this.valid === false ) { return; }
        const { rval } = this.#compiled;
        this.#root = { '$': root };
        const paths = this.#evaluate(this.#compiled.steps, []);
        let i = paths.length
        if ( i === 0 ) { this.#root = null; return; }
        while ( i-- ) {
            const { obj, key } = this.#resolvePath(paths[i]);
            if ( obj === undefined ) { continue; }
            if ( rval !== undefined ) {
                this.#modifyVal(obj, key);
            } else if ( Array.isArray(obj) && typeof key === 'number' ) {
                obj.splice(key, 1);
            } else {
                delete obj[key];
            }
        }
        const result = this.#root['$'] ?? null;
        this.#root = null;
        return result;
    }
    dump() {
        return JSON.stringify(this.#compiled);
    }
    toJSON(obj, ...args) {
        return JSONPath.toJSON(obj, null, ...args)
    }
    get [Symbol.toStringTag]() {
        return 'JSONPath';
    }
    #UNDEFINED = 0;
    #ROOT = 1;
    #CURRENT = 2;
    #CHILDREN = 3;
    #DESCENDANTS = 4;
    #QUANTIFIER = 5;
    #reUnquotedIdentifier = /^[A-Za-z_][\w]*|^\*/;
    #reExpr = /^\s*([!=^$*]=|[<>]=?)\s*(.+?)\]/;
    #reIndice = /^-?\d+/;
    #reRval = /^=([a-z]+)\((.+)\)$/;
    #reQuantifier = /^\{(\d+|\d+,\d+|\d+,|,\d+)\};\$/;
    #root;
    #compiled;
    #compile(query, i) {
        if ( query.length === 0 ) { return; }
        const steps = [];
        let c = query.charCodeAt(i);
        if ( c === 0x24 /* $ */ ) {
            steps.push({ mv: this.#ROOT });
            i += 1;
        } else if ( c === 0x40 /* @ */ ) {
            steps.push({ mv: this.#CURRENT });
            i += 1;
        } else {
            steps.push({ mv: i === 0 ? this.#ROOT : this.#CURRENT });
        }
        let mv = this.#UNDEFINED;
        for (;;) {
            if ( i === query.length ) { break; }
            c = query.charCodeAt(i);
            if ( c === 0x20 /* whitespace */ ) {
                i += 1;
                continue;
            }
            // Dot accessor syntax
            if ( c === 0x2E /* . */ ) {
                if ( mv !== this.#UNDEFINED ) { return; }
                if ( query.startsWith('..', i) ) {
                    mv = this.#DESCENDANTS;
                    i += 2;
                } else {
                    mv = this.#CHILDREN;
                    i += 1;
                }
                continue;
            }
            if ( c === 0x3B /* ; */ ) {
                if ( query.startsWith(';$', i) === false ) { return; }
                steps.push(
                    { mv: this.#QUANTIFIER, min: 1, max: 1e6 },
                    { mv: this.#ROOT }
                );
                i += 2;
                mv = this.#UNDEFINED;
                continue;
            }
            if ( c === 0x7B /* { */ ) {
                const match = this.#reQuantifier.exec(query.slice(i));
                if ( match === null ) { return; }
                const comma = match[1].indexOf(',');
                let min, max;
                if ( comma === -1 ) {
                    min = max = parseInt(match[1]);
                } else {
                    min = parseInt(match[1].slice(0, comma)) || 0;
                    max = parseInt(match[1].slice(comma+1)) || 1e6;
                }
                steps.push(
                    { mv: this.#QUANTIFIER, min, max },
                    { mv: this.#ROOT }
                );
                i += match[0].length;
                mv = this.#UNDEFINED;
                continue;
            }
            if ( c !== 0x5B /* [ */ ) {
                if ( mv === this.#UNDEFINED ) {
                    const step = steps.at(-1);
                    if ( step === undefined ) { return; }
                    const j = this.#compileExpr(query, step, i);
                    if ( j ) { i = j; }
                    break;
                }
                const r = this.#consumeUnquotedIdentifier(query, i);
                if  ( r === undefined ) { return; }
                steps.push({ mv, k: r.s });
                i = r.i;
                mv = this.#UNDEFINED;
                continue;
            }
            // Bracket accessor syntax
            if ( mv === this.#CHILDREN ) { return; }
            if ( query.startsWith('[?', i) ) {
                const not = query.charCodeAt(i+2) === 0x21 /* ! */ ? 1 : 0;
                const j = i + 2 + not;
                const r = this.#compile(query, j);
                if ( r === undefined ) { return; }
                if ( query.startsWith(']', r.i) === false ) { return; }
                if ( not ) { r.steps.at(-1).not = true; }
                steps.push({ mv: mv || this.#CHILDREN, steps: r.steps });
                i = r.i + 1;
                mv = this.#UNDEFINED;
                continue;
            }
            if ( query.startsWith('[*]', i) ) {
                mv ||= this.#CHILDREN;
                steps.push({ mv, k: '*' });
                i += 3;
                mv = this.#UNDEFINED;
                continue;
            }
            const r = this.#consumeIdentifier(query, i+1);
            if ( r === undefined ) { return; }
            mv ||= this.#CHILDREN;
            steps.push({ mv, k: r.s });
            i = r.i + 1;
            mv = this.#UNDEFINED;
        }
        if ( steps.length === 0 ) { return; }
        if ( mv !== this.#UNDEFINED ) { return; }
        return { steps, i };
    }
    #evaluate(steps, pathin) {
        let resultset = [];
        if ( Array.isArray(steps) === false ) { return resultset; }
        for ( const step of steps ) {
            switch ( step.mv ) {
            case this.#ROOT:
                resultset = [ [ '$' ] ];
                break;
            case this.#CURRENT:
                if ( step.op ) {
                    const { obj, key } = this.#resolvePath(pathin);
                    if ( obj === undefined ) { return []; }
                    const outcome = this.#evaluateExpr(step, obj, key);
                    if ( outcome !== true ) { break; }
                }
                resultset = [ pathin ];
                break;
            case this.#CHILDREN:
            case this.#DESCENDANTS: {
                if ( resultset.length === 0 ) { break; }
                resultset = this.#getMatches(resultset, step);
                break;
            }
            case this.#QUANTIFIER: {
                const { length } = resultset;
                if ( length < step.min || length > step.max ) { return []; }
                resultset = [];
                break;
            }
            default:
                break;
            }
        }
        return resultset;
    }
    #getMatches(listin, step) {
        const listout = [];
        for ( const pathin of listin ) {
            const { value: owner } = this.#resolvePath(pathin);
            if ( owner === undefined ) { continue; }
            if ( step.steps ) {
                this.#getMatchesFromExpr(pathin, step, owner, listout);
                continue;
            }
            const iter = this.#expandKey(owner, step.k);
            if ( iter ) {
                for ( const k of iter ) {
                    const outcome = this.#evaluateExpr(step, owner, k);
                    if ( outcome !== true ) { continue; }
                    listout.push([ ...pathin, k ]);
                }
            }
            if ( step.mv !== this.#DESCENDANTS ) { continue; }
            for ( const { obj, key, path } of this.#getDescendants(owner, true) ) {
                const iter = this.#expandKey(obj[key], step.k);
                if ( iter === undefined ) { continue; }
                for ( const k of iter ) {
                    const outcome = this.#evaluateExpr(step, obj[key], k);
                    if ( outcome !== true ) { continue; }
                    listout.push([ ...pathin, ...path, k ]);
                }
            }
        }
        return listout;
    }
    #expandKey(owner, k) {
        if ( typeof owner !== 'object' || owner === null ) { return; }
        if ( Array.isArray(k) ) {
            const out = [];
            for ( const a of k ) {
                const iter = this.#expandKey(owner, a);
                if ( iter === undefined ) { continue; }
                out.push(...iter);
            }
            return out;
        }
        if ( typeof k === 'number' ) {
            if ( Array.isArray(owner) === false ) { return; }
            return [ k >= 0 ? k : owner.length + k ];
        }
        if ( k === '*' ) {
            if ( Array.isArray(owner) ) { return owner.keys(); }
            return JSONPath.keys(owner);
        }
        if ( k instanceof JSONPath.Regex ) {
            const out = [];
            for ( const key of JSONPath.keys(owner) ) {
                if ( k.test(key) === false ) { continue; }
                out.push(key);
            }
            return out;
        }
        return [ k ];
    }
    #getMatchesFromExpr(pathin, step, owner, out) {
        const recursive = step.mv === this.#DESCENDANTS;
        const v2 = this.#compiled.v2 || recursive || Array.isArray(owner);
        for ( const { path } of this.#getDescendants(owner, recursive) ) {
            const q = v2 ? [ ...pathin, ...path ] : pathin;
            const r = this.#evaluate(step.steps, q);
            if ( Boolean(r?.length) === false ) { continue; }
            out.push(q);
            if ( v2 === false ) { break; }
        }
    }
    #getDescendants(v, recursive) {
        const iterator = {
            next() {
                const n = this.stack.length;
                if ( n === 0 ) {
                    this.value = undefined;
                    this.done = true;
                    return this;
                }
                const details = this.stack[n-1];
                const entry = details.keys.next();
                if ( entry.done ) {
                    this.stack.pop();
                    this.path.pop();
                    return this.next();
                }
                this.path[n-1] = entry.value;
                this.value = {
                    obj: details.obj,
                    key: entry.value,
                    path: this.path.slice(),
                };
                const v = this.value.obj[this.value.key];
                if ( recursive ) {
                    if ( Array.isArray(v) ) {
                        this.stack.push({ obj: v, keys: v.keys() });
                    } else if ( typeof v === 'object' && v !== null ) {
                        this.stack.push({ obj: v, keys: JSONPath.keys(v).values() });
                    }
                }
                return this;
            },
            path: [],
            value: undefined,
            done: false,
            stack: [],
            [Symbol.iterator]() { return this; },
        };
        if ( Array.isArray(v) ) {
            iterator.stack.push({ obj: v, keys: v.keys() });
        } else if ( typeof v === 'object' && v !== null ) {
            iterator.stack.push({ obj: v, keys: JSONPath.keys(v).values() });
        }
        return iterator;
    }
    #consumeIdentifier(query, i) {
        const keys = [];
        let needIdentifier = true;
        while ( i < query.length ) {
            const c0 = query.charCodeAt(i);
            if ( c0 === 0x5D /* ] */ ) { break; }
            if ( c0 === 0x20 /* SPACE */ ) {
                i += 1;
                continue;
            }
            if ( c0 === 0x2C /* , */ ) {
                if ( needIdentifier ) { return; }
                i += 1;
                needIdentifier = true;
                continue;
            }
            if ( c0 === 0x22 /* " */ || c0 === 0x27 /* ' */ ) {
                const r = this.#untilChar(query, c0, i+1);
                if ( r === undefined ) { return; }
                keys.push(r.s);
                i = r.i;
                needIdentifier = false;
                continue;
            }
            if ( c0 === 0x2D /* - */ || c0 >= 0x30 && c0 <= 0x39 ) {
                const match = this.#reIndice.exec(query.slice(i));
                if ( match === null ) { return; }
                const indice = parseInt(query.slice(i), 10);
                keys.push(indice);
                i += match[0].length;
                needIdentifier = false;
                continue;
            }
            if ( this.v2 ) { return; }
            const r = this.#consumeUnquotedIdentifier(query, i);
            if ( r === undefined ) { return; }
            keys.push(r.s);
            i = r.i;
        }
        if ( needIdentifier ) { return; }
        return { s: keys.length === 1 ? keys[0] : keys, i };
    }
    #consumeUnquotedIdentifier(query, i) {
        if ( query.charCodeAt(i) === 0x2F /* / */ ) {
            const r = this.#untilChar(query, 0x2F, i+1);
            if ( r === undefined ) { return; }
            let re;
            try { re = new JSONPath.Regex(r.s); } catch { return; }
            return { s: re, i: r.i };
        }
        const match = this.#reUnquotedIdentifier.exec(query.slice(i));
        if ( match === null ) { return; }
        return { s: match[0], i: i + match[0].length };
    }
    #untilChar(query, targetCharCode, i) {
        const len = query.length;
        const parts = [];
        let beg = i, end = i;
        for (;;) {
            if ( end === len ) { return; }
            const c = query.charCodeAt(end);
            if ( c === targetCharCode ) {
                parts.push(query.slice(beg, end));
                end += 1;
                break;
            }
            if ( c === 0x5C /* \ */ && (end+1) < len ) {
                const d = query.charCodeAt(end+1);
                if ( d === targetCharCode ) {
                    parts.push(query.slice(beg, end));
                    end += 1;
                    beg = end;
                }
            }
            end += 1;
        }
        return { s: parts.join(''), i: end };
    }
    #compileExpr(query, step, i) {
        if ( query.startsWith('=/', i) ) {
            const r = this.#untilChar(query, 0x2F /* / */, i+2);
            if ( r === undefined ) { return i; }
            const match = /^[i]/.exec(query.slice(r.i));
            try {
                step.rval = new JSONPath.Regex(r.s, match && match[0] || undefined);
            } catch { return; }
            step.op = 're';
            if ( match ) { r.i += match[0].length; }
            return r.i;
        }
        const match = this.#reExpr.exec(query.slice(i));
        if ( match === null ) { return; }
        const op = match[1], rval = match[2];
        if ( rval.charCodeAt(0) === 0x27 /* ' */ ) {
            const r = this.#untilChar(rval, 0x27, 1);
            if ( r === undefined ) { return; }
            step.rval = r.s;
            step.op = op;
        } else {
            try {
                step.rval = JSON.parse(rval);
                step.op = op;
            } catch { return; }
        }
        return i + match[0].length - 1;
    }
    #resolvePath(path) {
        if ( path.length === 0 ) { return { value: this.#root }; }
        const key = path.at(-1);
        let obj = this.#root
        for ( let i = 0, n = path.length-1; i < n; i++ ) {
            obj = obj[path[i]];
            if ( obj instanceof Object === false ) { return {}; }
        }
        return { obj, key, value: obj[key] };
    }
    #evaluateExpr(step, owner, k) {
        if ( owner === undefined || owner === null ) { return; }
        const hasOwn = owner[k] !== undefined || JSONPath.hasOwn(owner, k);
        if ( step.op !== undefined && hasOwn === false ) { return; }
        const target = step.not !== true;
        const v = owner[k];
        switch ( step.op ) {
        case '==': return (v === step.rval) === target;
        case '!=': return (v !== step.rval) === target;
        case  '<': return (v < step.rval) === target;
        case '<=': return (v <= step.rval) === target;
        case  '>': return (v > step.rval) === target;
        case '>=': return (v >= step.rval) === target;
        case '^=': return `${v}`.startsWith(step.rval) === target;
        case '$=': return `${v}`.endsWith(step.rval) === target;
        case '*=': return `${v}`.includes(step.rval) === target;
        case 're': return step.rval.test(`${v}`);
        default: break;
        }
        return hasOwn === target;
    }
    #modifyVal(obj, key) {
        let { modify, rval } = this.#compiled;
        if ( typeof rval === 'string' ) {
            rval = rval.replace('${now}', `${Date.now()}`);
        }
        switch ( modify ) {
        case undefined:
            obj[key] = rval;
            break;
        case '+': {
            if ( rval instanceof Object === false ) { return; }
            const lval = obj[key];
            if ( lval instanceof Object === false ) { return; }
            if ( Array.isArray(lval) ) {
                if ( Array.isArray(rval) ) {
                    lval.push(...rval); break;
                }
                lval.push(rval); break;
            }
            for ( const [ k, v ] of JSONPath.entries(rval) ) {
                lval[k] = v;
            }
            break;
        }
        case 'call': {
            const entries = rval.slice();
            if ( entries.length < 2 ) { break; }
            entries.forEach((a, i, aa) => {
                if ( a === '${obj}' ) { aa[i] = obj; }
                else if ( a === '${key}' ) { aa[i] = key; }
                else if ( a === '${val}' ) { aa[i] = obj[key]; }
            });
            const instance = entries[0] ?? self;
            instance[entries[1]](...entries.slice(2));
            break;
        }
        case 'repl': {
            const lval = obj[key];
            if ( typeof lval !== 'string' ) { return; }
            if ( this.#compiled.re === undefined ) {
                this.#compiled.re = null;
                try {
                    this.#compiled.re = rval.regex !== undefined
                        ? new JSONPath.Regex(rval.regex, rval.flags)
                        : new JSONPath.Regex(rval.pattern.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'));
                } catch { }
            }
            if ( this.#compiled.re === null ) { return; }
            obj[key] = lval.replace(this.#compiled.re, rval.replacement);
            break;
        }
        default:
            break;
        }
    }
}
function jsonEditFetchResponse(jsonq = '', ...args) {
    jsonEditFetchResponseFn(false, jsonq, ...args);
}
try {
jsonPrune("entries.[-].command.reelWatchEndpoint.adClientParams.isAd")
} catch ( e ) { }
try {
setConstant("ytInitialPlayerResponse.playerAds", "undefined")
} catch ( e ) { }
try {
jsonPruneFetchResponse("reelWatchSequenceResponse.entries.[-].command.reelWatchEndpoint.adClientParams.isAd entries.[-].command.reelWatchEndpoint.adClientParams.isAd", "", "propsToMatch", "url:/reel_watch_sequence?")
} catch ( e ) { }
try {
setConstant("playerResponse.adPlacements", "undefined")
} catch ( e ) { }
try {
setConstant("ytInitialPlayerResponse.adPlacements", "undefined")
} catch ( e ) { }
try {
setConstant("ytInitialPlayerResponse.adSlots", "undefined")
} catch ( e ) { }
try {
jsonPrune("playerResponse.adPlacements playerResponse.playerAds playerResponse.adSlots adPlacements playerAds adSlots important")
} catch ( e ) { }
try {
jsonEditFetchResponse("..itemSectionRenderer.contents.*[?.adSlotRenderer]", "propsToMatch", "/get_watch")
} catch ( e ) { }

const install=()=>{if(!document.documentElement||document.getElementById('pulse-engine-style'))return;const style=document.createElement('style');style.id='pulse-engine-style';style.textContent="a[href^=\"https://tour.mrskin.com/\"]{display:none!important}\na[href^=\"http://www.adultempire.com/unlimited/promo?\"][href*=\"&partner_id=\"]{display:none!important}\na[href^=\"https://go.xxxiijmp.com\"]{display:none!important}\namp-img[width=\"   300\"]{display:none!important}\ndiv[style^=\"width:    300px; height:    250px;\"]{display:none!important}\na[href^=\"https://s.zlinkt.com/\"]{display:none!important}\n[data-identity=\"adhesive-ad\"]{display:none!important}\na[href^=\"https://prf.hn/click/\"][href*=\"/camref:\"] > img{display:none!important}\nzeus-ad{display:none!important}\na[href^=\"https://a.bestcontentoperation.top/\"]{display:none!important}\n[class^=\"div-gpt-ad\"]:not([style^=\"width: 1px; height: 1px; position: absolute; left: -10000px; top: -\"]){display:none!important}\n[href^=\"https://zone.gotrackier.com/\"]{display:none!important}\na[href^=\"https://pb-front.com/\"]{display:none!important}\na[href^=\"https://s.zlink7.com/\"]{display:none!important}\na[onclick=\"openAuc();\"]{display:none!important}\n[href^=\"http://www.mypillow.com/\"] > img{display:none!important}\n[data-testid=\"commercial-label-taboola\"]{display:none!important}\na[href^=\"https://activate-game.com/\"]{display:none!important}\n[href^=\"//taghaugh.com/\"]{display:none!important}\na[href^=\"https://lnkxt.bannerator.com/\"]{display:none!important}\na[href^=\"https://combodef.com/\"]{display:none!important}\na[href^=\"https://track.wg-aff.com\"]{display:none!important}\na[href^=\"https://aweptjmp.com/\"]{display:none!important}\na[href^=\"https://bngprm.com/\"]{display:none!important}\na[href^=\"https://track.aftrk5.com/\"]{display:none!important}\na[href*=\"//jjgirls.com/sex/Chaturbate\"]{display:none!important}\n[data-revive-zoneid]{display:none!important}\na[href^=\"https://ads.planetwin365affiliate.com/\"]{display:none!important}\n[href^=\"https://ad.admitad.com/\"]{display:none!important}\nspan[data-ez-ph-id]{display:none!important}\na[href^=\"https://t.ajrkm1.com/\"]{display:none!important}\na[href^=\"https://ad.doubleclick.net/\"]{display:none!important}\na[href*=\".g2afse.com/\"]{display:none!important}\n[href^=\"http://clicks.totemcash.com/\"]{display:none!important}\nbody > div[style$=\"z-index: 2147483647; top: 0px; left: 0px; position: fixed; display: block;\"]{display:none!important}\na[href^=\"https://twinrdsrv.com/\"]{display:none!important}\ndiv[id^=\"vuukle-ad-\"]{display:none!important}\nhtml[lang=\"vi\"] .sspp-area{display:none!important}\na[href^=\"https://www.bet365.com/\"][href*=\"affiliate=\"]{display:none!important}\n[href^=\"https://join.enjoyx.com/track/\"]{display:none!important}\n[href^=\"https://www.mypillow.com/\"] > img{display:none!important}\n[class^=\"s2nPlayer\"]{display:none!important}\na[href^=\"https://s.zlinkn.com/\"]{display:none!important}\na[href*=\".click/?z=\"][href*=\"&n\"]{display:none!important}\n[data-testid=\"ad_testID\"]{display:none!important}\na[href^=\"https://track.1234sd123.com/\"]{display:none!important}\nimg[height=\"   90\"][width=\"   728\"]{display:none!important}\na[href^=\"https://porntubemate.com/\"]{display:none!important}\na[href^=\"https://lijavaxa.com/\"]{display:none!important}\na[href^=\"https://mediaserver.entainpartners.com/renderBanner.do?\"]{display:none!important}\n[href^=\"https://join.playboyplus.com/track/\"]{display:none!important}\nbutton.btn[data-openuri*=\"/4/\"]{display:none!important}\ndiv[style^=\"z-index: 999999; background-image: url(\\\"data:image/gif;base64,\"][style$=\"position: absolute;\"]{display:none!important}\n[href^=\"https://fireads.online/\"]{display:none!important}\na[href^=\"https://offhandpump.com/\"]{display:none!important}\ndiv[style=\"position: fixed; display: block; width: 100%; height: 100%; inset: 0px; background-color: rgba(0, 0, 0, 0); z-index: 300000;\"]{display:none!important}\n[data-lnguri^=\"https://s3.amazonaws.com\"]{display:none!important}\na[href^=\"https://get-link.xyz/\"]{display:none!important}\na[href^=\"https://chaturbate.com/in/?\"]{display:none!important}\na[href^=\"https://wmctjd.com/\"]{display:none!important}\na[href^=\"https://ausoafab.net/\"]{display:none!important}\na[href^=\"https://stardomcoit.com/\"]{display:none!important}\na[href^=\"http://www.h4trck.com/\"]{display:none!important}\na[href^=\"https://www.purevpn.com/\"][href*=\"&utm_source=aff-\"]{display:none!important}\na[href^=\"https://www.toprevenuegate.com/\"]{display:none!important}\n[href^=\"https://t.mobtyb.com/\"]{display:none!important}\n[id^=\"google_ads_iframe_\"]{display:none!important}\na[href^=\"https://www.mrskin.com/account/\"]{display:none!important}\na[href^=\"https://go.strpjmp.com/\"]{display:none!important}\ndiv[id^=\"dfp-ad-\"]{display:none!important}\n[href=\"https://jdrucker.com/gold\"] > img{display:none!important}\na[href^=\"https://osfultrbriolenai.info/\"]{display:none!important}\napp-ad{display:none!important}\n[href^=\"https://a.acebet.com/api/click?\"]{display:none!important}\na[href^=\"https://track.cam4tracking.com/\"]{display:none!important}\n[data-ad-manager-id]{display:none!important}\n[href^=\"https://clicks.affstrack.com/\"] > img{display:none!important}\na[href^=\"https://Click.ggpickaff.com/\"]{display:none!important}\n[href^=\"https://www.herbanomic.com/\"] > img{display:none!important}\na[href^=\"https://track.ultravpn.com/\"]{display:none!important}\naside[aria-label=\"retailmedia.complimentarySponsored\"]{display:none!important}\n.ytp-suggested-action > .ytp-suggested-action-badge{display:none!important}\na[href*=\"&maxads=\"]{display:none!important}\nhtml[lang=\"tr\"] > body.has-footer-ad.has-pageskin-desktop a.pageskin-desktop-wrapper{display:none!important}\n[id^=\"ad_slider\"]{display:none!important}\na[href^=\"https://www.polybuzz.ai/\"] img.responsive-image{display:none!important}\n[href=\"//xxxrevpushclcdu.com/app.webp\"]{display:none!important}\na[href^=\"https://in.rabbtrk.com/\"]{display:none!important}\nfigure.movie-detail-banner div[style=\"position: absolute; top: 0px; left: 0px; width: 100%; height: 100%; z-index: 999; cursor: pointer;\"]{display:none!important}\nad-slot{display:none!important}\ndiv[id^=\"ad_position_\"]{display:none!important}\ndiv[data-adzone]{display:none!important}\na[onmousedown^=\"this.href='https://paid.outbrain.com/network/redir?\"] + .ob_source{display:none!important}\nimg[height=\"   250\"][width=\"   300\"]{display:none!important}\na[href^=\"https://pb-imc.com/\"]{display:none!important}\na[href*=\".adsrv.eacdn.com/\"]{display:none!important}\na[href*=\"/jump/next.php?r=\"]{display:none!important}\n[class^=\"div-gpt-ad\"]{display:none!important}\na[href^=\"https://a2.adform.net/\"]{display:none!important}\na[href^=\"https://syndication.dynsrvtbg.com/\"]{display:none!important}\na[href^=\"https://www.adultempire.com/\"][href*=\"?partner_id=\"]{display:none!important}\n[href^=\"https://www.avantlink.com/click.php\"] img{display:none!important}\na[href^=\"https://t.acam.link/\"]{display:none!important}\ndiv[id^=\"div-gpt-\"]{display:none!important}\n[data-ez-name]{display:none!important}\na[href^=\"https://s.zlinkd.com/\"]{display:none!important}\n[href^=\"//mellowads.com/\"]{display:none!important}\na[href^=\"https://www.get-express-vpn.com/offer/\"]{display:none!important}\ndiv[style^=\"width:    160px; height:    600px; margin: 0px auto; min-width: 160px; min-height: 600px;\"]{display:none!important}\nimg[src^=\"https://s-img.adskeeper.com/\"]{display:none!important}\na[href^=\"https://gml-grp.com/\"]{display:none!important}\na[href^=\"https://syndicate.contentsserved.com/\"]{display:none!important}\nlazy-list > ad-slot-renderer{display:none!important}\n[href^=\"https://go.smljmp.com/\"]{display:none!important}\na[href^=\"http://www.friendlyduck.com/AF_\"]{display:none!important}\namp-img[height=\"   120\"]{display:none!important}\na[href^=\"http://sarcasmadvisor.com/\"]{display:none!important}\na[href^=\"https://go.hpyjmp.com\"]{display:none!important}\n[data-css-class=\"dfp-inarticle\"]{display:none!important}\n[href^=\"https://mypatriotsupply.com/\"] > img{display:none!important}\ndiv[id^=\"taboola-stream-\"]{display:none!important}\na[href^=\"https://www.googleadservices.com/pagead/aclk?\"] > img{display:none!important}\ndiv[id][style^=\"position: fixed; inset: 0px; z-index: 2147483647; background: black\"][style*=\"opacity: 0.01\"]{display:none!important}\n[id^=\"ad-wrap-\"]{display:none!important}\na[href^=\"https://www.dating-finder.com/signup/?ai_d=\"]{display:none!important}\n[href^=\"//producebreed.com/\"]{display:none!important}\nbody > #mdl-register.modal.anm-e.regnow{display:none!important}\na[href^=\"https://hot-growngames.life/\"]{display:none!important}\na[href^=\"https://camfapr.com/landing/click/\"]{display:none!important}\nins[style$=\"height:    90px; width:    728px;\"]{display:none!important}\namp-embed[type=\"taboola\"]{display:none!important}\n[href=\"//sexcams.plus/\"]{display:none!important}\na[href^=\"https://tm-offers.gamingadult.com/\"]{display:none!important}\nhtml[lang=\"tr\"] #aa-wp .container-ads-adjustment{display:none!important}\na[href^=\"https://www.brazzersnetwork.com/landing/\"]{display:none!important}\n[data-ad-name]{display:none!important}\na[href^=\"https://myclick-2.com/\"]{display:none!important}\na[href^=\"https://www.sheetmusicplus.com/\"][href*=\"?aff_id=\"]{display:none!important}\ndiv[data-contentexchange-widget]{display:none!important}\ndiv[id^=\"optidigital-adslot\"]{display:none!important}\ndiv[data-id-advertdfpconf]{display:none!important}\n[data-template-type=\"nativead\"]{display:none!important}\n.ytd-section-list-renderer > .ytd-item-section-renderer > ytd-search-pyv-renderer.ytd-item-section-renderer{display:none!important}\na[href^=\"https://go.dmzjmp.com\"]{display:none!important}\na[href^=\"https://ismlks.com/\"]{display:none!important}\ndiv[data-adname]{display:none!important}\nytm-rich-item-renderer > ad-slot-renderer{display:none!important}\na[href^=\"https://clixtrac.com/\"]{display:none!important}\nhtml[lang][style^=\"--main-bg-color\"] > body:not([class]):not([id]) > div#container[style=\"visibility: visible;\"] > div#banner[style=\"opacity:0\"] ~ main:not([class]):not([id]) > div#message{display:none!important}\ngpt-ad{display:none!important}\n[href^=\"https://awbbjmp.com/\"]{display:none!important}\na[href^=\"https://bodelen.com/\"]{display:none!important}\n[data-ad-width]:not([style$=\"left: -10000px !important; top: -1000px !important;\"]):not(.adsbygoogle){display:none!important}\na[href^=\"https://click.linksynergy.com/fs-bin/\"] > img{display:none!important}\n[href^=\"https://url.totaladblock.com/\"]{display:none!important}\na[href^=\"http://adultfriendfinder.com/go/\"]{display:none!important}\na[href^=\"https://go.xlviiirdr.com\"]{display:none!important}\na[href^=\"https://service.bv-aff-trx.com/\"]{display:none!important}\ndiv[id^=\"zergnet-widget\"]{display:none!important}\n[id^=\"section-ad-banner\"]{display:none!important}\na[href^=\"http://partners.etoro.com/\"]{display:none!important}\ndiv[ow-ad-unit-wrapper]{display:none!important}\na[href^=\"http://revolvemockerycopper.com/\"]{display:none!important}\nhtml[lang=\"tr\"] > body.has-footer-ad.has-pageskin-desktop a.pageskin-mobile-wrapper{display:none!important}\ndiv[aria-label=\"Ads\"]{display:none!important}\na[href^=\"https://datewhisper.life/\"]{display:none!important}\na[href^=\"https://go.cmtaffiliates.com/\"]{display:none!important}\n#contents > ytd-rich-item-renderer:has(> ytd-ad-slot-renderer){display:none!important}\ndiv[data-adunit-path]{display:none!important}\n#sticker-layer{display:none!important}\na[href^=\"https://gamingadlt.com/?offer=\"]{display:none!important}\na[href^=\"https://ab.advertiserurl.com/aff/\"]{display:none!important}\ndiv[id*=\"ScriptRoot\"]{display:none!important}\n[data-desktop-ad-id]{display:none!important}\na[href^=\"https://www.mrskin.com/tour\"]{display:none!important}\n[data-id^=\"div-gpt-ad\"]{display:none!important}\na[href^=\"https://svb-analytics.trackerrr.com/\"]{display:none!important}\n[href^=\"https://shiftnetwork.infusionsoft.com/go/\"] > img{display:none!important}\na[href^=\"https://upsups.click/\"]{display:none!important}\na[href^=\"https://www.infowarsstore.com/\"] > img{display:none!important}\na[href^=\"https://helmethomicidal.com/\"]{display:none!important}\na[href^=\"https://www.privateinternetaccess.com/\"] > img{display:none!important}\na[href^=\"https://billing.purevpn.com/aff.php\"] > img{display:none!important}\ndiv[class$=\"player-promo-col\"]{display:none!important}\na[href^=\"https://slkmis.com/\"]{display:none!important}\na.poly-title[href^=\"https://www.polybuzz.ai/\"]{display:none!important}\n[href^=\"https://adult.xyz/\"]{display:none!important}\na[href*=\"/go.php?a_aid=\"]{display:none!important}\na[href^=\"https://voluum.prom-xcams.com/\"]{display:none!important}\na[href^=\"https://www.geekbuying.com/dynamic-ads/\"]{display:none!important}\na[href^=\"https://go.etoro.com/\"] > img{display:none!important}\na[href^=\"https://random-affiliate.atimaze.com/\"]{display:none!important}\n[href^=\"https://ad1.adfarm1.adition.com/\"]{display:none!important}\n[href^=\"http://mypillow.com/\"] > img{display:none!important}\na[href^=\"https://go.rmhfrtnd.com\"]{display:none!important}\ndiv[data-ad-region]{display:none!important}\ndiv[id^=\"ad-position-\"]{display:none!important}\na[href^=\"https://prf.hn/click/\"][href*=\"/creativeref:\"] > img{display:none!important}\ndiv[style=\"position: fixed; inset: 0px; z-index: 2147483647; pointer-events: auto;\"]{display:none!important}\na[href^=\"https://paid.outbrain.com/network/redir?\"]{display:none!important}\na[id][href=\"https://hamrocsit.com\"][rel^=\"noopener noreferrer\"]{display:none!important}\na[href*=\".sadbaguette.com/\"]{display:none!important}\na[href^=\"https://landing.brazzersnetwork.com/\"]{display:none!important}\na[href^=\"//hoodingluster.com/\"]{display:none!important}\natf-ad-slot{display:none!important}\na[href^=\"https://go.markets.com/visit/?bta=\"]{display:none!important}\na[href^=\"https://ctrdwm.com/\"]{display:none!important}\na[href^=\"https://go.admjmp.com\"]{display:none!important}\na[href^=\"https://rixofa.com/\"]{display:none!important}\na[href^=\"https://tracker.loropartners.com/\"]{display:none!important}\na[href^=\"https://www.bang.com/?aff=\"]{display:none!important}\ndiv[id^=\"adngin-\"]{display:none!important}\na[href^=\"https://join.bannedsextapes.com/track/\"]{display:none!important}\napp-large-ad{display:none!important}\n[href^=\"https://turtlebids.irauctions.com/\"] img{display:none!important}\na[href^=\"https://mmwebhandler.aff-online.com/\"]{display:none!important}\n.ytd-rich-item-renderer.style-scope > .ytd-rich-item-renderer > .ytd-ad-slot-renderer.style-scope{display:none!important}\na[href^=\"https://a.candyai.love/\"]{display:none!important}\na[href^=\"https://quotationfirearmrevision.com/\"]{display:none!important}\na[href^=\"https://t.ajrkm3.com/\"]{display:none!important}\na[href^=\"https://tracking.avapartner.com/\"]{display:none!important}\na[href^=\"https://twinrdsyte.com/\"]{display:none!important}\n[data-asg-ins]{display:none!important}\n[onclick*=\"content.ad/\"]{display:none!important}\na[href^=\"https://a.medfoodsafety.com/\"]{display:none!important}\ndiv[id^=\"lazyad-\"]{display:none!important}\na[href^=\"https://lead1.pl/\"]{display:none!important}\na[href^=\"https://jaxofuna.com/\"]{display:none!important}\ndiv[style^=\"position:fixed;inset:0px;z-index:2147483647;background:black;opacity:0.01\"]{display:none!important}\nimg[width=\"728\"][height=\"90\"]{display:none!important}\na[href^=\"https://www.dql2clk.com/\"]{display:none!important}\n[class^=\"amp-ad-\"]{display:none!important}\n.ytd-two-column-browse-results-renderer > ytd-rich-grid-renderer > #masthead-ad.ytd-rich-grid-renderer{display:none!important}\n[data-m-ad-id]{display:none!important}\na[href^=\"https://join.dreamsexworld.com/\"]{display:none!important}\na[href^=\"https://juicyads.in/\"]{display:none!important}\na[href^=\"https://www8.smartadserver.com/\"]{display:none!important}\nbroadstreet-zone-container{display:none!important}\na[href^=\"https://italarizege.xyz/\"]{display:none!important}\ndiv[style=\"top: 0px; left: 0px; width: 1287px; height: 500px; position: fixed; z-index: 2147483647;\"]{display:none!important}\na[href^=\"https://go.tmrjmp.com\"]{display:none!important}\na[href^=\"https://ndt5.net/\"]{display:none!important}\na[href^=\"https://trk.softonixs.xyz/\"]{display:none!important}\na[href^=\"https://1winpb.com/\"]{display:none!important}\n[href^=\"https://glersakr.com/\"]{display:none!important}\nps-connatix-module{display:none!important}\na[id][href=\"https://toolkitspro.com\"][rel^=\"noopener noreferrer\"]{display:none!important}\na[href^=\"http://cam4com.go2cloud.org/aff_c?\"]{display:none!important}\na[href^=\"https://cam4com.go2cloud.org/\"]{display:none!important}\n[href^=\"//look.utndln.com/offer\"]{display:none!important}\ndiv#spot-holder.spot-holder[style=\"display: block;\"]{display:none!important}\n[style^=\"position: fixed; inset: 0px; z-index: 2147483647; background-color: transparent; pointer-events: auto; cursor: auto; margin: 0px; padding: 0px; display: block;\"]{display:none!important}\n[href^=\"http://referrer.website/\"]{display:none!important}\nhl-adsense{display:none!important}\na[href^=\"https://pb-track.com/\"]{display:none!important}\n[data-taboola-options]{display:none!important}\n[href^=\"https://www.cloudways.com/en/?id\"]{display:none!important}\napp-advertisement{display:none!important}\na[href^=\"https://syndication.exdynsrv.com/splash.php\"]{display:none!important}\na[href^=\"https://join.sexworld3d.com/track/\"]{display:none!important}\ndiv[data-dfp-id]{display:none!important}\na[href^=\"https://banners.livepartners.com/\"]{display:none!important}\nytm-companion-slot[data-content-type] > ytm-companion-ad-renderer{display:none!important}\na[href^=\"https://t.ajump1.com/\"]{display:none!important}\n[href^=\"https://charmingdatings.life/\"]{display:none!important}\n[class^=\"tile-picker__CitrusBannerContainer-sc-\"]{display:none!important}\na[href*=\".cfm?domain=\"][href*=\"&fp=\"]{display:none!important}\ndiv[data-ad-placeholder]{display:none!important}\n[data-freestar-ad][id]{display:none!important}\na[href^=\"https://engine.phn.doublepimp.com/\"]{display:none!important}\na[href^=\"https://s.gentlefieldpattern.com/\"]{display:none!important}\na[href^=\"https://go.awdeliverynet.com/\"]{display:none!important}\na[href^=\"https://wittered-mainging.com/\"]{display:none!important}\na[href^=\"https://www.financeads.net/tc.php?\"]{display:none!important}\naside[id^=\"adrotate_widgets-\"]{display:none!important}\na[href*=\".adglare.net/\"]{display:none!important}\n.ytd-watch-flexy > .ytd-watch-next-secondary-results-renderer > ytd-ad-slot-renderer.ytd-watch-next-secondary-results-renderer{display:none!important}\n[data-lnguri*=\"vipbox\"]{display:none!important}\n[href^=\"https://mypillow.com/\"] > img{display:none!important}\n[data-ad-cls]{display:none!important}\na[href^=\"https://go.trackitalltheway.com/\"]{display:none!important}\na[href^=\"https://go.xlviirdr.com\"]{display:none!important}\na[href^=\"https://losingoldfry.com/\"]{display:none!important}\na[href^=\"https://snowdayonline.xyz/\"]{display:none!important}\n[href^=\"https://join.girlsoutwest.com/\"]{display:none!important}\na[href^=\"https://adclick.g.doubleclick.net/\"]{display:none!important}\na[href^=\"https://clickadilla.com/\"]{display:none!important}\n[href=\"https://chaturbate.jjgirls.com/\"] > img{display:none!important}\na[href^=\"https://go.skinstrip.net\"][href*=\"?campaignId=\"]{display:none!important}\na[href^=\"https://go.xxxvjmp.com/\"]{display:none!important}\na[href^=\"https://static.fleshlight.com/images/banners/\"]{display:none!important}\na[href^=\"https://stellarcashflow.net/trck/\"]{display:none!important}\n[class^=\"adDisplay-module\"]{display:none!important}\na[href^=\"https://www.adxsrve.com/\"]{display:none!important}\na[href^=\"https://www.dating-finder.com/?ai_d=\"]{display:none!important}\niframe[scrolling=\"no\"][sandbox*=\"allow-popups allow-modals\"][style^=\"width: 100%; height: 100%; border: none;\"]{display:none!important}\na[href^=\"https://syndication.exoclick.com/\"]{display:none!important}\namp-img[width=\"   336\"]{display:none!important}\na[href^=\"https://claring-loccelkin.com/\"]{display:none!important}\na[href^=\"https://click.dtiserv2.com/\"]{display:none!important}\ndiv[id^=\"st\"][style^=\"z-index: 999999999;\"]{display:none!important}\na[href^=\"https://best-experience-cool.com/\"]{display:none!important}\na[href^=\"https://www.liquidfire.mobi/\"]{display:none!important}\na[onclick^=\"window.location.replace('https://random-affiliate.atimaze.com/\"]{display:none!important}\na[href^=\"https://ads.betfair.com/redirect.aspx?\"]{display:none!important}\nbody > div[style=\"position: fixed; inset: 0px; z-index: 2147483647; background: transparent; cursor: pointer;\"]{display:none!important}\ndiv[id^=\"mrec-leaderboard-\"]{display:none!important}\na[href^=\"https://123-stream.org/\"]{display:none!important}\n[data-block-type=\"ad\"]{display:none!important}\n[href^=\"https://rpwmct.com/\"]{display:none!important}\na[href^=\"https://ak.psaltauw.net/\"]{display:none!important}\ndiv[style^=\"position: fixed; inset: 0px; z-index: 2147483647; background: black; opacity: 0.01\"]{display:none!important}\na[href^=\"https://go.bushheel.com/\"]{display:none!important}\na[href^=\"//startgaming.net/tienda/\" i]{display:none!important}\ndiv[id^=\"adspot-\"]{display:none!important}\n[href^=\"https://wap4dollar.com/ad/nonadult/serve.php\"]{display:none!important}\na[href^=\"http://trk.globwo.online/\"]{display:none!important}\na[href^=\"https://click.candyoffers.com/\"]{display:none!important}\na[href^=\"https://streamate.com/landing/click/\"]{display:none!important}\na[href^=\"https://www.highcpmrevenuenetwork.com/\"]{display:none!important}\na[data-redirect^=\"https://paid.outbrain.com/network/redir?\"]{display:none!important}\na[href^=\"https://tatrck.com/\"]{display:none!important}\na[href^=\"https://click.hoolig.app/\"]{display:none!important}\na[href^=\"https://s.happyleafmotion.com/\"]{display:none!important}\na[href^=\"https://a.medfoodhome.com/\"]{display:none!important}\n[href^=\"https://zstacklife.com/\"] img{display:none!important}\na[href^=\"https://s.zlink3.com/\"]{display:none!important}\ndiv[id^=\"gpt_ad_\"]{display:none!important}\n[data-mobile-ad-id]{display:none!important}\ncitrus-ad-wrapper{display:none!important}\na[dontfo=\"\"][style$=\"position: absolute; z-index: 2147483647;\"]{display:none!important}\na[href^=\"https://bc.game/\"]{display:none!important}\na[href^=\"https://go.goaserv.com/\"]{display:none!important}\n[data-ad-module]{display:none!important}\na[href^=\"https://www.adskeeper.com\"]{display:none!important}\n[href^=\"https://track.fiverr.com/visit/\"] > img{display:none!important}\na[href^=\"https://www.goldenfrog.com/vyprvpn?offer_id=\"][href*=\"&aff_id=\"]{display:none!important}\na[href^=\"https://clicks.pipaffiliates.com/\"]{display:none!important}\ndiv[id^=\"ad-div-\"]{display:none!important}\na[href^=\"https://black77854.com/\"]{display:none!important}\n[data-ad-width]{display:none!important}\namp-ad-custom{display:none!important}\na[href^=\"https://ctjdwm.com/\"]{display:none!important}\n[href^=\"https://www.profitablegatecpm.com/\"]{display:none!important}\na[href^=\"http://join.brokestraightboys.com/track/\"]{display:none!important}\nguj-ad{display:none!important}\na[href^=\"https://get.surfshark.net/aff_c?\"][href*=\"&aff_id=\"] > img{display:none!important}\na[href^=\"https://go.xtbaffiliates.com/\"]{display:none!important}\na[href^=\"https://zirdough.net/\"]{display:none!important}\ndisplay-ad-component{display:none!important}\ndiv[id^=\"sticky_ad_\"]{display:none!important}\nins[id][style*=\"--gn-ov-ad-height\"]{display:none!important}\na[href^=\"https://natour.naughtyamerica.com/track/\"]{display:none!important}\n.grid.ytd-browse > #primary > .style-scope > .ytd-rich-grid-renderer > .ytd-rich-grid-renderer > .ytd-ad-slot-renderer{display:none!important}\na[href^=\"https://go.xlvirdr.com\"]{display:none!important}\na[href^=\"https://www.nutaku.net/signup/landing/\"]{display:none!important}\na[href^=\"https://go.mnaspm.com/\"]{display:none!important}\na[href^=\"https://go.grinsbest.com/\"]{display:none!important}\n#description-inner > ytd-merch-shelf-renderer > #main.ytd-merch-shelf-renderer{display:none!important}\n[href^=\"https://antiagingbed.com/discount/\"] > img{display:none!important}\na[href^=\"https://ads.leovegas.com/\"]{display:none!important}\n[href=\"https://t.me/Russia_Vs_Ukraine_War3\"]{display:none!important}\na[href^=\"https://s.zlinkr.com/\"]{display:none!important}\na[href^=\"https://traffdaq.com/\"]{display:none!important}\ndiv[data-ad-targeting]{display:none!important}\n[data-cl-spot-id]{display:none!important}\n[href^=\"https://istlnkcl.com/\"]{display:none!important}\n[href^=\"https://www.targetingpartner.com/\"]{display:none!important}\n[href^=\"https://exi8ef83z9.com/\"]{display:none!important}\na[href^=\"https://bongacams2.com/track?\"]{display:none!important}\na[href^=\" https://www.friendlyduck.com/AF_\"]{display:none!important}\na[href^=\"https://go.xlivrdr.com\"]{display:none!important}\na[href^=\"https://trk.nfl-online-streams.club/\"]{display:none!important}\na[href^=\"https://s.cant3am.com/\"]{display:none!important}\na[href^=\"https://track.totalav.com/\"]{display:none!important}\na[href^=\"https://postback1win.com/\"]{display:none!important}\n[href*=\"/afu.php\"]{display:none!important}\n[href^=\"https://ap.octopuspop.com/click/\"] > img{display:none!important}\nins.adsbygoogle[data-ad-client]{display:none!important}\n[href^=\"https://affiliate.fastcomet.com/\"] > img{display:none!important}\n[src^=\"//dombnrs.com/\"]{display:none!important}\na[href^=\"https://www.friendlyduck.com/AF_\"]{display:none!important}\n[href^=\"https://mystore.com/\"] > img{display:none!important}\ndiv[id^=\"google_dfp_\"]{display:none!important}\n[data-testid=\"prism-ad-wrapper\"]{display:none!important}\n[href^=\"https://www.hostg.xyz/\"] > img{display:none!important}\na[href^=\"https://golinks.work/\"]{display:none!important}\na[href^=\"https://a.bestcontentfood.top/\"]{display:none!important}\n[data-ad-manager-id]:not([style$=\"left: -10000px !important; top: -1000px !important;\"]):not(.adsbygoogle){display:none!important}\ndiv[id][class][style^=\"display\"][style*=\"hidden\"][style*=\"width:   \"]{display:none!important}\n.ytd-watch-flexy > ytd-merch-shelf-renderer > #main.ytd-merch-shelf-renderer{display:none!important}\na[data-oburl^=\"https://paid.outbrain.com/network/redir?\"]{display:none!important}\ndiv[id^=\"crt-\"][style]{display:none!important}\na[href^=\"http://sneakyadministration.com/\"]{display:none!important}\na[href^=\"https://fc.lc/ref/\"]{display:none!important}\ndiv[data-adunit]{display:none!important}\na[href^=\"https://go.xxxjmp.com\"]{display:none!important}\ndiv[id^=\"div-gpt-\"]:not([style^=\"width: 1px; height: 1px; position: absolute; left: -10000px; top: -\"]):not([style=\"pointer-events: none; height: 1px; width: 0px; opacity: 0; visibility: hidden; position: fixed; bottom: 0px;\"]){display:none!important}\na[href^=\"https://go.xxxijmp.com\"]{display:none!important}\na[href^=\"https://go.nordvpn.net/aff\"] > img{display:none!important}\na[href^=\"https://go.hpyrdr.com/\"]{display:none!important}\namp-fx-flying-carpet{display:none!important}\na[href^=\"https://join.virtualtaboo.com/track/\"]{display:none!important}\nhtml[lang=\"tr\"] #aa-wp .rnd-ads-adjustment{display:none!important}\n#shopping-timely-shelf{display:none!important}\n[href^=\"https://klsdee.com/\"]{display:none!important}\n[name^=\"google_ads_iframe\"]{display:none!important}\na[href^=\"http://com-1.pro/\"]{display:none!important}\na[href^=\"https://track.aftrk3.com/\"]{display:none!important}\na[href^=\"https://pubads.g.doubleclick.net/\"]{display:none!important}\n[href^=\"https://join3.bannedsextapes.com\"]{display:none!important}\n[href^=\"https://popcash.net/\"]{display:none!important}\na[href^=\"https://iqbroker.com/\"][href*=\"?aff=\"]{display:none!important}\nhtml[lang=\"tr\"] > body.has-footer-ad.has-pageskin-desktop a.pageskin-click-right{display:none!important}\na[href^=\"https://awptjmp.com/\"]{display:none!important}\ndisplay-ads{display:none!important}\n[href^=\"//cadsecs.com/\"]{display:none!important}\n[data-testid^=\"section-AdRowBillboard\"]{display:none!important}\ntopadblock{display:none!important}\na[href^=\"https://go.rmishe.com/\"]{display:none!important}\na[href^=\"http://stickingrepute.com/\"]{display:none!important}\n[href^=\"https://rapidgator.net/article/premium/ref/\"]{display:none!important}\na[id][href=\"https://chpadblock.com/\"][rel^=\"noopener noreferrer\"]{display:none!important}\na[href^=\"https://go.bbrdbr.com\"]{display:none!important}\na[href^=\"https://s.eunow4u.com/\"]{display:none!important}\na[style=\"width:100%;height:100%;z-index:10000000000000000;position:absolute;top:0;left:0;\"]{display:none!important}\na[href^=\"http://www.iyalc.com/\"]{display:none!important}\na[href^=\"https://t.aslnk.link/\"]{display:none!important}\n[href*=\"passtechusa.com\"]{display:none!important}\na[href^=\"http://bongacams.com/track?\"]{display:none!important}\ndiv[id^=\"adrotate_widgets-\"]{display:none!important}\n[data-uri^=\"https://s3.amazonaws.com\"]{display:none!important}\na-ad{display:none!important}\na[href^=\"https://a.adtng.com/\"]{display:none!important}\n[href^=\"https://track.aftrk1.com/\"]{display:none!important}\n[href^=\"https://aads.com/campaigns/\"]{display:none!important}\n[href^=\"https://wsup.ai/\"]{display:none!important}\n[href^=\"https://mylead.global/stl/\"] > img{display:none!important}\na[href^=\"https://click.Ggpickaff.com/\"]{display:none!important}\na.tdn[href^=\"https://landing.brazzersnetwork.com/\"]{display:none!important}\n.ytlr-horizontal-list-renderer__items > .yt-virtual-list__container > .yt-virtual-list__item--visible.yt-virtual-list__item--selected.yt-virtual-list__item:has-text(Ad){display:none!important}\na[href^=\"https://adultfriendfinder.com/go/\"]{display:none!important}\ndiv[id*=\"MarketGid\"]{display:none!important}\n#shorts-inner-container > .ytd-shorts:has(> .ytd-reel-video-renderer > ytd-ad-slot-renderer){display:none!important}\n[href*=\"uselnk.com/\"]{display:none!important}\na[href^=\"https://join.virtuallust3d.com/\"]{display:none!important}\nin-page-message[doskip]{display:none!important}\na[href^=\"https://1betandgonow.com/\"]{display:none!important}\nbody > a.visible[id][href][target=\"_blank\"]{display:none!important}\nins.adsbygoogle[data-ad-slot]{display:none!important}\n[href^=\"https://www.safestcontentgate.com/\"]{display:none!important}\na[href^=\"https://clickins.slixa.com/\"]{display:none!important}\na[href^=\"https://financeads.net/tc.php?\"]{display:none!important}\na[href^=\"https://aj1070.online/\"]{display:none!important}\ndiv[style^=\"height:    250px; width:    300px;\"]{display:none!important}\n[href^=\"https://optimizedelite.com/\"] > img{display:none!important}\na[href^=\"https://explore-site.com/\"]{display:none!important}\na[href^=\"https://go.xlirdr.com\"]{display:none!important}\nyt-overlay-product-sticker{display:none!important}\na[href^=\"https://ctosrd.com/\"]{display:none!important}\na[href^=\"https://serve.awmdelivery.com/\"]{display:none!important}\ndiv[id^=\"ezoic-pub-ad-\"]{display:none!important}\n.ytReelMetapanelViewModelHost > .ytReelMetapanelViewModelMetapanelItem > .ytShortsSuggestedActionViewModelStaticHost{display:none!important}\na[href^=\"https://www.highperformancecpmgate.com/\"]{display:none!important}\n[data-dynamic-ads]{display:none!important}\n[href^=\"https://www.restoro.com/\"]{display:none!important}\na[href^=\"http://tc.tradetracker.net/\"] > img{display:none!important}\na[href^=\"https://bongacams10.com/track?\"]{display:none!important}\n[data-d-ad-id]{display:none!important}\na[href^=\"https://playnano.online/offerwalls/?ref=\"]{display:none!important}\ndiv[class$=\"-adlabel\"]{display:none!important}\nhtml[lang=\"tr\"] > body.has-footer-ad.has-pageskin-desktop a.pageskin-click-left{display:none!important}\na[href^=\"https://believessway.com/\"]{display:none!important}\nAD-SLOT{display:none!important}\nytd-item-section-renderer > .ytd-item-section-renderer > ytd-ad-slot-renderer.style-scope{display:none!important}\na[href^=\"https://identicaldrench.com/\"]{display:none!important}\na[href^=\"//go.eabids.com/\"]{display:none!important}\nDFP-AD{display:none!important}\ndiv[id^=\"div-ads-\"]{display:none!important}\na[href^=\"https://www.onlineusershielder.com/\"]{display:none!important}\nad-shield-ads{display:none!important}\na[href*=\".foxqck.com/\"]{display:none!important}\n[href^=\"https://go.xlrdr.com\"]{display:none!important}\na[href^=\"https://torguard.net/aff.php\"] > img{display:none!important}\namp-connatix-player{display:none!important}\na[href^=\"https://wolf-327b.com/\"]{display:none!important}\nspan[id^=\"ezoic-pub-ad-placeholder-\"]{display:none!important}\na[href^=\"https://getmatchedlocally.com/\"]{display:none!important}\nytd-rich-item-renderer:has(> #content > ytd-ad-slot-renderer){display:none!important}\nimg[src^=\"https://images.purevpnaffiliates.com\"]{display:none!important}\nbottomadblock{display:none!important}\na[href^=\"https://track.adform.net/\"]{display:none!important}\na[href^=\"https://track.afcpatrk.com/\"]{display:none!important}\ndiv[id^=\"apn_native_ad_slot_\"]{display:none!important}\n[data-ad-module]:not([style$=\"left: -10000px !important; top: -1000px !important;\"]):not(.adsbygoogle){display:none!important}\n[href^=\"https://go.mavrtracktor.com/\"]{display:none!important}\n[data-testid=\"adBanner-wrapper\"]{display:none!important}\na[href^=\"https://prf.hn/click/\"][href*=\"/adref:\"] > img{display:none!important}\n[href=\"https://snvhost.com/\"]{display:none!important}\na[href^=\"https://t.adating.link/\"]{display:none!important}\na[href^=\"https://tc.tradetracker.net/\"] > img{display:none!important}\n[href^=\"https://www.brighteonstore.com/products/\"] img{display:none!important}\n[href^=\"https://groorsoa.net/\"]{display:none!important}\n[id^=\"div-gpt-ad\"]:not([style^=\"width: 1px; height: 1px; position: absolute; left: -10000px; top: -\"]):not([style=\"pointer-events: none; height: 1px; width: 0px; opacity: 0; visibility: hidden; position: fixed; bottom: 0px;\"]){display:none!important}\na[href^=\"https://a-ads.com/\"]{display:none!important}\na[href^=\"https://a.bestcontentweb.top/\"]{display:none!important}\n[data-rc-widget]{display:none!important}\na[href^=\"https://6-partner.com/\"]{display:none!important}\n[href^=\"https://track.wg-aff.com/click\"]{display:none!important}\na[href^=\"https://ad.zanox.com/ppc/\"] > img{display:none!important}\na[href^=\"https://sexynearme.com/\"]{display:none!important}\n[href=\"https://clickaine.com\"]{display:none!important}\ndiv[data-alias=\"300x250 Ad 2\"]{display:none!important}\namp-ad{display:none!important}\n[href^=\"https://wct.link/click?\"]{display:none!important}\ndiv[class^=\"Adstyled__AdWrapper-\"]{display:none!important}\nAMP-AD{display:none!important}\na[href^=\"https://www.effectiveratecpm.com/\"]{display:none!important}\na[onmousedown^=\"this.href='https://paid.outbrain.com/network/redir?\"]{display:none!important}\niframe.lazyloaded[data-src^=\"https://rcm-fe.amazon-adsystem.com/\"]{display:none!important}\n[href^=\"https://ilovemyfreedoms.com/landing-\"]{display:none!important}\na[href^=\"//s.zlinkd.com/\"]{display:none!important}\na[href^=\"https://bngpt.com/\"]{display:none!important}\n[href^=\"https://www.onclickperformance.com/\"]{display:none!important}\na[href^=\"https://loboclick.com/\"]{display:none!important}\na[href^=\"https://s.ma3ion.com/\"]{display:none!important}\na[href^=\"http://www.poweredbyliquidfire.mobi/\"]{display:none!important}\n[href^=\"https://go.rdrjmp.com/\"]{display:none!important}\nhtml[lang=\"tr\"] #aa-wp + .fixed-footer .footer-ad-container{display:none!important}\ndiv[data-alias=\"300x250 Ad 1\"]{display:none!important}\n[data-testid^=\"taboola-\"]{display:none!important}\na[href^=\"https://dl-protect.net/\"]{display:none!important}\ndiv[id^=\"rc-widget-\"]{display:none!important}\n[id^=\"ad_sky\"]{display:none!important}\n[id^=\"div-gpt-ad\"]{display:none!important}\na[href^=\"https://fastestvpn.com/lifetime-special-deal?a_aid=\"]{display:none!important}\ndiv[data-ad-wrapper]{display:none!important}";document.documentElement.appendChild(style);};if(document.documentElement)install();else document.addEventListener('DOMContentLoaded',install,{once:true});break;}
case "music.youtube.com":{
function setConstantFn(
    trusted = false,
    chain = '',
    rawValue = '',
    ...varargs
) {
    if ( chain === '' ) { return; }
    const safe = safeSelf();
    const logPrefix = safe.makeLogPrefix('set-constant', chain, rawValue);
    const extraArgs = safe.parseVarargs(varargs);
    function setConstant(chain, rawValue) {
        const trappedProp = (( ) => {
            const pos = chain.lastIndexOf('.');
            if ( pos === -1 ) { return chain; }
            return chain.slice(pos+1);
        })();
        const cloakFunc = fn => {
            safe.Object_defineProperty(fn, 'name', { value: trappedProp });
            return new Proxy(fn, {
                defineProperty(target, prop) {
                    if ( prop !== 'toString' ) {
                        return Reflect.defineProperty(...arguments);
                    }
                    return true;
                },
                deleteProperty(target, prop) {
                    if ( prop !== 'toString' ) {
                        return Reflect.deleteProperty(...arguments);
                    }
                    return true;
                },
                get(target, prop) {
                    if ( prop === 'toString' ) {
                        return function() {
                            return `function ${trappedProp}() { [native code] }`;
                        }.bind(null);
                    }
                    return Reflect.get(...arguments);
                },
            });
        };
        if ( trappedProp === '' ) { return; }
        const thisScript = document.currentScript;
        let normalValue = validateConstantFn(trusted, rawValue, extraArgs);
        if ( rawValue === 'noopFunc' || rawValue === 'trueFunc' || rawValue === 'falseFunc' ) {
            normalValue = cloakFunc(normalValue);
        }
        let aborted = false;
        const mustAbort = function(v) {
            if ( trusted ) { return false; }
            if ( aborted ) { return true; }
            aborted =
                (v !== undefined && v !== null) &&
                (normalValue !== undefined && normalValue !== null) &&
                (typeof v !== typeof normalValue);
            if ( aborted ) {
                safe.uboLog(logPrefix, `Aborted because value set to ${v}`);
            }
            return aborted;
        };
        // https://github.com/uBlockOrigin/uBlock-issues/issues/156
        //   Support multiple trappers for the same property.
        const trapProp = function(owner, prop, configurable, handler) {
            if ( handler.init(configurable ? owner[prop] : normalValue) === false ) { return; }
            const odesc = safe.Object_getOwnPropertyDescriptor(owner, prop);
            let prevGetter, prevSetter;
            if ( odesc instanceof safe.Object ) {
                owner[prop] = normalValue;
                if ( odesc.get instanceof Function ) {
                    prevGetter = odesc.get;
                }
                if ( odesc.set instanceof Function ) {
                    prevSetter = odesc.set;
                }
            }
            try {
                safe.Object_defineProperty(owner, prop, {
                    configurable,
                    get() {
                        if ( prevGetter !== undefined ) {
                            prevGetter();
                        }
                        return handler.getter();
                    },
                    set(a) {
                        if ( prevSetter !== undefined ) {
                            prevSetter(a);
                        }
                        handler.setter(a);
                    }
                });
                safe.uboLog(logPrefix, 'Trap installed');
            } catch(ex) {
                safe.uboErr(logPrefix, ex);
            }
        };
        const trapChain = function(owner, chain) {
            const pos = chain.indexOf('.');
            if ( pos === -1 ) {
                trapProp(owner, chain, false, {
                    v: undefined,
                    init: function(v) {
                        if ( mustAbort(v) ) { return false; }
                        this.v = v;
                        return true;
                    },
                    getter: function() {
                        if ( document.currentScript === thisScript ) {
                            return this.v;
                        }
                        safe.uboLog(logPrefix, 'Property read');
                        return normalValue;
                    },
                    setter: function(a) {
                        if ( mustAbort(a) === false ) { return; }
                        normalValue = a;
                    }
                });
                return;
            }
            const prop = chain.slice(0, pos);
            const v = owner[prop];
            chain = chain.slice(pos + 1);
            if ( v instanceof safe.Object || typeof v === 'object' && v !== null ) {
                trapChain(v, chain);
                return;
            }
            trapProp(owner, prop, true, {
                v: undefined,
                init: function(v) {
                    this.v = v;
                    return true;
                },
                getter: function() {
                    return this.v;
                },
                setter: function(a) {
                    this.v = a;
                    if ( a instanceof safe.Object ) {
                        trapChain(a, chain);
                    }
                }
            });
        };
        trapChain(window, chain);
    }
    runAt(( ) => {
        setConstant(chain, rawValue);
    }, extraArgs.runAt);
}
function runAt(fn, when) {
    const intFromReadyState = state => {
        const targets = {
            'loading': 1, 'asap': 1,
            'interactive': 2, 'end': 2, '2': 2,
            'complete': 3, 'idle': 3, '3': 3,
        };
        const tokens = Array.isArray(state) ? state : [ state ];
        for ( const token of tokens ) {
            const prop = `${token}`;
            if ( Object.hasOwn(targets, prop) === false ) { continue; }
            return targets[prop];
        }
        return 0;
    };
    const runAt = intFromReadyState(when);
    if ( intFromReadyState(document.readyState) >= runAt ) {
        fn(); return;
    }
    const onStateChange = ( ) => {
        if ( intFromReadyState(document.readyState) < runAt ) { return; }
        fn();
        safe.removeEventListener.apply(document, args);
    };
    const safe = safeSelf();
    const args = [ 'readystatechange', onStateChange, { capture: true } ];
    safe.addEventListener.apply(document, args);
}
function safeSelf() {
    if ( safeSelf.safe ) {
        return safeSelf.safe;
    }
    const self = globalThis;
    const safe = {
        'Array_from': Array.from,
        'Error': self.Error,
        'Function_toString': Function.prototype.call.bind(self.Function.prototype.toString),
        'Math_floor': Math.floor,
        'Math_max': Math.max,
        'Math_min': Math.min,
        'Math_random': Math.random,
        'Object': Object,
        'Object_defineProperty': Object.defineProperty.bind(Object),
        'Object_defineProperties': Object.defineProperties.bind(Object),
        'Object_fromEntries': Object.fromEntries.bind(Object),
        'Object_getOwnPropertyDescriptor': Object.getOwnPropertyDescriptor.bind(Object),
        'Object_hasOwn': Object.hasOwn.bind(Object),
        'Object_toString': Object.prototype.toString,
        'RegExp': self.RegExp,
        'RegExp_test': Function.prototype.call.bind(self.RegExp.prototype.test),
        'RegExp_exec': self.RegExp.prototype.exec,
        'Request_clone': self.Request.prototype.clone,
        'String': self.String,
        'String_fromCharCode': String.fromCharCode,
        'String_split': String.prototype.split,
        'XMLHttpRequest': self.XMLHttpRequest,
        'addEventListener': self.EventTarget.prototype.addEventListener,
        'removeEventListener': self.EventTarget.prototype.removeEventListener,
        'fetch': self.fetch,
        'JSON': self.JSON,
        'JSON_parse': Function.prototype.call.bind(self.JSON.parse, self.JSON),
        'JSON_stringify': Function.prototype.call.bind(self.JSON.stringify, self.JSON),
        'log': console.log.bind(console),
        // Properties
        logLevel: 0,
        // Methods
        makeLogPrefix(...args) {
            return this.sendToLogger && `[${args.join(' \u205D ')}]` || '';
        },
        uboLog(...args) {
            if ( this.sendToLogger === undefined ) { return; }
            if ( args === undefined || args[0] === '' ) { return; }
            return this.sendToLogger('info', ...args);
            
        },
        uboErr(...args) {
            if ( this.sendToLogger === undefined ) { return; }
            if ( args === undefined || args[0] === '' ) { return; }
            return this.sendToLogger('error', ...args);
        },
        escapeRegexChars(s) {
            return s.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
        },
        initPattern(pattern, options = {}) {
            if ( pattern === '' ) {
                return { matchAll: true, expect: true };
            }
            const expect = (options.canNegate !== true || pattern.startsWith('!') === false);
            if ( expect === false ) {
                pattern = pattern.slice(1);
            }
            const match = /^\/(.+)\/([gimsu]*)$/.exec(pattern);
            if ( match !== null ) {
                return {
                    re: new this.RegExp(
                        match[1],
                        match[2] || options.flags
                    ),
                    expect,
                };
            }
            if ( options.flags !== undefined ) {
                return {
                    re: new this.RegExp(this.escapeRegexChars(pattern),
                        options.flags
                    ),
                    expect,
                };
            }
            return { pattern, expect };
        },
        testPattern(details, haystack) {
            if ( details.matchAll ) { return true; }
            if ( details.re ) {
                return this.RegExp_test(details.re, haystack) === details.expect;
            }
            return haystack.includes(details.pattern) === details.expect;
        },
        patternToRegex(pattern, flags = undefined, verbatim = false) {
            if ( pattern === '' ) { return /^/; }
            const match = /^\/(.+)\/([gimsu]*)$/.exec(pattern);
            if ( match === null ) {
                const reStr = this.escapeRegexChars(pattern);
                return new RegExp(verbatim ? `^${reStr}$` : reStr, flags);
            }
            try {
                return new RegExp(match[1], match[2] || undefined);
            }
            catch {
            }
            return /^/;
        },
        parseVarargs(varargs) {
            const entries = varargs.reduce((out, v, i, a) => {
                if ( i & 1 ) { return out; }
                const rawValue = a[i+1];
                const value = /^\d+$/.test(rawValue)
                    ? parseInt(rawValue, 10)
                    : rawValue;
                out.push([ a[i], value ]);
                return out;
            }, []);
            return this.Object_fromEntries(entries);
        },
    };
    safeSelf.safe = safe;
    if ( scriptletGlobals.bcSecret === undefined ) { return safe; }
    // This is executed only when the logger is opened
    safe.logLevel = scriptletGlobals.logLevel || 1;
    let lastLogType = '';
    let lastLogText = '';
    let lastLogTime = 0;
    safe.toLogText = (type, ...args) => {
        if ( args.length === 0 ) { return; }
        const text = `[${document.location.hostname || document.location.href}]${args.join(' ')}`;
        if ( text === lastLogText && type === lastLogType ) {
            if ( (Date.now() - lastLogTime) < 5000 ) { return; }
        }
        lastLogType = type;
        lastLogText = text;
        lastLogTime = Date.now();
        return text;
    };
    try {
        const bc = new self.BroadcastChannel(scriptletGlobals.bcSecret);
        let bcBuffer = [];
        safe.sendToLogger = (type, ...args) => {
            const text = safe.toLogText(type, ...args);
            if ( text === undefined ) { return; }
            if ( bcBuffer === undefined ) {
                return bc.postMessage({ what: 'messageToLogger', type, text });
            }
            bcBuffer.push({ type, text });
        };
        bc.onmessage = ev => {
            const msg = ev.data;
            switch ( msg ) {
            case 'iamready!':
                if ( bcBuffer === undefined ) { break; }
                bcBuffer.forEach(({ type, text }) =>
                    bc.postMessage({ what: 'messageToLogger', type, text })
                );
                bcBuffer = undefined;
                break;
            case 'setScriptletLogLevelToOne':
                safe.logLevel = 1;
                break;
            case 'setScriptletLogLevelToTwo':
                safe.logLevel = 2;
                break;
            }
        };
        bc.postMessage('areyouready?');
    } catch {
        safe.sendToLogger = (type, ...args) => {
            const text = safe.toLogText(type, ...args);
            if ( text === undefined ) { return; }
            safe.log(`uBO ${text}`);
        };
    }
    return safe;
}
function validateConstantFn(trusted, raw, extraArgs = {}) {
    const safe = safeSelf();
    let value;
    if ( raw === 'undefined' ) {
        value = undefined;
    } else if ( raw === 'false' ) {
        value = false;
    } else if ( raw === 'true' ) {
        value = true;
    } else if ( raw === 'null' ) {
        value = null;
    } else if ( raw === "''" || raw === '' ) {
        value = '';
    } else if ( raw === '[]' || raw === 'emptyArr' ) {
        value = [];
    } else if ( raw === '{}' || raw === 'emptyObj' ) {
        value = {};
    } else if ( raw === 'noopFunc' ) {
        value = function(){};
    } else if ( raw === 'trueFunc' ) {
        value = function(){ return true; };
    } else if ( raw === 'falseFunc' ) {
        value = function(){ return false; };
    } else if ( raw === 'throwFunc' ) {
        value = function(){ throw ''; };
    } else if ( /^-?\d+$/.test(raw) ) {
        value = parseInt(raw);
        if ( isNaN(raw) ) { return; }
        if ( Math.abs(raw) > 0x7FFF ) { return; }
    } else if ( trusted ) {
        if ( raw.startsWith('json:') ) {
            try { value = safe.JSON_parse(raw.slice(5)); } catch { return; }
        } else if ( raw.startsWith('{') && raw.endsWith('}') ) {
            try { value = safe.JSON_parse(raw).value; } catch { return; }
        }
    } else {
        return;
    }
    if ( extraArgs.as !== undefined ) {
        if ( extraArgs.as === 'function' ) {
            return ( ) => value;
        } else if ( extraArgs.as === 'callback' ) {
            return ( ) => (( ) => value);
        } else if ( extraArgs.as === 'resolved' ) {
            return Promise.resolve(value);
        } else if ( extraArgs.as === 'rejected' ) {
            return Promise.reject(value);
        }
    }
    return value;
}
function setConstant(
    ...args
) {
    setConstantFn(false, ...args);
}
function objectPruneFn(
    obj,
    rawPrunePaths,
    rawNeedlePaths,
    stackNeedleDetails = { matchAll: true },
    extraArgs = {}
) {
    if ( typeof rawPrunePaths !== 'string' ) { return; }
    const safe = safeSelf();
    const prunePaths = rawPrunePaths !== ''
        ? safe.String_split.call(rawPrunePaths, / +/)
        : [];
    const needlePaths = prunePaths.length !== 0 && rawNeedlePaths !== ''
        ? safe.String_split.call(rawNeedlePaths, / +/)
        : [];
    if ( stackNeedleDetails.matchAll !== true ) {
        if ( matchesStackTraceFn(stackNeedleDetails, extraArgs.logstack) === false ) {
            return;
        }
    }
    if ( objectPruneFn.mustProcess === undefined ) {
        objectPruneFn.mustProcess = (root, needlePaths) => {
            for ( const needlePath of needlePaths ) {
                if ( objectFindOwnerFn(root, needlePath) === false ) {
                    return false;
                }
            }
            return true;
        };
    }
    if ( prunePaths.length === 0 ) { return; }
    let outcome = 'nomatch';
    if ( objectPruneFn.mustProcess(obj, needlePaths) ) {
        for ( const path of prunePaths ) {
            if ( objectFindOwnerFn(obj, path, true) ) {
                outcome = 'match';
            }
        }
    }
    if ( outcome === 'match' ) { return obj; }
}
function matchesStackTraceFn(
    needleDetails,
    logLevel = ''
) {
    const safe = safeSelf();
    const exceptionToken = getExceptionTokenFn();
    const error = new safe.Error(exceptionToken);
    const docURL = new URL(self.location.href);
    docURL.hash = '';
    // Normalize stack trace
    const reLine = /(.*?@)?(\S+)(:\d+):\d+\)?$/;
    const lines = [];
    for ( let line of safe.String_split.call(error.stack, /[\n\r]+/) ) {
        if ( line.includes(exceptionToken) ) { continue; }
        line = line.trim();
        const match = safe.RegExp_exec.call(reLine, line);
        if ( match === null ) { continue; }
        let url = match[2];
        if ( url.startsWith('(') ) { url = url.slice(1); }
        if ( url === docURL.href ) {
            url = 'inlineScript';
        } else if ( url.startsWith('<anonymous>') ) {
            url = 'injectedScript';
        }
        let fn = match[1] !== undefined
            ? match[1].slice(0, -1)
            : line.slice(0, match.index).trim();
        if ( fn.startsWith('at') ) { fn = fn.slice(2).trim(); }
        let rowcol = match[3];
        lines.push(' ' + `${fn} ${url}${rowcol}:1`.trim());
    }
    lines[0] = `stackDepth:${lines.length-1}`;
    const stack = lines.join('\t');
    const r = needleDetails.matchAll !== true &&
        safe.testPattern(needleDetails, stack);
    if (
        logLevel === 'all' ||
        logLevel === 'match' && r ||
        logLevel === 'nomatch' && !r
    ) {
        safe.uboLog(stack.replace(/\t/g, '\n'));
    }
    return r;
}
function getExceptionTokenFn() {
    const token = getRandomTokenFn();
    const oe = self.onerror;
    self.onerror = function(msg, ...args) {
        if ( typeof msg === 'string' && msg.includes(token) ) { return true; }
        if ( oe instanceof Function ) {
            return oe.call(this, msg, ...args);
        }
    }.bind();
    return token;
}
function getRandomTokenFn() {
    const safe = safeSelf();
    return safe.String_fromCharCode(Date.now() % 26 + 97) +
        safe.Math_floor(safe.Math_random() * 982451653 + 982451653).toString(36);
}
function objectFindOwnerFn(
    root,
    path,
    prune = false
) {
    const safe = safeSelf();
    let owner = root;
    let chain = path;
    for (;;) {
        if ( typeof owner !== 'object' || owner === null  ) { return false; }
        const pos = chain.indexOf('.');
        if ( pos === -1 ) {
            if ( prune === false ) {
                return safe.Object_hasOwn(owner, chain);
            }
            let modified = false;
            if ( chain === '*' ) {
                for ( const key in owner ) {
                    if ( safe.Object_hasOwn(owner, key) === false ) { continue; }
                    delete owner[key];
                    modified = true;
                }
            } else if ( safe.Object_hasOwn(owner, chain) ) {
                delete owner[chain];
                modified = true;
            }
            return modified;
        }
        const prop = chain.slice(0, pos);
        const next = chain.slice(pos + 1);
        let found = false;
        if ( prop === '[-]' && Array.isArray(owner) ) {
            let i = owner.length;
            while ( i-- ) {
                if ( objectFindOwnerFn(owner[i], next) === false ) { continue; }
                owner.splice(i, 1);
                found = true;
            }
            return found;
        }
        if ( prop === '{-}' && owner instanceof Object ) {
            for ( const key of Object.keys(owner) ) {
                if ( objectFindOwnerFn(owner[key], next) === false ) { continue; }
                delete owner[key];
                found = true;
            }
            return found;
        }
        if (
            prop === '[]' && Array.isArray(owner) ||
            prop === '{}' && owner instanceof Object ||
            prop === '*' && owner instanceof Object
        ) {
            for ( const key of Object.keys(owner) ) {
                if (objectFindOwnerFn(owner[key], next, prune) === false ) { continue; }
                found = true;
            }
            return found;
        }
        if ( safe.Object_hasOwn(owner, prop) === false ) { return false; }
        owner = owner[prop];
        chain = chain.slice(pos + 1);
    }
}
function proxyApplyFn(
    target = '',
    handler = '',
    options = {}
) {
    let context = globalThis;
    let prop = target;
    for (;;) {
        const pos = prop.indexOf('.');
        if ( pos === -1 ) { break; }
        context = context[prop.slice(0, pos)];
        if ( context instanceof Object === false ) { return; }
        prop = prop.slice(pos+1);
    }
    const fn = context[prop];
    if ( typeof fn !== 'function' ) { return; }
    if ( proxyApplyFn.CtorContext === undefined ) {
        proxyApplyFn.ctorContexts = [];
        proxyApplyFn.CtorContext = class {
            constructor(...args) {
                this.init(...args);
            }
            init(callFn, callArgs) {
                this.callFn = callFn;
                this.callArgs = callArgs;
                return this;
            }
            reflect() {
                const r = Reflect.construct(this.callFn, this.callArgs);
                this.callFn = this.callArgs = this.private = undefined;
                proxyApplyFn.ctorContexts.push(this);
                return r;
            }
            static factory(...args) {
                return proxyApplyFn.ctorContexts.length !== 0
                    ? proxyApplyFn.ctorContexts.pop().init(...args)
                    : new proxyApplyFn.CtorContext(...args);
            }
        };
        proxyApplyFn.applyContexts = [];
        proxyApplyFn.ApplyContext = class {
            constructor(...args) {
                this.init(...args);
            }
            init(callFn, thisArg, callArgs) {
                this.callFn = callFn;
                this.thisArg = thisArg;
                this.callArgs = callArgs;
                return this;
            }
            reflect() {
                const r = Reflect.apply(this.callFn, this.thisArg, this.callArgs);
                this.callFn = this.thisArg = this.callArgs = this.private = undefined;
                proxyApplyFn.applyContexts.push(this);
                return r;
            }
            static factory(...args) {
                return proxyApplyFn.applyContexts.length !== 0
                    ? proxyApplyFn.applyContexts.pop().init(...args)
                    : new proxyApplyFn.ApplyContext(...args);
            }
        };
        proxyApplyFn.isCtor = new Map();
        proxyApplyFn.proxies = new WeakMap();
        if ( (options.skipToString || proxyApplyFn.skipToString) !== true ) {
            proxyApplyFn.nativeToString = Function.prototype.toString;
            const proxiedToString = new Proxy(Function.prototype.toString, {
                apply(target, thisArg) {
                    let proxied = thisArg;
                    for(;;) {
                        const fn = proxyApplyFn.proxies.get(proxied);
                        if ( fn === undefined ) { break; }
                        proxied = fn;
                    }
                    return proxyApplyFn.nativeToString.call(proxied);
                }
            });
            proxyApplyFn.proxies.set(proxiedToString, proxyApplyFn.nativeToString);
            Function.prototype.toString = proxiedToString;
        }
    }
    if ( proxyApplyFn.isCtor.has(target) === false ) {
        proxyApplyFn.isCtor.set(target, fn.prototype?.constructor === fn);
    }
    const proxyDetails = {
        apply(target, thisArg, args) {
            return handler(proxyApplyFn.ApplyContext.factory(target, thisArg, args));
        }
    };
    if ( proxyApplyFn.isCtor.get(target) ) {
        proxyDetails.construct = function(target, args) {
            return handler(proxyApplyFn.CtorContext.factory(target, args));
        };
    }
    const proxiedTarget = new Proxy(fn, proxyDetails);
    proxyApplyFn.proxies.set(proxiedTarget, fn);
    context[prop] = proxiedTarget;
}
function jsonPrune(
    rawPrunePaths = '',
    rawNeedlePaths = '',
    stackNeedle = '',
    ...varargs
) {
    const safe = safeSelf();
    const logPrefix = safe.makeLogPrefix('json-prune', rawPrunePaths, rawNeedlePaths, stackNeedle);
    const stackNeedleDetails = safe.initPattern(stackNeedle, { canNegate: true });
    const extraArgs = safe.parseVarargs(varargs);
    proxyApplyFn('JSON.parse', function(context) {
        const objBefore = context.reflect();
        if ( rawPrunePaths === '' ) {
            safe.uboLog(logPrefix, safe.JSON_stringify(objBefore, null, 2));
        }
        const objAfter = objectPruneFn(
            objBefore,
            rawPrunePaths,
            rawNeedlePaths,
            stackNeedleDetails,
            extraArgs
        );
        if ( objAfter === undefined ) { return objBefore; }
        safe.uboLog(logPrefix, 'Pruned');
        if ( safe.logLevel > 1 ) {
            safe.uboLog(logPrefix, `After pruning:\n${safe.JSON_stringify(objAfter, null, 2)}`);
        }
        return objAfter;
    });
}
function collateFetchArgumentsFn(resource, options) {
    const safe = safeSelf();
    const props = [
        'body', 'cache', 'credentials', 'duplex', 'headers',
        'integrity', 'keepalive', 'method', 'mode', 'priority',
        'redirect', 'referrer', 'referrerPolicy', 'url'
    ];
    const out = {};
    if ( collateFetchArgumentsFn.collateKnownProps === undefined ) {
        collateFetchArgumentsFn.collateKnownProps = (src, out) => {
            for ( const prop of props ) {
                if ( src[prop] === undefined ) { continue; }
                out[prop] = src[prop];
            }
        };
    }
    if (
        typeof resource !== 'object' ||
        safe.Object_toString.call(resource) !== '[object Request]'
    ) {
        out.url = `${resource}`;
    } else {
        let clone;
        try {
            clone = safe.Request_clone.call(resource);
        } catch {
        }
        collateFetchArgumentsFn.collateKnownProps(clone || resource, out);
    }
    if ( typeof options === 'object' && options !== null ) {
        collateFetchArgumentsFn.collateKnownProps(options, out);
    }
    return out;
}
function matchObjectPropertiesFn(propNeedles, ...objs) {
    const safe = safeSelf();
    const matched = [];
    for ( const obj of objs ) {
        if ( obj instanceof Object === false ) { continue; }
        for ( const [ prop, details ] of propNeedles ) {
            let value = obj[prop];
            if ( value === undefined ) { continue; }
            if ( typeof value !== 'string' ) {
                try { value = safe.JSON_stringify(value); }
                catch { }
                if ( typeof value !== 'string' ) { continue; }
            }
            if ( safe.testPattern(details, value) === false ) { return; }
            matched.push(`${prop}: ${value}`);
        }
    }
    return matched;
}
function parsePropertiesToMatchFn(propsToMatch, implicit = '') {
    const safe = safeSelf();
    const needles = new Map();
    if ( propsToMatch === undefined || propsToMatch === '' ) { return needles; }
    const options = { canNegate: true };
    for ( const needle of safe.String_split.call(propsToMatch, /\s+/) ) {
        let [ prop, pattern ] = safe.String_split.call(needle, ':');
        if ( prop === '' ) { continue; }
        if ( pattern !== undefined && /[^$\w -]/.test(prop) ) {
            prop = `${prop}:${pattern}`;
            pattern = undefined;
        }
        if ( pattern !== undefined ) {
            needles.set(prop, safe.initPattern(pattern, options));
        } else if ( implicit !== '' ) {
            needles.set(implicit, safe.initPattern(prop, options));
        }
    }
    return needles;
}
function jsonPruneFetchResponse(
    rawPrunePaths = '',
    rawNeedlePaths = '',
    ...varargs
) {
    const safe = safeSelf();
    const logPrefix = safe.makeLogPrefix('json-prune-fetch-response', rawPrunePaths, rawNeedlePaths);
    const extraArgs = safe.parseVarargs(varargs);
    const propNeedles = parsePropertiesToMatchFn(extraArgs.propsToMatch, 'url');
    const stackNeedle = safe.initPattern(extraArgs.stackToMatch || '', { canNegate: true });
    const logall = rawPrunePaths === '';
    const applyHandler = function(target, thisArg, args) {
        const fetchPromise = Reflect.apply(target, thisArg, args);
        if ( propNeedles.size !== 0 ) {
            const props = collateFetchArgumentsFn(...args);
            const matched = matchObjectPropertiesFn(propNeedles, props);
            if ( matched === undefined ) { return fetchPromise; }
            if ( safe.logLevel > 1 ) {
                safe.uboLog(logPrefix, `Matched "propsToMatch":\n\t${matched.join('\n\t')}`);
            }
        }
        return fetchPromise.then(responseBefore => {
            const response = responseBefore.clone();
            return response.json().then(objBefore => {
                if ( typeof objBefore !== 'object' ) { return responseBefore; }
                if ( logall ) {
                    safe.uboLog(logPrefix, safe.JSON_stringify(objBefore, null, 2));
                    return responseBefore;
                }
                const objAfter = objectPruneFn(
                    objBefore,
                    rawPrunePaths,
                    rawNeedlePaths,
                    stackNeedle,
                    extraArgs
                );
                if ( typeof objAfter !== 'object' ) { return responseBefore; }
                safe.uboLog(logPrefix, 'Pruned');
                const responseAfter = Response.json(objAfter, {
                    status: responseBefore.status,
                    statusText: responseBefore.statusText,
                    headers: responseBefore.headers,
                });
                Object.defineProperties(responseAfter, {
                    ok: { value: responseBefore.ok },
                    redirected: { value: responseBefore.redirected },
                    type: { value: responseBefore.type },
                    url: { value: responseBefore.url },
                });
                return responseAfter;
            }).catch(reason => {
                safe.uboErr(logPrefix, 'Error:', reason);
                return responseBefore;
            });
        }).catch(reason => {
            safe.uboErr(logPrefix, 'Error:', reason);
            return fetchPromise;
        });
    };
    self.fetch = new Proxy(self.fetch, {
        apply: applyHandler
    });
}
try {
setConstant("ytInitialPlayerResponse.adPlacements", "undefined")
} catch ( e ) { }
try {
setConstant("playerResponse.adPlacements", "undefined")
} catch ( e ) { }
try {
jsonPrune("playerResponse.adPlacements playerResponse.playerAds playerResponse.adSlots adPlacements playerAds adSlots important")
} catch ( e ) { }
try {
setConstant("ytInitialPlayerResponse.playerAds", "undefined")
} catch ( e ) { }
try {
jsonPruneFetchResponse("reelWatchSequenceResponse.entries.[-].command.reelWatchEndpoint.adClientParams.isAd entries.[-].command.reelWatchEndpoint.adClientParams.isAd", "", "propsToMatch", "url:/reel_watch_sequence?")
} catch ( e ) { }
try {
jsonPrune("entries.[-].command.reelWatchEndpoint.adClientParams.isAd")
} catch ( e ) { }
try {
setConstant("ytInitialPlayerResponse.adSlots", "undefined")
} catch ( e ) { }

const install=()=>{if(!document.documentElement||document.getElementById('pulse-engine-style'))return;const style=document.createElement('style');style.id='pulse-engine-style';style.textContent=".ytd-section-list-renderer > .ytd-item-section-renderer > ytd-search-pyv-renderer.ytd-item-section-renderer{display:none!important}\n.ytp-suggested-action > .ytp-suggested-action-badge{display:none!important}\n#sticker-layer{display:none!important}\nyt-overlay-product-sticker{display:none!important}\n.ytd-watch-flexy > .ytd-watch-next-secondary-results-renderer > ytd-ad-slot-renderer.ytd-watch-next-secondary-results-renderer{display:none!important}\nytd-rich-item-renderer:has(> #content > ytd-ad-slot-renderer){display:none!important}\n.ytd-two-column-browse-results-renderer > ytd-rich-grid-renderer > #masthead-ad.ytd-rich-grid-renderer{display:none!important}\n#shopping-timely-shelf{display:none!important}\n#description-inner > ytd-merch-shelf-renderer > #main.ytd-merch-shelf-renderer{display:none!important}\n.ytlr-horizontal-list-renderer__items > .yt-virtual-list__container > .yt-virtual-list__item--visible.yt-virtual-list__item--selected.yt-virtual-list__item:has-text(Ad){display:none!important}\n.ytd-watch-flexy > ytd-merch-shelf-renderer > #main.ytd-merch-shelf-renderer{display:none!important}\n.ytReelMetapanelViewModelHost > .ytReelMetapanelViewModelMetapanelItem > .ytShortsSuggestedActionViewModelStaticHost{display:none!important}\n#shorts-inner-container > .ytd-shorts:has(> .ytd-reel-video-renderer > ytd-ad-slot-renderer){display:none!important}\n.grid.ytd-browse > #primary > .style-scope > .ytd-rich-grid-renderer > .ytd-rich-grid-renderer > .ytd-ad-slot-renderer{display:none!important}\n.ytd-rich-item-renderer.style-scope > .ytd-rich-item-renderer > .ytd-ad-slot-renderer.style-scope{display:none!important}\n#contents > ytd-rich-item-renderer:has(> ytd-ad-slot-renderer){display:none!important}\nytd-item-section-renderer > .ytd-item-section-renderer > ytd-ad-slot-renderer.style-scope{display:none!important}";document.documentElement.appendChild(style);};if(document.documentElement)install();else document.addEventListener('DOMContentLoaded',install,{once:true});break;}
case "www.youtube.com":{
function replaceNodeTextFn(
    nodeName = '',
    pattern = '',
    replacement = '',
    ...varargs
) {
    const safe = safeSelf();
    const logPrefix = safe.makeLogPrefix('replace-node-text.fn', ...Array.from(arguments));
    const reNodeName = safe.patternToRegex(nodeName, 'i', true);
    const rePattern = safe.patternToRegex(pattern, 'gms');
    const extraArgs = safe.parseVarargs(varargs);
    const reIncludes = extraArgs.includes || extraArgs.condition
        ? safe.patternToRegex(extraArgs.includes || extraArgs.condition, 'ms')
        : null;
    const reExcludes = extraArgs.excludes
        ? safe.patternToRegex(extraArgs.excludes, 'ms')
        : null;
    const textContentFactory = (( ) => {
        const out = { createScript: s => s };
        const { trustedTypes: tt } = self;
        if ( tt instanceof Object ) {
            if ( typeof tt.getPropertyType === 'function' ) {
                if ( tt.getPropertyType('script', 'textContent') === 'TrustedScript' ) {
                    return tt.createPolicy(getRandomTokenFn(), out);
                }
            }
        }
        return out;
    })();
    let sedCount = extraArgs.sedCount ?? Number.MAX_SAFE_INTEGER;
    const handleNode = node => {
        const before = node.textContent;
        if ( reIncludes ) {
            reIncludes.lastIndex = 0;
            if ( safe.RegExp_test(reIncludes, before) === false ) { return; }
        }
        if ( reExcludes ) {
            reExcludes.lastIndex = 0;
            if ( safe.RegExp_test(reExcludes, before) ) { return; }
        }
        rePattern.lastIndex = 0;
        if ( safe.RegExp_test(rePattern, before) === false ) { return; }
        rePattern.lastIndex = 0;
        const after = pattern !== ''
            ? before.replace(rePattern, replacement)
            : replacement;
        node.textContent = node.nodeName === 'SCRIPT'
            ? textContentFactory.createScript(after)
            : after;
        if ( safe.logLevel > 1 ) {
            safe.uboLog(logPrefix, `Text before:\n${before.trim()}`);
        }
        safe.uboLog(logPrefix, `Text after:\n${after.trim()}`);
        sedCount -= 1;
    };
    const handleTree = root => {
        const treeWalker = document.createTreeWalker(root,
            NodeFilter.SHOW_ELEMENT | NodeFilter.SHOW_TEXT
        );
        const { currentScript } = document;
        let count = 0;
        for (;;) {
            const node = treeWalker.nextNode();
            if ( node === null ) { break; }
            count += 1;
            if ( node === currentScript ) { continue; }
            if ( reNodeName.test(node.nodeName) ) {
                handleNode(node);
            } else if ( node.nodeName === 'TEMPLATE' ) {
                count += handleTree(node.content);
            } else {
                continue;
            }
            if ( sedCount === 0 ) { break; }
        }
        return count;
    };
    if ( document.documentElement ) {
        const count = handleTree(document.documentElement);
        safe.uboLog(logPrefix, `${count} nodes present before installing mutation observer`);
    }
    const stay = Boolean(extraArgs.stay);
    if ( sedCount === 0 && stay === false ) { return; }
    const stop = (takeRecord = true) => {
        const mutations = takeRecord ? observer.takeRecords() : [];
        observer.disconnect();
        handleMutations(mutations);
        if ( safe.logLevel > 1 ) {
            safe.uboLog(logPrefix, 'Quitting');
        }
    };
    const handleMutations = mutations => {
        for ( const mutation of mutations ) {
            for ( const node of mutation.addedNodes ) {
                if ( reNodeName.test(node.nodeName) ) {
                    handleNode(node);
                } else if ( node.nodeName === 'TEMPLATE' ) {
                    handleTree(node.content);
                } else {
                    continue;
                }
                if ( sedCount === 0 ) { return stop(false); }
            }
        }
    };
    const observer = new MutationObserver(handleMutations);
    observer.observe(document, { childList: true, subtree: true });
    if ( stay ) { return; }
    runAt(( ) => {
        const quitAfter = extraArgs.quitAfter ?? 0;
        if ( quitAfter === 0 ) { return stop(); }
        setTimeout(( ) => { stop(); }, quitAfter);
    }, 'interactive');
}
function getRandomTokenFn() {
    const safe = safeSelf();
    return safe.String_fromCharCode(Date.now() % 26 + 97) +
        safe.Math_floor(safe.Math_random() * 982451653 + 982451653).toString(36);
}
function safeSelf() {
    if ( safeSelf.safe ) {
        return safeSelf.safe;
    }
    const self = globalThis;
    const safe = {
        'Array_from': Array.from,
        'Error': self.Error,
        'Function_toString': Function.prototype.call.bind(self.Function.prototype.toString),
        'Math_floor': Math.floor,
        'Math_max': Math.max,
        'Math_min': Math.min,
        'Math_random': Math.random,
        'Object': Object,
        'Object_defineProperty': Object.defineProperty.bind(Object),
        'Object_defineProperties': Object.defineProperties.bind(Object),
        'Object_fromEntries': Object.fromEntries.bind(Object),
        'Object_getOwnPropertyDescriptor': Object.getOwnPropertyDescriptor.bind(Object),
        'Object_hasOwn': Object.hasOwn.bind(Object),
        'Object_toString': Object.prototype.toString,
        'RegExp': self.RegExp,
        'RegExp_test': Function.prototype.call.bind(self.RegExp.prototype.test),
        'RegExp_exec': self.RegExp.prototype.exec,
        'Request_clone': self.Request.prototype.clone,
        'String': self.String,
        'String_fromCharCode': String.fromCharCode,
        'String_split': String.prototype.split,
        'XMLHttpRequest': self.XMLHttpRequest,
        'addEventListener': self.EventTarget.prototype.addEventListener,
        'removeEventListener': self.EventTarget.prototype.removeEventListener,
        'fetch': self.fetch,
        'JSON': self.JSON,
        'JSON_parse': Function.prototype.call.bind(self.JSON.parse, self.JSON),
        'JSON_stringify': Function.prototype.call.bind(self.JSON.stringify, self.JSON),
        'log': console.log.bind(console),
        // Properties
        logLevel: 0,
        // Methods
        makeLogPrefix(...args) {
            return this.sendToLogger && `[${args.join(' \u205D ')}]` || '';
        },
        uboLog(...args) {
            if ( this.sendToLogger === undefined ) { return; }
            if ( args === undefined || args[0] === '' ) { return; }
            return this.sendToLogger('info', ...args);
            
        },
        uboErr(...args) {
            if ( this.sendToLogger === undefined ) { return; }
            if ( args === undefined || args[0] === '' ) { return; }
            return this.sendToLogger('error', ...args);
        },
        escapeRegexChars(s) {
            return s.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
        },
        initPattern(pattern, options = {}) {
            if ( pattern === '' ) {
                return { matchAll: true, expect: true };
            }
            const expect = (options.canNegate !== true || pattern.startsWith('!') === false);
            if ( expect === false ) {
                pattern = pattern.slice(1);
            }
            const match = /^\/(.+)\/([gimsu]*)$/.exec(pattern);
            if ( match !== null ) {
                return {
                    re: new this.RegExp(
                        match[1],
                        match[2] || options.flags
                    ),
                    expect,
                };
            }
            if ( options.flags !== undefined ) {
                return {
                    re: new this.RegExp(this.escapeRegexChars(pattern),
                        options.flags
                    ),
                    expect,
                };
            }
            return { pattern, expect };
        },
        testPattern(details, haystack) {
            if ( details.matchAll ) { return true; }
            if ( details.re ) {
                return this.RegExp_test(details.re, haystack) === details.expect;
            }
            return haystack.includes(details.pattern) === details.expect;
        },
        patternToRegex(pattern, flags = undefined, verbatim = false) {
            if ( pattern === '' ) { return /^/; }
            const match = /^\/(.+)\/([gimsu]*)$/.exec(pattern);
            if ( match === null ) {
                const reStr = this.escapeRegexChars(pattern);
                return new RegExp(verbatim ? `^${reStr}$` : reStr, flags);
            }
            try {
                return new RegExp(match[1], match[2] || undefined);
            }
            catch {
            }
            return /^/;
        },
        parseVarargs(varargs) {
            const entries = varargs.reduce((out, v, i, a) => {
                if ( i & 1 ) { return out; }
                const rawValue = a[i+1];
                const value = /^\d+$/.test(rawValue)
                    ? parseInt(rawValue, 10)
                    : rawValue;
                out.push([ a[i], value ]);
                return out;
            }, []);
            return this.Object_fromEntries(entries);
        },
    };
    safeSelf.safe = safe;
    if ( scriptletGlobals.bcSecret === undefined ) { return safe; }
    // This is executed only when the logger is opened
    safe.logLevel = scriptletGlobals.logLevel || 1;
    let lastLogType = '';
    let lastLogText = '';
    let lastLogTime = 0;
    safe.toLogText = (type, ...args) => {
        if ( args.length === 0 ) { return; }
        const text = `[${document.location.hostname || document.location.href}]${args.join(' ')}`;
        if ( text === lastLogText && type === lastLogType ) {
            if ( (Date.now() - lastLogTime) < 5000 ) { return; }
        }
        lastLogType = type;
        lastLogText = text;
        lastLogTime = Date.now();
        return text;
    };
    try {
        const bc = new self.BroadcastChannel(scriptletGlobals.bcSecret);
        let bcBuffer = [];
        safe.sendToLogger = (type, ...args) => {
            const text = safe.toLogText(type, ...args);
            if ( text === undefined ) { return; }
            if ( bcBuffer === undefined ) {
                return bc.postMessage({ what: 'messageToLogger', type, text });
            }
            bcBuffer.push({ type, text });
        };
        bc.onmessage = ev => {
            const msg = ev.data;
            switch ( msg ) {
            case 'iamready!':
                if ( bcBuffer === undefined ) { break; }
                bcBuffer.forEach(({ type, text }) =>
                    bc.postMessage({ what: 'messageToLogger', type, text })
                );
                bcBuffer = undefined;
                break;
            case 'setScriptletLogLevelToOne':
                safe.logLevel = 1;
                break;
            case 'setScriptletLogLevelToTwo':
                safe.logLevel = 2;
                break;
            }
        };
        bc.postMessage('areyouready?');
    } catch {
        safe.sendToLogger = (type, ...args) => {
            const text = safe.toLogText(type, ...args);
            if ( text === undefined ) { return; }
            safe.log(`uBO ${text}`);
        };
    }
    return safe;
}
function runAt(fn, when) {
    const intFromReadyState = state => {
        const targets = {
            'loading': 1, 'asap': 1,
            'interactive': 2, 'end': 2, '2': 2,
            'complete': 3, 'idle': 3, '3': 3,
        };
        const tokens = Array.isArray(state) ? state : [ state ];
        for ( const token of tokens ) {
            const prop = `${token}`;
            if ( Object.hasOwn(targets, prop) === false ) { continue; }
            return targets[prop];
        }
        return 0;
    };
    const runAt = intFromReadyState(when);
    if ( intFromReadyState(document.readyState) >= runAt ) {
        fn(); return;
    }
    const onStateChange = ( ) => {
        if ( intFromReadyState(document.readyState) < runAt ) { return; }
        fn();
        safe.removeEventListener.apply(document, args);
    };
    const safe = safeSelf();
    const args = [ 'readystatechange', onStateChange, { capture: true } ];
    safe.addEventListener.apply(document, args);
}
function removeNodeText(
    nodeName,
    includes,
    ...extraArgs
) {
    replaceNodeTextFn(nodeName, '', '', 'includes', includes || '', ...extraArgs);
}
function matchObjectPropertiesFn(propNeedles, ...objs) {
    const safe = safeSelf();
    const matched = [];
    for ( const obj of objs ) {
        if ( obj instanceof Object === false ) { continue; }
        for ( const [ prop, details ] of propNeedles ) {
            let value = obj[prop];
            if ( value === undefined ) { continue; }
            if ( typeof value !== 'string' ) {
                try { value = safe.JSON_stringify(value); }
                catch { }
                if ( typeof value !== 'string' ) { continue; }
            }
            if ( safe.testPattern(details, value) === false ) { return; }
            matched.push(`${prop}: ${value}`);
        }
    }
    return matched;
}
function parsePropertiesToMatchFn(propsToMatch, implicit = '') {
    const safe = safeSelf();
    const needles = new Map();
    if ( propsToMatch === undefined || propsToMatch === '' ) { return needles; }
    const options = { canNegate: true };
    for ( const needle of safe.String_split.call(propsToMatch, /\s+/) ) {
        let [ prop, pattern ] = safe.String_split.call(needle, ':');
        if ( prop === '' ) { continue; }
        if ( pattern !== undefined && /[^$\w -]/.test(prop) ) {
            prop = `${prop}:${pattern}`;
            pattern = undefined;
        }
        if ( pattern !== undefined ) {
            needles.set(prop, safe.initPattern(pattern, options));
        } else if ( implicit !== '' ) {
            needles.set(implicit, safe.initPattern(prop, options));
        }
    }
    return needles;
}
function trustedReplaceXhrResponse(
    pattern = '',
    replacement = '',
    propsToMatch = '',
    ...varargs
) {
    const safe = safeSelf();
    const logPrefix = safe.makeLogPrefix('trusted-replace-xhr-response', pattern, replacement, propsToMatch);
    const xhrInstances = new WeakMap();
    if ( pattern === '*' ) { pattern = '.*'; }
    const rePattern = safe.patternToRegex(pattern);
    const propNeedles = parsePropertiesToMatchFn(propsToMatch, 'url');
    const extraArgs = safe.parseVarargs(varargs);
    const reIncludes = extraArgs.includes ? safe.patternToRegex(extraArgs.includes) : null;
    self.XMLHttpRequest = class extends self.XMLHttpRequest {
        open(method, url, ...args) {
            const outerXhr = this;
            const xhrDetails = { method, url };
            let outcome = 'match';
            if ( propNeedles.size !== 0 ) {
                if ( matchObjectPropertiesFn(propNeedles, xhrDetails) === undefined ) {
                    outcome = 'nomatch';
                }
            }
            if ( outcome === 'match' ) {
                if ( safe.logLevel > 1 ) {
                    safe.uboLog(logPrefix, `Matched "propsToMatch"`);
                }
                xhrInstances.set(outerXhr, xhrDetails);
            }
            return super.open(method, url, ...args);
        }
        get response() {
            const innerResponse = super.response;
            const xhrDetails = xhrInstances.get(this);
            if ( xhrDetails === undefined ) {
                return innerResponse;
            }
            const responseLength = typeof innerResponse === 'string'
                ? innerResponse.length
                : undefined;
            if ( xhrDetails.lastResponseLength !== responseLength ) {
                xhrDetails.response = undefined;
                xhrDetails.lastResponseLength = responseLength;
            }
            if ( xhrDetails.response !== undefined ) {
                return xhrDetails.response;
            }
            if ( typeof innerResponse !== 'string' ) {
                return (xhrDetails.response = innerResponse);
            }
            if ( reIncludes && reIncludes.test(innerResponse) === false ) {
                return (xhrDetails.response = innerResponse);
            }
            const textBefore = innerResponse;
            const textAfter = textBefore.replace(rePattern, replacement);
            if ( textAfter !== textBefore ) {
                safe.uboLog(logPrefix, 'Match');
            }
            return (xhrDetails.response = textAfter);
        }
        get responseText() {
            const response = this.response;
            if ( typeof response !== 'string' ) {
                return super.responseText;
            }
            return response;
        }
    };
}
function objectPruneFn(
    obj,
    rawPrunePaths,
    rawNeedlePaths,
    stackNeedleDetails = { matchAll: true },
    extraArgs = {}
) {
    if ( typeof rawPrunePaths !== 'string' ) { return; }
    const safe = safeSelf();
    const prunePaths = rawPrunePaths !== ''
        ? safe.String_split.call(rawPrunePaths, / +/)
        : [];
    const needlePaths = prunePaths.length !== 0 && rawNeedlePaths !== ''
        ? safe.String_split.call(rawNeedlePaths, / +/)
        : [];
    if ( stackNeedleDetails.matchAll !== true ) {
        if ( matchesStackTraceFn(stackNeedleDetails, extraArgs.logstack) === false ) {
            return;
        }
    }
    if ( objectPruneFn.mustProcess === undefined ) {
        objectPruneFn.mustProcess = (root, needlePaths) => {
            for ( const needlePath of needlePaths ) {
                if ( objectFindOwnerFn(root, needlePath) === false ) {
                    return false;
                }
            }
            return true;
        };
    }
    if ( prunePaths.length === 0 ) { return; }
    let outcome = 'nomatch';
    if ( objectPruneFn.mustProcess(obj, needlePaths) ) {
        for ( const path of prunePaths ) {
            if ( objectFindOwnerFn(obj, path, true) ) {
                outcome = 'match';
            }
        }
    }
    if ( outcome === 'match' ) { return obj; }
}
function matchesStackTraceFn(
    needleDetails,
    logLevel = ''
) {
    const safe = safeSelf();
    const exceptionToken = getExceptionTokenFn();
    const error = new safe.Error(exceptionToken);
    const docURL = new URL(self.location.href);
    docURL.hash = '';
    // Normalize stack trace
    const reLine = /(.*?@)?(\S+)(:\d+):\d+\)?$/;
    const lines = [];
    for ( let line of safe.String_split.call(error.stack, /[\n\r]+/) ) {
        if ( line.includes(exceptionToken) ) { continue; }
        line = line.trim();
        const match = safe.RegExp_exec.call(reLine, line);
        if ( match === null ) { continue; }
        let url = match[2];
        if ( url.startsWith('(') ) { url = url.slice(1); }
        if ( url === docURL.href ) {
            url = 'inlineScript';
        } else if ( url.startsWith('<anonymous>') ) {
            url = 'injectedScript';
        }
        let fn = match[1] !== undefined
            ? match[1].slice(0, -1)
            : line.slice(0, match.index).trim();
        if ( fn.startsWith('at') ) { fn = fn.slice(2).trim(); }
        let rowcol = match[3];
        lines.push(' ' + `${fn} ${url}${rowcol}:1`.trim());
    }
    lines[0] = `stackDepth:${lines.length-1}`;
    const stack = lines.join('\t');
    const r = needleDetails.matchAll !== true &&
        safe.testPattern(needleDetails, stack);
    if (
        logLevel === 'all' ||
        logLevel === 'match' && r ||
        logLevel === 'nomatch' && !r
    ) {
        safe.uboLog(stack.replace(/\t/g, '\n'));
    }
    return r;
}
function getExceptionTokenFn() {
    const token = getRandomTokenFn();
    const oe = self.onerror;
    self.onerror = function(msg, ...args) {
        if ( typeof msg === 'string' && msg.includes(token) ) { return true; }
        if ( oe instanceof Function ) {
            return oe.call(this, msg, ...args);
        }
    }.bind();
    return token;
}
function objectFindOwnerFn(
    root,
    path,
    prune = false
) {
    const safe = safeSelf();
    let owner = root;
    let chain = path;
    for (;;) {
        if ( typeof owner !== 'object' || owner === null  ) { return false; }
        const pos = chain.indexOf('.');
        if ( pos === -1 ) {
            if ( prune === false ) {
                return safe.Object_hasOwn(owner, chain);
            }
            let modified = false;
            if ( chain === '*' ) {
                for ( const key in owner ) {
                    if ( safe.Object_hasOwn(owner, key) === false ) { continue; }
                    delete owner[key];
                    modified = true;
                }
            } else if ( safe.Object_hasOwn(owner, chain) ) {
                delete owner[chain];
                modified = true;
            }
            return modified;
        }
        const prop = chain.slice(0, pos);
        const next = chain.slice(pos + 1);
        let found = false;
        if ( prop === '[-]' && Array.isArray(owner) ) {
            let i = owner.length;
            while ( i-- ) {
                if ( objectFindOwnerFn(owner[i], next) === false ) { continue; }
                owner.splice(i, 1);
                found = true;
            }
            return found;
        }
        if ( prop === '{-}' && owner instanceof Object ) {
            for ( const key of Object.keys(owner) ) {
                if ( objectFindOwnerFn(owner[key], next) === false ) { continue; }
                delete owner[key];
                found = true;
            }
            return found;
        }
        if (
            prop === '[]' && Array.isArray(owner) ||
            prop === '{}' && owner instanceof Object ||
            prop === '*' && owner instanceof Object
        ) {
            for ( const key of Object.keys(owner) ) {
                if (objectFindOwnerFn(owner[key], next, prune) === false ) { continue; }
                found = true;
            }
            return found;
        }
        if ( safe.Object_hasOwn(owner, prop) === false ) { return false; }
        owner = owner[prop];
        chain = chain.slice(pos + 1);
    }
}
function proxyApplyFn(
    target = '',
    handler = '',
    options = {}
) {
    let context = globalThis;
    let prop = target;
    for (;;) {
        const pos = prop.indexOf('.');
        if ( pos === -1 ) { break; }
        context = context[prop.slice(0, pos)];
        if ( context instanceof Object === false ) { return; }
        prop = prop.slice(pos+1);
    }
    const fn = context[prop];
    if ( typeof fn !== 'function' ) { return; }
    if ( proxyApplyFn.CtorContext === undefined ) {
        proxyApplyFn.ctorContexts = [];
        proxyApplyFn.CtorContext = class {
            constructor(...args) {
                this.init(...args);
            }
            init(callFn, callArgs) {
                this.callFn = callFn;
                this.callArgs = callArgs;
                return this;
            }
            reflect() {
                const r = Reflect.construct(this.callFn, this.callArgs);
                this.callFn = this.callArgs = this.private = undefined;
                proxyApplyFn.ctorContexts.push(this);
                return r;
            }
            static factory(...args) {
                return proxyApplyFn.ctorContexts.length !== 0
                    ? proxyApplyFn.ctorContexts.pop().init(...args)
                    : new proxyApplyFn.CtorContext(...args);
            }
        };
        proxyApplyFn.applyContexts = [];
        proxyApplyFn.ApplyContext = class {
            constructor(...args) {
                this.init(...args);
            }
            init(callFn, thisArg, callArgs) {
                this.callFn = callFn;
                this.thisArg = thisArg;
                this.callArgs = callArgs;
                return this;
            }
            reflect() {
                const r = Reflect.apply(this.callFn, this.thisArg, this.callArgs);
                this.callFn = this.thisArg = this.callArgs = this.private = undefined;
                proxyApplyFn.applyContexts.push(this);
                return r;
            }
            static factory(...args) {
                return proxyApplyFn.applyContexts.length !== 0
                    ? proxyApplyFn.applyContexts.pop().init(...args)
                    : new proxyApplyFn.ApplyContext(...args);
            }
        };
        proxyApplyFn.isCtor = new Map();
        proxyApplyFn.proxies = new WeakMap();
        if ( (options.skipToString || proxyApplyFn.skipToString) !== true ) {
            proxyApplyFn.nativeToString = Function.prototype.toString;
            const proxiedToString = new Proxy(Function.prototype.toString, {
                apply(target, thisArg) {
                    let proxied = thisArg;
                    for(;;) {
                        const fn = proxyApplyFn.proxies.get(proxied);
                        if ( fn === undefined ) { break; }
                        proxied = fn;
                    }
                    return proxyApplyFn.nativeToString.call(proxied);
                }
            });
            proxyApplyFn.proxies.set(proxiedToString, proxyApplyFn.nativeToString);
            Function.prototype.toString = proxiedToString;
        }
    }
    if ( proxyApplyFn.isCtor.has(target) === false ) {
        proxyApplyFn.isCtor.set(target, fn.prototype?.constructor === fn);
    }
    const proxyDetails = {
        apply(target, thisArg, args) {
            return handler(proxyApplyFn.ApplyContext.factory(target, thisArg, args));
        }
    };
    if ( proxyApplyFn.isCtor.get(target) ) {
        proxyDetails.construct = function(target, args) {
            return handler(proxyApplyFn.CtorContext.factory(target, args));
        };
    }
    const proxiedTarget = new Proxy(fn, proxyDetails);
    proxyApplyFn.proxies.set(proxiedTarget, fn);
    context[prop] = proxiedTarget;
}
function jsonPrune(
    rawPrunePaths = '',
    rawNeedlePaths = '',
    stackNeedle = '',
    ...varargs
) {
    const safe = safeSelf();
    const logPrefix = safe.makeLogPrefix('json-prune', rawPrunePaths, rawNeedlePaths, stackNeedle);
    const stackNeedleDetails = safe.initPattern(stackNeedle, { canNegate: true });
    const extraArgs = safe.parseVarargs(varargs);
    proxyApplyFn('JSON.parse', function(context) {
        const objBefore = context.reflect();
        if ( rawPrunePaths === '' ) {
            safe.uboLog(logPrefix, safe.JSON_stringify(objBefore, null, 2));
        }
        const objAfter = objectPruneFn(
            objBefore,
            rawPrunePaths,
            rawNeedlePaths,
            stackNeedleDetails,
            extraArgs
        );
        if ( objAfter === undefined ) { return objBefore; }
        safe.uboLog(logPrefix, 'Pruned');
        if ( safe.logLevel > 1 ) {
            safe.uboLog(logPrefix, `After pruning:\n${safe.JSON_stringify(objAfter, null, 2)}`);
        }
        return objAfter;
    });
}
function editInboundObjectFn(
    trusted = false,
    propChain = '',
    argPosRaw = '',
    jsonq = '',
) {
    if ( propChain === '' ) { return; }
    const safe = safeSelf();
    const logPrefix = safe.makeLogPrefix(
        `${trusted ? 'trusted-' : ''}edit-inbound-object`,
        propChain,
        jsonq
    );
    const jsonp = JSONPath.create(jsonq);
    if ( jsonp.valid === false || jsonp.value !== undefined && trusted !== true ) {
        return safe.uboLog(logPrefix, 'Bad JSONPath query');
    }
    const argPos = parseInt(argPosRaw, 10);
    if ( isNaN(argPos) ) { return; }
    const getArgPos = args => {
        if ( Array.isArray(args) === false ) { return; }
        if ( argPos >= 0 ) {
            if ( args.length <= argPos ) { return; }
            return argPos;
        }
        if ( args.length < -argPos ) { return; }
        return args.length + argPos;
    };
    const editObj = obj => {
        let clone;
        try {
            clone = safe.JSON_parse(safe.JSON_stringify(obj));
        } catch {
        }
        if ( typeof clone !== 'object' || clone === null ) { return; }
        const objAfter = jsonp.apply(clone);
        if ( objAfter === undefined ) { return; }
        safe.uboLog(logPrefix, 'Edited');
        if ( safe.logLevel > 1 ) {
            safe.uboLog(logPrefix, `After edit:\n${safe.JSON_stringify(objAfter, null, 2)}`);
        }
        return objAfter;
    };
    proxyApplyFn(propChain, function(context) {
        const i = getArgPos(context.callArgs);
        if ( i !== undefined ) {
            const obj = editObj(context.callArgs[i]);
            if ( obj ) {
                context.callArgs[i] = obj;
            }
        }
        return context.reflect();
    });
}
class JSONPath {
    static create(query) {
        const jsonp = new JSONPath();
        jsonp.compile(query);
        return jsonp;
    }
    static toJSON(obj, stringifier, ...args) {
        return (stringifier || JSON.stringify)(obj, ...args)
            .replace(/\//g, '\\/');
    }
    static keys = Object.keys;
    static entries = Object.entries;
    static hasOwn = Object.hasOwn;
    static Regex = RegExp;
    get value() {
        return this.#compiled && this.#compiled.rval;
    }
    set value(v) {
        if ( this.#compiled === undefined ) { return; }
        this.#compiled.rval = v;
    }
    get valid() {
        return this.#compiled !== undefined;
    }
    compile(query) {
        this.#compiled = undefined;
        this.v2 = query.startsWith('v2:');
        if ( this.v2 ) { query = query.slice(3); }
        const r = this.#compile(query, 0);
        if ( r === undefined ) { return; }
        if ( r.i !== query.length ) {
            let val;
            if ( query.startsWith('=', r.i) ) {
                const match = this.#reRval.exec(query.slice(r.i));
                if ( match ) {
                    r.modify = match[1];
                    val = match[2];
                } else {
                    val = query.slice(r.i+1);
                }
            } else if ( query.startsWith('+=', r.i) ) {
                r.modify = '+';
                val = query.slice(r.i+2);
            }
            try { r.rval = JSON.parse(val); }
            catch { return; }
        }
        r.v2 = this.v2;
        this.#compiled = r;
    }
    evaluate(root) {
        if ( this.valid === false ) { return []; }
        this.#root = { '$': root };
        const paths = this.#evaluate(this.#compiled.steps, []);
        this.#root = null;
        return paths;
    }
    apply(root) {
        if ( this.valid === false ) { return; }
        const { rval } = this.#compiled;
        this.#root = { '$': root };
        const paths = this.#evaluate(this.#compiled.steps, []);
        let i = paths.length
        if ( i === 0 ) { this.#root = null; return; }
        while ( i-- ) {
            const { obj, key } = this.#resolvePath(paths[i]);
            if ( obj === undefined ) { continue; }
            if ( rval !== undefined ) {
                this.#modifyVal(obj, key);
            } else if ( Array.isArray(obj) && typeof key === 'number' ) {
                obj.splice(key, 1);
            } else {
                delete obj[key];
            }
        }
        const result = this.#root['$'] ?? null;
        this.#root = null;
        return result;
    }
    dump() {
        return JSON.stringify(this.#compiled);
    }
    toJSON(obj, ...args) {
        return JSONPath.toJSON(obj, null, ...args)
    }
    get [Symbol.toStringTag]() {
        return 'JSONPath';
    }
    #UNDEFINED = 0;
    #ROOT = 1;
    #CURRENT = 2;
    #CHILDREN = 3;
    #DESCENDANTS = 4;
    #QUANTIFIER = 5;
    #reUnquotedIdentifier = /^[A-Za-z_][\w]*|^\*/;
    #reExpr = /^\s*([!=^$*]=|[<>]=?)\s*(.+?)\]/;
    #reIndice = /^-?\d+/;
    #reRval = /^=([a-z]+)\((.+)\)$/;
    #reQuantifier = /^\{(\d+|\d+,\d+|\d+,|,\d+)\};\$/;
    #root;
    #compiled;
    #compile(query, i) {
        if ( query.length === 0 ) { return; }
        const steps = [];
        let c = query.charCodeAt(i);
        if ( c === 0x24 /* $ */ ) {
            steps.push({ mv: this.#ROOT });
            i += 1;
        } else if ( c === 0x40 /* @ */ ) {
            steps.push({ mv: this.#CURRENT });
            i += 1;
        } else {
            steps.push({ mv: i === 0 ? this.#ROOT : this.#CURRENT });
        }
        let mv = this.#UNDEFINED;
        for (;;) {
            if ( i === query.length ) { break; }
            c = query.charCodeAt(i);
            if ( c === 0x20 /* whitespace */ ) {
                i += 1;
                continue;
            }
            // Dot accessor syntax
            if ( c === 0x2E /* . */ ) {
                if ( mv !== this.#UNDEFINED ) { return; }
                if ( query.startsWith('..', i) ) {
                    mv = this.#DESCENDANTS;
                    i += 2;
                } else {
                    mv = this.#CHILDREN;
                    i += 1;
                }
                continue;
            }
            if ( c === 0x3B /* ; */ ) {
                if ( query.startsWith(';$', i) === false ) { return; }
                steps.push(
                    { mv: this.#QUANTIFIER, min: 1, max: 1e6 },
                    { mv: this.#ROOT }
                );
                i += 2;
                mv = this.#UNDEFINED;
                continue;
            }
            if ( c === 0x7B /* { */ ) {
                const match = this.#reQuantifier.exec(query.slice(i));
                if ( match === null ) { return; }
                const comma = match[1].indexOf(',');
                let min, max;
                if ( comma === -1 ) {
                    min = max = parseInt(match[1]);
                } else {
                    min = parseInt(match[1].slice(0, comma)) || 0;
                    max = parseInt(match[1].slice(comma+1)) || 1e6;
                }
                steps.push(
                    { mv: this.#QUANTIFIER, min, max },
                    { mv: this.#ROOT }
                );
                i += match[0].length;
                mv = this.#UNDEFINED;
                continue;
            }
            if ( c !== 0x5B /* [ */ ) {
                if ( mv === this.#UNDEFINED ) {
                    const step = steps.at(-1);
                    if ( step === undefined ) { return; }
                    const j = this.#compileExpr(query, step, i);
                    if ( j ) { i = j; }
                    break;
                }
                const r = this.#consumeUnquotedIdentifier(query, i);
                if  ( r === undefined ) { return; }
                steps.push({ mv, k: r.s });
                i = r.i;
                mv = this.#UNDEFINED;
                continue;
            }
            // Bracket accessor syntax
            if ( mv === this.#CHILDREN ) { return; }
            if ( query.startsWith('[?', i) ) {
                const not = query.charCodeAt(i+2) === 0x21 /* ! */ ? 1 : 0;
                const j = i + 2 + not;
                const r = this.#compile(query, j);
                if ( r === undefined ) { return; }
                if ( query.startsWith(']', r.i) === false ) { return; }
                if ( not ) { r.steps.at(-1).not = true; }
                steps.push({ mv: mv || this.#CHILDREN, steps: r.steps });
                i = r.i + 1;
                mv = this.#UNDEFINED;
                continue;
            }
            if ( query.startsWith('[*]', i) ) {
                mv ||= this.#CHILDREN;
                steps.push({ mv, k: '*' });
                i += 3;
                mv = this.#UNDEFINED;
                continue;
            }
            const r = this.#consumeIdentifier(query, i+1);
            if ( r === undefined ) { return; }
            mv ||= this.#CHILDREN;
            steps.push({ mv, k: r.s });
            i = r.i + 1;
            mv = this.#UNDEFINED;
        }
        if ( steps.length === 0 ) { return; }
        if ( mv !== this.#UNDEFINED ) { return; }
        return { steps, i };
    }
    #evaluate(steps, pathin) {
        let resultset = [];
        if ( Array.isArray(steps) === false ) { return resultset; }
        for ( const step of steps ) {
            switch ( step.mv ) {
            case this.#ROOT:
                resultset = [ [ '$' ] ];
                break;
            case this.#CURRENT:
                if ( step.op ) {
                    const { obj, key } = this.#resolvePath(pathin);
                    if ( obj === undefined ) { return []; }
                    const outcome = this.#evaluateExpr(step, obj, key);
                    if ( outcome !== true ) { break; }
                }
                resultset = [ pathin ];
                break;
            case this.#CHILDREN:
            case this.#DESCENDANTS: {
                if ( resultset.length === 0 ) { break; }
                resultset = this.#getMatches(resultset, step);
                break;
            }
            case this.#QUANTIFIER: {
                const { length } = resultset;
                if ( length < step.min || length > step.max ) { return []; }
                resultset = [];
                break;
            }
            default:
                break;
            }
        }
        return resultset;
    }
    #getMatches(listin, step) {
        const listout = [];
        for ( const pathin of listin ) {
            const { value: owner } = this.#resolvePath(pathin);
            if ( owner === undefined ) { continue; }
            if ( step.steps ) {
                this.#getMatchesFromExpr(pathin, step, owner, listout);
                continue;
            }
            const iter = this.#expandKey(owner, step.k);
            if ( iter ) {
                for ( const k of iter ) {
                    const outcome = this.#evaluateExpr(step, owner, k);
                    if ( outcome !== true ) { continue; }
                    listout.push([ ...pathin, k ]);
                }
            }
            if ( step.mv !== this.#DESCENDANTS ) { continue; }
            for ( const { obj, key, path } of this.#getDescendants(owner, true) ) {
                const iter = this.#expandKey(obj[key], step.k);
                if ( iter === undefined ) { continue; }
                for ( const k of iter ) {
                    const outcome = this.#evaluateExpr(step, obj[key], k);
                    if ( outcome !== true ) { continue; }
                    listout.push([ ...pathin, ...path, k ]);
                }
            }
        }
        return listout;
    }
    #expandKey(owner, k) {
        if ( typeof owner !== 'object' || owner === null ) { return; }
        if ( Array.isArray(k) ) {
            const out = [];
            for ( const a of k ) {
                const iter = this.#expandKey(owner, a);
                if ( iter === undefined ) { continue; }
                out.push(...iter);
            }
            return out;
        }
        if ( typeof k === 'number' ) {
            if ( Array.isArray(owner) === false ) { return; }
            return [ k >= 0 ? k : owner.length + k ];
        }
        if ( k === '*' ) {
            if ( Array.isArray(owner) ) { return owner.keys(); }
            return JSONPath.keys(owner);
        }
        if ( k instanceof JSONPath.Regex ) {
            const out = [];
            for ( const key of JSONPath.keys(owner) ) {
                if ( k.test(key) === false ) { continue; }
                out.push(key);
            }
            return out;
        }
        return [ k ];
    }
    #getMatchesFromExpr(pathin, step, owner, out) {
        const recursive = step.mv === this.#DESCENDANTS;
        const v2 = this.#compiled.v2 || recursive || Array.isArray(owner);
        for ( const { path } of this.#getDescendants(owner, recursive) ) {
            const q = v2 ? [ ...pathin, ...path ] : pathin;
            const r = this.#evaluate(step.steps, q);
            if ( Boolean(r?.length) === false ) { continue; }
            out.push(q);
            if ( v2 === false ) { break; }
        }
    }
    #getDescendants(v, recursive) {
        const iterator = {
            next() {
                const n = this.stack.length;
                if ( n === 0 ) {
                    this.value = undefined;
                    this.done = true;
                    return this;
                }
                const details = this.stack[n-1];
                const entry = details.keys.next();
                if ( entry.done ) {
                    this.stack.pop();
                    this.path.pop();
                    return this.next();
                }
                this.path[n-1] = entry.value;
                this.value = {
                    obj: details.obj,
                    key: entry.value,
                    path: this.path.slice(),
                };
                const v = this.value.obj[this.value.key];
                if ( recursive ) {
                    if ( Array.isArray(v) ) {
                        this.stack.push({ obj: v, keys: v.keys() });
                    } else if ( typeof v === 'object' && v !== null ) {
                        this.stack.push({ obj: v, keys: JSONPath.keys(v).values() });
                    }
                }
                return this;
            },
            path: [],
            value: undefined,
            done: false,
            stack: [],
            [Symbol.iterator]() { return this; },
        };
        if ( Array.isArray(v) ) {
            iterator.stack.push({ obj: v, keys: v.keys() });
        } else if ( typeof v === 'object' && v !== null ) {
            iterator.stack.push({ obj: v, keys: JSONPath.keys(v).values() });
        }
        return iterator;
    }
    #consumeIdentifier(query, i) {
        const keys = [];
        let needIdentifier = true;
        while ( i < query.length ) {
            const c0 = query.charCodeAt(i);
            if ( c0 === 0x5D /* ] */ ) { break; }
            if ( c0 === 0x20 /* SPACE */ ) {
                i += 1;
                continue;
            }
            if ( c0 === 0x2C /* , */ ) {
                if ( needIdentifier ) { return; }
                i += 1;
                needIdentifier = true;
                continue;
            }
            if ( c0 === 0x22 /* " */ || c0 === 0x27 /* ' */ ) {
                const r = this.#untilChar(query, c0, i+1);
                if ( r === undefined ) { return; }
                keys.push(r.s);
                i = r.i;
                needIdentifier = false;
                continue;
            }
            if ( c0 === 0x2D /* - */ || c0 >= 0x30 && c0 <= 0x39 ) {
                const match = this.#reIndice.exec(query.slice(i));
                if ( match === null ) { return; }
                const indice = parseInt(query.slice(i), 10);
                keys.push(indice);
                i += match[0].length;
                needIdentifier = false;
                continue;
            }
            if ( this.v2 ) { return; }
            const r = this.#consumeUnquotedIdentifier(query, i);
            if ( r === undefined ) { return; }
            keys.push(r.s);
            i = r.i;
        }
        if ( needIdentifier ) { return; }
        return { s: keys.length === 1 ? keys[0] : keys, i };
    }
    #consumeUnquotedIdentifier(query, i) {
        if ( query.charCodeAt(i) === 0x2F /* / */ ) {
            const r = this.#untilChar(query, 0x2F, i+1);
            if ( r === undefined ) { return; }
            let re;
            try { re = new JSONPath.Regex(r.s); } catch { return; }
            return { s: re, i: r.i };
        }
        const match = this.#reUnquotedIdentifier.exec(query.slice(i));
        if ( match === null ) { return; }
        return { s: match[0], i: i + match[0].length };
    }
    #untilChar(query, targetCharCode, i) {
        const len = query.length;
        const parts = [];
        let beg = i, end = i;
        for (;;) {
            if ( end === len ) { return; }
            const c = query.charCodeAt(end);
            if ( c === targetCharCode ) {
                parts.push(query.slice(beg, end));
                end += 1;
                break;
            }
            if ( c === 0x5C /* \ */ && (end+1) < len ) {
                const d = query.charCodeAt(end+1);
                if ( d === targetCharCode ) {
                    parts.push(query.slice(beg, end));
                    end += 1;
                    beg = end;
                }
            }
            end += 1;
        }
        return { s: parts.join(''), i: end };
    }
    #compileExpr(query, step, i) {
        if ( query.startsWith('=/', i) ) {
            const r = this.#untilChar(query, 0x2F /* / */, i+2);
            if ( r === undefined ) { return i; }
            const match = /^[i]/.exec(query.slice(r.i));
            try {
                step.rval = new JSONPath.Regex(r.s, match && match[0] || undefined);
            } catch { return; }
            step.op = 're';
            if ( match ) { r.i += match[0].length; }
            return r.i;
        }
        const match = this.#reExpr.exec(query.slice(i));
        if ( match === null ) { return; }
        const op = match[1], rval = match[2];
        if ( rval.charCodeAt(0) === 0x27 /* ' */ ) {
            const r = this.#untilChar(rval, 0x27, 1);
            if ( r === undefined ) { return; }
            step.rval = r.s;
            step.op = op;
        } else {
            try {
                step.rval = JSON.parse(rval);
                step.op = op;
            } catch { return; }
        }
        return i + match[0].length - 1;
    }
    #resolvePath(path) {
        if ( path.length === 0 ) { return { value: this.#root }; }
        const key = path.at(-1);
        let obj = this.#root
        for ( let i = 0, n = path.length-1; i < n; i++ ) {
            obj = obj[path[i]];
            if ( obj instanceof Object === false ) { return {}; }
        }
        return { obj, key, value: obj[key] };
    }
    #evaluateExpr(step, owner, k) {
        if ( owner === undefined || owner === null ) { return; }
        const hasOwn = owner[k] !== undefined || JSONPath.hasOwn(owner, k);
        if ( step.op !== undefined && hasOwn === false ) { return; }
        const target = step.not !== true;
        const v = owner[k];
        switch ( step.op ) {
        case '==': return (v === step.rval) === target;
        case '!=': return (v !== step.rval) === target;
        case  '<': return (v < step.rval) === target;
        case '<=': return (v <= step.rval) === target;
        case  '>': return (v > step.rval) === target;
        case '>=': return (v >= step.rval) === target;
        case '^=': return `${v}`.startsWith(step.rval) === target;
        case '$=': return `${v}`.endsWith(step.rval) === target;
        case '*=': return `${v}`.includes(step.rval) === target;
        case 're': return step.rval.test(`${v}`);
        default: break;
        }
        return hasOwn === target;
    }
    #modifyVal(obj, key) {
        let { modify, rval } = this.#compiled;
        if ( typeof rval === 'string' ) {
            rval = rval.replace('${now}', `${Date.now()}`);
        }
        switch ( modify ) {
        case undefined:
            obj[key] = rval;
            break;
        case '+': {
            if ( rval instanceof Object === false ) { return; }
            const lval = obj[key];
            if ( lval instanceof Object === false ) { return; }
            if ( Array.isArray(lval) ) {
                if ( Array.isArray(rval) ) {
                    lval.push(...rval); break;
                }
                lval.push(rval); break;
            }
            for ( const [ k, v ] of JSONPath.entries(rval) ) {
                lval[k] = v;
            }
            break;
        }
        case 'call': {
            const entries = rval.slice();
            if ( entries.length < 2 ) { break; }
            entries.forEach((a, i, aa) => {
                if ( a === '${obj}' ) { aa[i] = obj; }
                else if ( a === '${key}' ) { aa[i] = key; }
                else if ( a === '${val}' ) { aa[i] = obj[key]; }
            });
            const instance = entries[0] ?? self;
            instance[entries[1]](...entries.slice(2));
            break;
        }
        case 'repl': {
            const lval = obj[key];
            if ( typeof lval !== 'string' ) { return; }
            if ( this.#compiled.re === undefined ) {
                this.#compiled.re = null;
                try {
                    this.#compiled.re = rval.regex !== undefined
                        ? new JSONPath.Regex(rval.regex, rval.flags)
                        : new JSONPath.Regex(rval.pattern.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'));
                } catch { }
            }
            if ( this.#compiled.re === null ) { return; }
            obj[key] = lval.replace(this.#compiled.re, rval.replacement);
            break;
        }
        default:
            break;
        }
    }
}
function trustedEditInboundObject(propChain = '', argPos = '', jsonq = '') {
    editInboundObjectFn(true, propChain, argPos, jsonq);
}
function replaceFetchResponseFn(
    trusted = false,
    pattern = '',
    replacement = '',
    propsToMatch = '',
    ...varargs
) {
    if ( trusted !== true ) { return; }
    const safe = safeSelf();
    const logPrefix = safe.makeLogPrefix('replace-fetch-response', pattern, replacement, propsToMatch);
    if ( pattern === '*' ) { pattern = '.*'; }
    const rePattern = safe.patternToRegex(pattern);
    const propNeedles = parsePropertiesToMatchFn(propsToMatch, 'url');
    const extraArgs = safe.parseVarargs(varargs);
    const reIncludes = extraArgs.includes ? safe.patternToRegex(extraArgs.includes) : null;
    self.fetch = new Proxy(self.fetch, {
        apply: function(target, thisArg, args) {
            const fetchPromise = Reflect.apply(target, thisArg, args);
            if ( pattern === '' ) { return fetchPromise; }
            if ( propNeedles.size !== 0 ) {
                const props = collateFetchArgumentsFn(...args);
                const matched = matchObjectPropertiesFn(propNeedles, props);
                if ( matched === undefined ) { return fetchPromise; }
                if ( safe.logLevel > 1 ) {
                    safe.uboLog(logPrefix, `Matched "propsToMatch":\n\t${matched.join('\n\t')}`);
                }
            }
            return fetchPromise.then(responseBefore => {
                const response = responseBefore.clone();
                return response.text().then(textBefore => {
                    if ( reIncludes && reIncludes.test(textBefore) === false ) {
                        return responseBefore;
                    }
                    const textAfter = textBefore.replace(rePattern, replacement);
                    if ( textAfter === textBefore ) { return responseBefore; }
                    safe.uboLog(logPrefix, 'Replaced');
                    const responseAfter = new Response(textAfter, {
                        status: responseBefore.status,
                        statusText: responseBefore.statusText,
                        headers: responseBefore.headers,
                    });
                    Object.defineProperties(responseAfter, {
                        ok: { value: responseBefore.ok },
                        redirected: { value: responseBefore.redirected },
                        type: { value: responseBefore.type },
                        url: { value: responseBefore.url },
                    });
                    return responseAfter;
                }).catch(reason => {
                    safe.uboErr(logPrefix, reason);
                    return responseBefore;
                });
            }).catch(reason => {
                safe.uboErr(logPrefix, reason);
                return fetchPromise;
            });
        }
    });
}
function collateFetchArgumentsFn(resource, options) {
    const safe = safeSelf();
    const props = [
        'body', 'cache', 'credentials', 'duplex', 'headers',
        'integrity', 'keepalive', 'method', 'mode', 'priority',
        'redirect', 'referrer', 'referrerPolicy', 'url'
    ];
    const out = {};
    if ( collateFetchArgumentsFn.collateKnownProps === undefined ) {
        collateFetchArgumentsFn.collateKnownProps = (src, out) => {
            for ( const prop of props ) {
                if ( src[prop] === undefined ) { continue; }
                out[prop] = src[prop];
            }
        };
    }
    if (
        typeof resource !== 'object' ||
        safe.Object_toString.call(resource) !== '[object Request]'
    ) {
        out.url = `${resource}`;
    } else {
        let clone;
        try {
            clone = safe.Request_clone.call(resource);
        } catch {
        }
        collateFetchArgumentsFn.collateKnownProps(clone || resource, out);
    }
    if ( typeof options === 'object' && options !== null ) {
        collateFetchArgumentsFn.collateKnownProps(options, out);
    }
    return out;
}
function trustedReplaceFetchResponse(...args) {
    replaceFetchResponseFn(true, ...args);
}
function jsonPruneFetchResponse(
    rawPrunePaths = '',
    rawNeedlePaths = '',
    ...varargs
) {
    const safe = safeSelf();
    const logPrefix = safe.makeLogPrefix('json-prune-fetch-response', rawPrunePaths, rawNeedlePaths);
    const extraArgs = safe.parseVarargs(varargs);
    const propNeedles = parsePropertiesToMatchFn(extraArgs.propsToMatch, 'url');
    const stackNeedle = safe.initPattern(extraArgs.stackToMatch || '', { canNegate: true });
    const logall = rawPrunePaths === '';
    const applyHandler = function(target, thisArg, args) {
        const fetchPromise = Reflect.apply(target, thisArg, args);
        if ( propNeedles.size !== 0 ) {
            const props = collateFetchArgumentsFn(...args);
            const matched = matchObjectPropertiesFn(propNeedles, props);
            if ( matched === undefined ) { return fetchPromise; }
            if ( safe.logLevel > 1 ) {
                safe.uboLog(logPrefix, `Matched "propsToMatch":\n\t${matched.join('\n\t')}`);
            }
        }
        return fetchPromise.then(responseBefore => {
            const response = responseBefore.clone();
            return response.json().then(objBefore => {
                if ( typeof objBefore !== 'object' ) { return responseBefore; }
                if ( logall ) {
                    safe.uboLog(logPrefix, safe.JSON_stringify(objBefore, null, 2));
                    return responseBefore;
                }
                const objAfter = objectPruneFn(
                    objBefore,
                    rawPrunePaths,
                    rawNeedlePaths,
                    stackNeedle,
                    extraArgs
                );
                if ( typeof objAfter !== 'object' ) { return responseBefore; }
                safe.uboLog(logPrefix, 'Pruned');
                const responseAfter = Response.json(objAfter, {
                    status: responseBefore.status,
                    statusText: responseBefore.statusText,
                    headers: responseBefore.headers,
                });
                Object.defineProperties(responseAfter, {
                    ok: { value: responseBefore.ok },
                    redirected: { value: responseBefore.redirected },
                    type: { value: responseBefore.type },
                    url: { value: responseBefore.url },
                });
                return responseAfter;
            }).catch(reason => {
                safe.uboErr(logPrefix, 'Error:', reason);
                return responseBefore;
            });
        }).catch(reason => {
            safe.uboErr(logPrefix, 'Error:', reason);
            return fetchPromise;
        });
    };
    self.fetch = new Proxy(self.fetch, {
        apply: applyHandler
    });
}
function replaceNodeText(
    nodeName,
    pattern,
    replacement,
    ...extraArgs
) {
    replaceNodeTextFn(nodeName, pattern, replacement, ...extraArgs);
}
function setConstantFn(
    trusted = false,
    chain = '',
    rawValue = '',
    ...varargs
) {
    if ( chain === '' ) { return; }
    const safe = safeSelf();
    const logPrefix = safe.makeLogPrefix('set-constant', chain, rawValue);
    const extraArgs = safe.parseVarargs(varargs);
    function setConstant(chain, rawValue) {
        const trappedProp = (( ) => {
            const pos = chain.lastIndexOf('.');
            if ( pos === -1 ) { return chain; }
            return chain.slice(pos+1);
        })();
        const cloakFunc = fn => {
            safe.Object_defineProperty(fn, 'name', { value: trappedProp });
            return new Proxy(fn, {
                defineProperty(target, prop) {
                    if ( prop !== 'toString' ) {
                        return Reflect.defineProperty(...arguments);
                    }
                    return true;
                },
                deleteProperty(target, prop) {
                    if ( prop !== 'toString' ) {
                        return Reflect.deleteProperty(...arguments);
                    }
                    return true;
                },
                get(target, prop) {
                    if ( prop === 'toString' ) {
                        return function() {
                            return `function ${trappedProp}() { [native code] }`;
                        }.bind(null);
                    }
                    return Reflect.get(...arguments);
                },
            });
        };
        if ( trappedProp === '' ) { return; }
        const thisScript = document.currentScript;
        let normalValue = validateConstantFn(trusted, rawValue, extraArgs);
        if ( rawValue === 'noopFunc' || rawValue === 'trueFunc' || rawValue === 'falseFunc' ) {
            normalValue = cloakFunc(normalValue);
        }
        let aborted = false;
        const mustAbort = function(v) {
            if ( trusted ) { return false; }
            if ( aborted ) { return true; }
            aborted =
                (v !== undefined && v !== null) &&
                (normalValue !== undefined && normalValue !== null) &&
                (typeof v !== typeof normalValue);
            if ( aborted ) {
                safe.uboLog(logPrefix, `Aborted because value set to ${v}`);
            }
            return aborted;
        };
        // https://github.com/uBlockOrigin/uBlock-issues/issues/156
        //   Support multiple trappers for the same property.
        const trapProp = function(owner, prop, configurable, handler) {
            if ( handler.init(configurable ? owner[prop] : normalValue) === false ) { return; }
            const odesc = safe.Object_getOwnPropertyDescriptor(owner, prop);
            let prevGetter, prevSetter;
            if ( odesc instanceof safe.Object ) {
                owner[prop] = normalValue;
                if ( odesc.get instanceof Function ) {
                    prevGetter = odesc.get;
                }
                if ( odesc.set instanceof Function ) {
                    prevSetter = odesc.set;
                }
            }
            try {
                safe.Object_defineProperty(owner, prop, {
                    configurable,
                    get() {
                        if ( prevGetter !== undefined ) {
                            prevGetter();
                        }
                        return handler.getter();
                    },
                    set(a) {
                        if ( prevSetter !== undefined ) {
                            prevSetter(a);
                        }
                        handler.setter(a);
                    }
                });
                safe.uboLog(logPrefix, 'Trap installed');
            } catch(ex) {
                safe.uboErr(logPrefix, ex);
            }
        };
        const trapChain = function(owner, chain) {
            const pos = chain.indexOf('.');
            if ( pos === -1 ) {
                trapProp(owner, chain, false, {
                    v: undefined,
                    init: function(v) {
                        if ( mustAbort(v) ) { return false; }
                        this.v = v;
                        return true;
                    },
                    getter: function() {
                        if ( document.currentScript === thisScript ) {
                            return this.v;
                        }
                        safe.uboLog(logPrefix, 'Property read');
                        return normalValue;
                    },
                    setter: function(a) {
                        if ( mustAbort(a) === false ) { return; }
                        normalValue = a;
                    }
                });
                return;
            }
            const prop = chain.slice(0, pos);
            const v = owner[prop];
            chain = chain.slice(pos + 1);
            if ( v instanceof safe.Object || typeof v === 'object' && v !== null ) {
                trapChain(v, chain);
                return;
            }
            trapProp(owner, prop, true, {
                v: undefined,
                init: function(v) {
                    this.v = v;
                    return true;
                },
                getter: function() {
                    return this.v;
                },
                setter: function(a) {
                    this.v = a;
                    if ( a instanceof safe.Object ) {
                        trapChain(a, chain);
                    }
                }
            });
        };
        trapChain(window, chain);
    }
    runAt(( ) => {
        setConstant(chain, rawValue);
    }, extraArgs.runAt);
}
function validateConstantFn(trusted, raw, extraArgs = {}) {
    const safe = safeSelf();
    let value;
    if ( raw === 'undefined' ) {
        value = undefined;
    } else if ( raw === 'false' ) {
        value = false;
    } else if ( raw === 'true' ) {
        value = true;
    } else if ( raw === 'null' ) {
        value = null;
    } else if ( raw === "''" || raw === '' ) {
        value = '';
    } else if ( raw === '[]' || raw === 'emptyArr' ) {
        value = [];
    } else if ( raw === '{}' || raw === 'emptyObj' ) {
        value = {};
    } else if ( raw === 'noopFunc' ) {
        value = function(){};
    } else if ( raw === 'trueFunc' ) {
        value = function(){ return true; };
    } else if ( raw === 'falseFunc' ) {
        value = function(){ return false; };
    } else if ( raw === 'throwFunc' ) {
        value = function(){ throw ''; };
    } else if ( /^-?\d+$/.test(raw) ) {
        value = parseInt(raw);
        if ( isNaN(raw) ) { return; }
        if ( Math.abs(raw) > 0x7FFF ) { return; }
    } else if ( trusted ) {
        if ( raw.startsWith('json:') ) {
            try { value = safe.JSON_parse(raw.slice(5)); } catch { return; }
        } else if ( raw.startsWith('{') && raw.endsWith('}') ) {
            try { value = safe.JSON_parse(raw).value; } catch { return; }
        }
    } else {
        return;
    }
    if ( extraArgs.as !== undefined ) {
        if ( extraArgs.as === 'function' ) {
            return ( ) => value;
        } else if ( extraArgs.as === 'callback' ) {
            return ( ) => (( ) => value);
        } else if ( extraArgs.as === 'resolved' ) {
            return Promise.resolve(value);
        } else if ( extraArgs.as === 'rejected' ) {
            return Promise.reject(value);
        }
    }
    return value;
}
function setConstant(
    ...args
) {
    setConstantFn(false, ...args);
}
function trustedPreventDomBypass(
    methodPath = '',
    targetProp = ''
) {
    if ( methodPath === '' ) { return; }
    const safe = safeSelf();
    const logPrefix = safe.makeLogPrefix('trusted-prevent-dom-bypass', methodPath, targetProp);
    proxyApplyFn(methodPath, function(context) {
        const elems = new Set(context.callArgs.filter(e => e instanceof HTMLElement));
        const r = context.reflect();
        if ( elems.length === 0 ) { return r; }
        for ( const elem of elems ) {
            try {
                if ( `${elem.contentWindow}` !== '[object Window]' ) { continue; }
                if ( elem.contentWindow.location.href !== 'about:blank' ) {
                    if ( elem.contentWindow.location.href !== self.location.href ) {
                        continue;
                    }
                }
                if ( targetProp !== '' ) {
                    const chains = targetProp.split(/\s+/);
                    let me = self, it = elem.contentWindow;
                    for ( let chain of chains ) {
                        for (;;) {
                            const pos = chain.indexOf('.');
                            if ( pos === -1 ) { break; }
                            const prop = chain.slice(0, pos);
                            me = me[prop]; it = it[prop];
                            chain = chain.slice(pos+1);
                        }
                        it[chain] = me[chain];
                    }
                } else {
                    Object.defineProperty(elem, 'contentWindow', { value: self });
                }
                safe.uboLog(logPrefix, 'Bypass prevented');
            } catch {
            }
        }
        return r;
    });
}
function jsonPruneXhrResponse(
    rawPrunePaths = '',
    rawNeedlePaths = '',
    ...varargs
) {
    const safe = safeSelf();
    const logPrefix = safe.makeLogPrefix('json-prune-xhr-response', rawPrunePaths, rawNeedlePaths);
    const xhrInstances = new WeakMap();
    const extraArgs = safe.parseVarargs(varargs);
    const propNeedles = parsePropertiesToMatchFn(extraArgs.propsToMatch, 'url');
    const stackNeedle = safe.initPattern(extraArgs.stackToMatch || '', { canNegate: true });
    self.XMLHttpRequest = class extends self.XMLHttpRequest {
        open(method, url, ...args) {
            const xhrDetails = { method, url };
            let outcome = 'match';
            if ( propNeedles.size !== 0 ) {
                if ( matchObjectPropertiesFn(propNeedles, xhrDetails) === undefined ) {
                    outcome = 'nomatch';
                }
            }
            if ( outcome === 'match' ) {
                if ( safe.logLevel > 1 ) {
                    safe.uboLog(logPrefix, `Matched optional "propsToMatch", "${extraArgs.propsToMatch}"`);
                }
                xhrInstances.set(this, xhrDetails);
            }
            return super.open(method, url, ...args);
        }
        get response() {
            const innerResponse = super.response;
            const xhrDetails = xhrInstances.get(this);
            if ( xhrDetails === undefined ) {
                return innerResponse;
            }
            const responseLength = typeof innerResponse === 'string'
                ? innerResponse.length
                : undefined;
            if ( xhrDetails.lastResponseLength !== responseLength ) {
                xhrDetails.response = undefined;
                xhrDetails.lastResponseLength = responseLength;
            }
            if ( xhrDetails.response !== undefined ) {
                return xhrDetails.response;
            }
            let objBefore;
            if ( typeof innerResponse === 'object' ) {
                objBefore = innerResponse;
            } else if ( typeof innerResponse === 'string' ) {
                try {
                    objBefore = safe.JSON_parse(innerResponse);
                } catch {
                }
            }
            if ( typeof objBefore !== 'object' ) {
                return (xhrDetails.response = innerResponse);
            }
            const objAfter = objectPruneFn(
                objBefore,
                rawPrunePaths,
                rawNeedlePaths,
                stackNeedle,
                extraArgs
            );
            let outerResponse;
            if ( typeof objAfter === 'object' ) {
                outerResponse = typeof innerResponse === 'string'
                    ? safe.JSON_stringify(objAfter)
                    : objAfter;
                safe.uboLog(logPrefix, 'Pruned');
            } else {
                outerResponse = innerResponse;
            }
            return (xhrDetails.response = outerResponse);
        }
        get responseText() {
            const response = this.response;
            return typeof response !== 'string'
                ? super.responseText
                : response;
        }
    };
}
function adjustSetTimeout(
    needleArg = '',
    delayArg = '',
    boostArg = ''
) {
    if ( typeof needleArg !== 'string' ) { return; }
    const safe = safeSelf();
    const reNeedle = safe.patternToRegex(needleArg);
    let delay = delayArg !== '*' ? parseInt(delayArg, 10) : -1;
    if ( isNaN(delay) || isFinite(delay) === false ) { delay = 1000; }
    let boost = parseFloat(boostArg);
    boost = isNaN(boost) === false && isFinite(boost)
        ? Math.min(Math.max(boost, 0.001), 50)
        : 0.05;
    self.setTimeout = new Proxy(self.setTimeout, {
        apply: function(target, thisArg, args) {
            const [ a, b ] = args;
            if (
                (delay === -1 || b === delay) &&
                reNeedle.test(a.toString())
            ) {
                args[1] = b * boost;
            }
            return target.apply(thisArg, args);
        }
    });
}
try {
removeNodeText("script", "window,\"fetch\"")
} catch ( e ) { }
try {
trustedReplaceXhrResponse("/\"adPlacements.*?(\"adSlots\"|\"adBreakHeartbeatParams\")/gms", "$1", "/\\/player(?:\\?.+)?$/")
} catch ( e ) { }
try {
jsonPrune("entries.[-].command.reelWatchEndpoint.adClientParams.isAd")
} catch ( e ) { }
try {
trustedEditInboundObject("JSON.stringify", "0", "[?.attestationRequest][?.context.client.userAgent=/adunit|channel|lactmilli|instream|inline|yahi|eafg/].playbackContext.contentPlaybackContext.referer=repl({\"regex\":\"(?:#reloadxhr)?$\",\"replacement\":\"#reloadxhr\"})")
} catch ( e ) { }
try {
trustedReplaceFetchResponse("\"adSlots\"", "\"no_ads\"", "/^\\W+$/")
} catch ( e ) { }
try {
jsonPruneFetchResponse("adPlacements adSlots playerResponse.adPlacements playerResponse.adSlots [].playerResponse.adPlacements [].playerResponse.adSlots", "", "propsToMatch", "/\\/player(\\?|$)/")
} catch ( e ) { }
try {
trustedEditInboundObject("JSON.stringify", "0", "[?.attestationRequest][?.context.client.userAgent*=\"instream\"].playbackContext[?.contentPlaybackContext]+={\"adPlaybackContext\":{\"adType\":\"AD_TYPE_INSTREAM\"}}")
} catch ( e ) { }
try {
replaceNodeText("script", "/(\\(function serverContract\\(\\)|^if \\(window\\.ytcsi)/", "(()=>{if(\"YOUTUBE_PREMIUM_LOGO\"===window.ytInitialData?.topbar?.desktopTopbarRenderer?.logo?.topbarLogoRenderer?.iconImage?.iconType||\"YOUTUBE_PREMIUM_LOGO\"===document.getElementById(\"masthead\")?.attributes?.[\"logo-type\"]?.value||location.href.startsWith(\"https://www.youtube.com/tv#/\"))return;const e=window.ytcfg.data_.INNERTUBE_CONTEXT.client.userAgent,t=[\"channel\",\"lactmilli\",\"inline\",\"yahi\"];let r=!1,o=t;const n=t=>{if(t){let r=e?.match?.(/Mozilla\\/5\\.0 \\([^)]+/)?.[0];window.ytcfg.data_.INNERTUBE_CONTEXT.client.userAgent=r?e.replace?.(r,r+\"; \"+t):e}else window.ytcfg.data_.INNERTUBE_CONTEXT.client.userAgent=e},a=()=>{let e=document.getElementById(\"movie_player\");return{player:e,response:e?.getPlayerResponse?.(),stats:e?.getStatsForNerds?.(),progress:e?.getProgressState?.(),buffer:e?.getPlayerStateObject?.()?.isBuffering}},l=(e,t,r)=>e&&\"0.00 s\"===t?.buffer_health_seconds&&\"0x0\"===t?.resolution&&r.length>0,s=()=>{let{player:e,response:t,stats:n,buffer:s}=a();e&&l(s,n,o)&&(t?.playbackTracking?.videostatsPlaybackUrl?.baseUrl?.includes?.(\"reloadxhr\")&&(o=o.slice(1)),r=!0)};document.addEventListener(\"DOMContentLoaded\",(function(){const e=()=>{let e,s,{player:i,response:d,stats:c,progress:p,buffer:y}=a();if(i&&(window.location.href.includes(\"/watch?\")||window.location.href.includes(\"/embed/\"))){if(p&&p.duration>0&&(p.loaded<p.duration||p.duration-p.current>1)||d?.videoDetails?.isLive){if(c&&!c?.debug_info?.startsWith?.(\"SSAP, AD\")){const t=new URLSearchParams(window.location.search),a=d.videoDetails?.videoId,u=d.playerConfig?.playbackStartConfig?.startSeconds||parseInt(t.get(\"t\"),10)||0,g=d.playabilityStatus?.errorScreen,w=JSON.stringify(g?.playerErrorMessageRenderer?.subreason?.runs||g?.playerInterstitialRenderer?.content?.interstitialViewModel?.description?.commandRuns);return void(\"UNPLAYABLE\"===d?.playabilityStatus?.status&&!g?.playerErrorMessageRenderer?.playerCaptchaViewModel&&w?.includes?.(\"WEB_PAGE_TYPE_UNKNOWN\")&&w?.includes?.(\"https://support.google.com/youtube/answer/3037019\")?(o=o.slice(1),o.length>0?n(o[0]):n(\"\"),r=!1,i.loadVideoById(a,u)):0===o.length?(r=!1,n(\"\")):l(y,c,o)&&r?(n(o[0]),r=!1,i.loadVideoById(a,u)):!r&&p.current-u>.1&&p.current-u<5&&location.href.includes(\"&list=\")&&null===i.getPlaylistId?.()&&(e=document.querySelector(\"yt-playlist-manager\"),s=e?.getPlaylistData?.(),s&&(e.setPlaylistData?.(s),e.setPlayerPlaybackControlData?.({playlistPanelRenderer:s}))))}c&&p.duration>0&&i.seekTo?.(p.duration)}}else o=t};e(),new MutationObserver((()=>{e()})).observe(document,{childList:!0,subtree:!0})})),window.Map.prototype.has=new Proxy(window.Map.prototype.has,{apply:(e,t,o)=>(\"onSnackbarMessage\"!==o?.[0]||r||s(),Reflect.apply(e,t,o))}),window.Array.prototype.push=new Proxy(window.Array.prototype.push,{apply:(e,t,r)=>{let o=r?.[0];return!o||\"object\"!=typeof o||\"Uint8Array\"!==o.constructor.name||o.buffer?.byteLength!==o.length||105!==o.length&&104!==o.length||(console.log(o),s()),Reflect.apply(e,t,r)}});const i={apply:(e,t,r)=>{const o=r[0];return\"function\"==typeof o&&o.toString().includes(\"onAbnormalityDetected\")&&(r[0]=function(){}),Reflect.apply(e,t,r)}};window.Promise.prototype.then=new Proxy(window.Promise.prototype.then,i)})();$1", "sedCount", "1", "excludes", "MutationObserver", "condition", "/serverContract|js_ld/")
} catch ( e ) { }
try {
trustedEditInboundObject("JSON.stringify", "0", "[?.attestationRequest][?.context.client.userAgent*=\"channel\"].context.client[?.clientName==\"WEB\"]+={\"clientScreen\":\"CHANNEL\"}")
} catch ( e ) { }
try {
setConstant("ytInitialPlayerResponse.adPlacements", "undefined")
} catch ( e ) { }
try {
trustedReplaceFetchResponse("\"adPlacements\"", "\"no_ads\"", "player?")
} catch ( e ) { }
try {
trustedEditInboundObject("JSON.stringify", "0", "[?.attestationRequest][?.context.client.userAgent*=\"yahi\"]+={\"params\":\"YAHI\"}")
} catch ( e ) { }
try {
setConstant("ytInitialPlayerResponse.playerAds", "undefined")
} catch ( e ) { }
try {
setConstant("ytInitialPlayerResponse.adSlots", "undefined")
} catch ( e ) { }
try {
trustedPreventDomBypass("Node.prototype.appendChild", "fetch")
} catch ( e ) { }
try {
jsonPruneXhrResponse("adPlacements adSlots playerResponse.adPlacements playerResponse.adSlots [].playerResponse.adPlacements [].playerResponse.adSlots", "", "propsToMatch", "/\\/player(?:\\?.+)?$/")
} catch ( e ) { }
try {
trustedReplaceFetchResponse("\"adSlots\"", "\"no_ads\"", "/get_watch?")
} catch ( e ) { }
try {
trustedReplaceFetchResponse("\"adSlots\"", "\"no_ads\"", "player?")
} catch ( e ) { }
try {
trustedEditInboundObject("JSON.stringify", "0", "[?.attestationRequest][?.context.client.userAgent*=\"lactmilli\"]+={\"params\":\"8AUB\"}")
} catch ( e ) { }
try {
trustedReplaceXhrResponse("/\"adPlacements.*?([A-Z]\"\\}|\"\\}{2,4})\\}\\],/", "", "/playlist\\?list=|\\/player(?:\\?.+)?$|watch\\?[tv]=/")
} catch ( e ) { }
try {
trustedEditInboundObject("JSON.stringify", "0", "[?.attestationRequest][?.context.client.userAgent=/channel|lactmilli|instream/].playbackContext.contentPlaybackContext.lactMilliseconds=\"${now}\"")
} catch ( e ) { }
try {
setConstant("playerResponse.adPlacements", "undefined")
} catch ( e ) { }
try {
adjustSetTimeout("[native code]", "17000", "0.001")
} catch ( e ) { }
try {
trustedEditInboundObject("JSON.stringify", "0", "[?.createCommentParams].context.client.userAgent=repl({\"regex\":\"/; (adunit|channel|lactmilli|instream|inline|yahi|eafg)\\b/\",\"replacement\":\"\"})")
} catch ( e ) { }
try {
trustedPreventDomBypass("Node.prototype.appendChild", "Request")
} catch ( e ) { }
try {
jsonPruneFetchResponse("reelWatchSequenceResponse.entries.[-].command.reelWatchEndpoint.adClientParams.isAd entries.[-].command.reelWatchEndpoint.adClientParams.isAd", "", "propsToMatch", "url:/reel_watch_sequence?")
} catch ( e ) { }
try {
jsonPruneFetchResponse("adPlacements adSlots playerResponse.adPlacements playerResponse.adSlots", "", "propsToMatch", "/playlist?")
} catch ( e ) { }

const install=()=>{if(!document.documentElement||document.getElementById('pulse-engine-style'))return;const style=document.createElement('style');style.id='pulse-engine-style';style.textContent=".ytlr-horizontal-list-renderer__items > .yt-virtual-list__container > .yt-virtual-list__item--visible.yt-virtual-list__item--selected.yt-virtual-list__item:has-text(Ad){display:none!important}\n#sticker-layer{display:none!important}\nyt-overlay-product-sticker{display:none!important}\n#toast:has(> #text-container > yt-formatted-string#text.yt-notification-action-renderer){display:none!important}\n.grid.ytd-browse > #primary > .style-scope > .ytd-rich-grid-renderer > .ytd-rich-grid-renderer > .ytd-ad-slot-renderer{display:none!important}\nytd-rich-item-renderer:has(> #content > ytd-ad-slot-renderer){display:none!important}\n#contents > ytd-rich-item-renderer:has(> ytd-ad-slot-renderer){display:none!important}\n#description-inner > ytd-merch-shelf-renderer > #main.ytd-merch-shelf-renderer{display:none!important}\n.ytd-watch-flexy > .ytd-watch-next-secondary-results-renderer > ytd-ad-slot-renderer.ytd-watch-next-secondary-results-renderer{display:none!important}\n.ytd-two-column-browse-results-renderer > ytd-rich-grid-renderer > #masthead-ad.ytd-rich-grid-renderer{display:none!important}\n.ytd-section-list-renderer > .ytd-item-section-renderer > ytd-search-pyv-renderer.ytd-item-section-renderer{display:none!important}\n.ytd-watch-flexy > ytd-merch-shelf-renderer > #main.ytd-merch-shelf-renderer{display:none!important}\n.ytd-rich-item-renderer.style-scope > .ytd-rich-item-renderer > .ytd-ad-slot-renderer.style-scope{display:none!important}\nytd-item-section-renderer > .ytd-item-section-renderer > ytd-ad-slot-renderer.style-scope{display:none!important}\n.ytSnackbarContainerOpened:has(> .snackbarViewModelSwipeable > .snackbarViewModelEngagementBarWrapper .ytAttributedStringWhiteSpacePreWrap[style]){display:none!important}\n.ytp-suggested-action > .ytp-suggested-action-badge{display:none!important}\n#shopping-timely-shelf{display:none!important}\n.ytReelMetapanelViewModelHost > .ytReelMetapanelViewModelMetapanelItem > .ytShortsSuggestedActionViewModelStaticHost{display:none!important}\n#shorts-inner-container > .ytd-shorts:has(> .ytd-reel-video-renderer > ytd-ad-slot-renderer){display:none!important}";document.documentElement.appendChild(style);};if(document.documentElement)install();else document.addEventListener('DOMContentLoaded',install,{once:true});break;}
case "youtube.com":{
function objectPruneFn(
    obj,
    rawPrunePaths,
    rawNeedlePaths,
    stackNeedleDetails = { matchAll: true },
    extraArgs = {}
) {
    if ( typeof rawPrunePaths !== 'string' ) { return; }
    const safe = safeSelf();
    const prunePaths = rawPrunePaths !== ''
        ? safe.String_split.call(rawPrunePaths, / +/)
        : [];
    const needlePaths = prunePaths.length !== 0 && rawNeedlePaths !== ''
        ? safe.String_split.call(rawNeedlePaths, / +/)
        : [];
    if ( stackNeedleDetails.matchAll !== true ) {
        if ( matchesStackTraceFn(stackNeedleDetails, extraArgs.logstack) === false ) {
            return;
        }
    }
    if ( objectPruneFn.mustProcess === undefined ) {
        objectPruneFn.mustProcess = (root, needlePaths) => {
            for ( const needlePath of needlePaths ) {
                if ( objectFindOwnerFn(root, needlePath) === false ) {
                    return false;
                }
            }
            return true;
        };
    }
    if ( prunePaths.length === 0 ) { return; }
    let outcome = 'nomatch';
    if ( objectPruneFn.mustProcess(obj, needlePaths) ) {
        for ( const path of prunePaths ) {
            if ( objectFindOwnerFn(obj, path, true) ) {
                outcome = 'match';
            }
        }
    }
    if ( outcome === 'match' ) { return obj; }
}
function matchesStackTraceFn(
    needleDetails,
    logLevel = ''
) {
    const safe = safeSelf();
    const exceptionToken = getExceptionTokenFn();
    const error = new safe.Error(exceptionToken);
    const docURL = new URL(self.location.href);
    docURL.hash = '';
    // Normalize stack trace
    const reLine = /(.*?@)?(\S+)(:\d+):\d+\)?$/;
    const lines = [];
    for ( let line of safe.String_split.call(error.stack, /[\n\r]+/) ) {
        if ( line.includes(exceptionToken) ) { continue; }
        line = line.trim();
        const match = safe.RegExp_exec.call(reLine, line);
        if ( match === null ) { continue; }
        let url = match[2];
        if ( url.startsWith('(') ) { url = url.slice(1); }
        if ( url === docURL.href ) {
            url = 'inlineScript';
        } else if ( url.startsWith('<anonymous>') ) {
            url = 'injectedScript';
        }
        let fn = match[1] !== undefined
            ? match[1].slice(0, -1)
            : line.slice(0, match.index).trim();
        if ( fn.startsWith('at') ) { fn = fn.slice(2).trim(); }
        let rowcol = match[3];
        lines.push(' ' + `${fn} ${url}${rowcol}:1`.trim());
    }
    lines[0] = `stackDepth:${lines.length-1}`;
    const stack = lines.join('\t');
    const r = needleDetails.matchAll !== true &&
        safe.testPattern(needleDetails, stack);
    if (
        logLevel === 'all' ||
        logLevel === 'match' && r ||
        logLevel === 'nomatch' && !r
    ) {
        safe.uboLog(stack.replace(/\t/g, '\n'));
    }
    return r;
}
function getExceptionTokenFn() {
    const token = getRandomTokenFn();
    const oe = self.onerror;
    self.onerror = function(msg, ...args) {
        if ( typeof msg === 'string' && msg.includes(token) ) { return true; }
        if ( oe instanceof Function ) {
            return oe.call(this, msg, ...args);
        }
    }.bind();
    return token;
}
function getRandomTokenFn() {
    const safe = safeSelf();
    return safe.String_fromCharCode(Date.now() % 26 + 97) +
        safe.Math_floor(safe.Math_random() * 982451653 + 982451653).toString(36);
}
function safeSelf() {
    if ( safeSelf.safe ) {
        return safeSelf.safe;
    }
    const self = globalThis;
    const safe = {
        'Array_from': Array.from,
        'Error': self.Error,
        'Function_toString': Function.prototype.call.bind(self.Function.prototype.toString),
        'Math_floor': Math.floor,
        'Math_max': Math.max,
        'Math_min': Math.min,
        'Math_random': Math.random,
        'Object': Object,
        'Object_defineProperty': Object.defineProperty.bind(Object),
        'Object_defineProperties': Object.defineProperties.bind(Object),
        'Object_fromEntries': Object.fromEntries.bind(Object),
        'Object_getOwnPropertyDescriptor': Object.getOwnPropertyDescriptor.bind(Object),
        'Object_hasOwn': Object.hasOwn.bind(Object),
        'Object_toString': Object.prototype.toString,
        'RegExp': self.RegExp,
        'RegExp_test': Function.prototype.call.bind(self.RegExp.prototype.test),
        'RegExp_exec': self.RegExp.prototype.exec,
        'Request_clone': self.Request.prototype.clone,
        'String': self.String,
        'String_fromCharCode': String.fromCharCode,
        'String_split': String.prototype.split,
        'XMLHttpRequest': self.XMLHttpRequest,
        'addEventListener': self.EventTarget.prototype.addEventListener,
        'removeEventListener': self.EventTarget.prototype.removeEventListener,
        'fetch': self.fetch,
        'JSON': self.JSON,
        'JSON_parse': Function.prototype.call.bind(self.JSON.parse, self.JSON),
        'JSON_stringify': Function.prototype.call.bind(self.JSON.stringify, self.JSON),
        'log': console.log.bind(console),
        // Properties
        logLevel: 0,
        // Methods
        makeLogPrefix(...args) {
            return this.sendToLogger && `[${args.join(' \u205D ')}]` || '';
        },
        uboLog(...args) {
            if ( this.sendToLogger === undefined ) { return; }
            if ( args === undefined || args[0] === '' ) { return; }
            return this.sendToLogger('info', ...args);
            
        },
        uboErr(...args) {
            if ( this.sendToLogger === undefined ) { return; }
            if ( args === undefined || args[0] === '' ) { return; }
            return this.sendToLogger('error', ...args);
        },
        escapeRegexChars(s) {
            return s.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
        },
        initPattern(pattern, options = {}) {
            if ( pattern === '' ) {
                return { matchAll: true, expect: true };
            }
            const expect = (options.canNegate !== true || pattern.startsWith('!') === false);
            if ( expect === false ) {
                pattern = pattern.slice(1);
            }
            const match = /^\/(.+)\/([gimsu]*)$/.exec(pattern);
            if ( match !== null ) {
                return {
                    re: new this.RegExp(
                        match[1],
                        match[2] || options.flags
                    ),
                    expect,
                };
            }
            if ( options.flags !== undefined ) {
                return {
                    re: new this.RegExp(this.escapeRegexChars(pattern),
                        options.flags
                    ),
                    expect,
                };
            }
            return { pattern, expect };
        },
        testPattern(details, haystack) {
            if ( details.matchAll ) { return true; }
            if ( details.re ) {
                return this.RegExp_test(details.re, haystack) === details.expect;
            }
            return haystack.includes(details.pattern) === details.expect;
        },
        patternToRegex(pattern, flags = undefined, verbatim = false) {
            if ( pattern === '' ) { return /^/; }
            const match = /^\/(.+)\/([gimsu]*)$/.exec(pattern);
            if ( match === null ) {
                const reStr = this.escapeRegexChars(pattern);
                return new RegExp(verbatim ? `^${reStr}$` : reStr, flags);
            }
            try {
                return new RegExp(match[1], match[2] || undefined);
            }
            catch {
            }
            return /^/;
        },
        parseVarargs(varargs) {
            const entries = varargs.reduce((out, v, i, a) => {
                if ( i & 1 ) { return out; }
                const rawValue = a[i+1];
                const value = /^\d+$/.test(rawValue)
                    ? parseInt(rawValue, 10)
                    : rawValue;
                out.push([ a[i], value ]);
                return out;
            }, []);
            return this.Object_fromEntries(entries);
        },
    };
    safeSelf.safe = safe;
    if ( scriptletGlobals.bcSecret === undefined ) { return safe; }
    // This is executed only when the logger is opened
    safe.logLevel = scriptletGlobals.logLevel || 1;
    let lastLogType = '';
    let lastLogText = '';
    let lastLogTime = 0;
    safe.toLogText = (type, ...args) => {
        if ( args.length === 0 ) { return; }
        const text = `[${document.location.hostname || document.location.href}]${args.join(' ')}`;
        if ( text === lastLogText && type === lastLogType ) {
            if ( (Date.now() - lastLogTime) < 5000 ) { return; }
        }
        lastLogType = type;
        lastLogText = text;
        lastLogTime = Date.now();
        return text;
    };
    try {
        const bc = new self.BroadcastChannel(scriptletGlobals.bcSecret);
        let bcBuffer = [];
        safe.sendToLogger = (type, ...args) => {
            const text = safe.toLogText(type, ...args);
            if ( text === undefined ) { return; }
            if ( bcBuffer === undefined ) {
                return bc.postMessage({ what: 'messageToLogger', type, text });
            }
            bcBuffer.push({ type, text });
        };
        bc.onmessage = ev => {
            const msg = ev.data;
            switch ( msg ) {
            case 'iamready!':
                if ( bcBuffer === undefined ) { break; }
                bcBuffer.forEach(({ type, text }) =>
                    bc.postMessage({ what: 'messageToLogger', type, text })
                );
                bcBuffer = undefined;
                break;
            case 'setScriptletLogLevelToOne':
                safe.logLevel = 1;
                break;
            case 'setScriptletLogLevelToTwo':
                safe.logLevel = 2;
                break;
            }
        };
        bc.postMessage('areyouready?');
    } catch {
        safe.sendToLogger = (type, ...args) => {
            const text = safe.toLogText(type, ...args);
            if ( text === undefined ) { return; }
            safe.log(`uBO ${text}`);
        };
    }
    return safe;
}
function objectFindOwnerFn(
    root,
    path,
    prune = false
) {
    const safe = safeSelf();
    let owner = root;
    let chain = path;
    for (;;) {
        if ( typeof owner !== 'object' || owner === null  ) { return false; }
        const pos = chain.indexOf('.');
        if ( pos === -1 ) {
            if ( prune === false ) {
                return safe.Object_hasOwn(owner, chain);
            }
            let modified = false;
            if ( chain === '*' ) {
                for ( const key in owner ) {
                    if ( safe.Object_hasOwn(owner, key) === false ) { continue; }
                    delete owner[key];
                    modified = true;
                }
            } else if ( safe.Object_hasOwn(owner, chain) ) {
                delete owner[chain];
                modified = true;
            }
            return modified;
        }
        const prop = chain.slice(0, pos);
        const next = chain.slice(pos + 1);
        let found = false;
        if ( prop === '[-]' && Array.isArray(owner) ) {
            let i = owner.length;
            while ( i-- ) {
                if ( objectFindOwnerFn(owner[i], next) === false ) { continue; }
                owner.splice(i, 1);
                found = true;
            }
            return found;
        }
        if ( prop === '{-}' && owner instanceof Object ) {
            for ( const key of Object.keys(owner) ) {
                if ( objectFindOwnerFn(owner[key], next) === false ) { continue; }
                delete owner[key];
                found = true;
            }
            return found;
        }
        if (
            prop === '[]' && Array.isArray(owner) ||
            prop === '{}' && owner instanceof Object ||
            prop === '*' && owner instanceof Object
        ) {
            for ( const key of Object.keys(owner) ) {
                if (objectFindOwnerFn(owner[key], next, prune) === false ) { continue; }
                found = true;
            }
            return found;
        }
        if ( safe.Object_hasOwn(owner, prop) === false ) { return false; }
        owner = owner[prop];
        chain = chain.slice(pos + 1);
    }
}
function proxyApplyFn(
    target = '',
    handler = '',
    options = {}
) {
    let context = globalThis;
    let prop = target;
    for (;;) {
        const pos = prop.indexOf('.');
        if ( pos === -1 ) { break; }
        context = context[prop.slice(0, pos)];
        if ( context instanceof Object === false ) { return; }
        prop = prop.slice(pos+1);
    }
    const fn = context[prop];
    if ( typeof fn !== 'function' ) { return; }
    if ( proxyApplyFn.CtorContext === undefined ) {
        proxyApplyFn.ctorContexts = [];
        proxyApplyFn.CtorContext = class {
            constructor(...args) {
                this.init(...args);
            }
            init(callFn, callArgs) {
                this.callFn = callFn;
                this.callArgs = callArgs;
                return this;
            }
            reflect() {
                const r = Reflect.construct(this.callFn, this.callArgs);
                this.callFn = this.callArgs = this.private = undefined;
                proxyApplyFn.ctorContexts.push(this);
                return r;
            }
            static factory(...args) {
                return proxyApplyFn.ctorContexts.length !== 0
                    ? proxyApplyFn.ctorContexts.pop().init(...args)
                    : new proxyApplyFn.CtorContext(...args);
            }
        };
        proxyApplyFn.applyContexts = [];
        proxyApplyFn.ApplyContext = class {
            constructor(...args) {
                this.init(...args);
            }
            init(callFn, thisArg, callArgs) {
                this.callFn = callFn;
                this.thisArg = thisArg;
                this.callArgs = callArgs;
                return this;
            }
            reflect() {
                const r = Reflect.apply(this.callFn, this.thisArg, this.callArgs);
                this.callFn = this.thisArg = this.callArgs = this.private = undefined;
                proxyApplyFn.applyContexts.push(this);
                return r;
            }
            static factory(...args) {
                return proxyApplyFn.applyContexts.length !== 0
                    ? proxyApplyFn.applyContexts.pop().init(...args)
                    : new proxyApplyFn.ApplyContext(...args);
            }
        };
        proxyApplyFn.isCtor = new Map();
        proxyApplyFn.proxies = new WeakMap();
        if ( (options.skipToString || proxyApplyFn.skipToString) !== true ) {
            proxyApplyFn.nativeToString = Function.prototype.toString;
            const proxiedToString = new Proxy(Function.prototype.toString, {
                apply(target, thisArg) {
                    let proxied = thisArg;
                    for(;;) {
                        const fn = proxyApplyFn.proxies.get(proxied);
                        if ( fn === undefined ) { break; }
                        proxied = fn;
                    }
                    return proxyApplyFn.nativeToString.call(proxied);
                }
            });
            proxyApplyFn.proxies.set(proxiedToString, proxyApplyFn.nativeToString);
            Function.prototype.toString = proxiedToString;
        }
    }
    if ( proxyApplyFn.isCtor.has(target) === false ) {
        proxyApplyFn.isCtor.set(target, fn.prototype?.constructor === fn);
    }
    const proxyDetails = {
        apply(target, thisArg, args) {
            return handler(proxyApplyFn.ApplyContext.factory(target, thisArg, args));
        }
    };
    if ( proxyApplyFn.isCtor.get(target) ) {
        proxyDetails.construct = function(target, args) {
            return handler(proxyApplyFn.CtorContext.factory(target, args));
        };
    }
    const proxiedTarget = new Proxy(fn, proxyDetails);
    proxyApplyFn.proxies.set(proxiedTarget, fn);
    context[prop] = proxiedTarget;
}
function jsonPrune(
    rawPrunePaths = '',
    rawNeedlePaths = '',
    stackNeedle = '',
    ...varargs
) {
    const safe = safeSelf();
    const logPrefix = safe.makeLogPrefix('json-prune', rawPrunePaths, rawNeedlePaths, stackNeedle);
    const stackNeedleDetails = safe.initPattern(stackNeedle, { canNegate: true });
    const extraArgs = safe.parseVarargs(varargs);
    proxyApplyFn('JSON.parse', function(context) {
        const objBefore = context.reflect();
        if ( rawPrunePaths === '' ) {
            safe.uboLog(logPrefix, safe.JSON_stringify(objBefore, null, 2));
        }
        const objAfter = objectPruneFn(
            objBefore,
            rawPrunePaths,
            rawNeedlePaths,
            stackNeedleDetails,
            extraArgs
        );
        if ( objAfter === undefined ) { return objBefore; }
        safe.uboLog(logPrefix, 'Pruned');
        if ( safe.logLevel > 1 ) {
            safe.uboLog(logPrefix, `After pruning:\n${safe.JSON_stringify(objAfter, null, 2)}`);
        }
        return objAfter;
    });
}
try {
jsonPrune("entries.[-].command.reelWatchEndpoint.adClientParams.isAd")
} catch ( e ) { }

const install=()=>{if(!document.documentElement||document.getElementById('pulse-engine-style'))return;const style=document.createElement('style');style.id='pulse-engine-style';style.textContent="html[lang=\"vi\"] .sspp-area{display:none!important}\napp-large-ad{display:none!important}\ndiv[style^=\"position:fixed;inset:0px;z-index:2147483647;background:black;opacity:0.01\"]{display:none!important}\na[href^=\"http://sneakyadministration.com/\"]{display:none!important}\na[href^=\"https://stardomcoit.com/\"]{display:none!important}\n[href^=\"https://go.mavrtracktor.com/\"]{display:none!important}\nhtml[lang=\"tr\"] #aa-wp .container-ads-adjustment{display:none!important}\na[href^=\"https://clickins.slixa.com/\"]{display:none!important}\ndiv[data-adname]{display:none!important}\na[href^=\"https://combodef.com/\"]{display:none!important}\n[data-testid=\"adBanner-wrapper\"]{display:none!important}\na[href*=\"&maxads=\"]{display:none!important}\n[href^=\"//taghaugh.com/\"]{display:none!important}\n[class^=\"tile-picker__CitrusBannerContainer-sc-\"]{display:none!important}\na[href^=\"https://svb-analytics.trackerrr.com/\"]{display:none!important}\ndiv[id^=\"apn_native_ad_slot_\"]{display:none!important}\ndiv[style^=\"width:    300px; height:    250px;\"]{display:none!important}\nhtml[lang][style^=\"--main-bg-color\"] > body:not([class]):not([id]) > div#container[style=\"visibility: visible;\"] > div#banner[style=\"opacity:0\"] ~ main:not([class]):not([id]) > div#message{display:none!important}\na[href^=\"https://ctosrd.com/\"]{display:none!important}\na[href^=\"https://go.bbrdbr.com\"]{display:none!important}\na[href^=\"https://go.markets.com/visit/?bta=\"]{display:none!important}\n[id^=\"google_ads_iframe_\"]{display:none!important}\n[href^=\"https://wsup.ai/\"]{display:none!important}\n[href^=\"https://fireads.online/\"]{display:none!important}\na[href^=\"https://go.xlivrdr.com\"]{display:none!important}\nytd-rich-item-renderer:has(> #content > ytd-ad-slot-renderer){display:none!important}\nhtml[lang=\"tr\"] #aa-wp .rnd-ads-adjustment{display:none!important}\na[href^=\"https://a.bestcontentweb.top/\"]{display:none!important}\n[href^=\"https://rpwmct.com/\"]{display:none!important}\na[href^=\"https://go.trackitalltheway.com/\"]{display:none!important}\na[href^=\"https://lead1.pl/\"]{display:none!important}\na[href^=\"https://losingoldfry.com/\"]{display:none!important}\ntopadblock{display:none!important}\nyt-overlay-product-sticker{display:none!important}\na[href^=\"https://www.bang.com/?aff=\"]{display:none!important}\nspan[data-ez-ph-id]{display:none!important}\nhtml[lang=\"tr\"] > body.has-footer-ad.has-pageskin-desktop a.pageskin-click-left{display:none!important}\na[href^=\"http://www.iyalc.com/\"]{display:none!important}\na[href^=\"https://italarizege.xyz/\"]{display:none!important}\na[href^=\"https://tm-offers.gamingadult.com/\"]{display:none!important}\n.ytd-section-list-renderer > .ytd-item-section-renderer > ytd-search-pyv-renderer.ytd-item-section-renderer{display:none!important}\na[href^=\"https://www.brazzersnetwork.com/landing/\"]{display:none!important}\na[href^=\"https://go.xlirdr.com\"]{display:none!important}\na[href^=\"https://s.zlinkt.com/\"]{display:none!important}\na[href^=\"https://www.onlineusershielder.com/\"]{display:none!important}\nimg[src^=\"https://s-img.adskeeper.com/\"]{display:none!important}\n[data-testid=\"prism-ad-wrapper\"]{display:none!important}\n[data-css-class=\"dfp-inarticle\"]{display:none!important}\na[href^=\"https://twinrdsrv.com/\"]{display:none!important}\nguj-ad{display:none!important}\na[href^=\"https://go.xtbaffiliates.com/\"]{display:none!important}\na[href*=\"/jump/next.php?r=\"]{display:none!important}\n[href^=\"https://zone.gotrackier.com/\"]{display:none!important}\n[data-lnguri*=\"vipbox\"]{display:none!important}\n[href^=\"https://antiagingbed.com/discount/\"] > img{display:none!important}\na.tdn[href^=\"https://landing.brazzersnetwork.com/\"]{display:none!important}\na[href^=\"https://go.xxxijmp.com\"]{display:none!important}\namp-ad-custom{display:none!important}\n[href=\"https://jdrucker.com/gold\"] > img{display:none!important}\na[href^=\"https://billing.purevpn.com/aff.php\"] > img{display:none!important}\na[href^=\"https://jaxofuna.com/\"]{display:none!important}\na[href^=\"https://track.cam4tracking.com/\"]{display:none!important}\na[href^=\"https://www.get-express-vpn.com/offer/\"]{display:none!important}\n[href^=\"https://www.brighteonstore.com/products/\"] img{display:none!important}\na[href^=\"https://clixtrac.com/\"]{display:none!important}\na[href^=\"http://trk.globwo.online/\"]{display:none!important}\ndiv[data-id-advertdfpconf]{display:none!important}\na[href^=\"https://click.Ggpickaff.com/\"]{display:none!important}\na[href^=\"https://cam4com.go2cloud.org/\"]{display:none!important}\na[href^=\"http://tc.tradetracker.net/\"] > img{display:none!important}\na[href^=\"https://go.etoro.com/\"] > img{display:none!important}\na[href^=\"https://helmethomicidal.com/\"]{display:none!important}\na[id][href=\"https://hamrocsit.com\"][rel^=\"noopener noreferrer\"]{display:none!important}\n[data-uri^=\"https://s3.amazonaws.com\"]{display:none!important}\n[href=\"https://t.me/Russia_Vs_Ukraine_War3\"]{display:none!important}\na[id][href=\"https://toolkitspro.com\"][rel^=\"noopener noreferrer\"]{display:none!important}\na[href^=\"https://syndication.dynsrvtbg.com/\"]{display:none!important}\n[data-template-type=\"nativead\"]{display:none!important}\na[href*=\"//jjgirls.com/sex/Chaturbate\"]{display:none!important}\na[href^=\"https://upsups.click/\"]{display:none!important}\ndiv[data-ad-placeholder]{display:none!important}\n[href^=\"https://affiliate.fastcomet.com/\"] > img{display:none!important}\na[href^=\"https://tracker.loropartners.com/\"]{display:none!important}\na[href^=\"https://join.virtuallust3d.com/\"]{display:none!important}\na[href^=\"https://trk.softonixs.xyz/\"]{display:none!important}\na[href^=\"https://www.highperformancecpmgate.com/\"]{display:none!important}\namp-img[height=\"   120\"]{display:none!important}\na[href^=\"https://black77854.com/\"]{display:none!important}\na[href^=\"https://static.fleshlight.com/images/banners/\"]{display:none!important}\ndiv[data-dfp-id]{display:none!important}\na[href^=\"https://financeads.net/tc.php?\"]{display:none!important}\namp-img[width=\"   336\"]{display:none!important}\nzeus-ad{display:none!important}\n[href^=\"//producebreed.com/\"]{display:none!important}\n#description-inner > ytd-merch-shelf-renderer > #main.ytd-merch-shelf-renderer{display:none!important}\ndiv[style^=\"z-index: 999999; background-image: url(\\\"data:image/gif;base64,\"][style$=\"position: absolute;\"]{display:none!important}\n[data-id^=\"div-gpt-ad\"]{display:none!important}\n[id^=\"ad_slider\"]{display:none!important}\na[id][href=\"https://chpadblock.com/\"][rel^=\"noopener noreferrer\"]{display:none!important}\na[href^=\"https://www.highcpmrevenuenetwork.com/\"]{display:none!important}\n[data-desktop-ad-id]{display:none!important}\ndiv[aria-label=\"Ads\"]{display:none!important}\n.ytReelMetapanelViewModelHost > .ytReelMetapanelViewModelMetapanelItem > .ytShortsSuggestedActionViewModelStaticHost{display:none!important}\naside[id^=\"adrotate_widgets-\"]{display:none!important}\n[href^=\"https://mystore.com/\"] > img{display:none!important}\ndiv[class^=\"Adstyled__AdWrapper-\"]{display:none!important}\n.grid.ytd-browse > #primary > .style-scope > .ytd-rich-grid-renderer > .ytd-rich-grid-renderer > .ytd-ad-slot-renderer{display:none!important}\n[href^=\"https://awbbjmp.com/\"]{display:none!important}\na[href^=\"https://datewhisper.life/\"]{display:none!important}\n[href^=\"https://www.herbanomic.com/\"] > img{display:none!important}\namp-embed[type=\"taboola\"]{display:none!important}\na[href*=\".adglare.net/\"]{display:none!important}\na[href^=\"https://www.effectiveratecpm.com/\"]{display:none!important}\nin-page-message[doskip]{display:none!important}\na[href*=\".g2afse.com/\"]{display:none!important}\na[href^=\"https://lijavaxa.com/\"]{display:none!important}\na[href^=\"https://go.cmtaffiliates.com/\"]{display:none!important}\na[href^=\"https://www.adskeeper.com\"]{display:none!important}\ndiv[id^=\"div-gpt-\"]:not([style^=\"width: 1px; height: 1px; position: absolute; left: -10000px; top: -\"]):not([style=\"pointer-events: none; height: 1px; width: 0px; opacity: 0; visibility: hidden; position: fixed; bottom: 0px;\"]){display:none!important}\nspan[id^=\"ezoic-pub-ad-placeholder-\"]{display:none!important}\n.ytd-watch-flexy > .ytd-watch-next-secondary-results-renderer > ytd-ad-slot-renderer.ytd-watch-next-secondary-results-renderer{display:none!important}\na[href^=\"https://join.bannedsextapes.com/track/\"]{display:none!important}\na[href^=\"https://www.privateinternetaccess.com/\"] > img{display:none!important}\na[href^=\"https://go.strpjmp.com/\"]{display:none!important}\n[data-ad-cls]{display:none!important}\n[id^=\"ad_sky\"]{display:none!important}\na[href^=\"https://a.candyai.love/\"]{display:none!important}\na[href^=\"https://go.hpyrdr.com/\"]{display:none!important}\ndiv[id^=\"rc-widget-\"]{display:none!important}\n[data-identity=\"adhesive-ad\"]{display:none!important}\na[href^=\"https://a.medfoodhome.com/\"]{display:none!important}\n[href=\"https://clickaine.com\"]{display:none!important}\n[href^=\"https://groorsoa.net/\"]{display:none!important}\n[href^=\"https://track.aftrk1.com/\"]{display:none!important}\na[href^=\"http://www.poweredbyliquidfire.mobi/\"]{display:none!important}\na[href^=\"https://activate-game.com/\"]{display:none!important}\na[href^=\"https://s.zlink3.com/\"]{display:none!important}\na[href^=\"https://www.dating-finder.com/?ai_d=\"]{display:none!important}\n[href^=\"//mellowads.com/\"]{display:none!important}\na[href^=\"http://com-1.pro/\"]{display:none!important}\na[href*=\"/go.php?a_aid=\"]{display:none!important}\ndiv[style^=\"height:    250px; width:    300px;\"]{display:none!important}\na[href^=\"https://fastestvpn.com/lifetime-special-deal?a_aid=\"]{display:none!important}\nins.adsbygoogle[data-ad-client]{display:none!important}\na[href^=\"https://ndt5.net/\"]{display:none!important}\na[href^=\"https://a.bestcontentoperation.top/\"]{display:none!important}\na[href^=\"https://t.ajrkm3.com/\"]{display:none!important}\n[class^=\"div-gpt-ad\"]:not([style^=\"width: 1px; height: 1px; position: absolute; left: -10000px; top: -\"]){display:none!important}\na[href^=\"https://ads.planetwin365affiliate.com/\"]{display:none!important}\na[href^=\"https://join.sexworld3d.com/track/\"]{display:none!important}\nDFP-AD{display:none!important}\n[data-ez-name]{display:none!important}\na[href^=\"https://slkmis.com/\"]{display:none!important}\na-ad{display:none!important}\na[href^=\"https://click.candyoffers.com/\"]{display:none!important}\na[href^=\"https://go.rmhfrtnd.com\"]{display:none!important}\n[data-lnguri^=\"https://s3.amazonaws.com\"]{display:none!important}\na[href^=\"https://golinks.work/\"]{display:none!important}\n[href^=\"https://www.safestcontentgate.com/\"]{display:none!important}\na[href^=\"https://lnkxt.bannerator.com/\"]{display:none!important}\na[href^=\"http://sarcasmadvisor.com/\"]{display:none!important}\na[href^=\"https://go.xxxvjmp.com/\"]{display:none!important}\na[href^=\"https://clicks.pipaffiliates.com/\"]{display:none!important}\na[href^=\"https://ausoafab.net/\"]{display:none!important}\n[href^=\"https://clicks.affstrack.com/\"] > img{display:none!important}\na[href^=\"https://go.rmishe.com/\"]{display:none!important}\ndisplay-ads{display:none!important}\na[href^=\"https://trk.nfl-online-streams.club/\"]{display:none!important}\na[href^=\"https://adclick.g.doubleclick.net/\"]{display:none!important}\ndiv[id^=\"dfp-ad-\"]{display:none!important}\n[href=\"//sexcams.plus/\"]{display:none!important}\nhtml[lang=\"tr\"] #aa-wp + .fixed-footer .footer-ad-container{display:none!important}\na[href^=\"https://go.admjmp.com\"]{display:none!important}\nimg[height=\"   90\"][width=\"   728\"]{display:none!important}\na[href^=\"https://www.nutaku.net/signup/landing/\"]{display:none!important}\nbottomadblock{display:none!important}\na[href^=\"https://s.gentlefieldpattern.com/\"]{display:none!important}\na[href^=\"https://osfultrbriolenai.info/\"]{display:none!important}\ndiv[id][style^=\"position: fixed; inset: 0px; z-index: 2147483647; background: black\"][style*=\"opacity: 0.01\"]{display:none!important}\na[href^=\"https://paid.outbrain.com/network/redir?\"]{display:none!important}\n[href^=\"https://go.smljmp.com/\"]{display:none!important}\na[href^=\"http://cam4com.go2cloud.org/aff_c?\"]{display:none!important}\n[class^=\"amp-ad-\"]{display:none!important}\ndiv[id^=\"optidigital-adslot\"]{display:none!important}\na[href^=\"//s.zlinkd.com/\"]{display:none!important}\na[href^=\"https://1betandgonow.com/\"]{display:none!important}\na[href^=\"https://go.xlviirdr.com\"]{display:none!important}\n[href^=\"https://popcash.net/\"]{display:none!important}\n[href^=\"https://join.girlsoutwest.com/\"]{display:none!important}\nAMP-AD{display:none!important}\ndiv[class$=\"player-promo-col\"]{display:none!important}\n[href^=\"https://ad1.adfarm1.adition.com/\"]{display:none!important}\na[href^=\"https://twinrdsyte.com/\"]{display:none!important}\na[href^=\"https://syndicate.contentsserved.com/\"]{display:none!important}\n[data-ad-width]:not([style$=\"left: -10000px !important; top: -1000px !important;\"]):not(.adsbygoogle){display:none!important}\na[href^=\"https://track.afcpatrk.com/\"]{display:none!important}\ndiv[class$=\"-adlabel\"]{display:none!important}\nbody > #mdl-register.modal.anm-e.regnow{display:none!important}\ndiv[data-contentexchange-widget]{display:none!important}\ndiv[id^=\"google_dfp_\"]{display:none!important}\n[href^=\"https://ilovemyfreedoms.com/landing-\"]{display:none!important}\na[href^=\"https://track.aftrk5.com/\"]{display:none!important}\na[href^=\"https://www.mrskin.com/account/\"]{display:none!important}\na[href^=\"https://postback1win.com/\"]{display:none!important}\nbody > div[style=\"position: fixed; inset: 0px; z-index: 2147483647; background: transparent; cursor: pointer;\"]{display:none!important}\n[data-ad-width]{display:none!important}\n#contents > ytd-rich-item-renderer:has(> ytd-ad-slot-renderer){display:none!important}\na[href^=\"https://go.xxxiijmp.com\"]{display:none!important}\na[href^=\"https://gamingadlt.com/?offer=\"]{display:none!important}\na[href^=\"https://pb-front.com/\"]{display:none!important}\n.ytlr-horizontal-list-renderer__items > .yt-virtual-list__container > .yt-virtual-list__item--visible.yt-virtual-list__item--selected.yt-virtual-list__item:has-text(Ad){display:none!important}\n[href^=\"//look.utndln.com/offer\"]{display:none!important}\na[href^=\"https://voluum.prom-xcams.com/\"]{display:none!important}\n[data-rc-widget]{display:none!important}\na[href^=\"https://explore-site.com/\"]{display:none!important}\n[id^=\"section-ad-banner\"]{display:none!important}\nbody > a.visible[id][href][target=\"_blank\"]{display:none!important}\n[href^=\"https://istlnkcl.com/\"]{display:none!important}\ndiv[id^=\"adspot-\"]{display:none!important}\ndiv[style^=\"width:    160px; height:    600px; margin: 0px auto; min-width: 160px; min-height: 600px;\"]{display:none!important}\na[href^=\"https://traffdaq.com/\"]{display:none!important}\n[data-testid^=\"section-AdRowBillboard\"]{display:none!important}\n[href^=\"https://ap.octopuspop.com/click/\"] > img{display:none!important}\na[href*=\".click/?z=\"][href*=\"&n\"]{display:none!important}\na[href^=\"https://1winpb.com/\"]{display:none!important}\na[href^=\"https://go.bushheel.com/\"]{display:none!important}\n[href^=\"https://join.playboyplus.com/track/\"]{display:none!important}\n[href^=\"//cadsecs.com/\"]{display:none!important}\n[href^=\"https://track.wg-aff.com/click\"]{display:none!important}\na[href^=\"https://ads.leovegas.com/\"]{display:none!important}\na[href^=\"https://loboclick.com/\"]{display:none!important}\ndiv#spot-holder.spot-holder[style=\"display: block;\"]{display:none!important}\ndiv[id^=\"div-ads-\"]{display:none!important}\n[data-dynamic-ads]{display:none!important}\n[data-taboola-options]{display:none!important}\na[href^=\"https://aj1070.online/\"]{display:none!important}\na[href*=\".foxqck.com/\"]{display:none!important}\n[href^=\"https://track.fiverr.com/visit/\"] > img{display:none!important}\na[href^=\"https://clickadilla.com/\"]{display:none!important}\n[data-asg-ins]{display:none!important}\n[href^=\"http://clicks.totemcash.com/\"]{display:none!important}\n[href^=\"https://www.restoro.com/\"]{display:none!important}\na[href^=\"https://bc.game/\"]{display:none!important}\na[href^=\"https://track.wg-aff.com\"]{display:none!important}\na[href^=\"https://s.zlinkd.com/\"]{display:none!important}\nbutton.btn[data-openuri*=\"/4/\"]{display:none!important}\na[href^=\"https://Click.ggpickaff.com/\"]{display:none!important}\na[href^=\"https://go.xlvirdr.com\"]{display:none!important}\na[href^=\"https://tracking.avapartner.com/\"]{display:none!important}\ncitrus-ad-wrapper{display:none!important}\n[src^=\"//dombnrs.com/\"]{display:none!important}\ndiv[id^=\"vuukle-ad-\"]{display:none!important}\na[href^=\"https://www.infowarsstore.com/\"] > img{display:none!important}\nhtml[lang=\"tr\"] > body.has-footer-ad.has-pageskin-desktop a.pageskin-click-right{display:none!important}\na[data-redirect^=\"https://paid.outbrain.com/network/redir?\"]{display:none!important}\n[name^=\"google_ads_iframe\"]{display:none!important}\na[href^=\"https://www.liquidfire.mobi/\"]{display:none!important}\n[data-ad-module]:not([style$=\"left: -10000px !important; top: -1000px !important;\"]):not(.adsbygoogle){display:none!important}\na[href^=\"https://prf.hn/click/\"][href*=\"/adref:\"] > img{display:none!important}\namp-img[width=\"   300\"]{display:none!important}\na[href^=\"https://www.geekbuying.com/dynamic-ads/\"]{display:none!important}\na[href^=\"https://chaturbate.com/in/?\"]{display:none!important}\na[href^=\"https://go.grinsbest.com/\"]{display:none!important}\nins[id][style*=\"--gn-ov-ad-height\"]{display:none!important}\na[href^=\"https://wolf-327b.com/\"]{display:none!important}\n[href^=\"https://join3.bannedsextapes.com\"]{display:none!important}\na[href^=\"https://click.dtiserv2.com/\"]{display:none!important}\na[href^=\"https://syndication.exdynsrv.com/splash.php\"]{display:none!important}\na[href^=\"https://bodelen.com/\"]{display:none!important}\ndiv[id^=\"adrotate_widgets-\"]{display:none!important}\na[href^=\"https://gml-grp.com/\"]{display:none!important}\na[href^=\"https://snowdayonline.xyz/\"]{display:none!important}\n[href^=\"https://mylead.global/stl/\"] > img{display:none!important}\ndisplay-ad-component{display:none!important}\na[href^=\"https://go.xxxjmp.com\"]{display:none!important}\na[href^=\"https://service.bv-aff-trx.com/\"]{display:none!important}\ndiv[id^=\"div-gpt-\"]{display:none!important}\ndiv[id*=\"MarketGid\"]{display:none!important}\na[href^=\"https://prf.hn/click/\"][href*=\"/camref:\"] > img{display:none!important}\na[href^=\"https://offhandpump.com/\"]{display:none!important}\ndiv[id^=\"mrec-leaderboard-\"]{display:none!important}\na[href^=\"https://iqbroker.com/\"][href*=\"?aff=\"]{display:none!important}\n[href*=\"uselnk.com/\"]{display:none!important}\n[data-cl-spot-id]{display:none!important}\n[data-freestar-ad][id]{display:none!important}\n[href^=\"https://join.enjoyx.com/track/\"]{display:none!important}\na[href^=\"https://www.adultempire.com/\"][href*=\"?partner_id=\"]{display:none!important}\n[href^=\"https://www.hostg.xyz/\"] > img{display:none!important}\natf-ad-slot{display:none!important}\nhtml[lang=\"tr\"] > body.has-footer-ad.has-pageskin-desktop a.pageskin-desktop-wrapper{display:none!important}\n[data-ad-name]{display:none!important}\n[class^=\"div-gpt-ad\"]{display:none!important}\n[style^=\"position: fixed; inset: 0px; z-index: 2147483647; background-color: transparent; pointer-events: auto; cursor: auto; margin: 0px; padding: 0px; display: block;\"]{display:none!important}\n[href^=\"https://wap4dollar.com/ad/nonadult/serve.php\"]{display:none!important}\na[href^=\"https://go.hpyjmp.com\"]{display:none!important}\n[href^=\"https://www.mypillow.com/\"] > img{display:none!important}\na[href^=\"https://bngprm.com/\"]{display:none!important}\na[href^=\"https://bongacams2.com/track?\"]{display:none!important}\nimg[height=\"   250\"][width=\"   300\"]{display:none!important}\na[href^=\"https://quotationfirearmrevision.com/\"]{display:none!important}\na[href*=\".adsrv.eacdn.com/\"]{display:none!important}\na[href^=\"https://go.skinstrip.net\"][href*=\"?campaignId=\"]{display:none!important}\na[href^=\"https://aweptjmp.com/\"]{display:none!important}\na[href^=\"https://t.adating.link/\"]{display:none!important}\nimg[src^=\"https://images.purevpnaffiliates.com\"]{display:none!important}\na[href^=\"https://www.toprevenuegate.com/\"]{display:none!important}\ndiv[id^=\"gpt_ad_\"]{display:none!important}\na[href^=\"https://123-stream.org/\"]{display:none!important}\n[data-m-ad-id]{display:none!important}\n[href^=\"https://www.avantlink.com/click.php\"] img{display:none!important}\na[href^=\"https://t.acam.link/\"]{display:none!important}\na[href^=\"http://bongacams.com/track?\"]{display:none!important}\na[href^=\"https://prf.hn/click/\"][href*=\"/creativeref:\"] > img{display:none!important}\na[href^=\"https://track.totalav.com/\"]{display:none!important}\niframe.lazyloaded[data-src^=\"https://rcm-fe.amazon-adsystem.com/\"]{display:none!important}\na[href^=\"https://banners.livepartners.com/\"]{display:none!important}\na[href^=\"http://www.friendlyduck.com/AF_\"]{display:none!important}\n[href^=\"https://www.onclickperformance.com/\"]{display:none!important}\ndiv[style=\"position: fixed; display: block; width: 100%; height: 100%; inset: 0px; background-color: rgba(0, 0, 0, 0); z-index: 300000;\"]{display:none!important}\nfigure.movie-detail-banner div[style=\"position: absolute; top: 0px; left: 0px; width: 100%; height: 100%; z-index: 999; cursor: pointer;\"]{display:none!important}\na[href^=\"https://torguard.net/aff.php\"] > img{display:none!important}\n[href=\"https://snvhost.com/\"]{display:none!important}\na[href^=\"https://www.dating-finder.com/signup/?ai_d=\"]{display:none!important}\ndiv[id^=\"st\"][style^=\"z-index: 999999999;\"]{display:none!important}\nAD-SLOT{display:none!important}\na[href^=\"https://www.sheetmusicplus.com/\"][href*=\"?aff_id=\"]{display:none!important}\nad-shield-ads{display:none!important}\naside[aria-label=\"retailmedia.complimentarySponsored\"]{display:none!important}\n[id^=\"ad-wrap-\"]{display:none!important}\ndiv[style^=\"position: fixed; inset: 0px; z-index: 2147483647; background: black; opacity: 0.01\"]{display:none!important}\na[href^=\"https://t.ajump1.com/\"]{display:none!important}\n[data-testid=\"ad_testID\"]{display:none!important}\n[href^=\"https://www.cloudways.com/en/?id\"]{display:none!important}\ndiv[id^=\"ezoic-pub-ad-\"]{display:none!important}\na[href^=\"https://bongacams10.com/track?\"]{display:none!important}\na[href^=\"https://getmatchedlocally.com/\"]{display:none!important}\n[data-testid=\"commercial-label-taboola\"]{display:none!important}\na[href^=\"https://www.googleadservices.com/pagead/aclk?\"] > img{display:none!important}\n[href^=\"https://go.rdrjmp.com/\"]{display:none!important}\n[href^=\"https://url.totaladblock.com/\"]{display:none!important}\na[onmousedown^=\"this.href='https://paid.outbrain.com/network/redir?\"]{display:none!important}\nad-slot{display:none!important}\na[href*=\".cfm?domain=\"][href*=\"&fp=\"]{display:none!important}\na[href^=\"http://adultfriendfinder.com/go/\"]{display:none!important}\na[href^=\"https://www.bet365.com/\"][href*=\"affiliate=\"]{display:none!important}\ndiv[data-ad-wrapper]{display:none!important}\ndiv[id^=\"sticky_ad_\"]{display:none!important}\n[class^=\"adDisplay-module\"]{display:none!important}\nhtml[lang=\"tr\"] > body.has-footer-ad.has-pageskin-desktop a.pageskin-mobile-wrapper{display:none!important}\na[href^=\"https://a-ads.com/\"]{display:none!important}\n[data-ad-module]{display:none!important}\na[href^=\"https://believessway.com/\"]{display:none!important}\na[href^=\"https://hot-growngames.life/\"]{display:none!important}\na[href^=\"//hoodingluster.com/\"]{display:none!important}\na[href^=\"https://s.happyleafmotion.com/\"]{display:none!important}\na[href^=\"https://a.adtng.com/\"]{display:none!important}\na[href^=\"https://go.xlviiirdr.com\"]{display:none!important}\ndiv[id^=\"lazyad-\"]{display:none!important}\n[href^=\"https://adult.xyz/\"]{display:none!important}\n[onclick*=\"content.ad/\"]{display:none!important}\ndiv[style=\"top: 0px; left: 0px; width: 1287px; height: 500px; position: fixed; z-index: 2147483647;\"]{display:none!important}\ndiv[id^=\"ad-position-\"]{display:none!important}\ndiv[style=\"position: fixed; inset: 0px; z-index: 2147483647; pointer-events: auto;\"]{display:none!important}\n[data-block-type=\"ad\"]{display:none!important}\na[href^=\"https://in.rabbtrk.com/\"]{display:none!important}\na[href^=\"https://www.polybuzz.ai/\"] img.responsive-image{display:none!important}\n[data-ad-manager-id]:not([style$=\"left: -10000px !important; top: -1000px !important;\"]):not(.adsbygoogle){display:none!important}\ndiv[data-alias=\"300x250 Ad 2\"]{display:none!important}\ndiv[id^=\"taboola-stream-\"]{display:none!important}\n[href^=\"https://optimizedelite.com/\"] > img{display:none!important}\na[href*=\".sadbaguette.com/\"]{display:none!important}\na[href^=\"https://join.virtualtaboo.com/track/\"]{display:none!important}\na[href^=\"https://track.1234sd123.com/\"]{display:none!important}\n[href=\"//xxxrevpushclcdu.com/app.webp\"]{display:none!important}\na[style=\"width:100%;height:100%;z-index:10000000000000000;position:absolute;top:0;left:0;\"]{display:none!important}\na[href^=\"https://www.mrskin.com/tour\"]{display:none!important}\n[href^=\"https://go.xlrdr.com\"]{display:none!important}\n[href*=\"passtechusa.com\"]{display:none!important}\n[href^=\"http://referrer.website/\"]{display:none!important}\nins.adsbygoogle[data-ad-slot]{display:none!important}\na[href^=\"https://www8.smartadserver.com/\"]{display:none!important}\na[href^=\"https://www.purevpn.com/\"][href*=\"&utm_source=aff-\"]{display:none!important}\n[data-testid^=\"taboola-\"]{display:none!important}\na[href^=\"https://a2.adform.net/\"]{display:none!important}\nbody > div[style$=\"z-index: 2147483647; top: 0px; left: 0px; position: fixed; display: block;\"]{display:none!important}\na[href^=\"https://get-link.xyz/\"]{display:none!important}\na[href^=\"https://tc.tradetracker.net/\"] > img{display:none!important}\na[href^=\"https://awptjmp.com/\"]{display:none!important}\na[onclick=\"openAuc();\"]{display:none!important}\ndiv[id^=\"zergnet-widget\"]{display:none!important}\n#shorts-inner-container > .ytd-shorts:has(> .ytd-reel-video-renderer > ytd-ad-slot-renderer){display:none!important}\nhl-adsense{display:none!important}\n[href^=\"https://t.mobtyb.com/\"]{display:none!important}\na[href^=\"https://www.dql2clk.com/\"]{display:none!important}\na[href^=\"//go.eabids.com/\"]{display:none!important}\na[href^=\"https://ab.advertiserurl.com/aff/\"]{display:none!important}\n[href^=\"https://exi8ef83z9.com/\"]{display:none!important}\na[href^=\"https://www.friendlyduck.com/AF_\"]{display:none!important}\ndiv[data-ad-region]{display:none!important}\na[data-oburl^=\"https://paid.outbrain.com/network/redir?\"]{display:none!important}\ndiv[data-adunit]{display:none!important}\na[href^=\"https://claring-loccelkin.com/\"]{display:none!important}\na[href^=\"http://stickingrepute.com/\"]{display:none!important}\na[href^=\"https://ismlks.com/\"]{display:none!important}\na[href^=\"https://fc.lc/ref/\"]{display:none!important}\na[href^=\"http://revolvemockerycopper.com/\"]{display:none!important}\na[href^=\"https://landing.brazzersnetwork.com/\"]{display:none!important}\na[href^=\"https://mediaserver.entainpartners.com/renderBanner.do?\"]{display:none!important}\na[href^=\"https://t.ajrkm1.com/\"]{display:none!important}\ndiv[data-adunit-path]{display:none!important}\na[href^=\"//startgaming.net/tienda/\" i]{display:none!important}\n[href^=\"http://www.mypillow.com/\"] > img{display:none!important}\na[href^=\"https://pubads.g.doubleclick.net/\"]{display:none!important}\ndiv[data-alias=\"300x250 Ad 1\"]{display:none!important}\na[href^=\"https://ctjdwm.com/\"]{display:none!important}\na[href^=\"http://partners.etoro.com/\"]{display:none!important}\na[href^=\"https://syndication.exoclick.com/\"]{display:none!important}\ndiv[ow-ad-unit-wrapper]{display:none!important}\na[href^=\"https://t.aslnk.link/\"]{display:none!important}\namp-fx-flying-carpet{display:none!important}\na[href^=\"https://track.adform.net/\"]{display:none!important}\niframe[scrolling=\"no\"][sandbox*=\"allow-popups allow-modals\"][style^=\"width: 100%; height: 100%; border: none;\"]{display:none!important}\na[href^=\"https://www.goldenfrog.com/vyprvpn?offer_id=\"][href*=\"&aff_id=\"]{display:none!important}\na[href^=\"https://tatrck.com/\"]{display:none!important}\namp-connatix-player{display:none!important}\na[href^=\"https://go.goaserv.com/\"]{display:none!important}\na[href^=\"https://ad.doubleclick.net/\"]{display:none!important}\n#sticker-layer{display:none!important}\n[href^=\"https://www.profitablegatecpm.com/\"]{display:none!important}\namp-ad{display:none!important}\na[href^=\"https://engine.phn.doublepimp.com/\"]{display:none!important}\na[onmousedown^=\"this.href='https://paid.outbrain.com/network/redir?\"] + .ob_source{display:none!important}\napp-ad{display:none!important}\na[href^=\"https://adultfriendfinder.com/go/\"]{display:none!important}\na[href^=\"https://dl-protect.net/\"]{display:none!important}\na[href^=\"https://random-affiliate.atimaze.com/\"]{display:none!important}\na[href^=\"https://camfapr.com/landing/click/\"]{display:none!important}\na[href^=\"https://go.awdeliverynet.com/\"]{display:none!important}\n[href^=\"https://charmingdatings.life/\"]{display:none!important}\na[href^=\"https://go.tmrjmp.com\"]{display:none!important}\na[href^=\"https://stellarcashflow.net/trck/\"]{display:none!important}\n.ytd-rich-item-renderer.style-scope > .ytd-rich-item-renderer > .ytd-ad-slot-renderer.style-scope{display:none!important}\na[href^=\"https://pb-track.com/\"]{display:none!important}\ndiv[data-ad-targeting]{display:none!important}\n[href*=\"/afu.php\"]{display:none!important}\na[href^=\"http://www.adultempire.com/unlimited/promo?\"][href*=\"&partner_id=\"]{display:none!important}\n[href=\"https://chaturbate.jjgirls.com/\"] > img{display:none!important}\na[href^=\"https://ctrdwm.com/\"]{display:none!important}\na[href^=\"https://track.aftrk3.com/\"]{display:none!important}\na[href^=\"https://best-experience-cool.com/\"]{display:none!important}\na.poly-title[href^=\"https://www.polybuzz.ai/\"]{display:none!important}\n[data-mobile-ad-id]{display:none!important}\n[href^=\"https://rapidgator.net/article/premium/ref/\"]{display:none!important}\n[data-revive-zoneid]{display:none!important}\na[href^=\"https://juicyads.in/\"]{display:none!important}\na[href^=\"https://mmwebhandler.aff-online.com/\"]{display:none!important}\na[href^=\"https://serve.awmdelivery.com/\"]{display:none!important}\n.ytp-suggested-action > .ytp-suggested-action-badge{display:none!important}\na[href^=\"https://ak.psaltauw.net/\"]{display:none!important}\na[href^=\"https://click.linksynergy.com/fs-bin/\"] > img{display:none!important}\n[href^=\"https://a.acebet.com/api/click?\"]{display:none!important}\na[href^=\"https://get.surfshark.net/aff_c?\"][href*=\"&aff_id=\"] > img{display:none!important}\na[href^=\"https://wmctjd.com/\"]{display:none!important}\na[href^=\"https://pb-imc.com/\"]{display:none!important}\n[id^=\"div-gpt-ad\"]:not([style^=\"width: 1px; height: 1px; position: absolute; left: -10000px; top: -\"]):not([style=\"pointer-events: none; height: 1px; width: 0px; opacity: 0; visibility: hidden; position: fixed; bottom: 0px;\"]){display:none!important}\n[href^=\"https://www.targetingpartner.com/\"]{display:none!important}\na[href^=\"https://s.zlinkn.com/\"]{display:none!important}\nbroadstreet-zone-container{display:none!important}\ndiv[id][class][style^=\"display\"][style*=\"hidden\"][style*=\"width:   \"]{display:none!important}\na[href^=\"https://www.financeads.net/tc.php?\"]{display:none!important}\na[href^=\"https://wittered-mainging.com/\"]{display:none!important}\n[href^=\"https://mypillow.com/\"] > img{display:none!important}\ndiv[id^=\"ad_position_\"]{display:none!important}\n.ytd-watch-flexy > ytd-merch-shelf-renderer > #main.ytd-merch-shelf-renderer{display:none!important}\na[href^=\"https://playnano.online/offerwalls/?ref=\"]{display:none!important}\nins[style$=\"height:    90px; width:    728px;\"]{display:none!important}\n[href^=\"https://aads.com/campaigns/\"]{display:none!important}\na[href^=\"https://rixofa.com/\"]{display:none!important}\na[href^=\"https://streamate.com/landing/click/\"]{display:none!important}\n.ytd-two-column-browse-results-renderer > ytd-rich-grid-renderer > #masthead-ad.ytd-rich-grid-renderer{display:none!important}\na[dontfo=\"\"][style$=\"position: absolute; z-index: 2147483647;\"]{display:none!important}\ndiv[id^=\"ad-div-\"]{display:none!important}\n[href^=\"https://zstacklife.com/\"] img{display:none!important}\na[href^=\"https://natour.naughtyamerica.com/track/\"]{display:none!important}\na[href^=\"https://ads.betfair.com/redirect.aspx?\"]{display:none!important}\nimg[width=\"728\"][height=\"90\"]{display:none!important}\nytd-item-section-renderer > .ytd-item-section-renderer > ytd-ad-slot-renderer.style-scope{display:none!important}\n#shopping-timely-shelf{display:none!important}\n[href^=\"https://turtlebids.irauctions.com/\"] img{display:none!important}\na[onclick^=\"window.location.replace('https://random-affiliate.atimaze.com/\"]{display:none!important}\n[id^=\"div-gpt-ad\"]{display:none!important}\na[href^=\"http://join.brokestraightboys.com/track/\"]{display:none!important}\na[href^=\"https://bngpt.com/\"]{display:none!important}\na[href^=\"https://a.medfoodsafety.com/\"]{display:none!important}\na[href^=\"https://go.nordvpn.net/aff\"] > img{display:none!important}\nps-connatix-module{display:none!important}\na[href^=\"https://www.adxsrve.com/\"]{display:none!important}\ndiv[id*=\"ScriptRoot\"]{display:none!important}\na[href^=\"https://porntubemate.com/\"]{display:none!important}\na[href^=\"https://identicaldrench.com/\"]{display:none!important}\ndiv[data-adzone]{display:none!important}\n[href^=\"https://glersakr.com/\"]{display:none!important}\n[href^=\"https://klsdee.com/\"]{display:none!important}\ngpt-ad{display:none!important}\na[href^=\"https://join.dreamsexworld.com/\"]{display:none!important}\na[href^=\"https://myclick-2.com/\"]{display:none!important}\na[href^=\"https://tour.mrskin.com/\"]{display:none!important}\na[href^=\"https://ad.zanox.com/ppc/\"] > img{display:none!important}\n[href^=\"http://mypillow.com/\"] > img{display:none!important}\na[href^=\"https://6-partner.com/\"]{display:none!important}\na[href^=\"https://s.zlink7.com/\"]{display:none!important}\na[href^=\"https://click.hoolig.app/\"]{display:none!important}\napp-advertisement{display:none!important}\n[data-d-ad-id]{display:none!important}\n[href^=\"https://ad.admitad.com/\"]{display:none!important}\na[href^=\"https://s.ma3ion.com/\"]{display:none!important}\na[href^=\" https://www.friendlyduck.com/AF_\"]{display:none!important}\na[href^=\"https://zirdough.net/\"]{display:none!important}\na[href^=\"https://go.dmzjmp.com\"]{display:none!important}\n[href^=\"https://wct.link/click?\"]{display:none!important}\na[href^=\"https://sexynearme.com/\"]{display:none!important}\na[href^=\"https://s.zlinkr.com/\"]{display:none!important}\na[href^=\"https://a.bestcontentfood.top/\"]{display:none!important}\n[data-ad-manager-id]{display:none!important}\na[href^=\"http://www.h4trck.com/\"]{display:none!important}\na[href^=\"https://track.ultravpn.com/\"]{display:none!important}\n[class^=\"s2nPlayer\"]{display:none!important}\na[href^=\"https://s.eunow4u.com/\"]{display:none!important}\ndiv[id^=\"adngin-\"]{display:none!important}\na[href^=\"https://s.cant3am.com/\"]{display:none!important}\n[href^=\"https://shiftnetwork.infusionsoft.com/go/\"] > img{display:none!important}\na[href^=\"https://go.mnaspm.com/\"]{display:none!important}\n[href^=\"https://mypatriotsupply.com/\"] > img{display:none!important}\ndiv[id^=\"crt-\"][style]{display:none!important}";document.documentElement.appendChild(style);};if(document.documentElement)install();else document.addEventListener('DOMContentLoaded',install,{once:true});break;}
}
})();
