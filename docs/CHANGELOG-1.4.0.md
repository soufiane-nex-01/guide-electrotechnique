# Guide Électrotechnique · NEX-01 · 1.4.0

## ما الجديد

- **محاكيات الدليل / Simulations du guide**: 41 نشاطاً أعيد بناؤها بالـHTML وSVG وJavaScript لتتبع الوضع النهاري والليلي وتعمل باللمس ولوحة المفاتيح. تشمل المقاومة والألوان، المولد، RLC، موازنة القدرة والتعويض، المحول، المؤقتات، RC، الإنارة، المحركات، الهواء المضغوط وغيرها.
- 39 نشاطاً مستندة إلى محتوى الأرشيف المتاح. الأنشطة التعليمية قد تستخدم رسوماً مبسطة؛ الأصل السليم يبقى متاحاً بزر **Ouvrir l’original Flash**. جدول مفتاح الكامات وتركيب الكونتاكتور ملفان تالفان في المصدر، لذلك أضيف بديلان تعليميان موسومان بوضوح. ورشة الكامات قابلة لتعديل 12 موضعاً وليست استعادة للتوصيلات المفقودة.
- **Préférences → Langue**: الفرنسية افتراضياً، مع الإنجليزية والعربية واتجاه RTL. تُحمّل ملفات اللغة المختارة محلياً؛ لا تتطلب الترجمة اتصالاً.
- نصوص 615 بطاقة متاحة بالإنجليزية والعربية مع إظهار المصدر الفرنسي للمقارنة. هذه ترجمة مساعدة راجعنا منها المصطلحات والمقاطع الحساسة، وليست مراجعة بشرية شاملة للأرشيف. الرسوم التاريخية غير المعاد بناؤها ووثائق PDF تحتفظ بلغتها الأصلية.
- 6 مسارات، 36 درساً، 72 سؤالاً مصححاً مع شروحات. مراجعة يدوية كاملة للنصوص التعليمية الجديدة وأسئلتها باللغتين.
- 7 مختبرات إضافية: مقسّم الجهد والأعطال، جسر ويتستون، RC، التيار المتناوب والقدرة، التعشيق الذاتي، حلقة 4–20 mA، وPWM مع خافض الجهد المثالي.
- تقييم تطبيقي من 12 مسألة وأسئلة متغيرة، مع تصحيح وشرح وحفظ محلي للجلسة والنتائج. النتائج تعليمية ولا تمنح تأهيلاً مهنياً.
- قاموس من 48 مصطلحاً؛ الفرنسية أولاً ثم ترجمة المصطلح والتعريف إلى العربية أو الإنجليزية.
- 59 ملاحظة مراجعة في 47 بطاقة توضح أخطاء أو معلومات تاريخية مهمة، دون تغيير النص الفرنسي الأصلي. منحنيات الحماية ومناطق العمل التاريخية موسومة بأنها لا تستبدل المراجع الحالية أو منحنيات المصنع.

## Sources et méthode

Les ajouts sont des explications et exercices rédigés pour l’application, avec références dans les leçons : Sitelec, INRS, OpenStax, Modbus Organization, Texas Instruments et NI. Les modèles numériques sont pédagogiques et idéalisés ; leurs hypothèses sont indiquées. Ils ne dimensionnent pas une installation réelle.

La partie historique est attribuée à Philippe Juguet / POS Industry (2004–2005). La mission de NEX-01, rendre les domaines de l’électricité accessibles au grand public, figure dans « À propos ». Les archives originales sont conservées ; aucun quiz ni article tiers complet n’a été importé depuis Sitelec.

## Fluidité

Les nouvelles simulations ne chargent pas Ruffle. Les dessins SVG sont petits et recalculés à la demande ; aucun shader, vidéo, système de particules ou animation permanente. Les paragraphes sont chargés à l’ouverture de leur fiche, avec un cache limité. Les données et la progression restent hors ligne. Les liens vers les références s’ouvrent dans le navigateur externe.

## Limites de validation

Les contrôles numériques, les scénarios DOM et la cohérence des fichiers sont vérifiés. La compilation, l’alignement et la signature de l’APK sont contrôlés. Cette édition n’a pas été testée sur un téléphone physique ou un émulateur. Les outils de prévisualisation Windows et navigateur ont expiré ; le rendu visuel complet et les performances sur l’itel S18 restent à valider sur l’appareil.
