#GFeedReader

Работа Георгия Агапова (гр.2537)

Читалка для rss/atom.
APK: https://dl.dropboxusercontent.com/u/3693476/apk/GFeedReader.apk

Протестировано на ссылках:

http://goo.gl/RnOo2Q (http://stackoverflow.com/feeds/tag/android)
//Atom feed

http://goo.gl/WLHbvO (http://www.gazeta.ru/export/rss/first.xml)
//Rss feed in windows-1251 (could be determined only by &lt;?xml..?&gt; (xml
declaration))

http://goo.gl/Kk6S4X (https://news.google.ru/news/feeds?output=rss)
//Rss feed in utf-8, without xml declaration

http://goo.gl/YOvk9j
(http://test.soundirect.ru/rss/get?partner=43&podcast_block=27&limit=1000)
//Long rss feed with 1000 items and without pubDates

Материалы пятой лекции
=======
http://yadi.sk/d/lFN5v1KcAkNyS

Домашнее задание:
=======
Написать RSS-ридер. В первую часть входят следующие компоненты:
- Загрузка и парсинг данных (URLConnection/HttpClient, SAX/DOM и AsyncTask)
- UI для отображения списка статей (ListView+любой adapter)
- UI для отображения контента статьи (WebView)

Порядок сдачи:
=======
Сдавать задание нужно в виде форка и пулл-реквеста к https://github.com/IFMO-MobDev-2013/lesson5, в описании укажите ФИО и номер группы.
Пожалуйста, не забывайте коммитить проект целиком (включая apk), а не только activity.
Подробнее про пулл-реквесты можно почитать тут, например: http://habrahabr.ru/post/125999/ и https://help.github.com/articles/using-pull-requests.

Результат принимается до четверга (17 октября) 23:59. После этого оценка за это домашнее задание автоматически снижается в два раза.

Оценки:
=======
https://docs.google.com/spreadsheet/ccc?key=0AkYNnR0IM6SpdEJPcWRpUGNKYzRCUExnamJ4NmJMYXc&usp=sharing

Полезные ссылки:
=======
- http://developer.android.com/training/basics/network-ops/xml.html
- http://developer.android.com/training/basics/network-ops/connecting.html
- http://developer.android.com/guide/topics/ui/declaring-layout.html#AdapterViews
- http://stackoverflow.com/feeds/tag/android как пример RSS-потока
