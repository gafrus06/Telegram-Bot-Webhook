# Telegram-Bot-Webhook
Telegram Bot Webhook. Random Market.  Java. + Spring Boot + PostgresSQL
# Телеграм бот "RandomMarketBot"

Этот телеграм бот разработан с использованием Java Spring Boot и взаимодействует с базой данных PostgreSQL. Бот работает на вебхуках и предназначен для случайного подбора товаров по определенной категории. Пользователь может просматривать товары, добавлять их в корзину, пропускать или связываться с продавцом. В дополнение к этому, пользователи могут также выставлять свои собственные товары на продажу.

## Бот
1.Меню

![image](https://github.com/gafrus06/Telegram-Bot-Webhook/assets/127015154/8748e7cf-a2b3-4fdd-85b2-73f5243fece6)

2.К покупкам

![image](https://github.com/gafrus06/Telegram-Bot-Webhook/assets/127015154/2f934c39-8b95-445a-bdcb-289c00758a7a)

3.Продать

![image](https://github.com/gafrus06/Telegram-Bot-Webhook/assets/127015154/99ba3c22-256b-45dc-bd9f-87b9df66dd63)

4.Выбор категории

![image](https://github.com/gafrus06/Telegram-Bot-Webhook/assets/127015154/19906ea6-9915-4d2d-b3f2-1d54f6dc353f)

5.Случайный товар

![image](https://github.com/gafrus06/Telegram-Bot-Webhook/assets/127015154/4661f39e-f1b4-4216-a5b6-7d2ade07f208)

6.Корзина

![image](https://github.com/gafrus06/Telegram-Bot-Webhook/assets/127015154/b4482da8-0b77-458c-8b44-678105cfdee2)

![image](https://github.com/gafrus06/Telegram-Bot-Webhook/assets/127015154/30676146-3bf8-4453-b5a1-a0aa95dad706)


## Как использовать бота

1. Для начала работы с ботом, пользователь должен начать диалог командой "/start". Нажать на кнопку "Начать", далее перейти "Начать покупки" -> "К покупкам".
2. Бот предложит пользователю выбрать категорию товаров для просмотра.
3. После выбора категории, бот будет случайно подберет по одному товару из этой категории. Выведет фотогрифю товара, а также краткое описание с кнопками.
4. Пользователь может пролистывать товары, выбирая действия: "Купить 💰", "В корзину 🛒", "↩️ Назад" или "➡️".
5. Пользователь также может добавлять свои товары на продажу, указывая информацию о товаре. Меню -> Начать покупки -> Продать.

## Требования

- Java 17
- Spring Boot 3.3.0
- PostgreSQL
- Telegram Bot API

## Установка и настройка

1. Склонируйте репозиторий с исходным кодом бота.
2. Создайте базу данных PostgreSQL и сконфигурируйте подключение в файле application.properties.
3. Настройте вебхуки для бота, указав адрес вашего сервера и путь к обработчику входящих сообщений.
4. Соберите и запустите приложение.

## Авторы

- Руслан Гафиятов- Разработчик
- gafrus06@mail.ru - Контактная информация




