package com.electrotechnique.guide;
import android.content.res.AssetManager;
import android.net.Uri;
import android.webkit.WebResourceResponse;
import java.io.*;
import java.util.*;

/** A bounded HTTPS origin backed solely by APK assets, with no network fallback. */
final class LocalAssets {
    static final String ORIGIN="https://appassets.androidplatform.net";
    static final String HOME=ORIGIN+"/assets/index.html";
    static boolean local(Uri uri) {
        return "https".equals(uri.getScheme()) && "appassets.androidplatform.net".equals(uri.getHost())
            && uri.getUserInfo()==null && (uri.getPort()==-1 || uri.getPort()==443);
    }
    static String path(Uri uri) {
        if(!local(uri))return null;
        String path=uri.getPath();
        if(path==null || !path.startsWith("/assets/"))return null;
        path=path.substring(8);
        if(path.indexOf(0)>=0 || path.indexOf(92)>=0)return null;
        for(String segment:path.split("/"))if(segment.equals("..")||segment.equals("."))return null;
        return path;
    }
    static String mime(String path) {
        if(path.endsWith(".html"))return "text/html";
        if(path.endsWith(".css"))return "text/css";
        if(path.endsWith(".js"))return "application/javascript";
        if(path.endsWith(".json"))return "application/json";
        if(path.endsWith(".wasm"))return "application/wasm";
        if(path.endsWith(".svg"))return "image/svg+xml";
        if(path.endsWith(".woff2"))return "font/woff2";
        if(path.endsWith(".swf"))return "application/x-shockwave-flash";
        if(path.endsWith(".pdf"))return "application/pdf";
        if(path.endsWith(".png"))return "image/png";
        return "application/octet-stream";
    }
    static WebResourceResponse serve(AssetManager assets,Uri uri,String method) {
        String path=path(uri);
        if(path==null)return error(403,"Forbidden");
        if(!method.equals("GET") && !method.equals("HEAD"))return error(405,"Method Not Allowed");
        try {
            InputStream input=assets.open("web/"+path);
            if(method.equals("HEAD")){input.close();input=new ByteArrayInputStream(new byte[0]);}
            Map<String,String> headers=new HashMap<>();
            headers.put("Cache-Control","no-cache");headers.put("X-Content-Type-Options","nosniff");
            return new WebResourceResponse(mime(path),"UTF-8",200,"OK",headers,input);
        }catch(IOException ex){return error(404,"Not Found");}
    }
    static WebResourceResponse error(int code,String reason) {
        return new WebResourceResponse("text/plain","UTF-8",code,reason,Collections.emptyMap(),new ByteArrayInputStream(reason.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
    }
    static String document(String value) {
        return value!=null && value.matches("document-[1-8][.]pdf")?value:null;
    }
}