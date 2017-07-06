*Пример работы для резюме. Представленный код взят из **не** завершённого проекта.*

## Краткое описание

Проект предсавляет из себя реализацию *CRM* для владельцев спортивных комплексов.

Проект имеет следующую иерархическую стркутуру модулей:
* **proSportCommonLibrary** (набор общих компонентов: интерфейс для взаисодействия с REST API, инструменты для работы с аккаунтами и т.д.)
  * **proSportManagerApplication** (приложение для менеджеров спортивных площадок: отслеживание и обработка заказов, упрвление скидками и расписанием работы)
  * **proSportPlayerLibrary** (набор общих компонентов приложений для клиентов: отслеживание и создание заказов, просмотр списка спортивных комплексов и их площадок)
    * **proSportPlayerSoccerApplication** (прилжение для клиентов для футбола)
    * **proSportPlayer\*\*\*Application** *не реализовано* (другие прилжения для клиентов для конкретного вида спорта)

## Примеры снимоков экрана
### Прилжение для клиентов ("Футбол")

<table>
  <tr>
    <td width="33%" valign="bottom"><img src="/screenshots/player/Screenshot_20170531-095211.png"></td>
    <td width="33%" valign="bottom">Информация о комплеке<img src="/screenshots/player/Screenshot_20170531-095215.png"></td>
    <td width="33%" valign="bottom">Список площадок комплекса<img src="/screenshots/player/Screenshot_20170531-095226.png"></td>
  </tr>
  <tr>
    <td width="33%" valign="bottom">Бронирование времени<img src="/screenshots/player/Screenshot_20170531-095323.png"></td>
    <td width="33%" valign="bottom">Ближайшие комплексы<img src="/screenshots/player/Screenshot_20170531-095346.png"></td>
    <td width="33%" valign="bottom"><img src="/screenshots/player/Screenshot_20170531-095350.png"></td>
  </tr>
   <tr>
    <td width="33%" valign="bottom">Список комплексов<img src="/screenshots/player/Screenshot_20170531-095400.png"></td>
    <td width="33%" valign="bottom">Настройка поиска комплексов<img src="/screenshots/player/Screenshot_20170531-095420.png"></td>
    <td width="33%" valign="bottom">Список заказов<img src="/screenshots/player/Screenshot_20170531-095517.png"></td>
  </tr>
   <tr>
    <td width="33%" valign="bottom">Настройки<img src="/screenshots/player/Screenshot_20170531-095530.png"></td>
    <td width="33%" valign="bottom"></td>
    <td width="33%" valign="bottom"></td>
  </tr>
</table>

### Прилжение для менеджеров спортивных комплексов

<table>
  <tr>
    <td width="33%" valign="bottom">Расписание площадки<img src="/screenshots/manager/Screenshot_20170531-100401.png"></td>
    <td width="33%" valign="bottom"><img src="/screenshots/manager/Screenshot_20170531-100415.png"></td>
    <td width="33%" valign="bottom"><img src="/screenshots/manager/Screenshot_20170531-100421.png"></td>
  </tr>
   <tr>
    <td width="33%" valign="bottom"><img src="/screenshots/manager/Screenshot_20170531-100439.png"></td>
    <td width="33%" valign="bottom"><img src="/screenshots/manager/Screenshot_20170531-100539.png"></td>
    <td width="33%" valign="bottom"><img src="/screenshots/manager/Screenshot_20170531-100600.png"></td>
  </tr>
</table>


