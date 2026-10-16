package com.electrotechnique.guide;
import android.app.Activity;
final class Words {
 static String get(Activity a,String fr){String lang=a.getSharedPreferences("appearance",0).getString("language","fr");if("fr".equals(lang))return fr;switch(fr){
 case "Relancer le guide":return "ar".equals(lang)?"إعادة تشغيل الدليل":"Restart the guide";
 case "Le lecteur a été interrompu. Vos favoris sont conservés.":return "ar".equals(lang)?"توقف العارض. مفضلاتك محفوظة.":"The reader stopped. Your bookmarks are saved.";
 case "Relancer":return "ar".equals(lang)?"إعادة التشغيل":"Restart";
 case "Confirmer":return "ar".equals(lang)?"تأكيد":"Confirm";
 case "Annuler":return "ar".equals(lang)?"إلغاء":"Cancel";
 case "Aucun navigateur disponible":return "ar".equals(lang)?"لا يوجد متصفح متاح":"No browser available";
 case "Le sélecteur de fichiers n’est pas disponible":return "ar".equals(lang)?"منتقي الملفات غير متاح":"The file picker is unavailable";
 case "Document enregistré":return "ar".equals(lang)?"تم حفظ المستند":"Document saved";
 case "Le document n’a pas pu être enregistré":return "ar".equals(lang)?"تعذر حفظ المستند":"The document could not be saved";
 case "Retour":return "ar".equals(lang)?"رجوع":"Back";
 case "Retour au guide":return "ar".equals(lang)?"العودة إلى الدليل":"Back to the guide";
 case "Ouverture du document…":return "ar".equals(lang)?"جارٍ فتح المستند…":"Opening document…";
 case "Page précédente":return "ar".equals(lang)?"الصفحة السابقة":"Previous page";
 case "Page suivante":return "ar".equals(lang)?"الصفحة التالية":"Next page";
 case "Réduire le zoom":return "ar".equals(lang)?"تصغير":"Zoom out";
 case "Agrandir le zoom":return "ar".equals(lang)?"تكبير":"Zoom in";
 case "Impossible d’ouvrir ce PDF":return "ar".equals(lang)?"تعذر فتح مستند PDF":"Cannot open this PDF";
 case "Page indisponible · Réduisez le zoom":return "ar".equals(lang)?"الصفحة غير متاحة · قلل التكبير":"Page unavailable · Reduce zoom";
 case "Document PDF, page ":return "ar".equals(lang)?"مستند PDF، الصفحة ":"PDF document, page ";
 case " sur ":return "ar".equals(lang)?" من ":" of ";
 default:return fr;
 }}
}
