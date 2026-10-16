/* Offline localization: only UI strings and the chosen language's data are loaded. */
(() => {
 'use strict';
 const KEY='electrotechnique.language.v1';let language='fr',dictionary={},observer,started=false;
 try{const stored=localStorage.getItem(KEY);if(['fr','en','ar'].includes(stored))language=stored;}catch{}
 document.documentElement.lang=language;document.documentElement.dir=language==='ar'?'rtl':'ltr';
 const sourceCache=new WeakMap(),lastCache=new WeakMap();
 const clean=s=>s.replace(/\s+/g,' ').trim(),escapeRE=s=>s.replace(/[.*+?^${}()|[\]\\]/g,'\\$&');let patterns=[];
 function tr(value){const key=clean(value);if(!key)return value;let result=dictionary[key];if(result===undefined){for(const p of patterns){const match=key.match(p.regex);if(match){result=p.target.replace(/⟦(\d+)⟧/g,(_,i)=>match[Number(i)+1]);break;}}}
  if(result===undefined&&key.includes(' · '))result=key.split(' · ').map(tr).join(' · ');
  if(result===undefined){for(const prefix of ['Ajouter aux favoris','Retirer des favoris','Votre réponse','Réponse attendue','Repère conseillé','Facteur de conversion'])if(key.startsWith(prefix+' :')&&dictionary[prefix]){result=dictionary[prefix]+key.slice(prefix.length);break;}}
  return result===undefined?value:value.replace(value.trim(),result);}
 function apply(root){if(language==='fr'||!root)return;
  const nodes=[];if(root.nodeType===3)nodes.push(root);else if(root.nodeType===1){const walk=document.createTreeWalker(root,NodeFilter.SHOW_TEXT);while(walk.nextNode())nodes.push(walk.currentNode);}
  for(const node of nodes){if(node.parentElement?.closest('script,style,[data-no-translate],[lang="fr"],.course-points'))continue;const current=node.nodeValue;if(current!==lastCache.get(node))sourceCache.set(node,current);const translated=tr(sourceCache.get(node)||current);if(translated!==current){if(node.parentElement?.tagName==='OPTION'&&!node.parentElement.hasAttribute('value'))node.parentElement.value=current;node.nodeValue=translated;}lastCache.set(node,translated);}
  const elements=root.nodeType===1?[root,...root.querySelectorAll('[placeholder],[aria-label],[title]')]:[];
  for(const el of elements)for(const attr of ['placeholder','aria-label','title'])if(el.hasAttribute(attr)){const v=el.getAttribute(attr),t=tr(v);if(t!==v)el.setAttribute(attr,t);}
 }
 function start(){if(started)return;started=true;if(document.documentElement.dataset.platform==='android')setTimeout(()=>{location.href='guide-language://'+language;},40);if(language==='fr')return;apply(document.body);document.title=tr(document.title);
  const options={childList:true,subtree:true,characterData:true,attributes:true,attributeFilter:['placeholder','aria-label','title']};
  observer=new MutationObserver(records=>{observer.disconnect();const roots=new Set();for(const r of records){if(r.type==='childList')for(const node of r.addedNodes)roots.add(node);else roots.add(r.target);}for(const node of roots)if(node.isConnected)apply(node);observer.observe(document.body,options);});observer.observe(document.body,options);
 }
 const ready=language==='fr'?Promise.resolve():fetch(`locales/${language}/ui.json`).then(r=>{if(!r.ok)throw Error('locale');return r.json();}).then(d=>{dictionary=d;patterns=Object.entries(d).filter(([a,b])=>/\d/.test(a)&&typeof b==='string').map(([a,b])=>{const nums=a.match(/\d+(?:[.,]\d+)?/g)||[];let offset=0;const target=b.replace(/\d+(?:[.,]\d+)?/g,n=>{let i=nums.indexOf(n,offset);if(i<0)i=nums.indexOf(n);if(i<0)return n;offset=i+1;return `⟦${i}⟧`;});return {regex:new RegExp('^'+a.split(/\d+(?:[.,]\d+)?/).map(escapeRE).join('(\\d+(?:[.,]\\d+)?)')+'$'),target};});}).catch(()=>{language='fr';document.documentElement.lang='fr';document.documentElement.dir='ltr';});
 function asset(path){return language!=='fr'&&(/^(catalog|learning)\.json$/.test(path)||path.startsWith('content/'))?`locales/${language}/${path}`:path;}
 function set(value){if(!['fr','en','ar'].includes(value)||value===language)return;try{localStorage.setItem(KEY,value);}catch{return false;}location.reload();return true;}
 function reset(){try{localStorage.removeItem(KEY);}catch{}}
 window.GuideI18n=Object.freeze({get lang(){return language;},ready,asset,tr,start,set,reset});
})();
