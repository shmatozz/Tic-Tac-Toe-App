# Tic-Tac-Toe Android app
 
 ![image (1)](https://user-images.githubusercontent.com/89917833/218395301-5ac4d28e-1e33-4d38-8601-a8de736a44a2.png) ![image](https://user-images.githubusercontent.com/89917833/218396544-2413c2d7-a6e1-4646-aa8b-55175b75da75.png) ![image](https://user-images.githubusercontent.com/89917833/218396797-36404c4a-4800-4b3a-bdef-915d0364a11d.png)

## GitHub Actions

Push в master:
- Запускаются юнит тесты приложения
- Генерируется .apk файл приложения
- Собирается docker image на self-hosted ранере
- .apk файл приложения публикуется на Firebase
- Запускаются UI тесты с помощью Firebase Test Lab
- Отправляется сообщение в TG беседу проекта о коммите в ветку master

Pull request в master:
- Запускаются юнит тесты
- Запускается анализ кода
- Отправляется сообщение в TG беседу проекта о создании PR

**Features:**

- Gameplay with bot with variable complexity
> EASY - random move, NORMAL - random choice from EASY and HARD step, HARD - always draw or lose
- Selectable Game Rules
> win on row, column or diagonal
- Music in the Game Field
> A Promise From Distant Days - Sergey Eybog (OST Everlasting Summer)
- RU and EN localization
