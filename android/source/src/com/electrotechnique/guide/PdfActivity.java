package com.electrotechnique.guide;
import android.app.Activity;
import android.os.*;
import android.graphics.*;
import android.graphics.pdf.PdfRenderer;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.concurrent.*;

public final class PdfActivity extends Activity {
    private final ExecutorService worker=Executors.newSingleThreadExecutor();
    private PdfRenderer renderer;
    private ImageView image;
    private TextView status;
    private ScrollView pageScroll;
    private Button previous,next;
    private int page=0,count=0;
    private volatile int token;
    private boolean lowMemory;
    private float zoom=1f;
    private volatile boolean closed;
    private Bitmap shown;
    private String words(String fr){return Words.get(this,fr);}
    @Override public void onCreate(Bundle state){
        setTheme(Screen.style(this));super.onCreate(state);
        android.app.ActivityManager memory=(android.app.ActivityManager)getSystemService(ACTIVITY_SERVICE);lowMemory=memory!=null&&(memory.isLowRamDevice()||memory.getMemoryClass()<=192);
        String file=LocalAssets.document(getIntent().getStringExtra("file"));if(file==null){finish();return;}
        if(state!=null){page=state.getInt("page",0);zoom=state.getFloat("zoom",1f);}
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);setContentView(root);Screen.fit(this,root);
        LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);top.setBackgroundColor(Screen.panel(this));
        Button back=button(words("Retour"),words("Retour au guide"));back.setOnClickListener(v->finish());top.addView(back);
        status=new TextView(this);status.setTextColor(Screen.text(this));status.setTextSize(14);status.setGravity(Gravity.CENTER);status.setText(words("Ouverture du document…"));status.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);top.addView(status,new LinearLayout.LayoutParams(0,Screen.dp(this,52),1));
        root.addView(top);
        HorizontalScrollView horizontal=new HorizontalScrollView(this);horizontal.setFillViewport(true);
        ScrollView scroll=new ScrollView(this);pageScroll=scroll;scroll.setFillViewport(true);
        image=new ImageView(this);image.setAdjustViewBounds(true);image.setScaleType(ImageView.ScaleType.FIT_START);
        scroll.addView(image,new ScrollView.LayoutParams(-1,-2));horizontal.addView(scroll,new HorizontalScrollView.LayoutParams(-1,-1));root.addView(horizontal,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout controls=new LinearLayout(this);controls.setGravity(Gravity.CENTER);controls.setBackgroundColor(Screen.panel(this));
        previous=button("‹",words("Page précédente"));next=button("›",words("Page suivante"));Button minus=button("−",words("Réduire le zoom"));Button plus=button("+",words("Agrandir le zoom"));
        for(Button b:new Button[]{previous,minus,plus,next})controls.addView(b,new LinearLayout.LayoutParams(0,Screen.dp(this,52),1));root.addView(controls);
        previous.setOnClickListener(v->{if(page>0){page--;scroll.scrollTo(0,0);render();}});next.setOnClickListener(v->{if(page<count-1){page++;scroll.scrollTo(0,0);render();}});
        minus.setOnClickListener(v->{zoom=Math.max(1f,zoom-.5f);render();});plus.setOnClickListener(v->{zoom=Math.min(3f,zoom+.5f);render();});previous.setEnabled(false);next.setEnabled(false);
        worker.execute(()->{
            try{
                File local=new File(getCacheDir(),file);
                try(InputStream in=getAssets().open("web/documents/"+file);OutputStream out=new FileOutputStream(local)){byte[] buffer=new byte[65536];int n;while((n=in.read(buffer))!=-1)out.write(buffer,0,n);}
                renderer=new PdfRenderer(ParcelFileDescriptor.open(local,ParcelFileDescriptor.MODE_READ_ONLY));
                int pages=renderer.getPageCount();runOnUiThread(()->{if(closed)return;count=pages;page=Math.max(0,Math.min(page,count-1));render();});
            }catch(IOException|SecurityException e){runOnUiThread(()->{if(!closed)status.setText(words("Impossible d’ouvrir ce PDF"));});}
        });
    }
    private Button button(String text,String description){Button b=new Button(this);b.setText(text);b.setTextColor(Screen.blue(this));b.setTextSize(17);b.setMinWidth(Screen.dp(this,52));b.setMinimumWidth(Screen.dp(this,52));b.setContentDescription(description);return b;}
    private void render(){
        if(count<1||closed)return;
        final int serial=++token,index=page;
        final int displayWidth=(int)(getResources().getDisplayMetrics().widthPixels*zoom);
        final int width=Math.max(320,Math.min(lowMemory?1200:1800,displayWidth));
        previous.setEnabled(page>0);next.setEnabled(page<count-1);status.setText((page+1)+" / "+count+" · "+Math.round(zoom*100)+" %");
        worker.execute(()->{
            if(closed||serial!=token)return;
            Bitmap bitmap=null;
            try(PdfRenderer.Page pdf=renderer.openPage(index)){
                int height=Math.min(lowMemory?1800:2600,Math.max(1,(int)((float)width*pdf.getHeight()/pdf.getWidth())));
                int actualWidth=Math.max(1,(int)((float)height*pdf.getWidth()/pdf.getHeight()));
                bitmap=Bitmap.createBitmap(actualWidth,height,Bitmap.Config.ARGB_8888);bitmap.eraseColor(Color.WHITE);pdf.render(bitmap,null,null,PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            }catch(RuntimeException|OutOfMemoryError e){if(bitmap!=null)bitmap.recycle();runOnUiThread(()->{if(!closed&&serial==token)status.setText(words("Page indisponible · Réduisez le zoom"));});return;}
            final Bitmap result=bitmap;
            runOnUiThread(()->{if(closed||serial!=token){result.recycle();return;}Bitmap old=shown;shown=result;image.setImageBitmap(result);image.setContentDescription(words("Document PDF, page ")+(index+1)+words(" sur ")+count);int displayHeight=(int)((float)displayWidth*result.getHeight()/result.getWidth());pageScroll.setLayoutParams(new HorizontalScrollView.LayoutParams(displayWidth,-1));image.setLayoutParams(new ScrollView.LayoutParams(displayWidth,displayHeight));if(old!=null)old.recycle();});
        });
    }
    @Override protected void onSaveInstanceState(Bundle state){super.onSaveInstanceState(state);state.putInt("page",page);state.putFloat("zoom",zoom);}
    @Override protected void onDestroy(){closed=true;token++;worker.execute(()->{if(renderer!=null)renderer.close();});worker.shutdown();if(shown!=null){image.setImageDrawable(null);shown.recycle();shown=null;}super.onDestroy();}
}