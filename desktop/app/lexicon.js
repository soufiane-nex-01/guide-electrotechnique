/* A local French-first dictionary. No remote service or runtime dependency. */
(() => {
  'use strict';
  const escape = value => String(value).replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
  const normalize = value => String(value).normalize('NFD').replace(/[\u0300-\u036f\u064b-\u065f\u0670\u0640]/g,'').replace(/[أإآٱ]/g,'ا').replace(/ى/g,'ي').toLowerCase();
  let pending;let translation=window.GuideI18n?.lang==='en'?'en':'ar';
  async function load() {
    if (!pending) pending = fetch('lexicon.json').then(r => {if(!r.ok)throw Error('lexicon');return r.json();}).then(data => {
      if(!Array.isArray(data.terms)||!data.terms.length)throw Error('lexicon');
      for(const term of data.terms){if(!['fr','ar','definitionFr','definitionAr','category','id'].every(k=>typeof term[k]==='string'))throw Error('lexicon');term.search=normalize([term.fr,term.ar,term.en,term.definitionFr,term.definitionAr,term.definitionEn,...(term.aliases||[])].join(' '));}
      data.terms.sort((a,b)=>a.fr.localeCompare(b.fr,'fr'));
      return data;
    }).catch(error=>{pending=null;throw error;});
    return pending;
  }
  function shell(){return '<div class="lexicon-host"><div class="skeleton" role="status" aria-label="Chargement du lexique"></div></div>';}
  function entry(t){const english=translation==='en';return `<details class="lexicon-entry"><summary><span><strong lang="fr" data-no-translate>${escape(t.fr)}</strong><span class="lexicon-translation" lang="${translation}" dir="${english?'ltr':'rtl'}" data-no-translate>${escape(english?t.en:t.ar)}</span></span><span class="lexicon-plus" aria-hidden="true">+</span></summary><div class="lexicon-definitions"><div><span class="lexicon-language" data-no-translate>FR · Définition</span><p lang="fr" data-no-translate>${escape(t.definitionFr)}</p></div><div class="lexicon-arabic"><span class="lexicon-language" data-no-translate>${english?'EN · Definition':'AR · التعريف'}</span><p lang="${translation}" dir="${english?'ltr':'rtl'}" data-no-translate>${escape(english?t.definitionEn:t.definitionAr)}</p></div></div></details>`;}
  async function mount(host,initial=''){
    if(!host)return;
    host.innerHTML='<div class="skeleton" role="status" aria-label="Chargement du lexique"></div>';
    try {
      const data=await load();if(!host.isConnected)return;
      const categories=[...new Set(data.terms.map(t=>t.category))].sort((a,b)=>a.localeCompare(b,'fr'));
      host.innerHTML=`<form class="lexicon-filters" role="search"><label>Rechercher un terme<input type="search" class="lexicon-query" placeholder="Terme français, arabe ou anglais…" dir="auto" autocomplete="off" maxlength="160"></label><label>Domaine<select class="lexicon-category"><option value="">Tous les domaines</option>${categories.map(c=>`<option>${escape(c)}</option>`).join('')}</select></label><label>Traduction<select class="lexicon-language-select"><option value="ar" ${translation==='ar'?'selected':''}>العربية</option><option value="en" ${translation==='en'?'selected':''}>English</option></select></label></form><p class="lexicon-count" role="status" aria-live="polite"></p><div class="lexicon-list"></div><button type="button" class="button lexicon-more" hidden>Afficher plus de termes</button><p class="source-footnote">Définitions pédagogiques en français, avec traduction arabe et anglaise. Le contenu du guide original est conservé. <a href="#/lexicon-sources">Repères documentaires</a></p>`;
      const query=host.querySelector('.lexicon-query'),category=host.querySelector('select'),list=host.querySelector('.lexicon-list'),more=host.querySelector('.lexicon-more');
      let limit=12,timer;query.value=initial;
      function render(){if(!host.isConnected)return;const words=normalize(query.value).split(/\s+/).filter(Boolean);const terms=data.terms.filter(t=>(!category.value||t.category===category.value)&&words.every(word=>t.search.includes(word)));host.querySelector('.lexicon-count').textContent=`${terms.length} ${terms.length===1?'terme':'termes'} · FR → ${translation.toUpperCase()}`;list.innerHTML=terms.slice(0,limit).map(entry).join('')||'<div class="empty"><h2>Aucun terme trouvé</h2><p>Essayez un mot plus court, en français, en arabe ou en anglais, ou changez de domaine.</p><button type="button" class="button lexicon-reset">Effacer les filtres</button></div>';more.hidden=terms.length<=limit;}
      host.querySelector('form').addEventListener('submit',e=>{e.preventDefault();clearTimeout(timer);limit=12;render();});
      query.addEventListener('input',()=>{clearTimeout(timer);timer=setTimeout(()=>{limit=12;render();},140);});
      host.querySelector('.lexicon-language-select').addEventListener('change',e=>{translation=e.target.value;render();});
      category.addEventListener('change',()=>{clearTimeout(timer);limit=12;render();});
      more.addEventListener('click',()=>{limit+=12;render();});
      list.addEventListener('click',e=>{if(e.target.closest('.lexicon-reset')){clearTimeout(timer);query.value='';category.value='';limit=12;render();query.focus();}});
      render();
    }catch{if(host.isConnected){host.innerHTML='<div class="empty"><h2>Le lexique n’a pas pu être chargé</h2><p>Réessayez pour ouvrir les définitions enregistrées dans l’application.</p><button type="button" class="button lexicon-retry">Réessayer</button></div>';host.querySelector('button').addEventListener('click',()=>mount(host,initial));}}
  }
  function open(){
    let dialog=document.getElementById('lexicon-dialog');
    if(!dialog){dialog=document.createElement('dialog');dialog.id='lexicon-dialog';dialog.setAttribute('aria-labelledby','lexicon-dialog-title');dialog.innerHTML='<div class="command-head"><h2 id="lexicon-dialog-title">Lexique multilingue</h2><button type="button" class="icon-button" aria-label="Fermer le lexique">×</button></div><div class="lexicon-dialog-body">'+shell()+'</div>';document.body.append(dialog);dialog.querySelector('button').addEventListener('click',()=>dialog.close());dialog.addEventListener('click',e=>{if(e.target.closest('a'))dialog.close();});}
    if(!dialog.open){dialog.showModal();mount(dialog.querySelector('.lexicon-host'));}
  }
  function sources(){return `<section class="settings-panel"><h2>À propos des définitions</h2><p>Les définitions françaises sont des explications pédagogiques originales et concises. Les traductions arabe et anglaise accompagnent chaque terme. Ce lexique complète le guide sans modifier ses textes historiques.</p><p>Repères documentaires consultés pour la protection des moteurs et les équipements d’automatisation :</p><ul><li><a href="https://www.se.com/fr/fr/product-subcategory/1570-relais-de-protection-thermique/" target="_blank" rel="noopener">Schneider Electric — Relais de protection thermique</a></li><li><a href="https://www.siemens.com/fr-fr/products/sinamics/" target="_blank" rel="noopener">Siemens — Variateurs SINAMICS</a></li><li><a href="https://www.siemens.com/fr-fr/products/simatic/controller-configurator/" target="_blank" rel="noopener">Siemens — Automates SIMATIC</a></li></ul><p class="meta">Le lexique fonctionne sans connexion. Seuls ces liens externes nécessitent Internet.</p><a class="button" href="#/lexicon">Retour au lexique</a></section>`;}
  window.GuideLexicon={shell,mount,open,sources};
})();
