// SPDX-License-Identifier: MPL-2.0
use adblock::{Engine, lists::{FilterSet, ParseOptions}, request::Request, resources::Resource};
use jni::{JNIEnv, objects::{JClass, JString}, sys::{jboolean, jstring}};
use std::sync::{Mutex, OnceLock};

static ENGINE: OnceLock<Mutex<Option<Engine>>> = OnceLock::new();
fn storage() -> &'static Mutex<Option<Engine>> { ENGINE.get_or_init(|| Mutex::new(None)) }
pub fn compile(rules: &str, resources: &str) -> Result<Engine, String> {
    let mut set = FilterSet::new(false);
    set.add_filter_list(rules.to_string(), ParseOptions { permissions: adblock::resources::PermissionMask::from_bits(1), ..Default::default() });
    let mut engine = Engine::new_with_filter_set(set);
    let resources: Vec<Resource> = serde_json::from_str(resources).map_err(|e| e.to_string())?;
    engine.use_resources(resources);
    Ok(engine)
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_noir_player_NativeAdblock_initialize(mut env: JNIEnv, _: JClass, rules: JString, resources: JString) -> jboolean {
    let result = std::panic::catch_unwind(std::panic::AssertUnwindSafe(|| {
        let rules: String = env.get_string(&rules).ok()?.into();
        let resources: String = env.get_string(&resources).ok()?.into();
        let engine = compile(&rules, &resources).ok()?;
        *storage().lock().ok()? = Some(engine);
        Some(())
    }));
    u8::from(matches!(result, Ok(Some(()))))
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_noir_player_NativeAdblock_matchRequest(mut env: JNIEnv, _: JClass, url: JString, source: JString, kind: JString, method: JString) -> jstring {
    let result = std::panic::catch_unwind(std::panic::AssertUnwindSafe(|| -> Option<String> {
        let url: String = env.get_string(&url).ok()?.into();
        let source: String = env.get_string(&source).ok()?.into();
        let kind: String = env.get_string(&kind).ok()?.into();
        let method: String = env.get_string(&method).ok()?.into();
        let request = Request::new(&url, &source, &kind, &method).ok()?;
        let lock = storage().lock().ok()?;
        let result = lock.as_ref()?.check_network_request(&request);
        Some(serde_json::json!({"blocked":result.should_block(),"exception":result.exception.is_some(),"redirect":result.redirect}).to_string())
    }));
    let value = result.ok().flatten().unwrap_or_else(|| "{}".into());
    env.new_string(value).map(|s| s.into_raw()).unwrap_or(std::ptr::null_mut())
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_com_noir_player_NativeAdblock_pageResources(mut env: JNIEnv, _: JClass, url: JString) -> jstring {
    let result = std::panic::catch_unwind(std::panic::AssertUnwindSafe(|| -> Option<String> {
        let url: String = env.get_string(&url).ok()?.into();
        let lock = storage().lock().ok()?;
        serde_json::to_string(&lock.as_ref()?.url_cosmetic_resources(&url)).ok()
    }));
    env.new_string(result.ok().flatten().unwrap_or_else(|| "{}".into())).map(|s| s.into_raw()).unwrap_or(std::ptr::null_mut())
}

#[cfg(test)]
mod tests {
    use super::*;
    #[test] fn network_rules_preserve_exceptions_and_first_party_media() {
        let engine=compile("||ads.example^\n@@||ads.example/allowed.js$script\n||track.example^$third-party", "[]").unwrap();
        let check=|url,source,kind| engine.check_network_request(&Request::new(url,source,kind,"GET").unwrap()).should_block();
        assert!(check("https://ads.example/ad.js","https://youtube.com/","script"));
        assert!(!check("https://ads.example/allowed.js","https://youtube.com/","script"));
        assert!(!check("https://track.example/video","https://track.example/","media"));
        assert!(check("https://track.example/video","https://youtube.com/","media"));
        assert!(!check("https://rr1.googlevideo.com/videoplayback","https://youtube.com/","media"));
    }
    #[test] fn cosmetic_rules_are_resolved_for_the_correct_domain() {
        let engine=compile("youtube.com##.ad-slot\nexample.com##.different", "[]").unwrap();
        let result=engine.url_cosmetic_resources("https://m.youtube.com/watch?v=fixture");
        assert!(result.hide_selectors.contains(".ad-slot"));
        assert!(!result.hide_selectors.contains(".different"));
    }
}
