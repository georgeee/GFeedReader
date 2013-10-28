Материалы седьмой лекции:
=======
http://yadi.sk/d/Z-rGkHzUBjznH

Домашнее задание:
=======
Написать RSS-ридер. В третьею часть дополнительно ко второй входит хранение в базе данных и общение IntentService<->Activity:
- Хранение всей информации в базе данных (sqlite).
- UI и пользовательские операции (добавление, редактирование и удаление) над списком каналов.
- Возможность принудительного обновления списка статей и уведомление о завершении этого процесса (например, через Toast).
- Уведомление о завершении загрузки/обновления через ResultReceiver/Broadcast/LocalBroadcast.
- Не забывайте закрывать Cursor-ы явно (try/finally) или пользоваться managed cursor-ами.
Ничего из условия выше не заменит здравого смысла.

Порядок сдачи:
=======
Сдавать задание нужно в виде форка и пулл-реквеста к https://github.com/IFMO-MobDev-2013/lesson7, в описании укажите ФИО и номер группы.
Пожалуйста, не забывайте коммитить проект целиком (включая apk), а не только activity.
Подробнее про пулл-реквесты можно почитать тут, например: http://habrahabr.ru/post/125999/ и https://help.github.com/articles/using-pull-requests.

Результат принимается до четверга (31 октября) 23:59. После этого оценка за это домашнее задание автоматически снижается в два раза.

Полезные ссылки:
=======
- Основные SQL-операции: http://ru.wikipedia.org/wiki/SQL#.D0.9E.D0.BF.D0.B8.D1.81.D0.B0.D0.BD.D0.B8.D0.B5
- Пример приложения с БД: http://developer.android.com/training/notepad/codelab/NotepadCodeLab.zip
- Простой и удобный адаптер для маппига Cursor на ListView: http://developer.android.com/reference/android/widget/ResourceCursorAdapter.html
- Пример взаимодействия Acitvity и IntentService: http://habrahabr.ru/post/167679/
- Операция объединения таблиц (для текущего задания не требуется, но пригодится в дальнейшем) http://ru.wikipedia.org/wiki/Join_(SQL)

Оценки:
=======
https://docs.google.com/spreadsheet/ccc?key=0AkYNnR0IM6SpdEJPcWRpUGNKYzRCUExnamJ4NmJMYXc&usp=sharing
