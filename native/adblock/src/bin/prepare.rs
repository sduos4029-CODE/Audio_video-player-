use std::{env,fs};
fn main(){
 let args:Vec<String>=env::args().collect();
 let engine=pulse_adblock::compile(&fs::read_to_string(&args[1]).unwrap(),&fs::read_to_string(&args[2]).unwrap()).unwrap();
 let mut pages=serde_json::Map::new();
 for host in ["youtube.com","www.youtube.com","m.youtube.com","music.youtube.com"]{
  let result=engine.url_cosmetic_resources(&format!("https://{host}/"));
  eprintln!("{host}: {} cosmetic selectors, {} script bytes, {} procedural rules",result.hide_selectors.len(),result.injected_script.len(),result.procedural_actions.len());
  pages.insert(host.into(),serde_json::to_value(result).unwrap());
 }
 fs::write(&args[3],serde_json::to_vec(&pages).unwrap()).unwrap();
}
