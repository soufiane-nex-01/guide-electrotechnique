package com.electrotechnique.guide;
import android.app.*;
import android.content.*;
import android.net.Uri;
import android.os.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import java.io.*;
import java.util.concurrent.*;

public final class MainActivity extends Activity {
    private WebView web;
    private FrameLayout root;
    private View fullscreen;
    private WebChromeClient.CustomViewCallback fullscreenCallback;
    private final ExecutorService io=Executors.newSingleThreadExecutor();
    private String pendingPdf;
    private String words(String fr){return Words.get(this,fr);}
    @Override public void onCreate(Bundle saved) {
        setTheme(Screen.style(this));super.onCreate(saved);
        root=new FrameLayout(this);setContentView(root);Screen.fit(this,root);
        web=new WebView(this);web.setBackgroundColor(Screen.background(this));
        root.addView(web,new FrameLayout.LayoutParams(-1,-1));
        WebSettings s=web.getSettings();s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);
        s.setAllowFileAccess(false);s.setAllowContentAccess(false);s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        s.setJavaScriptCanOpenWindowsAutomatically(false);s.setSupportMultipleWindows(false);
        if(Build.VERSION.SDK_INT>=33)s.setAlgorithmicDarkeningAllowed(false);
        else if(Build.VERSION.SDK_INT>=29)s.setForceDark(WebSettings.FORCE_DARK_OFF);
        s.setMediaPlaybackRequiresUserGesture(true);s.setTextZoom(100);
        s.setUseWideViewPort(true);s.setLoadWithOverviewMode(true);
        WebView.setWebContentsDebuggingEnabled(false);
        web.setWebViewClient(new WebViewClient(){
            @Override public WebResourceResponse shouldInterceptRequest(WebView view,WebResourceRequest request){return LocalAssets.serve(getAssets(),request.getUrl(),request.getMethod());}
            @Override public boolean shouldOverrideUrlLoading(WebView view,WebResourceRequest request){return navigate(request.getUrl(),request.isForMainFrame());}
            @Override public void onPageFinished(WebView view,String url){if(url.startsWith(LocalAssets.HOME))getPreferences(0).edit().putString("lastUrl",url).apply();}
            @Override public boolean onRenderProcessGone(WebView view,RenderProcessGoneDetail detail){
                root.removeView(view);view.destroy();web=null;
                new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this,Screen.style(MainActivity.this))).setTitle(words("Relancer le guide")).setMessage(words("Le lecteur a été interrompu. Vos favoris sont conservés.")).setPositiveButton(words("Relancer"),(d,w)->recreate()).setCancelable(false).show();return true;
            }
        });
        web.setWebChromeClient(new WebChromeClient(){
            @Override public boolean onJsConfirm(WebView v,String url,String message,JsResult result){new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this,Screen.style(MainActivity.this))).setMessage(message).setPositiveButton(words("Confirmer"),(d,w)->result.confirm()).setNegativeButton(words("Annuler"),(d,w)->result.cancel()).setOnCancelListener(d->result.cancel()).show();return true;}
            @Override public boolean onJsAlert(WebView v,String url,String message,JsResult result){new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this,Screen.style(MainActivity.this))).setMessage(message).setPositiveButton("OK",(d,w)->result.confirm()).setOnCancelListener(d->result.confirm()).show();return true;}
            @Override public void onShowCustomView(View view,CustomViewCallback callback){if(fullscreen!=null){callback.onCustomViewHidden();return;}fullscreen=view;fullscreenCallback=callback;web.setVisibility(View.GONE);root.addView(view,new FrameLayout.LayoutParams(-1,-1));}
            @Override public void onHideCustomView(){leaveFullscreen();}
        });
        web.setDownloadListener((url,agent,disposition,mime,length)->{
            String path=LocalAssets.path(Uri.parse(url));
            if(path!=null && path.startsWith("documents/"))savePdf(LocalAssets.document(path.substring(10)));
        });
        pendingPdf=saved==null?null:saved.getString("pendingPdf");
        String url=saved==null?getPreferences(0).getString("lastUrl",LocalAssets.HOME):saved.getString("url",LocalAssets.HOME);
        web.loadUrl(validPage(url)?url:LocalAssets.HOME);
        if(Build.VERSION.SDK_INT>=33)getOnBackInvokedDispatcher().registerOnBackInvokedCallback(android.window.OnBackInvokedDispatcher.PRIORITY_DEFAULT,this::back);
    }
    private boolean validPage(String url){return url!=null && (url.equals(LocalAssets.HOME)||url.startsWith(LocalAssets.HOME+"#"));}
    private boolean navigate(Uri uri,boolean mainFrame) {
        if("guide-language".equals(uri.getScheme())){
            String language=uri.getHost();
            if(mainFrame && web!=null && validPage(web.getUrl()) && ("fr".equals(language)||"en".equals(language)||"ar".equals(language)))getSharedPreferences("appearance",0).edit().putString("language",language).apply();
            return true;
        }
        if("guide-theme".equals(uri.getScheme())){
            String theme=uri.getHost();
            if(mainFrame && web!=null && validPage(web.getUrl()) && ("light".equals(theme)||"dark".equals(theme))){
                getSharedPreferences("appearance",0).edit().putString("theme",theme).apply();
                Screen.palette(this,root);web.setBackgroundColor(Screen.background(this));
            }
            return true;
        }
        if(!mainFrame)return !LocalAssets.local(uri);
        if("guide-pdf".equals(uri.getScheme())){
            String file=LocalAssets.document(uri.getLastPathSegment());
            if(file!=null){if("save".equals(uri.getHost()))savePdf(file);else if("open".equals(uri.getHost()))openPdf(file);}return true;
        }
        String path=LocalAssets.path(uri);
        if(path!=null){if(path.startsWith("documents/")&&path.endsWith(".pdf")){openPdf(LocalAssets.document(path.substring(10)));return true;}return !path.equals("index.html");}
        if("https".equals(uri.getScheme())||"http".equals(uri.getScheme())){
            try{startActivity(new Intent(Intent.ACTION_VIEW,uri));}catch(ActivityNotFoundException e){Toast.makeText(this,words("Aucun navigateur disponible"),Toast.LENGTH_SHORT).show();}
        }
        return true;
    }
    private void openPdf(String file){if(file!=null)startActivity(new Intent(this,PdfActivity.class).putExtra("file",file));}
    private void savePdf(String file){
        if(file==null)return;pendingPdf=file;
        Intent intent=new Intent(Intent.ACTION_CREATE_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE).setType("application/pdf").putExtra(Intent.EXTRA_TITLE,file);
        try{startActivityForResult(intent,42);}catch(ActivityNotFoundException ex){Toast.makeText(this,words("Le sélecteur de fichiers n’est pas disponible"),Toast.LENGTH_LONG).show();}
    }
    @Override protected void onActivityResult(int request,int result,Intent data){
        super.onActivityResult(request,result,data);
        if(request!=42)return;
        String file=LocalAssets.document(pendingPdf);pendingPdf=null;
        if(result!=RESULT_OK||data==null||data.getData()==null||file==null)return;
        Uri destination=data.getData();
        io.execute(()->{boolean success=false;
            try(InputStream in=getAssets().open("web/documents/"+file);OutputStream out=getContentResolver().openOutputStream(destination,"w")){
                if(out==null)throw new IOException("Output unavailable");byte[] buffer=new byte[65536];int n;while((n=in.read(buffer))!=-1)out.write(buffer,0,n);success=true;
            }catch(IOException ex){}
            final boolean ok=success;runOnUiThread(()->Toast.makeText(this,ok?words("Document enregistré"):words("Le document n’a pas pu être enregistré"),Toast.LENGTH_LONG).show());
        });
    }
    private void leaveFullscreen(){if(fullscreen==null)return;root.removeView(fullscreen);fullscreen=null;if(web!=null)web.setVisibility(View.VISIBLE);if(fullscreenCallback!=null)fullscreenCallback.onCustomViewHidden();fullscreenCallback=null;}
    private void back(){
        if(fullscreen!=null){leaveFullscreen();return;}if(web==null){finish();return;}
        web.evaluateJavascript("(()=>{const d=document.querySelector('dialog[open]');if(d){d.close();return true;}const s=document.querySelector('.sidebar.open');if(s){document.getElementById('scrim').click();return true;}return false;})()",handled->{if("true".equals(handled))return;if(web!=null && web.canGoBack())web.goBack();else finish();});
    }
    @Override public void onBackPressed(){back();}
    @Override protected void onSaveInstanceState(Bundle b){super.onSaveInstanceState(b);if(web!=null)b.putString("url",web.getUrl());b.putString("pendingPdf",pendingPdf);}
    @Override protected void onPause(){if(web!=null){if(validPage(web.getUrl()))getPreferences(0).edit().putString("lastUrl",web.getUrl()).apply();web.onPause();}super.onPause();}
    @Override protected void onResume(){super.onResume();if(web!=null)web.onResume();}
    @Override protected void onDestroy(){leaveFullscreen();if(web!=null){root.removeView(web);web.destroy();web=null;}io.shutdown();super.onDestroy();}
}