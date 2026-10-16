"""Runs on GitHub only: verify transfer, publish the exact reviewed source and assets."""
from pathlib import Path
import base64, hashlib, json, os, subprocess, urllib.request, urllib.error, urllib.parse
TOKEN = os.environ['GH_TOKEN']
FULL = 'soufiane-nex-01/guide-electrotechnique'
BASE = '/repos/'+FULL
assert os.environ['GITHUB_REPOSITORY'] == FULL
assert os.environ['GITHUB_REF'] == 'refs/heads/publication-transfer-v1.4.0'

def api(path, method='GET', data=None, binary=False):
    url = path if path.startswith('https://') else 'https://api.github.com'+path
    assert urllib.parse.urlparse(url).hostname in ['api.github.com','uploads.github.com']
    req = urllib.request.Request(url, method=method,
        data=data if binary else json.dumps(data).encode() if data is not None else None,
        headers={'Authorization':'Bearer '+TOKEN,'Accept':'application/vnd.github+json',
                 'Content-Type':'application/octet-stream' if binary else 'application/json',
                 'User-Agent':'Guide-Electrotechnique-publication'})
    with urllib.request.urlopen(req,timeout=240) as response:
        raw=response.read()
        return json.loads(raw) if raw else None

profile = api(BASE)
assert profile['owner']['id'] == 276366739
if not Path('transfer/manifest.json').exists():
    result=api(BASE+'/git/blobs','POST',{'encoding':'utf-8','content':'Guide publication permission check\n'})
    assert result.get('sha')
    print('Preflight passed: GitHub runner can write the authorized project.')
    raise SystemExit(0)

m=json.loads(Path('transfer/manifest.json').read_text())
assert m['repository']==FULL
out=Path('assembled');out.mkdir(exist_ok=True)
pack=out/'publication.pack'
with pack.open('wb') as destination:
    for item in m['chunks']:
        part=Path(item['path']).read_bytes()
        assert len(part)==item['bytes'] and hashlib.sha256(part).hexdigest()==item['sha256']
        destination.write(part)
assert pack.stat().st_size==m['pack']['bytes']
assert hashlib.sha256(pack.read_bytes()).hexdigest()==m['pack']['sha256']
print('All transfer chunks match the local SHA-256 fingerprints.')
repo=out/'source.git';repo.mkdir(exist_ok=True)
authorization='AUTHORIZATION: basic '+base64.b64encode(('x-access-token:'+TOKEN).encode()).decode()
print('::add-mask::'+authorization)
env=dict(os.environ,GIT_TERMINAL_PROMPT='0',GIT_CONFIG_COUNT='1',
         GIT_CONFIG_KEY_0='http.https://github.com/.extraheader',GIT_CONFIG_VALUE_0=authorization)
def git(*args, input_file=None, stdout=subprocess.PIPE):
    r=subprocess.run(['git',*args],cwd=repo,env=env,stdin=input_file,stdout=stdout,stderr=subprocess.PIPE)
    if r.returncode:
        raise RuntimeError(r.stderr.decode('utf-8','replace'))
    return r.stdout
git('init','--bare')
git('remote','add','origin','https://github.com/'+FULL+'.git')
git('fetch','origin','main')
current=git('rev-parse','FETCH_HEAD').decode().strip()
assert current in [m['expectedHead'],m['finalCommit']], 'Main changed externally; publication stopped.'
with pack.open('rb') as source:
    git('index-pack','--stdin',input_file=source)
assert git('rev-parse',m['finalCommit']+'^{tree}').decode().strip()==m['sourceTree']
git('merge-base','--is-ancestor',m['expectedHead'],m['finalCommit'])
assets=[]
for item in m['assets']:
    assert Path(item['name']).name==item['name']
    path=out/item['name']
    with path.open('wb') as target:
        git('show',m['transportCommit']+':'+item['path'],stdout=target)
    assert path.stat().st_size==item['bytes']
    assert hashlib.sha256(path.read_bytes()).hexdigest()==item['sha256']
    assets.append((path,item['sha256']))
checksums=out/'SHA256SUMS.txt'
checksums.write_text(''.join(sha+'  '+path.name+'\n' for path,sha in assets),encoding='ascii')
assets.append((checksums,hashlib.sha256(checksums.read_bytes()).hexdigest()))
print('Source tree and all three release files match the reviewed local files.')
if current!=m['finalCommit']:
    git('push','origin',m['finalCommit']+':refs/heads/main')
assert api(BASE+'/commits/main')['sha']==m['finalCommit']
try:
    release=api(BASE+'/releases/tags/v1.4.0')
except urllib.error.HTTPError as error:
    if error.code!=404:raise
    release=api(BASE+'/releases','POST',{'tag_name':'v1.4.0','target_commitish':m['finalCommit'],
        'name':'Guide Electrotechnique 1.4.0 — NEX-01','body':m['releaseNotes'],'draft':True,'prerelease':False})
existing={item['name']:item for item in release['assets']}
for path,sha in assets:
    item=existing.get(path.name)
    if item:
        assert item['state']=='uploaded' and item['size']==path.stat().st_size
        if item.get('digest'):assert item['digest']=='sha256:'+sha
    else:
        item=api(release['upload_url'].split('{')[0]+'?name='+urllib.parse.quote(path.name),
                 'POST',path.read_bytes(),binary=True)
        assert item['state']=='uploaded' and item['size']==path.stat().st_size
        if item.get('digest'):assert item['digest']=='sha256:'+sha
    print('Verified release asset: '+path.name)
release=api(BASE+'/releases/'+str(release['id']),'PATCH',{'draft':False})
assert len(release['assets'])==4
print('Published: '+release['html_url'])
print('Final source commit: '+m['finalCommit'])
