using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Diagnostics;
using System.Windows.Forms;
using System.Drawing;

// Local-only static host. No upload endpoint, shell endpoint, or external binding.
internal static class Program {
    const int Port = 8876;
    const string Origin = "http://127.0.0.1:8876/";
    static readonly string Root = Path.GetFullPath(Path.Combine(AppDomain.CurrentDomain.BaseDirectory,"app")) + Path.DirectorySeparatorChar;
    static TcpListener listener;
    static volatile bool running = true;
    static NotifyIcon tray;
    [STAThread]
    static void Main(string[] args) {
        bool headless = Array.IndexOf(args,"--headless") >= 0;
        if (!File.Exists(Path.Combine(Root,"index.html"))) { if (!headless) MessageBox.Show("Le dossier app est introuvable. Extrayez tout le dossier avant de lancer le guide.","Guide de l'Electrotechnique"); return; }
        try { listener=new TcpListener(IPAddress.Loopback,Port);listener.Start(); }
        catch (SocketException) {
            bool ours=false;
            try { using(var wc=new WebClient()) ours=wc.DownloadString(Origin+"__guide_health")=="electrotechnique-local-v1"; } catch {}
            if(!headless) { if(ours) Open(); else MessageBox.Show("Le port local 8876 est utilise par une autre application. Fermez l'autre serveur puis relancez le guide.","Guide de l'Electrotechnique"); }
            Environment.ExitCode=2;return;
        }
        var thread=new Thread(Accept);thread.IsBackground=true;thread.Start();
        if(headless) { while(running) Thread.Sleep(1000);return; }
        Application.EnableVisualStyles();
        var menu=new ContextMenuStrip();menu.Items.Add("Ouvrir le guide",null,delegate { Open(); });menu.Items.Add("Quitter",null,delegate { running=false;listener.Stop();tray.Visible=false;Application.Exit(); });
        tray=new NotifyIcon();tray.Icon=SystemIcons.Information;tray.Text="Guide de l'Electrotechnique";tray.ContextMenuStrip=menu;tray.Visible=true;tray.DoubleClick+=delegate { Open(); };
        Open();Application.Run();tray.Dispose();
    }
    static void Open(){try { Process.Start(new ProcessStartInfo(Origin){UseShellExecute=true}); } catch { MessageBox.Show("Ouvrez votre navigateur a l'adresse : "+Origin); }}
    static void Accept(){while(running){try { var client=listener.AcceptTcpClient();ThreadPool.QueueUserWorkItem(delegate { Serve(client); }); } catch { if(!running)return; }}}
    static void Serve(TcpClient client){using(client){try {
        client.ReceiveTimeout=5000;client.SendTimeout=15000;
        var stream=client.GetStream();var header=new MemoryStream();int prev=0,current=0;
        while(header.Length<16384){int b=stream.ReadByte();if(b<0)return;header.WriteByte((byte)b);prev=current;current=(current<<8)|b;if(current==0x0d0a0d0a)break;}
        string request=Encoding.ASCII.GetString(header.ToArray());var lines=request.Split(new[]{"\r\n"},StringSplitOptions.None);var first=lines[0].Split(' ');
        if(first.Length!=3){Error(stream,400,"Bad Request");return;}
        string method=first[0];if(method!="GET"&&method!="HEAD"){Error(stream,405,"Method Not Allowed");return;}
        string host="",range="";foreach(string line in lines){if(line.StartsWith("Host:",StringComparison.OrdinalIgnoreCase))host=line.Substring(5).Trim();if(line.StartsWith("Range:",StringComparison.OrdinalIgnoreCase))range=line.Substring(6).Trim();}
        if(host!="127.0.0.1:8876"&&host!="localhost:8876"){Error(stream,403,"Forbidden");return;}
        string relative=Uri.UnescapeDataString(first[1].Split('?')[0]);
        if(relative=="/__guide_health"){byte[] health=Encoding.ASCII.GetBytes("electrotechnique-local-v1");Headers(stream,200,"OK","text/plain",health.Length,"");if(method=="GET")stream.Write(health,0,health.Length);return;}
        if(!relative.StartsWith("/")||relative.IndexOf('\0')>=0){Error(stream,400,"Bad Request");return;}
        relative=relative.TrimStart('/').Replace('/',Path.DirectorySeparatorChar);
        if(relative.Length==0)relative="index.html";
        string path=Path.GetFullPath(Path.Combine(Root,relative));
        if(!path.StartsWith(Root,StringComparison.OrdinalIgnoreCase)){Error(stream,403,"Forbidden");return;}
        if(!File.Exists(path)){Error(stream,404,"Not Found");return;}
        using(var file=File.OpenRead(path)){
            long start=0,end=file.Length-1;bool partial=false;
            if(range.StartsWith("bytes=",StringComparison.OrdinalIgnoreCase)){
                string[] parts=range.Substring(6).Split('-');long n;
                if(parts.Length==2&&parts[0].Length>0&&long.TryParse(parts[0],out n)){start=n;if(parts[1].Length>0&&long.TryParse(parts[1],out n))end=Math.Min(end,n);partial=true;}
                else if(parts.Length==2&&parts[0].Length==0&&long.TryParse(parts[1],out n)&&n>0){start=Math.Max(0,file.Length-n);partial=true;}
                if(start<0||start>file.Length-1||end<start){Headers(stream,416,"Range Not Satisfiable","text/plain",0,"Content-Range: bytes */"+file.Length+"\r\n");return;}
            }
            long length=end-start+1;string extra=partial?"Content-Range: bytes "+start+"-"+end+"/"+file.Length+"\r\n":"";
            Headers(stream,partial?206:200,partial?"Partial Content":"OK",Mime(path),length,extra);
            if(method=="HEAD")return;file.Position=start;byte[] buffer=new byte[65536];long left=length;
            while(left>0){int read=file.Read(buffer,0,(int)Math.Min(buffer.Length,left));if(read==0)break;stream.Write(buffer,0,read);left-=read;}
        }
    }catch { /* A closed browser connection must not stop the library. */ }}}
    static void Error(NetworkStream s,int status,string reason){byte[] b=Encoding.UTF8.GetBytes(reason);Headers(s,status,reason,"text/plain; charset=utf-8",b.Length,"");s.Write(b,0,b.Length);}
    static void Headers(NetworkStream s,int status,string reason,string type,long length,string extra){
        string h="HTTP/1.1 "+status+" "+reason+"\r\nContent-Type: "+type+"\r\nContent-Length: "+length+"\r\nConnection: close\r\nAccept-Ranges: bytes\r\nX-Content-Type-Options: nosniff\r\nReferrer-Policy: no-referrer\r\nCache-Control: no-cache\r\n"+extra+"\r\n";
        byte[] bytes=Encoding.ASCII.GetBytes(h);s.Write(bytes,0,bytes.Length);
    }
    static string Mime(string p){switch(Path.GetExtension(p).ToLowerInvariant()){
        case ".html":return "text/html; charset=utf-8";case ".css":return "text/css; charset=utf-8";case ".js":return "application/javascript; charset=utf-8";case ".json":return "application/json; charset=utf-8";case ".wasm":return "application/wasm";case ".svg":return "image/svg+xml";case ".pdf":return "application/pdf";case ".swf":return "application/x-shockwave-flash";case ".woff2":return "font/woff2";case ".png":return "image/png";default:return "application/octet-stream";
    }}
}
