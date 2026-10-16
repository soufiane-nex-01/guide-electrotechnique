from pathlib import Path
import argparse,os,subprocess,zipfile,shutil,secrets,json,hashlib,sys
sys.stdout.reconfigure(encoding="utf-8",errors="replace")
parser=argparse.ArgumentParser(description='Build the offline Android APK using the installed SDK and JDK, without Gradle or network downloads.')
parser.add_argument('--sdk',default=os.environ.get('ANDROID_SDK_ROOT',str(Path(os.environ.get('LOCALAPPDATA',''))/'Android/Sdk')))
parser.add_argument('--jdk',default=os.environ.get('JAVA_HOME',r'C:\Program Files\Java\jdk-17'))
parser.add_argument('--output',default=str(Path(__file__).resolve().parents[1]))
parser.add_argument('--work',default=None)
args=parser.parse_args();source=Path(__file__).resolve().parent;out=Path(args.output).resolve();out.mkdir(parents=True,exist_ok=True)
sdk=Path(args.sdk);jdk=Path(args.jdk);bt=sorted((sdk/'build-tools').iterdir(),key=lambda p:tuple(int(x) for x in p.name.split('.') if x.isdigit()))[-1]
platform=sorted((sdk/'platforms').glob('android-*'),key=lambda p:float(p.name.split('-')[1]))[-1];android=platform/'android.jar'
work=Path(args.work).resolve() if args.work else out/'build';work.mkdir(exist_ok=True)
for folder in ['generated','classes','dex']:(work/folder).mkdir(exist_ok=True)
shutil.copyfile(android,work/'android.jar');android=work/'android.jar'
shutil.copyfile(bt/'core-lambda-stubs.jar',work/'core-lambda-stubs.jar')
bootclasspath=str(work/'core-lambda-stubs.jar')+os.pathsep+str(android)
log=[]
def run(arguments):
 result=subprocess.run([str(a) for a in arguments],capture_output=True,text=True,encoding='utf8',errors='replace',env=env)
 if result.stdout.strip():print(result.stdout[:2500],flush=True)
 if result.stderr.strip():print(result.stderr[:2500],flush=True)
 log.append({'tool':str(arguments[0]),'exitCode':result.returncode,'output':result.stdout+result.stderr})
 if result.returncode:raise RuntimeError('Android build step failed')
 return result.stdout+result.stderr
# Keep tool-specific runtime state inside this build directory.
env=os.environ.copy();env['JAVA_HOME']=str(jdk)
java=[jdk/'bin/java.exe','-Djava.awt.headless=true','-Duser.home='+str(work)]
entropy=work/'entropy.bin'
if not entropy.exists():entropy.write_bytes(os.urandom(1048576))
java+=['-Djava.security.egd='+entropy.as_uri()]
run([bt/'aapt2.exe','compile','--dir',source/'res','-o',work/'resources.zip'])
run([bt/'aapt2.exe','link','--auto-add-overlay','-I',android,'--manifest',source/'AndroidManifest.xml','--java',work/'generated','-A',source/'assets','-R',work/'resources.zip','-o',work/'resources.apk'])
sources=list((source/'src').rglob('*.java'))+list((work/'generated').rglob('*.java'))
response=work/'javac.args';response.write_text('\n'.join('"'+str(p).replace('\\','/')+'"' for p in sources),encoding='utf8')
run([jdk/'bin/javac.exe','-J-Dfile.encoding=UTF-8','-J-Duser.language=en','-encoding','UTF-8','-source','8','-target','8','-bootclasspath',bootclasspath,'-d',work/'classes','@'+str(response)])
with zipfile.ZipFile(work/'classes.jar','w',zipfile.ZIP_DEFLATED) as z:
 for p in (work/'classes').rglob('*.class'):z.write(p,p.relative_to(work/'classes').as_posix())
run(java+['-cp',bt/'lib/d8.jar','com.android.tools.r8.D8','--min-api','28','--lib',android,'--output',work/'dex',work/'classes.jar'])
with zipfile.ZipFile(work/'resources.apk') as original, zipfile.ZipFile(work/'unsigned.apk','w') as package:
 for entry in original.infolist():
  normalized=zipfile.ZipInfo(entry.filename,date_time=entry.date_time);normalized.compress_type=entry.compress_type
  package.writestr(normalized,original.read(entry.filename))
 for p in (work/'dex').glob('*.dex'):package.write(p,p.name,compress_type=zipfile.ZIP_DEFLATED)
run([bt/'zipalign.exe','-P','16','-f','4',work/'unsigned.apk',work/'aligned.apk'])
signing=out/'signature-privee';signing.mkdir(exist_ok=True);key=signing/'guide-release.p12';password=signing/'mot-de-passe.txt'
if not password.exists():password.write_text(secrets.token_hex(32),encoding='ascii')
env['GUIDE_SIGNING_PASSWORD']=password.read_text(encoding='ascii').strip()
if not key.exists():
 run([jdk/'bin/keytool.exe','-J-Djava.security.egd='+entropy.as_uri(),'-genkeypair','-keystore',key,'-storetype','PKCS12','-storepass:env','GUIDE_SIGNING_PASSWORD','-keypass:env','GUIDE_SIGNING_PASSWORD','-alias','guide','-keyalg','RSA','-keysize','3072','-validity','10000','-dname','CN=Guide Electrotechnique, OU=Application locale','-noprompt'])
apk=out/'Guide-Electrotechnique-Android.apk'
run(java+['-jar',bt/'lib/apksigner.jar','sign','--ks',key,'--ks-key-alias','guide','--ks-pass','env:GUIDE_SIGNING_PASSWORD','--key-pass','env:GUIDE_SIGNING_PASSWORD','--v1-signing-enabled','true','--v2-signing-enabled','true','--v3-signing-enabled','true','--v4-signing-enabled','false','--out',apk,work/'aligned.apk'])
verification=run(java+['-jar',bt/'lib/apksigner.jar','verify','--verbose','--print-certs',apk])
run([bt/'zipalign.exe','-c','-P','16','4',apk])
badging=run([bt/'aapt2.exe','dump','badging',apk])
assert ("minSdkVersion:'28'" in badging or "sdkVersion:'28'" in badging) and "targetSdkVersion:'35'" in badging
assert 'uses-permission:' not in badging
with zipfile.ZipFile(apk) as z:
 assert z.testzip() is None
 assert all(('Lcom/electrotechnique/guide/'+name+';').encode() in z.read('classes.dex') for name in ['MainActivity','PdfActivity','LocalAssets','Screen'])
 for item in json.loads((source/'source-manifest.json').read_text(encoding='utf8')):
  assert hashlib.sha256(z.read('assets/web/original/'+item['path'])).hexdigest()==item['sha256']
 catalog=json.loads(z.read('assets/web/catalog.json'))
 assert len(catalog['lessons'])==615
 assert len([n for n in z.namelist() if n.startswith('assets/web/original/') and n.lower().endswith('.swf')])==769
 assert len([n for n in z.namelist() if n.startswith('assets/web/documents/') and n.endswith('.pdf')])==8
 for lesson in catalog['lessons']:
  text=z.read('assets/web/content/'+lesson['id']+'.json').decode('utf8');assert '\ufffd' not in text
result={'apk':apk.name,'bytes':apk.stat().st_size,'sha256':hashlib.sha256(apk.read_bytes()).hexdigest(),'minSdk':28,'targetSdk':35,'catalogEntries':615,'originalSwfFiles':769,'pdfDocuments':8,'signatureVerification':verification,'deviceTest':'Not performed: no accessible Android device or configured emulator in this environment.'}
(out/'VERIFICATION-ANDROID.json').write_text(json.dumps(result,ensure_ascii=False,indent=2),encoding='utf8')
(out/'build-log.json').write_text(json.dumps(log,ensure_ascii=False,indent=2),encoding='utf8')
print(json.dumps({'apk':str(apk),'bytes':result['bytes'],'sha256':result['sha256']}),flush=True)
