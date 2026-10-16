# Guide Électrotechnique — Android 1.4.0

## التثبيت أو التحديث

انقل **Guide-Electrotechnique-Android.apk** إلى الهاتف وافتحه بمدير الملفات، ثم ثبّته فوق النسخة السابقة. استُخدم اسم الحزمة وشهادة التوقيع نفسيهما؛ لا تحذف التطبيق أولاً إذا أردت الاحتفاظ بالمفضلة والتقدم.

يتطلب Android 9 أو أحدث وAndroid System WebView حديثاً. التطبيق ومحتواه يعملان محلياً؛ فتح المراجع الخارجية يحتاج متصفحاً واتصالاً. لا يلزم تشغيل نسخة الحاسوب.

من **Préférences** اختر اللغة و**Clair / Sombre**. افتح **Simulations du guide** للأنشطة المعاد بناؤها أو **Laboratoire interactif** للمختبرات الإضافية، و**Évaluation pratique** للاختبار. في بطاقة الدرس يوجد تبويب **Activité interactive**.

تبقى المفضلة والتقدم محليين. إزالة التطبيق أو مسح بياناته يمسح البيانات المحفوظة. ملفات PDF تفتح بالقارئ المدمج، ويمكن حفظ نسخة عبر منتقي الملفات في Android.

## البناء من المصدر

التطبيق المضيف مكتوب بـJava، والواجهة بـHTML/CSS/JavaScript. يتطلب البناء JDK 17 وAndroid SDK Build Tools 36 ومنصة API 35 أو أحدث وPython 3:

```powershell
python android/source/build.py --sdk "C:/path/to/Android/Sdk" --jdk "C:/path/to/jdk-17" --work "C:/path/to/build-work"
```

ملف ZIP للمصدر لا يتضمن مفتاح التوقيع. احتفظ بمجلد `signature-privee` الموجود محلياً لتحديث النسخة بنفس الشهادة، ولا تشاركه. هذه حزمة تثبيت مباشر وليست نشراً في Google Play.

راجع **NOUVEAUTES-1.4.0.md** للتغييرات و**VERIFICATION.md** لنتائج الفحص وحدوده. هذه النسخة لم تُختبر على جهاز Android فعلي أو محاكي.
